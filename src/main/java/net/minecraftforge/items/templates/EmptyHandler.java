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

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerObserver;
import net.minecraftforge.items.filter.IStackFilter;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.OptionalInt;

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
    public ItemStack getStackInSlot(int slot)
    {
        return ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public ItemStack insert(OptionalInt slot, @Nonnull ItemStack stack, boolean simulate)
    {
        return ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public ItemStack extract(OptionalInt slot, IStackFilter filter, int amount, boolean simulate)
    {
        return ItemStack.EMPTY;
    }

    @Override
    public void addObserver(IItemHandlerObserver observer)
    {

    }

    @Override
    public void removeObserver(IItemHandlerObserver observer)
    {

    }
}
