/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event.sound;

import net.minecraft.client.sounds.SoundEngine;
import net.minecraftforge.fml.event.IModBusEvent;

/**
 * Raised by the SoundManager.loadSoundSettings, this would be a good place for
 * adding your custom sounds to the SoundPool.
 */
public class SoundLoadEvent extends SoundEvent implements IModBusEvent
{
    public SoundLoadEvent(SoundEngine manager)
    {
        super(manager);
    }
}
