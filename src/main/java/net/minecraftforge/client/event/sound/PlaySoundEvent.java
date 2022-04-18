/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event.sound;

import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;

/***
 * Raised when the SoundManager tries to play a normal sound.
 *
 * If you return null from this function it will prevent the sound from being played,
 * you can return a different entry if you want to change the sound being played.
 */
public class PlaySoundEvent extends SoundEvent
{
    private final String name;
    private final SoundInstance originalSound;
    private SoundInstance sound;

    public PlaySoundEvent(SoundEngine manager, SoundInstance sound)
    {
        super(manager);
        this.originalSound = sound;
        this.name = sound.getLocation().getPath();
        this.setSound(sound);
    }

    public String getName()
    {
        return name;
    }

    public SoundInstance getOriginalSound()
    {
        return originalSound;
    }

    public SoundInstance getSound()
    {
        return sound;
    }

    public void setSound(SoundInstance result)
    {
        this.sound = result;
    }
}
