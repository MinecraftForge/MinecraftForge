/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event.sound;

import net.minecraft.client.sounds.SoundEngine;

/**
 * This event is raised by the SoundManager when it does its first setup of the
 * SoundSystemConfig's codecs, use this function to add your own codecs.
 */
public class SoundSetupEvent extends SoundEvent
{
    public SoundSetupEvent(SoundEngine manager)
    {
        super(manager);
    }
}
