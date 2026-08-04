/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event.sound;

import net.minecraftforge.eventbus.api.Event;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundEngine;
import net.minecraft.client.audio.SoundSource;

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
        private final ISound sound;
        private final SoundSource source;
        private final String name;

        public SoundSourceEvent(SoundEngine manager, ISound sound, SoundSource source)
        {
            super(manager);
            this.name = sound.getSoundLocation().getPath();
            this.sound = sound;
            this.source = source;
        }

        public ISound getSound()
        {
            return sound;
        }

        public SoundSource getSource()
        {
            return source;
        }

        public String getName()
        {
            return name;
        }
    }
}
