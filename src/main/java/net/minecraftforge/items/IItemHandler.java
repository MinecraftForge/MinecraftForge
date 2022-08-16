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
import java.util.HashMap;
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
     * insertItem must have the four following properties.
     * First, the simulation mode must not mutate the inventory.
     * Second, the simulation mode must be accurate, i.e insertItem(slot, stack, true)
     * must be equivalent to insertItem(slot, stack, false).
     * Third, real insertion must be idempotent, i.e insertItem(slot, insertItem(slot, stack, false), false)
     * must be equivalent to insertItem(slot, stack, false).
     * Fourth, the itemstack returned must only differ with the itemstack given by count,
     * and the returned count must be less than or equal to the given count.
     * The obvious exemption to this rule is given for returning the empty itemstack.
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
     * This method must have the four properties required for {@link IItemHandler#insertItem(int, ItemStack, boolean)}.
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
        return insertItems(stacks, simulate, false);
    }

    /**
     * <p>
     * Inserts a list of itemstacks into the inventory and return the remainder.
     * The list of itemstacks and its contents <em>should not</em> be modified in this function!
     * </p>
     * <p>
     * The inventory "should" attempt to fill nonempty slots first,
     * equivalent to the behaviour of a player picking up an item.
     * However, the inventory may technically distribute the items however it likes.
     * Note: This function stacks items without subtypes with different metadata together.
     * </p>
     * <p>
     * Even though a default implementation is provided,
     * if your inventory behaves in "nonstandard" ways, you may have to override it.
     * </p>
     * <p>
     * This method must have the four properties required for {@link IItemHandler#insertItem(int, ItemStack, boolean)}.
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
    default ItemStack[] insertItemsStacked(@NotNull List<ItemStack> stacks, boolean simulate)
    {
        return insertItems(
                Arrays.asList(insertItems(stacks, simulate, true)),
                simulate, false);
    }

    @NotNull
    private ItemStack[] insertItems(@NotNull List<ItemStack> stacks, boolean simulate, boolean stackedOnly)
    {
        var remaining = stacks.toArray(ItemStack[]::new);

        for (int i = 0; i < getSlots(); i++)
        {
            var slot = getStackInSlot(i);
            boolean allDone = true;
            for (int j = 0; j < stacks.size(); j++)
            {
                if (remaining[j].isEmpty()) continue;
                allDone = false;

                // pretty sure this could be replaced with stackedOnly && !slot.isEmpty()
                if (stackedOnly && !ItemHandlerHelper.canItemStacksStackRelaxed(slot, remaining[j]))
                    continue;

                var prev = remaining[j];
                remaining[j] = insertItem(i, remaining[j], simulate);

                // run if simulating + we could insert part of the current itemstack into the current slot
                // note that this could be a false positive, since simulation mode doesn't actually update the inv
                // so insertItem could think a slot is empty when we want it to run as if it's not
                // false negatives are impossible
                if (simulate && !prev.equals(remaining[j], false))
                {
                    if (slot.isEmpty())
                    {
                        // remaining[j] is already accurate
                        // update slot var
                        slot = prev.copy();
                        slot.shrink(remaining[j].getCount());
                    }
                    else if (ItemHandlerHelper.canItemStacksStack(slot, prev))
                    {
                        int space = Math.min(getSlotLimit(i), slot.getMaxStackSize()) - slot.getCount();
                        int inserted = Math.min(space, prev.getCount());

                        // update remaining[j]
                        remaining[j] = prev.copy();
                        remaining[j].shrink(inserted);

                        // update slot var
                        slot.grow(inserted);
                    }
                    // handle false positive
                    // no-op, no need change slot var
                    else remaining[j] = prev;
                }
            }

            if (allDone) break;
        }

        return remaining;
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
