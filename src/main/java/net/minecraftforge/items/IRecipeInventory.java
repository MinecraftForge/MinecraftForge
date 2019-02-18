/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

package net.minecraftforge.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

/**
 * This is an extension of IInventory that can be used for matching with recipes.  This defaults all the methods that are not used by crafting recipes.
 * Mods should take into account what methods are defaulted here when calling IInventory methods during recipe matching or crafting.
 * See 
 */
public interface IRecipeInventory extends IInventory
{

    /**
     * Returns the size of this inventory.  Must be equivalent to {@link #getHeight()} * {@link #getWidth()}.
     */
    default int getSizeInventory()
    {
        return getHeight() * getWidth();
    }

    /**
     * Returns the stack in this slot.  This stack should be a modifiable reference, not a copy of a stack in your inventory.
     */
    ItemStack getStack(int index);

    /**
     * Attempts to remove n items from the specified slot.  Returns the split stack that was removed.  Modifies the inventory.
     */
    default ItemStack decrStackSize(int index, int count)
    {
        return getStackInSlot(index).isEmpty() ? ItemStack.EMPTY : getStackInSlot(index).split(count);
    }

    /**
     * Sets the contents of this slot to the provided stack.
     */
    void setInventorySlotContents(int index, ItemStack stack);

    /**
     * Returns the height of this inventory.
     */
    int getHeight();

    /**
     * Returns the width of this inventory.
     */
    int getWidth();

    /**
     * Removes the stack contained in this slot from the underlying handler, and returns it.
     */
    default ItemStack removeStackFromSlot(int index)
    {
        ItemStack s = getStackInSlot(index);
        if(s.isEmpty()) return ItemStack.EMPTY;
        setInventorySlotContents(index, ItemStack.EMPTY);
        return s;
    }

    //The following methods are never used by vanilla in crafting.  They are defaulted and defunct as mods need not override them.
    default boolean isEmpty() { return false; }
    default int getInventoryStackLimit() { return 0; }
    default void markDirty() {}
    default boolean isUsableByPlayer(EntityPlayer player) { return false; }
    default void openInventory(EntityPlayer player) {}
    default void closeInventory(EntityPlayer player) {}
    default boolean isItemValidForSlot(int index, ItemStack stack) { return false; }
    default int getField(int id) { return 0; }
    default void setField(int id, int value) {}
    default int getFieldCount() { return 0; }
    default void clear() {}
    default ITextComponent getName() { return null; }
    default boolean hasCustomName() { return false; }
    default ITextComponent getCustomName() { return null; }
 
}
