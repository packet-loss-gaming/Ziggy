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

import gg.packetloss.ziggy.abstraction.ZBlockInfo;
import gg.packetloss.ziggy.abstraction.ZWorld;
import gg.packetloss.ziggy.intel.context.BlockActionContext;
import gg.packetloss.ziggy.intel.context.PlayerTrustContext;

import java.util.UUID;

public abstract class AbstractBlockRemoveMatcher implements BlockRemoveMatcher {
    public abstract boolean isMatchedBlock(ZBlockInfo blockType);

    @Override
    public final boolean matches(BlockActionContext blockContext) {
        return isMatchedBlock(blockContext.getFrom()) && blockContext.getTo().isAir();
    }

    public abstract int getOwnerAdjustment();
    public abstract int getOwnerVisibleAdjustment();
    public abstract int getGlobalTrustPunishmentLevel();
    public abstract int getGlobalTrustPunishmentAdjustment();
    public abstract int getGlobalTrustGraceLevel();
    public abstract int getGlobalTrustGraceAdjustment();
    public abstract int getFallbackTrustAdjustment();

    @Override
    public final int getTrustAdjustmentInContext(BlockActionContext blockContext, PlayerTrustContext trustContext) {
        ZWorld world = blockContext.getLocation().getWorld();

        UUID owner = trustContext.getAffectedCluster().getOwner();
        UUID player = blockContext.getPlayer();

        // If this is the owner, this is fine, leave it neutral.
        if (owner.equals(player)) {
            return getOwnerAdjustment();
        }

        // If the owner can see this happening, increase trust more rapidly.
        if (world.isVisibleChange(owner, player)) {
            return getOwnerVisibleAdjustment();
        }

        // If the owner can't see this happening, and the player's global trust is bad, assume the worst.
        if (trustContext.getQuantifiedGlobalTrust() < getGlobalTrustPunishmentLevel()) {
            return getGlobalTrustPunishmentAdjustment();
        }

        // If high global trust, assume good intent.
        if (trustContext.getQuantifiedGlobalTrust() > getGlobalTrustGraceLevel()) {
            return getGlobalTrustGraceAdjustment();
        }

        // Otherwise, assume this is bad faith.
        return getFallbackTrustAdjustment();
    }
}
