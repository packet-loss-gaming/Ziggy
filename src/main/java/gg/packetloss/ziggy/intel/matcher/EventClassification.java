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
    BLOCK_REMOVAL(UpdateScopeClassification.NONE, BlockClassification.UNDECIDED),
    STRUCTURE_ADD(UpdateScopeClassification.EXPANSION, BlockClassification.STRUCTURAL),
    ENVIRONMENT_ADD(UpdateScopeClassification.EXPANSION, BlockClassification.ENVIRONMENTAL),
    UNDECIDED_ADD(UpdateScopeClassification.EXPANSION, BlockClassification.UNDECIDED),
    STRUCTURE_INTERACT(UpdateScopeClassification.MAINTAIN, BlockClassification.STRUCTURAL),
    ENVIRONMENT_INTERACT(UpdateScopeClassification.MAINTAIN, BlockClassification.ENVIRONMENTAL),
    UNDECIDED_INTERACT(UpdateScopeClassification.MAINTAIN, BlockClassification.UNDECIDED);

    private final UpdateScopeClassification updateClassification;
    private final BlockClassification baseClassification;

    private EventClassification(UpdateScopeClassification updateClassification, BlockClassification baseClassification) {
        this.updateClassification = updateClassification;
        this.baseClassification = baseClassification;
    }

    public UpdateScopeClassification getUpdateClassification() {
        return updateClassification;
    }

    public BlockClassification getBaseClassification() {
        return baseClassification;
    }
}
