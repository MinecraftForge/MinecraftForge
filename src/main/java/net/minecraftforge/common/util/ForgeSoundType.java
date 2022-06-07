/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.util;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * A subclass of {@link SoundType} that uses {@link Supplier<SoundEvent>}s.
 * <p>
 * This class allows mod developers to safely create custom {@code SoundType}s for use in their e.g. {@link Block}.
 * <p>
 * The problem with using {@code SoundType} directly is it requires {@link SoundEvent} instances directly, because
 * {@code SoundType}s are required to be present during {@link Block} creation and registration. However,
 * {@code SoundEvent} must also be registered.
 * <p>
 * A possible solution of initializing {@code SoundEvent}s first would require static initialization of the
 * {@code SoundEvent} instances and later registration, which goes against the contract of the registry system and
 * prevents the use of {@link DeferredRegister} and {@link RegistryObject}s.
 * <p>
 * This class offers an alternative and preferable solution, by allowing mods to create {@link SoundType}s using
 * {@link Supplier}s of {@link SoundEvent}s instead, which do not require static initialization of {@code SoundEvent}s
 * and allow the direct use of {@code RegistryObject}s.
 *
 * @see SoundType
 */
public class ForgeSoundType extends SoundType
{
    private final Supplier<SoundEvent> breakSound;
    private final Supplier<SoundEvent> stepSound;
    private final Supplier<SoundEvent> placeSound;
    private final Supplier<SoundEvent> hitSound;
    private final Supplier<SoundEvent> fallSound;

    public ForgeSoundType(float volumeIn, float pitchIn, Supplier<SoundEvent> breakSoundIn, Supplier<SoundEvent> stepSoundIn, Supplier<SoundEvent> placeSoundIn, Supplier<SoundEvent> hitSoundIn, Supplier<SoundEvent> fallSoundIn)
    {
        super(volumeIn, pitchIn, (SoundEvent) null, (SoundEvent) null, (SoundEvent) null, (SoundEvent) null, (SoundEvent) null);
        this.breakSound = breakSoundIn;
        this.stepSound = stepSoundIn;
        this.placeSound = placeSoundIn;
        this.hitSound = hitSoundIn;
        this.fallSound = fallSoundIn;
    }

    @NotNull
    @Override
    public SoundEvent getBreakSound()
    {
        return breakSound.get();
    }

    @NotNull
    @Override
    public SoundEvent getStepSound()
    {
        return stepSound.get();
    }

    @NotNull
    @Override
    public SoundEvent getPlaceSound()
    {
        return placeSound.get();
    }

    @NotNull
    @Override
    public SoundEvent getHitSound()
    {
        return hitSound.get();
    }

    @NotNull
    @Override
    public SoundEvent getFallSound()
    {
        return fallSound.get();
    }
}
