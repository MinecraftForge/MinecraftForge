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

import net.minecraft.item.Items;
import net.minecraft.item.Item;
import net.minecraft.item.MilkBucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
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
        Item item = container.getItem();/* TODO fluids
        if (item == Items.WATER_BUCKET)
        {
            return new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME);
        }
        else if (item == Items.LAVA_BUCKET)
        {
            return new FluidStack(FluidRegistry.LAVA, Fluid.BUCKET_VOLUME);
        }
        else if (item == Items.MILK_BUCKET)
        {
            return FluidRegistry.getFluidStack("milk", Fluid.BUCKET_VOLUME);
        }
        else*/ if (item == ForgeMod.getInstance().universalBucket)
        {
            return ForgeMod.getInstance().universalBucket.getFluid(container);
        }
        else
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
        return new FluidTankProperties[] { new FluidTankProperties(getFluid(), Fluid.BUCKET_VOLUME) };
    }

    @Override
    public int fill(FluidStack resource, boolean doFill)
    {
        if (container.getCount() != 1 || resource == null || resource.amount < Fluid.BUCKET_VOLUME || container.getItem() instanceof MilkBucketItem || getFluid() != null || !canFillFluidType(resource))
        {
            return 0;
        }

        if (doFill)
        {
            setFluid(resource);
        }

        return Fluid.BUCKET_VOLUME;
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain)
    {
        if (container.getCount() != 1 || resource == null || resource.amount < Fluid.BUCKET_VOLUME)
        {
            return null;
        }

        FluidStack fluidStack = getFluid();
        if (fluidStack != null && fluidStack.isFluidEqual(resource))
        {
            if (doDrain)
            {
                setFluid((FluidStack) null);
            }
            return fluidStack;
        }

        return null;
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain)
    {
        if (container.getCount() != 1 || maxDrain < Fluid.BUCKET_VOLUME)
        {
            return null;
        }

        FluidStack fluidStack = getFluid();
        if (fluidStack != null)
        {
            if (doDrain)
            {
                setFluid((FluidStack) null);
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
