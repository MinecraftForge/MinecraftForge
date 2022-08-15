/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.items;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

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
    @NotNull
    ItemStack getStackInSlot(int slot);

    /**
     * <p>
     * Inserts an ItemStack into the given slot and return the remainder.
     * The ItemStack <em>should not</em> be modified in this function!
     * Note: This behaviour is subtly different from {@link IFluidHandler#fill(FluidStack, IFluidHandler.FluidAction)}.
     * </p>
     * <p>
     * The ItemHandler must have the three following properties.
     * First, the simulation mode must not mutate the inventory.
     * Second, the simulation mode must be accurate, i.e insertItem(slot, stack, true)
     * must be equivalent to insertItem(slot, stack, false).
     * Third, real insertion must be idempotent, i.e insertItem(slot, insertItem(slot, stack, false), false)
     * must be equivalent to insertItem(slot, stack, false).
     * </p>
     *
     * @param slot     Slot to insert into.
     * @param stack    ItemStack to insert. This must not be modified by the item handler.
     * @param simulate If true, the insertion is only simulated
     * @return The remaining ItemStack that was not inserted (if the entire stack is accepted, then return an empty ItemStack).
     *         May be the same as the input ItemStack if unchanged, otherwise a new ItemStack.
     *         The returned ItemStack can be safely modified after.
     **/
    @NotNull
    ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate);

    /**
     * <p>
     * Inserts a list of itemstacks into the inventory and return the remainder.
     * The list of itemstacks and its contents <em>should not</em> be modified in this function!
     * The distribution of the items is left to the ItemHandler.
     * </p>
     * <p>
     * Even though a default implementation is provided,
     * if your inventory behaves in "nonstandard" ways, you may have to override it.
     * </p>
     * <p>
     * Simulation mode must be accurate, and real insert must be idempotent.
     * See {@link IItemHandler#insertItem(int, ItemStack, boolean)} for more details.
     * </p>
     *
     * @param stacks   List of items to insert. Contents must not be null.
     * @param simulate If true, the insertion is only simulated
     * @return An array with the same length as the input list representing the remaining itemstacks.
     *         Each element of the array has the same properties as the return value of
     *         {@link IItemHandler#insertItem(int, ItemStack, boolean)}
     *         (non-null, could be the same as its corresponding element in input list, etc.).
     */
    @NotNull
    default ItemStack[] insertItems(@NotNull List<ItemStack> stacks, boolean simulate)
    {
        var remaining = new ItemStack[stacks.size()];
        for (int i = 0; i < stacks.size(); i++)
        {
            remaining[i] = stacks.get(i);
            for (int j = 0; j < getSlots(); j++)
            {
                if (remaining[i].isEmpty()) break;
                remaining[i] = insertItem(j, remaining[i], simulate);
            }

        }
        return remaining;
    }

    /**
     * <p>
     * Inserts a list of itemstacks into the inventory and return the remainder.
     * The list of itemstacks and its contents <em>should not</em> be modified in this function!
     * The inventory "should" attempt to fill existing itemstacks first,
     * equivalent to the behaviour of a player picking up an item.
     * However, the inventory may technically distribute the items however it likes.
     * Note: This function stacks items without subtypes with different metadata together.
     * </p>
     * <p>
     * Even though a default implementation is provided,
     * if your inventory behaves in "nonstandard" ways, you may have to override it.
     * </p>
     * <p>
     * Simulation mode must be accurate, and real insert must be idempotent.
     * See {@link IItemHandler#insertItem(int, ItemStack, boolean)} for more details.
     * </p>
     * @param stacks   List of items to insert. Contents must not be null.
     * @param simulate If true, the insertion is only simulated
     * @return An array with the same length as the input list representing the remaining itemstacks.
     *         Each element of the array has the same properties as the return value of
     *         {@link IItemHandler#insertItem(int, ItemStack, boolean)}
     *         (non-null, could be the same as its corresponding element in input list, etc.).
     */
    @NotNull
    default ItemStack[] insertItemsStacked(@NotNull List<ItemStack> stacks, boolean simulate) {
        var remaining = new ItemStack[stacks.size()];

        // init remaining array
        // and try to fill up existing itemstacks
        for (int i = 0; i < stacks.size(); i++)
        {
            remaining[i] = stacks.get(i);

            // not stackable -> don't bother trying to stack
            if (!remaining[i].isStackable()) continue;

            for (int j = 0; j < getSlots(); j++)
            {
                if (ItemHandlerHelper.canItemStacksStackRelaxed(getStackInSlot(j), remaining[i]))
                {
                    remaining[i] = insertItem(j, remaining[i], simulate);
                    if (remaining[i].isEmpty()) break;
                }
            }
        }

        // insert remaining items normally
        return insertItems(Arrays.asList(remaining), simulate);
    }

    /**
     * Extracts an ItemStack from the given slot.
     * <p>
     * The returned value must be empty if nothing is extracted,
     * otherwise its stack size must be less than or equal to {@code amount} and {@link ItemStack#getMaxStackSize()}.
     * </p>
     * <p>
     * As usual, the simulation mode must be accurate and not mutate the state of the inventory.
     * </p>
     *
     * @param slot     Slot to extract from.
     * @param amount   Amount to extract (may be greater than the current stack's max limit)
     * @param simulate If true, the extraction is only simulated
     * @return ItemStack extracted from the slot, must be empty if nothing can be extracted.
     *         The returned ItemStack can be safely modified after, so item handlers should return a new or copied stack.
     **/
    @NotNull
    ItemStack extractItem(int slot, int amount, boolean simulate);

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
    boolean isItemValid(int slot, @NotNull ItemStack stack);
}
