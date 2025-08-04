/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.data;

import java.util.Locale;
import java.util.function.Consumer;

import net.minecraft.data.tags.BlockItemTagsProvider;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
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
            .addTag(Tags.Blocks.BARRELS_WOODEN)
            .addOptionalTag(Legacy.Blocks.BARRELS);
        tag(Tags.Blocks.BARRELS_WOODEN, Tags.Items.BARRELS_WOODEN, Legacy.Blocks.BARRELS_WOODEN, Legacy.Items.BARRELS_WOODEN)
            .add(Blocks.BARREL)
            .addOptionalTag(Legacy.Blocks.BARRELS_WOODEN);
        tag(Tags.Blocks.BOOKSHELVES, Tags.Items.BOOKSHELVES, Legacy.Blocks.BOOKSHELVES, Legacy.Items.BOOKSHELVES)
            .add(Blocks.BOOKSHELF)
            .addOptionalTag(Legacy.Blocks.BOOKSHELVES);
        tag(Tags.Blocks.BUDDING_BLOCKS, Tags.Items.BUDDING_BLOCKS)
            .add(Blocks.BUDDING_AMETHYST);
        tag(Tags.Blocks.BUDS, Tags.Items.BUDS)
            .add(Blocks.SMALL_AMETHYST_BUD)
            .add(Blocks.MEDIUM_AMETHYST_BUD)
            .add(Blocks.LARGE_AMETHYST_BUD);
        tag(Tags.Blocks.CHAINS, Tags.Items.CHAINS)
            .add(Blocks.CHAIN);
        tag(Tags.Blocks.CHESTS, Tags.Items.CHESTS)
            .addTags(
                Tags.Blocks.CHESTS_ENDER,
                Tags.Blocks.CHESTS_TRAPPED,
                Tags.Blocks.CHESTS_WOODEN
            )
            .addOptionalTag(Legacy.Blocks.CHESTS);
        //copy(forgeBlockTagKey("chests/ender"), forgeItemTagKey("chests/ender"));
        tag(Tags.Blocks.CHESTS_ENDER, Tags.Items.CHESTS_ENDER)
            .add(Blocks.ENDER_CHEST); // forge:chests/ender
        //copy(forgeBlockTagKey("chests/trapped"), forgeItemTagKey("chests/trapped")));
        tag(Tags.Blocks.CHESTS_TRAPPED, Tags.Items.CHESTS_TRAPPED)
            .add(Blocks.TRAPPED_CHEST); // forge:chests/trapped
        tag(Tags.Blocks.CHESTS_WOODEN, Tags.Items.CHESTS_WOODEN, Legacy.Blocks.CHESTS_WOODEN, Legacy.Items.CHESTS_WOODEN)
            .add(
                Blocks.CHEST,
                Blocks.TRAPPED_CHEST
            )
            .addOptionalTag(Legacy.Blocks.CHESTS_WOODEN);
        tag(Tags.Blocks.CLUSTERS, Tags.Items.CLUSTERS)
            .add(Blocks.AMETHYST_CLUSTER);
        tag(Tags.Blocks.COBBLESTONES, Tags.Items.COBBLESTONES, Legacy.Blocks.COBBLESTONE, Legacy.Items.COBBLESTONE)
            .addTags(
                Tags.Blocks.COBBLESTONE_NORMAL,
                Tags.Blocks.COBBLESTONE_INFESTED,
                Tags.Blocks.COBBLESTONE_MOSSY,
                Tags.Blocks.COBBLESTONE_DEEPSLATE
            )
            .addOptionalTag(Legacy.Blocks.COBBLESTONE);
        //copy(forgeBlockTagKey("cobblestone/normal"), forgeItemTagKey("cobblestone/normal"));
        tag(Tags.Blocks.COBBLESTONE_NORMAL, Tags.Items.COBBLESTONE_NORMAL)
            .add(Blocks.COBBLESTONE); // forge:cobblestone/normal
        //copy(forgeBlockTagKey("cobblestone/infested"), forgeItemTagKey("cobblestone/infested"));
        tag(Tags.Blocks.COBBLESTONE_INFESTED, Tags.Items.COBBLESTONE_INFESTED)
            .add(Blocks.INFESTED_COBBLESTONE); // forge:cobblestone/infested
        //copy(forgeBlockTagKey("cobblestone/mossy"), forgeItemTagKey("cobblestone/mossy"));
        tag(Tags.Blocks.COBBLESTONE_MOSSY, Tags.Items.COBBLESTONE_MOSSY)
            .add(Blocks.MOSSY_COBBLESTONE); // forge:cobblestone/mossy
        //copy(forgeBlockTagKey("cobblestone/deepslate"), forgeItemTagKey("cobblestone/deepslate"));
        tag(Tags.Blocks.COBBLESTONE_DEEPSLATE, Tags.Items.COBBLESTONE_DEEPSLATE)
            .add(Blocks.COBBLED_DEEPSLATE); // forge:cobblestone/deepslate
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
        //copy(forgeBlockTagKey("end_stones"), forgeItemTagKey("end_stones"));
        tag(Tags.Blocks.END_STONES, Tags.Items.END_STONES)
            .add(Blocks.END_STONE); // forge:end_stones
        //copy(forgeBlockTagKey("fence_gates"), forgeItemTagKey("fence_gates"));
        tag(Tags.Blocks.FENCE_GATES, Tags.Items.FENCE_GATES)
            .addTags(Tags.Blocks.FENCE_GATES_WOODEN); // forge:fence_gates
        //copy(forgeBlockTagKey("fence_gates/wooden"), forgeItemTagKey("fence_gates/wooden"));
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
        //copy(forgeBlockTagKey("fences"), forgeItemTagKey("fences"));
        tag(Tags.Blocks.FENCES, Tags.Items.FENCES)
            .addTags(
                Tags.Blocks.FENCES_NETHER_BRICK,
                Tags.Blocks.FENCES_WOODEN
            ); // forge:fences
        //copy(forgeBlockTagKey("fences/nether_brick"), forgeItemTagKey("fences/nether_brick"));
        tag(Tags.Blocks.FENCES_NETHER_BRICK, Tags.Items.FENCES_NETHER_BRICK)
            .add(Blocks.NETHER_BRICK_FENCE); // forge:fences/nether_brick
        //copy(forgeBlockTagKey("fences/wooden"), forgeItemTagKey("fences/wooden"));
        tag(Tags.Blocks.FENCES_WOODEN, Tags.Items.FENCES_WOODEN)
            .addTag(BlockTags.WOODEN_FENCES); // forge:fences/wooden
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
            )
            .addOptionalTag(BlockTags.SMALL_FLOWERS);
        tag(Tags.Blocks.FLOWERS_TALL, Tags.Items.FLOWERS_TALL)
            .add(
                Blocks.SUNFLOWER,
                Blocks.LILAC,
                Blocks.PEONY,
                Blocks.ROSE_BUSH,
                Blocks.PITCHER_PLANT
            )
            .addOptionalTag(tagKey("tall_flowers")); //This is old vanilla tag, should it be removed?
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
            )
            .addOptionalTag(Tags.Blocks.GLASS);
        tag(Tags.Blocks.GLASS_BLOCKS_COLORLESS, Tags.Items.GLASS_BLOCKS_COLORLESS)
            .add(Blocks.GLASS)
            .addOptionalTag(Tags.Blocks.GLASS_COLORLESS);
        tag(Tags.Blocks.GLASS_BLOCKS_TINTED, Tags.Items.GLASS_BLOCKS_TINTED)
            .add(Blocks.TINTED_GLASS)
            .addOptionalTag(Legacy.Blocks.GLASS_TINTED);

        tag(Tags.Blocks.GLASS_BLOCKS_CHEAP, Tags.Items.GLASS_BLOCKS_CHEAP, Legacy.Blocks.GLASS_SILICA, Legacy.Items.GLASS_SILICA)
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
            )
            .addOptionalTag(Legacy.Blocks.GLASS_SILICA);
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
            )
            .addOptionalTag(Legacy.Blocks.GLASS_PANES);
        tag(Tags.Blocks.GLASS_PANES_COLORLESS, Tags.Items.GLASS_PANES_COLORLESS)
            .add(Blocks.GLASS_PANE)
            .addOptionalTag(forgeTagKey("glass_panes/colorless"));
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
        //copy(forgeBlockTagKey("gravel"), forgeItemTagKey("gravel"));
        tag(Tags.Blocks.GRAVEL, Tags.Items.GRAVEL)
            .add(Blocks.GRAVEL); // forge:gravel
        //copy(forgeBlockTagKey("netherrack"), forgeItemTagKey("netherrack"));
        tag(Tags.Blocks.NETHERRACK, Tags.Items.NETHERRACK)
            .add(Blocks.NETHERRACK); // forge:netherrack
        tag(Tags.Blocks.OBSIDIANS, Tags.Items.OBSIDIANS)
            .addTags(
                Tags.Blocks.OBSIDIANS_NORMAL,
                Tags.Blocks.OBSIDIANS_CRYING
            )
            .addOptionalTag(Legacy.Blocks.OBSIDIAN);
        tag(Tags.Blocks.OBSIDIANS_NORMAL, Tags.Items.OBSIDIANS_NORMAL)
            .add(Blocks.OBSIDIAN);
        tag(Tags.Blocks.OBSIDIANS_CRYING, Tags.Items.OBSIDIANS_CRYING)
            .add(Blocks.CRYING_OBSIDIAN);
        tag(Tags.Blocks.ORE_BEARING_GROUND_DEEPSLATE, Tags.Items.ORE_BEARING_GROUND_DEEPSLATE)
            .add(Blocks.DEEPSLATE); // forge:ore_bearing_ground/deepslate
        tag(Tags.Blocks.ORE_BEARING_GROUND_NETHERRACK, Tags.Items.ORE_BEARING_GROUND_NETHERRACK)
            .add(Blocks.NETHERRACK); // forge:ore_bearing_ground/netherrack
        tag(Tags.Blocks.ORE_BEARING_GROUND_STONE, Tags.Items.ORE_BEARING_GROUND_STONE)
            .add(Blocks.STONE); // forge:ore_bearing_ground/stone
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
            )
            .addOptionalTag(Legacy.Blocks.ORES);
        tag(Tags.Blocks.ORES_COAL, Tags.Items.ORES_COAL)
            .addTag(BlockTags.COAL_ORES); // forge:ores/coal
        tag(Tags.Blocks.ORES_COPPER, Tags.Items.ORES_COPPER)
            .addTag(BlockTags.COPPER_ORES); // forge:ores/copper
        tag(Tags.Blocks.ORES_DIAMOND, Tags.Items.ORES_DIAMOND)
            .addTag(BlockTags.DIAMOND_ORES); // forge:ores/diamond
        tag(Tags.Blocks.ORES_EMERALD, Tags.Items.ORES_EMERALD)
            .addTag(BlockTags.EMERALD_ORES); // forge:ores/emerald
        tag(Tags.Blocks.ORES_GOLD, Tags.Items.ORES_GOLD)
            .addTag(BlockTags.GOLD_ORES); // forge:ores/gold
        tag(Tags.Blocks.ORES_IRON, Tags.Items.ORES_IRON)
            .addTag(BlockTags.IRON_ORES); // forge:ores/iron
        tag(Tags.Blocks.ORES_LAPIS, Tags.Items.ORES_LAPIS)
            .addTag(BlockTags.LAPIS_ORES); // forge:ores/lapis
        tag(Tags.Blocks.ORES_QUARTZ, Tags.Items.ORES_QUARTZ, Legacy.Blocks.ORES_QUARTZ, Legacy.Items.QUARTZ_ORES)
            .add(Blocks.NETHER_QUARTZ_ORE)
            .addOptionalTag(Legacy.Blocks.ORES_QUARTZ);
        tag(Tags.Blocks.ORES_REDSTONE, Tags.Items.ORES_REDSTONE)
            .addTag(BlockTags.REDSTONE_ORES); // forge:ores/redstone
        tag(Tags.Blocks.ORES_NETHERITE_SCRAP, Tags.Items.ORES_NETHERITE_SCRAP, Legacy.Blocks.ORES_NETHERITE_SCRAP, Legacy.Items.ORES_NEHTERITE_SCRAP)
            .add(Blocks.ANCIENT_DEBRIS)
            .addOptionalTag(Legacy.Blocks.ORES_NETHERITE_SCRAP);
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
        tag(Tags.Blocks.SAND, Tags.Items.SAND) // forge:sand
            .addTags(
                Tags.Blocks.SAND_COLORLESS,
                Tags.Blocks.SAND_RED
            );
        tag(Tags.Blocks.SAND_COLORLESS, Tags.Items.SAND_COLORLESS) // forge:sand/colorless
            .add(Blocks.SAND);
        tag(Tags.Blocks.SAND_RED, Tags.Items.SAND_RED) // forge:sand/red
            .add(Blocks.RED_SAND);
        tag(Tags.Blocks.SANDSTONE_BLOCKS, Tags.Items.SANDSTONE_BLOCKS)
            .addTags(
                Tags.Blocks.SANDSTONE_RED_BLOCKS,
                Tags.Blocks.SANDSTONE_UNCOLORED_BLOCKS
            )
            .addOptionalTag(Legacy.Blocks.SANDSTONE);
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
            //.addOptionalTag(forgeTagKey("stone")); // can't add this because it would include infested/polished variants which aren't contained in Fabric's `c:stones`
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
                Tags.Blocks.STORAGE_BLOCKS_WHEAT
            )
            //.addOptionalTag(forgeTagKey("storage_blocks")); // can't add this because it would include contents from the non-common forge:storage_blocks/amethyst and forge:storage_blocks/quartz, which are not in the c namespace
            .addOptionalTags(
                Legacy.Blocks.STORAGE_BLOCKS_COAL,
                Legacy.Blocks.STORAGE_BLOCKS_COPPER,
                Legacy.Blocks.STORAGE_BLOCKS_DIAMOND,
                Legacy.Blocks.STORAGE_BLOCKS_EMERALD,
                Legacy.Blocks.STORAGE_BLOCKS_GOLD,
                Legacy.Blocks.STORAGE_BLOCKS_IRON,
                Legacy.Blocks.STORAGE_BLOCKS_LAPIS,
                Legacy.Blocks.STORAGE_BLOCKS_NETHERITE,
                Legacy.Blocks.STORAGE_BLOCKS_RAW_COPPER,
                Legacy.Blocks.STORAGE_BLOCKS_RAW_GOLD,
                Legacy.Blocks.STORAGE_BLOCKS_RAW_IRON,
                Legacy.Blocks.STORAGE_BLOCKS_REDSTONE
            );
        tag(Tags.Blocks.STORAGE_BLOCKS_AMETHYST, Tags.Items.STORAGE_BLOCKS_AMETHYST)
            .add(Blocks.AMETHYST_BLOCK);
        tag(Tags.Blocks.STORAGE_BLOCKS_BONE_MEAL, Tags.Items.STORAGE_BLOCKS_BONE_MEAL)
            .add(Blocks.BONE_BLOCK);
        tag(Tags.Blocks.STORAGE_BLOCKS_COAL, Tags.Items.STORAGE_BLOCKS_COAL, Legacy.Blocks.STORAGE_BLOCKS_COAL, Legacy.Items.STORAGE_BLOCKS_COAL)
            .add(Blocks.COAL_BLOCK)
            .addOptionalTag(Legacy.Blocks.STORAGE_BLOCKS_COAL);
        tag(Tags.Blocks.STORAGE_BLOCKS_COPPER, Tags.Items.STORAGE_BLOCKS_COPPER, Legacy.Blocks.STORAGE_BLOCKS_COPPER, Legacy.Items.STORAGE_BLOCKS_COPPER)
            .add(Blocks.COPPER_BLOCK)
            .addOptionalTag(Legacy.Blocks.STORAGE_BLOCKS_COPPER);
        tag(Tags.Blocks.STORAGE_BLOCKS_DIAMOND, Tags.Items.STORAGE_BLOCKS_DIAMOND, Legacy.Blocks.STORAGE_BLOCKS_DIAMOND, Legacy.Items.STORAGE_BLOCKS_DIAMOND)
            .add(Blocks.DIAMOND_BLOCK)
            .addOptionalTag(Legacy.Blocks.STORAGE_BLOCKS_DIAMOND);
        tag(Tags.Blocks.STORAGE_BLOCKS_DRIED_KELP, Tags.Items.STORAGE_BLOCKS_DRIED_KELP)
            .add(Blocks.DRIED_KELP_BLOCK);
        tag(Tags.Blocks.STORAGE_BLOCKS_EMERALD, Tags.Items.STORAGE_BLOCKS_EMERALD, Legacy.Blocks.STORAGE_BLOCKS_EMERALD, Legacy.Items.STORAGE_BLOCKS_EMERALD)
            .add(Blocks.EMERALD_BLOCK)
            .addOptionalTag(Legacy.Blocks.STORAGE_BLOCKS_EMERALD);
        tag(Tags.Blocks.STORAGE_BLOCKS_GOLD, Tags.Items.STORAGE_BLOCKS_GOLD, Legacy.Blocks.STORAGE_BLOCKS_GOLD, Legacy.Items.STORAGE_BLOCKS_GOLD)
            .add(Blocks.GOLD_BLOCK)
            .addOptionalTag(Legacy.Blocks.STORAGE_BLOCKS_GOLD);
        tag(Tags.Blocks.STORAGE_BLOCKS_IRON, Tags.Items.STORAGE_BLOCKS_IRON, Legacy.Blocks.STORAGE_BLOCKS_IRON, Legacy.Items.STORAGE_BLOCKS_IRON)
            .add(Blocks.IRON_BLOCK)
            .addOptionalTag(Legacy.Blocks.STORAGE_BLOCKS_IRON);
        tag(Tags.Blocks.STORAGE_BLOCKS_LAPIS, Tags.Items.STORAGE_BLOCKS_LAPIS, Legacy.Blocks.STORAGE_BLOCKS_LAPIS, Legacy.Items.STORAGE_BLOCKS_LAPIS)
            .add(Blocks.LAPIS_BLOCK)
            .addOptionalTag(Legacy.Blocks.STORAGE_BLOCKS_LAPIS);
        tag(Tags.Blocks.STORAGE_BLOCKS_NETHERITE, Tags.Items.STORAGE_BLOCKS_NETHERITE, Legacy.Blocks.STORAGE_BLOCKS_NETHERITE, Legacy.Items.STORAGE_BLOCKS_NETHERITE)
            .add(Blocks.NETHERITE_BLOCK)
            .addOptionalTag(Legacy.Blocks.STORAGE_BLOCKS_NETHERITE);
        tag(Tags.Blocks.STORAGE_BLOCKS_QUARTZ, Tags.Items.STORAGE_BLOCKS_QUARTZ)
            .add(Blocks.QUARTZ_BLOCK);
        tag(Tags.Blocks.STORAGE_BLOCKS_RAW_COPPER, Tags.Items.STORAGE_BLOCKS_RAW_COPPER, Legacy.Blocks.STORAGE_BLOCKS_RAW_COPPER, Legacy.Items.STORAGE_BLOCKS_RAW_COPPER)
            .add(Blocks.RAW_COPPER_BLOCK)
            .addOptionalTag(Legacy.Blocks.STORAGE_BLOCKS_RAW_COPPER);
        tag(Tags.Blocks.STORAGE_BLOCKS_RAW_GOLD, Tags.Items.STORAGE_BLOCKS_RAW_GOLD, Legacy.Blocks.STORAGE_BLOCKS_RAW_GOLD, Legacy.Items.STORAGE_BLOCKS_RAW_GOLD)
            .add(Blocks.RAW_GOLD_BLOCK)
            .addOptionalTag(Legacy.Blocks.STORAGE_BLOCKS_RAW_GOLD);
        tag(Tags.Blocks.STORAGE_BLOCKS_RAW_IRON, Tags.Items.STORAGE_BLOCKS_RAW_IRON, Legacy.Blocks.STORAGE_BLOCKS_RAW_IRON, Legacy.Items.STORAGE_BLOCKS_RAW_IRON)
            .add(Blocks.RAW_IRON_BLOCK)
            .addOptionalTag(Legacy.Blocks.STORAGE_BLOCKS_RAW_IRON);
        tag(Tags.Blocks.STORAGE_BLOCKS_REDSTONE, Tags.Items.STORAGE_BLOCKS_REDSTONE, Legacy.Blocks.STORAGE_BLOCKS_REDSTONE, Legacy.Items.STORAGE_BLOCKS_REDSTONE)
            .add(Blocks.REDSTONE_BLOCK)
            .addOptionalTag(Legacy.Blocks.STORAGE_BLOCKS_REDSTONE);
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
                Blocks.STRIPPED_DARK_OAK_LOG,
                Blocks.STRIPPED_JUNGLE_LOG,
                Blocks.STRIPPED_MANGROVE_LOG,
                Blocks.STRIPPED_OAK_LOG,
                Blocks.STRIPPED_SPRUCE_LOG
            );
        tag(Tags.Blocks.STRIPPED_WOODS, Tags.Items.STRIPPED_WOODS)
            .add(
                Blocks.STRIPPED_ACACIA_WOOD,
                Blocks.STRIPPED_BIRCH_WOOD,
                Blocks.STRIPPED_CHERRY_WOOD,
                Blocks.STRIPPED_DARK_OAK_WOOD,
                Blocks.STRIPPED_JUNGLE_WOOD,
                Blocks.STRIPPED_MANGROVE_WOOD,
                Blocks.STRIPPED_OAK_WOOD,
                Blocks.STRIPPED_SPRUCE_WOOD
            );

        // Backwards compat definitions for pre-1.21 legacy `forge:` tags.
        // TODO: [Forge][Tags][Old] Remove backwards compat tag entries in 1.22
        tag(Legacy.Blocks.BARRELS, Legacy.Items.BARRELS)
            .addTag(Legacy.Blocks.BARRELS_WOODEN);

        tag(Legacy.Blocks.CHESTS, Legacy.Items.CHESTS)
            .addTags(
                Tags.Blocks.CHESTS_ENDER,
                Tags.Blocks.CHESTS_TRAPPED,
                Legacy.Blocks.CHESTS_WOODEN
            );

        //copy(forgeBlockTagKey("glass/tinted"), forgeItemTagKey("glass/tinted"));
        addColored(tag(Tags.Blocks.STAINED_GLASS, Tags.Items.STAINED_GLASS)::add, Tags.Blocks.GLASS, "{color}_stained_glass");
        addColored(tag(Tags.Blocks.STAINED_GLASS_PANES, Tags.Items.STAINED_GLASS_PANES)::add, Tags.Blocks.GLASS_PANES, "{color}_stained_glass_pane");

        // tag(Tags.Blocks.GLASS, Tags.Items.GLASS, forgeBlockTagKey("glass"), forgeItemTagKey("glass")) // these are the same thing
        tag(Tags.Blocks.GLASS, Tags.Items.GLASS)
            .addTags(
                Tags.Blocks.GLASS_COLORLESS,
                Tags.Blocks.STAINED_GLASS,
                Legacy.Blocks.GLASS_TINTED
            );
        tag(Tags.Blocks.GLASS_COLORLESS, Tags.Items.GLASS_COLORLESS)
            .add(Blocks.GLASS);
        tag(Legacy.Blocks.GLASS_PANES, Legacy.Items.GLASS_PANES)
            .addTags(
                Legacy.Blocks.GLASS_PANES_COLORLESS,
                Tags.Blocks.STAINED_GLASS_PANES
            );
        tag(Legacy.Blocks.GLASS_PANES_COLORLESS, Legacy.Items.GLASS_PANES_COLORLESS)
            .add(Blocks.GLASS_PANE);
        tag(Legacy.Blocks.GLASS_TINTED, Legacy.Items.GLASS_TINTED)
            .add(Blocks.TINTED_GLASS);
        tag(Legacy.Blocks.OBSIDIAN, Legacy.Items.OBSIDIAN)
            .add(Blocks.OBSIDIAN);
        tag(Legacy.Blocks.ORES, Legacy.Items.ORES)
            .addTags(
                Tags.Blocks.ORES_COAL,
                Tags.Blocks.ORES_COPPER,
                Tags.Blocks.ORES_DIAMOND,
                Tags.Blocks.ORES_EMERALD,
                Tags.Blocks.ORES_GOLD,
                Tags.Blocks.ORES_IRON,
                Tags.Blocks.ORES_LAPIS,
                Tags.Blocks.ORES_REDSTONE,
                Legacy.Blocks.ORES_QUARTZ,
                Legacy.Blocks.ORES_NETHERITE_SCRAP
            );
        tag(Legacy.Blocks.SANDSTONE, Legacy.Items.SANDSTONE)
            .add(
                Blocks.SANDSTONE,
                Blocks.CUT_SANDSTONE,
                Blocks.CHISELED_SANDSTONE,
                Blocks.SMOOTH_SANDSTONE,
                Blocks.RED_SANDSTONE,
                Blocks.CUT_RED_SANDSTONE,
                Blocks.CHISELED_RED_SANDSTONE,
                Blocks.SMOOTH_RED_SANDSTONE
            );
        tag(Legacy.Blocks.STONE, Legacy.Items.STONE)
            .add(
                Blocks.ANDESITE,
                Blocks.DIORITE,
                Blocks.GRANITE,
                Blocks.INFESTED_STONE,
                Blocks.STONE,
                Blocks.POLISHED_ANDESITE,
                Blocks.POLISHED_DIORITE,
                Blocks.POLISHED_GRANITE,
                Blocks.DEEPSLATE,
                Blocks.POLISHED_DEEPSLATE,
                Blocks.INFESTED_DEEPSLATE,
                Blocks.TUFF
            );

        tag(Legacy.Blocks.STORAGE_BLOCKS, Legacy.Items.STORAGE_BLOCKS)
            .addTags(
                Tags.Blocks.STORAGE_BLOCKS_AMETHYST,
                Legacy.Blocks.STORAGE_BLOCKS_COAL,
                Legacy.Blocks.STORAGE_BLOCKS_COPPER,
                Legacy.Blocks.STORAGE_BLOCKS_DIAMOND,
                Legacy.Blocks.STORAGE_BLOCKS_EMERALD,
                Legacy.Blocks.STORAGE_BLOCKS_GOLD,
                Legacy.Blocks.STORAGE_BLOCKS_IRON,
                Legacy.Blocks.STORAGE_BLOCKS_LAPIS,
                Tags.Blocks.STORAGE_BLOCKS_QUARTZ,
                Legacy.Blocks.STORAGE_BLOCKS_RAW_COPPER,
                Legacy.Blocks.STORAGE_BLOCKS_RAW_GOLD,
                Legacy.Blocks.STORAGE_BLOCKS_RAW_IRON,
                Legacy.Blocks.STORAGE_BLOCKS_REDSTONE,
                Legacy.Blocks.STORAGE_BLOCKS_NETHERITE
            );
    }

    private static TagKey<Block> forgeTagKey(String path) {
        return BlockTags.create(ResourceLocation.fromNamespaceAndPath("forge", path));
    }

    private static TagKey<Block> tagKey(String name) {
        return BlockTags.create(ResourceLocation.withDefaultNamespace(name));
    }

    private void addColored(Consumer<Block> consumer, TagKey<Block> group, String pattern) {
        String prefix = group.location().getPath().toUpperCase(Locale.ENGLISH) + '_';
        for (DyeColor color  : DyeColor.values()) {
            ResourceLocation key = ResourceLocation.fromNamespaceAndPath("minecraft", pattern.replace("{color}",  color.getName()));
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

    private static ResourceLocation forgeRl(String path) {
        return ResourceLocation.fromNamespaceAndPath("forge", path);
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
            public TagAppender<Block, Block> remove(ResourceLocation value) {
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

    private static class Legacy {
        @Deprecated
        private static class Blocks {
            private static TagKey<Block> tag(String path) {
                return BlockTags.create(forgeRl(path));
            }

            public static final TagKey<Block> BARRELS = tag("barrels");
            public static final TagKey<Block> BARRELS_WOODEN = tag("barrels/wooden");
            public static final TagKey<Block> BOOKSHELVES = tag("bookshelves");
            public static final TagKey<Block> CHESTS = tag("chests");
            public static final TagKey<Block> CHESTS_WOODEN = tag("chests/wooden");
            public static final TagKey<Block> COBBLESTONE = tag("cobblestone");
            public static final TagKey<Block> GLASS_PANES = tag("glass_panes");
            public static final TagKey<Block> GLASS_PANES_COLORLESS = tag("glass_panes/colorless");
            public static final TagKey<Block> GLASS_SILICA = tag("glass/silica");
            public static final TagKey<Block> GLASS_TINTED = tag("glass/tinted");
            public static final TagKey<Block> OBSIDIAN = tag("obsidian");
            public static final TagKey<Block> ORES = tag("ores");
            public static final TagKey<Block> ORES_QUARTZ = tag("ores/quartz");
            public static final TagKey<Block> ORES_NETHERITE_SCRAP = tag("ores/netherite_scrap");
            public static final TagKey<Block> SANDSTONE = tag("sandstone");
            public static final TagKey<Block> STONE = tag("stone");
            public static final TagKey<Block> STORAGE_BLOCKS = tag("storage_blocks");
            public static final TagKey<Block> STORAGE_BLOCKS_COAL = tag("storage_blocks/coal");
            public static final TagKey<Block> STORAGE_BLOCKS_COPPER = tag("storage_blocks/copper");
            public static final TagKey<Block> STORAGE_BLOCKS_DIAMOND = tag("storage_blocks/diamond");
            public static final TagKey<Block> STORAGE_BLOCKS_EMERALD = tag("storage_blocks/emerald");
            public static final TagKey<Block> STORAGE_BLOCKS_GOLD = tag("storage_blocks/gold");
            public static final TagKey<Block> STORAGE_BLOCKS_IRON = tag("storage_blocks/iron");
            public static final TagKey<Block> STORAGE_BLOCKS_LAPIS = tag("storage_blocks/lapis");
            public static final TagKey<Block> STORAGE_BLOCKS_NETHERITE = tag("storage_blocks/netherite");
            public static final TagKey<Block> STORAGE_BLOCKS_RAW_COPPER = tag("storage_blocks/raw_copper");
            public static final TagKey<Block> STORAGE_BLOCKS_RAW_GOLD = tag("storage_blocks/raw_gold");
            public static final TagKey<Block> STORAGE_BLOCKS_RAW_IRON = tag("storage_blocks/raw_iron");
            public static final TagKey<Block> STORAGE_BLOCKS_REDSTONE = tag("storage_blocks/redstone");
        }

        @Deprecated
        private static class Items {
            private static TagKey<Item> tag(String path) {
                return ItemTags.create(forgeRl(path));
            }

            public static final TagKey<Item> BARRELS = tag("barrels");
            public static final TagKey<Item> BARRELS_WOODEN = tag("barrels/wooden");
            public static final TagKey<Item> BOOKSHELVES = tag("bookshelves");
            public static final TagKey<Item> CHESTS = tag("chests");
            public static final TagKey<Item> CHESTS_WOODEN = tag("chests/wooden");
            public static final TagKey<Item> COBBLESTONE = tag("cobblestone");
            public static final TagKey<Item> GLASS_PANES = tag("glass_panes");
            public static final TagKey<Item> GLASS_PANES_COLORLESS = tag("glass_panes/colorless");
            public static final TagKey<Item> GLASS_SILICA = tag("glass/silica");
            public static final TagKey<Item> GLASS_TINTED = tag("glass/tinted");
            public static final TagKey<Item> OBSIDIAN = tag("obsidian");
            public static final TagKey<Item> ORES = tag("ores");
            public static final TagKey<Item> QUARTZ_ORES = tag("ores/quartz");
            public static final TagKey<Item> ORES_NEHTERITE_SCRAP = tag("ores/netherite_scrap");
            public static final TagKey<Item> SANDSTONE = tag("sandstone");
            public static final TagKey<Item> STONE = tag("stone");
            public static final TagKey<Item> STORAGE_BLOCKS = tag("storage_blocks");
            public static final TagKey<Item> STORAGE_BLOCKS_COAL = tag("storage_blocks/coal");
            public static final TagKey<Item> STORAGE_BLOCKS_COPPER = tag("storage_blocks/copper");
            public static final TagKey<Item> STORAGE_BLOCKS_DIAMOND = tag("storage_blocks/diamond");
            public static final TagKey<Item> STORAGE_BLOCKS_EMERALD = tag("storage_blocks/emerald");
            public static final TagKey<Item> STORAGE_BLOCKS_GOLD = tag("storage_blocks/gold");
            public static final TagKey<Item> STORAGE_BLOCKS_IRON = tag("storage_blocks/iron");
            public static final TagKey<Item> STORAGE_BLOCKS_LAPIS = tag("storage_blocks/lapis");
            public static final TagKey<Item> STORAGE_BLOCKS_NETHERITE = tag("storage_blocks/netherite");
            public static final TagKey<Item> STORAGE_BLOCKS_RAW_COPPER = tag("storage_blocks/raw_copper");
            public static final TagKey<Item> STORAGE_BLOCKS_RAW_GOLD = tag("storage_blocks/raw_gold");
            public static final TagKey<Item> STORAGE_BLOCKS_RAW_IRON = tag("storage_blocks/raw_iron");
            public static final TagKey<Item> STORAGE_BLOCKS_REDSTONE = tag("storage_blocks/redstone");
        }
    }
}
