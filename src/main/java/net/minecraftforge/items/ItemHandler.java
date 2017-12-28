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
import net.minecraftforge.items.holder.IItemHolder;

import javax.annotation.Nonnull;

public class ItemHandler implements IItemHandlerModifiable
{
    private final IItemHolder holder;

    public ItemHandler(IItemHolder holder)
    {
        this.holder = holder;
    }

    @Override
    public int size()
    {
        return holder.getSlotCount();
    }

    @Override
    public int getSlotLimit(int slot)
    {
        return 64;
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot)
    {
        return holder.getStack(slot);
    }

    @Override
    public boolean isStackValidForSlot(@Nonnull ItemStack stack, int slot)
    {
        return holder.isStackValidForSlot(stack, slot);
    }

    @Override
    public boolean canExtractStackFromSlot(@Nonnull ItemStack stack, int slot)
    {
        return holder.canExtractStackFromSlot(stack, slot);
    }

    @Override
    public void clearInv()
    {
        holder.clear();
    }

    @Nonnull
    @Override
    public InsertTransaction insert(Range<Integer> slotRange, ItemStack stack, boolean simulate)
    {
        return ItemHandlerHelper.insert(slotRange, stack, simulate, this);
    }

    @Nonnull
    @Override
    public ItemStack extract(Range<Integer> slotRange, IStackFilter filter, int amount, boolean simulate)
    {
        return ItemHandlerHelper.extract(slotRange, filter, amount, simulate, this);
    }

    @Override
    public void multiExtract(IStackFilter filter, Range<Integer> slotRange, @Nonnull IExtractionManager manager, boolean simulate)
    {
        ItemHandlerHelper.MultiExtract(filter, slotRange, manager, simulate, this);
    }

    @Override
    @Nonnull
    public ItemStack setStackInSlot(int slot, @Nonnull ItemStack stack)
    {
        ItemStack stack1 = holder.getStack(slot);
        holder.putStack(slot, stack, false);
        return stack1;
    }

    public IItemHolder getHolder()
    {
        return holder;
    }

    @Override
    public void onContentsChanged(int slot)
    {
        holder.onContentChanged(slot);
    }
}
