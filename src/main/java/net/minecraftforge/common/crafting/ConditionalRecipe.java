/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.crafting;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;

import net.minecraft.Util;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.crafting.conditions.ConditionCodec;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

/**
 * So, A 'ConditionalRecipe' differs from all normal recipes in the fact that in addition to the conditions
 * disabling the entire recipe, it has sub-recipes that themselves have conditions.
 *
 * And when being deserialized it returns the first entry that passes the conditional check.
 * This basically means that you can have muultiple variants all use the same recipe name, and
 * only one will ever be loaded.
 *
 * This also means that you can wrap ALL recipes in a Conditional even those that don't explicitly
 * have support for them in their data gen.
 */
public class ConditionalRecipe {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private List<RecipePair> recipes = new ArrayList<>();
        private RecipeOutput bouncer = new RecipeOutput() {
            @Override
            public void accept(FinishedRecipe value) {
                recipe(value);
            }

            @SuppressWarnings("removal")
            @Override
            public Advancement.Builder advancement() {
                return Advancement.Builder.recipeAdvancement().parent(RecipeBuilder.ROOT_RECIPE_ADVANCEMENT);
            }
        };

        @Nullable
        private ICondition condition;
        @Nullable
        private ICondition mainCondition;

        @Nullable
        private ResourceLocation advancementId;

        public Builder mainCondition(ICondition value) {
            if (this.mainCondition != null)
                throw new IllegalStateException("Attempted to overrride the main condition, only one is allowed to be set");
            this.mainCondition = value;
            return this;
        }

        public Builder condition(ICondition value) {
            if (this.condition != null)
                throw new IllegalStateException("Attempted to override a previous set condition before adding a recipe");
            this.condition = value;
            return this;
        }

        public Builder recipe(Consumer<RecipeOutput> callable) {
            callable.accept(bouncer);
            return this;
        }

        public Builder recipe(FinishedRecipe recipe) {
            if (condition == null)
                throw new IllegalStateException("Can not add a recipe with no conditions.");
            recipes.add(new RecipePair(this.condition, recipe));
            condition = null;
            return this;
        }

        public Builder advancement(ResourceLocation id) {
            this.advancementId = id;
            return this;
        }

        public void save(RecipeOutput out, String namespace, String path) {
            save(out, new ResourceLocation(namespace, path));
        }

        public void save(RecipeOutput out, ResourceLocation id) {
            if (condition != null)
                throw new IllegalStateException("Invalid ConditionalRecipe builder, Orphaned conditions");

            if (recipes.isEmpty())
                throw new IllegalStateException("Invalid ConditionalRecipe builder, No recipes");

            var adv = ConditionalAdvancement.builder();
            var hasAdvancement = false;
            for (var pair : recipes) {
                if (pair.recipe().advancement() != null) {
                    adv.condition(pair.condition())
                       .advancement(pair.recipe().advancement());
                    hasAdvancement = true;
                }
            }

            if (advancementId == null)
                advancementId = id.withPrefix("recipes/");

            out.accept(new Finished(id, mainCondition, recipes, advancementId, hasAdvancement ? adv.write() : null));
        }
    }

    private record Finished(
        ResourceLocation id,
        @Nullable
        ICondition mainCondition,
        List<RecipePair> recipes,
        ResourceLocation advId,
        JsonObject advData
    ) implements FinishedRecipe {
        @Override
        public void serializeRecipeData(JsonObject json) {
            JsonArray array = new JsonArray();
            var main = mainCondition;
            if (main == null && recipes.size() == 1)
                main = recipes.get(0).condition();

            ForgeHooks.writeCondition(main, json);

            json.add("recipes", array);
            for (var pair : recipes) {
                var holder = pair.recipe().serializeRecipe();
                if (holder.has(ICondition.DEFAULT_FIELD))
                    throw new IllegalStateException("Recipe already serialized conditions!");
                ForgeHooks.writeCondition(pair.condition(), holder);
                array.add(holder);
            }
        }

        @Override
        public ResourceLocation id() {
            return id;
        }

        @Override
        public RecipeSerializer<?> type() {
            return ConditionalRecipe.SERIALZIER;
        }

        @Nullable
        @Override
        public AdvancementHolder advancement() {
            return null;
        }

        @Nullable
        @Override
        public AdvancementData advancementData() {
            return advData == null ? null : new AdvancementData(advId, advData);
        }
    }

    private record RecipePair(ICondition condition, FinishedRecipe recipe) {}

    private static Codec<Recipe<?>> CODEC = new Codec<Recipe<?>>() {
        @Override
        public <T> DataResult<T> encode(Recipe<?> input, DynamicOps<T> ops, T prefix) {
            throw new UnsupportedOperationException("ConditionRecipe.CODEC does not support encoding");
        }

        @Override
        public <T> DataResult<Pair<Recipe<?>, T>> decode(DynamicOps<T> ops, T input) {
            var context = ConditionCodec.getContext(ops);
            var json = new Dynamic<>(ops, input).convert(JsonOps.INSTANCE).getValue();
            try {
                var recipes = GsonHelper.getAsJsonArray(GsonHelper.convertToJsonObject(json.getAsJsonObject(), "root"), "recipes");
                int idx = 0;
                for (var entry : recipes) {
                    var object = GsonHelper.convertToJsonObject(entry, "recipe[" + idx++ + "]");
                    var conditionjson = GsonHelper.getAsJsonObject(object, ICondition.DEFAULT_FIELD);
                    var condition = Util.getOrThrow(ICondition.SAFE_CODEC.parse(JsonOps.INSTANCE, conditionjson), JsonSyntaxException::new);
                    if (!condition.test(context))
                        continue;
                    var type = new ResourceLocation(GsonHelper.getAsString(object, "type"));
                    var serializer = ForgeRegistries.RECIPE_SERIALIZERS.getValue(type);
                    if (serializer == null)
                        return DataResult.error(() -> "Invalid or unsupported recipe type '" + type + "' Conditions were successful, but unknown type");
                    var parsed = serializer.codec().parse(JsonOps.INSTANCE, object);
                    if (parsed.error().isPresent())
                        return DataResult.error(parsed.error().get()::message);
                    else if (!parsed.result().isPresent())
                        return DataResult.error(() -> "Recipe passed all conditions but did not parse a valid return");
                    else
                        return DataResult.success(Pair.of(parsed.result().get(), ops.empty()));
                }
                return DataResult.error(() -> "No recipe passed conditions, if this is the case, you should have an outer condition.");
            } catch (JsonSyntaxException e) {
                return DataResult.error(() -> e.getMessage());
            }
        }
    }.stable();

    public static final RecipeSerializer<Recipe<?>> SERIALZIER = new RecipeSerializer<Recipe<?>>() {
        @Override
        public Codec<Recipe<?>> codec() {
            return CODEC;
        }

        @Override
        public Recipe<?> fromNetwork(FriendlyByteBuf buffer) {
            throw new UnsupportedOperationException("ConditionaRecipe.SERIALIZER does not support decoding from network");
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, Recipe<?> recipe) {
            throw new UnsupportedOperationException("ConditionaRecipe.SERIALIZER does not support encoding to network");
        }
    };
}
