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

package net.minecraftforge.items.wrapper;

import com.google.common.collect.Range;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IExtractionManager;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.InsertTransaction;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.filter.IStackFilter;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CombinedWrapper implements IItemHandler
{
    protected final IItemHandler[] handlers;
    protected final int[] baseIndex; // index-offsets of the different handlers
    protected final int slotCount; // number of total slots

    public CombinedWrapper(IItemHandler... handlers)
    {
        this.handlers = handlers;
        this.baseIndex = new int[handlers.length];
        int index = 0;
        for (int i = 0; i < handlers.length; i++)
        {
            index += handlers[i].size();
            baseIndex[i] = index;
        }
        this.slotCount = index;
    }

    // returns the handler index for the slot
    protected int getIndexForSlot(int slot)
    {
        if (slot < 0)
            throw new IndexOutOfBoundsException();

        for (int i = 0; i < baseIndex.length; i++)
        {
            if (slot - baseIndex[i] < 0)
            {
                return i;
            }
        }
        throw new IndexOutOfBoundsException();
    }

    protected IItemHandler getHandlerFromIndex(int index)
    {
        if (index < 0 || index >= handlers.length)
        {
            throw new IndexOutOfBoundsException();
        }
        return handlers[index];
    }

    protected int getSlotFromIndex(int slot, int index)
    {
        if (index <= 0 || index >= baseIndex.length)
        {
            return slot;
        }
        return slot - baseIndex[index - 1];
    }

    @Override
    public boolean isStackValid(@Nonnull ItemStack stack)
    {
        for (IItemHandler handler : handlers)
        {
            if (handler.isStackValid(stack))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canExtractStack(@Nonnull ItemStack stack)
    {
        for (IItemHandler handler : handlers)
        {
            if (handler.canExtractStack(stack))
            {
                return true;
            }
        }
        return false;
    }

    public boolean isValid()
    {
        for (IItemHandler handler : handlers)
            if (handler == null)
                return false;
        return true;
    }

    @Override
    public boolean isStackValidForSlot(@Nonnull ItemStack stack, int slot)
    {
        int index = getIndexForSlot(slot);
        IItemHandler handler = getHandlerFromIndex(index);
        int slotindex = getSlotFromIndex(slot, index);
        return handler.isStackValidForSlot(stack, slotindex);
    }

    @Override
    public boolean canExtractStackFromSlot(@Nonnull ItemStack stack, int slot)
    {
        int index = getIndexForSlot(slot);
        IItemHandler handler = getHandlerFromIndex(index);
        int slotindex = getSlotFromIndex(slot, index);
        return handler.canExtractStackFromSlot(stack, slotindex);
    }

    @Override
    public void clearInv()
    {
        Arrays.stream(handlers).forEach(IItemHandler::clearInv);
    }

    @Override
    public int size()
    {
        return slotCount;
    }

    @Override
    public int getSlotLimit(int slot)
    {
        int index = getIndexForSlot(slot);
        IItemHandler handler = getHandlerFromIndex(index);
        int slotindex = getSlotFromIndex(slot, index);
        return handler.getSlotLimit(slotindex);
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot)
    {
        int index = getIndexForSlot(slot);
        IItemHandler handler = getHandlerFromIndex(index);
        int slotindex = getSlotFromIndex(slot, index);
        return handler.getStackInSlot(slotindex);
    }

    @Nonnull
    @Override
    public InsertTransaction insert(Range<Integer> slotRange, ItemStack stack, boolean simulate)
    {
        if (ItemHandlerHelper.isRangeSlotLess(slotRange))
        {
            for (IItemHandler handler : handlers)
            {
                InsertTransaction transaction = handler.insert(Range.all(), stack, simulate);
                if (!transaction.getInsertedStack().isEmpty())
                    return transaction;
            }
        }
        else if (ItemHandlerHelper.isRangeSingleton(slotRange))
        {
            int slot = slotRange.lowerEndpoint();
            int index = getIndexForSlot(slot);
            IItemHandler handler = getHandlerFromIndex(index);
            return handler.insert(Range.singleton(getSlotFromIndex(slot, index)), stack, simulate);
        }
        else
        {
            int minSlot = (slotRange.hasLowerBound() ? slotRange.lowerEndpoint() : 0);
            int maxSlot = (slotRange.hasUpperBound() ? Math.min(slotRange.upperEndpoint(), size()) : size());
            int minIndex = getIndexForSlot(minSlot);
            int maxIndex = getIndexForSlot(maxSlot);
            int currentMinSlot = minSlot;
            for (int i = minIndex; i < maxIndex; i++)
            {
                IItemHandler handler = getHandlerFromIndex(i);
                if (maxSlot >= handler.size() && currentMinSlot == 0)
                {
                    InsertTransaction Inserted = handler.insert(Range.all(), stack, simulate);
                    if (!Inserted.getInsertedStack().isEmpty())
                    {
                        return Inserted;
                    }
                    else currentMinSlot -= handler.size();
                }
                else
                {
                    int currentMaxSlot = Math.min(handler.size(), maxSlot);
                    InsertTransaction Inserted = handler.insert(Range.closed(currentMinSlot, currentMaxSlot), stack, simulate);
                    if (!Inserted.getInsertedStack().isEmpty())
                    {
                        return Inserted;
                    }
                    else currentMinSlot -= (currentMaxSlot - currentMinSlot);
                }
            }
        }
        return new InsertTransaction(ItemStack.EMPTY, stack);
    }

    @Nonnull
    @Override
    public ItemStack extract(Range<Integer> slotRange, IStackFilter filter, int amount, boolean simulate)
    {
        if (ItemHandlerHelper.isRangeSlotLess(slotRange))
        {
            for (IItemHandler handler : handlers)
            {
                ItemStack extract = handler.extract(Range.all(), filter, amount, simulate);
                if (!extract.isEmpty())
                    return extract;
            }
        }
        else if (ItemHandlerHelper.isRangeSingleton(slotRange))
        {
            int slot = slotRange.lowerEndpoint();
            int index = getIndexForSlot(slot);
            IItemHandler handler = getHandlerFromIndex(index);
            return handler.extract(Range.singleton(getSlotFromIndex(slot, index)), filter, amount, simulate);
        }
        else
        {
            int minSlot = (slotRange.hasLowerBound() ? slotRange.lowerEndpoint() : 0);
            int maxSlot = (slotRange.hasUpperBound() ? Math.min(slotRange.upperEndpoint(), size()) : size());
            int minIndex = getIndexForSlot(minSlot);
            int maxIndex = getIndexForSlot(maxSlot);
            int currentMinSlot = minSlot;
            for (int i = minIndex; i < maxIndex; i++)
            {

                IItemHandler handler = getHandlerFromIndex(i);
                if (maxSlot >= handler.size() && currentMinSlot == 0)
                {
                    ItemStack extracted = handler.extract(Range.all(), filter, amount, simulate);
                    if (!extracted.isEmpty())
                    {
                        return extracted;
                    }
                    else currentMinSlot -= handler.size();
                }
                else
                {
                    int currentMaxSlot = Math.min(handler.size(), maxSlot);
                    ItemStack extracted = handler.extract(Range.closed(currentMinSlot, currentMaxSlot), filter, amount, simulate);
                    if (!extracted.isEmpty())
                    {
                        return extracted;
                    }
                    else currentMinSlot -= (currentMaxSlot - currentMinSlot);
                }
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void MultiExtract(IStackFilter filter, Range<Integer> slotRange, @Nonnull IExtractionManager manager, boolean simulate)
    {
        if (ItemHandlerHelper.isRangeSlotLess(slotRange))
        {
            for (IItemHandler handler : handlers)
            {
                handler.MultiExtract(filter, slotRange, manager, simulate);

            }
        }
        else if (ItemHandlerHelper.isRangeSingleton(slotRange))
        {
            int slot = slotRange.lowerEndpoint();
            int index = getIndexForSlot(slot);
            IItemHandler handler = getHandlerFromIndex(index);
            handler.MultiExtract(filter, Range.singleton(getSlotFromIndex(slot, index)), manager, simulate);
        }
        else
        {
            {
                int minSlot = (slotRange.hasLowerBound() ? slotRange.lowerEndpoint() : 0);
                int maxSlot = (slotRange.hasUpperBound() ? Math.min(slotRange.upperEndpoint(), size()) : size());
                int minIndex = getIndexForSlot(minSlot);
                int maxIndex = getIndexForSlot(maxSlot);
                boolean onehandler = minIndex == maxIndex;
                if (onehandler)
                {
                    IItemHandler handler = getHandlerFromIndex(maxIndex);
                    handler.MultiExtract(filter, slotRange, manager, simulate);
                }
                else
                {
                    for (int i = minIndex; i < maxIndex; i++)
                    {
                        IItemHandler handler = getHandlerFromIndex(i);
                        if (maxSlot >= handler.size() && minSlot == 0)
                            handler.MultiExtract(filter, Range.all(), manager, simulate);
                        else
                        {
                            int currentMaxSlot = Math.min(handler.size(), maxSlot);
                            handler.MultiExtract(filter, Range.closed(minSlot, currentMaxSlot), manager, simulate);
                        }
                    }
                }
            }
        }
    }

    public static class Builder
    {
        List<IItemHandler> handlers = new ArrayList<>();

        public Builder withHandler(IItemHandler handler)
        {
            handlers.add(handler);
            return this;
        }

        public CombinedWrapper build()
        {
            return new CombinedWrapper(handlers.toArray(new IItemHandler[handlers.size()]));
        }
    }
}
