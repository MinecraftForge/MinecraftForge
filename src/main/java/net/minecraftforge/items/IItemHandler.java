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

package net.minecraftforge.items;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.filter.IStackFilter;

import javax.annotation.Nonnull;
import java.util.OptionalInt;

public interface IItemHandler
{

    /**
     * @return the size of this IItemHandler
     */
    int size();

    /**
     * @param stack to check
     * @return true is the stack can be inserted
     */
    default boolean isStackValid(@Nonnull ItemStack stack)
    {
        for (int i = 0; i < size(); i++)
        {
            if (isStackValidForSlot(i, stack))
                return true;
        }
        return false;
    }

    /**
     * @param stack to check
     * @return true is the stack can be extracted
     */
    default boolean canExtractStack(@Nonnull ItemStack stack)
    {
        for (int i = 0; i < size(); i++)
        {
            if (canExtractStackFromSlot(i, stack))
                return true;
        }
        return false;
    }

    /**
     * @param stack to check
     * @param slot  Slot to query.
     * @return true is the stack can be inserted into the slot
     */
    default boolean isStackValidForSlot(int slot, @Nonnull ItemStack stack)
    {
        return true;
    }

    /**
     * @param stack to check
     * @param slot  Slot to query.
     * @return true is the stack can be extracted from the slot
     */
    default boolean canExtractStackFromSlot(int slot, @Nonnull ItemStack stack)
    {
        return true;
    }

    /**
     * @return true if the IItemHandler is full. this does take voiding into account
     */
    default boolean isFull()
    {
        for (int i = 0; i < size(); i++)
        {
            if (getFreeSpaceForSlot(i) != 0)
                return false;
        }
        return true;
    }

    /**
     * @return true if the IItemHandler is empty
     */
    default boolean isEmpty()
    {
        for (int i = 0; i < size(); i++)
        {
            if (!getStackInSlot(i).isEmpty())
                return false;
        }
        return true;
    }

    /**
     * @param slot Slot to query.
     * @return the free space available in the giving slot
     */
    default int getFreeSpaceForSlot(int slot)
    {
        ItemStack existing = getStackInSlot(slot);
        if (!existing.isEmpty())
        {
            if (!existing.isStackable())
            {
                return 0;
            }
            else return getStackLimit(existing, slot) - existing.getCount();
        }
        return getSlotLimit(slot);
    }

    /**
     * this will set all slots to {@link ItemStack#EMPTY}
     */
    void clearInv();

    /**
     * Retrieves the maximum stack size allowed to exist in the given slot.
     *
     * @param slot Slot to query.
     * @return The maximum stack size allowed in the slot.
     */
    int getSlotLimit(int slot);


    /**
     * @param stack the stack to get the maxStackSize for the queried slot from can be more {@link ItemStack#getMaxStackSize()}
     * @param slot  Slot to query.
     * @return The maximum stack size allowed in the slot for the giving stack.
     */
    default int getStackLimit(@Nonnull ItemStack stack, int slot)
    {
        return Math.min(stack.getMaxStackSize(), getSlotLimit(slot));
    }

    /**
     * Returns the ItemStack in a given slot.
     * <p>
     * The result's stack size may be greater than the itemStacks max size.
     * <p>
     * If the result is empty, then the slot is empty.
     * <p>
     * SERIOUSLY: DO NOT MODIFY THE RETURNED ITEMSTACK
     *
     * @param slot Slot to query
     * @return ItemStack in given slot. Empty Itemstack if the slot is empty.
     */
    @Nonnull
    ItemStack getStackInSlot(int slot);

    /**
     * @param slot     use {@link OptionalInt#empty()} for slotLess insertion, use {@link OptionalInt#of(int)} for slotted insertion
     * @param stack    the stack to insert
     * @param simulate If true, the insertion is only simulated
     * @return The remaining ItemStack that was not inserted (if the entire stack is accepted, then return ItemStack.EMPTY).
     * May be the same as the input ItemStack if unchanged, otherwise a new ItemStack.
     * The returned ItemStack can be safely modified after.
     */
    @Nonnull
    ItemStack insert(OptionalInt slot, @Nonnull ItemStack stack, boolean simulate);

    /**
     * @param slot     use {@link OptionalInt#empty()} for slotLess extraction, use {@link OptionalInt#of(int)} for slotted extraction
     * @param filter   the filter to test the the stack in in the current slot extraction
     * @param amount   the amount to extract
     * @param simulate If true, the extraction is only simulated
     * @return ItemStack extracted from the slot, must be empty if nothing can be extracted.
     * The returned ItemStack can be safely modified after, so item handlers should return a new or copied stack.
     */
    @Nonnull
    ItemStack extract(OptionalInt slot, IStackFilter filter, int amount, boolean simulate);
}
