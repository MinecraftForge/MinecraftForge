/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.items.wrapper;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;

public class EmptyHandler implements IItemHandlerModifiable
{
    public static final IItemHandler INSTANCE = new EmptyHandler();

    @Override
    public int getSlots()
    {
        return 0;
    }

    @Override
    @Nonnull
    public ItemStack getStackInSlot(int slot)
    {
        return ItemStack.EMPTY;
    }

    @Override
    @Nonnull
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
    {
        return stack;
    }

    @Override
    @Nonnull
    public ItemStack extractItem(int slot, int amount, boolean simulate)
    {
        return ItemStack.EMPTY;
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack)
    {
        // nothing to do here
    }

    @Override
    public int getSlotLimit(int slot)
    {
        return 0;
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack)
    {
        return false;
    }
}
