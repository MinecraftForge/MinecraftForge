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

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class BrewingRecipeRegistry {

    /**
     * Returns the result ItemStack obtained by brewing the passed base and
     * reagent.
     */
    public static ItemStack getOutput(Level level, ItemStack input, ItemStack ingredient)
    {
        if (input.isEmpty() || input.getCount() != 1) return ItemStack.EMPTY;
        if (ingredient.isEmpty()) return ItemStack.EMPTY;

        for (IBrewingRecipe recipe : level.getRecipeManager().getAllRecipesFor(ForgeMod.BREWING))
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
     * Returns true if the passed base and reagent have an result
     */
    public static boolean hasOutput(Level level, ItemStack input, ItemStack ingredient)
    {
        return !getOutput(level, input, ingredient).isEmpty();
    }

    /**
     * Used by the brewing stand to determine if its contents can be brewed.
     * Extra parameters exist to allow modders to create bigger brewing stands
     * without much hassle
     */
    public static boolean canBrew(Level level, NonNullList<ItemStack> inputs, ItemStack ingredient, int[] inputIndexes)
    {
        if (ingredient.isEmpty()) return false;

        for (int i : inputIndexes)
        {
            if (hasOutput(level, inputs.get(i), ingredient))
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
    public static void brewPotions(Level level, NonNullList<ItemStack> inputs, ItemStack ingredient, int[] inputIndexes)
    {
        for (int i : inputIndexes)
        {
            ItemStack output = getOutput(level, inputs.get(i), ingredient);
            if (!output.isEmpty())
            {
                inputs.set(i, output);
            }
        }
    }

    /**
     * Returns true if the passed ItemStack is a valid reagent for any of the
     * recipes in the registry.
     */
    public static boolean isValidIngredient(@Nullable Level level, ItemStack stack)
    {
        if (stack.isEmpty()) return false;

        if (level != null)
        {
            for (IBrewingRecipe recipe : level.getRecipeManager().getAllRecipesFor(ForgeMod.BREWING))
            {
                if (recipe.isIngredient(stack))
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns true if the passed ItemStack is a valid base for any of the
     * recipes in the registry.
     */
    public static boolean isValidInput(@Nullable Level level, ItemStack stack)
    {
        if (stack.getCount() != 1) return false;

        if (level != null)
        {
            for (IBrewingRecipe recipe : level.getRecipeManager().getAllRecipesFor(ForgeMod.BREWING))
            {
                if (recipe.isInput(stack))
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns an unmodifiable list containing all the recipes in the registry
     */
    public static List<IBrewingRecipe> getRecipes(Level level)
    {
        return Collections.unmodifiableList(level.getRecipeManager().getAllRecipesFor(ForgeMod.BREWING));
    }
}
