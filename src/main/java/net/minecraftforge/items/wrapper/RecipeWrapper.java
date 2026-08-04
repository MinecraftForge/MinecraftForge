/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.items.wrapper;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

public class RecipeWrapper implements IInventory {

    protected final IItemHandlerModifiable inv;

    public RecipeWrapper(IItemHandlerModifiable inv)
    {
        this.inv = inv;
    }

    /**
     * Returns the size of this inventory.  Must be equivalent to {@link #getHeight()} * {@link #getWidth()}.
     */
    @Override
    public int getSizeInventory()
    {
        return inv.getSlots();
    }

    /**
     * Returns the stack in this slot.  This stack should be a modifiable reference, not a copy of a stack in your inventory.
     */
    @Override
    public ItemStack getStackInSlot(int slot)
    {
        return inv.getStackInSlot(slot);
    }

    /**
     * Attempts to remove n items from the specified slot.  Returns the split stack that was removed.  Modifies the inventory.
     */
    @Override
    public ItemStack decrStackSize(int slot, int count)
    {
        ItemStack stack = inv.getStackInSlot(slot);
        return stack.isEmpty() ? ItemStack.EMPTY : stack.split(count);
    }

    /**
     * Sets the contents of this slot to the provided stack.
     */
    @Override
    public void setInventorySlotContents(int slot, ItemStack stack)
    {
        inv.setStackInSlot(slot, stack);
    }

    /**
     * Removes the stack contained in this slot from the underlying handler, and returns it.
     */
    @Override
    public ItemStack removeStackFromSlot(int index)
    {
        ItemStack s = getStackInSlot(index);
        if(s.isEmpty()) return ItemStack.EMPTY;
        setInventorySlotContents(index, ItemStack.EMPTY);
        return s;
    }

    @Override
    public boolean isEmpty()
    {
        for(int i = 0; i < inv.getSlots(); i++)
        {
            if(!inv.getStackInSlot(i).isEmpty()) return false;
        }
        return true;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack)
    {
        return inv.isItemValid(slot, stack);
    }

    @Override
    public void clear() 
    {
        for(int i = 0; i < inv.getSlots(); i++)
        {
            inv.setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    //The following methods are never used by vanilla in crafting.  They are defunct as mods need not override them.
    @Override
    public int getInventoryStackLimit() { return 0; }
    @Override
    public void markDirty() {}
    @Override
    public boolean isUsableByPlayer(PlayerEntity player) { return false; }
    @Override
    public void openInventory(PlayerEntity player) {}
    @Override
    public void closeInventory(PlayerEntity player) {}

}
