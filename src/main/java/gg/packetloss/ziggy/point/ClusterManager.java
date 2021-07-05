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
import gg.packetloss.ziggy.abstraction.BlockClassification;
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
import java.util.function.Consumer;

public class ClusterManager {
    private transient final HullSolver hullSolver = new JarvisHull();
    private transient final HullInterpreter hullInterpreter = new HullInterpreter();

    private final Map<UUID, List<PointCluster>> pointClusters = new HashMap<>();
    private transient final Map<UUID, PlayerPointQueue> pointQueue = new HashMap<>();

    private transient boolean dirty = false;

    private transient ReadWriteLock pointClusterLock = new ReentrantReadWriteLock();
    private transient Lock pointQueueLock = new ReentrantLock();

    private List<PointCluster> getOwnerClustersWithLock(UUID owner) {
        return pointClusters.compute(owner, (ignored, value) -> {
            if (value == null) {
                value = new ArrayList<>();
            }
            return value;
        });
    }

    private List<PointCluster> getOwnerClusters(UUID owner) {
        pointClusterLock.readLock().lock();

        try {
            return getOwnerClustersWithLock(owner);
        } finally {
            pointClusterLock.readLock().unlock();
        }
    }

    private ClusterMatch getBestExistingMatch(List<PointCluster> ownerClusters, PlayerPointQueue pointsToAdd) {
        pointClusterLock.readLock().lock();

        try {
            List<ClusterMatch> matches = new ArrayList<>();

            // Try to add the points to an existing hull
            for (PointCluster ownerCluster : ownerClusters) {
                // Check to see if this is a compatible cluster
                if (!ownerCluster.acceptsBlocksOfClassification(pointsToAdd.getClassification())) {
                    continue;
                }

                ArrayPointSet points = ownerCluster.getPoints();
                points.addAll(pointsToAdd.getPoints());
                ClusterPointSet newPoints = hullSolver.hull(points);

                if (hullInterpreter.isValid(newPoints) && hullInterpreter.isAcceptableGrowth(ownerCluster, newPoints)) {
                    matches.add(new ClusterMatch(ownerCluster, newPoints, pointsToAdd.getClassification()));
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
        } finally {
            pointClusterLock.readLock().unlock();
        }
    }

    private void updatingExisting(ClusterMatch clusterMatch) {
        pointClusterLock.writeLock().lock();

        try {
            PointCluster cluster = clusterMatch.getCluster();
            cluster.setPoints(clusterMatch.getNewPoints(), clusterMatch.getClassification());
            cluster.increaseInvestment();

            dirty = true;
        } finally {
            pointClusterLock.writeLock().unlock();
        }
    }

    private void createNew(List<PointCluster> ownerClusters, ClusterPointSet newPoints, BlockClassification classification) {
        pointClusterLock.writeLock().lock();

        try {
            PointCluster cluster = new PointCluster();

            cluster.setPoints(newPoints, classification);
            cluster.increaseInvestment();

            ownerClusters.add(cluster);

            dirty = true;
        } finally {
            pointClusterLock.writeLock().unlock();
        }
    }

    public void addPoints(UUID owner, PlayerPointQueue pointsToAdd) {
        List<PointCluster> ownerClusters = getOwnerClusters(owner);

        // Try to match these points with an existing hull
        ClusterMatch existingCluster = getBestExistingMatch(ownerClusters, pointsToAdd);
        if (existingCluster != null) {
            updatingExisting(existingCluster);
            return;
        }

        // Try to start a new hull with the points
        ClusterPointSet hulledPoints = hullSolver.hull(pointsToAdd.getPoints());
        if (hullInterpreter.isValid(hulledPoints)) {
            createNew(ownerClusters, hulledPoints, pointsToAdd.getClassification());
        }
    }

    private void flushQueue(UUID player) {
        pointQueueLock.lock();

        try {
            PlayerPointQueue queue = pointQueue.remove(player);
            ZiggyCore.getTaskBuilder().createAsyncTask(() -> {
                addPoints(player, queue.accepted());
            });
        } finally {
            pointQueueLock.unlock();
        }
    }

    private void enqueueWithLock(UUID player, Point2D point, BlockClassification classification) {
        PlayerPointQueue points = pointQueue.compute(player, (ignored, value) -> {
            if (value == null) {
                value = new PlayerPointQueue();
            }
            return value;
        });

        // If the new point is accepted by the current queue, defer to a new task
        // if not, flush immediately then queue the new point in a new queue.
        if (points.accept(point, classification)) {
            ZTask newTask = ZiggyCore.getTaskBuilder().createDelayedTask(() -> {
                flushQueue(player);
            }, ZiggyCore.getConfig().flushDelay);

            points.setNewTask(newTask);
        } else {
            flushQueue(player);
            enqueueWithLock(player, point, classification);
        }
    }

    public void enqueue(UUID player, Point2D point, BlockClassification classification) {
        pointQueueLock.lock();

        try {
            enqueueWithLock(player, point, classification);
        } finally {
            pointQueueLock.unlock();
        }
    }

    private void gatherClustersWithLockAt(List<PointCluster> sourceList, Point2D point, Consumer<PointCluster> consumer) {
        for (PointCluster cluster : sourceList) {
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
                consumer.accept(cluster);
            }
        }
    }

    private List<AnnotatedPointCluster> getClustersWithLockAt(Point2D point) {
        List<AnnotatedPointCluster> annotatedPointClusters = new ArrayList<>();

        for (Map.Entry<UUID, List<PointCluster>> entry : pointClusters.entrySet()) {
            gatherClustersWithLockAt(
                entry.getValue(),
                point,
                (cluster) -> new AnnotatedPointCluster(entry.getKey(), cluster)
            );
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

    public List<PointCluster> getOwnerClustersAtPoint(UUID owner, Point2D point) {
        List<PointCluster> affected = new ArrayList<>();
        pointClusterLock.readLock().lock();

        try {
            List<PointCluster> ownerClusters = getOwnerClustersWithLock(owner);

            // Minimize lock contention by bundling everything under our read lock,
            // then getting the write lock once in touchAll.
            gatherClustersWithLockAt(ownerClusters, point, affected::add);
        } finally {
            pointClusterLock.readLock().unlock();
        }
        return affected;
    }

    private void touchAll(List<PointCluster> clusters) {
        pointClusterLock.writeLock().lock();

        try {
            for (PointCluster cluster : clusters) {
                cluster.increaseInvestment();
            }

            dirty = true;
        } finally {
            pointClusterLock.writeLock().unlock();
        }
    }

    public void touchPlayerClustersAt(UUID player, Point2D point) {
        // FIXME: Should we put this on a background thread or otherwise debounce this?
        touchAll(getOwnerClustersAtPoint(player, point));
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

    public void writeToDisk(SerializationConsumer<ClusterManager> consumer) throws IOException {
        // If we don't get the lock immediately come back later, writing to disk is low priority.
        if (!pointClusterLock.writeLock().tryLock()) {
            return;
        }

        try {
            if (!dirty) {
                return;
            }

            consumer.accept(new Serializable<>(this));
            dirty = false;
        } finally {
            pointClusterLock.writeLock().unlock();
        }
    }
}
