/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

import static net.minecraft.client.RecipeBookCategories.*;

public class RecipeBookRegistry
{
    // The ImmutableMap is the patched out value of AGGREGATE_CATEGORIES
    private static final Map<RecipeBookCategories, List<RecipeBookCategories>> MUTABLE_AGGREGATE_CATEGORIES = new HashMap<>(ImmutableMap.of(CRAFTING_SEARCH, ImmutableList.of(CRAFTING_EQUIPMENT, CRAFTING_BUILDING_BLOCKS, CRAFTING_MISC, CRAFTING_REDSTONE), FURNACE_SEARCH, ImmutableList.of(FURNACE_FOOD, FURNACE_BLOCKS, FURNACE_MISC), BLAST_FURNACE_SEARCH, ImmutableList.of(BLAST_FURNACE_BLOCKS, BLAST_FURNACE_MISC), SMOKER_SEARCH, ImmutableList.of(SMOKER_FOOD)));

    private static final Map<RecipeBookType, List<RecipeBookCategories>> TYPE_TO_CATEGORIES = new HashMap<>();
    private static final Map<RecipeType<?>, Function<Recipe<?>, RecipeBookCategories>> FIND_CATEGORIES_FOR_TYPE = new HashMap<>();

    //Unmodifiable view, the nested lists are immutable
    public static final Map<RecipeBookCategories, List<RecipeBookCategories>> AGGREGATE_CATEGORIES_VIEW = Collections.unmodifiableMap(MUTABLE_AGGREGATE_CATEGORIES);
    public static final Map<RecipeBookType, List<RecipeBookCategories>> TYPE_TO_CATEGORIES_VIEW = Collections.unmodifiableMap(TYPE_TO_CATEGORIES);

    public static void addCategoriesToType(RecipeBookType type, List<RecipeBookCategories> categories)
    {
        TYPE_TO_CATEGORIES.put(type, categories instanceof ImmutableList ? categories : Collections.unmodifiableList(categories));
    }

    public static void addAggregateCategories(RecipeBookCategories search, List<RecipeBookCategories> aggregate)
    {
        MUTABLE_AGGREGATE_CATEGORIES.put(search, aggregate instanceof ImmutableList ? aggregate : Collections.unmodifiableList(aggregate));
    }

    public static void addCategoriesFinder(RecipeType<?> type, Function<Recipe<?>, RecipeBookCategories> finder)
    {
        FIND_CATEGORIES_FOR_TYPE.put(type, finder);
    }

    @Nullable
    public static RecipeBookCategories findCategories(RecipeType<?> type, Recipe<?> recipe)
    {
        return Optional.ofNullable(FIND_CATEGORIES_FOR_TYPE.get(type)).map(f -> f.apply(recipe)).orElse(null);
    }
}
