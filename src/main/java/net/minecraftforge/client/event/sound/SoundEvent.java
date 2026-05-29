/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.client.event.sound;

import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import com.mojang.blaze3d.audio.Channel;
import net.minecraftsingularity.common.MinecraftForge;
import net.minecraftsingularity.eventbus.api.bus.EventBus;
import net.minecraftsingularity.eventbus.api.event.InheritableEvent;
import net.minecraftsingularity.eventbus.api.event.MutableEvent;
import net.minecraftsingularity.fml.LogicalSide;
import org.jetbrains.annotations.ApiStatus;

/**
 * Superclass for sound related events.
 *
 * <p>These events are fired on the {@linkplain MinecraftForge#EVENT_BUS main singularity event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 *
 * @see SoundSourceEvent
 * @see PlaySoundEvent
 * @see SoundEngineLoadEvent
 */
public sealed interface SoundEvent permits PlaySoundEvent, SoundEngineLoadEvent, SoundEvent.SoundSourceEvent {
    /**
     * {@return the sound engine}
     */
    SoundEngine getEngine();

    /**
     * Superclass for when a sound has started to play on an audio channel.
     *
     * <p>These events are fired on the {@linkplain MinecraftForge#EVENT_BUS main singularity event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     *
     * @see PlaySoundSourceEvent
     * @see PlayStreamingSourceEvent
     */
    sealed interface SoundSourceEvent extends SoundEvent, InheritableEvent permits PlaySoundSourceEvent, PlayStreamingSourceEvent {
        EventBus<SoundSourceEvent> BUS = EventBus.create(SoundSourceEvent.class);

        /**
         * {@return the sound being played}
         */
        SoundInstance getSound();

        /**
         * {@return the audio channel on which the sound is playing on}
         */
        Channel getChannel();

        /**
         * {@return the name of the sound being played} This is equivalent to the path of the location of the original sound.
         */
        String getName();
    }
}
