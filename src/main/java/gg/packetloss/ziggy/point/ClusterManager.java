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

import gg.packetloss.ziggy.point.hull.HullSolver;
import gg.packetloss.ziggy.point.hull.HullInterpreter;
import gg.packetloss.ziggy.point.hull.JarivsHull;

import java.util.*;

public class ClusterManager {
    private static final int QUEUE_FLUSH_LENGTH = 10;

    private final HullSolver hullSolver = new JarivsHull();
    private final HullInterpreter hullInterpreter = new HullInterpreter();

    private final Map<UUID, List<PointCluster>> pointClusters = new HashMap<>();
    private final Map<UUID, List<Point2D>> pointQueue = new HashMap<>();

    public void addPoints(UUID owner, List<Point2D> pointsToAdd) {
        List<PointCluster> ownerClusters = pointClusters.compute(owner, (ignored, value) -> {
            if (value == null) {
                value = new ArrayList<>();
            }
            return value;
        });

        // Try to add the points to an existing hull
        for (PointCluster ownerCluster : ownerClusters) {
            List<Point2D> points = ownerCluster.getPoints();
            points.addAll(pointsToAdd);
            List<Point2D> newPoints = hullSolver.hull(points);

            if (hullInterpreter.isValid(newPoints)) {
                ownerCluster.setPoints(newPoints);
                return;
            }
        }

        // Try to start a new hull with the points
        if (hullInterpreter.isValid(pointsToAdd)) {
            PointCluster cluster = new PointCluster();
            cluster.setPoints(pointsToAdd);
            ownerClusters.add(cluster);
        }
    }

    public void enqueue(UUID player, Point2D point) {
        List<Point2D> points = pointQueue.compute(player, (ignored, value) -> {
            if (value == null) {
                value = new ArrayList<>();
            }
            return value;
        });

        points.add(point);

        if (points.size() > QUEUE_FLUSH_LENGTH) {
            pointQueue.remove(player);
            addPoints(player, points);
        }
    }

    public List<AnnotatedPointCluster> getClustersAt(Point2D point) {
        List<AnnotatedPointCluster> annotatedPointClusters = new ArrayList<>();

        for (Map.Entry<UUID, List<PointCluster>> entry : pointClusters.entrySet()) {
            for (PointCluster cluster : entry.getValue()) {
                List<Point2D> points = cluster.getPoints();

                // Calculate original area
                long originalArea = hullInterpreter.getArea(points);

                // Calculate new area
                points.add(point);
                long newArea = hullInterpreter.getArea(hullSolver.hull(points));

                if (newArea <= originalArea) {
                    annotatedPointClusters.add(new AnnotatedPointCluster(entry.getKey(), cluster));
                }
            }
        }

        return annotatedPointClusters;
    }
}
