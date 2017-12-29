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

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.IItemHandlerObserver;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.filter.IStackFilter;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.OptionalInt;

public abstract class IInvWrapperBase implements IItemHandlerModifiable
{
    private final List<IItemHandlerObserver> observers = new ArrayList<>();

    @Override
    public int size()
    {
        return getInventory().getSizeInventory();
    }

    @Override
    public int getSlotLimit(int slot)
    {
        return getInventory().getInventoryStackLimit();
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot)
    {
        return getInventory().getStackInSlot(slot);
    }

    public boolean isStackValidForSlot(@Nonnull ItemStack stack, int slot)
    {
        return getInventory().isItemValidForSlot(slot, stack);
    }

    @Override
    @Nonnull
    public ItemStack setStackInSlot(int slot, @Nonnull ItemStack stack)
    {
        ItemStack stack1 = getStackInSlot(slot);
        getInventory().setInventorySlotContents(slot, stack);
        getInventory().markDirty();
        return stack1;
    }

    @Override
    public boolean isEmpty()
    {
        return getInventory().isEmpty();
    }

    @Nonnull
    @Override
    public ItemStack insert(OptionalInt slot, @Nonnull ItemStack stack, boolean simulate)
    {
        return ItemHandlerHelper.insert(slot, stack, simulate, this, observers);
    }

    @Nonnull
    @Override
    public ItemStack extract(OptionalInt slot, IStackFilter filter, int amount, boolean simulate)
    {
        return ItemHandlerHelper.extract(slot, filter, amount, simulate, this, observers);
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
    public void onContentsChanged(int slot)
    {
        getInventory().markDirty();
    }

    @Override
    public void clearInv()
    {
        getInventory().clear();
    }

    protected abstract IInventory getInventory();
}
