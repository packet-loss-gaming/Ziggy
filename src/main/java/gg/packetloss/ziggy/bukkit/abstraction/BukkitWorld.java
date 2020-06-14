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
import gg.packetloss.ziggy.abstraction.ZWorld;
import gg.packetloss.ziggy.point.Point3D;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BukkitWorld implements ZWorld {
    private final World world;

    public BukkitWorld(World world) {
        this.world = world;
    }

    @Override
    public String getName() {
        return world.getName().toLowerCase().replaceAll("[ _]", "-");
    }

    @Override
    public ZBlockInfo getInfoAt(Point3D point) {
        return new BukkitBlockInfo(world.getBlockAt(point.getX(), point.getY(), point.getZ()).getBlockData());
    }

    @Override
    public boolean isVisibleChange(UUID owner, UUID player) {
        Player bukkitOwner = Bukkit.getPlayer(owner);
        if (bukkitOwner == null) {
            return false;
        }

        Player bukkitPlayer = Bukkit.getPlayer(player);
        if (bukkitPlayer == null) {
            return false;
        }

        return bukkitOwner.hasLineOfSight(bukkitPlayer) && bukkitOwner.canSee(bukkitPlayer);
    }
}
