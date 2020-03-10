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

package gg.packetloss.ziggy.bukkit.abstraction;

import gg.packetloss.ziggy.abstraction.ZBlockInfo;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

public class BukkitBlockInfo implements ZBlockInfo {
    private final BlockData blockData;

    public BukkitBlockInfo(BlockData blockData) {
        this.blockData = blockData;
    }

    @Override
    public boolean isWorthless() {
        return blockData.getMaterial() == Material.AIR;
    }

    @Override
    public boolean isContainer() {
        switch (blockData.getMaterial()) {
            case CHEST:
            case TRAPPED_CHEST:
            case DISPENSER:
            case DROPPER:
            case HOPPER:
            case BREWING_STAND:
            case ENDER_CHEST:
                return true;
            default:
                return false;
        }
    }
}
