/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.common.smelting;

import com.google.common.collect.Maps;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Map;

public class SmeltingHandler extends FurnaceRecipes
{
    public static final SmeltingHandler INSTANCE = new SmeltingHandler();

    private final Map<ItemStack, ISmeltingRecipe> recipes = Maps.newHashMap();
    private final Map<ItemStack, ItemStack> staticView;

    private SmeltingHandler()
    {
        super();
        staticView = Maps.transformValues(recipes, ISmeltingRecipe::getGenericOutput);
    }

    @Override
    public void addSmeltingRecipe(ItemStack input, ItemStack stack, float experience)
    {
        addSmeltingRecipe(input, new BasicSmeltingRecipe(stack, experience));
    }

    public void addSmeltingRecipe(ItemStack input, ISmeltingRecipe recipe)
    {
        if (findRecipe(input) != null)
        {
            FMLLog.log.info("Ignored smelting recipe with conflicting input: {} = {}", input, recipe.getGenericOutput());
            return;
        }
        recipes.put(input, recipe);
    }

    @Override
    public ItemStack getSmeltingResult(ItemStack stack)
    {
        ISmeltingRecipe recipe = findRecipe(stack);
        if (recipe != null)
            return recipe.getSmeltingResult(stack);
        else
            return ItemStack.EMPTY;
    }

    @Override
    public Map<ItemStack, ItemStack> getSmeltingList()
    {
        return staticView;
    }

    @Override
    public float getSmeltingExperience(ItemStack stack)
    {
        ISmeltingRecipe recipe = findRecipe(stack);
        if (recipe != null)
            return recipe.getExperience(stack);
        else
            return 0;
    }

    public ISmeltingRecipe findRecipe(ItemStack input)
    {
        for (Map.Entry<ItemStack, ISmeltingRecipe> entry : this.recipes.entrySet())
        {
            if (OreDictionary.itemMatches(entry.getKey(), input, false))
            {
                return entry.getValue();
            }
        }
        return null;
    }
}
