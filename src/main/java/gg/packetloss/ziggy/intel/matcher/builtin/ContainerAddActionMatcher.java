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

package gg.packetloss.ziggy.intel.matcher.builtin;

import gg.packetloss.ziggy.abstraction.ZBlockInfo;
import gg.packetloss.ziggy.intel.matcher.AbstractBlockAddMatcher;

public class ContainerAddActionMatcher extends AbstractBlockAddMatcher {
    @Override
    public boolean isMatchedBlock(ZBlockInfo blockType) {
        return blockType.isContainer();
    }

    @Override
    public int getOwnerAdjustment() {
        return 1;
    }

    @Override
    public int getOwnerVisibleAdjustment() {
        return 6;
    }

    @Override
    public int getGlobalTrustPunishmentLevel() {
        return -10;
    }

    @Override
    public int getGlobalTrustPunishmentAdjustment() {
        return -1;
    }

    @Override
    public int getFallbackTrustAdjustment() {
        return 0;
    }
}
