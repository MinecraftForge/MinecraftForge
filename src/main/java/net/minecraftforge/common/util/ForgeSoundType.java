/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

import net.minecraft.block.SoundType;
import net.minecraft.util.SoundEvent;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class ForgeSoundType extends SoundType
{
    private final Supplier<SoundEvent> breakSound;
    private final Supplier<SoundEvent> stepSound;
    private final Supplier<SoundEvent> placeSound;
    private final Supplier<SoundEvent> hitSound;
    private final Supplier<SoundEvent> fallSound;

    public ForgeSoundType(float volumeIn, float pitchIn, Supplier<SoundEvent> breakSoundIn, Supplier<SoundEvent> stepSoundIn, Supplier<SoundEvent> placeSoundIn, Supplier<SoundEvent> hitSoundIn, Supplier<SoundEvent> fallSoundIn)
    {
        super(volumeIn, pitchIn, null, null, null, null, null);
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
