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

import gg.packetloss.ziggy.ZiggyCore;
import gg.packetloss.ziggy.point.AnnotatedPointCluster;
import gg.packetloss.ziggy.trust.ImmutableTrustData;

public class PlayerTrustContext {
    private final AnnotatedPointCluster affectedCluster;
    private final int regionalInvestment;
    private final ImmutableTrustData globalTrust;
    private final ImmutableTrustData localTrust;

    public PlayerTrustContext(AnnotatedPointCluster affectedCluster, int regionalInvestment,
                              ImmutableTrustData globalTrust, ImmutableTrustData localTrust) {
        this.affectedCluster = affectedCluster;
        this.regionalInvestment = regionalInvestment;
        this.globalTrust = globalTrust;
        this.localTrust = localTrust;
    }

    public AnnotatedPointCluster getAffectedCluster() {
        return affectedCluster;
    }

    public int getRegionalInvestment() {
        return regionalInvestment;
    }

    public ImmutableTrustData getGlobalTrust() {
        return globalTrust;
    }

    public ImmutableTrustData getLocalTrust() {
        return localTrust;
    }

    public int getQuantifiedGlobalTrust() {
        return globalTrust.getContribution() + (globalTrust.getPrestige() * ZiggyCore.getConfig().trustPrestigeAmount);
    }

    public int getQuantifiedLocalTrust() {
        return localTrust.getContribution() + (localTrust.getPrestige() * ZiggyCore.getConfig().trustPrestigeAmount);
    }
}
