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

package net.minecraftforge.fluids.capability.wrappers;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.*;
import net.minecraft.util.Direction;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

/**
 * Wrapper for vanilla and forge buckets.
 * Swaps between empty bucket and filled bucket of the correct type.
 */
public class FluidBucketWrapper implements IFluidHandlerItem, ICapabilityProvider
{
    private final LazyOptional<IFluidHandlerItem> holder = LazyOptional.of(() -> this);

    @Nonnull
    protected ItemStack container;

    public FluidBucketWrapper(@Nonnull ItemStack container)
    {
        this.container = container;
    }

    @Nonnull
    @Override
    public ItemStack getContainer()
    {
        return container;
    }

    public boolean canFillFluidType(FluidStack fluid)
    {/* TODO fluids
        if (fluid.getFluid() == FluidRegistry.WATER || fluid.getFluid() == FluidRegistry.LAVA || fluid.getFluid().getName().equals("milk"))
        {
            return true;
        }
        return FluidRegistry.isUniversalBucketEnabled() && FluidRegistry.getBucketFluids().contains(fluid.getFluid());
        */ return false;
    }

    @Nullable
    public FluidStack getFluid()
    {
        Item item = container.getItem();
        if (item instanceof BucketItem)
        {
            return new FluidStack(((BucketItem)item).getFluid(), FluidAttributes.BUCKET_VOLUME);
        }
        /* TODO fluids
        else if (item == Items.MILK_BUCKET)
        {
            return FluidRegistry.getFluidStack("milk", Fluid.BUCKET_VOLUME);
        }
        else*/
        {
            return null;
        }
    }

    protected void setFluid(@Nullable FluidStack fluidStack)
    {
        if (fluidStack == null)
            container = new ItemStack(Items.BUCKET);
        else
            container = FluidUtil.getFilledBucket(fluidStack);
    }

    @Override
    public IFluidTankProperties[] getTankProperties()
    {
        return new FluidTankProperties[] { new FluidTankProperties(getFluid(), FluidAttributes.BUCKET_VOLUME) };
    }

    @Override
    public int fill(FluidStack resource, CapabilityFluidHandler.FluidAction action)
    {
        if (container.getCount() != 1 || resource == null || resource.amount < FluidAttributes.BUCKET_VOLUME || container.getItem() instanceof MilkBucketItem || getFluid() != null || !canFillFluidType(resource))
        {
            return 0;
        }

        if (action.execute())
        {
            setFluid(resource);
        }

        return FluidAttributes.BUCKET_VOLUME;
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, CapabilityFluidHandler.FluidAction action)
    {
        if (container.getCount() != 1 || resource == null || resource.amount < FluidAttributes.BUCKET_VOLUME)
        {
            return null;
        }

        FluidStack fluidStack = getFluid();
        if (fluidStack != null && fluidStack.isFluidEqual(resource))
        {
            if (action.execute())
            {
                setFluid(null);
            }
            return fluidStack;
        }

        return null;
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, CapabilityFluidHandler.FluidAction action)
    {
        if (container.getCount() != 1 || maxDrain < FluidAttributes.BUCKET_VOLUME)
        {
            return null;
        }

        FluidStack fluidStack = getFluid();
        if (fluidStack != null)
        {
            if (action.execute())
            {
                setFluid(null);
            }
            return fluidStack;
        }

        return null;
    }
    
    @Override
    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing)
    {
        return CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY.orEmpty(capability, holder);
    }
}
