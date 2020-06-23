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
import gg.packetloss.ziggy.point.Point2D;
import gg.packetloss.ziggy.point.PointCluster;

import static gg.packetloss.ziggy.point.hull.HullConstants.MAX_AREA;
import static gg.packetloss.ziggy.point.hull.HullConstants.MAX_SPAN;

public class HullInterpreter {
    private boolean isBoundingValid(ArrayPointSet points) {
        PointCluster cluster = new PointCluster();
        cluster.setPoints(points);

        return cluster.getWidth() < MAX_SPAN && cluster.getLength() < MAX_SPAN;
    }

    private boolean isAreaValid(ArrayPointSet points) {
        return getArea(points) < MAX_AREA;
    }

    public boolean isValid(ArrayPointSet points) {
        if (points.size() < 3) {
            return false;
        }

        return isBoundingValid(points) && isAreaValid(points);
    }

    public long getArea(ArrayPointSet points) {
        double area = 0;

        Point2D lastSeenPoint = points.get(points.size() - 1);
        for (Point2D currentPoint : points) {
            double xSum = lastSeenPoint.getX() + currentPoint.getX();
            double zDiff = lastSeenPoint.getZ() - currentPoint.getZ();

            area += xSum * zDiff;

            lastSeenPoint = currentPoint;
        }

        return Math.round(Math.abs(area / 2));
    }
}
