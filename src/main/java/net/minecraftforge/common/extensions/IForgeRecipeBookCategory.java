/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

package net.minecraftforge.common.extensions;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.List;
import java.util.function.Predicate;

public interface IForgeRecipeBookCategory<T extends IRecipeType<?>> extends IForgeRegistryEntry<IForgeRecipeBookCategory<?>> {
    /**
     * If tab is without filter - does not filter recipes and always is on most top position
     * @return if tab is without filter
     */
    boolean isUnfiltered();

    /**
     * Filter for displayed recipes
     * @return filtering function
     */
    Predicate<IRecipe<?>> getPredicate();

    /**
     * Recipe type of recipe book category
     * @return recipe type
     */
    T getRecipeType();

    /**
     * Recipe tab icons, can be 1 or 2 ItemStack
     * @return recipe tab icons
     */
    List<ItemStack> getIcon();
}
