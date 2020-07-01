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
import gg.packetloss.ziggy.abstraction.ZBlockInfo;
import gg.packetloss.ziggy.intel.context.BlockActionContext;
import gg.packetloss.ziggy.point.Point3D;

public interface BlockAddMatcher extends BlockActionMatcher {
    private BlockClassification getContextualClassification(BlockActionContext blockContext, int depth) {
        Point3D lowerPosition = blockContext.getLocation().getPosition().add(0, -depth, 0);
        ZBlockInfo lowerBlock = blockContext.getWorld().getInfoAt(lowerPosition);
        return lowerBlock.classify();

    }

    @Override
    public default EventClassification classifyEvent(BlockActionContext blockContext) {
        switch (blockContext.getTo().classify()) {
            case STRUCTURAL: {
                // Containers are for sure always structural.
                if (blockContext.getTo().isContainer()) {
                    return EventClassification.STRUCTURE_ADD;
                }

                // If the block 2 blocks below is determined environmental, then consider this environmental
                // it's either a path or a floor on ground.
                //
                // This isn't great as a house will initially be misclassified, however, later
                // classifications when walls and decorations are added should correct this mistake.
                if (getContextualClassification(blockContext, 2) == BlockClassification.ENVIRONMENTAL) {
                    return EventClassification.ENVIRONMENT_ADD;
                }

                return EventClassification.STRUCTURE_ADD;
            }
            case ENVIRONMENTAL:
                return EventClassification.ENVIRONMENT_ADD;
            case UNDECIDED:
                return EventClassification.UNDECIDED_ADD;
            default:
                throw new UnsupportedOperationException();
        }
    }
}
