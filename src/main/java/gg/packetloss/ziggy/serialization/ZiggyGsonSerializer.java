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

package gg.packetloss.ziggy.serialization;

import com.google.gson.Gson;
import gg.packetloss.ziggy.ZiggyStateManager;
import gg.packetloss.ziggy.point.ClusterManager;
import gg.packetloss.ziggy.trust.TrustManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ZiggyGsonSerializer implements ZiggySerializer {
    private static final Gson GSON = new Gson();

    private final Path saveDirectory;

    public ZiggyGsonSerializer(Path saveDirectory) {
        this.saveDirectory = saveDirectory;
    }

    private Path getClustersDirectory() {
        return saveDirectory.resolve("clusters");
    }

    @Override
    public void write(String world, Serializable<ClusterManager> clusterManager) throws IOException {
        Path clustersDir = getClustersDirectory();

        Files.createDirectories(clustersDir);

        Path worldFileTmp = clustersDir.resolve(world + ".json.tmp");
        try (BufferedWriter writer = Files.newBufferedWriter(worldFileTmp, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            writer.write(GSON.toJson(clusterManager.get()));
        }

        Path worldFile = clustersDir.resolve(world + ".json");
        Files.move(worldFileTmp, worldFile, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
    }

    private Path getTmpTrustFile() {
        return saveDirectory.resolve("trust.json.tmp");
    }

    private Path getTrustFile() {
        return saveDirectory.resolve("trust.json");
    }

    @Override
    public void write(Serializable<TrustManager> trustManager) throws IOException {
        Path trustFileTmp = getTmpTrustFile();
        try (BufferedWriter writer = Files.newBufferedWriter(trustFileTmp, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            writer.write(GSON.toJson(trustManager.get()));
        }

        Path trustFile = getTrustFile();
        Files.move(trustFileTmp, trustFile, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
    }

    @Override
    public ZiggyStateManager load() throws IOException {
        Map<String, ClusterManager> loadedClusters = new HashMap<>();

        if (Files.exists(getClustersDirectory())) {
            List<Path> worldClusters = Files.walk(getClustersDirectory()).filter(p -> p.toString().endsWith(".json")).collect(Collectors.toList());
            for (Path worldClusterPath : worldClusters) {
                String worldName = worldClusterPath.getFileName().toString().split("\\.")[0];
                ClusterManager clusterManager;

                try (BufferedReader reader = Files.newBufferedReader(worldClusterPath)) {
                    clusterManager = GSON.fromJson(reader, ClusterManager.class);
                }

                loadedClusters.put(worldName, clusterManager);
            }
        }

        TrustManager trustManager = new TrustManager();
        if (Files.exists(getTrustFile())) {
            try (BufferedReader reader = Files.newBufferedReader(getTrustFile())) {
                trustManager = GSON.fromJson(reader, TrustManager.class);
            }
        }

        return new ZiggyStateManager(loadedClusters, trustManager);
    }
}
