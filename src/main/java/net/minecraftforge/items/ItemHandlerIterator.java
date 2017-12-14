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

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ItemHandlerIterator implements IItemHandlerIterator
{
    private final IItemHandler itemHandler;
    private int index = 0;

    public ItemHandlerIterator(IItemHandler itemHandler)
    {
        this.itemHandler = itemHandler;
    }

    private int size()
    {
        return itemHandler.size();
    }

    @Override
    public boolean hasNext()
    {
        return index != size();
    }

    @Override
    public boolean hasPrevious()
    {
        return index != 0;
    }

    @Override
    public int currentIndex()
    {
        return index;
    }

    @Override
    public int nextIndex()
    {
        if (index == size())
            throw new NullPointerException();
        return index + 1;
    }

    @Override
    public int previousIndex()
    {
        if (index == 0)
            throw new NullPointerException();
        return index - 1;
    }

    @Override
    @Nonnull
    public ItemStack next()
    {
        ItemStack stack = itemHandler.getStackInSlot(index);
        index++;
        return stack;

    }

    @Override
    @Nonnull
    public ItemStack previous()
    {
        ItemStack stack = itemHandler.getStackInSlot(index);
        index--;
        return stack;
    }
}
