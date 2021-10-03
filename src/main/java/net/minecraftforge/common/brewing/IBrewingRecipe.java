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
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;

/**
 * Interface representing a brewing recipe. For reference look at the basic forge implementation {@link BrewingRecipe}, {@link MixingBrewingRecipe} or {@link ContainerBrewingRecipe}.
 */
public interface IBrewingRecipe extends Recipe<IBrewingContainer>
{
    @Override
    default boolean matches(final IBrewingContainer container, final Level level)
    {
        return isBase(container.base()) && isReagent(container.reagent());
    }

    @Override
    default boolean canCraftInDimensions(int width, int height)
    {
        return true;
    }

    @Override
    default RecipeType<?> getType()
    {
        return ForgeMod.BREWING.get();
    }

    boolean isReagent(final ItemStack reagent);

    boolean isBase(final ItemStack base);
}
