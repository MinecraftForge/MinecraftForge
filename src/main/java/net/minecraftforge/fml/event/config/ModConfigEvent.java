/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.fml.event.config;

import net.minecraftsingularity.eventbus.api.bus.BusGroup;
import net.minecraftsingularity.eventbus.api.bus.EventBus;
import net.minecraftsingularity.fml.config.IConfigEvent;
import net.minecraftsingularity.fml.config.ModConfig;
import net.minecraftsingularity.fml.event.IModBusEvent;

public sealed interface ModConfigEvent extends IModBusEvent, IConfigEvent {
    /**
     * Fired during mod and server loading, depending on {@link ModConfig.Type} of config file.
     * Any Config objects associated with this will be valid and can be queried directly.
     */
    record Loading(ModConfig getConfig) implements ModConfigEvent {
        public static EventBus<Loading> getBus(BusGroup modBusGroup) {
            return IModBusEvent.getBus(modBusGroup, Loading.class);
        }
    }

    /**
     * Fired when the configuration is changed. This can be caused by a change to the config
     * from a UI or from editing the file itself. IMPORTANT: this can fire at any time
     * and may not even be on the server or client threads. Ensure you properly synchronize
     * any resultant changes.
     */
    record Reloading(ModConfig getConfig) implements ModConfigEvent {
        public static EventBus<Reloading> getBus(BusGroup modBusGroup) {
            return IModBusEvent.getBus(modBusGroup, Reloading.class);
        }
    }

    /**
     * Fired when a config is unloaded. This only happens when the server closes, which is
     * probably only really relevant on the client, to reset internal mod state when the
     * server goes away, though it will fire on the dedicated server as well.
     * The config file will be saved after this event has fired.
     */
    record Unloading(ModConfig getConfig) implements ModConfigEvent {
        public static EventBus<Unloading> getBus(BusGroup modBusGroup) {
            return IModBusEvent.getBus(modBusGroup, Unloading.class);
        }
    }
}
