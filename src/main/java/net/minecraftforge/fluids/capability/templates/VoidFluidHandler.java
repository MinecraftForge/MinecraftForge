/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fluids.capability.templates;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;

import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

/**
 * VoidFluidHandler is a template fluid handler that can be filled indefinitely without ever getting full.
 * It does not store fluid that gets filled into it, but "destroys" it upon receiving it.
 */
public class VoidFluidHandler implements IFluidHandler
{
    public static final VoidFluidHandler INSTANCE = new VoidFluidHandler();

    public VoidFluidHandler() {}

    @Override
    public int getTanks() { return 1; }

    @Nonnull
    @Override
    public FluidStack getFluidInTank(int tank) { return FluidStack.EMPTY; }

    @Override
    public int getTankCapacity(int tank) { return Integer.MAX_VALUE; }

    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) { return true; }

    @Override
    public int fill(FluidStack resource, FluidAction action)
    {
        return resource.getAmount();
    }

    @Nonnull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action)
    {
        return FluidStack.EMPTY;
    }

    @Nonnull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action)
    {
        return FluidStack.EMPTY;
    }
}
