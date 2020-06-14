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

import com.google.common.collect.ImmutableList;
import gg.packetloss.ziggy.bukkit.ZiggyPlugin;
import gg.packetloss.ziggy.bukkit.abstraction.BukkitLocation;
import gg.packetloss.ziggy.intel.Admin;
import gg.packetloss.ziggy.point.AnnotatedPointCluster;
import gg.packetloss.ziggy.point.Point2D;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BukkitAdministrativeProxy {
    private final Admin admin;
    private ImmutableList<BlockData> coloredBlocks;

    public BukkitAdministrativeProxy(Admin admin) {
        this.admin = admin;

        populateColoredBlocks();
    }

    private void populateColoredBlocks() {
        List<BlockData> coloredBlocks = new ArrayList<>();

        for (Material material : Material.values()) {
            String name = material.name();
            if (name.endsWith("CONCRETE") && !name.startsWith("LEGACY_")) {
                coloredBlocks.add(material.createBlockData());
            }
        }

        this.coloredBlocks = ImmutableList.copyOf(coloredBlocks);
    }

    private void drawPointsFor(Player player, Location where) {
        List<AnnotatedPointCluster> clusters = admin.getPointClustersAt(new BukkitLocation(where));

        List<Location> fakeLocations = new ArrayList<>();

        // Send colored regions
        int index = 0;
        for (AnnotatedPointCluster cluster : clusters) {
            BlockData fakeBlock = coloredBlocks.get(index % coloredBlocks.size());

            for (Point2D point : cluster.getPointCluster().getPoints()) {
                int x = point.getX();
                int z = point.getZ();
                int y = where.getWorld().getHighestBlockAt(x, z).getY();

                Location fakeLocation = new Location(where.getWorld(), x, y, z);
                player.sendBlockChange(fakeLocation, fakeBlock);
                fakeLocations.add(fakeLocation);
            }
            ++index;
        }

        // Send original block information
        Bukkit.getScheduler().runTaskLater(ZiggyPlugin.inst(), () -> {
            fakeLocations.forEach((location -> {
                player.sendBlockChange(location, location.getBlock().getBlockData());
            }));
        }, 20 * 5);
    }

    public boolean interactWithItem(Player player, ItemStack itemStack, Location where) {
        if (player.isSneaking() && itemStack.getType() == Material.GOLDEN_SHOVEL) {
            drawPointsFor(player, where);
            return true;
        }

        return false;
    }
}
