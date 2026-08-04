/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event.sound;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundEngine;
import net.minecraft.client.audio.SoundSource;
import net.minecraftforge.client.event.sound.SoundEvent.SoundSourceEvent;

public class PlayStreamingSourceEvent extends SoundSourceEvent
{
    public PlayStreamingSourceEvent(SoundEngine manager, ISound sound, SoundSource source)
    {
        super(manager, sound, source);
    }
}
