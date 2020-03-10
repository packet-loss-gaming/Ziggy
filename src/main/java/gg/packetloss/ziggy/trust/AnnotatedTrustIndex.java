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

package gg.packetloss.ziggy.trust;

import java.util.UUID;

public class AnnotatedTrustIndex {
    private final UUID owner;
    private final TrustIndex index;

    public AnnotatedTrustIndex(UUID owner, TrustIndex index) {
        this.owner = owner;
        this.index = index;
    }

    public UUID getOwner() {
        return owner;
    }

    public void adjustTrust(UUID player, int adjustment) {
        index.adjustTrust(player, adjustment);
    }

    public int getTrust(UUID player) {
        return index.getTrust(player);
    }
}
