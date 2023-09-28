/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.recipe.recipebook;
/*
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.client.event.RegisterRecipeBookCategoriesEvent;

import java.util.function.Supplier;

public class RecipeBookExtensionClientHelper {
    public static final Supplier<RecipeBookCategories> TESTING_SEARCH = Suppliers.memoize(() -> RecipeBookCategories.create("TESTING_SEARCH", new ItemStack(Items.COMPASS)));
    public static final Supplier<RecipeBookCategories> TESTING_CAT_1 = Suppliers.memoize(() -> RecipeBookCategories.create("TESTING_CAT_1", new ItemStack(Items.DIAMOND)));
    public static final Supplier<RecipeBookCategories> TESTING_CAT_2 = Suppliers.memoize(() -> RecipeBookCategories.create("TESTING_CAT_2", new ItemStack(Items.NETHERITE_INGOT)));

    public static void init(RegisterRecipeBookCategoriesEvent event) {
        event.registerBookCategories(RecipeBookExtensionTest.TEST_TYPE, ImmutableList.of(TESTING_SEARCH.get(), TESTING_CAT_1.get(), TESTING_CAT_2.get()));
        event.registerAggregateCategory(TESTING_SEARCH.get(), ImmutableList.of(TESTING_CAT_1.get(), TESTING_CAT_2.get()));
        event.registerRecipeCategoryFinder(RecipeBookExtensionTest.RECIPE_BOOK_TEST_RECIPE_TYPE.get(), r -> {
            if (r.getResultItem(Minecraft.getInstance().level.registryAccess()).getItem() == Items.DIAMOND_BLOCK)
                return TESTING_CAT_1.get();
            else return TESTING_CAT_2.get();
        });
    }
}
*/
