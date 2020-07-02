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

package gg.packetloss.ziggy;

import java.util.Set;

public class ZiggyConfig {
    public int maxSpan = 100;
    public int maxArea = maxSpan * maxSpan;
    public int forceFlushDistance = 6;
    public int flushDelay = 50;

    public int initialInvestment = 5;
    public int continuedUseInvestmentFloor = 30;
    public int investmentIncrement = 7;
    public int investmentDecrement = 1;

    public int trustPrestigeValue = 150;
    public int trustPrestigeAmount = 100;
    public int trustContributionCap = 75;

    public Set<String> ignoredWorlds = Set.of("City");
}
