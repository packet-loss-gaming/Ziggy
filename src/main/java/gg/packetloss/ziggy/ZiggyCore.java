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

import gg.packetloss.ziggy.abstraction.ZTaskBuilder;
import org.apache.commons.lang.Validate;

public class ZiggyCore {
    private static ZiggyCore inst;

    private ZTaskBuilder taskBuilder;
    private ZiggyConfig config;
    private ZiggyStateManager stateManager;

    private ZiggyCore() { }

    public static ZiggyCore inst() {
        if (inst == null) {
            inst = new ZiggyCore();
        }

        return inst;
    }

    public void registerTaskBuilder(ZTaskBuilder taskBuilder) {
        Validate.isTrue(this.taskBuilder == null);
        this.taskBuilder = taskBuilder;
    }

    public void registerConfig(ZiggyConfig config) {
        Validate.isTrue(this.config == null);
        this.config = config;
    }

    public void registerStateManager(ZiggyStateManager stateManager) {
        Validate.isTrue(this.stateManager == null);
        this.stateManager = stateManager;
    }

    public static ZTaskBuilder getTaskBuilder() {
        return inst().taskBuilder;
    }

    public static ZiggyConfig getConfig() {
        return inst().config;
    }

    public static ZiggyStateManager getStateManager() {
        return inst().stateManager;
    }
}
