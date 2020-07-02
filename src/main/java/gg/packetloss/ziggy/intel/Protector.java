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

        // If visible or the player has very high trust, allow.
        if (world.isVisibleChange(owner, player) || trustContext.getGlobalTrust().getPrestige() > 30) {
            return true;
        }

        if (trustContext.getRegionalInvestment() > 0) {
            // If this player is the most invested in this area, this is a pass by default.
            return true;
        } else if (trustContext.getRegionalInvestment() == 0) {
            // If the player has a tied investment, this is a pass so long as they don't have negative trust.
            return trustContext.getQuantifiedLocalTrust() >= 0;
        } else {
            // If the player has less investment, they must have an established relationship with the player.
            return trustContext.getLocalTrust().getPrestige() >= 2;
        }
    }

    public boolean isAcceptable(BlockActionContext blockAction) {
        UUID player = blockAction.getPlayer();
        ZLocation location = blockAction.getLocation();

        // We don't pass judgement here.
        if (core.isDisabledFor(location.getWorld())) {
            return true;
        }

        List<AnnotatedPointCluster> pointClusters = core.getAffectedClusters(location);

        int regionalInvestment = 0;
        for (AnnotatedPointCluster pointCluster : pointClusters) {
            UUID owner = pointCluster.getOwner();
            int investment = pointCluster.getPointCluster().getInvestment();

            if (owner.equals(player)) {
                regionalInvestment += investment;
            } else {
                regionalInvestment -= investment;
            }
        }

        for (AnnotatedPointCluster pointCluster : pointClusters) {
            UUID owner = pointCluster.getOwner();
            PlayerTrustContext trustContext = new PlayerTrustContext(
                    pointCluster,
                    regionalInvestment,
                    core.getGlobalTrust(player),
                    core.getLocalTrust(owner, player)
            );

            if (!checkTrustContext(blockAction, trustContext)) {
                return false;
            }
        }

        // If we didn't check anything and this player has been behaving poorly,
        // this is not okay.
        if (pointClusters.isEmpty() && core.getGlobalTrust(player).getContribution() < -25) {
            return false;
        }

        return true;
    }
}
