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

package net.minecraftforge.common;

import com.google.common.collect.ImmutableList;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraftforge.common.extensions.IForgeRecipeBookCategory;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.List;
import java.util.function.Predicate;

public class ForgeRecipeBookCategory<T extends IRecipeType<?>> extends ForgeRegistryEntry<IForgeRecipeBookCategory<?>> implements IForgeRecipeBookCategory<T> {
    private final List<ItemStack> icons;
    private final boolean isUnfiltered;
    private final T recipeType;
    private final Predicate<IRecipe<?>> predicate;

    /**
     * @param isUnfiltered If tab is without filter
     * @param recipeType Recipe type
     * @param icons Recipe tab icons
     */
    public ForgeRecipeBookCategory(boolean isUnfiltered, T recipeType, ItemStack... icons) {
        this(isUnfiltered, recipeType, recipe -> true, icons);
    }

    /**
     * @param isUnfiltered If tab is without filter
     * @param recipeType Recipe type
     * @param predicate Filtering function
     * @param icons Recipe tab icons
     */
    public ForgeRecipeBookCategory(boolean isUnfiltered, T recipeType, Predicate<IRecipe<?>> predicate, ItemStack... icons) {
        this.isUnfiltered = isUnfiltered;
        this.recipeType = recipeType;
        this.predicate = predicate;
        this.icons = ImmutableList.copyOf(icons);
    }

    @Override
    public boolean isUnfiltered() {
        return this.isUnfiltered;
    }

    @Override
    public Predicate<IRecipe<?>> getPredicate() {
        return this.predicate;
    }

    @Override
    public T getRecipeType() {
        return this.recipeType;
    }

    @Override
    public List<ItemStack> getIcon() {
        return this.icons;
    }

    public static List<IForgeRecipeBookCategory<?>> getValidCategories(IRecipe<?> recipe) {
        return ForgeRegistries.RECIPE_BOOK_CATEGORIES.getValues().stream().filter(category -> {
            return recipe.getType().equals(category.getRecipeType()) && category.getPredicate().test(recipe);
        }).collect(java.util.stream.Collectors.toList());
    }
}
