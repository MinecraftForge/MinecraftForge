/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.extensions;

import java.util.Optional;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;

public interface IForgeBucketPickup
{

    private BucketPickup self()
    {
        return (BucketPickup) this;
    }

    /**
     * State sensitive variant of {@link BucketPickup#getPickupSound()}.
     *
     * Override to change the pickup sound based on the {@link BlockState} of the object being picked up.
     *
     * @param state State
     *
     * @return Sound event for pickup sound or empty if there isn't a pickup sound.
     */
    default Optional<SoundEvent> getPickupSound(BlockState state)
    {
        return self().getPickupSound();
    }
}