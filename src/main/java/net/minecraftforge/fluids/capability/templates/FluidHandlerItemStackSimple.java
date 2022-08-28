/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fluids.capability.templates;

import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * FluidHandlerItemStackSimple is a template capability provider for ItemStacks.
 * Data is stored directly in the vanilla NBT, in the same way as the old ItemFluidContainer.
 *
 * This implementation only allows item containers to be fully filled or emptied, similar to vanilla buckets.
 */
public class FluidHandlerItemStackSimple implements IFluidHandlerItem, ICapabilityProvider
{
    public static final String FLUID_NBT_KEY = "Fluid";

    private final LazyOptional<IFluidHandlerItem> holder = LazyOptional.of(() -> this);

    @NotNull
    protected ItemStack container;
    protected int capacity;

    /**
     * @param container  The container itemStack, data is stored on it directly as NBT.
     * @param capacity   The maximum capacity of this fluid tank.
     */
    public FluidHandlerItemStackSimple(@NotNull ItemStack container, int capacity)
    {
        this.container = container;
        this.capacity = capacity;
    }

    @NotNull
    @Override
    public ItemStack getContainer()
    {
        return container;
    }

    @NotNull
    public FluidStack getFluid()
    {
        CompoundTag tagCompound = container.getTag();
        if (tagCompound == null || !tagCompound.contains(FLUID_NBT_KEY))
        {
            return FluidStack.EMPTY;
        }
        return FluidStack.loadFluidStackFromNBT(tagCompound.getCompound(FLUID_NBT_KEY));
    }

    protected void setFluid(FluidStack fluid)
    {
        if (!container.hasTag())
        {
            container.setTag(new CompoundTag());
        }

        CompoundTag fluidTag = new CompoundTag();
        fluid.writeToNBT(fluidTag);
        container.getTag().put(FLUID_NBT_KEY, fluidTag);
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

        return capacity;
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {

        return true;
    }

    @Override
    public int fill(@NotNull FluidStack resource, FluidAction action)
    {
        if (container.getCount() != 1 || resource.isEmpty() || !canFillFluidType(resource))
        {
            return 0;
        }

        FluidStack contained = getFluid();
        if (contained.isEmpty())
        {
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

    @NotNull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action)
    {
        if (container.getCount() != 1 || resource.isEmpty() || !resource.isFluidEqual(getFluid()))
        {
            return FluidStack.EMPTY;
        }
        return drain(resource.getAmount(), action);
    }

    @NotNull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action)
    {
        if (container.getCount() != 1 || maxDrain <= 0)
        {
            return FluidStack.EMPTY;
        }

        FluidStack contained = getFluid();
        if (contained.isEmpty() || !canDrainFluidType(contained))
        {
            return FluidStack.EMPTY;
        }

        final int drainAmount = Math.min(contained.getAmount(), maxDrain);
        if (drainAmount == capacity) {
            FluidStack drained = contained.copy();

            if (action.execute()) {
                setContainerToEmpty();
            }

            return drained;
        }

        return FluidStack.EMPTY;
    }

    public boolean canFillFluidType(FluidStack fluid)
    {
        return true;
    }

    public boolean canDrainFluidType(FluidStack fluid)
    {
        return true;
    }

    /**
     * Override this method for special handling.
     * Can be used to swap out the container's item for a different one with "container.setItem".
     * Can be used to destroy the container with "container.stackSize--"
     */
    protected void setContainerToEmpty()
    {
        container.removeTagKey(FLUID_NBT_KEY);
    }

    @Override
    @NotNull
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction facing)
    {
        return ForgeCapabilities.FLUID_HANDLER_ITEM.orEmpty(capability, holder);
    }

    /**
     * Destroys the container item when it's emptied.
     */
    public static class Consumable extends FluidHandlerItemStackSimple
    {
        public Consumable(ItemStack container, int capacity)
        {
            super(container, capacity);
        }

        @Override
        protected void setContainerToEmpty()
        {
            super.setContainerToEmpty();
            container.shrink(1);
        }
    }

    /**
     * Swaps the container item for a different one when it's emptied.
     */
    public static class SwapEmpty extends FluidHandlerItemStackSimple
    {
        protected final ItemStack emptyContainer;

        public SwapEmpty(ItemStack container, ItemStack emptyContainer, int capacity)
        {
            super(container, capacity);
            this.emptyContainer = emptyContainer;
        }

        @Override
        protected void setContainerToEmpty()
        {
            super.setContainerToEmpty();
            container = emptyContainer;
        }
    }
}
