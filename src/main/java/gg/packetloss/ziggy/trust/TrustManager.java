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

public class TrustManager {
    private Map<UUID, TrustIndex> playerTrust = new HashMap<>();
    private Map<UUID, Integer> globalTrust = new HashMap<>();

    private void adjustLocalizedTrust(UUID owner, UUID player, int adjustment) {
        TrustIndex trustIndex = playerTrust.compute(owner, (ignored, value) -> {
            if (value == null) {
                value = new TrustIndex();
            }
            return value;
        });

        trustIndex.adjustTrust(player, adjustment);
    }

    private void adjustGlobalTrust(UUID player, int adjustment) {
        globalTrust.compute(player, (ignored, value) -> {
            if (value == null) {
                value = 0;
            }
            return value + adjustment;
        });
    }

    public void adjustTrust(UUID owner, UUID player, int adjustment) {
        adjustLocalizedTrust(owner, player, adjustment);
        adjustGlobalTrust(player, adjustment);
    }

    public int getGlobalTrust(UUID player) {
        return globalTrust.getOrDefault(player, 0);
    }

    public int getLocalTrust(UUID owner, UUID player) {
        TrustIndex localTrustIndex = playerTrust.get(owner);
        if (localTrustIndex != null) {
            return localTrustIndex.getTrust(player);
        }

        return 0;
    }
}
