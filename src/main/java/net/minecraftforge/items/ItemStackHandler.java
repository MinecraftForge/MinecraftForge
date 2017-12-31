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

import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.filter.IStackFilter;

import javax.annotation.Nonnull;
import java.util.OptionalInt;

public class ItemStackHandler implements IItemHandlerModifiable, INBTSerializable<NBTTagCompound>
{
    protected final NonNullList<ItemStack> stacks;

    public ItemStackHandler(int size)
    {
        this.stacks = NonNullList.withSize(size, ItemStack.EMPTY);
    }


    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack)
    {
        stacks.set(slot, stack);
    }

    @Override
    public int size()
    {
        return stacks.size();
    }

    @Override
    public void clearInv()
    {
        stacks.clear();
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
        return stacks.get(slot);
    }

    @Nonnull
    @Override
    public ItemStack insert(OptionalInt slot, @Nonnull ItemStack stack, boolean simulate)
    {
        return ItemHandlerHelper.insert(slot, stack, simulate, this);
    }

    @Nonnull
    @Override
    public ItemStack extract(OptionalInt slot, IStackFilter filter, int amount, boolean simulate)
    {
        return ItemHandlerHelper.extract(slot, filter, amount, simulate, this);
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound compound = new NBTTagCompound();

        return ItemStackHelper.saveAllItems(compound, stacks);
    }

    @Override
    public void deserializeNBT(NBTTagCompound compound)
    {
        ItemStackHelper.loadAllItems(compound, stacks);
    }
}
