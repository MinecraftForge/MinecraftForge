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
import net.minecraftforge.items.IItemHandlerIterator;

import javax.annotation.Nonnull;

public class EmptyItemHandlerItr implements IItemHandlerIterator
{

    public static final EmptyItemHandlerItr INSTANCE = new EmptyItemHandlerItr();

    private EmptyItemHandlerItr()
    {
    }

    @Override
    public boolean hasNext()
    {
        return false;
    }

    @Override
    public boolean hasPrevious()
    {
        return false;
    }

    @Override
    public int currentIndex()
    {
        return 0;
    }

    @Override
    public int nextIndex()
    {
        return 0;
    }

    @Override
    public int previousIndex()
    {
        return 0;
    }

    @Nonnull
    @Override
    public ItemStack next()
    {
        return ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public ItemStack previous()
    {
        return ItemStack.EMPTY;
    }
}
