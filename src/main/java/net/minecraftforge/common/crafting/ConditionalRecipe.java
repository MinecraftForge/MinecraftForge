/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.crafting;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapDecoder;
import com.mojang.serialization.MapEncoder;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;

import net.minecraft.Util;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
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
        private List<InnerRecipe> recipes = new ArrayList<>();
        private List<InnerAdvancement> advancements = new ArrayList<>();

        private RecipeOutput bouncer = new RecipeOutput() {
            @Override
            public void accept(ResourceLocation id, Recipe<?> value, @Nullable AdvancementHolder advancement) {
                recipe(id, value, advancement);
            }

            @SuppressWarnings("removal")
            @Override
            public Advancement.Builder advancement() {
                return Advancement.Builder.recipeAdvancement().parent(RecipeBuilder.ROOT_RECIPE_ADVANCEMENT);
            }

            @Override
            public void accept(ResourceLocation id, Recipe<?> recipe, ResourceLocation advancementId, JsonElement advancement) {
                AdvancementHolder holder = null;
                if (advancement != null) {
                    Advancement adv = Util.getOrThrow(Advancement.CODEC.parse(JsonOps.INSTANCE, advancement), JsonParseException::new);
                    holder = new AdvancementHolder(advancementId, adv);
                }
                accept(id, recipe, holder);
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

        public Builder recipe(ResourceLocation id, Recipe<?> recipe, @Nullable AdvancementHolder advancement) {
            if (condition == null)
                throw new IllegalStateException("Can not add a recipe with no conditions.");
            recipes.add(new InnerRecipe(this.condition, recipe));
            if (advancement != null)
                advancements.add(new InnerAdvancement(this.condition, advancement));
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

            JsonElement advancement = null;
            if (!advancements.isEmpty()) {
                var adv = ConditionalAdvancement.builder();
                for (var data : advancements) {
                    adv.condition(data.condition())
                       .advancement(data.advancement());
                }
                if (advancementId == null)
                    advancementId = id.withPrefix("recipes/");
                advancement = adv.build();
            } else {
                advancementId = null;
            }

            out.accept(id, new Wrapper(mainCondition, recipes), advancementId, advancement);
        }
    }

    private record InnerRecipe(ICondition condition, Recipe<?> recipe) {}
    private record InnerAdvancement(ICondition condition, AdvancementHolder advancement) {}

    private static class Wrapper implements Recipe<Container> {
        @Override public boolean matches(Container inv, Level level) { return false; }
        @Override public ItemStack assemble(Container inv, RegistryAccess reg) { return null; }
        @Override public boolean canCraftInDimensions(int width, int height) { return false; }
        @Override public ItemStack getResultItem(RegistryAccess reg) { return null; }
        @Override public RecipeSerializer<?> getSerializer() { return ConditionalRecipe.SERIALZIER; }
        @Override public RecipeType<?> getType() { throw new UnsupportedOperationException(); }

        @Nullable private final ICondition main;
        private final List<InnerRecipe> recipes;
        private Wrapper(@Nullable ICondition main, List<InnerRecipe> recipes) {
            this.main = main;
            this.recipes = recipes;
        }

    }
    private static final Codec<Recipe<?>> CODEC = Codec.of(new MapEncoder.Implementation<>() {
        @Override
        public <T> RecordBuilder<T> encode(Recipe<?> input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
            if (!(input instanceof Wrapper))
                new IllegalStateException("ConditionalRecipe.CODEC can only be used during data gen, how did you get here?");
            Wrapper wrapper = (Wrapper)input;

            if (wrapper.main != null)
                prefix.add(ICondition.DEFAULT_FIELD, ICondition.CODEC.encodeStart(ops, wrapper.main));
            else if (wrapper.recipes.size() == 1)
                prefix.add(ICondition.DEFAULT_FIELD, ICondition.CODEC.encodeStart(ops, wrapper.recipes.get(0).condition()));

            var recipes = new ArrayList<T>();
            for (var recipe : wrapper.recipes) {
                var json = (JsonObject)json(Recipe.CODEC, recipe.recipe());
                if (wrapper.recipes.size() > 1)
                    json.add(ICondition.DEFAULT_FIELD, json(ICondition.CODEC, recipe.condition()));
                recipes.add(JsonOps.INSTANCE.convertTo(ops, json));
            }
            prefix.add("recipes", ops.createList(recipes.stream()));
            return prefix;
        }

        @Override
        public <T> Stream<T> keys(DynamicOps<T> ops) {
            return Stream.of(ops.createString(ICondition.DEFAULT_FIELD), ops.createString("recipes"));
        }


        private static <T> JsonElement json(Codec<T> codec, T object) {
            return Util.getOrThrow(codec.encodeStart(JsonOps.INSTANCE, object), IllegalStateException::new);
        }
    }, new MapDecoder.Implementation<Recipe<?>>() {
        @Override
        public <T> DataResult<Recipe<?>> decode(DynamicOps<T> ops, MapLike<T> input) {
            var context = ConditionCodec.getContext(ops);
            var json = ops.convertTo(JsonOps.INSTANCE, input.get("recipes"));
            try {
                var recipes = GsonHelper.convertToJsonArray(json, "recipes");
                int idx = 0;
                for (var entry : recipes) {
                    var object = GsonHelper.convertToJsonObject(entry, "recipe[" + idx++ + "]");
                    if (object.has(ICondition.DEFAULT_FIELD)) {
                        var conditionjson = GsonHelper.getAsJsonObject(object, ICondition.DEFAULT_FIELD);
                        var condition = Util.getOrThrow(ICondition.SAFE_CODEC.parse(JsonOps.INSTANCE, conditionjson), JsonSyntaxException::new);
                        if (!condition.test(context))
                            continue;
                    }
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
                        return DataResult.success(parsed.result().get());
                }
                return DataResult.error(() -> "No recipe passed conditions, if this is the case, you should have an outer condition.");
            } catch (JsonSyntaxException e) {
                return DataResult.error(() -> e.getMessage());
            }
        }

        @Override
        public <T> Stream<T> keys(DynamicOps<T> ops) {
            return Stream.of(ops.createString(ICondition.DEFAULT_FIELD), ops.createString("recipes"));
        }
    }).codec();

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
