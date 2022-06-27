/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event.sound;

import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * Fired when a sound is about to be played by the sound engine. This fires before the sound is played and before any
 * checks on the sound (such as a zeroed volume, an empty {@link net.minecraft.client.resources.sounds.Sound}, and
 * others). This can be used to change or prevent (by passing {@code null)} a sound from being played through
 * {@link #setSound(SoundInstance)}).
 *
 * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.</p>
 *
 * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 *
 * @see PlaySoundSourceEvent
 * @see PlayStreamingSourceEvent
 */
public class PlaySoundEvent extends SoundEvent
{
    private final String name;
    private final SoundInstance originalSound;
    @Nullable
    private SoundInstance sound;

    @ApiStatus.Internal
    public PlaySoundEvent(SoundEngine manager, SoundInstance sound)
    {
        super(manager);
        this.originalSound = sound;
        this.name = sound.getLocation().getPath();
        this.setSound(sound);
    }

    /**
     * {@return the name of the original sound} This is equivalent to the path of the location of the original sound.
     */
    public String getName()
    {
        return name;
    }

    /**
     * {@return the original sound that was to be played}
     */
    public SoundInstance getOriginalSound()
    {
        return originalSound;
    }

    /**
     * {@return the sound to be played, or {@code null} if no sound will be played}
     */
    @Nullable
    public SoundInstance getSound()
    {
        return sound;
    }

    /**
     * Sets the sound to be played, which may be {@code null} to prevent any sound from being played.
     *
     * @param newSound the new sound to be played, or {@code null} for no sound
     */
    public void setSound(@Nullable SoundInstance newSound)
    {
        this.sound = newSound;
    }
}
