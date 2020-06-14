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

import gg.packetloss.ziggy.abstraction.ZLocation;
import gg.packetloss.ziggy.abstraction.ZWorld;
import gg.packetloss.ziggy.point.AnnotatedPointCluster;
import gg.packetloss.ziggy.point.ClusterManager;
import gg.packetloss.ziggy.point.Point2D;
import gg.packetloss.ziggy.serialization.ZiggySerializer;
import gg.packetloss.ziggy.trust.TrustManager;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ZiggyCore {
    private final Map<String, ClusterManager> clusterManager;
    private final TrustManager trustManager;

    public ZiggyCore(Map<String, ClusterManager> clusterManager, TrustManager trustManager) {
        this.clusterManager = clusterManager;
        this.trustManager = trustManager;
    }

    private ClusterManager getFor(ZWorld world) {
        return clusterManager.compute(world.getName(), (ignored, value) -> {
            if (value == null) {
                value = new ClusterManager();
            }
            return value;
        });
    }

    public List<AnnotatedPointCluster> getAffectedClusters(ZWorld world, Point2D point) {
        return getFor(world).getClustersAt(point);
    }

    public List<AnnotatedPointCluster> getAffectedClusters(ZLocation location) {
        return getAffectedClusters(location.getWorld(), location.getPosition().to2D());
    }

    public void enqueue(UUID player, ZWorld world, Point2D point) {
        getFor(world).enqueue(player, point);
    }

    public void enqueue(UUID player, ZLocation location) {
        enqueue(player, location.getWorld(), location.getPosition().to2D());
    }

    public void applyTrustModification(UUID owner, UUID player, int adjustment) {
        trustManager.adjustTrust(owner, player, adjustment);
    }

    public int getGlobalTrust(UUID player) {
        return trustManager.getGlobalTrust(player);
    }

    public int getLocalTrust(UUID owner, UUID player) {
        return trustManager.getLocalTrust(owner, player);
    }

    public void serializeWith(ZiggySerializer serializer) throws IOException {
        for (Map.Entry<String, ClusterManager> entry : clusterManager.entrySet()) {
            serializer.write(entry.getKey(), entry.getValue());
        }

        serializer.write(trustManager);
    }
}
