/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.gameplay.crafting;

import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.SimpleCraftingContainer;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.test.BaseTestMod;

@GameTestHolder("forge." + ConditionalRecipeTest.MODID)
@Mod(ConditionalRecipeTest.MODID)
public class ConditionalRecipeTest extends BaseTestMod {
    static final String MODID = "conditional_recipe";

    public ConditionalRecipeTest(FMLConstructModEvent event) {
        super(event);
    }

    @SubscribeEvent
    public void gatherData(GatherDataEvent event) {
        var gen = event.getGenerator();
        gen.addProvider(event.includeServer(), new Recipes(gen.getPackOutput(), event.getLookupProvider()));
    }

    private static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    private static <C extends RecipeInput, T extends Recipe<C>> void assertFalse(GameTestHelper helper, RecipeType<T> type, C container) {
        var recipe = helper.getLevel().getRecipeManager().getRecipeFor(type, container, helper.getLevel());
        helper.assertTrue(recipe.isEmpty(), () -> "Found crafting recipe when unexpected: " + recipe.get().id());
        helper.succeed();
    }

    private static <C extends RecipeInput, T extends Recipe<C>> void assertTrue(GameTestHelper helper, RecipeType<T> type, C container) {
        var recipe = helper.getLevel().getRecipeManager().getRecipeFor(type, container, helper.getLevel());
        helper.assertTrue(recipe.isPresent(), "Did not find crafting recipe when expected!");
        helper.assertTrue(recipe.get().id().getNamespace().equals(MODID), "It wasn't our recipe: " + recipe.get().id());
        helper.succeed();
    }

    @GameTest(template = "forge:empty3x3x3")
    public static void shaped_false_conditions(GameTestHelper helper) {
        assertFalse(helper, RecipeType.CRAFTING, SimpleCraftingContainer.builder()
            .pattern(
                "XXX",
                "XXX",
                "XXX"
            )
            .define('X', Blocks.DIRT)
            .build()
        );
    }

    @GameTest(template = "forge:empty3x3x3")
    public static void shaped_true_conditions(GameTestHelper helper) {
        assertTrue(helper, RecipeType.CRAFTING, SimpleCraftingContainer.builder()
            .pattern(
                "XXX",
                "XXX",
                "XXX"
            )
            .define('X', Blocks.OAK_LOG)
            .build()
        );
    }

    @GameTest(template = "forge:empty3x3x3")
    public static void shapeless_false_conditions(GameTestHelper helper) {
        assertFalse(helper, RecipeType.CRAFTING, SimpleCraftingContainer.builder()
            .pattern("XXX")
            .define('X', Blocks.REDSTONE_ORE)
            .build()
        );
    }

    @GameTest(template = "forge:empty3x3x3")
    public static void shapeless_true_conditions(GameTestHelper helper) {
        assertTrue(helper, RecipeType.CRAFTING, SimpleCraftingContainer.builder()
            .pattern("XXXX")
            .define('X', Blocks.REDSTONE_ORE)
            .build()
        );
    }

    @GameTest(template = "forge:empty3x3x3")
    public static void cooking_false_conditions(GameTestHelper helper) {
        assertFalse(helper, RecipeType.SMELTING, new SingleRecipeInput(new ItemStack(Blocks.DIRT)));
    }

    @GameTest(template = "forge:empty3x3x3")
    public static void cooking_true_conditions(GameTestHelper helper) {
        assertTrue(helper, RecipeType.SMELTING, new SingleRecipeInput(new ItemStack(Blocks.BEE_NEST)));
    }

    @GameTest(template = "forge:empty3x3x3")
    public static void single_item_false_conditions(GameTestHelper helper) {
        assertFalse(helper, RecipeType.STONECUTTING, new SingleRecipeInput(new ItemStack(Blocks.DIRT)));
    }

    @GameTest(template = "forge:empty3x3x3")
    public static void single_item_true_conditions(GameTestHelper helper) {
        assertTrue(helper, RecipeType.STONECUTTING, new SingleRecipeInput(new ItemStack(Blocks.REDSTONE_ORE)));
    }

    @GameTest(template = "forge:empty3x3x3")
    public static void conditional_recipe_choice(GameTestHelper helper) {
        assertFalse(helper, RecipeType.CRAFTING, SimpleCraftingContainer.builder()
            .pattern("XXX")
            .pattern("XX ")
            .define('X', Blocks.DIRT)
            .build()
        );
        assertTrue(helper, RecipeType.CRAFTING, SimpleCraftingContainer.builder()
            .pattern("XXX")
            .pattern("XX ")
            .define('X', Blocks.OAK_LOG)
            .build()
        );
    }

    public static class Recipes extends RecipeProvider implements IConditionBuilder {
        public Recipes(PackOutput gen, CompletableFuture<HolderLookup.Provider> lookup) {
            super(gen, lookup);
        }

        @Override
        protected void buildRecipes(RecipeOutput out) {
            // TODO: Move this to a Condition test instead of using recipe encoders.
            ConditionalRecipe.builder()
                .condition(
                    and (
                        or (
                            not(modLoaded("minecraft")),
                            itemExists("minecraft", "dirt"),
                            TRUE(),
                            tagEmpty(ItemTags.DIRT)
                        ),
                        FALSE()
                    )
                )
                .recipe(
                    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Blocks.DIAMOND_BLOCK, 64)
                        .pattern("XXX")
                        .pattern("XXX")
                        .pattern("XXX")
                        .define('X', Blocks.DIRT)
                        .unlockedBy(getHasName(Blocks.DIRT), has(Blocks.DIRT))
                        ::save
                )
                .save(out, rl("test_encode_all_conditions"));

            ConditionalRecipe.builder()
                .condition(FALSE())
                .recipe(
                    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Blocks.DIAMOND_BLOCK, 64)
                        .pattern("XXX")
                        .pattern("XXX")
                        .pattern("XXX")
                        .define('X', Blocks.DIRT)
                        .unlockedBy(getHasName(Blocks.DIRT), has(Blocks.DIRT))
                        ::save
                )
                .save(out, rl("shaped_false_conditions"));

            ConditionalRecipe.builder()
                .condition(TRUE())
                .recipe(
                    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Blocks.DIAMOND_BLOCK, 64)
                        .pattern("XXX")
                        .pattern("XXX")
                        .pattern("XXX")
                        .define('X', Blocks.OAK_LOG)
                        .unlockedBy(getHasName(Blocks.OAK_LOG), has(Blocks.OAK_LOG))
                        ::save
                )
                .save(out, rl("shaped_true_conditions"));

            ConditionalRecipe.builder()
                .condition(FALSE())
                .recipe(
                    ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Blocks.DIAMOND_BLOCK)
                        .requires(Blocks.REDSTONE_ORE)
                        .requires(Blocks.REDSTONE_ORE)
                        .requires(Blocks.REDSTONE_ORE)
                        .unlockedBy(getHasName(Blocks.REDSTONE_ORE), has(Blocks.REDSTONE_ORE))
                        ::save
                )
                .save(out, rl("shapeless_false_conditions"));

            ConditionalRecipe.builder()
                .condition(TRUE())
                .recipe(
                    ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Blocks.DIAMOND_BLOCK)
                        .requires(Blocks.REDSTONE_ORE)
                        .requires(Blocks.REDSTONE_ORE)
                        .requires(Blocks.REDSTONE_ORE)
                        .requires(Blocks.REDSTONE_ORE)
                        .unlockedBy(getHasName(Blocks.REDSTONE_ORE), has(Blocks.REDSTONE_ORE))
                        ::save
                )
                .save(out, rl("shapeless_true_conditions"));

            ConditionalRecipe.builder()
                .condition(FALSE())
                .recipe(
                    SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.DIRT), RecipeCategory.MISC, Blocks.DIAMOND_BLOCK, 0.1F, 200)
                        .unlockedBy(getHasName(Blocks.DIRT), has(Blocks.DIRT))
                        ::save
                )
                .save(out, rl("cooking_false_conditions"));

            ConditionalRecipe.builder()
                .condition(TRUE())
                .recipe(
                    SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.BEE_NEST), RecipeCategory.MISC, Blocks.DIAMOND_BLOCK, 0.1F, 200)
                        .unlockedBy(getHasName(Blocks.BEE_NEST), has(Blocks.BEE_NEST))
                        ::save
                )
                .save(out, rl("cooking_true_conditions"));

            ConditionalRecipe.builder()
                .condition(FALSE())
                .recipe(
                    SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.DIRT), RecipeCategory.MISC, Blocks.DIAMOND_BLOCK)
                        .unlockedBy(getHasName(Blocks.DIRT), has(Blocks.DIRT))
                        ::save
                )
                .save(out, rl("single_item_false_conditions"));

            ConditionalRecipe.builder()
                .condition(TRUE())
                .recipe(
                    SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.REDSTONE_ORE), RecipeCategory.MISC, Blocks.DIAMOND_BLOCK)
                    .unlockedBy(getHasName(Blocks.REDSTONE_ORE), has(Blocks.REDSTONE_ORE))
                    ::save
                )
                .save(out, rl("singe_item_true_conditions"));

            // TODO SmithingTransformRecipeBuilder
            // TODO SmithingTrimRecipeBuilder


            ConditionalRecipe.builder()
                .condition(FALSE())
                .recipe(
                    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Blocks.DIAMOND_BLOCK, 64)
                        .pattern("XXX")
                        .pattern("XX ")
                        .define('X', Blocks.DIRT)
                        .unlockedBy(getHasName(Blocks.DIRT), has(Blocks.DIRT))
                        ::save
                )
                .condition(TRUE())
                .recipe(
                    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Blocks.DIAMOND_BLOCK, 64)
                        .pattern("XXX")
                        .pattern("XX ")
                        .define('X', Blocks.OAK_LOG)
                        .unlockedBy(getHasName(Blocks.OAK_LOG), has(Blocks.OAK_LOG))
                        ::save
                )
                .save(out, rl("conditional_recipe_choice"));

            ConditionalRecipe.builder()
                .condition(FALSE())
                .recipe(
                    ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Blocks.DIAMOND_BLOCK, 64)
                        .requires(Blocks.DIRT)
                        .unlockedBy(getHasName(Blocks.DIRT), has(Blocks.DIRT))
                        ::save
                )
                .save(out, rl("conditional_doesnt_load_empty"));
        }
    }

}
