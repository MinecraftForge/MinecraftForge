/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.client.event.sound;

import com.mojang.blaze3d.audio.Channel;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraftsingularity.client.event.sound.SoundEvent.SoundSourceEvent;
import net.minecraftsingularity.common.MinecraftForge;
import net.minecraftsingularity.eventbus.api.bus.EventBus;
import net.minecraftsingularity.fml.LogicalSide;
import org.jetbrains.annotations.ApiStatus;

/**
 * Fired when a <em>non-streaming</em> sound is being played. A non-streaming sound is loaded fully into memory
 * in a buffer before being played, and used for most sounds of short length such as sound effects for clicking
 * buttons.
 *
 * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main singularity event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 *
 * @see PlayStreamingSourceEvent
 */
public record PlaySoundSourceEvent(SoundEngine getEngine, SoundInstance getSound, Channel getChannel, String getName)
        implements SoundSourceEvent {
    public static final EventBus<PlaySoundSourceEvent> BUS = EventBus.create(PlaySoundSourceEvent.class);

    @ApiStatus.Internal
    public PlaySoundSourceEvent(SoundEngine engine, SoundInstance sound, Channel channel) {
        this(engine, sound, channel, sound.getIdentifier().getPath());
    }

    @ApiStatus.Internal
    public PlaySoundSourceEvent {}
}
