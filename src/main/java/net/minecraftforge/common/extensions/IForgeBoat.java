/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.extensions;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.FluidType;

public interface IForgeBoat
{
    private Boat self()
    {
        return (Boat) this;
    }

    /**
     * Returns whether the boat can be used on the fluid.
     *
     * @param state the state of the fluid
     * @return {@code true} if the boat can be used, {@code false} otherwise
     */
    default boolean canBoatInFluid(FluidState state)
    {
        return state.supportsBoating(self());
    }

    /**
     * Returns whether the boat can be used on the fluid.
     *
     * @param type the type of the fluid
     * @return {@code true} if the boat can be used, {@code false} otherwise
     */
    default boolean canBoatInFluid(FluidType type)
    {
        return type.supportsBoating(self());
    }

    /**
     * When {@code false}, the fluid will no longer update its height value while
     * within a boat while it is not within a fluid ({@link Boat#isUnderWater()}.
     *
     * @param state the state of the fluid the rider is within
     * @param rider the rider of the boat
     * @return {@code true} if the fluid height should be updated, {@code false} otherwise
     */
    default boolean shouldUpdateFluidWhileRiding(FluidState state, Entity rider)
    {
        return state.shouldUpdateWhileBoating(self(), rider);
    }
}
