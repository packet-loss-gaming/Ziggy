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

import gg.packetloss.ziggy.point.AnnotatedPointCluster;

public class PlayerTrustContext {
    private final AnnotatedPointCluster affectedCluster;
    private final int numOtherInterest;
    private final int globalTrust;
    private final int currentTrust;

    public PlayerTrustContext(AnnotatedPointCluster affectedCluster, int numOtherInterest, int globalTrust, int currentTrust) {
        this.affectedCluster = affectedCluster;
        this.numOtherInterest = numOtherInterest;
        this.globalTrust = globalTrust;
        this.currentTrust = currentTrust;
    }

    public AnnotatedPointCluster getAffectedCluster() {
        return affectedCluster;
    }

    public int getNumberOfInterests() {
        return numOtherInterest;
    }

    public int getGlobalTrust() {
        return globalTrust;
    }

    public int getLocalTrust() {
        return currentTrust;
    }
}
