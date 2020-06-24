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

package gg.packetloss.ziggy.point.hull;

import gg.packetloss.ziggy.point.ArrayPointSet;
import gg.packetloss.ziggy.point.ClusterPointSet;
import gg.packetloss.ziggy.point.Point2D;

public class JarvisHull implements HullSolver {
    private enum CCWState {
        COLINEAR,
        CLOCKWISE,
        COUNTERCLOCKWISE
    }

    /**
     * Calculates the direction based on the cross product.
     */
    private CCWState ccw(Point2D p1, Point2D p2, Point2D p3) {
        int val = (p2.getZ() - p1.getZ()) * (p3.getX() - p2.getX()) -
                  (p2.getX() - p1.getX()) * (p3.getZ() - p2.getZ());

        if (val == 0) return CCWState.COLINEAR;
        return (val > 0) ? CCWState.CLOCKWISE : CCWState.COUNTERCLOCKWISE;
    }

    @Override
    public ClusterPointSet hull(ArrayPointSet points) {
        if (points.size() < 4) {
            return new ClusterPointSet(points);
        }

        // Find the left most point
        int startingPIdx = 0;
        for (int i = 0; i < points.size(); ++i) {
            if (points.get(i).getX() < points.get(startingPIdx).getX()) {
                startingPIdx = i;
            }
        }

        ClusterPointSet hullPoints = new ClusterPointSet();

        // Set the starting point to the left most point which must always be on the hull
        int currentPIdx = startingPIdx;
        hullPoints.add(points.get(currentPIdx));

        while (true) {
            int nextPIdx = (currentPIdx + 1) % points.size();

            for (int possiblePIdx = 0; possiblePIdx < points.size(); ++possiblePIdx) {
                if (possiblePIdx == nextPIdx) {
                    continue;
                }

                Point2D currentP = points.get(currentPIdx);
                Point2D possibleP = points.get(possiblePIdx);
                Point2D nextP = points.get(nextPIdx);

                switch (ccw(currentP, possibleP, nextP)) {
                    case COLINEAR: {
                        if (possibleP.distanceSquared(currentP) > nextP.distanceSquared(currentP)) {
                            nextPIdx = possiblePIdx;
                        }
                        break;
                    }
                    case COUNTERCLOCKWISE:
                        nextPIdx = possiblePIdx;
                    case CLOCKWISE:
                        break;
                }
            }

            currentPIdx = nextPIdx;

            if (currentPIdx == startingPIdx) {
                break;
            }

            hullPoints.add(points.get(nextPIdx));
        }

        return hullPoints;
    }
}
