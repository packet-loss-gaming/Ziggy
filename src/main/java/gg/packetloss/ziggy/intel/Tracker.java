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

import gg.packetloss.ziggy.ZiggyCore;
import gg.packetloss.ziggy.abstraction.ZLocation;
import gg.packetloss.ziggy.intel.context.BlockActionContext;
import gg.packetloss.ziggy.intel.context.PlayerTrustContext;
import gg.packetloss.ziggy.intel.matcher.BlockActionMatcher;
import gg.packetloss.ziggy.intel.matcher.builtin.AnyBlockAddActionMatcher;
import gg.packetloss.ziggy.intel.matcher.builtin.AnyBlockRemoveActionMatcher;
import gg.packetloss.ziggy.intel.matcher.builtin.ContainerAddActionMatcher;
import gg.packetloss.ziggy.intel.matcher.builtin.ContainerRemoveActionMatcher;
import gg.packetloss.ziggy.point.AnnotatedPointCluster;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Tracker {
    private final ZiggyCore core;

    private List<BlockActionMatcher> blockActionMatchers = new ArrayList<>();

    public Tracker(ZiggyCore core) {
        this.core = core;

        setupDefaultActionMatchers();
    }

    private void setupDefaultActionMatchers() {
        // More specific matches should come first
        blockActionMatchers.add(new ContainerAddActionMatcher());
        blockActionMatchers.add(new ContainerRemoveActionMatcher());

        // Fallback matchers
        blockActionMatchers.add(new AnyBlockAddActionMatcher());
        blockActionMatchers.add(new AnyBlockRemoveActionMatcher());
    }

    public List<BlockActionMatcher> getBlockActionMatchers() {
        return blockActionMatchers;
    }

    private Optional<BlockActionMatcher> getMatcher(BlockActionContext context) {
        for (BlockActionMatcher actionMatcher : blockActionMatchers) {
            if (actionMatcher.matches(context)) {
                return Optional.of(actionMatcher);
            }
        }

        return Optional.empty();
    }

    public void trackBlockAction(BlockActionContext blockAction) {
        getMatcher(blockAction).ifPresent((matcher) -> {
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

                core.applyTrustModification(owner, player, matcher.getTrustAdjustmentInContext(blockAction, trustContext));
            }

            if (matcher.isQueuingEvent()) {
                core.enqueue(player, location);
            }
        });
    }
}
