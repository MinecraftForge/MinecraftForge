/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fluids.capability.templates;

import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import org.jetbrains.annotations.Nullable;

/**
 * FluidHandlerItemStackSimple is a template capability provider for ItemStacks.
 * Data is stored using the {@linkplain ForgeMod#FLUID_STACK_DATA fluidstack data component}.
 * <p>
 * This implementation only allows item containers to be fully filled or emptied, similar to vanilla buckets.
 */
public class FluidHandlerItemStackSimple implements IFluidHandlerItem, ICapabilityProvider {
    private final LazyOptional<IFluidHandlerItem> holder = LazyOptional.of(() -> this);

    protected ItemStack container;
    protected int capacity;

    /**
     * @param container  The container itemStack, data is stored on it directly as NBT.
     * @param capacity   The maximum capacity of this fluid tank.
     */
    public FluidHandlerItemStackSimple(ItemStack container, int capacity) {
        this.container = container;
        this.capacity = capacity;
    }

    @Override
    public ItemStack getContainer() {
        return container;
    }

    public FluidStack getFluid() {
        return container.getOrDefault(ForgeMod.FLUID_STACK_DATA.get(), FluidStack.EMPTY);
    }

    protected void setFluid(FluidStack fluid) {
        container.set(ForgeMod.FLUID_STACK_DATA.get(), fluid);
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        return getFluid();
    }

    @Override
    public int getTankCapacity(int tank) {
        return capacity;
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return true;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (container.getCount() != 1 || resource.isEmpty() || !canFillFluidType(resource))
            return 0;

        FluidStack contained = getFluid();
        if (contained.isEmpty()) {
            int fillAmount = Math.min(capacity, resource.getAmount());
            if (fillAmount == capacity) {
                if (action.execute()) {
                    FluidStack filled = resource.copy();
                    filled.setAmount(fillAmount);
                    setFluid(filled);
                }

                return fillAmount;
            }
        }

        return 0;
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        if (container.getCount() != 1 || resource.isEmpty() || !resource.isFluidEqual(getFluid()))
            return FluidStack.EMPTY;

        return drain(resource.getAmount(), action);
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        if (container.getCount() != 1 || maxDrain <= 0)
            return FluidStack.EMPTY;

        FluidStack contained = getFluid();
        if (contained.isEmpty() || !canDrainFluidType(contained))
            return FluidStack.EMPTY;

        final int drainAmount = Math.min(contained.getAmount(), maxDrain);
        if (drainAmount == capacity) {
            FluidStack drained = contained.copy();

            if (action.execute())
                setContainerToEmpty();

            return drained;
        }

        return FluidStack.EMPTY;
    }

    public boolean canFillFluidType(FluidStack fluid) {
        return true;
    }

    public boolean canDrainFluidType(FluidStack fluid) {
        return true;
    }

    /**
     * Override this method for special handling.
     * <p>
     * Can be used to swap out the container's item for a different one with "container.setItem".
     * <p>
     * Can be used to destroy the container with "container.stackSize--"
     */
    protected void setContainerToEmpty() {
        container.remove(ForgeMod.FLUID_STACK_DATA.get());
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
        return ForgeCapabilities.FLUID_HANDLER_ITEM.orEmpty(capability, holder);
    }

    /**
     * Destroys the container item when it's emptied.
     */
    public static class Consumable extends FluidHandlerItemStackSimple {
        public Consumable(ItemStack container, int capacity) {
            super(container, capacity);
        }

        @Override
        protected void setContainerToEmpty() {
            super.setContainerToEmpty();
            container.shrink(1);
        }
    }

    /**
     * Swaps the container item for a different one when it's emptied.
     */
    public static class SwapEmpty extends FluidHandlerItemStackSimple {
        protected final ItemStack emptyContainer;

        public SwapEmpty(ItemStack container, ItemStack emptyContainer, int capacity) {
            super(container, capacity);
            this.emptyContainer = emptyContainer;
        }

        @Override
        protected void setContainerToEmpty() {
            super.setContainerToEmpty();
            container = emptyContainer;
        }
    }
}