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

package net.minecraftforge.common.smelting;

import java.util.Collection;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

/**
 * Base interface for furnace recipes.
 *
 * @see SmeltingRecipeRegistry
 */
public interface ISmeltingRecipe
{

    /**
     * Determine if the recipe matches the given input.
     *
     * @param input the input stack
     * @return true if the recipe matches
     */
    boolean matches(@Nonnull ItemStack input);

    /**
     * Get the output for the recipe.
     *
     * @param input the input stack
     * @return the output stack
     */
    @Nonnull
    ItemStack getOutput(@Nonnull ItemStack input);

    /**
     * The time in ticks it takes for this recipe to apply in a furnace.
     *
     * @param input the input stack
     * @return the smelting time in ticks
     */
    int getDuration(@Nonnull ItemStack input);

    /**
     * Provide a list of Items that are possible as a result of {@code getOutput}. This is needed for e.g. crafting stats.
     * This need not be an exhaustive list. Only Item and metadata are required information. As in: A recipe that produces ItemStacks
     * of Items.STICK with metadata 0 but various different NBT tags only has to return an ItemStack of Items.STICK with metadata 0 here,
     * there is no requirement to list all NBT combinations.
     *
     * <br><br><b>The resulting ItemStacks <i>must not</i> be modified!</b>
     *
     * @return a list of outputs
     */
    Collection<ItemStack> getPossibleOutputs();

}
