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
import net.minecraftforge.fluids.capability.FluidResult;
import net.minecraftforge.fluids.capability.IFluidHandlerBlock;
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
    protected ItemStack itemStack;

    public FluidTankItem(ItemStack item, int capacity) {
        this(item, capacity, e -> true, FluidResult::getItemStack);
    }

    public FluidTankItem(ItemStack item, int capacity, Predicate<FluidStack> validator) {
        this(item, capacity, validator, FluidResult::getItemStack);
    }

    public FluidTankItem(ItemStack item, int capacity, Predicate<FluidStack> validator, Function<FluidResult, ItemStack> stackCheck) {
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
        itemStack.deserializeNBT((CompoundNBT) nbt.get("ItemStack"));
        capacity = nbt.getInt("Capacity");
        return this;
    }

    public CompoundNBT writeToNBT(CompoundNBT nbt) {
        fluidStack.writeToNBT(nbt);
        nbt.put("ItemStack", itemStack.serializeNBT());
        nbt.putInt("Capacity", capacity);
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
    public FluidResult fillItem(FluidStack resource, IFluidHandlerBlock.FluidAction action) {
        if (resource.isEmpty() || !isFluidValid(resource)) {
            return FluidResult.EMPTY;
        }
        if (action.simulate()) {
            FluidStack returned = resource.copy();
            if (resource.isEmpty()) {
                returned.setAmount(Math.min(capacity, resource.getAmount()));
                itemStack = stackFunction.apply(FluidResult.of(returned, itemStack));
                return FluidResult.of(returned, itemStack);
            }
            if (!resource.isFluidEqual(fluidStack)) {
                return FluidResult.EMPTY;
            }
            returned.setAmount(Math.min(capacity - fluidStack.getAmount(), resource.getAmount()));
            itemStack = stackFunction.apply(FluidResult.of(returned, itemStack));
            return FluidResult.of(returned, itemStack);
        }

        if (fluidStack.isEmpty()) {
            resource.setAmount(Math.min(capacity, resource.getAmount()));
            itemStack = stackFunction.apply(FluidResult.of(resource, itemStack));
            onContentsChanged();
            return FluidResult.of(resource, itemStack);
        }
        if (!resource.isFluidEqual(fluidStack)) {
            return FluidResult.EMPTY;
        }

        FluidStack returned = new FluidStack(resource.getFluid(), capacity - fluidStack.getAmount());

        if (resource.getAmount() < returned.getAmount()) {
            fluidStack.grow(resource.getAmount());
            returned = resource.copy();
        }
        else {
            fluidStack.setAmount(capacity);
        }
        if (returned.getAmount() > 0) {
            onContentsChanged();
        }
        itemStack = stackFunction.apply(FluidResult.of(resource, itemStack));
        return FluidResult.of(returned, itemStack);
    }

    @Nonnull
    @Override
    public FluidResult drainItem(FluidStack resource, IFluidHandlerBlock.FluidAction action) {
        if (resource.isEmpty() || resource.isFluidEqual(fluidStack)) {
            return FluidResult.EMPTY;
        }
        return drainItem(resource, action);
    }

    @Nonnull
    @Override
    public FluidResult drainItem(int maxDrain, IFluidHandlerBlock.FluidAction action) {
        int drained = maxDrain;
        if (fluidStack.getAmount() < drained)
        {
            drained = fluidStack.getAmount();
        }
        FluidStack stack = fluidStack.copy();
        fluidStack.setAmount(drained);
        stack.setAmount(drained);
        if (action.execute() && drained > 0) {
            fluidStack.shrink(drained);
            onContentsChanged();
        }
        itemStack = stackFunction.apply(FluidResult.of(stack, itemStack));
        return FluidResult.of(stack, itemStack);
    }

    protected void onContentsChanged() { }

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
