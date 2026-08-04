/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event.sound;

import net.minecraft.client.audio.SoundEngine;

/**
 * Raised by the SoundManager.loadSoundSettings, this would be a good place for
 * adding your custom sounds to the SoundPool.
 */
public class SoundLoadEvent extends SoundEvent
{
    public SoundLoadEvent(SoundEngine manager)
    {
        super(manager);
    }
}
