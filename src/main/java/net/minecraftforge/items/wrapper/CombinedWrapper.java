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

import com.google.common.collect.Iterators;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerObserver;
import net.minecraftforge.items.filter.IStackFilter;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.OptionalInt;

public class CombinedWrapper implements IItemHandler, IItemHandlerObserver
{
    protected final IItemHandler[] handlers;
    protected final int[] baseIndex; // index-offsets of the different handlers
    protected final int slotCount; // number of total slots
    private final List<IItemHandlerObserver> observers = new ArrayList<>();

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
        Arrays.stream(handlers).forEach(handler -> handler.addObserver(this));
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

    protected int getIndexFromHandler(IItemHandler handler){
        for (int i = 0; i < handlers.length; i++)
        {
            if (handler == handlers[i])
            {
                return i;
            }
        }
        return -1;
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
    public ItemStack insert(OptionalInt slot, @Nonnull ItemStack stack, boolean simulate)
    {
        if (slot.isPresent()){
            int index = getIndexForSlot(slot.getAsInt());
            IItemHandler handler = getHandlerFromIndex(index);
            int slotindex = getSlotFromIndex(slot.getAsInt(), index);
            return handler.insert(OptionalInt.of(slotindex), stack, simulate);
        }
        for (IItemHandler handler : handlers){
            ItemStack remainder = handler.insert(OptionalInt.empty(), stack, simulate);
            if (remainder.getCount() != stack.getCount()){
                return remainder;
            }
        }
        return stack;
    }

    @Nonnull
    @Override
    public ItemStack extract(OptionalInt slot, IStackFilter filter, int amount, boolean simulate)
    {
        if (slot.isPresent()){
            int index = getIndexForSlot(slot.getAsInt());
            IItemHandler handler = getHandlerFromIndex(index);
            int slotindex = getSlotFromIndex(slot.getAsInt(), index);
            return handler.extract(OptionalInt.of(slotindex), filter, amount, simulate);
        }
        for (IItemHandler handler : handlers){
            ItemStack extract = handler.extract(OptionalInt.empty(), filter, amount, simulate);
            if (!extract.isEmpty())
                return extract;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void addObserver(IItemHandlerObserver observer)
    {
        observers.add(observer);
    }

    @Override
    public void removeObserver(IItemHandlerObserver observer)
    {
        observers.remove(observer);
    }

    @Override
    public void onStackInserted(IItemHandler handler, @Nonnull ItemStack oldStack, @Nonnull ItemStack newStack, int slot)
    {
        observers.forEach(observer -> observer.onStackInserted(this, oldStack, newStack, getSlotFromIndex(slot, getIndexFromHandler(handler))));
    }

    @Override
    public void onStackExtracted(IItemHandler handler, @Nonnull ItemStack oldStack, @Nonnull ItemStack newStack, int slot)
    {
        observers.forEach(observer -> observer.onStackExtracted(this, oldStack, newStack, getSlotFromIndex(slot, getIndexFromHandler(handler))));
    }

    public static class Builder{
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
