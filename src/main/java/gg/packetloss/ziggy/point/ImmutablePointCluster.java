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

public class ImmutablePointCluster {
    private final int investment;
    private final ClusterPointSet points;

    public ImmutablePointCluster(PointCluster cluster) {
        this.investment = cluster.getInvestment();
        this.points = cluster.getPoints();
    }

    public int getInvestment() {
        return investment;
    }

    public ClusterPointSet getPoints() {
        return new ClusterPointSet(points);
    }
}
