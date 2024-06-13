/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.crafting;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;

/**
 * Used to mark a recipe that shape matters so that the recipe
 * book and auto crafting picks the correct shape.
 * Note: These methods can't be named 'getHeight' or 'getWidth' due to obfusication issues.
 */
public interface IShapedRecipe<T extends RecipeInput> extends Recipe<T> {
    int getRecipeWidth();
    int getRecipeHeight();
}
