/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

import com.google.common.base.Preconditions;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;

/**
 * A wrapper that composes another IItemHandlerModifiable, exposing only a range of the composed slots.
 * Shifting of slot indices is handled automatically for you.
 */
public class RangedWrapper implements IItemHandlerModifiable {

    private final IItemHandlerModifiable compose;
    private final int minSlot;
    private final int maxSlot;

    public RangedWrapper(IItemHandlerModifiable compose, int minSlot, int maxSlotExclusive)
    {
        Preconditions.checkArgument(maxSlotExclusive > minSlot, "Max slot must be greater than min slot");
        this.compose = compose;
        this.minSlot = minSlot;
        this.maxSlot = maxSlotExclusive;
    }

    @Override
    public int getSlots()
    {
        return maxSlot - minSlot;
    }

    @Override
    @Nonnull
    public ItemStack getStackInSlot(int slot)
    {
        if (checkSlot(slot))
        {
            return compose.getStackInSlot(slot + minSlot);
        }

        return ItemStack.EMPTY;
    }

    @Override
    @Nonnull
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
    {
        if (checkSlot(slot))
        {
            return compose.insertItem(slot + minSlot, stack, simulate);
        }

        return stack;
    }

    @Override
    @Nonnull
    public ItemStack extractItem(int slot, int amount, boolean simulate)
    {
        if (checkSlot(slot))
        {
            return compose.extractItem(slot + minSlot, amount, simulate);
        }

        return ItemStack.EMPTY;
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack)
    {
        if (checkSlot(slot))
        {
            compose.setStackInSlot(slot + minSlot, stack);
        }
    }

    @Override
    public int getSlotLimit(int slot)
    {
        if (checkSlot(slot))
        {
            return compose.getSlotLimit(slot + minSlot);
        }

        return 0;
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack)
    {
        if (checkSlot(slot))
        {
            return compose.isItemValid(slot + minSlot, stack);
        }

        return false;
    }

    private boolean checkSlot(int localSlot)
    {
        return localSlot + minSlot < maxSlot;
    }

}
