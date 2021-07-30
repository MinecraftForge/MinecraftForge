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

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;

import java.util.List;

public class BrewingRecipeHelper
{

    /**
     * Used by the brewing stand to determine if its contents can be brewed.
     * Extra parameters exist to allow modders to create bigger brewing stands
     * without much hassle
     */
    public static boolean canBrew(Level level, BrewingContainerWrapper container)
    {
        if (container.getItem(container.ingredientSlot()).isEmpty()) return false;
        return !level.getRecipeManager().getRecipesFor(ForgeMod.BREWING, container, level).isEmpty();
    }

    /**
     * Used by the brewing stand to brew its inventory Extra parameters exist to
     * allow modders to create bigger brewing stands without much hassle
     */
    public static void brewPotions(Level level, BrewingContainerWrapper container)
    {
        final List<IBrewingRecipe> recipes = level.getRecipeManager().getRecipesFor(ForgeMod.BREWING, container, level);
        for (int slot : container.potionSlots())
        {
            for (IBrewingRecipe recipe : recipes)
            {
                if (recipe.getBase().test(container.getItem(slot)))
                {
                    container.setItem(slot, recipe.assemble(container));
                    break;
                }
            }
        }
    }

    /**
     * Returns true if the passed ItemStack is a valid reagent for any of the
     * recipes in the registry.
     */
    public static boolean isValidIngredient(Level level, ItemStack stack)
    {
        if (stack.isEmpty()) return false;

        for (IBrewingRecipe recipe : level.getRecipeManager().getAllRecipesFor(ForgeMod.BREWING))
        {
            if (recipe.getReagent().test(stack))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if the passed ItemStack is a valid base for any of the
     * recipes in the registry.
     */
    public static boolean isValidInput(Level level, ItemStack stack)
    {
        if (stack.getCount() != 1) return false;

        for (IBrewingRecipe recipe : level.getRecipeManager().getAllRecipesFor(ForgeMod.BREWING))
        {
            if (recipe.getBase().test(stack))
            {
                return true;
            }
        }
        return false;
    }
}
