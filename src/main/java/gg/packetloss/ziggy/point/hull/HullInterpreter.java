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

import gg.packetloss.ziggy.ZiggyCore;
import gg.packetloss.ziggy.point.ClusterPointSet;
import gg.packetloss.ziggy.point.PointCluster;

public class HullInterpreter {
    private boolean isBoundingValid(ClusterPointSet points) {
        PointCluster cluster = new PointCluster();
        cluster.setPoints(points);

        int maxSpan = ZiggyCore.getConfig().maxSpan;
        return cluster.getWidth() < maxSpan && cluster.getLength() < maxSpan;
    }

    private boolean isAreaValid(ClusterPointSet points) {
        int maxArea = ZiggyCore.getConfig().maxArea;
        return points.getArea() < maxArea;
    }

    public boolean isValid(ClusterPointSet points) {
        if (points.size() < 3) {
            return false;
        }

        return isBoundingValid(points) && isAreaValid(points);
    }

    private boolean isAcceptableGrowth(PointCluster cluster, PointCluster newCluster) {
        // The max change size is twice the force flush distance so that missing a couple of blocks
        // doesn't end up causing a new region to form.
        //
        // NOTE: Changes to this logic need to make sure they
        //       won't be incompatible with force flush.
        final int maxChangeSize = ZiggyCore.getConfig().forceFlushDistance * 2;

        int lengthChange = newCluster.getLength() - cluster.getLength();
        if (lengthChange > maxChangeSize) {
            return false;
        }

        int widthChange = newCluster.getWidth() - cluster.getWidth();
        if (widthChange > maxChangeSize) {
            return false;
        }

        return true;
    }

    public boolean isAcceptableGrowth(PointCluster cluster, ClusterPointSet newPoints) {
        PointCluster newCluster = new PointCluster();
        newCluster.setPoints(newPoints);
        return isAcceptableGrowth(cluster, newCluster);
    }
}
