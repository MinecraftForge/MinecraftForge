/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event.sound;

import net.minecraft.client.sounds.SoundEngine;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.RecordEvent;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;

/**
 * Fired when the {@link SoundEngine} is constructed or (re)loaded, such as during game initialization or when the sound
 * output device is changed.
 *
 * <p>This event is fired only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 */
@NullMarked
public record SoundEngineLoadEvent(SoundEngine getEngine) implements RecordEvent, SoundEvent {
    public static final EventBus<SoundEngineLoadEvent> BUS = EventBus.create(SoundEngineLoadEvent.class);

    @Deprecated(forRemoval = true, since = "1.21.9")
    public static EventBus<SoundEngineLoadEvent> getBus(BusGroup modBusGroup) {
        return BUS;
    }

    @ApiStatus.Internal
    public SoundEngineLoadEvent {}
}
