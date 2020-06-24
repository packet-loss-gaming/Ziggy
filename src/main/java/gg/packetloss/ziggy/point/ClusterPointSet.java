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

import java.util.List;

public class ClusterPointSet extends ArrayPointSet {
    public ClusterPointSet() { }

    public ClusterPointSet(List<Point2D> pointList) {
        super(pointList);
    }

    public ClusterPointSet(ArrayPointSet pointSet) {
        super(pointSet);
    }

    public long getArea() {
        if (isEmpty()) {
            return 0;
        }

        double area = 0;

        Point2D lastSeenPoint = get(size() - 1);
        for (Point2D currentPoint : this) {
            double xSum = lastSeenPoint.getX() + currentPoint.getX();
            double zDiff = lastSeenPoint.getZ() - currentPoint.getZ();

            area += xSum * zDiff;

            lastSeenPoint = currentPoint;
        }

        return Math.round(Math.abs(area / 2));
    }
}
