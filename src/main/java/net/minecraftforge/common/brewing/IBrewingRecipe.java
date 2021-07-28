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

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;

public interface IBrewingRecipe extends Recipe<Container>
{

    /**
     * Returns true is the passed ItemStack is an input for this recipe. "Input"
     * being the item that goes in one of the three bottom slots of the brewing
     * stand (e.g: water bottle)
     */
    boolean isInput(ItemStack input);

    /**
     * Returns true if the passed ItemStack is an ingredient for this recipe.
     * "Ingredient" being the item that goes in the top slot of the brewing
     * stand (e.g: nether wart)
     */
    boolean isIngredient(ItemStack ingredient);

    /**
     * Returns the output when the passed input is brewed with the passed
     * ingredient. Empty if invalid input or ingredient.
     */
    ItemStack getOutput(ItemStack input, ItemStack ingredient);

    @Override
    default boolean matches(Container container, Level level)
    {
        if (container instanceof BrewingStandBlockEntity)
            return (isInput(container.getItem(0)) || isInput(container.getItem(1)) || isInput(container.getItem(2))) && isIngredient(container.getItem(3));
        else
            return isInput(container.getItem(0)) && isInput(container.getItem(1));
    }

    @Override
    default ItemStack assemble(Container container)
    {
        return getOutput(container.getItem(0), container.getItem(1));
    }

    @Override
    default boolean canCraftInDimensions(int p_43999_, int p_44000_)
    {
        return true;
    }

    @Override
    default RecipeType<?> getType()
    {
        return BrewingRecipeRegistry.BREWING;
    }
}
