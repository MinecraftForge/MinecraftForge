/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.crafting;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapDecoder;
import com.mojang.serialization.MapEncoder;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ConditionCodec;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.ICondition.IContext;
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
        private final List<InnerRecipe> recipes = new ArrayList<>();
        private final List<InnerAdvancement> advancements = new ArrayList<>();

        private final RecipeOutput bouncer = new RecipeOutput() {
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
                    Advancement adv = Advancement.CODEC.parse(JsonOps.INSTANCE, advancement).getOrThrow(JsonParseException::new);
                    holder = new AdvancementHolder(advancementId, adv);
                }
                accept(id, recipe, holder);
            }

            @Override
            public Provider registry() {
                return null;
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
                advancements.add(new InnerAdvancement(this.condition, advancement, null));
            condition = null;
            return this;
        }

        public Builder advancement(ResourceLocation id) {
            this.advancementId = id;
            return this;
        }

        public void save(RecipeOutput out, String namespace, String path) {
            save(out, ResourceLocation.fromNamespaceAndPath(namespace, path));
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
                    adv.condition(data.condition());
                    if (data.json != null)
                        adv.advancement(data.json());
                    else
                       adv.advancement(data.advancement());
                }
                if (advancementId == null)
                    advancementId = id.withPrefix("recipes/");
                advancement = adv.build(out.registry());
            } else {
                advancementId = null;
            }

            out.accept(id, new Wrapper(mainCondition, recipes), advancementId, advancement);
        }
    }

    private record InnerRecipe(ICondition condition, Recipe<?> recipe) {}
    private record InnerAdvancement(ICondition condition, AdvancementHolder advancement, JsonObject json) {}

    private static class Wrapper implements Recipe<CraftingInput> {
        @Override public boolean matches(CraftingInput inv, Level level) { return false; }
        @Override public ItemStack assemble(CraftingInput inv, HolderLookup.Provider reg) { return null; }
        @Override public boolean canCraftInDimensions(int width, int height) { return false; }
        @Override public ItemStack getResultItem(HolderLookup.Provider reg) { return null; }
        @Override public RecipeSerializer<?> getSerializer() { return ConditionalRecipe.SERIALZIER; }
        @Override public RecipeType<?> getType() { throw new UnsupportedOperationException(); }

        @Nullable private final ICondition main;
        private final List<InnerRecipe> recipes;
        private Wrapper(@Nullable ICondition main, List<InnerRecipe> recipes) {
            this.main = main;
            this.recipes = recipes;
        }

    }
    private static final MapCodec<Recipe<?>> CODEC = Codec.of(new MapEncoder.Implementation<>() {
        @Override
        public <T> RecordBuilder<T> encode(Recipe<?> input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
            if (!(input instanceof Wrapper))
                new IllegalStateException("ConditionalRecipe.CODEC can only be used during data gen, how did you get here?");
            Wrapper wrapper = (Wrapper)input;

            if (wrapper.main != null)
                prefix.add(ICondition.DEFAULT_FIELD, ICondition.CODEC.encodeStart(ops, wrapper.main));
            else if (wrapper.recipes.size() == 1)
                prefix.add(ICondition.DEFAULT_FIELD, ICondition.CODEC.encodeStart(ops, wrapper.recipes.get(0).condition()));

            var recipes = ops.listBuilder();
            for (var recipe : wrapper.recipes) {
                var map = ops.mapBuilder();
                if (wrapper.main != null || wrapper.recipes.size() != 1)
                    map.add(ICondition.DEFAULT_FIELD, recipe.condition(), ICondition.CODEC);
                map.add("recipe", recipe.recipe(), Recipe.CODEC);
                recipes.add(map.build(ops.emptyMap()));
            }
            prefix.add("recipes", recipes.build(ops.emptyList()));
            return prefix;
        }

        @Override
        public <T> Stream<T> keys(DynamicOps<T> ops) {
            return Stream.of(ops.createString(ICondition.DEFAULT_FIELD), ops.createString("recipes"));
        }
    }, new MapDecoder.Implementation<Recipe<?>>() {
        @Override
        public <T> DataResult<Recipe<?>> decode(DynamicOps<T> ops, MapLike<T> input) {
            var context = ConditionCodec.getContext(ops);
            return ops.getStream(input.get("recipes")).flatMap(stream -> {
                var count = new Holder<Integer>();
                count.value = -1;
                var ret = stream.map(entry -> accept(context, ops, count, entry))
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(null);

                if (ret != null)
                    return ret;

                return DataResult.error(() -> "No recipe passed conditions, if this is the case, you should have an outer condition.");
            });
        }

        private <T> DataResult<Recipe<?>> accept(IContext context, DynamicOps<T> ops, Holder<Integer> count, T entry) {
            count.value = count.value + 1;
            var map = ops.getMap(entry).result().orElse(null);
            if (map == null)
                return DataResult.error(() -> "Entry " + count.value + " was not MapLike " + entry.getClass());

            if (map.get(ICondition.DEFAULT_FIELD) != null) {
                var parsed = ICondition.SAFE_CODEC.parse(ops, (T)map.get(ICondition.DEFAULT_FIELD));
                if (parsed.result().isPresent()) {
                    var condition = parsed.result().get();
                    if (!condition.test(context, ops))
                        return null;
                }
            }

            var recipe = map.get("recipe");
            if (recipe == null)
                return DataResult.error(() -> "Missing `recipe` entry " + count.value);

            var ret = Recipe.CODEC.parse(ops, (T)recipe);
            return ret;
        }

        @Override
        public <T> Stream<T> keys(DynamicOps<T> ops) {
            return Stream.of(ops.createString(ICondition.DEFAULT_FIELD), ops.createString("recipes"));
        }
    });

    public static final RecipeSerializer<Recipe<?>> SERIALZIER = new RecipeSerializer<Recipe<?>>() {
        @Override
        public MapCodec<Recipe<?>> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, Recipe<?>> streamCodec() {
            throw new UnsupportedOperationException("ConditionaRecipe.SERIALIZER does not support encoding to network");
        }
    };

    private static final class Holder<T> {
        private T value;
    }
}
