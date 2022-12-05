/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.items;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

@AutoRegisterCapability
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
     * </p>
     * Note: This behaviour is subtly different from {@link IFluidHandler#fill(FluidStack, IFluidHandler.FluidAction)}
     * <p>
     * Note to implementors: this method may have additional constraints based on {@link #getIOGuarantees()}.
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
     * Extracts an ItemStack from the given slot.
     * <p>
     * The returned value must be empty if nothing is extracted,
     * otherwise its stack size must be less than or equal to {@code amount} and {@link ItemStack#getMaxStackSize()}.
     * </p>
     * Note to implementors: this method may have additional constraints based on {@link #getIOGuarantees()}.
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
     * Retrieves the maximum stack size allowed to exist in the given slot for a given stack.
     * <p>
     * This may be greater than {@link ItemStack#getMaxStackSize()} but not greater than {@link #getSlotLimit(int)}.
     *
     * @param slot  Slot to query
     * @param stack Stack to query
     * @return      The maximum size allowed in the slot for the given stack.
     */
    default int getMaxStackSize(int slot, @NotNull ItemStack stack) {
        return Math.min(getSlotLimit(slot), stack.getMaxStackSize());
    }

    /**
     * <p>
     * This function re-implements the vanilla function {@link Container#canPlaceItem(int, ItemStack)}.
     * It should be used instead of simulated insertions in cases where the contents and state of the inventory are
     * irrelevant, mainly for the purpose of automation and logic (for instance, testing if a minecart can wait
     * to deposit its items into a full inventory, or if the items in the minecart can never be placed into the
     * inventory and should move on).
     * </p>
     * <ul>
     *     <li>If {@code false}, insertion of the item can never be valid.</li>
     *     <li>If {@code true}, no assumptions can be made and insertion must be simulated case-by-case.</li>
     * </ul>
     * The actual items in the inventory, its fullness, or any other state are <b>not</b> considered by this method.
     *
     * @param slot    Slot to query for validity
     * @param stack   Stack to test with for validity
     *
     * @return {@code true} if the ItemStack may be inserted, not considering the current state of the inventory.
     *         {@code false} if the ItemStack can never be inserted in any situation.
     */
    boolean isItemValid(int slot, @NotNull ItemStack stack);

    /**
     * The extraction counterpart of {@link #isItemValid(int, ItemStack)}.
     * <p>
     * It should be used instead of simulated extractions in cases where the contents and state of the inventory are
     * irrelevant, mainly for the purpose of automation and logic (for instance, testing if a minecart can wait to
     * extract items from this inventory to fill its own, or if items can never be extracted and it should move on).
     * </p>
     * <ul>
     *     <li>If {@code false}, extraction of the item is never possible.</li>
     *     <li>If {@code true}, no assumptions can be made and extraction must be simulated case-by-case.</li>
     * </ul>
     * The actual items in the inventory, its fullness, or any other state are <b>not</b> considered by this method.
     *
     * @param slot  Slot to query for validity
     * @param stack Stack to test for validity
     * @return {@code true} if extraction of the item from the given slot may be possible.
     *         {@code false} if no extraction is possible under any circumstances.
     */
    default boolean isExtractionAllowed(int slot, @NotNull ItemStack stack)
    {
        return true; // TODO: 1.20 - Remove default, forcing users to explicitly choose, widening adoption
    }

    /**
     * Queries the extended contract terms for {@link #insertItem(int, ItemStack, boolean)} and
     * {@link #extractItem(int, int, boolean)}.
     *
     * @return The I/O guarantees of this item handler
     */
    default IOGuarantees getIOGuarantees()
    {
        return IOGuarantees.NONE; // TODO: 1.20 - Remove default, forcing users to explicitly choose, widening adoption
    }

    /**
     * Enum describing the guarantees provided by insertion and extraction operations.
     */
    enum IOGuarantees
    {
        /**
         * Insertion and extraction may affect other slots, and the results of these operations may not be reflected in
         * the stack in the queried slot.
         * <p>
         * An I/O operation may raise the guarantees to a more restrictive level, but callers do not need to concern
         * themselves with this, as they can still safely operate assuming they get no guarantees. A case like this may
         * show up in a block with multiple input slots where setting the first item determines what can go into the
         * other slots, and behavior becomes consistent/strict as long as that first item remains in the inventory.
         * <p>
         * This is how item handlers have historically worked.
         */
        NONE(false, false),
        /**
         * The inventory is guaranteed to accurately reflect the outcome of insertion/extraction operations via
         * {@link #getStackInSlot(int)}.
         * Additionally, all slots are guaranteed to be fully independent from each other, meaning changes to one of
         * them must not immediately affect the contents or behavior of any of the others. A delayed side effect
         * (for example, at the end of the tick or next tick) is considered valid behavior.
         * <p>
         * This means that a third party may safely operate on multiple slots guaranteeing the independence of such
         * operations.
         */
        CONSISTENT(true, false),
        /**
         * A stricter version of {@link #CONSISTENT} providing additional guarantees on insertion and extraction
         * equivalent to an implementation of {@link Container}.
         * <p>
         * Insertion must adhere to the following:
         * <ul>
         *     <li>If {@link #isItemValid(int, ItemStack)} returns {@code false} for the provided stack, the stack must
         *     be rejected.</li>
         *     <li>If the slot contains an item and {@link ItemHandlerHelper#canItemStacksStack(ItemStack, ItemStack)}
         *     returns false when comparing it with the provided stack, the stack must be rejected.</li>
         *     <li>Otherwise any amount up to {@link #getMaxStackSize(int, ItemStack)} minus the current size of the
         *     stack in the slot must be allowed to be inserted, and any additional items must be rejected.</li>
         * </ul>
         * <p>
         * Extraction must adhere to the following:
         * <ul>
         *     <li>If {@link #isExtractionAllowed(int, ItemStack)} returns {@code false}, no action must be performed
         *     and an empty stack must be returned</li>
         *     <li>Otherwise any amount up to the current size of the stack in the slot must be allowed to be extracted.</li>
         * </ul>
         * <p>
         * This means that a third party may safely predict the outcome of multiple insertion/extraction operations on
         * the same slot and collapse them down to one or two operations. They may also do this across multiple slots,
         * since they are guaranteed to be independent.
         */
        STRICT(true, true);

        private final boolean consistent, strict;

        IOGuarantees(boolean consistent, boolean strict)
        {
            this.consistent = consistent;
            this.strict = strict;
        }

        /**
         * {@return {@code false} if the contract described in {@link #CONSISTENT} applies, {@code false} otherwise}
         */
        public boolean isConsistent()
        {
            return consistent;
        }

        /**
         * {@return {@code false} if the contract described in {@link #STRICT} applies, {@code false} otherwise}
         */
        public boolean isStrict()
        {
            return strict;
        }

        /**
         * {@return the least strict of the two guarantees}
         */
        public static IOGuarantees leastStrict(IOGuarantees first, IOGuarantees second)
        {
            return first.ordinal() < second.ordinal() ? first : second;
        }
    }
}
