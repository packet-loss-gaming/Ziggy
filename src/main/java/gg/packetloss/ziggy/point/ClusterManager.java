/*
 * Copyright (c) 2020 Wyatt Childers.
 *
 * This file is part of Ziggy.
 *
 * Ziggy is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Ziggy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Ziggy.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package gg.packetloss.ziggy.point;

import gg.packetloss.ziggy.ZiggyCore;
import gg.packetloss.ziggy.abstraction.ZTask;
import gg.packetloss.ziggy.point.hull.HullInterpreter;
import gg.packetloss.ziggy.point.hull.HullSolver;
import gg.packetloss.ziggy.point.hull.JarvisHull;
import gg.packetloss.ziggy.serialization.Serializable;
import gg.packetloss.ziggy.serialization.SerializationConsumer;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ClusterManager {
    private transient final HullSolver hullSolver = new JarvisHull();
    private transient final HullInterpreter hullInterpreter = new HullInterpreter();

    private final Map<UUID, List<PointCluster>> pointClusters = new HashMap<>();
    private transient final Map<UUID, PlayerPointQueue> pointQueue = new HashMap<>();
    private transient final List<Map.Entry<UUID, ArrayPointSet>> delayedPointQueue = new ArrayList<>();

    private transient boolean dirty = false;

    private transient ReadWriteLock pointClusterLock = new ReentrantReadWriteLock();
    private transient Lock pointQueueLock = new ReentrantLock();
    private transient Lock delayedPointQueueLock = new ReentrantLock();

    private List<PointCluster> getOwnerClustersWithLock(UUID owner) {
        return pointClusters.compute(owner, (ignored, value) -> {
            if (value == null) {
                value = new ArrayList<>();
            }
            return value;
        });
    }

    private ClusterMatch getBestExistingMatchWithLock(List<PointCluster> ownerClusters, ArrayPointSet pointsToAdd) {
        List<ClusterMatch> matches = new ArrayList<>();

        // Try to add the points to an existing hull
        for (PointCluster ownerCluster : ownerClusters) {
            ArrayPointSet points = ownerCluster.getPoints();
            points.addAll(pointsToAdd);
            ClusterPointSet newPoints = hullSolver.hull(points);

            if (hullInterpreter.isValid(newPoints)) {
                matches.add(new ClusterMatch(ownerCluster, newPoints));
            }
        }

        switch (matches.size()) {
            case 0:
                return null;
            case 1:
                return matches.get(0);
            default:
                matches.sort(Comparator.comparingLong(ClusterMatch::getArea));
                return matches.get(0);
        }
    }

    private void addPointsWithLock(UUID owner, ArrayPointSet pointsToAdd) {
        List<PointCluster> ownerClusters = getOwnerClustersWithLock(owner);

        // Try to match these points with an existing hull
        ClusterMatch existingCluster = getBestExistingMatchWithLock(ownerClusters, pointsToAdd);
        if (existingCluster != null) {
            PointCluster ownerCluster = existingCluster.getCluster();
            ownerCluster.setPoints(existingCluster.getNewPoints());
            ownerCluster.increaseInvestment();

            dirty = true;
            return;
        }

        // Try to start a new hull with the points
        ClusterPointSet hulledPoints = hullSolver.hull(pointsToAdd);
        if (hullInterpreter.isValid(hulledPoints)) {
            PointCluster cluster = new PointCluster();
            cluster.setPoints(hulledPoints);
            cluster.increaseInvestment();

            ownerClusters.add(cluster);

            dirty = true;
        }
    }

    public void addPoints(UUID owner, ArrayPointSet pointsToAdd) {
        // Try to get the main lock, if this fails, add this point set to a delayed queue
        if (!pointClusterLock.writeLock().tryLock()) {
            delayedPointQueueLock.lock();

            try {
                delayedPointQueue.add(new AbstractMap.SimpleEntry<>(owner, pointsToAdd));
            } finally {
                delayedPointQueueLock.unlock();
            }

            return;
        }

        try {
            addPointsWithLock(owner, pointsToAdd);
        } finally {
            pointClusterLock.writeLock().unlock();
        }
    }

    private void flushQueueWithLock(UUID player) {
        PlayerPointQueue queue = pointQueue.remove(player);
        addPoints(player, queue.flush());
    }

    private void enqueueWithLock(UUID player, Point2D point) {
        PlayerPointQueue points = pointQueue.compute(player, (ignored, value) -> {
            if (value == null) {
                value = new PlayerPointQueue();
            }
            return value;
        });

        // If the new point is accepted by the current queue, defer to a new task
        // if not, flush immediately then queue the new point in a new queue.
        if (points.accept(point)) {
            ZTask newTask = ZiggyCore.getTaskBuilder().createDelayedTask(() -> {
                pointQueueLock.lock();

                try {
                    flushQueueWithLock(player);
                } finally {
                    pointQueueLock.unlock();
                }
            }, ZiggyCore.getConfig().flushDelay);

            points.setNewTask(newTask);
        } else {
            flushQueueWithLock(player);
            enqueueWithLock(player, point);
        }
    }

    public void enqueue(UUID player, Point2D point) {
        pointQueueLock.lock();

        try {
            enqueueWithLock(player, point);
        } finally {
            pointQueueLock.unlock();
        }
    }

    private List<AnnotatedPointCluster> getClustersWithLockAt(Point2D point) {
        List<AnnotatedPointCluster> annotatedPointClusters = new ArrayList<>();

        for (Map.Entry<UUID, List<PointCluster>> entry : pointClusters.entrySet()) {
            for (PointCluster cluster : entry.getValue()) {
                // Filter out points that are clearly out of bounds
                if (!cluster.quickContains(point)) {
                    continue;
                }

                ClusterPointSet points = cluster.getPoints();

                // Calculate original area
                long originalArea = points.getArea();

                // Calculate new area
                points.add(point);
                long newArea = hullSolver.hull(points).getArea();

                if (newArea <= originalArea) {
                    annotatedPointClusters.add(new AnnotatedPointCluster(entry.getKey(), cluster));
                }
            }
        }

        return annotatedPointClusters;
    }

    public List<AnnotatedPointCluster> getClustersAt(Point2D point) {
        pointClusterLock.readLock().lock();

        try {
            return getClustersWithLockAt(point);
        } finally {
            pointClusterLock.readLock().unlock();
        }
    }

    private List<AnnotatedPointCluster> getClustersWithLockNear(Point2D point) {
        List<AnnotatedPointCluster> annotatedPointClusters = new ArrayList<>();

        for (Map.Entry<UUID, List<PointCluster>> entry : pointClusters.entrySet()) {
            for (PointCluster cluster : entry.getValue()) {
                // Filter out points that are clearly out of bounds
                if (cluster.getRoughCenter().distanceSquared(point) > 100 * 100) {
                    continue;
                }

                annotatedPointClusters.add(new AnnotatedPointCluster(entry.getKey(), cluster));
            }
        }

        return annotatedPointClusters;
    }

    public List<AnnotatedPointCluster> getClustersNear(Point2D point) {
        pointClusterLock.readLock().lock();

        try {
            return getClustersWithLockNear(point);
        } finally {
            pointClusterLock.readLock().unlock();
        }
    }

    private void cleanupWithLock() {
        Iterator<Map.Entry<UUID, List<PointCluster>>> it = pointClusters.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, List<PointCluster>> entry = it.next();

            List<PointCluster> pointClusters = entry.getValue();
            pointClusters.removeIf(PointCluster::decayInvestment);
            if (pointClusters.isEmpty()) {
                it.remove();
            }
        }

        dirty = true;
    }

    public void cleanup() {
        pointClusterLock.writeLock().lock();

        try {
            cleanupWithLock();
        } finally {
            pointClusterLock.writeLock().unlock();
        }
    }

    private void replayDelayedWithLock() {
        delayedPointQueueLock.lock();

        try {
            for (Map.Entry<UUID, ArrayPointSet> entry : delayedPointQueue) {
                addPointsWithLock(entry.getKey(), entry.getValue());
            }

            delayedPointQueue.clear();
        } finally {
            delayedPointQueueLock.unlock();
        }
    }
    
    public void writeToDisk(SerializationConsumer<ClusterManager> consumer) throws IOException {
        pointClusterLock.writeLock().lock();

        try {
            if (!dirty) {
                return;
            }

            consumer.accept(new Serializable<>(this));
            dirty = false;

            replayDelayedWithLock();
        } finally {
            pointClusterLock.writeLock().unlock();
        }
    }
}
