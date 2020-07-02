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

import gg.packetloss.ziggy.serialization.Serializable;
import gg.packetloss.ziggy.serialization.SerializationConsumer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TrustManager {
    private Map<UUID, TrustIndex> playerTrust = new HashMap<>();
    private TrustIndex globalTrust = new TrustIndex();

    private transient boolean dirty = false;

    private transient ReadWriteLock trustLock = new ReentrantReadWriteLock();

    private void adjustLocalizedTrustWithLock(UUID owner, UUID player, int adjustment) {
        // Don't create a "self trust" index, that's just silly
        if (owner.equals(player)) {
            return;
        }

        TrustIndex trustIndex = playerTrust.compute(owner, (ignored, value) -> {
            if (value == null) {
                value = new TrustIndex();
            }
            return value;
        });

        trustIndex.adjustTrust(player, adjustment);
    }

    private void adjustGlobalTrustWithLock(UUID player, int adjustment) {
        globalTrust.adjustTrust(player, adjustment);
    }

    private void adjustTrustWithLock(UUID owner, UUID player, int adjustment) {
        adjustLocalizedTrustWithLock(owner, player, adjustment);
        adjustGlobalTrustWithLock(player, adjustment);

        dirty = true;
    }

    public void adjustTrust(UUID owner, UUID player, int adjustment) {
        trustLock.writeLock().lock();

        try {
            adjustTrustWithLock(owner, player, adjustment);
        } finally {
            trustLock.writeLock().unlock();
        }
    }

    private ImmutableTrustData getGlobalTrustWithLock(UUID player) {
        return globalTrust.getTrust(player);
    }

    public ImmutableTrustData getGlobalTrust(UUID player) {
        trustLock.readLock().lock();

        try {
            return getGlobalTrustWithLock(player);
        } finally {
            trustLock.readLock().unlock();
        }
    }

    private ImmutableTrustData getLocalTrustWithLock(UUID owner, UUID player) {
        TrustIndex localTrustIndex = playerTrust.get(owner);
        if (localTrustIndex != null) {
            return localTrustIndex.getTrust(player);
        }

        return ImmutableTrustData.NONE;
    }

    public ImmutableTrustData getLocalTrust(UUID owner, UUID player) {
        trustLock.readLock().lock();

        try {
            return getLocalTrustWithLock(owner, player);
        } finally {
            trustLock.readLock().unlock();
        }
    }

    public void writeToDisk(SerializationConsumer<TrustManager> consumer) throws IOException {
        // If we don't get the lock immediately come back later, writing to disk is low priority.
        if (!trustLock.writeLock().tryLock()) {
            return;
        }

        try {
            if (!dirty) {
                return;
            }

            consumer.accept(new Serializable<>(this));
            dirty = false;
        } finally {
            trustLock.writeLock().unlock();
        }
    }
}
