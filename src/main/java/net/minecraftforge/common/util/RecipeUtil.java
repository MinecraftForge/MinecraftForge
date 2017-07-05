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

package net.minecraftforge.common.util;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

import com.google.common.base.Preconditions;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;

public final class RecipeUtil
{
    private RecipeUtil()
    {
    }

    /**
     * Based on {@link net.minecraft.item.crafting.ShapedRecipes#checkMatch(InventoryCrafting, int, int, boolean)}
     */
    public static boolean checkMatch(InventoryCrafting inv, NonNullList<Ingredient> input, int startX, int startY, int width, int height, boolean mirror)
    {
        for (int x = 0; x < 3; x++)
        {
            for (int y = 0; y < 3; y++)
            {
                int subX = x - startX;
                int subY = y - startY;
                Ingredient target = Ingredient.EMPTY;

                if (subX >= 0 && subY >= 0 && subX < width && subY < height)
                {
                    if (mirror)
                    {
                        target = input.get(width - subX - 1 + subY * width);
                    }
                    else
                    {
                        target = input.get(subX + subY * width);
                    }
                }

                if (!target.apply(inv.getStackInRowAndColumn(x, y)))
                {
                    return false;
                }
            }
        }

        return true;
    }

    public static class CheckedItemStackSupplier implements Supplier<ItemStack>
    {
        @Nonnull
        private final ItemStack itemStack;

        public static CheckedItemStackSupplier create(@Nonnull Block block)
        {
            Preconditions.checkNotNull(block, "block must not be null.");
            Item itemFromBlock = Item.getItemFromBlock(block);
            return create(itemFromBlock);
        }

        public static CheckedItemStackSupplier create(@Nonnull Item item)
        {
            Preconditions.checkNotNull(item, "item must not be null.");
            Preconditions.checkArgument(item != Items.AIR, "item must not be air.");
            return new CheckedItemStackSupplier(new ItemStack(item));
        }

        public static CheckedItemStackSupplier create(@Nonnull ItemStack itemStack)
        {
            Preconditions.checkNotNull(itemStack, "itemStack must not be null.");
            Preconditions.checkArgument(!itemStack.isEmpty(), "itemStack must not be empty.");
            return new CheckedItemStackSupplier(itemStack.copy());
        }

        private CheckedItemStackSupplier(@Nonnull ItemStack itemStack)
        {
            this.itemStack = itemStack;
        }

        @Override
        public ItemStack get()
        {
            return itemStack.copy();
        }
    }
}
