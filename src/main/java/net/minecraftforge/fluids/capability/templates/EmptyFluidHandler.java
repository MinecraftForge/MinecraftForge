/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fluids.capability.templates;

import javax.annotation.Nonnull;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

public class EmptyFluidHandler implements IFluidHandler
{
    public static final EmptyFluidHandler INSTANCE = new EmptyFluidHandler();

    protected EmptyFluidHandler() {}

    @Override
    public int getTanks() { return 1; }

    @Nonnull
    @Override
    public FluidStack getFluidInTank(int tank) { return FluidStack.EMPTY; }

    @Override
    public int getTankCapacity(int tank) { return 0; }

    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) { return true; }

    @Override
    public int fill(FluidStack resource, FluidAction action)
    {
        return 0;
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
