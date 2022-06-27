/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Allows users to register custom categories for the vanilla recipe book, making it usable in modded GUIs.
 *
 * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.
 *
 * <p>This event is fired on the {@linkplain FMLJavaModLoadingContext#getModEventBus() mod-specific event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 */
public class RegisterRecipeBookCategoriesEvent extends Event implements IModBusEvent
{
    private final Map<RecipeBookCategories, ImmutableList<RecipeBookCategories>> aggregateCategories;
    private final Map<RecipeBookType, ImmutableList<RecipeBookCategories>> typeCategories;
    private final Map<RecipeType<?>, Function<Recipe<?>, RecipeBookCategories>> recipeCategoryLookups;

    @ApiStatus.Internal
    public RegisterRecipeBookCategoriesEvent(
            Map<RecipeBookCategories, ImmutableList<RecipeBookCategories>> aggregateCategories,
            Map<RecipeBookType, ImmutableList<RecipeBookCategories>> typeCategories,
            Map<RecipeType<?>, Function<Recipe<?>, RecipeBookCategories>> recipeCategoryLookups)
    {
        this.aggregateCategories = aggregateCategories;
        this.typeCategories = typeCategories;
        this.recipeCategoryLookups = recipeCategoryLookups;
    }

    /**
     * Registers the list of categories that compose an aggregate category.
     */
    public void registerAggregateCategory(RecipeBookCategories category, List<RecipeBookCategories> others)
    {
        aggregateCategories.put(category, ImmutableList.copyOf(others));
    }

    /**
     * Registers the list of categories that compose a recipe book.
     */
    public void registerBookCategories(RecipeBookType type, List<RecipeBookCategories> categories)
    {
        typeCategories.put(type, ImmutableList.copyOf(categories));
    }

    /**
     * Registers a category lookup for a certain recipe type.
     */
    public void registerRecipeCategoryFinder(RecipeType<?> type, Function<Recipe<?>, RecipeBookCategories> lookup)
    {
        recipeCategoryLookups.put(type, lookup);
    }
}
