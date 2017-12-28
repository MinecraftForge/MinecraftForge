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
import net.minecraftforge.items.templates.VoidExtractionManager;

import javax.annotation.Nonnull;

public class RangedWrapper implements IItemHandler
{
    private final IItemHandler compose;
    private final int min;
    private final int maxSlotExclusive;

    public RangedWrapper(IItemHandler compose, int min, int maxSlotExclusive)
    {
        this.compose = compose;
        this.min = min;
        this.maxSlotExclusive = maxSlotExclusive;
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
        compose.multiExtract(stack -> true, Range.closed(min, maxSlotExclusive), VoidExtractionManager.INSTANCE, false);
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
    public InsertTransaction insert(Range<Integer> slotRange, ItemStack stack, boolean simulate)
    {
        if (ItemHandlerHelper.isRangeSlotLess(slotRange))
        {
            return compose.insert(Range.closed(min, maxSlotExclusive), stack, simulate);
        }
        else
        {
            int minSlot = (slotRange.hasLowerBound() ? slotRange.lowerEndpoint() : 0);
            int maxSlot = (slotRange.hasUpperBound() ? Math.min(slotRange.upperEndpoint(), size()) : size());
            return compose.insert(Range.closed(Math.max(min, (minSlot + min)), Math.min(maxSlotExclusive, (maxSlot + min))), stack, simulate);
        }
    }

    @Nonnull
    @Override
    public ItemStack extract(Range<Integer> slotRange, IStackFilter filter, int amount, boolean simulate)
    {
        if (ItemHandlerHelper.isRangeSlotLess(slotRange))
        {
            return compose.extract(Range.closed(min, maxSlotExclusive), filter, amount, simulate);
        }
        else
        {
            int minSlot = (slotRange.hasLowerBound() ? slotRange.lowerEndpoint() : 0);
            int maxSlot = (slotRange.hasUpperBound() ? Math.min(slotRange.upperEndpoint(), size()) : size());
            return compose.extract(Range.closed(Math.max(min, (minSlot + min)), Math.min(maxSlotExclusive, (maxSlot + min))), filter, amount, simulate);
        }
    }

    @Override
    public void multiExtract(IStackFilter filter, Range<Integer> slotRange, @Nonnull IExtractionManager manager, boolean simulate)
    {
        if (ItemHandlerHelper.isRangeSlotLess(slotRange))
        {
            compose.multiExtract(filter, Range.closed(min, maxSlotExclusive), manager, simulate);
        }
        else
        {
            int minSlot = (slotRange.hasLowerBound() ? slotRange.lowerEndpoint() : 0);
            int maxSlot = (slotRange.hasUpperBound() ? Math.min(slotRange.upperEndpoint(), size()) : size());
            compose.multiExtract(filter, Range.closed(Math.max(min, (minSlot + min)), Math.min(maxSlotExclusive, (maxSlot + min))), manager, simulate);
        }
    }
}
