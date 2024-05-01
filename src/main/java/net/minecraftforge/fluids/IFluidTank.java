/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fluids;

import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

import javax.annotation.Nonnull;

/**
 * This interface represents a Fluid Tank. IT IS NOT REQUIRED but is provided for convenience.
 * You are free to handle Fluids in any way that you wish - this is simply an easy default way.
 * DO NOT ASSUME that these objects are used internally in all cases.
 */
public interface IFluidTank {

    /**
     * @return FluidStack representing the fluid in the tank, null if the tank is empty.
     */
    @Nonnull
    FluidStack getFluid();

    /**
     * @return Current amount of fluid in the tank.
     */
    int getFluidAmount();

    /**
     * @return Capacity of this fluid tank.
     */
    int getCapacity();

    /**
     * @param stack Fluidstack holding the Fluid to be queried.
     * @return If the tank can hold the fluid (EVER, not at the time of query).
     */
    boolean isFluidValid(FluidStack stack);

    /**
     * @param resource FluidStack attempting to fill the tank.
     * @param action   If SIMULATE, the fill will only be simulated.
     * @return Amount of fluid that was accepted (or would be, if simulated) by the tank.
     */
    int fill(FluidStack resource, FluidAction action);

    /**
     * @param maxDrain Maximum amount of fluid to be removed from the container.
     * @param action   If SIMULATE, the drain will only be simulated.
     * @return Amount of fluid that was removed (or would be, if simulated) from the tank.
     */
    @Nonnull
    FluidStack drain(int maxDrain, FluidAction action);

    /**
     * @param resource Maximum amount of fluid to be removed from the container.
     * @param action   If SIMULATE, the drain will only be simulated.
     * @return FluidStack representing fluid that was removed (or would be, if simulated) from the tank.
     */
    @Nonnull
    FluidStack drain(FluidStack resource, FluidAction action);

}
