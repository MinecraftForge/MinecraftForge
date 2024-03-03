/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.event.config;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.config.IConfigEvent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.IModBusEvent;

public class ModConfigEvent extends Event implements IModBusEvent, IConfigEvent {
    private final ModConfig config;

    ModConfigEvent(final ModConfig config) {
        this.config = config;
    }

    @Override
    public ModConfig getConfig() {
        return config;
    }

    public static class Loading extends ModConfigEvent {
        public Loading(final ModConfig config) {
            super(config);
        }
    }

    public static class Reloading extends ModConfigEvent {
        public Reloading(final ModConfig config) {
            super(config);
        }
    }
}
