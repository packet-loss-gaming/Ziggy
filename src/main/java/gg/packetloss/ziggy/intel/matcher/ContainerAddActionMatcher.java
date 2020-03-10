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

import gg.packetloss.ziggy.abstraction.ZWorld;
import gg.packetloss.ziggy.intel.context.BlockActionContext;
import gg.packetloss.ziggy.intel.context.PlayerTrustContext;

import java.util.UUID;

public class ContainerAddActionMatcher implements BlockAddMatcher {
    @Override
    public boolean matches(BlockActionContext blockContext) {
        return blockContext.getFrom().isWorthless() && blockContext.getTo().isContainer();
    }

    @Override
    public int getTrustAdjustmentInContext(BlockActionContext blockContext, PlayerTrustContext trustContext) {
        ZWorld world = blockContext.getLocation().getWorld();

        UUID owner = trustContext.getAffectedCluster().getOwner();
        UUID player = blockContext.getPlayer();

        // If this is the owner, increase trust marginally, they're contributing to their land.
        if (owner.equals(player)) {
            return 1;
        }

        // If the owner can see this happening, increase trust more rapidly.
        if (world.isVisibleChange(owner, player)) {
            return 3;
        }

        // If the owner can't see this happening, and the player's global trust is bad, assume the worst.
        if (trustContext.getGlobalTrust() < -10) {
            return -1;
        }

        // Otherwise, assume the best/neutrality.
        return 0;
    }
}
