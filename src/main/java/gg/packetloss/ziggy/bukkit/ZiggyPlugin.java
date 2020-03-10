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

package gg.packetloss.ziggy.bukkit;

import gg.packetloss.ziggy.ZiggyCore;
import gg.packetloss.ziggy.bukkit.listener.BukkitPreventionListener;
import gg.packetloss.ziggy.bukkit.listener.BukkitTrackingListener;
import gg.packetloss.ziggy.bukkit.proxy.BukkitPreventionProxy;
import gg.packetloss.ziggy.bukkit.proxy.BukkitTrackerProxy;
import gg.packetloss.ziggy.intel.Protector;
import gg.packetloss.ziggy.intel.Tracker;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class ZiggyPlugin extends JavaPlugin {
    private ZiggyCore core;
    private Tracker tracker;
    private Protector protector;

    @Override
    public void onLoad() {
        core = new ZiggyCore();
        tracker = new Tracker(core);
        protector = new Protector(core);
    }

    private void registerEvents(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, this);
    }

    @Override
    public void onEnable() {
        registerEvents(new BukkitTrackingListener(new BukkitTrackerProxy(tracker)));
        registerEvents(new BukkitPreventionListener(new BukkitPreventionProxy(protector)));
    }
}
