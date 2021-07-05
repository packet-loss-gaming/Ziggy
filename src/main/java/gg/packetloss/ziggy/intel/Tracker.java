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
import gg.packetloss.ziggy.ZiggyStateManager;
import gg.packetloss.ziggy.abstraction.ZLocation;
import gg.packetloss.ziggy.intel.context.BlockActionContext;
import gg.packetloss.ziggy.intel.context.PlayerTrustContext;
import gg.packetloss.ziggy.intel.matcher.BlockActionMatcher;
import gg.packetloss.ziggy.intel.matcher.EventClassification;
import gg.packetloss.ziggy.intel.matcher.builtin.*;
import gg.packetloss.ziggy.point.AnnotatedPointCluster;
import gg.packetloss.ziggy.trust.ImmutableTrustData;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Tracker {
    private final ZiggyStateManager core;

    private List<BlockActionMatcher> blockActionMatchers = new ArrayList<>();

    public Tracker(ZiggyStateManager core) {
        this.core = core;

        setupDefaultActionMatchers();
    }

    private void setupDefaultActionMatchers() {
        // More specific matches should come first
        blockActionMatchers.add(new ContainerAddActionMatcher());
        blockActionMatchers.add(new ContainerRemoveActionMatcher());

        // Fallback matchers
        blockActionMatchers.add(new AnyBlockAddActionMatcher());
        blockActionMatchers.add(new AnyBlockInteractActionMatcher());
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

    public void trackBlockAction(BlockActionContext blockContext) {
        getMatcher(blockContext).ifPresent((matcher) -> {
            UUID player = blockContext.getPlayer();
            ZLocation location = blockContext.getLocation();

            List<AnnotatedPointCluster> pointClusters = core.getAffectedClusters(location);
            ImmutableTrustData globalTrust = core.getGlobalTrust(player);
            for (AnnotatedPointCluster pointCluster : pointClusters) {
                UUID owner = pointCluster.getOwner();
                PlayerTrustContext trustContext = new PlayerTrustContext(
                        pointCluster,
                        pointClusters.size(),
                        globalTrust,
                        core.getLocalTrust(owner, player)
                );

                core.applyTrustModification(owner, player, matcher.getTrustAdjustmentInContext(blockContext, trustContext));
            }

            EventClassification classification = matcher.classifyEvent(blockContext);
            switch (classification.getUpdateClassification()) {
                case NONE -> {
                    // This action shouldn't cause any cluster updates
                }
                case EXPANSION -> {
                    // This action could expand or create a player's clusters
                    core.enqueue(player, location, classification.getBaseClassification());
                }
                case MAINTAIN -> {
                    // This action can only maintain a player's clusters
                    core.touchAffected(player, location);
                }
            }
        });
    }
}
