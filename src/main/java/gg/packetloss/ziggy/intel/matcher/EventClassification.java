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

package gg.packetloss.ziggy.intel.matcher;

import gg.packetloss.ziggy.abstraction.BlockClassification;

public enum EventClassification {
    BLOCK_REMOVAL(false, BlockClassification.UNDECIDED),
    STRUCTURE_ADD(true, BlockClassification.STRUCTURAL),
    ENVIRONMENT_ADD(true, BlockClassification.ENVIRONMENTAL),
    UNDECIDED_ADD(true, BlockClassification.UNDECIDED);

    private final boolean isQueueing;
    private final BlockClassification baseClassification;

    private EventClassification(boolean isQueueing, BlockClassification baseClassification) {
        this.isQueueing = isQueueing;
        this.baseClassification = baseClassification;
    }

    public boolean isQueueing() {
        return isQueueing;
    }

    public BlockClassification getBaseClassification() {
        return baseClassification;
    }
}
