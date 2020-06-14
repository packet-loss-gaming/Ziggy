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

public class HullInterpreter {
    private static final int MAX_SPAN = 100;
    private static final int MAX_SPAN_SQ = MAX_SPAN * MAX_SPAN;
    private static final int MAX_AREA = 500;

    public boolean isValid(ArrayPointSet points) {
        if (points.size() < 2) {
            return false;
        }

        double area = 0;

        Point2D lastSeenPoint = points.get(points.size() - 1);
        for (Point2D currentPoint : points) {
            if (currentPoint.distanceSquared(lastSeenPoint) > MAX_SPAN_SQ) {
                return false;
            }

            double xSum = lastSeenPoint.getX() + currentPoint.getX();
            double zDiff = lastSeenPoint.getZ() - currentPoint.getZ();

            area += xSum * zDiff;

            lastSeenPoint = currentPoint;
        }

        return Math.abs(area / 2) < MAX_AREA;
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
