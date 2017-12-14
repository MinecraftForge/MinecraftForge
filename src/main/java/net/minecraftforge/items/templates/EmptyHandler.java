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

package net.minecraftforge.items.templates;

import com.google.common.collect.Range;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IExtractionManager;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerIterator;
import net.minecraftforge.items.InsertTransaction;
import net.minecraftforge.items.filter.IStackFilter;

import javax.annotation.Nonnull;

public class EmptyHandler implements IItemHandler
{

    public static final EmptyHandler INSTANCE = new EmptyHandler();

    private EmptyHandler()
    {
    }

    @Override
    public int size()
    {
        return 0;
    }

    @Override
    public int getSlotLimit(int slot)
    {
        return 0;
    }

    @Override
    public boolean isStackValid(@Nonnull ItemStack stack)
    {
        return false;
    }

    @Override
    public boolean canExtractStack(@Nonnull ItemStack stack)
    {
        return false;
    }

    @Override
    public boolean isStackValidForSlot(@Nonnull ItemStack stack, int slot)
    {
        return false;
    }

    @Override
    public boolean canExtractStackFromSlot(@Nonnull ItemStack stack, int slot)
    {
        return false;
    }

    @Override
    public void clearInv()
    {

    }

    @Override
    public boolean isFull()
    {
        return true;
    }

    @Override
    public boolean isEmpty()
    {
        return false;
    }

    @Nonnull
    @Override
    public IItemHandlerIterator itemHandlerIterator()
    {
        return EmptyItemHandlerItr.INSTANCE;
    }

    @Override
    public float calcRedStoneFromInventory(Range<Integer> scanRange, int scale, boolean ignoreStackSize)
    {
        return 0;
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot)
    {
        return ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public InsertTransaction insert(Range<Integer> slotRange, @Nonnull ItemStack stack, boolean simulate)
    {
        return new InsertTransaction(ItemStack.EMPTY, stack);
    }

    @Nonnull
    @Override
    public ItemStack extract(Range<Integer> slotRange, IStackFilter filter, int amount, boolean simulate)
    {
        return ItemStack.EMPTY;
    }

    @Override
    public void MultiExtract(IStackFilter filter, Range<Integer> slotRange, @Nonnull IExtractionManager manager, boolean simulate)
    {
    }
}
