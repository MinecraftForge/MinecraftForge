/*
 * Minecraft Forge
 * Copyright (c) 2016-2022.
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

package net.minecraftforge.debug.recipe.recipebook;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.debug.recipe.recipebook.RecipeBookTestRecipe.Ingredients;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;

public class RecipeBookTestRecipeSerializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<RecipeBookTestRecipe>
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
        Ingredients ingredients = buf.readWithCodec(Ingredients.CODEC);
        return new RecipeBookTestRecipe(id, ingredients);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, RecipeBookTestRecipe recipe)
    {
        buffer.writeWithCodec(Ingredients.CODEC, recipe.ingredients);
    }
}
