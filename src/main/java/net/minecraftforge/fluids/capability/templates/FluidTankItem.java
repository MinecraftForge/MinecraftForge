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

package net.minecraftforge.fluids.capability.templates;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.FluidResult;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import javax.annotation.Nonnull;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Flexible implementation of a Fluid Storage object. NOT REQUIRED.
 *
 * @author King Lemming
 */
public class FluidTankItem implements IFluidHandlerItem {

    protected Predicate<FluidStack> validator;
    protected Function<FluidResult, ItemStack> stackFunction;
    protected int capacity;
    protected FluidStack fluidStack;

    public FluidTankItem(int capacity) {
        this(capacity, e -> true, FluidResult::getItemStack);
    }

    public FluidTankItem(int capacity, Predicate<FluidStack> validator) {
        this(capacity, validator, FluidResult::getItemStack);
    }

    public FluidTankItem(int capacity, Predicate<FluidStack> validator, Function<FluidResult, ItemStack> stackCheck) {
        this.capacity = capacity;
        this.validator = validator;
        this.stackFunction = stackCheck;
    }

    public FluidTankItem setCapacity(int capacity)
    {
        this.capacity = capacity;
        return this;
    }

    public FluidTankItem setValidator(Predicate<FluidStack> validator)
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

    @Nonnull
    public FluidStack getFluid()
    {
        return fluidStack;
    }
    public int getFluidAmount()
    {
        return fluidStack.getAmount();
    }

    public FluidTankItem readFromNBT(CompoundNBT nbt) {
        FluidStack fluid = FluidStack.loadFluidStackFromNBT(nbt);
        setFluid(fluid);
        return this;
    }

    public CompoundNBT writeToNBT(CompoundNBT nbt) {
        fluidStack.writeToNBT(nbt);
        return nbt;
    }

    @Override
    public int getTanks() {

        return 1;
    }

    @Nonnull
    @Override
    public FluidStack getFluidInTank(int tank) {

        return fluidStack;
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
    public FluidResult fillItem(FluidResult resource, FluidAction action) {
        if (resource.isEmpty() || !isFluidValid(resource.getFluidStack())) {
            return FluidResult.EMPTY;
        }
        if (action.simulate()) {
            FluidResult returned = resource.copy();
            if (resource.getFluidStack().isEmpty()) {
                returned.capFluidAmount(capacity);
                returned.updateStack(stackFunction);
                return returned;
            }
            if (!fluidStack.isFluidEqual(resource)) {
                return FluidResult.EMPTY;
            }
            returned.capFluidAmount(capacity);
            returned.updateStack(stackFunction);
            return returned;
        }

        if (fluidStack.isEmpty()) {
            resource.capFluidAmount(capacity);
            resource.updateStack(stackFunction);
            fluidStack = resource.getFluidStack();
            onContentsChanged();
            return resource;
        }
        if (!fluidStack.isFluidEqual(resource)) {
            return FluidResult.EMPTY;
        }

        FluidResult returned = resource.copy();

        if (resource.getFluidAmount() < resource.getFluidAmount()) {
            fluidStack.grow(resource.getFluidAmount());
            returned = resource.copy();
        }
        else {
            fluidStack.setAmount(capacity);
        }
        if (returned.getFluidAmount() > 0) {
            onContentsChanged();
        }
        return returned;
    }

    @Nonnull
    @Override
    public FluidResult drainItem(FluidResult resource, FluidAction action) {
        if (resource.isEmpty() || !fluidStack.isFluidEqual(resource)) {
            return FluidResult.EMPTY;
        }
        return drainItem(resource.getFluidAmount(), action);
    }

    @Nonnull
    @Override
    public FluidResult drainItem(int maxDrain, FluidAction action) {
        int drained = maxDrain;
        if (fluidStack.getAmount() < drained)
        {
            drained = fluidStack.getAmount();
        }
        FluidResult result = FluidResult.of(fluidStack);
        result.setFluidAmount(drained);
        if (action.execute() && drained > 0) {
            fluidStack.shrink(drained);
            onContentsChanged();
        }
        return result;
    }

    protected void onContentsChanged() {

    }

    public void setFluid(FluidStack stack) {
        this.fluidStack = stack;
    }

    public boolean isEmpty() {
        return fluidStack.isEmpty();
    }

    public int getSpace() {
        return Math.max(0, capacity - fluidStack.getAmount());
    }
}
