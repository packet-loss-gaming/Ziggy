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
import gg.packetloss.ziggy.intel.Protector;
import gg.packetloss.ziggy.intel.context.BlockActionContext;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class BukkitPreventionProxy  {
    private final Protector protector;

    public BukkitPreventionProxy(Protector protector) {
        this.protector = protector;
    }

    public boolean placeBlock(Player player, Block block) {
        return !protector.isAcceptable(new BlockActionContext(
                player.getUniqueId(),
                new BukkitLocation(block),
                ZBlockInfo.IRRELEVANT,
                new BukkitBlockInfo(block.getBlockData())
        ));
    }

    public boolean breakBlock(Player player, Block block) {
        return !protector.isAcceptable(new BlockActionContext(
                player.getUniqueId(),
                new BukkitLocation(block),
                new BukkitBlockInfo(block.getBlockData()),
                ZBlockInfo.IRRELEVANT
        ));
    }}
