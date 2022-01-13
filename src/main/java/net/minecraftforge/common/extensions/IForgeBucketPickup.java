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