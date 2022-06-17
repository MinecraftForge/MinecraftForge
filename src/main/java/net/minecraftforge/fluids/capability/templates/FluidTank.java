/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fluids.capability.templates;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

/**
 * Flexible implementation of a Fluid Storage object. NOT REQUIRED.
 *
 * @author King Lemming
 */
public class FluidTank implements IFluidHandler, IFluidTank {

    protected Predicate<FluidStack> validator;
    @NotNull
    protected FluidStack fluid = FluidStack.EMPTY;
    protected int capacity;

    public FluidTank(int capacity)
    {
        this(capacity, e -> true);
    }

    public FluidTank(int capacity, Predicate<FluidStack> validator)
    {
        this.capacity = capacity;
        this.validator = validator;
    }

    public FluidTank setCapacity(int capacity)
    {
        this.capacity = capacity;
        return this;
    }

    public FluidTank setValidator(Predicate<FluidStack> validator)
    {
        if (validator != null) {
            this.validator = validator;
        }
        return this;
    }

    public boolean isFluidValid(FluidStack stack)
    {
        return validator.test(stack);
    }

    public int getCapacity()
    {
        return capacity;
    }

    @NotNull
    public FluidStack getFluid()
    {
        return fluid;
    }

    public int getFluidAmount()
    {
        return fluid.getAmount();
    }

    public FluidTank readFromNBT(CompoundTag nbt) {

        FluidStack fluid = FluidStack.loadFluidStackFromNBT(nbt);
        setFluid(fluid);
        return this;
    }

    public CompoundTag writeToNBT(CompoundTag nbt) {

        fluid.writeToNBT(nbt);

        return nbt;
    }

    @Override
    public int getTanks() {

        return 1;
    }

    @NotNull
    @Override
    public FluidStack getFluidInTank(int tank) {

        return getFluid();
    }

    @Override
    public int getTankCapacity(int tank) {

        return getCapacity();
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {

        return isFluidValid(stack);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action)
    {
        if (resource.isEmpty() || !isFluidValid(resource))
        {
            return 0;
        }
        if (action.simulate())
        {
            if (fluid.isEmpty())
            {
                return Math.min(capacity, resource.getAmount());
            }
            if (!fluid.isFluidEqual(resource))
            {
                return 0;
            }
            return Math.min(capacity - fluid.getAmount(), resource.getAmount());
        }
        if (fluid.isEmpty())
        {
            fluid = new FluidStack(resource, Math.min(capacity, resource.getAmount()));
            onContentsChanged();
            return fluid.getAmount();
        }
        if (!fluid.isFluidEqual(resource))
        {
            return 0;
        }
        int filled = capacity - fluid.getAmount();

        if (resource.getAmount() < filled)
        {
            fluid.grow(resource.getAmount());
            filled = resource.getAmount();
        }
        else
        {
            fluid.setAmount(capacity);
        }
        if (filled > 0)
            onContentsChanged();
        return filled;
    }

    @NotNull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action)
    {
        if (resource.isEmpty() || !resource.isFluidEqual(fluid))
        {
            return FluidStack.EMPTY;
        }
        return drain(resource.getAmount(), action);
    }

    @NotNull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action)
    {
        int drained = maxDrain;
        if (fluid.getAmount() < drained)
        {
            drained = fluid.getAmount();
        }
        FluidStack stack = new FluidStack(fluid, drained);
        if (action.execute() && drained > 0)
        {
            fluid.shrink(drained);
            onContentsChanged();
        }
        return stack;
    }

    protected void onContentsChanged()
    {

    }

    public void setFluid(FluidStack stack)
    {
        this.fluid = stack;
    }

    public boolean isEmpty()
    {
        return fluid.isEmpty();
    }

    public int getSpace()
    {
        return Math.max(0, capacity - fluid.getAmount());
    }

}
