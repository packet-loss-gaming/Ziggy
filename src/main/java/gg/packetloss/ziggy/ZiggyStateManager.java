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

import gg.packetloss.ziggy.abstraction.BlockClassification;
import gg.packetloss.ziggy.abstraction.ZLocation;
import gg.packetloss.ziggy.abstraction.ZWorld;
import gg.packetloss.ziggy.point.AnnotatedPointCluster;
import gg.packetloss.ziggy.point.ClusterManager;
import gg.packetloss.ziggy.point.Point2D;
import gg.packetloss.ziggy.serialization.ZiggySerializer;
import gg.packetloss.ziggy.trust.TrustManager;

import java.io.IOException;
import java.util.*;

public class ZiggyStateManager {
    private final Map<String, ClusterManager> clusterManager;
    private final TrustManager trustManager;

    public ZiggyStateManager(Map<String, ClusterManager> clusterManager, TrustManager trustManager) {
        this.clusterManager = clusterManager;
        this.trustManager = trustManager;
    }

    private Optional<ClusterManager> getFor(ZWorld world) {
        if (ZiggyCore.getConfig().ignoredWorlds.contains(world.getFriendlyName())) {
            return Optional.empty();
        }

        return Optional.of(clusterManager.compute(world.getName(), (ignored, value) -> {
            if (value == null) {
                value = new ClusterManager();
            }
            return value;
        }));
    }

    public List<AnnotatedPointCluster> getAffectedClusters(ZWorld world, Point2D point) {
        return getFor(world).map(clusterManager -> clusterManager.getClustersAt(point)).orElse(new ArrayList<>());
    }

    public List<AnnotatedPointCluster> getAffectedClusters(ZLocation location) {
        return getAffectedClusters(location.getWorld(), location.getPosition().to2D());
    }

    public List<AnnotatedPointCluster> getClustersNear(ZWorld world, Point2D point) {
        return getFor(world).map(clusterManager -> clusterManager.getClustersNear(point)).orElse(new ArrayList<>());
    }

    public List<AnnotatedPointCluster> getClustersNear(ZLocation location) {
        return getClustersNear(location.getWorld(), location.getPosition().to2D());
    }

    public void enqueue(UUID player, ZWorld world, Point2D point, BlockClassification classification) {
        getFor(world).ifPresent(clusterManager -> clusterManager.enqueue(player, point, classification));
    }

    public void enqueue(UUID player, ZLocation location, BlockClassification classification) {
        enqueue(player, location.getWorld(), location.getPosition().to2D(), classification);
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

    public void cleanup() {
        for (ClusterManager manager : clusterManager.values()) {
            manager.cleanup();
        }
    }

    public void serializeWith(ZiggySerializer serializer) throws IOException {
        for (Map.Entry<String, ClusterManager> entry : clusterManager.entrySet()) {
            entry.getValue().writeToDisk(serializableCluster -> {
                serializer.write(entry.getKey(), serializableCluster);
            });
        }

        trustManager.writeToDisk(serializer::write);
    }
}
