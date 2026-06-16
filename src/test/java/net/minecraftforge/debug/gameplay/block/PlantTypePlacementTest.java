/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.gameplay.block;

import net.minecraft.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.*;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.gametest.GameTest;
import net.minecraftforge.gametest.GameTestNamespace;
import net.minecraftforge.test.BaseTestMod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import static net.minecraft.world.level.block.Blocks.*;

@GameTestNamespace("forge")
@Mod(PlantTypePlacementTest.MOD_ID)
public class PlantTypePlacementTest extends BaseTestMod {
    static final String MOD_ID = "plant_type_placement";

    private static final Map<TagKey<Block>, List<Block>> TAGS = Util.make(new HashMap<>(), map -> {
        map.put(BlockTags.DIRT,       List.of(DIRT, COARSE_DIRT, ROOTED_DIRT));
        map.put(BlockTags.MUD,        List.of(MUD, MUDDY_MANGROVE_ROOTS));
        map.put(BlockTags.MOSS_BLOCKS,  List.of(MOSS_BLOCK, PALE_MOSS_BLOCK));
        map.put(BlockTags.GRASS_BLOCKS, List.of(GRASS_BLOCK, PODZOL, MYCELIUM));
        map.put(BlockTags.SAND,       List.of(SAND, RED_SAND, SUSPICIOUS_SAND));
        var terracotta = new ArrayList<Block>(Blocks.DYED_TERRACOTTA.asList());
        terracotta.add(TERRACOTTA);
        map.put(BlockTags.TERRACOTTA, terracotta);
        map.put(BlockTags.NYLIUM,     List.of(CRIMSON_NYLIUM, WARPED_NYLIUM));
        map.put(BlockTags.OVERRIDES_MUSHROOM_LIGHT_REQUIREMENT, List.of(MYCELIUM, PODZOL, CRIMSON_NYLIUM, WARPED_NYLIUM));
    });

    private record Info(Consumer<GameTestHelper> handler, List<Block> blocks) {}
    private static Info of(Consumer<GameTestHelper> handler, Block... blocks) {
        return new Info(handler, Arrays.asList(blocks));
    }

    private final Map<Class<? extends Block>, Info> PLANTS = Util.make(new HashMap<>(), map -> {
        map.put(BambooStalkBlock.class,  of(this::bamboo, BAMBOO));
        map.put(CactusBlock.class,       of(this::cactus, CACTUS));
        map.put(SugarCaneBlock.class,    of(this::sugarcane, SUGAR_CANE));

        // VegitationBlock has no direct creations
        map.put(AttachedStemBlock.class, of(this::farmland, ATTACHED_MELON_STEM, ATTACHED_PUMPKIN_STEM));
        map.put(AzaleaBlock.class,       of(this::simple, AZALEA, FLOWERING_AZALEA));
        map.put(BushBlock.class,         of(this::vegetation, BUSH));
        map.put(CactusFlowerBlock.class, of(this::cactus_flower, CACTUS_FLOWER));

        map.put(CropBlock.class,         of(this::farmland, WHEAT));
        map.put(BeetrootBlock.class,     of(this::farmland, BEETROOTS));
        map.put(CarrotBlock.class,       of(this::farmland, CARROTS));
        map.put(PotatoBlock.class,       of(this::farmland, POTATOES));
        map.put(TorchflowerCropBlock.class, of(this::farmland, TORCHFLOWER_CROP));

        map.put(DoublePlantBlock.class,   of(this::todo, LARGE_FERN, PITCHER_PLANT, TALL_GRASS));
        map.put(PitcherCropBlock.class,   of(this::farmland, PITCHER_CROP));
        map.put(SmallDripleafBlock.class, of(this::todo, SMALL_DRIPLEAF));
        map.put(TallFlowerBlock.class,    of(this::todo, LILAC, PEONY, ROSE_BUSH, SUNFLOWER));
        map.put(TallSeagrassBlock.class,  of(this::todo, TALL_SEAGRASS));

        map.put(DryVegetationBlock.class, of(this::dry_vegitation, DEAD_BUSH));
        map.put(ShortDryGrassBlock.class, of(this::dry_vegitation, SHORT_DRY_GRASS));
        map.put(TallDryGrassBlock.class,  of(this::dry_vegitation, TALL_DRY_GRASS));

        map.put(FireflyBushBlock.class,   of(this::vegetation, FIREFLY_BUSH));
        map.put(FlowerBedBlock.class,     of(this::vegetation, PINK_PETALS, WILDFLOWERS));

        map.put(FlowerBlock.class,     of(this::vegetation, ALLIUM, AZURE_BLUET, BLUE_ORCHID, CORNFLOWER, DANDELION, GOLDEN_DANDELION, LILY_OF_THE_VALLEY, ORANGE_TULIP, OXEYE_DAISY, PINK_TULIP, POPPY, RED_TULIP, TORCHFLOWER, WHITE_TULIP));
        map.put(EyeblossomBlock.class, of(this::vegetation, CLOSED_EYEBLOSSOM, OPEN_EYEBLOSSOM));
        map.put(WitherRoseBlock.class, of(this::simple, WITHER_ROSE));

        map.put(NetherFungusBlock.class,  of(this::simple, CRIMSON_FUNGUS, WARPED_FUNGUS));
        map.put(LeafLitterBlock.class,    of(this::todo, LEAF_LITTER));
        map.put(MushroomBlock.class,      of(this::mushroom, BROWN_MUSHROOM, RED_MUSHROOM));
        map.put(NetherSproutsBlock.class, of(this::simple, NETHER_SPROUTS));
        map.put(NetherWartBlock.class,    of(this::simple, NETHER_WART));
        map.put(NetherRootsBlock.class,   of(this::simple, CRIMSON_ROOTS, WARPED_ROOTS));

        map.put(SaplingBlock.class,           of(this::vegetation, ACACIA_SAPLING, BIRCH_SAPLING, CHERRY_SAPLING, DARK_OAK_SAPLING, JUNGLE_SAPLING, OAK_SAPLING, PALE_OAK_SAPLING, SPRUCE_SAPLING));
        map.put(MangrovePropaguleBlock.class, of(this::vegetation, MANGROVE_PROPAGULE));

        map.put(SeagrassBlock.class,       of(this::todo, SEAGRASS));
        map.put(SeaPickleBlock.class,      of(this::todo, SEA_PICKLE));
        map.put(StemBlock.class,           of(this::farmland, MELON_STEM, PUMPKIN_STEM));
        map.put(SweetBerryBushBlock.class, of(this::vegetation, SWEET_BERRY_BUSH));
        map.put(TallGrassBlock.class,      of(this::vegetation, FERN, SHORT_GRASS));
        map.put(LilyPadBlock.class,        of(this::todo, LILY_PAD));
    });

    @SafeVarargs
    private List<Block> plants(Class<? extends Block>... classes) {
        var ret = new ArrayList<Block>();
        for (var cls : classes)
            ret.addAll(PLANTS.get(cls).blocks());
        return ret;
    }

    private void todo(GameTestHelper helper) {
        // TODO: [Forge][Plant][Test] Implement the final tests for some vanilla blocks
        //     DoublePlantBlock = UPPER == self LOWER == VEGITATION
        //         SmallDripleafBlock = UPPER = self LOWER == SMALL_DRIPLEAF_PLACEABLE || water_source || VEGITATION
        //         TallFlowerBlock
        //         TallSeagrassBlock = UPPER = self LOWER == Solid Up Face && !MAGMA_BLOCK

        //     LeafLitterBlock = Sturdy UP face
        //     SeagrassBlock = Sturdy UP face && !MAGMA_BLOCK
        //     SeaPickleBlock = Sturdy UP Face || Face UP != empty?
        //     WaterlilyBlock = (Water Fluid || IceBlock) && self.fluid == Empty
    }

    public PlantTypePlacementTest(FMLJavaModLoadingContext context) {
        super(context, false, false);
    }

    // Tests to make sure that we know about all plants in vanilla
    @GameTest
    public void coverage(GameTestHelper helper) {
        var vanilla = helper.registryLookup(Registries.BLOCK).entrySet()
            .stream()
            .filter(e -> e.getValue() instanceof IPlantable && e.getKey().identifier().getNamespace().equals("minecraft"))
            .map(Map.Entry::getValue)
            .collect(Collectors.groupingBy(Object::getClass));

        var missing = new HashSet<>(PLANTS.keySet());
        for (var entry : vanilla.entrySet()) {
            var known = PLANTS.get(entry.getKey());
            helper.assertTrue(known != null, "Unknown IPlantable concrete class: " + entry.getKey());
            helper.assertValueEqual(sort(known.blocks()), sort(entry.getValue()), entry.getKey().getSimpleName(), "Does not have expected values");
            missing.remove(entry.getKey());
        }

        helper.assertValueEqual(Collections.emptySet(), missing, "Missing", "Expected plants were missing");
        helper.succeed();
    }

    @GameTest(structure = "forge:empty5x4x4") // SUPPORTS_BAMBOO = sand(3) + substrate() + extra(4) = 17
    public void bamboo(GameTestHelper helper) {
        var plants = plants(BambooStalkBlock.class);
        var soils = join(sand(helper), substrate(helper), List.of(BAMBOO, BAMBOO_SAPLING, GRAVEL, SUSPICIOUS_GRAVEL));
        // these need special case
        var special = List.of(BAMBOO, BAMBOO_SAPLING);

        testTag(helper, BlockTags.SUPPORTS_BAMBOO, soils);
        soils.removeAll(special);

        var pos = iterate(helper, plants, soils);

        for (var plant : plants) {
            for (var soil : special) {
                pos = offset(helper, pos);
                var sandPos = pos.above();
                var soilPos = sandPos.above();
                var plantPos = soilPos.above();

                helper.setAndAssertBlock(pos, STONE);
                helper.setAndAssertBlock(sandPos, SAND);
                helper.setAndAssertBlock(soilPos, soil);
                canSurvive(helper, plantPos, plant);
            }
        }

        helper.succeed();
    }

    @GameTest(structure = "forge:empty4x4x5") // sand(3) + cactus * 2 for spacing + we need a 1 block border because BARRIOR is solid
    public void cactus(GameTestHelper helper) {
        // CactusBlock = (Horizontal != solid && !Fluid.LAVA) && (Tag.SAND, CACTUS) && !above().liquid()
        var plants = plants(CactusBlock.class);
        var soils = sand(helper); // Add an extra sand so it builds somewhere to test cactus

        int idx = 0;
        for (var plant : plants) {
            BlockPos pos = null;
            for (var soil : soils) {
                pos = new BlockPos(idx % 2 == 0 ? 1 : 2, 0, idx + 1);
                var soilPos = pos.above();
                var plantPos = soilPos.above();

                helper.setAndAssertBlock(pos, STONE);
                helper.setAndAssertBlock(soilPos, soil);
                canSurvive(helper, plantPos, plant);

                idx++;
            }
            // Test on top of the last placed cactus
            canSurvive(helper, pos.above(3), plant);
        }

        // TODO: [Forge][Plants][Test] Test solid/lava next to cactus

        helper.succeed();
    }

    @GameTest(structure = "forge:empty3x4x3") // Need 4 high for CACTUS soil
    public void cactus_flower(GameTestHelper helper) {
        helper.makeFloor(STONE, 0);
        helper.makeFloor(SAND, 1);

        var pos = BlockPos.ZERO.above();
        //     CactusFlowerBlock = SUPPORT_OVERRIDE_CACTUS_FLOWER(CACTUS, FARMLAND), SturdyCenter Up
        for (var plant : plants(CactusFlowerBlock.class)) {
            helper.setAndAssertBlock(pos, FARMLAND);
            canSurvive(helper, pos.above(), plant);

            var cactus = pos.offset(1, 1, 1);
            helper.setAndAssertBlock(cactus, CACTUS);
            canSurvive(helper, cactus.above(), plant);

            //TODO: [Forge][Plants][Test] Center Support for Cactus Flower

            pos.east(3);
        }
        helper.succeed();
    }

    @GameTest(structure = "forge:empty13x3x9") // SUPPORTS_SUGAR_CANE(substrate() + sand(3)) * (water(2) + FROSTED_ICE)
    public void sugarcane(GameTestHelper helper) {
        // SugarCaneBlock = DIRT || SAND && Horizontal (Fluid == WATER || FROSTED_ICE)
        var plants = plants(SugarCaneBlock.class);
        var soils = join(substrate(helper), sand(helper));

        var start = BlockPos.ZERO;
        for (var plant : plants) {
            for (var soil : soils) {
                var pos = start;
                for (int z = 0; z < 9; z++) {
                    helper.setBlock(pos,         STONE);
                    helper.setBlock(pos.above(), soil);
                    pos = pos.south();
                }


                // Place fluids
                pos = start.above();
                helper.setAndAssertBlock(pos.south(2), WATER);
                helper.setAndAssertBlock(pos.south(3), WATER.defaultBlockState().setValue(LiquidBlock.LEVEL, 8));
                helper.setAndAssertBlock(pos.south(6), FROSTED_ICE);

                // Test Plants
                pos = start.above();
                canSurvive(helper, pos.above().south(1), plant);
                canSurvive(helper, pos.above().south(4), plant);
                canSurvive(helper, pos.above().south(7), plant);

                start = start.east();
            }
        }
        helper.succeed();
    }

    @GameTest(structure = "forge:empty11x3x33") // vegetation(11) * 32 plants
    public void vegetation(GameTestHelper helper) {
        var plants = plants(
            BushBlock.class,           // 1
            FireflyBushBlock.class,    // 1
            FlowerBedBlock.class,      // 2
            FlowerBlock.class,         // 14
            EyeblossomBlock.class,     // 2
            SaplingBlock.class,        // 8
            SweetBerryBushBlock.class, // 1
            MangrovePropaguleBlock.class, // 1
            TallGrassBlock.class       // 2
        );
        var soils = supportsVegetation(helper);

        iterate(helper, plants, soils);
        helper.succeed();
    }

    @GameTest(structure = "forge:empty16x3x8")
    public void simple(GameTestHelper helper) {
        var pos = iterate(helper, BlockPos.ZERO, plants(AzaleaBlock.class), supportsAzalea(helper)); // 12
        pos = iterate(helper, pos, plants(WitherRoseBlock.class), supportsWitherRose(helper)); // 14
        pos = iterate(helper, pos, plants(NetherWartBlock.class), supportsNetherWart(helper)); // 1
        pos = iterate(helper, pos, plants(NetherSproutsBlock.class), supportsNetherSprouts(helper)); // 14
        pos = iterate(helper, pos, List.of(Blocks.WARPED_ROOTS), supportsWarpedRoots(helper)); // 14
        pos = iterate(helper, pos, List.of(Blocks.CRIMSON_ROOTS), supportsCrimsonRoots(helper)); // 14
        pos = iterate(helper, pos, List.of(Blocks.WARPED_FUNGUS), supportsWarpedFungus(helper)); // 15
        pos = iterate(helper, pos, List.of(Blocks.CRIMSON_FUNGUS), supportsCrimsonFungus(helper)); // 15
        helper.succeed();
    }

    @GameTest(structure = "forge:empty3x3x4") // 4 stems + 6 crops
    public void farmland(GameTestHelper helper) {
        var plants = plants(
            AttachedStemBlock.class, // 2
            StemBlock.class,         // 2
            CropBlock.class,
            BeetrootBlock.class,
            CarrotBlock.class,
            PotatoBlock.class,
            TorchflowerCropBlock.class,
            PitcherCropBlock.class
        );
        iterate(helper, plants, List.of(FARMLAND));
        helper.succeed();
    }

    @GameTest(structure = "forge:empty32x3x3") // 32 * plants(3)
    public void dry_vegitation(GameTestHelper helper) {
        var plants = plants(DryVegetationBlock.class, ShortDryGrassBlock.class, TallDryGrassBlock.class);
        iterate(helper, plants, supportsDryVegetation(helper));
        helper.succeed();
    }

    @GameTest(structure = "forge:empty11x3x3") // (10 dirt + 2 nylium + 3 extra) * 2 plants
    public void fungus(GameTestHelper helper) {
        // FUNGUS PlantType self explanatory
        var plants = List.of(WARPED_FUNGUS, CRIMSON_FUNGUS);

        var nylium = List.of(CRIMSON_NYLIUM, WARPED_NYLIUM);
        testTag(helper, BlockTags.NYLIUM, nylium);

        var extra = List.of(FARMLAND, MYCELIUM, SOUL_SOIL);

        // See FungusBlock.mayPlaceOn
        var pos = iterate(helper,      plants, dirt(helper));
            pos = iterate(helper, pos, plants, nylium);
            pos = iterate(helper, pos, plants, extra);
        helper.succeed();
    }

    // 6 on X to allow Dark area, and 2 extra rows to test the Tag
    // 4 on Y to allow 2 spaces in the dark for the bad test (FLOOR GLASS PLANT CEILING)
    // 4 on Z to allow 2 spaces in the dark area to test good and bad (WALL GOOD BAD WALL)
    @GameTest(structure = "forge:empty16x4x16")
    public void mushroom(GameTestHelper helper) {
        // MushroomBlock = (SOLID && Light < 13) || Tag.MUSHROOM_GROW_BLOCK
        var plants = plants(MushroomBlock.class);

        helper.makeFloor(STONE, 0);
        helper.makeFloor(STONE, 3);
        // I would rather make a enclosed box, but lighting doesn't propagate for some reason.


        var soil = testTag(helper, BlockTags.OVERRIDES_MUSHROOM_LIGHT_REQUIREMENT, TAGS.get(BlockTags.OVERRIDES_MUSHROOM_LIGHT_REQUIREMENT));
        iterate(helper, plants, soil);

        // delay a handful of ticks to propagate lighting
        helper.runAfterDelay(10, () -> {

            // Box us in so that it is dark
            var dark = new BlockPos(7, 1, 7);

            for (var plant : plants) {
                // Its on STONE, should survive
                canSurvive(helper, dark, plant);
                var bad = dark.south();

                // Should fail on GLASS because not solid
                helper.setBlock(bad, GLASS);
                helper.setAndAssertBlock(bad.above(), plant);
                var state = helper.getBlockState(bad.above());
                helper.assertFalse(state.canSurvive(helper.getLevel(), helper.absolutePos(bad.above())), () -> name(state) + " can survive on " + name(helper.getBlockState(bad)));

                dark = dark.east(2);
            }

            helper.succeed();
        });
    }


    //===================================================
    //                HELPERS
    //===================================================
    private static BlockPos iterate(GameTestHelper helper, Collection<Block> plants, Collection<Block> soils) {
        return iterate(helper, BlockPos.ZERO.west(), plants, soils);
    }

    private static BlockPos iterate(GameTestHelper helper, BlockPos pos, Collection<Block> plants, Collection<Block> soils) {
        for (var plant : plants) {
            for (var soil : soils) {
                pos = offset(helper, pos);
                var soilPos = pos.above();
                var plantPos = soilPos.above();

                helper.setBlock(pos, STONE);
                helper.setAndAssertBlock(soilPos, soil);
                canSurvive(helper, plantPos, plant);
            }
        }
        return pos;
    }

    private static Collection<Block> known(GameTestHelper helper, TagKey<Block> tag) {
        var expected = TAGS.get(tag);
        if (expected == null)
            helper.fail("Failed to find known values for: " + tag);
        return testTag(helper, tag, expected);
    }

    @SafeVarargs
    private static Collection<Block> join(GameTestHelper helper, TagKey<Block>... tags) {
        var ret = new HashSet<Block>();
        for (var tag : tags)
            ret.addAll(known(helper, tag));
        return ret;
    }

    @SafeVarargs
    private static Collection<Block> join(Collection<Block>... lists) {
        var ret = new HashSet<Block>();
        for (var list : lists)
            ret.addAll(list);
        return ret;
    }

    private static Collection<Block> dirt(GameTestHelper helper) { // 3
        return known(helper, BlockTags.DIRT);
    }
    private static Collection<Block> sand(GameTestHelper helper) { // 3
        return known(helper, BlockTags.SAND);
    }
    private static Collection<Block> terracotta(GameTestHelper helper) { // 18
        return known(helper, BlockTags.TERRACOTTA);
    }
    private static Collection<Block> nylium(GameTestHelper helper) { // 2
        return known(helper, BlockTags.NYLIUM);
    }
    private static Collection<Block> substrate(GameTestHelper helper) { // 10 = dirt(3), mud(2), moss_blocks(2), grass_blocks(3)
        return testTag(helper, BlockTags.SUBSTRATE_OVERWORLD, join(helper, BlockTags.DIRT, BlockTags.MUD, BlockTags.MOSS_BLOCKS, BlockTags.GRASS_BLOCKS));
    }
    private static Collection<Block> supportsVegetation(GameTestHelper helper) { // 11 = (substrate(10) + FARMLAND)
        return testTag(helper, BlockTags.SUPPORTS_VEGETATION, join(substrate(helper), List.of(FARMLAND)));
    }
    private static Collection<Block> supportsAzalea(GameTestHelper helper) { // 12 = (vegetation(11) + CLAY)
        return testTag(helper, BlockTags.SUPPORTS_AZALEA, join(supportsVegetation(helper), List.of(CLAY)));
    }
    private static Collection<Block> supportsWitherRose(GameTestHelper helper) { // 14 = (vegetation(11) + NETHERRACK + SOUL_SAND + SOUL_SOIL)
        return testTag(helper, BlockTags.SUPPORTS_WITHER_ROSE, join(supportsVegetation(helper), List.of(NETHERRACK, SOUL_SAND, SOUL_SOIL)));
    }
    private static Collection<Block> supportsNetherWart(GameTestHelper helper) { // 1
        return testTag(helper, BlockTags.SUPPORTS_NETHER_WART, List.of(SOUL_SAND));
    }
    private static Collection<Block> supportsNetherSprouts(GameTestHelper helper) { // 14 = (vegetation(11) + nylium(2) + SOUL_SOIL)
        return testTag(helper, BlockTags.SUPPORTS_NETHER_SPROUTS, join(supportsVegetation(helper), nylium(helper), List.of(SOUL_SOIL)));
    }
    private static Collection<Block> supportsWarpedRoots(GameTestHelper helper) { // 14 = (vegetation(11) + nylium(2) + SOUL_SOIL)
        return testTag(helper, BlockTags.SUPPORTS_WARPED_ROOTS, join(supportsVegetation(helper), nylium(helper), List.of(SOUL_SOIL)));
    }
    private static Collection<Block> supportsCrimsonRoots(GameTestHelper helper) { // 14 = (warpedRoots(14))
        return testTag(helper, BlockTags.SUPPORTS_CRIMSON_ROOTS, supportsWarpedRoots(helper));
    }
    private static Collection<Block> supportsWarpedFungus(GameTestHelper helper) { // 15 = (vegetation(11) + nylium(2) + MYCELIUM + SOUL_SOIL)
        return testTag(helper, BlockTags.SUPPORTS_WARPED_FUNGUS, join(supportsVegetation(helper), nylium(helper), List.of(MYCELIUM, SOUL_SOIL)));
    }
    private static Collection<Block> supportsCrimsonFungus(GameTestHelper helper) { // 15 = (warpedFungus(15))
        return testTag(helper, BlockTags.SUPPORTS_CRIMSON_FUNGUS, supportsWarpedFungus(helper));
    }
    private static Collection<Block> supportsDryVegetation(GameTestHelper helper) { // 32 = (sand(3) + terracotta(18) + vegetation(11)
        return testTag(helper, BlockTags.SUPPORTS_DRY_VEGETATION, join(sand(helper), terracotta(helper), supportsVegetation(helper)));
    }

    private static BlockPos offset(GameTestHelper helper, BlockPos pos) {
        pos = pos.east();
        if (pos.getX() >= (int)helper.getBounds().getXsize())
            pos = new BlockPos(0, pos.getY(), pos.getZ() + 1);
        if (pos.getZ() >= (int)helper.getBounds().getZsize())
            helper.fail("Ran out of space to run test");
        return pos;
    }

    private static Collection<Block> sort(Collection<Block> blocks) {
        var sorted = new ArrayList<>(blocks);
        Collections.sort(sorted, (a, b) -> name(a).compareTo(name(b)));
        return sorted;
    }

    private static String name(BlockState block) {
        return name(block.getBlock());
    }

    private static String name(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block).getPath();
    }

    public static void canSurvive(GameTestHelper helper, BlockPos pos, Block expected) {
        helper.setAndAssertBlock(pos, expected);
        var state = helper.getBlockState(pos);
        var soil = helper.getBlockState(pos.below());
        helper.assertTrue(state.canSurvive(helper.getLevel(), helper.absolutePos(pos)), () -> name(state) + " can not survive on " + name(soil));
    }

    private static Collection<Block> testTag(GameTestHelper helper, TagKey<Block> tag, Collection<Block> expected) {
        var lookup = helper.registryLookup(Registries.BLOCK);
        var values = lookup.getOrThrow(tag).stream()
            .filter(h -> h.unwrapKey().orElseThrow().identifier().getNamespace().equals("minecraft")) // Only vanilla entries
            .map(Holder::get)
            .toList();
        helper.assertValueEqual(sort(expected), sort(values), tag.location().toString(), "Tag does not have expected values");
        return expected;
    }

    @SuppressWarnings("unused")
    private static void box(GameTestHelper helper, Block block, BlockPos start, BlockPos end) {
        for (var pos : BlockPos.betweenClosed(start, end)) {
            if (   pos.getX() == start.getX()
                || pos.getX() == end.getX()
                || pos.getZ() == start.getZ()
                || pos.getZ() == end.getZ()
                || pos.getY() == start.getY()
                || pos.getY() == end.getY())
                helper.setBlock(pos, block);
        }
    }
}
