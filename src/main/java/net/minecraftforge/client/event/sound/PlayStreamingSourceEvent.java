/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event.sound;

import com.mojang.blaze3d.audio.Channel;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraftforge.client.event.sound.SoundEvent.SoundSourceEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.ApiStatus;

/**
 * Fired when a <em>streaming</em> sound is being played. A streaming sound is streamed directly from its source
 * (such as a file), and used for sounds of long length which are unsuitable to keep fully loaded in-memory in a buffer
 * (as is done for regular non-streaming sounds), such as background music or music discs.
 *
 * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.</p>
 *
 * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 *
 * @see PlayStreamingSourceEvent
 */
public class PlayStreamingSourceEvent extends SoundSourceEvent
{
    @ApiStatus.Internal
    public PlayStreamingSourceEvent(SoundEngine engine, SoundInstance sound, Channel channel)
    {
        super(engine, sound, channel);
    }
}
