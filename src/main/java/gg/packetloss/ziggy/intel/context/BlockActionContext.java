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

package gg.packetloss.ziggy.intel.context;

import gg.packetloss.ziggy.abstraction.ZBlockInfo;
import gg.packetloss.ziggy.abstraction.ZLocation;

import java.util.UUID;

public class BlockActionContext {
    private final UUID player;

    private final ZLocation location;
    private final ZBlockInfo from;
    private final ZBlockInfo to;

    public BlockActionContext(UUID player, ZLocation location,
                              ZBlockInfo from, ZBlockInfo to) {
        this.player = player;
        this.location = location;
        this.from = from;
        this.to = to;
    }

    public UUID getPlayer() {
        return player;
    }

    public ZLocation getLocation() {
        return location;
    }

    public ZBlockInfo getFrom() {
        return from;
    }

    public ZBlockInfo getTo() {
        return to;
    }
}
