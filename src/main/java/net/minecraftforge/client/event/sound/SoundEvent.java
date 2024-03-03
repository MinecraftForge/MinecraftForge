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
    private final SoundEngine manager;
    public SoundEvent(SoundEngine manager)
    {
        this.manager = manager;
    }

    public SoundEngine getManager()
    {
        return manager;
    }

    public static class SoundSourceEvent extends SoundEvent
    {
        private final SoundInstance sound;
        private final Channel source;
        private final String name;

        public SoundSourceEvent(SoundEngine manager, SoundInstance sound, Channel source)
        {
            super(manager);
            this.name = sound.getLocation().getPath();
            this.sound = sound;
            this.source = source;
        }

        public SoundInstance getSound()
        {
            return sound;
        }

        public Channel getSource()
        {
            return source;
        }

        public String getName()
        {
            return name;
        }
    }
}
