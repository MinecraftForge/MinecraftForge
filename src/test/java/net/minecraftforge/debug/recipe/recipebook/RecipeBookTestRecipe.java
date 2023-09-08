/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.recipe.recipebook;

/*
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.debug.recipe.recipebook.RecipeBookTestRecipe.Ingredients;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jetbrains.annotations.Nullable;

public class RecipeBookTestRecipe implements Recipe<RecipeBookExtensionTest.RecipeBookTestContainer> {
    public final Ingredients ingredients;
    private final int width;
    private final int height;
    private final NonNullList<Ingredient> items;

    public RecipeBookTestRecipe(Ingredients ingredients) {
        this.ingredients = ingredients;
        this.width = ingredients.pattern.get(0).length();
        this.height = ingredients.pattern.size();
        List<String> pattern = new ArrayList<>(ingredients.pattern); //might need to reverse this list.
        while (pattern.size() != 4)
            pattern.add("  ");
        this.items = pattern.stream()
                .flatMap(s -> Stream.of(s.substring(0,1), s.substring(1, 2)))
                .map(s -> s.equals(" ") ? Ingredient.EMPTY : ingredients.recipe.get(s))
                .peek(i -> Objects.requireNonNull(i, "A key in sculpting pattern was not defined!"))
                .collect(Collectors.toCollection(NonNullList::create));
    }

    @Override
    public boolean matches(RecipeBookExtensionTest.RecipeBookTestContainer container, Level level) {
        for (int i = 0; i <= 2 - this.width; ++i) {
            for (int j = 0; j <= 4 - this.height; ++j) {
                if (this.matches(container, i, j, true) || this.matches(container, i, j, false))
                    return true;
            }
        }
        return false;
    }

    private boolean matches(RecipeBookExtensionTest.RecipeBookTestContainer container, int x, int y, boolean mirror) {
        for (int i = 0; i < 2; ++i) {
            for (int j = 0; j < 4; ++j) {
                int curX = i - x;
                int curY = j - y;
                Ingredient ingredient = Ingredient.EMPTY;

                if (curX >= 0 && curY >= 0 && curX < this.width && curY < this.height) {
                    int idx = mirror ? this.width - curX - 1 + curY * this.width : curX + curY * this.width;
                    ingredient = this.items.get(idx);
                }

                if (!ingredient.test(container.getItem(i + j * 2)))
                    return false;
            }
        }

        return true;
    }

    @Override
    public ItemStack assemble(RecipeBookExtensionTest.RecipeBookTestContainer p_44001_, RegistryAccess registryAccess) {
        return this.getResultItem(registryAccess).copy();
    }

    @Override
    public boolean canCraftInDimensions(int p_43999_, int p_44000_) {
        return this.width <= p_43999_ && this.height <= p_44000_; //used for recipe book
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return this.ingredients.result();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeBookExtensionTest.RECIPE_BOOK_TEST_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeBookExtensionTest.RECIPE_BOOK_TEST_RECIPE_TYPE.get();
    }

    @Override
    public String getGroup() {
        return this.ingredients.group;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.items;
    }

    @Override
    public boolean isIncomplete() {
        return this.getIngredients().isEmpty() ||
                this.getIngredients().stream()
                        .filter((ingredient) -> !ingredient.isEmpty())
                        .anyMatch(ForgeHooks::hasNoElements);
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(Items.NETHERITE_BLOCK);
    }

    public record Ingredients(String group, List<String> pattern, Map<String, Ingredient> recipe, ItemStack result) {
        private static final Function<String, DataResult<String>> VERIFY_LENGTH_1 =
                s -> s.length() == 1 ? DataResult.success(s) : DataResult.error(() -> "Key must be a single character!");

        private static final Function<String, DataResult<String>> VERIFY_LENGTH_2 =
                s -> s.length() == 2 ? DataResult.success(s) : DataResult.error(() -> "Key row length must be of 2!");

        private static final Function<List<String>, DataResult<List<String>>> VERIFY_SIZE = l -> {
            if (l.size() <= 4 && l.size() >= 1) {
                List<String> temp = new ArrayList<>(l);
                Collections.reverse(temp); //reverse so the first row is at the bottom in the json.
                return DataResult.success(ImmutableList.copyOf(temp));
            }
            return DataResult.error(() -> "Pattern must have between 1 and 4 rows of keys");
        };

        public static final Codec<Ingredient> INGREDIENT_CODEC = Codec.PASSTHROUGH.comapFlatMap(obj -> {
            JsonElement json = obj.convert(JsonOps.INSTANCE).getValue();
            try {
                return DataResult.success(Ingredient.fromJson(json));
            } catch (Exception e) {
                return DataResult.error(() -> "Failed to parse ingredient: " + e.getMessage());
            }
        }, ingredient -> new Dynamic<>(JsonOps.INSTANCE, ingredient.toJson()));

        public static final Codec<Ingredients> CODEC = RecordCodecBuilder.create(inst ->
                inst.group(
                        Codec.STRING.fieldOf("group").forGetter(Ingredients::group),
                        Codec.STRING.flatXmap(VERIFY_LENGTH_2, VERIFY_LENGTH_2).listOf().flatXmap(VERIFY_SIZE, VERIFY_SIZE).fieldOf("pattern").forGetter(Ingredients::pattern),
                        Codec.unboundedMap(Codec.STRING.flatXmap(VERIFY_LENGTH_1, VERIFY_LENGTH_1), INGREDIENT_CODEC).fieldOf("key").forGetter(Ingredients::recipe),
                        ItemStack.CODEC.fieldOf("result").forGetter(Ingredients::result)
                ).apply(inst, Ingredients::new)
        );
    }

    public static class Serializer implements RecipeSerializer<RecipeBookTestRecipe> {
        @Override
        public void toNetwork(FriendlyByteBuf buffer, RecipeBookTestRecipe recipe) {
            buffer.writeWithCodec(NbtOps.INSTANCE, Ingredients.CODEC, recipe.ingredients);
        }

        @Override
        public Codec<RecipeBookTestRecipe> codec() {
            return CODEC;
        }

        @Override
        public @Nullable RecipeBookTestRecipe fromNetwork(FriendlyByteBuf buf) {
            Ingredients ingredients = buf.readWithCodec(NbtOps.INSTANCE, Ingredients.CODEC);
            return new RecipeBookTestRecipe(ingredients);
        }
    }
}
*/