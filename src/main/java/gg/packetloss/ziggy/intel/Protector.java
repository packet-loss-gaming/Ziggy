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

package gg.packetloss.ziggy.intel;

import gg.packetloss.ziggy.ZiggyStateManager;
import gg.packetloss.ziggy.abstraction.ZLocation;
import gg.packetloss.ziggy.abstraction.ZWorld;
import gg.packetloss.ziggy.intel.context.BlockActionContext;
import gg.packetloss.ziggy.intel.context.PlayerTrustContext;
import gg.packetloss.ziggy.point.AnnotatedPointCluster;

import java.util.List;
import java.util.UUID;

public class Protector {
    private final ZiggyStateManager core;

    public Protector(ZiggyStateManager core) {
        this.core = core;
    }

    private boolean checkTrustContext(BlockActionContext actionContext, PlayerTrustContext trustContext) {
        ZWorld world = actionContext.getLocation().getWorld();

        UUID owner = trustContext.getAffectedCluster().getOwner();
        UUID player = actionContext.getPlayer();

        // If visible or the player has very high trust, allow, unless the local trust is poor
        if (world.isVisibleChange(owner, player) || trustContext.getGlobalTrust() > 1000) {
            return trustContext.getLocalTrust() >= -10;
        }

        // If this player is the only one with an interest, require higher trust for a local change,
        // as there's less exposure.
        if (trustContext.getNumberOfInterests() == 1) {
            return trustContext.getLocalTrust() > 50;
        }

        // There are more people, allow so long as global trust is okay
        if (trustContext.getNumberOfInterests() > 1) {
            return trustContext.getGlobalTrust() > 0 && trustContext.getLocalTrust() >= -10;
        }

        return false;
    }

    public boolean isAcceptable(BlockActionContext blockAction) {
        UUID player = blockAction.getPlayer();
        ZLocation location = blockAction.getLocation();

        List<AnnotatedPointCluster> pointClusters = core.getAffectedClusters(location);
        for (AnnotatedPointCluster pointCluster : pointClusters) {
            UUID owner = pointCluster.getOwner();
            PlayerTrustContext trustContext = new PlayerTrustContext(
                    pointCluster,
                    pointClusters.size(),
                    core.getGlobalTrust(player),
                    core.getLocalTrust(owner, player)
            );

            if (!checkTrustContext(blockAction, trustContext)) {
                return false;
            }
        }

        return !pointClusters.isEmpty() || core.getGlobalTrust(player) > -25;
    }
}
