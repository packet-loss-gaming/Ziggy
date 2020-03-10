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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TrustIndex {
    private final Map<UUID, Integer> index = new HashMap<>();

    public void adjustTrust(UUID player, int adjustment) {
        index.compute(player, (ignored, value) -> {
            if (value == null) {
                value = 0;
            }
            return value + adjustment;
        });
    }

    public int getTrust(UUID player) {
        return index.getOrDefault(player, 0);
    }
}
