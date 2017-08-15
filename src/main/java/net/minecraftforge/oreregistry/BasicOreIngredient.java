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

package net.minecraftforge.oreregistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.util.RecipeItemHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * A wrapper for {@link BasicOre} for use in recipes as an {@link Ingredient}.
 */
public class BasicOreIngredient extends Ingredient
{
    protected final BasicOre ore;
    protected IntList itemIds = null;

    public BasicOreIngredient(BasicOre ore)
    {
        super(0);
        this.ore = ore;
    }

    @Override
    @Nonnull
    public ItemStack[] getMatchingStacks()
    {
        List<ItemStack> ores = ore.getVariants();
        return ores.toArray(new ItemStack[ores.size()]);
    }

    @Override
    @Nonnull
    @SideOnly(Side.CLIENT)
    public IntList getValidItemStacksPacked()
    {
        List<ItemStack> ores = ore.getVariants();
        if (this.itemIds == null || this.itemIds.size() != ores.size())
        {
            this.itemIds = new IntArrayList(ores.size());

            for (ItemStack itemstack : ores)
            {
                this.itemIds.add(RecipeItemHelper.pack(itemstack));
            }

            this.itemIds.sort(IntComparators.NATURAL_COMPARATOR);
        }

        return this.itemIds;
    }

    @Override
    public boolean apply(@Nullable ItemStack input)
    {
        if (input == null)
        {
            return false;
        }
        else
        {
            for (ItemStack itemstack : ore.getVariants())
            {
                if (itemstack.getItem() == input.getItem())
                {
                    int i = itemstack.getMetadata();

                    if (i == input.getMetadata())
                    {
                        return true;
                    }
                }
            }

            return false;
        }
    }

    @Override
    protected void invalidate()
    {
        this.itemIds = null;
    }
}
