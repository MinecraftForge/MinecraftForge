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

import com.google.common.collect.Range;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.filter.IStackFilter;
import net.minecraftforge.items.filter.ItemStackFilter;

import javax.annotation.Nonnull;

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
            if (isStackValidForSlot(stack, i))
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
            if (canExtractStackFromSlot(stack, i))
                return true;
        }
        return false;
    }

    /**
     * @param stack to check
     * @param slot  to check
     * @return true is the stack can be inserted into the slot
     */
    default boolean isStackValidForSlot(@Nonnull ItemStack stack, int slot)
    {
        return true;
    }

    /**
     * @param stack to check
     * @param slot  to check
     * @return true is the stack can be extracted from the slot
     */
    default boolean canExtractStackFromSlot(@Nonnull ItemStack stack, int slot)
    {
        return true;
    }

    /**
     * @return if the IItemHandler is full. this does take voiding into account
     */
    default boolean isFull()
    {
        for (int i = 0; i < size(); i++)
        {
            if (ItemHandlerHelper.getFreeSpaceForSlot(this, i) != 0)
                return false;
        }
        return true;
    }

    /**
     * @return if the IItemHandler is empty
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
     * this will set all slots to {@link ItemStack#EMPTY}
     */
    void clearInv();

    @Nonnull
    default IItemHandlerIterator itemHandlerIterator()
    {
        return new ItemHandlerIterator(this);
    }


    /**
     * this does NOT take voiding into account
     *
     * @param scanRange       the slotRange to use {@link Range#all()} for slotLess, use {@link Range#singleton(Comparable)} for slotted
     * @param scale           the scale to use
     * @param ignoreStackSize if true the stackSize is ignored
     * @return a float between 0 and scale.
     */
    //todo give me a better name
    default float calcRedStoneFromInventory(Range<Integer> scanRange, int scale, boolean ignoreStackSize)
    {
        int minSlot = (scanRange.hasLowerBound() ? scanRange.lowerEndpoint() : 0);
        int maxSlot = (scanRange.hasUpperBound() ? Math.min(scanRange.upperEndpoint(), size()) : size());

        if (ignoreStackSize)
        {
            if (ItemHandlerHelper.isRangeSingleton(scanRange))
            {
                return getStackInSlot(scanRange.lowerEndpoint()).isEmpty() ? scale : 0;
            }
            else
            {
                float proportion = 0.0F;

                for (int i = minSlot; i < maxSlot; i++)
                {
                    if (!getStackInSlot(i).isEmpty())
                    {
                        proportion++;
                    }
                }

                proportion = proportion / (float) size();

                return (proportion * scale);
            }
        }
        else
        {
            if (ItemHandlerHelper.isRangeSingleton(scanRange))
            {
                float proportion = 0.0F;

                ItemStack itemstack = getStackInSlot(scanRange.lowerEndpoint());

                if (!itemstack.isEmpty())
                {
                    proportion += (float) itemstack.getCount() / (float) getStackLimit(itemstack, scanRange.lowerEndpoint());
                }

                return proportion * scale;
            }
            float proportion = 0.0F;

            for (int j = minSlot; j < maxSlot; ++j)
            {
                ItemStack itemstack = getStackInSlot(j);

                if (!itemstack.isEmpty())
                {
                    proportion += (float) itemstack.getCount() / (float) getStackLimit(itemstack, j);
                }
            }

            proportion = proportion / (float) size();

            return (proportion * scale);
        }
    }

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
     * @param slotRange use {@link Range#all()} for slotLess extraction, use {@link Range#singleton(Comparable)} for slotted extraction
     * @param stack     the sctack to insert
     * @param simulate  If true, the insertion is only simulated
     * @return a {@link InsertTransaction}
     */
    @Nonnull
    InsertTransaction insert(Range<Integer> slotRange, @Nonnull ItemStack stack, boolean simulate);

    /**
     * @param slotRange use {@link Range#all()} for slotLess extraction, use {@link Range#singleton(Comparable)} for slotted extraction
     * @param filter    the filter to test the the stack in in the current slot
     * @param amount    the amount to extract
     * @param simulate  If true, the extraction is only simulated
     * @return the stack that is extracted or would have been if simulated, return {@link ItemStack#EMPTY} if nothing was extracted
     */
    @Nonnull
    ItemStack extract(Range<Integer> slotRange, IStackFilter filter, int amount, boolean simulate);

    /**
     * @param slotRange  use {@link Range#all()} for slotLess extraction, use {@link Range#singleton(Comparable)} for slotted extraction
     * @param matchStack the stack to match against the stack in in the current slot
     * @param matchNBT   if true match the existingStacks NBTTagCompound
     * @param matchMeta  if true match the existingStacks metadata
     * @param amount     the amount to extract
     * @param simulate   If true, the extraction is only simulated
     * @return the stack that is extracted or would have been if simulated, return {@link ItemStack#EMPTY} if nothing was extracted
     */
    @Nonnull
    default ItemStack extractStack(Range<Integer> slotRange, @Nonnull ItemStack matchStack, boolean matchNBT, boolean matchMeta, int amount, boolean simulate)
    {
        if (matchStack.isEmpty()) return ItemStack.EMPTY;
        ItemStackFilter.Builder filterBuilder = ItemStackFilter.filterBuilder();
        filterBuilder.withItem(matchStack.getItem());
        if (matchNBT && matchStack.getTagCompound() != null)
        {
            filterBuilder.withNbtTag(nbtTagCompound -> matchStack.getTagCompound().equals(nbtTagCompound));
            filterBuilder.withCapNBTData(matchStack);
        }
        if (matchMeta)
            filterBuilder.withMetadata(Range.singleton(matchStack.getMetadata()));

        return extract(slotRange, filterBuilder.build(), amount, simulate);
    }

    /**
     * @param filter    the filter to test the the stack in in the current slot
     * @param slotRange use {@link Range#all()} for slotLess extraction, use {@link Range#singleton(Comparable)} for slotted extraction
     * @param manager   to use for the extract operation see {@link IExtractionManager} for more detail
     * @param simulate  if true, the extraction is only simulated
     */
    void MultiExtract(IStackFilter filter, Range<Integer> slotRange, @Nonnull IExtractionManager manager, boolean simulate);
}
