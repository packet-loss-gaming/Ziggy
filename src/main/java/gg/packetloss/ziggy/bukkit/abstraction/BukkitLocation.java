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
import gg.packetloss.ziggy.abstraction.ZLocation;
import gg.packetloss.ziggy.abstraction.ZWorld;
import gg.packetloss.ziggy.point.Point3D;
import org.bukkit.block.Block;

public class BukkitLocation implements ZLocation {
    private BukkitWorld world;
    private Point3D point;

    public BukkitLocation(Block block) {
        this.world = new BukkitWorld(block.getWorld());
        this.point = new Point3D(block.getX(), block.getY(), block.getZ());
    }

    @Override
    public ZWorld getWorld() {
        return world;
    }

    @Override
    public Point3D getPosition() {
        return point;
    }

    @Override
    public ZBlockInfo getBlockInfo() {
        return world.getInfoAt(point);
    }
}
