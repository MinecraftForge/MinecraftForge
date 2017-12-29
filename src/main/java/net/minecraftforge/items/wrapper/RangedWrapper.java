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
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerObserver;
import net.minecraftforge.items.filter.IStackFilter;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.OptionalInt;

public class RangedWrapper implements IItemHandler, IItemHandlerObserver
{
    private final IItemHandler compose;
    private final int min;
    private final int maxSlotExclusive;
    protected final Range<Integer> scanrange;
    protected final List<IItemHandlerObserver> observers = new ArrayList<>();

    public RangedWrapper(IItemHandler compose, int min, int maxSlotExclusive)
    {
        this.compose = compose;
        this.min = min;
        this.maxSlotExclusive = maxSlotExclusive;
        this.scanrange = Range.closed(min, maxSlotExclusive);
        compose.addObserver(this);
    }

    @Override
    public boolean isStackValid(@Nonnull ItemStack stack)
    {
        return compose.isStackValid(stack);
    }

    @Override
    public boolean canExtractStack(@Nonnull ItemStack stack)
    {
        return compose.canExtractStack(stack);
    }

    @Override
    public boolean isStackValidForSlot(@Nonnull ItemStack stack, int slot)
    {
        return compose.isStackValidForSlot(stack, slot + min);
    }

    @Override
    public boolean canExtractStackFromSlot(@Nonnull ItemStack stack, int slot)
    {
        return compose.canExtractStackFromSlot(stack, slot + min);
    }

    @Override
    public void clearInv()
    {
        for (int i = min; i < maxSlotExclusive; i++)
        {
            ItemStack stack = getStackInSlot(i);
            while (!stack.isEmpty())
                compose.extract(OptionalInt.of(i), stack1 -> true, stack.getMaxStackSize(), false);
        }
    }

    @Override
    public int size()
    {
        return maxSlotExclusive - min;
    }

    @Override
    public int getSlotLimit(int slot)
    {
        return compose.getSlotLimit(slot + min);
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot)
    {
        return compose.getStackInSlot(slot + min);
    }

    @Nonnull
    @Override
    public ItemStack insert(OptionalInt slot, @Nonnull ItemStack stack, boolean simulate)
    {
        if (slot.isPresent())
        {
            return compose.insert(OptionalInt.of(min + slot.getAsInt()), stack, simulate);
        }
        for (int i = min; i < maxSlotExclusive; i++)
        {
            ItemStack remainder = compose.insert(OptionalInt.of(i), stack, simulate);
            if (remainder.getCount() != stack.getCount())
                return remainder;
        }
        return ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public ItemStack extract(OptionalInt slot, IStackFilter filter, int amount, boolean simulate)
    {
        if (slot.isPresent())
        {
            return compose.extract(OptionalInt.of(min + slot.getAsInt()), filter, amount, simulate);
        }
        for (int i = min; i < maxSlotExclusive; i++)
        {
            ItemStack extract = compose.extract(OptionalInt.of(i), filter, amount, simulate);
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
        if (scanrange.contains(slot))
        {
            observers.forEach(observer -> observer.onStackInserted(this, oldStack, newStack, slot));
        }
    }

    @Override
    public void onStackExtracted(IItemHandler handler, @Nonnull ItemStack oldStack, @Nonnull ItemStack newStack, int slot)
    {
        if (scanrange.contains(slot))
        {
            observers.forEach(observer -> observer.onStackExtracted(this, oldStack, newStack, slot));
        }
    }
}
