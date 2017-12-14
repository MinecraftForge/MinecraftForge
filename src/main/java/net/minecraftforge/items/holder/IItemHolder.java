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

package net.minecraftforge.items.holder;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public interface IItemHolder
{

    default boolean isStackValidForSlot(@Nonnull ItemStack stack, int slot)
    {
        return true;
    }

    default boolean canExtractStackFromSlot(@Nonnull ItemStack stack, int slot)
    {
        return true;
    }

    /**
     * Gets the amount of slots in this inventory, can be more then {@link ItemStack#getMaxStackSize()}.
     */
    int getSlotCount();

    /**
     * Gets the stack in a specific slot.
     */
    @Nonnull
    ItemStack getStack(int slot);

    /**
     * Tries to set the stack in a specific slot and returns whether it was successful.
     */
    boolean putStack(int slot, ItemStack stack, boolean simulate);

    /**
     * Sets the stack in the specified slot without performing any tests.
     */
    void setStack(int slot, ItemStack stack);

    /**
     * Removes the stack in the specified slot and returns it.
     */
    @Nonnull
    default ItemStack removeStack(int slot)
    {
        ItemStack stack = getStack(slot);
        return putStack(slot, ItemStack.EMPTY, false) ? stack : ItemStack.EMPTY;
    }

    void clear();

    /**
     * Decreases the stack in the slot by the specified amount.
     */
    @Nonnull
    default ItemStack decreaseStack(int slot, int amount, boolean simulate)
    {
        ItemStack newStack = getStack(slot).copy();
        ItemStack split = newStack.splitStack(amount);
        return putStack(slot, newStack, simulate) ? split : ItemStack.EMPTY;
    }

    /**
     * Gets the maximum stack size for a specific slot.
     */
    default int getStackSizeLimit(int slot)
    {
        ItemStack stack = getStack(slot);
        return stack.isEmpty() ? 64 : stack.getMaxStackSize();
    }

    void onContentChanged(int slot);
}
