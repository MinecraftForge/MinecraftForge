/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.recipe.recipebook;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.debug.recipe.recipebook.RecipeBookTestRecipe.Ingredients;
import org.jetbrains.annotations.Nullable;

public class RecipeBookTestRecipeSerializer implements RecipeSerializer<RecipeBookTestRecipe>
{
    @Override
    public RecipeBookTestRecipe fromJson(ResourceLocation id, JsonObject json)
    {
        Ingredients ingredients = Ingredients.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow(false, s->{});
        return new RecipeBookTestRecipe(id, ingredients);
    }

    @Nullable
    @Override
    public RecipeBookTestRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf)
    {
        Ingredients ingredients = buf.readWithCodec(NbtOps.INSTANCE, Ingredients.CODEC);
        return new RecipeBookTestRecipe(id, ingredients);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, RecipeBookTestRecipe recipe)
    {
        buffer.writeWithCodec(NbtOps.INSTANCE, Ingredients.CODEC, recipe.ingredients);
    }
}
