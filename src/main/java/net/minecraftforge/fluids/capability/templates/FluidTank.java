/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

/*
 * (C) 2014-2018 Team CoFH / CoFH / Cult of the Full Hub
 * http://www.teamcofh.com
 */
package net.minecraftforge.fluids.capability.templates;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Predicate;

/**
 * Flexible implementation of a Fluid Storage object. NOT REQUIRED.
 *
 * @author King Lemming
 */
public class FluidTank implements IFluidHandler, IFluidTank {

    protected Predicate<FluidStack> validator;
    @Nullable
    protected FluidStack fluid = null;
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

    public FluidStack getFluid()
    {
        return fluid;
    }

    public int getFluidAmount()
    {

        if (fluid == null)
        {
            return 0;
        }
        return fluid.amount;
    }

    public FluidTank readFromNBT(CompoundNBT nbt) {

        FluidStack fluid = null;
        if (!nbt.contains("Empty"))
        {
            fluid = FluidStack.loadFluidStackFromNBT(nbt);
        }
        setFluid(fluid);
        return this;
    }

    public CompoundNBT writeToNBT(CompoundNBT nbt) {

        if (fluid != null)
        {
            fluid.writeToNBT(nbt);
        }
        else
        {
            nbt.putString("Empty", "");
        }
        return nbt;
    }

    @Override
    public int getTanks() {

        return 1;
    }

    @Nullable
    @Override
    public FluidStack getFluidInTank(int tank) {

        return getFluid();
    }

    @Override
    public int getTankCapacity(int tank) {

        return getCapacity();
    }

    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {

        return isFluidValid(stack);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action)
    {
        if (resource == null || !isFluidValid(resource))
        {
            return 0;
        }
        if (action.simulate())
        {
            if (fluid == null)
            {
                return Math.min(capacity, resource.amount);
            }
            if (!fluid.isFluidEqual(resource))
            {
                return 0;
            }
            return Math.min(capacity - fluid.amount, resource.amount);
        }
        if (fluid == null)
        {
            onContentsChanged();
            fluid = new FluidStack(resource, Math.min(capacity, resource.amount));
            return fluid.amount;
        }
        if (!fluid.isFluidEqual(resource))
        {
            return 0;
        }
        int filled = capacity - fluid.amount;

        if (resource.amount < filled)
        {
            fluid.amount += resource.amount;
            filled = resource.amount;
        }
        else
        {
            fluid.amount = capacity;
        }
        return filled;
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action)
    {
        if (resource == null || !resource.isFluidEqual(fluid))
        {
            return null;
        }
        return drain(resource.amount, action);
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action)
    {
        if (fluid == null)
        {
            return null;
        }
        int drained = maxDrain;
        if (fluid.amount < drained)
        {
            drained = fluid.amount;
        }
        FluidStack stack = new FluidStack(fluid, drained);
        if (action.execute())
        {
            fluid.amount -= drained;
            if (fluid.amount <= 0)
            {
                fluid = null;
            }
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
        return fluid == null || fluid.amount <= 0;
    }

    public int getSpace()
    {
        if (fluid == null)
        {
            return capacity;
        }
        return fluid.amount >= capacity ? 0 : capacity - fluid.amount;
    }

}
