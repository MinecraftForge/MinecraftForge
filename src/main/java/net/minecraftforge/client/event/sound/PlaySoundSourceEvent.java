/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event.sound;

import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import com.mojang.blaze3d.audio.Channel;
import net.minecraftforge.client.event.sound.SoundEvent.SoundSourceEvent;

public class PlaySoundSourceEvent extends SoundSourceEvent
{
    public PlaySoundSourceEvent(SoundEngine manager, SoundInstance sound, Channel source)
    {
        super(manager, sound, source);
    }
}
