/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event.sound;

import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import com.mojang.blaze3d.audio.Channel;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.ApiStatus;

/**
 * Superclass for sound related events.
 *
 * <p>These events are fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 *
 * @see SoundSourceEvent
 * @see PlaySoundEvent
 * @see SoundEngineLoadEvent
 */
public abstract class SoundEvent extends Event
{
    private final SoundEngine engine;

    @ApiStatus.Internal
    protected SoundEvent(SoundEngine engine)
    {
        this.engine = engine;
    }

    /**
     * {@return the sound engine}
     */
    public SoundEngine getEngine()
    {
        return engine;
    }

    /**
     * Superclass for when a sound has started to play on an audio channel.
     *
     * <p>These events are fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     *
     * @see PlaySoundSourceEvent
     * @see PlayStreamingSourceEvent
     */
    public static abstract class SoundSourceEvent extends SoundEvent
    {
        private final SoundInstance sound;
        private final Channel channel;
        private final String name;

        @ApiStatus.Internal
        protected SoundSourceEvent(SoundEngine engine, SoundInstance sound, Channel channel)
        {
            super(engine);
            this.name = sound.getLocation().getPath();
            this.sound = sound;
            this.channel = channel;
        }

        /**
         * {@return the sound being played}
         */
        public SoundInstance getSound()
        {
            return sound;
        }

        /**
         * {@return the audio channel on which the sound is playing on}
         */
        public Channel getChannel()
        {
            return channel;
        }

        /**
         * {@return the name of the sound being played} This is equivalent to the path of the location of the original sound.
         */
        public String getName()
        {
            return name;
        }
    }
}
