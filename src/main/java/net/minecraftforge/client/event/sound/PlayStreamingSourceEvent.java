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
 * Fired when a <em>streaming</em> sound is being played. A streaming sound is streamed directly from its source
 * (such as a file), and used for sounds of long length which are unsuitable to keep fully loaded in-memory in a buffer
 * (as is done for regular non-streaming sounds), such as background music or music discs.
 *
 * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main singularity event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 *
 * @see PlayStreamingSourceEvent
 */
public record PlayStreamingSourceEvent(SoundEngine getEngine, SoundInstance getSound, Channel getChannel, String getName)
        implements SoundSourceEvent {
    public static final EventBus<PlayStreamingSourceEvent> BUS = EventBus.create(PlayStreamingSourceEvent.class);

    @ApiStatus.Internal
    public PlayStreamingSourceEvent(SoundEngine engine, SoundInstance sound, Channel channel) {
        this(engine, sound, channel, sound.getIdentifier().getPath());
    }

    @ApiStatus.Internal
    public PlayStreamingSourceEvent {}
}
