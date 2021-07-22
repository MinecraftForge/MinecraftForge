/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.core.NonNullList;

public class BrewingRecipeRegistry {

    private static List<IBrewingRecipe> recipes = new ArrayList<IBrewingRecipe>();

    static
    {
        addRecipe(new VanillaBrewingRecipe());
    }

    /**
     * Adds a recipe to the registry. Due to the nature of the brewing stand
     * inputs that stack (a.k.a max stack size > 1) are not allowed.
     *
     * @param input
     *            The Ingredient that goes in same slots as the water bottles
     *            would.
     * @param ingredient
     *            The Ingredient that goes in the same slot as nether wart would.
     * @param output
     *            The ItemStack that will replace the input once the brewing is
     *            done.
     * @return true if the recipe was added.
     */
    public static boolean addRecipe(Ingredient input, Ingredient ingredient, ItemStack output)
    {
        return addRecipe(new BrewingRecipe(input, ingredient, output));
    }

    /**
     * Adds a recipe to the registry. Due to the nature of the brewing stand
     * inputs that stack (a.k.a max stack size > 1) are not allowed.
     */
    public static boolean addRecipe(IBrewingRecipe recipe)
    {
        return recipes.add(recipe);
    }

    /**
     * Returns the output ItemStack obtained by brewing the passed input and
     * ingredient.
     */
    public static ItemStack getOutput(ItemStack input, ItemStack ingredient)
    {
        if (input.isEmpty() || input.getCount() != 1) return ItemStack.EMPTY;
        if (ingredient.isEmpty()) return ItemStack.EMPTY;

        for (IBrewingRecipe recipe : recipes)
        {
            ItemStack output = recipe.getOutput(input, ingredient);
            if (!output.isEmpty())
            {
                return output;
            }
        }
        return ItemStack.EMPTY;
    }

    /**
     * Returns true if the passed input and ingredient have an output
     */
    public static boolean hasOutput(ItemStack input, ItemStack ingredient)
    {
        return !getOutput(input, ingredient).isEmpty();
    }

    /**
     * Used by the brewing stand to determine if its contents can be brewed.
     * Extra parameters exist to allow modders to create bigger brewing stands
     * without much hassle
     */
    public static boolean canBrew(NonNullList<ItemStack> inputs, ItemStack ingredient, int[] inputIndexes)
    {
        if (ingredient.isEmpty()) return false;

        for (int i : inputIndexes)
        {
            if (hasOutput(inputs.get(i), ingredient))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Used by the brewing stand to brew its inventory Extra parameters exist to
     * allow modders to create bigger brewing stands without much hassle
     */
    public static void brewPotions(NonNullList<ItemStack> inputs, ItemStack ingredient, int[] inputIndexes)
    {
        for (int i : inputIndexes)
        {
            ItemStack output = getOutput(inputs.get(i), ingredient);
            if (!output.isEmpty())
            {
                inputs.set(i, output);
            }
        }
    }

    /**
     * Returns true if the passed ItemStack is a valid ingredient for any of the
     * recipes in the registry.
     */
    public static boolean isValidIngredient(ItemStack stack)
    {
        if (stack.isEmpty()) return false;

        for (IBrewingRecipe recipe : recipes)
        {
            if (recipe.isIngredient(stack))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if the passed ItemStack is a valid input for any of the
     * recipes in the registry.
     */
    public static boolean isValidInput(ItemStack stack)
    {
        if (stack.getCount() != 1) return false;

        for (IBrewingRecipe recipe : recipes)
        {
            if (recipe.isInput(stack))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns an unmodifiable list containing all the recipes in the registry
     */
    public static List<IBrewingRecipe> getRecipes()
    {
        return Collections.unmodifiableList(recipes);
    }
}
