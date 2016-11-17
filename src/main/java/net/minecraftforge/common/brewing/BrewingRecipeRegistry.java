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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import javax.annotation.Nonnull;

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
     *            The ItemStack that goes in same slots as the water bottles
     *            would.
     * @param ingredient
     *            The ItemStack that goes in the same slot as nether wart would.
     * @param output
     *            The ItemStack that will replace the input once the brewing is
     *            done.
     * @return true if the recipe was added.
     */
    public static boolean addRecipe(@Nonnull ItemStack input, @Nonnull ItemStack ingredient, @Nonnull ItemStack output)
    {
        return addRecipe(new BrewingRecipe(input, ingredient, output));
    }

    /**
     * Adds a recipe to the registry. Due to the nature of the brewing stand
     * inputs that stack (a.k.a max stack size > 1) are not allowed.
     *
     * @param input
     *            The ItemStack that goes in same slots as the water bottles
     *            would.
     * @param ingredient
     *            The ItemStack that goes in the same slot as nether wart would.
     * @param output
     *            The ItemStack that will replace the input once the brewing is
     *            done.
     * @return true if the recipe was added.
     */
    public static boolean addRecipe(@Nonnull ItemStack input, @Nonnull String ingredient, @Nonnull ItemStack output)
    {
        return addRecipe(new BrewingOreRecipe(input, ingredient, output));
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
    @Nonnull
    public static ItemStack getOutput(@Nonnull ItemStack input, ItemStack ingredient)
    {
        if (input.func_190926_b() || input.getMaxStackSize() != 1 || input.func_190916_E() != 1) return ItemStack.field_190927_a;
        if (ingredient.func_190926_b()) return ItemStack.field_190927_a;

        for (IBrewingRecipe recipe : recipes)
        {
            ItemStack output = recipe.getOutput(input, ingredient);
            if (output != null)
            {
                return output;
            }
        }
        return ItemStack.field_190927_a;
    }

    /**
     * Returns true if the passed input and ingredient have an output
     */
    public static boolean hasOutput(ItemStack input, ItemStack ingredient)
    {
        return !getOutput(input, ingredient).func_190926_b();
    }

    /**
     * Used by the brewing stand to determine if its contents can be brewed.
     * Extra parameters exist to allow modders to create bigger brewing stands
     * without much hassle
     */
    public static boolean canBrew(NonNullList<ItemStack> inputs, @Nonnull ItemStack ingredient, int[] inputIndexes)
    {
        if (ingredient.func_190926_b()) return false;

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
    public static void brewPotions(NonNullList<ItemStack> inputs, @Nonnull ItemStack ingredient, int[] inputIndexes)
    {
        for (int i : inputIndexes)
        {
            ItemStack output = getOutput(inputs.get(i), ingredient);
            if (!output.func_190926_b())
            {
                inputs.set(i, output);
            }
        }
    }

    /**
     * Returns true if the passed ItemStack is a valid ingredient for any of the
     * recipes in the registry.
     */
    public static boolean isValidIngredient(@Nonnull ItemStack stack)
    {
        if (stack.func_190926_b()) return false;

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
    public static boolean isValidInput(@Nonnull ItemStack stack)
    {
        if (stack.getMaxStackSize() != 1 || stack.func_190916_E() != 1) return false;

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
