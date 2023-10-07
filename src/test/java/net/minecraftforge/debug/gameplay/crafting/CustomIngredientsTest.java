/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.gameplay.crafting;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.SimpleCraftingContainer;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.common.crafting.ingredients.IIngredientBuilder;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.util.INBTBuilder;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.test.BaseTestMod;

@GameTestHolder("forge." + CustomIngredientsTest.MODID)
@Mod(CustomIngredientsTest.MODID)
public class CustomIngredientsTest extends BaseTestMod implements INBTBuilder {
    static final String MODID = "custom_ingredients";
    private static final int TEST_DAMAGE = 3;
    private static final String TEST_DISPLAY_NAME = "{\"text\":\"Test Item\"}";
    private static final TagKey<Item> LEFT = tag("left");
    private static final TagKey<Item> RIGHT = tag("right");

    @SubscribeEvent
    public void gatherData(GatherDataEvent event) {
        var gen = event.getGenerator();
        var out = gen.getPackOutput();
        var look = event.getLookupProvider();
        var exist = event.getExistingFileHelper();

        var blockTags = new BlockTagsGen(out, look, exist);
        gen.addProvider(event.includeServer(), blockTags);
        gen.addProvider(event.includeServer(), new ItemTagsGen(out, look, blockTags, exist));
        gen.addProvider(event.includeServer(), new Recipes(out));
    }

    private static ResourceLocation rl(String path) {
        return new ResourceLocation(MODID, path);
    }

    private static TagKey<Item> tag(String name) {
        return ItemTags.create(rl(name));
    }

    private static <C extends Container, T extends Recipe<C>> void assertRecipeMiss(GameTestHelper helper, RecipeType<T> type, C container) {
        var recipe = helper.getLevel().getRecipeManager().getRecipeFor(type, container, helper.getLevel());
        helper.assertTrue(recipe.isEmpty(), () -> "Found crafting recipe when unexpected: " + recipe.get().id());
        helper.succeed();
    }

    private static <C extends Container, T extends Recipe<C>> void assertRecipeMatch(GameTestHelper helper, RecipeType<T> type, C container, String name) {
        var recipe = helper.getLevel().getRecipeManager().getRecipeFor(type, container, helper.getLevel());
        helper.assertTrue(recipe.isPresent(), "Did not find crafting recipe when expected!");
        helper.assertTrue(recipe.get().id().getNamespace().equals(MODID), "It wasn't our recipe: " + recipe.get().id());
        helper.assertTrue(recipe.get().id().getPath().equals(name), "It wasn't the correct recipe expected: " + name  + " got: " + recipe.get().id());
        helper.succeed();
    }


    private static CompoundTag named() {
        return CompoundTag.builder()
            .put(ItemStack.TAG_DISPLAY_NAME, TEST_DISPLAY_NAME)
            .build();
    }

    private static CompoundTag damaged() {
        return CompoundTag.builder()
            .putInt(ItemStack.TAG_DAMAGE, TEST_DAMAGE)
            .build();
    }

    private static ItemStack damaged(ItemLike item) {
        return tagged(item, damaged());
    }

    private static ItemStack named(ItemLike item) {
        return tagged(item, named());
    }

    public static ItemStack stack(ItemLike item) {
        return new ItemStack(item);
    }

    private static ItemStack tagged(ItemLike item, CompoundTag tag) {
        var ret = stack(item);
        ret.setTag(tag.copy());
        return ret;
    }

    @GameTest(template = "forge:empty3x3x3")
    public static void partial_nbt(GameTestHelper helper) {
        var both = named().merge(damaged());
        var wood = stack(Items.WOODEN_PICKAXE);
        var wood_tag = tagged(Items.WOODEN_PICKAXE, both);
        var stone = stack(Items.STONE_PICKAXE);
        var stone_tag = tagged(Items.STONE_PICKAXE, both);
        var iron = stack(Items.IRON_PICKAXE);
        var iron_tag = tagged(Items.IRON_PICKAXE, both);
        var diamond = stack(Items.DIAMOND_PICKAXE);
        var diamond_tag = tagged(Items.DIAMOND_PICKAXE, both);
        Function<ItemStack, CraftingContainer> container = (stack) ->
            SimpleCraftingContainer.builder()
                .pattern("XXX")
                .pattern("XXX")
                .define('X', stack)
                .build();

        assertRecipeMiss(helper, RecipeType.CRAFTING, container.apply(wood));
        assertRecipeMatch(helper, RecipeType.CRAFTING, container.apply(wood_tag), "partial_nbt_damage_only");
        assertRecipeMiss(helper, RecipeType.CRAFTING, container.apply(stone));
        assertRecipeMatch(helper, RecipeType.CRAFTING, container.apply(stone_tag), "partial_nbt_damage_only");
        assertRecipeMiss(helper, RecipeType.CRAFTING, container.apply(iron));
        assertRecipeMatch(helper, RecipeType.CRAFTING, container.apply(iron_tag), "partial_nbt_damage_only");
        assertRecipeMiss(helper, RecipeType.CRAFTING, container.apply(diamond));
        assertRecipeMiss(helper, RecipeType.CRAFTING, container.apply(diamond_tag));

        container = (stack) ->
            SimpleCraftingContainer.builder()
                .pattern("XXX")
                .define('X', stack)
                .build();

        assertRecipeMiss(helper, RecipeType.CRAFTING, container.apply(wood));
        assertRecipeMatch(helper, RecipeType.CRAFTING, container.apply(wood_tag), "partial_nbt_name_only");
        assertRecipeMiss(helper, RecipeType.CRAFTING, container.apply(stone));
        assertRecipeMatch(helper, RecipeType.CRAFTING, container.apply(stone_tag), "partial_nbt_name_only");
        assertRecipeMiss(helper, RecipeType.CRAFTING, container.apply(iron));
        assertRecipeMatch(helper, RecipeType.CRAFTING, container.apply(iron_tag), "partial_nbt_name_only");
        assertRecipeMiss(helper, RecipeType.CRAFTING, container.apply(diamond));
        assertRecipeMiss(helper, RecipeType.CRAFTING, container.apply(diamond_tag));
    }

    @GameTest(template = "forge:empty3x3x3")
    public static void compound(GameTestHelper helper) {
        Function<ItemStack, CraftingContainer> container = (stack) ->
            SimpleCraftingContainer.builder()
                .pattern("XXX")
                .pattern(" X ")
                .define('X', stack)
                .build();

        assertRecipeMiss(helper, RecipeType.CRAFTING, container.apply(stack(Items.DIRT)));
        assertRecipeMatch(helper, RecipeType.CRAFTING, container.apply(named(Items.DIRT)), "compound_ingredient");
        assertRecipeMiss(helper, RecipeType.CRAFTING, container.apply(stack(Items.OAK_LOG)));
        assertRecipeMatch(helper, RecipeType.CRAFTING, container.apply(named(Items.OAK_LOG)), "compound_ingredient");
        assertRecipeMiss(helper, RecipeType.CRAFTING, container.apply(stack(Items.REDSTONE)));
    }

    @GameTest(template = "forge:empty3x3x3")
    public static void strict(GameTestHelper helper) {
        Function<ItemStack, CraftingContainer> container = (stack) ->
            SimpleCraftingContainer.builder()
                .pattern("XXX")
                .define('X', stack)
                .build();

        assertRecipeMiss(helper, RecipeType.CRAFTING, container.apply(stack(Items.IRON_PICKAXE)));
        assertRecipeMatch(helper, RecipeType.CRAFTING, container.apply(damaged(Items.IRON_PICKAXE)), "strict_nbt_ingredient");
        assertRecipeMiss(helper, RecipeType.CRAFTING, container.apply(damaged(Items.STONE_PICKAXE)));
    }

    @GameTest(template = "forge:empty3x3x3")
    public static void intersection(GameTestHelper helper) {
        Function<ItemStack, CraftingContainer> container = (stack) ->
            SimpleCraftingContainer.builder()
                .pattern("XXX")
                .pattern("XXX")
                .pattern(" X ")
                .define('X', stack)
                .build();

        assertRecipeMiss(helper, RecipeType.CRAFTING, container.apply(stack(Items.DIRT)));
        assertRecipeMatch(helper, RecipeType.CRAFTING, container.apply(stack(Items.STONE)), "intersection_ingredient");
        assertRecipeMiss(helper, RecipeType.CRAFTING, container.apply(stack(Items.GRAVEL)));
    }


    @GameTest(template = "forge:empty3x3x3")
    public static void difference(GameTestHelper helper) {
        Function<ItemStack, CraftingContainer> container = (stack) ->
            SimpleCraftingContainer.builder()
                .pattern(" X ")
                .pattern("XXX")
                .pattern(" X ")
                .define('X', stack)
                .build();

        assertRecipeMatch(helper, RecipeType.CRAFTING, container.apply(stack(Items.DIRT)), "difference_ingredient");
        assertRecipeMiss(helper, RecipeType.CRAFTING, container.apply(stack(Items.STONE)));
    }

    private static class BlockTagsGen extends BlockTagsProvider {
        public BlockTagsGen(PackOutput out, CompletableFuture<HolderLookup.Provider> look, @Nullable ExistingFileHelper exist) {
            super(out, look, MODID, exist);
        }

        @Override
        public void addTags(HolderLookup.Provider lookup) { }
    }

    private static class ItemTagsGen extends ItemTagsProvider {
        public ItemTagsGen(PackOutput out, CompletableFuture<HolderLookup.Provider> lookup, BlockTagsProvider blocks, ExistingFileHelper existing) {
            super(out, lookup, blocks.contentsGetter(), MODID, existing);
        }

        @Override
        public void addTags(HolderLookup.Provider lookup) {
            tag(LEFT).add(Items.DIRT, Items.STONE);
            tag(RIGHT).add(Items.STONE, Items.GRAVEL);
        }
    }

    private static class Recipes extends RecipeProvider implements IConditionBuilder, IIngredientBuilder, INBTBuilder{
        public Recipes(PackOutput gen) {
            super(gen);
        }

        private static ShapedRecipeBuilder shaped() {
            return ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.DIRT);
        }

        @Override
        protected void buildRecipes(RecipeOutput out) {
            var hasName = getHasName(Items.DIRT);
            var has = has(Items.DIRT);

            // contains NBT match - should match a stone pickaxe that lost 3 durability, regardless of setting its name
            shaped()
                .pattern("XXX")
                .pattern("XXX")
                .define('X',
                    partialNbt()
                        .nbt(damaged())
                        .items(Items.WOODEN_PICKAXE, Items.STONE_PICKAXE, Items.IRON_PICKAXE)
                        .build()
                )
                .unlockedBy(hasName, has)
                .save(out, rl("partial_nbt_damage_only"));

            // contains NBT match - should match a named wood, stone, or iron pickaxe, regardless of damage
            shaped()
                .pattern("XXX")
                .define('X',
                    partialNbt()
                        .nbt(named())
                        .items(Items.WOODEN_PICKAXE, Items.STONE_PICKAXE, Items.IRON_PICKAXE)
                        .build()
                )
                .unlockedBy(hasName, has)
                .save(out, rl("partial_nbt_name_only"));

            // compound - should match named dirt or named oak logs
            shaped()
               .pattern("XXX")
               .pattern(" X ")
               .define('X', compound(
                   strictNbt(named(Items.DIRT)),
                   strictNbt(named(Items.OAK_LOG))
               ))
               .unlockedBy(hasName, has)
               .save(out, rl("compound_ingredient"));

            // strict NBT match - should match an unnamed pickaxe that lost 3 durability
            shaped()
                .pattern("XXX")
                .define('X', strictNbt(damaged(Items.IRON_PICKAXE)))
                .unlockedBy(hasName, has)
                .save(out, rl("strict_nbt_ingredient"));

            // intersection - should match STONE only, [dirt, stone] + [stone, gravel]
            shaped()
                .pattern("XXX")
                .pattern("XXX")
                .pattern(" X ")
                .define('X', intersection(LEFT, RIGHT))
                .unlockedBy(hasName, has)
                .save(out, rl("intersection_ingredient"));

            // difference - should match DIRT only, [dirt, stone] - [stone, gravel]
            shaped()
               .pattern(" X ")
               .pattern("XXX")
               .pattern(" X ")
               .define('X', difference(LEFT, RIGHT))
               .unlockedBy(hasName, has)
               .save(out, rl("difference_ingredient"));
        }
    }
}
