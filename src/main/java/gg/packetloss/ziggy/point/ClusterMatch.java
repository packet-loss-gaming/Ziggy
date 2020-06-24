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

class ClusterMatch {
    private final PointCluster cluster;
    private final ClusterPointSet newPoints;
    private long area = -1;

    public ClusterMatch(PointCluster cluster, ClusterPointSet newPoints) {
        this.cluster = cluster;
        this.newPoints = newPoints;
    }

    public PointCluster getCluster() {
        return cluster;
    }

    public ClusterPointSet getNewPoints() {
        return newPoints;
    }

    public long getArea() {
        if (area == -1) {
            area = newPoints.getArea();
        }

        return area;
    }
}
