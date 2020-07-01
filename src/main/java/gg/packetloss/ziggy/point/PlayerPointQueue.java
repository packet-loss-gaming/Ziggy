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
import org.apache.commons.lang.Validate;

public class PlayerPointQueue {
    private ZTask currentTask;
    private ArrayPointSet points = new ArrayPointSet();
    private BlockClassification currentClassification = BlockClassification.UNDECIDED;

    private boolean isAccepted = false;

    private void clearCurrentTask() {
        if (currentTask != null) {
            currentTask.cancel();
            currentTask = null;
        }
    }

    private boolean acceptClassification(BlockClassification newClassification) {
        if (currentClassification.compatibleWith(newClassification)) {
            currentClassification = newClassification;
            return true;
        }

        return false;
    }

    public boolean accept(Point2D point, BlockClassification newClassification) {
        Validate.isTrue(!isAccepted);

        // Subtract one to factor in maximum block bounds
        //
        // NOTE: Changes to this logic need to make sure they won't be
        //       incompatible with acceptable growth enforcement.
        double forceFlushDistanceSq = Math.pow(ZiggyCore.getConfig().forceFlushDistance - 1, 2);
        if (points.size() > 0 && points.get(0).distanceSquared(point) > forceFlushDistanceSq) {
            return false;
        }

        if (!acceptClassification(newClassification)) {
            return false;
        }

        clearCurrentTask();

        points.add(point);
        return true;
    }

    public void setNewTask(ZTask newTask) {
        this.currentTask = newTask;
    }

    public PlayerPointQueue accepted() {
        Validate.isTrue(!isAccepted);

        clearCurrentTask();
        isAccepted = true;

        return this;
    }

    public ArrayPointSet getPoints() {
        Validate.isTrue(isAccepted);
        return points;
    }

    public BlockClassification getClassification() {
        Validate.isTrue(isAccepted);
        return currentClassification;
    }

}
