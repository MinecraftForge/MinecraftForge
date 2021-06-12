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

package net.minecraftforge.fluids.capability.wrappers;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.fluid.Fluids;
import net.minecraft.item.*;
import net.minecraft.util.Direction;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.FluidResult;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import net.minecraftforge.fluids.capability.IFluidHandlerBlock.FluidAction;

import java.util.function.Function;

/**
 * Wrapper for vanilla and forge buckets.
 * Swaps between empty bucket and filled bucket of the correct type.
 */
public class FluidBucketWrapper implements IFluidHandlerItem, ICapabilityProvider {
    private final LazyOptional<IFluidHandlerItem> holder = LazyOptional.of(() -> this);

    @Nonnull
    protected ItemStack container;
    protected Function<FluidResult, ItemStack> stackFunction;

    public FluidBucketWrapper(@Nonnull ItemStack container) {
        this.container = container;
        this.stackFunction = x -> {
            if (x.getFluidStack().isEmpty())
                return new ItemStack(Items.BUCKET);
            else
                return FluidUtil.getFilledBucket(x.getFluidStack());
        };
    }

    public boolean canFillFluidType(FluidStack fluid)
    {
        if (fluid.getFluid() == Fluids.WATER || fluid.getFluid() == Fluids.LAVA) {
            return true;
        }
        return !fluid.getFluid().getAttributes().getBucket(fluid).isEmpty();
    }

    @Nonnull
    public FluidStack getFluid() {
        Item item = container.getItem();
        if (item instanceof BucketItem)
        {
            return new FluidStack(((BucketItem)item).getFluid(), FluidAttributes.BUCKET_VOLUME);
        }
        else if (item instanceof MilkBucketItem && ForgeMod.MILK.isPresent()){
            return new FluidStack(ForgeMod.MILK.get(), FluidAttributes.BUCKET_VOLUME);
        }
        return FluidStack.EMPTY;
    }

    protected void updateItemStack(@Nonnull FluidStack fluidStack) {
        container = stackFunction.apply(FluidResult.of(fluidStack));
    }

    @Override
    public int getTanks() {

        return 1;
    }

    @Nonnull
    @Override
    public FluidStack getFluidInTank(int tank) {

        return getFluid();
    }

    @Override
    public int getTankCapacity(int tank) {

        return FluidAttributes.BUCKET_VOLUME;
    }

    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {

        return true;
    }

    @Override
    public FluidResult fillItem(FluidStack resource, FluidAction action)
    {
        if (container.getCount() != 1 || resource.getAmount() < FluidAttributes.BUCKET_VOLUME || container.getItem() instanceof MilkBucketItem || !getFluid().isEmpty() || !canFillFluidType(resource))
        {
            return FluidResult.EMPTY;
        }

        if (action.execute())
        {
            updateItemStack(resource);
        }

        return FluidResult.of(new FluidStack(getFluid(), FluidAttributes.BUCKET_VOLUME), container);
    }

    @Nonnull
    @Override
    public FluidResult drainItem(FluidStack resource, FluidAction action)
    {
        if (container.getCount() != 1 || resource.getAmount() < FluidAttributes.BUCKET_VOLUME)
        {
            return FluidResult.EMPTY;
        }

        FluidStack fluidStack = getFluid();
        if (!fluidStack.isEmpty() && fluidStack.isFluidEqual(resource)) {
            if (action.execute())
            {
                updateItemStack(FluidStack.EMPTY);
            }
            return FluidResult.of(fluidStack, container);
        }

        return FluidResult.EMPTY;
    }

    @Nonnull
    @Override
    public FluidResult drainItem(int maxDrain, FluidAction action)
    {
        if (container.getCount() != 1 || maxDrain < FluidAttributes.BUCKET_VOLUME)
        {
            return FluidResult.EMPTY;
        }

        FluidStack fluidStack = getFluid();
        if (!fluidStack.isEmpty())
        {
            if (action.execute())
            {
                updateItemStack(FluidStack.EMPTY);
            }
            return FluidResult.of(fluidStack, container);
        }

        return FluidResult.EMPTY;
    }
    
    @Override
    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing)
    {
        return CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY.orEmpty(capability, holder);
    }
}
