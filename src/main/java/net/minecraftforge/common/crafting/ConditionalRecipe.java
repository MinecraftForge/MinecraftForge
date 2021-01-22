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

package net.minecraftforge.common.crafting;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;

public class ConditionalRecipe
{
    @ObjectHolder("forge:conditional")
    public static final IRecipeSerializer<IRecipe<?>> SERIALZIER = null;

    public static Builder builder()
    {
        return new Builder();
    }

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
        private ResourceLocation advId;
        private ConditionalAdvancement.Builder adv;

        private List<ICondition> currentConditions = new ArrayList<>();

        public Builder addCondition(ICondition condition)
        {
            currentConditions.add(condition);
            return this;
        }

        public Builder addRecipe(Consumer<Consumer<IFinishedRecipe>> callable)
        {
            callable.accept(this::addRecipe);
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

        public Builder generateAdvancement()
        {
            return generateAdvancement(null);
        }

        public Builder generateAdvancement(@Nullable ResourceLocation id)
        {
            ConditionalAdvancement.Builder builder = ConditionalAdvancement.builder();
            for(int i=0;i<recipes.size();i++)
            {
                for(ICondition cond : conditions.get(i))
                    builder = builder.addCondition(cond);
                builder = builder.addAdvancement(recipes.get(i));
            }
            return setAdvancement(id, builder);
        }

        public Builder setAdvancement(ConditionalAdvancement.Builder advancement)
        {
            return setAdvancement(null, advancement);
        }

        public Builder setAdvancement(String namespace, String path, ConditionalAdvancement.Builder advancement)
        {
            return setAdvancement(new ResourceLocation(namespace, path), advancement);
        }

        public Builder setAdvancement(@Nullable ResourceLocation id, ConditionalAdvancement.Builder advancement)
        {
            if (this.adv != null)
                throw new IllegalStateException("Invalid ConditionalRecipeBuilder, Advancement already set");
            this.advId = id;
            this.adv = advancement;
            return this;
        }

        public void build(Consumer<IFinishedRecipe> consumer, String namespace, String path)
        {
            build(consumer, new ResourceLocation(namespace, path));
        }

        public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation id)
        {
            if (!currentConditions.isEmpty())
                throw new IllegalStateException("Invalid ConditionalRecipe builder, Orphaned conditions");
            if (recipes.isEmpty())
                throw new IllegalStateException("Invalid ConditionalRecipe builder, No recipes");

            if (advId == null && adv != null)
            {
                advId = new ResourceLocation(id.getNamespace(), "recipes/" + id.getPath());
            }

            consumer.accept(new Finished(id, conditions, recipes, advId, adv));
        }
    }

    private static class Finished implements IFinishedRecipe
    {
        private final ResourceLocation id;
        private final List<ICondition[]> conditions;
        private final List<IFinishedRecipe> recipes;
        private final ResourceLocation advId;
        private final ConditionalAdvancement.Builder adv;

        private Finished(ResourceLocation id, List<ICondition[]> conditions, List<IFinishedRecipe> recipes, @Nullable ResourceLocation advId, @Nullable ConditionalAdvancement.Builder adv)
        {
            this.id = id;
            this.conditions = conditions;
            this.recipes = recipes;
            this.advId = advId;
            this.adv = adv;
        }

        @Override
        public void serialize(JsonObject json) {
            JsonArray array = new JsonArray();
            json.add("recipes", array);
            for (int x = 0; x < conditions.size(); x++)
            {
                JsonObject holder = new JsonObject();

                JsonArray conds = new JsonArray();
                for (ICondition c : conditions.get(x))
                    conds.add(CraftingHelper.serialize(c));
                holder.add("conditions", conds);
                holder.add("recipe", recipes.get(x).getRecipeJson());

                array.add(holder);
            }
        }

        @Override
        public ResourceLocation getID() {
            return id;
        }

        @Override
        public IRecipeSerializer<?> getSerializer()
        {
            return SERIALZIER;
        }

        @Override
        public JsonObject getAdvancementJson() {
            return adv == null ? null : adv.write();
        }

        @Override
        public ResourceLocation getAdvancementID() {
            return advId;
        }
    }
}
