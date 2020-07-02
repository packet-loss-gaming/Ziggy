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

import gg.packetloss.ziggy.ZiggyCore;

import java.util.concurrent.TimeUnit;

public class TrustData {
    private long lastPrestige = System.currentTimeMillis();
    private int contribution = 0;

    private int prestige = 0;

    public int getContribution() {
        return Math.min(ZiggyCore.getConfig().trustContributionCap, contribution);
    }

    public int getPrestige() {
        return prestige;
    }

    public void adjustContribution(int adjustment) {
        contribution += adjustment;
        if (contribution < ZiggyCore.getConfig().trustPrestigeAmount) {
            return;
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastPrestige >= TimeUnit.DAYS.toMillis(1)) {
            contribution = 0;
            ++prestige;

            lastPrestige = currentTime;
        }
    }
}
