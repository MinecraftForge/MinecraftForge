/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fluids.capability.wrappers;

import net.minecraft.world.level.material.Fluids;
import net.minecraft.core.Direction;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MilkBucketItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Wrapper for vanilla and forge buckets.
 * Swaps between empty bucket and filled bucket of the correct type.
 */
public class FluidBucketWrapper implements IFluidHandlerItem, ICapabilityProvider
{
    private final LazyOptional<IFluidHandlerItem> holder = LazyOptional.of(() -> this);

    @NotNull
    protected ItemStack container;

    public FluidBucketWrapper(@NotNull ItemStack container)
    {
        this.container = container;
    }

    @NotNull
    @Override
    public ItemStack getContainer()
    {
        return container;
    }

    public boolean canFillFluidType(FluidStack fluid)
    {
        if (fluid.getFluid() == Fluids.WATER || fluid.getFluid() == Fluids.LAVA)
        {
            return true;
        }
        return !fluid.getFluid().getFluidType().getBucket(fluid).isEmpty();
    }

    @NotNull
    public FluidStack getFluid()
    {
        Item item = container.getItem();
        if (item instanceof BucketItem)
        {
            return new FluidStack(((BucketItem)item).getFluid(), FluidType.BUCKET_VOLUME);
        }
        else if (item instanceof MilkBucketItem && ForgeMod.MILK.isPresent())
        {
            return new FluidStack(ForgeMod.MILK.get(), FluidType.BUCKET_VOLUME);
        }
        else
        {
            return FluidStack.EMPTY;
        }
    }

    protected void setFluid(@NotNull FluidStack fluidStack)
    {
        if (fluidStack.isEmpty())
            container = new ItemStack(Items.BUCKET);
        else
            container = FluidUtil.getFilledBucket(fluidStack);
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

        return FluidType.BUCKET_VOLUME;
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {

        return true;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action)
    {
        if (container.getCount() != 1 || resource.getAmount() < FluidType.BUCKET_VOLUME || container.getItem() instanceof MilkBucketItem || !getFluid().isEmpty() || !canFillFluidType(resource))
        {
            return 0;
        }

        if (action.execute())
        {
            setFluid(resource);
        }

        return FluidType.BUCKET_VOLUME;
    }

    @NotNull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action)
    {
        if (container.getCount() != 1 || resource.getAmount() < FluidType.BUCKET_VOLUME)
        {
            return FluidStack.EMPTY;
        }

        FluidStack fluidStack = getFluid();
        if (!fluidStack.isEmpty() && fluidStack.isFluidEqual(resource))
        {
            if (action.execute())
            {
                setFluid(FluidStack.EMPTY);
            }
            return fluidStack;
        }

        return FluidStack.EMPTY;
    }

    @NotNull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action)
    {
        if (container.getCount() != 1 || maxDrain < FluidType.BUCKET_VOLUME)
        {
            return FluidStack.EMPTY;
        }

        FluidStack fluidStack = getFluid();
        if (!fluidStack.isEmpty())
        {
            if (action.execute())
            {
                setFluid(FluidStack.EMPTY);
            }
            return fluidStack;
        }

        return FluidStack.EMPTY;
    }

    @Override
    @NotNull
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction facing)
    {
        return ForgeCapabilities.FLUID_HANDLER_ITEM.orEmpty(capability, holder);
    }
}
