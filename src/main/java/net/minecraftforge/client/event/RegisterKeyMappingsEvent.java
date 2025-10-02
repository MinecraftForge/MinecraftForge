/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.MutableEvent;
import net.minecraftforge.eventbus.api.event.characteristic.SelfDestructing;
import net.minecraftforge.fml.LogicalSide;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;

/**
 * Allows users to register custom {@link net.minecraft.client.KeyMapping key mappings}.
 *
 * <p>This event is fired only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 */
@NullMarked
public final class RegisterKeyMappingsEvent extends MutableEvent implements SelfDestructing {
    public static final EventBus<RegisterKeyMappingsEvent> BUS = EventBus.create(RegisterKeyMappingsEvent.class);

    @Deprecated(forRemoval = true, since = "1.21.9")
    public static EventBus<RegisterKeyMappingsEvent> getBus(BusGroup modBusGroup) {
        return BUS;
    }

    private final Options options;

    @ApiStatus.Internal
    public RegisterKeyMappingsEvent(Options options) {
        this.options = options;
    }

    /**
     * Registers a new key mapping.
     */
    public void register(KeyMapping key) {
        options.keyMappings = ArrayUtils.add(options.keyMappings, key);
    }
}
