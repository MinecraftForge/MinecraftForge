/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.client.event.sound;

import net.minecraft.client.sounds.SoundEngine;
import net.minecraftsingularity.eventbus.api.bus.EventBus;
import net.minecraftsingularity.eventbus.api.event.RecordEvent;
import net.minecraftsingularity.fml.LogicalSide;
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

    @ApiStatus.Internal
    public SoundEngineLoadEvent {}
}
