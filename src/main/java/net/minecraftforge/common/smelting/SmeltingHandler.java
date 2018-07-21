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

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.CompoundIngredient;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.oredict.OreDictionary;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class SmeltingHandler extends FurnaceRecipes
{
    private static final SmeltingHandler INSTANCE = new SmeltingHandler();

    private List<ISmeltingRecipe> recipes;
    private Map<ItemStack, ItemStack> staticView;

    private SmeltingHandler()
    {
        super();
        // super adds recipes an therefore indirectly initializes above fields
    }

    public static SmeltingHandler instance()
    {
        return SmeltingHandler.INSTANCE;
    }

    @Override
    public void addSmeltingRecipe(ItemStack input, ItemStack output, float experience)
    {
        addSmeltingRecipe(new BasicSmeltingRecipe(input, output, experience));
    }

    /**
     * Add an arbitrary Recipe to the furnace<br>
     * <b>You will be required to move to json 1.13</b>
     */
    public void addSmeltingRecipe(ISmeltingRecipe recipe)
    {
        ItemStack[] inputs = recipe.getInput().getMatchingStacks();
        List<ISmeltingRecipe> collisions = Lists.newArrayList();
        for (ItemStack in : inputs)
        {
            ISmeltingRecipe isr = findRecipe(in);
            if (isr != null)
                collisions.add(isr);
        }
        if (collisions.size() > 0)
        {
            collisions.add(recipe);
            if (collisions.stream().allMatch(ISmeltingRecipe::isBasic))
            {
                if (collisions.stream().allMatch(isr -> OreDictionary.itemMatches(isr.getDefaultResult(), recipe.getDefaultResult(), false)))
                {
                    getRecipes().removeAll(collisions);

                    List<Ingredient> ingredients = Lists.newArrayList();
                    collisions.forEach(isr -> ingredients.add(isr.getInput()));
                    Ingredient merged = new CompoundIngredient(ingredients);

                    float exp = 0;
                    for (ISmeltingRecipe isr : collisions)
                        exp = Math.max(isr.getExperience(ItemStack.EMPTY), exp);

                    getRecipes().add(new BasicSmeltingRecipe(merged, recipe.getDefaultResult(), exp));
                    FMLLog.log.debug("Merged {} smelting recipes to {} => {}", collisions.size(), Arrays.toString(merged.getMatchingStacks()), recipe.getDefaultResult());
                }
            }
            else
            {
                FMLLog.log.info("Ignored smelting recipe with conflicting input: {} = {}", Arrays.toString(recipe.getInput().getMatchingStacks()), recipe.getDefaultResult());
            }
        }
        else
        {
            if (recipe.getInput().getMatchingStacks().length > 0)
                getRecipes().add(recipe);
            else
                FMLLog.log.info("Ignored smelting recipe due to empty matchingStacks in Ingredient: [] = {}", recipe.getDefaultResult());
        }
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
    @Deprecated
    public Map<ItemStack, ItemStack> getSmeltingList()
    {
        return staticView;
    }

    /**
     * this method is required to be able to set {@link SmeltingHandler#recipes} before the super-constructor returns
     */
    public List<ISmeltingRecipe> getRecipes()
    {
        if (recipes != null)
            return recipes;
        recipes = Lists.newArrayList();
        staticView = new VanillaView();
        return recipes;
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
        for (ISmeltingRecipe recipe : getRecipes())
            if (recipe.getInput().apply(input))
                return recipe;
        return null;
    }

    /**
     * Map is used for binary compatibility reasons<br>
     * <br>
     * The map is a transformed live view of the new recipe list where:<br>
     * The keys correspond to the first ItemStack returned in {@link net.minecraft.item.crafting.Ingredient#getMatchingStacks()}<br>
     * The values correspond to the static representation of the Recipe Outputs as described in {@link ISmeltingRecipe#getDefaultResult()}<br>
     * <br>
     * {@link VanillaView#remove(Object)} removes the first recipe where the passed ItemStack matches the recipes Ingredient<br>
     * {@link VanillaView#put(ItemStack, ItemStack)} tries to add a basic recipe to the list with an experience value of 0.1
     */
    private class VanillaView extends AbstractMap<ItemStack, ItemStack>
    {
        @Override
        public Set<Map.Entry<ItemStack, ItemStack>> entrySet()
        {
            return new AbstractSet<Entry<ItemStack, ItemStack>>()
            {
                @Override
                public Iterator<Entry<ItemStack, ItemStack>> iterator()
                {
                    return Iterators.transform(recipes.iterator(), recipe ->
                            new SimpleImmutableEntry<>(recipe.getInput().getMatchingStacks()[0], recipe.getDefaultResult()));
                }

                @Override
                public int size()
                {
                    return recipes.size();
                }
            };
        }

        @Override
        public Collection<ItemStack> values()
        {
            return Lists.transform(recipes, ISmeltingRecipe::getDefaultResult);
        }

        @Override
        public ItemStack remove(Object key)
        {
            if (!(key instanceof ItemStack))
                return null;
            Iterator<ISmeltingRecipe> it = recipes.iterator();
            for (ISmeltingRecipe recipe = it.next(); it.hasNext(); )
            {
                if (recipe.getInput().apply((ItemStack) key))
                {
                    it.remove();
                    return recipe.getDefaultResult();
                }
            }
            return null;
        }

        @Override
        public ItemStack put(ItemStack key, ItemStack value)
        {
            addSmeltingRecipe(key, value, 0.1f);
            return null;
        }
    }
}
