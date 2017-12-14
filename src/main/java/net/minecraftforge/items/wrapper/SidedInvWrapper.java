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
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nonnull;

public class SidedInvWrapper extends IInvWrapperBase
{
    private final ISidedInventory inventory;
    private final EnumFacing facing;

    public SidedInvWrapper(ISidedInventory inventory, EnumFacing facing)
    {
        this.inventory = inventory;
        this.facing = facing;
    }

    @Override
    public boolean isStackValidForSlot(@Nonnull ItemStack stack, int slot)
    {
        return inventory.canInsertItem(inventory.getSlotsForFace(facing)[slot], stack, facing);
    }

    @Override
    public boolean canExtractStackFromSlot(@Nonnull ItemStack stack, int slot)
    {
        return inventory.canExtractItem(inventory.getSlotsForFace(facing)[slot], stack, facing);
    }

    @Override
    public int size()
    {
        return inventory.getSlotsForFace(facing).length;
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
        return inventory.getStackInSlot(inventory.getSlotsForFace(facing)[slot]);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        InvWrapper that = (InvWrapper) o;

        return getInventory().equals(that.getInventory());

    }

    @Override
    public int hashCode()
    {
        return getInventory().hashCode();
    }

    @Override
    protected IInventory getInventory()
    {
        return inventory;
    }
}
