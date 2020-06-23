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
import gg.packetloss.ziggy.abstraction.ZTask;

public class PlayerPointQueue {
    private ZTask currentTask;
    private ArrayPointSet points = new ArrayPointSet();

    private void clearCurrentTask() {
        if (currentTask != null) {
            currentTask.cancel();
            currentTask = null;
        }
    }

    public boolean accept(Point2D point) {
        double forceFlushDistanceSq = Math.pow(ZiggyCore.getConfig().forceFlushDistance, 2);
        if (points.size() > 0 && points.get(points.size() - 1).distanceSquared(point) > forceFlushDistanceSq) {
            return false;
        }

        clearCurrentTask();

        points.add(point);
        return true;
    }

    public void setNewTask(ZTask newTask) {
        this.currentTask = newTask;
    }

    public ArrayPointSet flush() {
        clearCurrentTask();
        return points;
    }
}
