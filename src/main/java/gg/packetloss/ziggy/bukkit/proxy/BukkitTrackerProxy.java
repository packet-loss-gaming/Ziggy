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

package gg.packetloss.ziggy.bukkit.proxy;

import gg.packetloss.ziggy.abstraction.ZBlockInfo;
import gg.packetloss.ziggy.bukkit.abstraction.BukkitBlockInfo;
import gg.packetloss.ziggy.bukkit.abstraction.BukkitLocation;
import gg.packetloss.ziggy.intel.Tracker;
import gg.packetloss.ziggy.intel.context.BlockActionContext;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class BukkitTrackerProxy {
    private final Tracker tracker;

    public BukkitTrackerProxy(Tracker tracker) {
        this.tracker = tracker;
    }

    public void placeBlock(Player player, Block block) {
        tracker.trackBlockAction(new BlockActionContext(
                player.getUniqueId(),
                new BukkitLocation(block),
                ZBlockInfo.IRRELEVANT,
                new BukkitBlockInfo(block.getBlockData(), block.getBiome())
        ));
    }

    public void breakBlock(Player player, Block block) {
        tracker.trackBlockAction(new BlockActionContext(
                player.getUniqueId(),
                new BukkitLocation(block),
                new BukkitBlockInfo(block.getBlockData(), block.getBiome()),
                ZBlockInfo.IRRELEVANT
        ));
    }

    public void interactBlock(Player player, Block block) {
        BukkitBlockInfo blockInfo = new BukkitBlockInfo(block.getBlockData(), block.getBiome());
        tracker.trackBlockAction(new BlockActionContext(
            player.getUniqueId(),
            new BukkitLocation(block),
            blockInfo,
            blockInfo
        ));
    }
}
