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

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

public class EmptyHandler implements IItemHandlerModifiable
{
    public static final IItemHandler INSTANCE = new EmptyHandler();

    @Override
    public int getSlots()
    {
        return 0;
    }

    @Override
    public ItemStack getStackInSlot(int slot)
    {
        return null;
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
    {
        return stack;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate)
    {
        return null;
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack)
    {
        // nothing to do here
    }
}
