/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fluids.capability.templates;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import org.jetbrains.annotations.NotNull;

public class EmptyFluidHandler implements IFluidHandler
{
    public static final EmptyFluidHandler INSTANCE = new EmptyFluidHandler();

    protected EmptyFluidHandler() {}

    @Override
    public int getTanks() { return 1; }

    @NotNull
    @Override
    public FluidStack getFluidInTank(int tank) { return FluidStack.EMPTY; }

    @Override
    public int getTankCapacity(int tank) { return 0; }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) { return true; }

    @Override
    public int fill(FluidStack resource, FluidAction action)
    {
        return 0;
    }

    @NotNull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action)
    {
        return FluidStack.EMPTY;
    }

    @NotNull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action)
    {
        return FluidStack.EMPTY;
    }
}
