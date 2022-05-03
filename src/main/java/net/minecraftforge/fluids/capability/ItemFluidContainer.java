/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fluids.capability;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A simple fluid container, to replace the functionality of the old FluidContainerRegistry and IFluidContainerItem.
 * This fluid container may be set so that is can only completely filled or empty. (binary)
 * It may also be set so that it gets consumed when it is drained. (consumable)
 */
public class ItemFluidContainer extends Item
{
    protected final int capacity;

    /**
     * @param capacity   The maximum capacity of this fluid container.
     */
    public ItemFluidContainer(Item.Properties properties, int capacity)
    {
        super(properties);
        this.capacity = capacity;
    }

    @Override
    public ICapabilityProvider initCapabilities(@Nonnull ItemStack stack, @Nullable CompoundNBT nbt)
    {
        return new FluidHandlerItemStack(stack, capacity);
    }
}
