/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.data;

import java.util.Locale;
import java.util.function.Consumer;

import net.minecraft.data.tags.BlockItemTagsProvider;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;

public abstract class ForgeBlockItemTagsProvider extends BlockItemTagsProvider {
    @Override
    @SuppressWarnings({ "unchecked", "removal" })
    protected void run() {
        tag(Tags.Blocks.BARRELS, Tags.Items.BARRELS)
            .addTag(Tags.Blocks.BARRELS_WOODEN);
        tag(Tags.Blocks.BARRELS_WOODEN, Tags.Items.BARRELS_WOODEN)
            .add(Blocks.BARREL);
        tag(Tags.Blocks.BARS_COPPER, Tags.Items.BARS_COPPER)
                .addAll(Blocks.COPPER_BARS.asList());
        tag(Tags.Blocks.BARS_IRON, Tags.Items.BARS_IRON)
                .add(Blocks.IRON_BARS);
        tag(Tags.Blocks.BARS, Tags.Items.BARS)
                .addTags(Tags.Blocks.BARS_COPPER, Tags.Blocks.BARS_IRON, BlockTags.BARS);
        tag(Tags.Blocks.BOOKSHELVES, Tags.Items.BOOKSHELVES)
            .add(Blocks.BOOKSHELF);
        tag(Tags.Blocks.BUDDING_BLOCKS, Tags.Items.BUDDING_BLOCKS)
            .add(Blocks.BUDDING_AMETHYST);
        tag(Tags.Blocks.BUDS, Tags.Items.BUDS)
            .add(Blocks.SMALL_AMETHYST_BUD)
            .add(Blocks.MEDIUM_AMETHYST_BUD)
            .add(Blocks.LARGE_AMETHYST_BUD);
        tag(Tags.Blocks.CHAINS, Tags.Items.CHAINS)
            .add(Blocks.IRON_CHAIN)
            .addAll(Blocks.COPPER_CHAIN.asList());
        tag(Tags.Blocks.CHESTS_ENDER, Tags.Items.CHESTS_ENDER)
            .add(Blocks.ENDER_CHEST);
        tag(Tags.Blocks.CHESTS_TRAPPED, Tags.Items.CHESTS_TRAPPED)
            .add(Blocks.TRAPPED_CHEST);
        tag(Tags.Blocks.CHESTS_WOODEN, Tags.Items.CHESTS_WOODEN)
            .add(Blocks.CHEST, Blocks.TRAPPED_CHEST);
        tag(Tags.Blocks.CHESTS, Tags.Items.CHESTS)
            .add(Blocks.COPPER_CHEST)
            .addTags(
                Tags.Blocks.CHESTS_ENDER,
                Tags.Blocks.CHESTS_TRAPPED,
                Tags.Blocks.CHESTS_WOODEN
            );
        tag(Tags.Blocks.CLUSTERS, Tags.Items.CLUSTERS)
            .add(Blocks.AMETHYST_CLUSTER);
        tag(Tags.Blocks.COBBLESTONES_NORMAL, Tags.Items.COBBLESTONES_NORMAL)
            .add(Blocks.COBBLESTONE);
        tag(Tags.Blocks.COBBLESTONES_INFESTED, Tags.Items.COBBLESTONES_INFESTED)
            .add(Blocks.INFESTED_COBBLESTONE);
        tag(Tags.Blocks.COBBLESTONES_MOSSY, Tags.Items.COBBLESTONES_MOSSY)
            .add(Blocks.MOSSY_COBBLESTONE);
        tag(Tags.Blocks.COBBLESTONES_DEEPSLATE, Tags.Items.COBBLESTONES_DEEPSLATE)
            .add(Blocks.COBBLED_DEEPSLATE);
        tag(Tags.Blocks.COBBLESTONES, Tags.Items.COBBLESTONES)
            .addTags(
                Tags.Blocks.COBBLESTONES_NORMAL,
                Tags.Blocks.COBBLESTONES_INFESTED,
                Tags.Blocks.COBBLESTONES_MOSSY,
                Tags.Blocks.COBBLESTONES_DEEPSLATE
            );
        tag(Tags.Blocks.CONCRETES, Tags.Items.CONCRETES)
            .add(
                Blocks.WHITE_CONCRETE,
                Blocks.ORANGE_CONCRETE,
                Blocks.MAGENTA_CONCRETE,
                Blocks.LIGHT_BLUE_CONCRETE,
                Blocks.YELLOW_CONCRETE,
                Blocks.LIME_CONCRETE,
                Blocks.PINK_CONCRETE,
                Blocks.GRAY_CONCRETE,
                Blocks.LIGHT_GRAY_CONCRETE,
                Blocks.CYAN_CONCRETE,
                Blocks.PURPLE_CONCRETE,
                Blocks.BLUE_CONCRETE,
                Blocks.BROWN_CONCRETE,
                Blocks.GREEN_CONCRETE,
                Blocks.RED_CONCRETE,
                Blocks.BLACK_CONCRETE
            );
        tag(Tags.Blocks.END_STONES, Tags.Items.END_STONES)
            .add(Blocks.END_STONE);
        tag(Tags.Blocks.FENCE_GATES, Tags.Items.FENCE_GATES)
            .addTags(Tags.Blocks.FENCE_GATES_WOODEN);
        tag(Tags.Blocks.FENCE_GATES_WOODEN, Tags.Items.FENCE_GATES_WOODEN)
            .add(
                Blocks.OAK_FENCE_GATE,
                Blocks.SPRUCE_FENCE_GATE,
                Blocks.BIRCH_FENCE_GATE,
                Blocks.JUNGLE_FENCE_GATE,
                Blocks.ACACIA_FENCE_GATE,
                Blocks.DARK_OAK_FENCE_GATE,
                Blocks.CRIMSON_FENCE_GATE,
                Blocks.WARPED_FENCE_GATE,
                Blocks.MANGROVE_FENCE_GATE,
                Blocks.BAMBOO_FENCE_GATE,
                Blocks.CHERRY_FENCE_GATE
            );
        tag(Tags.Blocks.FENCES_NETHER_BRICK, Tags.Items.FENCES_NETHER_BRICK)
            .add(Blocks.NETHER_BRICK_FENCE);
        tag(Tags.Blocks.FENCES_WOODEN, Tags.Items.FENCES_WOODEN)
            .addTag(BlockTags.WOODEN_FENCES);
        tag(Tags.Blocks.FENCES, Tags.Items.FENCES)
            .addTags(
                Tags.Blocks.FENCES_NETHER_BRICK,
                Tags.Blocks.FENCES_WOODEN
            );
        tag(Tags.Blocks.FLOWERS_SMALL, Tags.Items.FLOWERS_SMALL)
            .add(
                Blocks.DANDELION,
                Blocks.POPPY,
                Blocks.BLUE_ORCHID,
                Blocks.ALLIUM,
                Blocks.AZURE_BLUET,
                Blocks.RED_TULIP,
                Blocks.ORANGE_TULIP,
                Blocks.WHITE_TULIP,
                Blocks.PINK_TULIP,
                Blocks.OXEYE_DAISY,
                Blocks.CORNFLOWER,
                Blocks.LILY_OF_THE_VALLEY,
                Blocks.WITHER_ROSE,
                Blocks.TORCHFLOWER,
                Blocks.OPEN_EYEBLOSSOM,
                Blocks.CLOSED_EYEBLOSSOM
            );
        tag(Tags.Blocks.FLOWERS_TALL, Tags.Items.FLOWERS_TALL)
            .add(
                Blocks.SUNFLOWER,
                Blocks.LILAC,
                Blocks.PEONY,
                Blocks.ROSE_BUSH,
                Blocks.PITCHER_PLANT
            );
        tag(Tags.Blocks.FLOWERS, Tags.Items.FLOWERS)
            .add(
                Blocks.FLOWERING_AZALEA_LEAVES,
                Blocks.FLOWERING_AZALEA,
                Blocks.MANGROVE_PROPAGULE,
                Blocks.PINK_PETALS,
                Blocks.CHORUS_FLOWER,
                Blocks.SPORE_BLOSSOM
            )
            .addTags(
                Tags.Blocks.FLOWERS_SMALL,
                Tags.Blocks.FLOWERS_TALL
            )
            .addOptionalTag(BlockTags.FLOWERS);
        tag(Tags.Blocks.GLASS_BLOCKS, Tags.Items.GLASS_BLOCKS)
            .addTags(
                Tags.Blocks.GLASS_BLOCKS_COLORLESS,
                Tags.Blocks.GLASS_BLOCKS_CHEAP,
                Tags.Blocks.GLASS_BLOCKS_TINTED
            );
        tag(Tags.Blocks.GLASS_BLOCKS_COLORLESS, Tags.Items.GLASS_BLOCKS_COLORLESS)
            .add(Blocks.GLASS);
        tag(Tags.Blocks.GLASS_BLOCKS_TINTED, Tags.Items.GLASS_BLOCKS_TINTED)
            .add(Blocks.TINTED_GLASS);

        tag(Tags.Blocks.GLASS_BLOCKS_CHEAP, Tags.Items.GLASS_BLOCKS_CHEAP)
            .add(
                Blocks.GLASS,
                Blocks.WHITE_STAINED_GLASS,
                Blocks.ORANGE_STAINED_GLASS,
                Blocks.MAGENTA_STAINED_GLASS,
                Blocks.LIGHT_BLUE_STAINED_GLASS,
                Blocks.YELLOW_STAINED_GLASS,
                Blocks.LIME_STAINED_GLASS,
                Blocks.PINK_STAINED_GLASS,
                Blocks.GRAY_STAINED_GLASS,
                Blocks.LIGHT_GRAY_STAINED_GLASS,
                Blocks.CYAN_STAINED_GLASS,
                Blocks.PURPLE_STAINED_GLASS,
                Blocks.BLUE_STAINED_GLASS,
                Blocks.BROWN_STAINED_GLASS,
                Blocks.GREEN_STAINED_GLASS,
                Blocks.RED_STAINED_GLASS,
                Blocks.BLACK_STAINED_GLASS
            );
        tag(Tags.Blocks.GLASS_PANES, Tags.Items.GLASS_PANES)
            .addTags(Tags.Blocks.GLASS_PANES_COLORLESS)
            .add(
                Blocks.WHITE_STAINED_GLASS_PANE,
                Blocks.ORANGE_STAINED_GLASS_PANE,
                Blocks.MAGENTA_STAINED_GLASS_PANE,
                Blocks.LIGHT_BLUE_STAINED_GLASS_PANE,
                Blocks.YELLOW_STAINED_GLASS_PANE,
                Blocks.LIME_STAINED_GLASS_PANE,
                Blocks.PINK_STAINED_GLASS_PANE,
                Blocks.GRAY_STAINED_GLASS_PANE,
                Blocks.LIGHT_GRAY_STAINED_GLASS_PANE,
                Blocks.CYAN_STAINED_GLASS_PANE,
                Blocks.PURPLE_STAINED_GLASS_PANE,
                Blocks.BLUE_STAINED_GLASS_PANE,
                Blocks.BROWN_STAINED_GLASS_PANE,
                Blocks.GREEN_STAINED_GLASS_PANE,
                Blocks.RED_STAINED_GLASS_PANE,
                Blocks.BLACK_STAINED_GLASS_PANE
            );
        tag(Tags.Blocks.GLASS_PANES_COLORLESS, Tags.Items.GLASS_PANES_COLORLESS)
            .add(Blocks.GLASS_PANE);
        tag(Tags.Blocks.GLAZED_TERRACOTTAS, Tags.Items.GLAZED_TERRACOTTAS)
            .add(
                Blocks.WHITE_GLAZED_TERRACOTTA,
                Blocks.ORANGE_GLAZED_TERRACOTTA,
                Blocks.MAGENTA_GLAZED_TERRACOTTA,
                Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA,
                Blocks.YELLOW_GLAZED_TERRACOTTA,
                Blocks.LIME_GLAZED_TERRACOTTA,
                Blocks.PINK_GLAZED_TERRACOTTA,
                Blocks.GRAY_GLAZED_TERRACOTTA,
                Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA,
                Blocks.CYAN_GLAZED_TERRACOTTA,
                Blocks.PURPLE_GLAZED_TERRACOTTA,
                Blocks.BLUE_GLAZED_TERRACOTTA,
                Blocks.BROWN_GLAZED_TERRACOTTA,
                Blocks.GREEN_GLAZED_TERRACOTTA,
                Blocks.RED_GLAZED_TERRACOTTA,
                Blocks.BLACK_GLAZED_TERRACOTTA
            );
        tag(Tags.Blocks.GRAVELS, Tags.Items.GRAVELS)
            .add(Blocks.GRAVEL);
        tag(Tags.Blocks.NATURAL_LOGS_NETHER, Tags.Items.NATURAL_LOGS_NETHER)
            .add(Blocks.CRIMSON_STEM, Blocks.WARPED_STEM);
        tag(Tags.Blocks.NATURAL_LOGS_OVERWORLD, Tags.Items.NATURAL_LOGS_OVERWORLD)
            .add(
                Blocks.ACACIA_LOG,
                Blocks.BIRCH_LOG,
                Blocks.CHERRY_LOG,
                Blocks.DARK_OAK_LOG,
                Blocks.JUNGLE_LOG,
                Blocks.MANGROVE_LOG,
                Blocks.OAK_LOG,
                Blocks.PALE_OAK_LOG,
                Blocks.SPRUCE_LOG
            );
        tag(Tags.Blocks.NATURAL_LOGS, Tags.Items.NATURAL_LOGS)
            .addTags(Tags.Blocks.NATURAL_LOGS_NETHER, Tags.Blocks.NATURAL_LOGS_OVERWORLD);
        tag(Tags.Blocks.NATURAL_WOODS, Tags.Items.NATURAL_WOODS)
            .add(
                Blocks.ACACIA_WOOD,
                Blocks.BIRCH_WOOD,
                Blocks.CHERRY_WOOD,
                Blocks.CRIMSON_HYPHAE,
                Blocks.DARK_OAK_WOOD,
                Blocks.JUNGLE_WOOD,
                Blocks.MANGROVE_WOOD,
                Blocks.OAK_WOOD,
                Blocks.PALE_OAK_WOOD,
                Blocks.SPRUCE_WOOD,
                Blocks.WARPED_HYPHAE
            );
        tag(Tags.Blocks.NETHERRACKS, Tags.Items.NETHERRACKS)
            .add(Blocks.NETHERRACK);
        tag(Tags.Blocks.OBSIDIANS, Tags.Items.OBSIDIANS)
            .addTags(
                Tags.Blocks.OBSIDIANS_NORMAL,
                Tags.Blocks.OBSIDIANS_CRYING
            );
        tag(Tags.Blocks.OBSIDIANS_NORMAL, Tags.Items.OBSIDIANS_NORMAL)
            .add(Blocks.OBSIDIAN);
        tag(Tags.Blocks.OBSIDIANS_CRYING, Tags.Items.OBSIDIANS_CRYING)
            .add(Blocks.CRYING_OBSIDIAN);
        tag(Tags.Blocks.ORE_BEARING_GROUND_DEEPSLATE, Tags.Items.ORE_BEARING_GROUND_DEEPSLATE)
            .add(Blocks.DEEPSLATE);
        tag(Tags.Blocks.ORE_BEARING_GROUND_NETHERRACK, Tags.Items.ORE_BEARING_GROUND_NETHERRACK)
            .add(Blocks.NETHERRACK);
        tag(Tags.Blocks.ORE_BEARING_GROUND_STONE, Tags.Items.ORE_BEARING_GROUND_STONE)
            .add(Blocks.STONE);
        tag(Tags.Blocks.ORE_RATES_DENSE, Tags.Items.ORE_RATES_DENSE)
            .add(
                Blocks.COPPER_ORE,
                Blocks.DEEPSLATE_COPPER_ORE,
                Blocks.DEEPSLATE_LAPIS_ORE,
                Blocks.DEEPSLATE_REDSTONE_ORE,
                Blocks.LAPIS_ORE,
                Blocks.REDSTONE_ORE
            );
        tag(Tags.Blocks.ORE_RATES_SINGULAR, Tags.Items.ORE_RATES_SINGULAR)
            .add(
                Blocks.ANCIENT_DEBRIS,
                Blocks.COAL_ORE,
                Blocks.DEEPSLATE_COAL_ORE,
                Blocks.DEEPSLATE_DIAMOND_ORE,
                Blocks.DEEPSLATE_EMERALD_ORE,
                Blocks.DEEPSLATE_GOLD_ORE,
                Blocks.DEEPSLATE_IRON_ORE,
                Blocks.DIAMOND_ORE,
                Blocks.EMERALD_ORE,
                Blocks.GOLD_ORE,
                Blocks.IRON_ORE,
                Blocks.NETHER_QUARTZ_ORE
            );
        tag(Tags.Blocks.ORE_RATES_SPARSE, Tags.Items.ORE_RATES_SPARSE)
            .add(Blocks.NETHER_GOLD_ORE);
        tag(Tags.Blocks.ORES_COAL, Tags.Items.ORES_COAL)
            .addTag(BlockTags.COAL_ORES);
        tag(Tags.Blocks.ORES_COPPER, Tags.Items.ORES_COPPER)
            .addTag(BlockTags.COPPER_ORES);
        tag(Tags.Blocks.ORES_DIAMOND, Tags.Items.ORES_DIAMOND)
            .addTag(BlockTags.DIAMOND_ORES);
        tag(Tags.Blocks.ORES_EMERALD, Tags.Items.ORES_EMERALD)
            .addTag(BlockTags.EMERALD_ORES);
        tag(Tags.Blocks.ORES_GOLD, Tags.Items.ORES_GOLD)
            .addTag(BlockTags.GOLD_ORES);
        tag(Tags.Blocks.ORES_IRON, Tags.Items.ORES_IRON)
            .addTag(BlockTags.IRON_ORES);
        tag(Tags.Blocks.ORES_LAPIS, Tags.Items.ORES_LAPIS)
            .addTag(BlockTags.LAPIS_ORES);
        tag(Tags.Blocks.ORES_QUARTZ, Tags.Items.ORES_QUARTZ)
            .add(Blocks.NETHER_QUARTZ_ORE);
        tag(Tags.Blocks.ORES_REDSTONE, Tags.Items.ORES_REDSTONE)
            .addTag(BlockTags.REDSTONE_ORES);
        tag(Tags.Blocks.ORES_NETHERITE_SCRAP, Tags.Items.ORES_NETHERITE_SCRAP)
            .add(Blocks.ANCIENT_DEBRIS);
        tag(Tags.Blocks.ORES, Tags.Items.ORES)
            .addTags(
                Tags.Blocks.ORES_COAL,
                Tags.Blocks.ORES_COPPER,
                Tags.Blocks.ORES_DIAMOND,
                Tags.Blocks.ORES_EMERALD,
                Tags.Blocks.ORES_GOLD,
                Tags.Blocks.ORES_IRON,
                Tags.Blocks.ORES_LAPIS,
                Tags.Blocks.ORES_NETHERITE_SCRAP,
                Tags.Blocks.ORES_REDSTONE,
                Tags.Blocks.ORES_QUARTZ
            );
        tag(Tags.Blocks.ORES_IN_GROUND_DEEPSLATE, Tags.Items.ORES_IN_GROUND_DEEPSLATE)
            .add(
                Blocks.DEEPSLATE_COAL_ORE,
                Blocks.DEEPSLATE_COPPER_ORE,
                Blocks.DEEPSLATE_DIAMOND_ORE,
                Blocks.DEEPSLATE_EMERALD_ORE,
                Blocks.DEEPSLATE_GOLD_ORE,
                Blocks.DEEPSLATE_IRON_ORE,
                Blocks.DEEPSLATE_LAPIS_ORE,
                Blocks.DEEPSLATE_REDSTONE_ORE
            );
        tag(Tags.Blocks.ORES_IN_GROUND_NETHERRACK, Tags.Items.ORES_IN_GROUND_NETHERRACK)
            .add(
                Blocks.NETHER_GOLD_ORE,
                Blocks.NETHER_QUARTZ_ORE
            );
        tag(Tags.Blocks.ORES_IN_GROUND_STONE, Tags.Items.ORES_IN_GROUND_STONE)
            .add(
                Blocks.COAL_ORE,
                Blocks.COPPER_ORE,
                Blocks.DIAMOND_ORE,
                Blocks.EMERALD_ORE,
                Blocks.GOLD_ORE,
                Blocks.IRON_ORE,
                Blocks.LAPIS_ORE,
                Blocks.REDSTONE_ORE
            );
        tag(Tags.Blocks.PLAYER_WORKSTATIONS_CRAFTING_TABLES, Tags.Items.PLAYER_WORKSTATIONS_CRAFTING_TABLES)
            .add(Blocks.CRAFTING_TABLE);
        tag(Tags.Blocks.PLAYER_WORKSTATIONS_FURNACES, Tags.Items.PLAYER_WORKSTATIONS_FURNACES)
            .add(Blocks.FURNACE);
        tag(Tags.Blocks.PUMPKINS, Tags.Items.PUMPKINS)
            .addTags(
                Tags.Blocks.PUMPKINS_NORMAL,
                Tags.Blocks.PUMPKINS_CARVED,
                Tags.Blocks.PUMPKINS_JACK_O_LANTERNS
            );
        tag(Tags.Blocks.PUMPKINS_NORMAL, Tags.Items.PUMPKINS_NORMAL)
            .add(Blocks.PUMPKIN);
        tag(Tags.Blocks.PUMPKINS_CARVED, Tags.Items.PUMPKINS_CARVED)
            .add(Blocks.CARVED_PUMPKIN);
        tag(Tags.Blocks.PUMPKINS_JACK_O_LANTERNS, Tags.Items.PUMPKINS_JACK_O_LANTERNS)
            .add(Blocks.JACK_O_LANTERN);
        tag(Tags.Blocks.ROPES, Tags.Items.ROPES);
        tag(Tags.Blocks.SANDS, Tags.Items.SANDS)
            .addTags(
                Tags.Blocks.SANDS_COLORLESS,
                Tags.Blocks.SANDS_RED
            );
        tag(Tags.Blocks.SANDS_COLORLESS, Tags.Items.SANDS_COLORLESS)
            .add(Blocks.SAND);
        tag(Tags.Blocks.SANDS_RED, Tags.Items.SANDS_RED)
            .add(Blocks.RED_SAND);
        tag(Tags.Blocks.SANDSTONE_BLOCKS, Tags.Items.SANDSTONE_BLOCKS)
            .addTags(
                Tags.Blocks.SANDSTONE_RED_BLOCKS,
                Tags.Blocks.SANDSTONE_UNCOLORED_BLOCKS
            );
        tag(Tags.Blocks.SANDSTONE_SLABS, Tags.Items.SANDSTONE_SLABS)
            .addTags(
                Tags.Blocks.SANDSTONE_RED_SLABS,
                Tags.Blocks.SANDSTONE_UNCOLORED_SLABS
            );
        tag(Tags.Blocks.SANDSTONE_STAIRS, Tags.Items.SANDSTONE_STAIRS)
            .addTags(
                Tags.Blocks.SANDSTONE_RED_STAIRS,
                Tags.Blocks.SANDSTONE_UNCOLORED_STAIRS
            );
        tag(Tags.Blocks.SANDSTONE_RED_BLOCKS, Tags.Items.SANDSTONE_RED_BLOCKS)
            .add(
                Blocks.RED_SANDSTONE,
                Blocks.CUT_RED_SANDSTONE,
                Blocks.CHISELED_RED_SANDSTONE,
                Blocks.SMOOTH_RED_SANDSTONE
            );
        tag(Tags.Blocks.SANDSTONE_RED_SLABS, Tags.Items.SANDSTONE_RED_SLABS)
            .add(
                Blocks.RED_SANDSTONE_SLAB,
                Blocks.CUT_RED_SANDSTONE_SLAB,
                Blocks.SMOOTH_RED_SANDSTONE_SLAB
            );
        tag(Tags.Blocks.SANDSTONE_RED_STAIRS, Tags.Items.SANDSTONE_RED_STAIRS)
            .add(
                Blocks.RED_SANDSTONE_STAIRS,
                Blocks.SMOOTH_RED_SANDSTONE_STAIRS
            );
        tag(Tags.Blocks.SANDSTONE_UNCOLORED_BLOCKS, Tags.Items.SANDSTONE_UNCOLORED_BLOCKS)
            .add(
                Blocks.SANDSTONE,
                Blocks.CUT_SANDSTONE,
                Blocks.CHISELED_SANDSTONE,
                Blocks.SMOOTH_SANDSTONE
            );
        tag(Tags.Blocks.SANDSTONE_UNCOLORED_SLABS, Tags.Items.SANDSTONE_UNCOLORED_SLABS)
            .add(
                Blocks.SANDSTONE_SLAB,
                Blocks.CUT_SANDSTONE_SLAB,
                Blocks.SMOOTH_SANDSTONE_SLAB
            );
        tag(Tags.Blocks.SANDSTONE_UNCOLORED_STAIRS, Tags.Items.SANDSTONE_UNCOLORED_STAIRS)
            .add(
                Blocks.SANDSTONE_STAIRS,
                Blocks.SMOOTH_SANDSTONE_STAIRS
            );
        tag(Tags.Blocks.STONES, Tags.Items.STONES)
            .add(
                Blocks.ANDESITE,
                Blocks.DIORITE,
                Blocks.GRANITE,
                Blocks.STONE,
                Blocks.DEEPSLATE,
                Blocks.TUFF
            );
        tag(Tags.Blocks.STORAGE_BLOCKS, Tags.Items.STORAGE_BLOCKS)
            .addTags(
                Tags.Blocks.STORAGE_BLOCKS_BONE_MEAL,
                Tags.Blocks.STORAGE_BLOCKS_COAL,
                Tags.Blocks.STORAGE_BLOCKS_COPPER,
                Tags.Blocks.STORAGE_BLOCKS_DIAMOND,
                Tags.Blocks.STORAGE_BLOCKS_DRIED_KELP,
                Tags.Blocks.STORAGE_BLOCKS_EMERALD,
                Tags.Blocks.STORAGE_BLOCKS_GOLD,
                Tags.Blocks.STORAGE_BLOCKS_IRON,
                Tags.Blocks.STORAGE_BLOCKS_LAPIS,
                Tags.Blocks.STORAGE_BLOCKS_NETHERITE,
                Tags.Blocks.STORAGE_BLOCKS_RAW_COPPER,
                Tags.Blocks.STORAGE_BLOCKS_RAW_GOLD,
                Tags.Blocks.STORAGE_BLOCKS_RAW_IRON,
                Tags.Blocks.STORAGE_BLOCKS_REDSTONE,
                Tags.Blocks.STORAGE_BLOCKS_SLIME,
                Tags.Blocks.STORAGE_BLOCKS_WHEAT,
                Tags.Blocks.STORAGE_BLOCKS_RESIN
            );
        tag(Tags.Blocks.STORAGE_BLOCKS_AMETHYST, Tags.Items.STORAGE_BLOCKS_AMETHYST)
            .add(Blocks.AMETHYST_BLOCK);
        tag(Tags.Blocks.STORAGE_BLOCKS_BONE_MEAL, Tags.Items.STORAGE_BLOCKS_BONE_MEAL)
            .add(Blocks.BONE_BLOCK);
        tag(Tags.Blocks.STORAGE_BLOCKS_COAL, Tags.Items.STORAGE_BLOCKS_COAL)
            .add(Blocks.COAL_BLOCK);
        tag(Tags.Blocks.STORAGE_BLOCKS_COPPER, Tags.Items.STORAGE_BLOCKS_COPPER)
            .add(Blocks.COPPER_BLOCK);
        tag(Tags.Blocks.STORAGE_BLOCKS_DIAMOND, Tags.Items.STORAGE_BLOCKS_DIAMOND)
            .add(Blocks.DIAMOND_BLOCK);
        tag(Tags.Blocks.STORAGE_BLOCKS_DRIED_KELP, Tags.Items.STORAGE_BLOCKS_DRIED_KELP)
            .add(Blocks.DRIED_KELP_BLOCK);
        tag(Tags.Blocks.STORAGE_BLOCKS_EMERALD, Tags.Items.STORAGE_BLOCKS_EMERALD)
            .add(Blocks.EMERALD_BLOCK);
        tag(Tags.Blocks.STORAGE_BLOCKS_GOLD, Tags.Items.STORAGE_BLOCKS_GOLD)
            .add(Blocks.GOLD_BLOCK);
        tag(Tags.Blocks.STORAGE_BLOCKS_IRON, Tags.Items.STORAGE_BLOCKS_IRON)
            .add(Blocks.IRON_BLOCK);
        tag(Tags.Blocks.STORAGE_BLOCKS_LAPIS, Tags.Items.STORAGE_BLOCKS_LAPIS)
            .add(Blocks.LAPIS_BLOCK);
        tag(Tags.Blocks.STORAGE_BLOCKS_NETHERITE, Tags.Items.STORAGE_BLOCKS_NETHERITE)
            .add(Blocks.NETHERITE_BLOCK);
        tag(Tags.Blocks.STORAGE_BLOCKS_QUARTZ, Tags.Items.STORAGE_BLOCKS_QUARTZ)
            .add(Blocks.QUARTZ_BLOCK);
        tag(Tags.Blocks.STORAGE_BLOCKS_RAW_COPPER, Tags.Items.STORAGE_BLOCKS_RAW_COPPER)
            .add(Blocks.RAW_COPPER_BLOCK);
        tag(Tags.Blocks.STORAGE_BLOCKS_RAW_GOLD, Tags.Items.STORAGE_BLOCKS_RAW_GOLD)
            .add(Blocks.RAW_GOLD_BLOCK);
        tag(Tags.Blocks.STORAGE_BLOCKS_RAW_IRON, Tags.Items.STORAGE_BLOCKS_RAW_IRON)
            .add(Blocks.RAW_IRON_BLOCK);
        tag(Tags.Blocks.STORAGE_BLOCKS_RESIN, Tags.Items.STORAGE_BLOCKS_RESIN)
            .add(Blocks.RESIN_BLOCK);
        tag(Tags.Blocks.STORAGE_BLOCKS_REDSTONE, Tags.Items.STORAGE_BLOCKS_REDSTONE)
            .add(Blocks.REDSTONE_BLOCK);
        tag(Tags.Blocks.STORAGE_BLOCKS_SLIME, Tags.Items.STORAGE_BLOCKS_SLIME)
            .add(Blocks.SLIME_BLOCK);
        tag(Tags.Blocks.STORAGE_BLOCKS_WHEAT, Tags.Items.STORAGE_BLOCKS_WHEAT)
            .add(Blocks.HAY_BLOCK);
        tag(Tags.Blocks.STRIPPED_LOGS, Tags.Items.STRIPPED_LOGS)
            .add(
                Blocks.STRIPPED_ACACIA_LOG,
                Blocks.STRIPPED_BAMBOO_BLOCK,
                Blocks.STRIPPED_BIRCH_LOG,
                Blocks.STRIPPED_CHERRY_LOG,
                Blocks.STRIPPED_CRIMSON_STEM,
                Blocks.STRIPPED_DARK_OAK_LOG,
                Blocks.STRIPPED_JUNGLE_LOG,
                Blocks.STRIPPED_MANGROVE_LOG,
                Blocks.STRIPPED_OAK_LOG,
                Blocks.STRIPPED_PALE_OAK_LOG,
                Blocks.STRIPPED_SPRUCE_LOG,
                Blocks.STRIPPED_WARPED_STEM
            );
        tag(Tags.Blocks.STRIPPED_WOODS, Tags.Items.STRIPPED_WOODS)
            .add(
                Blocks.STRIPPED_ACACIA_WOOD,
                Blocks.STRIPPED_BIRCH_WOOD,
                Blocks.STRIPPED_CHERRY_WOOD,
                Blocks.STRIPPED_CRIMSON_HYPHAE,
                Blocks.STRIPPED_DARK_OAK_WOOD,
                Blocks.STRIPPED_JUNGLE_WOOD,
                Blocks.STRIPPED_MANGROVE_WOOD,
                Blocks.STRIPPED_OAK_WOOD,
                Blocks.STRIPPED_PALE_OAK_WOOD,
                Blocks.STRIPPED_SPRUCE_WOOD,
                Blocks.STRIPPED_WARPED_HYPHAE
            );
    }

    private static TagKey<Block> forgeTagKey(String path) {
        return BlockTags.create(Identifier.fromNamespaceAndPath("forge", path));
    }

    private static TagKey<Block> tagKey(String name) {
        return BlockTags.create(Identifier.withDefaultNamespace(name));
    }

    private void addColored(Consumer<Block> consumer, TagKey<Block> group, String pattern) {
        String prefix = group.location().getPath().toUpperCase(Locale.ENGLISH) + '_';
        for (DyeColor color  : DyeColor.values()) {
            Identifier key = Identifier.fromNamespaceAndPath("minecraft", pattern.replace("{color}",  color.getName()));
            TagKey<Block> blockTag = getForgeTag(Tags.Blocks.class, prefix + color.getName());
            TagKey<Item> itemTag = getForgeTag(Tags.Items.class, prefix + color.getName());
            Block block = ForgeRegistries.BLOCKS.getValue(key);
            if (block == null || block  == Blocks.AIR)
                throw new IllegalStateException("Unknown vanilla block: " + key.toString());
            tag(blockTag, itemTag).add(block);
            consumer.accept(block);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> TagKey<T> getForgeTag(Class<?> cls, String name) {
        try {
            name = name.toUpperCase(Locale.ENGLISH);
            return (TagKey<T>)cls.getDeclaredField(name).get(null);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            throw new IllegalStateException(cls.getName() + " is missing tag name: " + name);
        }
    }

    private static Identifier forgeRl(String path) {
        return Identifier.fromNamespaceAndPath("forge", path);
    }

    private TagAppender<Block, Block> tag(TagKey<Block> block, TagKey<Item> item, TagKey<Block> oldBlock, TagKey<Item> oldItem) {
        var tag = tag(block, item);
        var old = tag(oldBlock, oldItem);
        return wrap(tag, old, oldBlock);
    }

    private static TagAppender<Block, Block> wrap(TagAppender<Block, Block> tag, TagAppender<Block, Block> old, TagKey<Block> oldBlock) {
        return new TagAppender<Block, Block>() {
            @Override
            public TagAppender<Block, Block> add(Block value) {
                tag.add(value);
                old.add(value);
                return this;
            }

            @Override
            public TagAppender<Block, Block> addOptional(Block value) {
                tag.addOptional(value);
                old.addOptional(value);
                return this;
            }

            @Override
            public TagAppender<Block, Block> addTag(TagKey<Block> value) {
                tag.addTag(value);
                old.addTag(value);
                return this;
            }

            @Override
            public TagAppender<Block, Block> addOptionalTag(TagKey<Block> value) {
                tag.addOptionalTag(value);
                if (value != oldBlock)
                    old.addOptionalTag(value);
                return this;
            }

            @Override
            public TagAppender<Block, Block> replace(boolean value) {
                tag.replace(value);
                old.replace(value);
                return this;
            }

            @Override
            public TagAppender<Block, Block> remove(Identifier value) {
                tag.remove(value);
                old.remove(value);
                return this;
            }

            @Override
            public TagAppender<Block, Block> remove(TagKey<Block> value) {
                tag.remove(value);
                old.remove(value);
                return this;
            }

            @Override
            public TagAppender<Block, Block> remove(Block value) {
                tag.remove(value);
                old.remove(value);
                return this;
            }

            @Override
            public String getSourceName() {
                return tag.getSourceName();
            }
        };
    }
}
