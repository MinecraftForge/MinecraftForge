/*
 * Minecraft Forge
 * Copyright (c) 2016-2022.
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

package net.minecraftforge.debug.recipe.recipebook;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.stats.RecipeBook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.client.RecipeBookRegistry;

import java.util.function.Supplier;

public class RecipeBookExtensionClientHelper
{
    public static final Supplier<RecipeBookCategories> TESTING_SEARCH = Suppliers.memoize(() -> RecipeBookCategories.create("TESTING_SEARCH", new ItemStack(Items.COMPASS)));
    public static final Supplier<RecipeBookCategories> TESTING_CAT_1 = Suppliers.memoize(() -> RecipeBookCategories.create("TESTING_CAT_1", new ItemStack(Items.DIAMOND)));
    public static final Supplier<RecipeBookCategories> TESTING_CAT_2 = Suppliers.memoize(() -> RecipeBookCategories.create("TESTING_CAT_2", new ItemStack(Items.NETHERITE_INGOT)));

    public static void init()
    {
        RecipeBookRegistry.addCategoriesToType(RecipeBookExtensionTest.TEST_TYPE, ImmutableList.of(TESTING_SEARCH.get(), TESTING_CAT_1.get(), TESTING_CAT_2.get()));
        RecipeBookRegistry.addAggregateCategories(TESTING_SEARCH.get(), ImmutableList.of(TESTING_CAT_1.get(), TESTING_CAT_2.get()));
        RecipeBookRegistry.addCategoriesFinder(RecipeBookTestRecipe.TYPE.get(), r ->
        {
            if (r.getResultItem().getItem() == Items.DIAMOND_BLOCK)
                return TESTING_CAT_1.get();
            else return TESTING_CAT_2.get();
        });
    }
}
