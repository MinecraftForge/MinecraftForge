/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event.sound;

import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import com.mojang.blaze3d.audio.Channel;

public class SoundEvent extends net.minecraftforge.eventbus.api.Event
{
    private final SoundEngine engine;
    public SoundEvent(SoundEngine engine)
    {
        this.engine = engine;
    }

    public SoundEngine getEngine()
    {
        return engine;
    }

    public static class SoundSourceEvent extends SoundEvent
    {
        private final SoundInstance sound;
        private final Channel channel;
        private final String name;

        public SoundSourceEvent(SoundEngine manager, SoundInstance sound, Channel channel)
        {
            super(manager);
            this.name = sound.getLocation().getPath();
            this.sound = sound;
            this.channel = channel;
        }

        public SoundInstance getSound()
        {
            return sound;
        }

        public Channel getChannel()
        {
            return channel;
        }

        public String getName()
        {
            return name;
        }
    }
}
