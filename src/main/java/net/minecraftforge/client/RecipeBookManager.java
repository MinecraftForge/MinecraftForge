/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client;

import net.minecraft.client.RecipeBookCategories;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.client.event.RegisterRecipeBookCategoriesEvent;
import net.minecraftforge.fml.ModLoader;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Manager for {@link RecipeBookType recipe book types} and {@link RecipeBookCategories categories}.
 * <p>
 * Provides a recipe category lookup.
 */
public final class RecipeBookManager {
    // Not using ConcurrentHashMap here because it's slower for lookups, so we only use it during init
    private static final Map<RecipeBookCategories, List<RecipeBookCategories>> AGGREGATE_CATEGORIES = new HashMap<>();
    private static final Map<RecipeBookType, List<RecipeBookCategories>> TYPE_CATEGORIES = new HashMap<>();
    private static final Map<RecipeType<?>, Function<Recipe<?>, RecipeBookCategories>> RECIPE_CATEGORY_LOOKUPS = new HashMap<>();
    private static final Map<RecipeBookCategories, List<RecipeBookCategories>> AGGREGATE_CATEGORIES_VIEW = Collections.unmodifiableMap(AGGREGATE_CATEGORIES);

    /**
     * Finds the category the specified recipe should display in, or null if none.
     */
    @Nullable
    public static <T extends Recipe<?>> RecipeBookCategories findCategories(RecipeType<T> type, RecipeHolder<T> recipe) {
        var lookup = RECIPE_CATEGORY_LOOKUPS.get(type);
        return lookup != null ? lookup.apply(recipe.value()) : null;
    }

    @ApiStatus.Internal
    public static Map<RecipeBookCategories, List<RecipeBookCategories>> getAggregateCategories(Map<RecipeBookCategories, List<RecipeBookCategories>> vanilla) {
        return AGGREGATE_CATEGORIES_VIEW;
    }

    @ApiStatus.Internal
    public static List<RecipeBookCategories> getCustomCategoriesOrEmpty(RecipeBookType recipeBookType) {
        return TYPE_CATEGORIES.getOrDefault(recipeBookType, List.of());
    }

    @ApiStatus.Internal
    public static void init() {
        // The ImmutableMap is the patched out value of AGGREGATE_CATEGORIES
        var aggregateCategories = new HashMap<>(Map.of(
            RecipeBookCategories.CRAFTING_SEARCH, List.of(RecipeBookCategories.CRAFTING_EQUIPMENT, RecipeBookCategories.CRAFTING_BUILDING_BLOCKS, RecipeBookCategories.CRAFTING_MISC, RecipeBookCategories.CRAFTING_REDSTONE),
            RecipeBookCategories.FURNACE_SEARCH, List.of(RecipeBookCategories.FURNACE_FOOD, RecipeBookCategories.FURNACE_BLOCKS, RecipeBookCategories.FURNACE_MISC),
            RecipeBookCategories.BLAST_FURNACE_SEARCH, List.of(RecipeBookCategories.BLAST_FURNACE_BLOCKS, RecipeBookCategories.BLAST_FURNACE_MISC),
            RecipeBookCategories.SMOKER_SEARCH, List.of(RecipeBookCategories.SMOKER_FOOD)
        ));

        var typeCategories = new HashMap<RecipeBookType, List<RecipeBookCategories>>();
        var recipeCategoryLookups = new HashMap<RecipeType<?>, Function<Recipe<?>, RecipeBookCategories>>();
        var event = new RegisterRecipeBookCategoriesEvent(aggregateCategories, typeCategories, recipeCategoryLookups);
        ModLoader.get().postEventWrapContainerInModOrder(event);
        AGGREGATE_CATEGORIES.putAll(aggregateCategories);
        TYPE_CATEGORIES.putAll(typeCategories);
        RECIPE_CATEGORY_LOOKUPS.putAll(recipeCategoryLookups);
    }
}
