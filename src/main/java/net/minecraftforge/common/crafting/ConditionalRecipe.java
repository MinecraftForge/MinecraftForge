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

package net.minecraftforge.common.crafting;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;

public class ConditionalRecipe
{
    public static class Serializer<T extends IRecipe<?>> implements IRecipeSerializer<T>
    {
        private ResourceLocation name;

        @Override
        public IRecipeSerializer<?> setRegistryName(ResourceLocation name)
        {
            this.name = name;
            return this;
        }

        @Override
        public ResourceLocation getRegistryName()
        {
            return name;
        }

        @Override
        public Class<IRecipeSerializer<?>> getRegistryType()
        {
            return Serializer.<IRecipeSerializer<?>>castClass(IRecipeSerializer.class);
        }

        @SuppressWarnings("unchecked") // Need this wrapper, because generics
        private static <G> Class<G> castClass(Class<?> cls)
        {
            return (Class<G>)cls;
        }

        @SuppressWarnings("unchecked") // We return a nested one, so we can't know what type it is.
        @Override
        public T read(ResourceLocation recipeId, JsonObject json)
        {
            JsonArray items = JSONUtils.getJsonArray(json, "recipes");
            int idx = 0;
            for (JsonElement ele : items)
            {
                if (!ele.isJsonObject())
                    throw new JsonSyntaxException("Invalid recipes entry at index " + idx + " Must be JsonObject");
                if (CraftingHelper.processConditions(JSONUtils.getJsonArray(ele.getAsJsonObject(), "conditions")))
                    return (T)RecipeManager.deserializeRecipe(recipeId, JSONUtils.getJsonObject(ele.getAsJsonObject(), "recipe"));
                idx++;
            }
            return null;
        }

        //Should never get here as we return one of the recipes we wrap.
        @Override public T read(ResourceLocation recipeId, PacketBuffer buffer) { return null; }
        @Override public void write(PacketBuffer buffer, T recipe) {}
    }

    public static class Builder
    {
        private List<ICondition[]> conditions = new ArrayList<>();
        private List<IFinishedRecipe> recipes = new ArrayList<>();

        private List<ICondition> currentConditions = new ArrayList<>();
        public Builder addCondition(ICondition condition)
        {
            currentConditions.add(condition);
            return this;
        }

        public Builder addRecipe(IFinishedRecipe recipe)
        {
            if (currentConditions.isEmpty())
                throw new IllegalStateException("Can not add a recipe with no conditions.");
            conditions.add(currentConditions.toArray(new ICondition[currentConditions.size()]));
            recipes.add(recipe);
            currentConditions.clear();
            return this;
        }
    }
}
