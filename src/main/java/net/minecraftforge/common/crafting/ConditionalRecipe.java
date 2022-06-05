/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.crafting;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;

public class ConditionalRecipe
{
    @ObjectHolder("forge:conditional")
    public static final RecipeSerializer<Recipe<?>> SERIALZIER = null;

    public static Builder builder()
    {
        return new Builder();
    }

    public static class Serializer<T extends Recipe<?>> implements RecipeSerializer<T>
    {
        private ResourceLocation name;

        @Override
        public RecipeSerializer<?> setRegistryName(ResourceLocation name)
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
        public Class<RecipeSerializer<?>> getRegistryType()
        {
            return Serializer.<RecipeSerializer<?>>castClass(RecipeSerializer.class);
        }

        @SuppressWarnings("unchecked") // Need this wrapper, because generics
        private static <G> Class<G> castClass(Class<?> cls)
        {
            return (Class<G>)cls;
        }

        @Override
        public T fromJson(ResourceLocation recipeId, JsonObject json)
        {
            return fromJson(recipeId, json, ICondition.IContext.EMPTY);
        }

        @SuppressWarnings("unchecked") // We return a nested one, so we can't know what type it is.
        @Override
        public T fromJson(ResourceLocation recipeId, JsonObject json, ICondition.IContext context)
        {
            JsonArray items = GsonHelper.getAsJsonArray(json, "recipes");
            int idx = 0;
            for (JsonElement ele : items)
            {
                if (!ele.isJsonObject())
                    throw new JsonSyntaxException("Invalid recipes entry at index " + idx + " Must be JsonObject");
                if (CraftingHelper.processConditions(GsonHelper.getAsJsonArray(ele.getAsJsonObject(), "conditions"), context))
                    return (T)RecipeManager.fromJson(recipeId, GsonHelper.getAsJsonObject(ele.getAsJsonObject(), "recipe"));
                idx++;
            }
            return null;
        }

        //Should never get here as we return one of the recipes we wrap.
        @Override public T fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) { return null; }
        @Override public void toNetwork(FriendlyByteBuf buffer, T recipe) {}
    }

    public static class Builder
    {
        private List<ICondition[]> conditions = new ArrayList<>();
        private List<FinishedRecipe> recipes = new ArrayList<>();
        private ResourceLocation advId;
        private ConditionalAdvancement.Builder adv;

        private List<ICondition> currentConditions = new ArrayList<>();

        public Builder addCondition(ICondition condition)
        {
            currentConditions.add(condition);
            return this;
        }

        public Builder addRecipe(Consumer<Consumer<FinishedRecipe>> callable)
        {
            callable.accept(this::addRecipe);
            return this;
        }

        public Builder addRecipe(FinishedRecipe recipe)
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

        public void build(Consumer<FinishedRecipe> consumer, String namespace, String path)
        {
            build(consumer, new ResourceLocation(namespace, path));
        }

        public void build(Consumer<FinishedRecipe> consumer, ResourceLocation id)
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

    private static class Finished implements FinishedRecipe
    {
        private final ResourceLocation id;
        private final List<ICondition[]> conditions;
        private final List<FinishedRecipe> recipes;
        private final ResourceLocation advId;
        private final ConditionalAdvancement.Builder adv;

        private Finished(ResourceLocation id, List<ICondition[]> conditions, List<FinishedRecipe> recipes, @Nullable ResourceLocation advId, @Nullable ConditionalAdvancement.Builder adv)
        {
            this.id = id;
            this.conditions = conditions;
            this.recipes = recipes;
            this.advId = advId;
            this.adv = adv;
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            JsonArray array = new JsonArray();
            json.add("recipes", array);
            for (int x = 0; x < conditions.size(); x++)
            {
                JsonObject holder = new JsonObject();

                JsonArray conds = new JsonArray();
                for (ICondition c : conditions.get(x))
                    conds.add(CraftingHelper.serialize(c));
                holder.add("conditions", conds);
                holder.add("recipe", recipes.get(x).serializeRecipe());

                array.add(holder);
            }
        }

        @Override
        public ResourceLocation getId() {
            return id;
        }

        @Override
        public RecipeSerializer<?> getType()
        {
            return SERIALZIER;
        }

        @Override
        public JsonObject serializeAdvancement() {
            return adv == null ? null : adv.write();
        }

        @Override
        public ResourceLocation getAdvancementId() {
            return advId;
        }
    }
}
