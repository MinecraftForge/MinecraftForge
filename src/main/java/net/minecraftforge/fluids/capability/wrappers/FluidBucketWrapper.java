/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

import javax.annotation.Nullable;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

/**
 * Wrapper for vanilla and forge buckets.
 * Swaps between empty bucket and filled bucket of the correct type.
 */
public class FluidBucketWrapper implements IFluidHandler, ICapabilityProvider
{
    protected final ItemStack container;

    public FluidBucketWrapper(ItemStack container)
    {
        this.container = container;
    }

    public boolean canFillFluidType(FluidStack fluid)
    {
        if (fluid.getFluid() == FluidRegistry.WATER || fluid.getFluid() == FluidRegistry.LAVA || fluid.getFluid().getName().equals("milk"))
        {
            return true;
        }
        return FluidRegistry.isUniversalBucketEnabled() && FluidRegistry.getBucketFluids().contains(fluid.getFluid());
    }

    @Nullable
    public FluidStack getFluid()
    {
        Item item = container.getItem();
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
        else if (item == ForgeModContainer.getInstance().universalBucket)
        {
            return ForgeModContainer.getInstance().universalBucket.getFluid(container);
        }
        else
        {
            return null;
        }
    }

    protected void setFluid(Fluid fluid) {
        if (fluid == null)
        {
            container.deserializeNBT(new ItemStack(Items.BUCKET).serializeNBT());
        }
        else if (fluid == FluidRegistry.WATER)
        {
            container.deserializeNBT(new ItemStack(Items.WATER_BUCKET).serializeNBT());
        }
        else if (fluid == FluidRegistry.LAVA)
        {
            container.deserializeNBT(new ItemStack(Items.LAVA_BUCKET).serializeNBT());
        }
        else if (fluid.getName().equals("milk"))
        {
            container.deserializeNBT(new ItemStack(Items.MILK_BUCKET).serializeNBT());
        }
        else if (FluidRegistry.isUniversalBucketEnabled() && FluidRegistry.getBucketFluids().contains(fluid))
        {
            ItemStack filledBucket = UniversalBucket.getFilledBucket(ForgeModContainer.getInstance().universalBucket, fluid);
            container.deserializeNBT(filledBucket.serializeNBT());
        }
    }

    @Override
    public IFluidTankProperties[] getTankProperties()
    {
        return new FluidTankProperties[] { new FluidTankProperties(getFluid(), Fluid.BUCKET_VOLUME) };
    }

    @Override
    public int fill(FluidStack resource, boolean doFill)
    {
        if (container.stackSize != 1 || resource == null || resource.amount < Fluid.BUCKET_VOLUME || container.getItem() instanceof ItemBucketMilk || getFluid() != null || !canFillFluidType(resource))
        {
            return 0;
        }

        if (doFill)
        {
            setFluid(resource.getFluid());
        }

        return Fluid.BUCKET_VOLUME;
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain)
    {
        if (container.stackSize != 1 || resource == null || resource.amount < Fluid.BUCKET_VOLUME)
        {
            return null;
        }

        FluidStack fluidStack = getFluid();
        if (fluidStack != null && fluidStack.isFluidEqual(resource))
        {
            if (doDrain)
            {
                setFluid(null);
            }
            return fluidStack;
        }

        return null;
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain)
    {
        if (container.stackSize != 1 || maxDrain < Fluid.BUCKET_VOLUME)
        {
            return null;
        }

        FluidStack fluidStack = getFluid();
        if (fluidStack != null)
        {
            if (doDrain)
            {
                setFluid(null);
            }
            return fluidStack;
        }

        return null;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing)
    {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing)
    {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
        {
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(this);
        }
        return null;
    }
}
