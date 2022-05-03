/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.crafting;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;

/**
 * Used to mark a recipe that shape matters so that the recipe
 * book and auto crafting picks the correct shape.
 * Note: These methods can't be named 'getHeight' or 'getWidth' due to obfusication issues.
 */
public interface IShapedRecipe<T extends IInventory> extends IRecipe<T>
{
    int getRecipeWidth();
    int getRecipeHeight();
}
