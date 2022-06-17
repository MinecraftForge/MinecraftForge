/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.items.wrapper;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;

public class EmptyHandler implements IItemHandlerModifiable
{
    public static final IItemHandler INSTANCE = new EmptyHandler();

    @Override
    public int getSlots()
    {
        return 0;
    }

    @Override
    @NotNull
    public ItemStack getStackInSlot(int slot)
    {
        return ItemStack.EMPTY;
    }

    @Override
    @NotNull
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate)
    {
        return stack;
    }

    @Override
    @NotNull
    public ItemStack extractItem(int slot, int amount, boolean simulate)
    {
        return ItemStack.EMPTY;
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack)
    {
        // nothing to do here
    }

    @Override
    public int getSlotLimit(int slot)
    {
        return 0;
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack)
    {
        return false;
    }
}
