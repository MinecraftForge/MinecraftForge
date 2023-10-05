/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.gameplay.crafting;

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
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.SimpleCraftingContainer;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.test.BaseTestMod;

@GameTestHolder("forge." + ConditionalRecipeTest.MODID)
@Mod(ConditionalRecipeTest.MODID)
public class ConditionalRecipeTest extends BaseTestMod {
    static final String MODID = "conditional_recipe";

    @SubscribeEvent
    public void gatherData(GatherDataEvent event) {
        var gen = event.getGenerator();
        gen.addProvider(event.includeServer(), new Recipes(gen.getPackOutput()));
    }

    private static ResourceLocation rl(String path) {
        return new ResourceLocation(MODID, path);
    }

    private static <C extends Container, T extends Recipe<C>> void assertFalse(GameTestHelper helper, RecipeType<T> type, C container) {
        var recipe = helper.getLevel().getRecipeManager().getRecipeFor(type, container, helper.getLevel());
        helper.assertTrue(recipe.isEmpty(), () -> "Found crafting recipe when unexpected: " + recipe.get().id());
        helper.succeed();
    }

    private static <C extends Container, T extends Recipe<C>> void assertTrue(GameTestHelper helper, RecipeType<T> type, C container) {
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
        assertFalse(helper, RecipeType.SMELTING, SimpleCraftingContainer.builder()
            .pattern("X")
            .define('X', Blocks.DIRT)
            .build()
        );
    }

    @GameTest(template = "forge:empty3x3x3")
    public static void cooking_true_conditions(GameTestHelper helper) {
        assertTrue(helper, RecipeType.SMELTING, SimpleCraftingContainer.builder()
            .pattern("X")
            .define('X', Blocks.BEE_NEST)
            .build()
        );
    }

    @GameTest(template = "forge:empty3x3x3")
    public static void single_item_false_conditions(GameTestHelper helper) {
        assertFalse(helper, RecipeType.STONECUTTING, SimpleCraftingContainer.builder()
            .pattern("X")
            .define('X', Blocks.DIRT)
            .build()
        );
    }

    @GameTest(template = "forge:empty3x3x3")
    public static void single_item_true_conditions(GameTestHelper helper) {
        assertTrue(helper, RecipeType.STONECUTTING, SimpleCraftingContainer.builder()
            .pattern("X")
            .define('X', Blocks.REDSTONE_ORE)
            .build()
        );
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
        public Recipes(PackOutput gen) {
            super(gen);
        }

        @Override
        protected void buildRecipes(RecipeOutput out) {
            // TODO: Move this to a Condition test instead of using recipe encoders.
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Blocks.DIAMOND_BLOCK, 64)
                .pattern("XXX")
                .pattern("XXX")
                .pattern("XXX")
                .define('X', Blocks.DIRT)
                .unlockedBy(getHasName(Blocks.DIRT), has(Blocks.DIRT))
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
                .save(out, rl("test_encode_all_conditions"));

            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Blocks.DIAMOND_BLOCK, 64)
                .pattern("XXX")
                .pattern("XXX")
                .pattern("XXX")
                .define('X', Blocks.DIRT)
                .unlockedBy(getHasName(Blocks.DIRT), has(Blocks.DIRT))
                .condition(FALSE())
                .save(out, rl("shaped_false_conditions"));

            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Blocks.DIAMOND_BLOCK, 64)
                .pattern("XXX")
                .pattern("XXX")
                .pattern("XXX")
                .define('X', Blocks.OAK_LOG)
                .unlockedBy(getHasName(Blocks.OAK_LOG), has(Blocks.OAK_LOG))
                .condition(TRUE())
                .save(out, rl("shaped_true_conditions"));

            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Blocks.DIAMOND_BLOCK)
                .requires(Blocks.REDSTONE_ORE)
                .requires(Blocks.REDSTONE_ORE)
                .requires(Blocks.REDSTONE_ORE)
                .unlockedBy(getHasName(Blocks.REDSTONE_ORE), has(Blocks.REDSTONE_ORE))
                .condition(FALSE())
                .save(out, rl("shapeless_false_conditions"));

            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Blocks.DIAMOND_BLOCK)
                .requires(Blocks.REDSTONE_ORE)
                .requires(Blocks.REDSTONE_ORE)
                .requires(Blocks.REDSTONE_ORE)
                .requires(Blocks.REDSTONE_ORE)
                .unlockedBy(getHasName(Blocks.REDSTONE_ORE), has(Blocks.REDSTONE_ORE))
                .condition(TRUE())
                .save(out, rl("shapeless_true_conditions"));

            SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.DIRT), RecipeCategory.MISC, Blocks.DIAMOND_BLOCK, 0.1F, 200)
                .unlockedBy(getHasName(Blocks.DIRT), has(Blocks.DIRT))
                .condition(FALSE())
                .save(out, rl("cooking_false_conditions"));

            SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.BEE_NEST), RecipeCategory.MISC, Blocks.DIAMOND_BLOCK, 0.1F, 200)
                .unlockedBy(getHasName(Blocks.BEE_NEST), has(Blocks.BEE_NEST))
                .condition(TRUE())
                .save(out, rl("cooking_true_conditions"));

            SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.DIRT), RecipeCategory.MISC, Blocks.DIAMOND_BLOCK)
                .unlockedBy(getHasName(Blocks.DIRT), has(Blocks.DIRT))
                .condition(FALSE())
                .save(out, rl("single_item_false_conditions"));

            SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.REDSTONE_ORE), RecipeCategory.MISC, Blocks.DIAMOND_BLOCK)
                .unlockedBy(getHasName(Blocks.REDSTONE_ORE), has(Blocks.REDSTONE_ORE))
                .condition(TRUE())
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


            /*

            // ingredient tests
            // strict NBT match - should match an unnamed iron pickaxe that lost 3 durability
            Ingredient nbtIngredient = StrictNBTIngredient.of(Util.make(() ->
                {
                    ItemStack stack = new ItemStack(Items.IRON_PICKAXE);
                    stack.setDamageValue(3);
                    return stack;
                }));
            ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, Items.GOLDEN_PICKAXE)
                                  .requires(nbtIngredient)
                                  .unlockedBy("has_pickaxe", has(Items.IRON_PICKAXE))
                                  .save(consumer, new ResourceLocation("data_gen_test", "exact_nbt_ingredient"));

            // ingredient tests
            // contains NBT match - should match a stone pickaxe that lost 3 durability, regardless of setting its name
            ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, Items.IRON_PICKAXE)
                                  .requires(PartialNBTIngredient.of(Items.STONE_PICKAXE, Util.make(() ->
                                      {
                                        CompoundTag nbt = new CompoundTag();
                                        nbt.putInt(ItemStack.TAG_DAMAGE, 3);
                                        return nbt;
                                      })))
                                  .unlockedBy("has_pickaxe", has(Items.STONE_PICKAXE))
                                  .save(consumer, new ResourceLocation("data_gen_test", "contains_nbt_ingredient_single_item"));

            // contains NBT match - should match a wood, stone, or iron pickaxe that was named "Diamond Pickaxe", regardless of damage
            ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, Items.DIAMOND_PICKAXE)
                                  .requires(PartialNBTIngredient.of(Util.make(() ->
                                      {
                                          CompoundTag nbt = new CompoundTag();
                                          CompoundTag display = new CompoundTag();
                                          display.putString(ItemStack.TAG_DISPLAY_NAME, "{\"text\":\"Diamond Pickaxe\"}");
                                          nbt.put(ItemStack.TAG_DISPLAY, display);
                                          return nbt;
                                      }), Items.WOODEN_PICKAXE, Items.STONE_PICKAXE, Items.IRON_PICKAXE))
                                  .unlockedBy("has_pickaxe", has(Items.WOODEN_PICKAXE))
                                  .save(consumer, new ResourceLocation("data_gen_test", "contains_nbt_ingredient_item_set"));

            // intersection - should match all non-flammable planks
            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, Blocks.NETHERRACK)
                    .pattern("###")
                    .pattern("###")
                    .pattern(" # ")
                    .define('#', IntersectionIngredient.of(Ingredient.of(ItemTags.PLANKS), Ingredient.of(ItemTags.NON_FLAMMABLE_WOOD)))
                    .unlockedBy("has_planks", has(Items.CRIMSON_PLANKS))
                    .save(consumer, new ResourceLocation("data_gen_test", "intersection_ingredient"));

            // difference - should match all flammable fences
            ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Items.FLINT_AND_STEEL)
                               .pattern(" # ")
                               .pattern("###")
                               .pattern(" # ")
                               .define('#', DifferenceIngredient.of(Ingredient.of(ItemTags.FENCES), Ingredient.of(ItemTags.NON_FLAMMABLE_WOOD)))
                               .unlockedBy("has_fence", has(Items.CRIMSON_FENCE))
                               .save(consumer, new ResourceLocation("data_gen_test", "difference_ingredient"));

            // compound - should match planks, logs, or bedrock
            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, Blocks.DIRT)
                               .pattern("###")
                               .pattern(" # ")
                               .define('#', CompoundIngredient.of(Ingredient.of(ItemTags.PLANKS), Ingredient.of(ItemTags.LOGS), Ingredient.of(Blocks.BEDROCK)))
                               .unlockedBy("has_planks", has(Items.CRIMSON_PLANKS))
                               .save(consumer, new ResourceLocation("data_gen_test", "compound_ingredient_only_vanilla"));

            // compound - should match planks, logs, or a stone pickaxe with 3 damage
            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, Blocks.GOLD_BLOCK)
                               .pattern("#")
                               .pattern("#")
                               .define('#', CompoundIngredient.of(Ingredient.of(ItemTags.PLANKS), Ingredient.of(ItemTags.LOGS), nbtIngredient))
                               .unlockedBy("has_planks", has(Items.CRIMSON_PLANKS))
                               .save(consumer, new ResourceLocation("data_gen_test", "compound_ingredient_custom_types"));
           */
        }
    }

}
