/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event.sound;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundEngine;

/***
 * Raised when the SoundManager tries to play a normal sound.
 *
 * If you return null from this function it will prevent the sound from being played,
 * you can return a different entry if you want to change the sound being played.
 */
public class PlaySoundEvent extends SoundEvent
{
    private final String name;
    private final ISound sound;
    private ISound result;

    public PlaySoundEvent(SoundEngine manager, ISound sound)
    {
        super(manager);
        this.sound = sound;
        this.name = sound.getSoundLocation().getPath();
        this.setResultSound(sound);
    }

    public String getName()
    {
        return name;
    }

    public ISound getSound()
    {
        return sound;
    }

    public ISound getResultSound()
    {
        return result;
    }

    public void setResultSound(ISound result)
    {
        this.result = result;
    }
}
