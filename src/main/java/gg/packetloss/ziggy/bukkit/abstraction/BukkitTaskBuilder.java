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

import gg.packetloss.ziggy.abstraction.ZTask;
import gg.packetloss.ziggy.abstraction.ZTaskBuilder;
import gg.packetloss.ziggy.bukkit.ZiggyPlugin;
import org.bukkit.Bukkit;

public class BukkitTaskBuilder implements ZTaskBuilder {
    @Override
    public ZTask createAsyncTask(Runnable taskLogic) {
        return new BukkitTask(Bukkit.getScheduler().runTaskAsynchronously(ZiggyPlugin.inst(), taskLogic));
    }

    @Override
    public ZTask createDelayedTask(Runnable taskLogic, int delayedTicks) {
        return new BukkitTask(Bukkit.getScheduler().runTaskLater(ZiggyPlugin.inst(), taskLogic, delayedTicks));
    }
}
