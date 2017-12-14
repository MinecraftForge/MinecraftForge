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

package net.minecraftforge.items.holder;

import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;

public class ItemHolder implements IItemHolder, INBTSerializable<NBTTagCompound>
{
    private final NonNullList<ItemStack> stacks;

    public ItemHolder(int size)
    {
        this.stacks = NonNullList.withSize(size, ItemStack.EMPTY);
    }

    @Override
    public int getSlotCount()
    {
        return stacks.size();
    }

    @Nonnull
    @Override
    public ItemStack getStack(int slot)
    {
        return stacks.get(slot);
    }

    @Override
    public boolean putStack(int slot, ItemStack stack, boolean simulated)
    {

        if (!simulated)
            setStack(slot, stack);
        return true;
    }

    @Override
    public void clear()
    {
        stacks.clear();
    }

    @Override
    public void onContentChanged(int slot)
    {

    }

    @Override
    public void setStack(int slot, ItemStack stack)
    {
        stacks.set(slot, stack);
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        return ItemStackHelper.saveAllItems(new NBTTagCompound(), stacks);
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
        ItemStackHelper.loadAllItems(nbt, stacks);
    }
}
