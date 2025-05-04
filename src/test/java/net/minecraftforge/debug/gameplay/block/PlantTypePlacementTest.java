/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.gameplay.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.gametest.GameTest;
import net.minecraftforge.gametest.GameTestNamespace;
import net.minecraftforge.test.BaseTestMod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static net.minecraft.world.level.block.Blocks.*;

@GameTestNamespace("forge")
@Mod(PlantTypePlacementTest.MOD_ID)
public class PlantTypePlacementTest extends BaseTestMod {
    static final String MOD_ID = "plant_type_placement";
    static BlockPos noOverlapOffset = BlockPos.ZERO;

    public PlantTypePlacementTest(FMLJavaModLoadingContext context) {
        super(context);
    }

    @GameTest
    public static void test_vanilla_plantables(GameTestHelper helper) {
        // CROP PlantType. Includes AttachedStemBlocks, CropBlocks, and PITCHER_CROP
        var vanillaCropList = tagToList(helper, BlockTags.CROPS);
        iterateBlocks(helper, vanillaCropList, FARMLAND);

        // PLAINS PlantType includes all saplings, flowers, and grasses
        var vanillaSaplingList = List.of(OAK_SAPLING, BIRCH_SAPLING, SPRUCE_SAPLING, JUNGLE_SAPLING, DARK_OAK_SAPLING, ACACIA_SAPLING, CHERRY_SAPLING, PALE_OAK_SAPLING);
        var dirts = tagAndAdditional(helper, BlockTags.DIRT, FARMLAND);
        iterateBlocks(helper, vanillaSaplingList, dirts);
        // Flowers and grass share the same rules as normal saplings
        var vanillaFlowerList = tagAndAdditional(helper, BlockTags.SMALL_FLOWERS, PITCHER_PLANT, SUNFLOWER, LILAC, PEONY, ROSE_BUSH, PITCHER_PLANT, WILDFLOWERS, PINK_PETALS, TALL_GRASS, LARGE_FERN, SHORT_GRASS, FERN, BUSH);
        iterateBlocks(helper, vanillaFlowerList, dirts);

        // MOIST PlantType, because clay is more moist or something
        var snowflakeSaplingList = List.of(MANGROVE_PROPAGULE, AZALEA, FLOWERING_AZALEA);
        iterateBlocks(helper, snowflakeSaplingList, dirts);
        iterateBlocks(helper, snowflakeSaplingList, CLAY);

        // FUNGUS PlantType self explanatory
        var fungus = List.of(WARPED_FUNGUS, CRIMSON_FUNGUS);
        var validForFungus = tagAndAdditional(helper, BlockTags.NYLIUM, SOUL_SOIL, MYCELIUM);
        iterateBlocks(helper, fungus, validForFungus);

        // NETHER PlantType self explanatory
        var warts = List.of(NETHER_WART);
        iterateBlocks(helper, warts, SOUL_SAND);

        // WATER PlantType self explanatory
        var lily = List.of(LILY_PAD);
        iterateBlocks(helper, lily, WATER);

        // DESERT PlantType, but only DEAD_BUSH
        var deadbush = List.of(DEAD_BUSH);
        var deadbushValid = tagMultiple(helper, BlockTags.SAND, BlockTags.DIRT, BlockTags.TERRACOTTA);
        iterateBlocks(helper, deadbush, deadbushValid);

        // CAVE PlantType, can grow on any solid block apparently. Might as well test they don't adhere to nonsolid
        generateLightCover(helper, noOverlapOffset);
        generateLightCover(helper, noOverlapOffset.east());
        helper.setAndAssertBlock(noOverlapOffset, STONE);
        helper.setAndAssertBlock(noOverlapOffset.east(), GLASS);
        helper.setAndAssertBlock(noOverlapOffset.above(), RED_MUSHROOM);
        helper.setAndAssertBlock(noOverlapOffset.east().above(), RED_MUSHROOM);
        helper.runAfterDelay(5, () -> {
            helper.assertBlockPresent(RED_MUSHROOM, noOverlapOffset.above());
            helper.assertBlockNotPresent(RED_MUSHROOM, noOverlapOffset.east().above());
        });
        generateLightCover(helper, noOverlapOffset);
        generateLightCover(helper, noOverlapOffset.east());
        helper.setAndAssertBlock(noOverlapOffset, STONE);
        helper.setAndAssertBlock(noOverlapOffset.east(), GLASS);
        helper.setAndAssertBlock(noOverlapOffset.above(), BROWN_MUSHROOM);
        helper.setAndAssertBlock(noOverlapOffset.east().above(), BROWN_MUSHROOM);
        helper.runAfterDelay(5, () -> {
            helper.assertBlockPresent(BROWN_MUSHROOM, noOverlapOffset.above());
            helper.assertBlockNotPresent(BROWN_MUSHROOM, noOverlapOffset.east().above());
        });
        noOverlapOffset = noOverlapOffset.north();

        // Cactus
        var sands = tagToList(helper, BlockTags.SAND);
        var offset = new BlockPos(noOverlapOffset);
        for (Block sand : sands) {
            helper.setAndAssertBlock(offset, sand);
            helper.setBlock(offset.below(), STONE);
            helper.setAndAssertBlock(offset.above(), CACTUS);
            BlockPos finalOffset = offset;
            helper.runAfterDelay(5, () -> {
                helper.assertBlockPresent(CACTUS, finalOffset.above());
            });
            offset = offset.east(2);
        }
        noOverlapOffset = noOverlapOffset.north();

        // Finally, BEACH (sugar cane)
        var validCaneLand = new ArrayList<Block>();
        var validCaneLiquid = List.of(WATER, FROSTED_ICE);
        validCaneLand.addAll(sands); validCaneLand.addAll(dirts);
        validCaneLand.remove(FARMLAND);
        offset = new BlockPos(noOverlapOffset);
        for (Block liquid : validCaneLiquid) {
            for (Block land : validCaneLand) {
                helper.setAndAssertBlock(offset, land);
                helper.setBlock(offset.below(), STONE);
                helper.setAndAssertBlock(offset.north(), liquid);
                helper.setAndAssertBlock(offset.above(), SUGAR_CANE);
                BlockPos finalOffset1 = offset;
                helper.runAfterDelay(5, () -> {
                    helper.assertBlockPresent(SUGAR_CANE, finalOffset1.above());
                });
                offset = offset.east();
            }
            offset = noOverlapOffset.north(4);
        }
        helper.succeed();
        noOverlapOffset = BlockPos.ZERO;
    }

    private static void placeAndCheck(GameTestHelper helper, BlockPos pos, Block land, Block plant) {
        helper.setAndAssertBlock(pos, land);
        helper.setBlock(pos.below(), STONE);
        helper.setAndAssertBlock(pos.above(), plant);
        helper.runAfterDelay(5, () -> {
            helper.assertBlockPresent(plant, pos.above());
        });
    }

    private static void iterateBlocks(GameTestHelper helper, List<Block> plants, List<Block> lands) {
        for (Block plant : plants) {
            BlockPos offset = new BlockPos(noOverlapOffset);
            for (Block land : lands) {
                placeAndCheck(helper, offset, land, plant);
                offset = offset.east();
            }
            noOverlapOffset = noOverlapOffset.north();
        }
    }

    private static void iterateBlocks(GameTestHelper helper, List<Block> plants, Block possibleLand) {
        iterateBlocks(helper, plants, List.of(possibleLand));
    }

    private static List<Block> tagToList(GameTestHelper helper, TagKey<Block> tag) {
        return helper.getLevel().registryAccess().lookupOrThrow(Registries.BLOCK).getOrThrow(tag).stream().map(Holder::get).toList();
    }

    private static List<Block> tagAndAdditional(GameTestHelper helper, TagKey<Block> tag, Block... blocks) {
        var result = new ArrayList<Block>();
        result.addAll(tagToList(helper, tag));
        result.addAll(Arrays.asList(blocks));
        return result;
    }

    private static List<Block> tagMultiple(GameTestHelper helper, TagKey<Block>... tags) {
        var result = new ArrayList<Block>();
        for (TagKey<Block> tag : tags) {
            result.addAll(tagToList(helper, tag));
        }
        return result;
    }

    // make sure the mushrooms can actually be planted
    private static void generateLightCover(GameTestHelper helper, BlockPos landLocation) {
        BlockPos twoAbovePlant = landLocation.above(3);
        helper.setBlock(twoAbovePlant, STONE);
        helper.setBlock(twoAbovePlant.north(), STONE);
        helper.setBlock(twoAbovePlant.north().east(), STONE);
        helper.setBlock(twoAbovePlant.east(), STONE);
        helper.setBlock(twoAbovePlant.east().south(), STONE);
        helper.setBlock(twoAbovePlant.west(), STONE);
        helper.setBlock(twoAbovePlant.west().north(), STONE);
        helper.setBlock(twoAbovePlant.south(), STONE);
        helper.setBlock(twoAbovePlant.south().west(), STONE);
        helper.setBlock(twoAbovePlant.north(2), STONE);
        helper.setBlock(twoAbovePlant.west(2), STONE);
        helper.setBlock(twoAbovePlant.east(2), STONE);
        helper.setBlock(twoAbovePlant.south(2), STONE);
    }
}
