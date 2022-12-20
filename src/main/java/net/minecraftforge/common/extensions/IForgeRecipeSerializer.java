/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.extensions;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.conditions.IConditionContext;

public interface IForgeRecipeSerializer<T extends Recipe<?>>
{
	private RecipeSerializer<T> self()
	{
		return (RecipeSerializer<T>) this;
	}

	default T fromJson(ResourceLocation recipeLoc, JsonObject recipeJson, IConditionContext context)
	{
		return self().fromJson(recipeLoc, recipeJson);
	}
}
