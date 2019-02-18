/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

public class RecipeStackHandler extends ItemStackHandler implements IRecipeInventory
{

    protected int height;
    protected int width;

    public RecipeStackHandler(int height, int width)
    {
        super(height * width);
        this.height = height;
        this.width = width;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        setStackInSlot(index, stack);
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public ItemStack getStack(int index) {
        return getStackInSlot(index);
    }

}
