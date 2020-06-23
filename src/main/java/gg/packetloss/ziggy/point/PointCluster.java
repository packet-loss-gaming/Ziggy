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

import java.util.ArrayList;
import java.util.List;

public class PointCluster {
    private List<Point2D> points = new ArrayList<>();

    private int minX;
    private int maxX;
    private int minZ;
    private int maxZ;

    public ArrayPointSet getPoints() {
        return new ArrayPointSet(points);
    }

    private void flush() {
        Point2D first = points.get(0);

        minX = first.getX();
        maxX = first.getX();
        minZ = first.getZ();
        maxZ = first.getZ();

        for (int i = 1; i < points.size(); ++i) {
            Point2D point2D = points.get(i);

            minX = Math.min(minX, point2D.getX());
            maxX = Math.max(maxX, point2D.getX());

            minZ = Math.min(minZ, point2D.getZ());
            maxZ = Math.max(maxZ, point2D.getZ());
        }
    }

    public void setPoints(ArrayPointSet points) {
        assert points.size() > 0;

        this.points = points.asList();

        flush();
    }

    public boolean quickContains(Point2D point2D) {
        return minX <= point2D.getX() && point2D.getX() <= maxX &&
                minZ <= point2D.getZ() && point2D.getZ() <= maxZ;
    }

    public int getWidth() {
        return maxX - minX;
    }

    public int getLength() {
        return maxZ - minZ;
    }

    public Point2D getRoughCenter() {
        return new Point2D((minX + maxX) / 2, (minZ + maxZ) / 2);
    }
}
