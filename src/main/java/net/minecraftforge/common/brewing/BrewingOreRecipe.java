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

package net.minecraftforge.common.brewing;

import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class BrewingOreRecipe extends AbstractBrewingRecipe<List<ItemStack>> {

    public BrewingOreRecipe(ItemStack input, String ingredient, ItemStack output)
    {
        super(input, OreDictionary.getOres(ingredient), output);
    }

    public BrewingOreRecipe(ItemStack input, List<ItemStack> ingredient, ItemStack output)
    {
        super(input, ingredient, output);
    }

    @Override
    public boolean isIngredient(ItemStack stack)
    {
        for (ItemStack target : this.getIngredient())
        {
            if (OreDictionary.itemMatches(target, stack, false))
            {
                return true;
            }

        }
        return false;
    }
}