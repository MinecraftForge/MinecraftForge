/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

import com.google.common.base.Preconditions;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;

// TODO: replace all the boolean parameters by FluidAction (renamed to also fit items and energy)
public interface IItemHandler
{
    /**
     * Returns the number of slots available
     *
     * @return The number of slots available
     **/
    int getSlots();

    /**
     * Returns the ItemStack in a given slot.
     *
     * The result's stack size may be greater than the itemstack's max size.
     *
     * If the result is empty, then the slot is empty.
     *
     * <p>
     * <strong>IMPORTANT:</strong> This ItemStack <em>MUST NOT</em> be modified. This method is not for
     * altering an inventory's contents. Any implementers who are able to detect
     * modification through this method should throw an exception.
     * </p>
     * <p>
     * <strong><em>SERIOUSLY: DO NOT MODIFY THE RETURNED ITEMSTACK</em></strong>
     * </p>
     *
     * @param slot Slot to query
     * @return ItemStack in given slot. Empty Itemstack if the slot is empty.
     **/
    @Nonnull
    ItemStack getStackInSlot(int slot);

    /**
     * <p>
     * Inserts an ItemStack into the given slot and return the remainder.
     * The given stack's size may be greater than the itemstack's max size.
     * The ItemStack <em>should not</em> be modified in this function!
     * </p>
     * Note: This function returns the REMAINDER - what was NOT inserted!
     * This behaviour is subtly different from {@link IFluidHandler#fill(FluidStack, IFluidHandler.FluidAction)}, which returns what was inserted.
     *
     * @param slot     Slot to insert into.
     * @param stack    ItemStack to insert. This must not be modified by the item handler.
     * @param simulate If true, the insertion is only simulated
     * @return The remaining ItemStack that was not inserted (if the entire stack is accepted, then return an empty ItemStack).
     *         May be the same as the input ItemStack if unchanged, otherwise a new ItemStack.
     *         The returned ItemStack can be safely modified after.
     **/
    @Nonnull
    ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate);

    /**
     * Tries to insert as many of the given item as possible into this item handler, spreading it across slots as needed.
     * This should be preferred over {@link #insertItem} if the caller doesn't care about the slot the item goes into,
     * as it gives the handler the opportunity to optimize this operation.
     * The ItemStack <em>should not</em> be modified in this function!
     *
     * @param stack    The stack that should be inserted. {@link ItemStack#EMPTY} is not valid.
     *                 This must not be modified by the item handler.
     *                 This stack does not need to adhere to {@link ItemStack#getMaxStackSize()}.
     * @param simulate If true, the insertion is only simulated
     * @return The remaining ItemStack that was not inserted (if the entire stack is accepted, then return an empty ItemStack).
     *         May be the same as the input ItemStack if unchanged, otherwise a new ItemStack.
     *         The returned ItemStack can be safely modified after.
     */
    @Nonnull
    default ItemStack bulkInsertItem(@Nonnull ItemStack stack, boolean simulate) {
        Preconditions.checkArgument(!stack.isEmpty(), "stack may not be empty");

        for (int i = 0; i < getSlots(); i++)
        {
            stack = insertItem(i, stack, simulate);
            if (stack.isEmpty())
            {
                return ItemStack.EMPTY;
            }
        }

        return stack;
    }

    /**
     * Extracts an ItemStack from the given slot.
     * If there is more than the max stack size in the current slot and more than the max stack size is requested,
     * then more than the max stack size may be returned.
     * <p>
     * The returned value must be empty if nothing is extracted,
     * otherwise its stack size must be less than or equal to {@code amount} and {@link ItemStack#getMaxStackSize()}.
     * </p>
     *
     * @param slot     Slot to extract from.
     * @param amount   Amount to extract (may be greater than the current stack's max limit)
     * @param simulate If true, the extraction is only simulated
     * @return ItemStack extracted from the slot, must be empty if nothing can be extracted.
     *         The returned ItemStack can be safely modified after, so item handlers should return a new or copied stack.
     **/
    @Nonnull
    ItemStack extractItem(int slot, int amount, boolean simulate);

    /**
     * Extracts an ItemStack from the given slot, but limiting the amount extracted to {@link ItemStack#getMaxStackSize()}.
     * @see #extractItem
     */
    @Nonnull
    default ItemStack limitedExtractItem(int slot, int amount, boolean simulate) {
        int limitedAmount = Math.min(amount, getStackInSlot(slot).getMaxStackSize());
        return extractItem(slot, limitedAmount, simulate);
    }

    /**
     * Tries to extract as many items that can be stacked with the given template item as requested from this
     * item handler. Will extract regardless of slot and does not respect {@link ItemStack#getMaxStackSize()}.
     *
     * @param template The template item for the items that should be extracted.
     *                 Only items matching this templates item, tags and caps will be extracted.
     *                 {@link ItemStack#EMPTY} is not a valid template.
     *                 The handler must not modify this template.
     * @param amount   The maximum amount to extract. Must be 0 or greater.
     * @param simulate If true, the extraction is only simulated
     * @return The stack of items that could be extracted. This stack does not adhere to {@link ItemStack#getMaxStackSize()}
     *         if the input amount doesn't.
     */
    @Nonnull
    default ItemStack bulkExtractItem(ItemStack template, int amount, boolean simulate) {
        Preconditions.checkArgument(!template.isEmpty(), "template may not be empty");
        if (amount < 0) throw new IllegalArgumentException("amount may not be negative: " + amount);

        int extracted = 0;
        for (int i = 0; i < getSlots(); i++)
        {
            if (ItemHandlerHelper.canItemStacksStack(getStackInSlot(i), template))
            {
                extracted += extractItem(i, amount - extracted, simulate).getCount();
                if (extracted >= amount) break;
            }
        }

        return ItemHandlerHelper.copyStackWithSize(template, extracted);
    }

    /**
     * Retrieves the maximum stack size allowed to exist in the given slot.
     *
     * @param slot Slot to query.
     * @return     The maximum stack size allowed in the slot.
     */
    int getSlotLimit(int slot);

    /**
     * <p>
     * This function re-implements the vanilla function {@link Container#canPlaceItem(int, ItemStack)}.
     * It should be used instead of simulated insertions in cases where the contents and state of the inventory are
     * irrelevant, mainly for the purpose of automation and logic (for instance, testing if a minecart can wait
     * to deposit its items into a full inventory, or if the items in the minecart can never be placed into the
     * inventory and should move on).
     * </p>
     * <ul>
     * <li>isItemValid is false when insertion of the item is never valid.</li>
     * <li>When isItemValid is true, no assumptions can be made and insertion must be simulated case-by-case.</li>
     * <li>The actual items in the inventory, its fullness, or any other state are <strong>not</strong> considered by isItemValid.</li>
     * </ul>
     * @param slot    Slot to query for validity
     * @param stack   Stack to test with for validity
     *
     * @return true if the slot can insert the ItemStack, not considering the current state of the inventory.
     *         false if the slot can never insert the ItemStack in any situation.
     */
    boolean isItemValid(int slot, @Nonnull ItemStack stack);
}
