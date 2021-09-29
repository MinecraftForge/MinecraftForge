/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.common.util;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.sounds.SoundEvent;

import javax.annotation.Nonnull;
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

    @Nonnull
    @Override
    public SoundEvent getBreakSound()
    {
        return breakSound.get();
    }

    @Nonnull
    @Override
    public SoundEvent getStepSound()
    {
        return stepSound.get();
    }

    @Nonnull
    @Override
    public SoundEvent getPlaceSound()
    {
        return placeSound.get();
    }

    @Nonnull
    @Override
    public SoundEvent getHitSound()
    {
        return hitSound.get();
    }

    @Nonnull
    @Override
    public SoundEvent getFallSound()
    {
        return fallSound.get();
    }
}
