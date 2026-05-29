/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.fluids.capability.wrappers;

import net.minecraft.world.level.material.Fluids;
import net.minecraft.core.Direction;
import net.minecraftsingularity.common.singularityMod;
import net.minecraftsingularity.common.capabilities.Capability;
import net.minecraftsingularity.common.capabilities.singularityCapabilities;
import net.minecraftsingularity.common.capabilities.ICapabilityProvider;
import net.minecraftsingularity.common.util.LazyOptional;
import net.minecraftsingularity.fluids.FluidStack;
import net.minecraftsingularity.fluids.FluidType;
import net.minecraftsingularity.fluids.FluidUtil;
import net.minecraftsingularity.fluids.capability.IFluidHandlerItem;

import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Wrapper for vanilla and singularity buckets.
 * Swaps between empty bucket and filled bucket of the correct type.
 */
public class FluidBucketWrapper implements IFluidHandlerItem, ICapabilityProvider {
    private final LazyOptional<IFluidHandlerItem> holder = LazyOptional.of(() -> this);

    @NotNull
    protected ItemStack container;

    public FluidBucketWrapper(@NotNull ItemStack container) {
        this.container = container;
    }

    @NotNull
    @Override
    public ItemStack getContainer() {
        return container;
    }

    public boolean canFillFluidType(FluidStack fluid) {
        if (fluid.getFluid() == Fluids.WATER || fluid.getFluid() == Fluids.LAVA)
            return true;
        return !fluid.getFluid().getFluidType().getBucket(fluid).isEmpty();
    }

    @NotNull
    public FluidStack getFluid() {
        Item item = container.getItem();
        if (item instanceof BucketItem bucket)
            return new FluidStack(bucket.getFluid(), FluidType.BUCKET_VOLUME);
        else if (container.is(Items.MILK_BUCKET) && singularityMod.MILK.isPresent())
            return new FluidStack(singularityMod.MILK.get(), FluidType.BUCKET_VOLUME);
        else
            return FluidStack.EMPTY;
    }

    protected void setFluid(@NotNull FluidStack fluidStack) {
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
    public int fill(FluidStack resource, FluidAction action) {
        if (container.getCount() != 1 || resource.getAmount() < FluidType.BUCKET_VOLUME || container.is(Items.MILK_BUCKET) || !getFluid().isEmpty() || !canFillFluidType(resource))
            return 0;

        if (action.execute())
            setFluid(resource);

        return FluidType.BUCKET_VOLUME;
    }

    @NotNull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        if (container.getCount() != 1 || resource.getAmount() < FluidType.BUCKET_VOLUME)
            return FluidStack.EMPTY;

        FluidStack fluidStack = getFluid();
        if (!fluidStack.isEmpty() && fluidStack.isFluidEqual(resource)) {
            if (action.execute())
                setFluid(FluidStack.EMPTY);
            return fluidStack;
        }

        return FluidStack.EMPTY;
    }

    @NotNull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        if (container.getCount() != 1 || maxDrain < FluidType.BUCKET_VOLUME)
            return FluidStack.EMPTY;

        FluidStack fluidStack = getFluid();
        if (!fluidStack.isEmpty()) {
            if (action.execute())
                setFluid(FluidStack.EMPTY);
            return fluidStack;
        }

        return FluidStack.EMPTY;
    }

    @Override
    @NotNull
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction facing) {
        return singularityCapabilities.FLUID_HANDLER_ITEM.orEmpty(capability, holder);
    }
}
