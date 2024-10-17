/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

// We typically don't do static imports as S2S can't remap them {as they are not qualified}, however this conflicts with vanilla and our tag class names, and our tags don't get obfed so its one line of warning.
import static net.minecraftforge.common.Tags.Blocks.*;

@ApiStatus.Internal
public final class ForgeBlockTagsProvider extends BlockTagsProvider {
    public ForgeBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, "forge", existingFileHelper);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void addTags(HolderLookup.Provider p_256380_) {
        tag(BARRELS)
                .addTag(BARRELS_WOODEN)
                .addOptionalTag(forgeTagKey("barrels"));
        tag(BARRELS_WOODEN)
                .add(Blocks.BARREL)
                .addOptionalTag(forgeTagKey("barrels/wooden"));
        tag(BOOKSHELVES)
                .add(Blocks.BOOKSHELF)
                .addOptionalTag(forgeTagKey("bookshelves"));
        tag(BUDDING_BLOCKS).add(Blocks.BUDDING_AMETHYST);
        tag(BUDS).add(Blocks.SMALL_AMETHYST_BUD).add(Blocks.MEDIUM_AMETHYST_BUD).add(Blocks.LARGE_AMETHYST_BUD);
        tag(CHAINS).add(Blocks.CHAIN);
        tag(CHESTS)
                .addTags(CHESTS_ENDER, CHESTS_TRAPPED, CHESTS_WOODEN)
                .addOptionalTag(forgeTagKey("chests"));
        tag(CHESTS_ENDER).add(Blocks.ENDER_CHEST); // forge:chests/ender
        tag(CHESTS_TRAPPED).add(Blocks.TRAPPED_CHEST); // forge:chests/trapped
        tag(CHESTS_WOODEN)
                .add(Blocks.CHEST, Blocks.TRAPPED_CHEST)
                .addOptionalTag(forgeTagKey("chests/wooden"));
        tag(CLUSTERS).add(Blocks.AMETHYST_CLUSTER);
        tag(COBBLESTONES)
                .addTags(COBBLESTONE_NORMAL, COBBLESTONE_INFESTED, COBBLESTONE_MOSSY, COBBLESTONE_DEEPSLATE)
                .addOptionalTag(forgeTagKey("cobblestone"));
        tag(COBBLESTONE_NORMAL).add(Blocks.COBBLESTONE); // forge:cobblestone/normal
        tag(COBBLESTONE_INFESTED).add(Blocks.INFESTED_COBBLESTONE); // forge:cobblestone/infested
        tag(COBBLESTONE_MOSSY).add(Blocks.MOSSY_COBBLESTONE); // forge:cobblestone/mossy
        tag(COBBLESTONE_DEEPSLATE).add(Blocks.COBBLED_DEEPSLATE); // forge:cobblestone/deepslate
        tag(CONCRETES).add(Blocks.WHITE_CONCRETE, Blocks.ORANGE_CONCRETE, Blocks.MAGENTA_CONCRETE, Blocks.LIGHT_BLUE_CONCRETE, Blocks.YELLOW_CONCRETE, Blocks.LIME_CONCRETE, Blocks.PINK_CONCRETE, Blocks.GRAY_CONCRETE, Blocks.LIGHT_GRAY_CONCRETE, Blocks.CYAN_CONCRETE, Blocks.PURPLE_CONCRETE, Blocks.BLUE_CONCRETE, Blocks.BROWN_CONCRETE, Blocks.GREEN_CONCRETE, Blocks.RED_CONCRETE, Blocks.BLACK_CONCRETE);
        addColored(DYED, "{color}_banner");
        addColored(DYED, "{color}_bed");
        addColored(DYED, "{color}_candle");
        addColored(DYED, "{color}_carpet");
        addColored(DYED, "{color}_concrete");
        addColored(DYED, "{color}_concrete_powder");
        addColored(DYED, "{color}_glazed_terracotta");
        addColored(DYED, "{color}_shulker_box");
        addColored(DYED, "{color}_stained_glass");
        addColored(DYED, "{color}_stained_glass_pane");
        addColored(DYED, "{color}_terracotta");
        addColored(DYED, "{color}_wall_banner");
        addColored(DYED, "{color}_wool");
        addColoredTags(tag(DYED)::addTag, DYED);
        tag(END_STONES).add(Blocks.END_STONE); // forge:end_stones
        tag(ENDERMAN_PLACE_ON_BLACKLIST); // forge:enderman_place_on_blacklist
        tag(FENCE_GATES).addTags(FENCE_GATES_WOODEN); // forge:fence_gates
        tag(FENCE_GATES_WOODEN).add(Blocks.OAK_FENCE_GATE, Blocks.SPRUCE_FENCE_GATE, Blocks.BIRCH_FENCE_GATE, Blocks.JUNGLE_FENCE_GATE, Blocks.ACACIA_FENCE_GATE, Blocks.DARK_OAK_FENCE_GATE, Blocks.CRIMSON_FENCE_GATE, Blocks.WARPED_FENCE_GATE, Blocks.MANGROVE_FENCE_GATE, Blocks.BAMBOO_FENCE_GATE, Blocks.CHERRY_FENCE_GATE);
        tag(FENCES).addTags(FENCES_NETHER_BRICK, FENCES_WOODEN); // forge:fences
        tag(FENCES_NETHER_BRICK).add(Blocks.NETHER_BRICK_FENCE); // forge:fences/nether_brick
        tag(FENCES_WOODEN).addTag(BlockTags.WOODEN_FENCES); // forge:fences/wooden
        tag(GLASS_BLOCKS)
                .addTags(GLASS_BLOCKS_COLORLESS, GLASS_BLOCKS_CHEAP, GLASS_BLOCKS_TINTED)
                .addOptionalTag(forgeTagKey("glass"));
        tag(GLASS_BLOCKS_COLORLESS)
                .add(Blocks.GLASS)
                .addOptionalTag(forgeTagKey("glass/colorless"));
        tag(GLASS_BLOCKS_CHEAP)
                .add(Blocks.GLASS, Blocks.WHITE_STAINED_GLASS, Blocks.ORANGE_STAINED_GLASS, Blocks.MAGENTA_STAINED_GLASS, Blocks.LIGHT_BLUE_STAINED_GLASS, Blocks.YELLOW_STAINED_GLASS, Blocks.LIME_STAINED_GLASS, Blocks.PINK_STAINED_GLASS, Blocks.GRAY_STAINED_GLASS, Blocks.LIGHT_GRAY_STAINED_GLASS, Blocks.CYAN_STAINED_GLASS, Blocks.PURPLE_STAINED_GLASS, Blocks.BLUE_STAINED_GLASS, Blocks.BROWN_STAINED_GLASS, Blocks.GREEN_STAINED_GLASS, Blocks.RED_STAINED_GLASS, Blocks.BLACK_STAINED_GLASS)
                .addOptionalTag(forgeTagKey("glass/silica"));
        tag(GLASS_BLOCKS_TINTED)
                .add(Blocks.TINTED_GLASS)
                .addOptionalTag(forgeTagKey("glass/tinted"));
        tag(GLASS_PANES)
                .addTags(GLASS_PANES_COLORLESS).add(Blocks.WHITE_STAINED_GLASS_PANE, Blocks.ORANGE_STAINED_GLASS_PANE, Blocks.MAGENTA_STAINED_GLASS_PANE, Blocks.LIGHT_BLUE_STAINED_GLASS_PANE, Blocks.YELLOW_STAINED_GLASS_PANE, Blocks.LIME_STAINED_GLASS_PANE, Blocks.PINK_STAINED_GLASS_PANE, Blocks.GRAY_STAINED_GLASS_PANE, Blocks.LIGHT_GRAY_STAINED_GLASS_PANE, Blocks.CYAN_STAINED_GLASS_PANE, Blocks.PURPLE_STAINED_GLASS_PANE, Blocks.BLUE_STAINED_GLASS_PANE, Blocks.BROWN_STAINED_GLASS_PANE, Blocks.GREEN_STAINED_GLASS_PANE, Blocks.RED_STAINED_GLASS_PANE, Blocks.BLACK_STAINED_GLASS_PANE)
                .addOptionalTag(forgeTagKey("glass_panes"));
        tag(GLASS_PANES_COLORLESS)
                .add(Blocks.GLASS_PANE)
                .addOptionalTag(forgeTagKey("glass_panes/colorless"));
        tag(GLAZED_TERRACOTTAS).add(Blocks.WHITE_GLAZED_TERRACOTTA, Blocks.ORANGE_GLAZED_TERRACOTTA, Blocks.MAGENTA_GLAZED_TERRACOTTA, Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA, Blocks.YELLOW_GLAZED_TERRACOTTA, Blocks.LIME_GLAZED_TERRACOTTA, Blocks.PINK_GLAZED_TERRACOTTA, Blocks.GRAY_GLAZED_TERRACOTTA, Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA, Blocks.CYAN_GLAZED_TERRACOTTA, Blocks.PURPLE_GLAZED_TERRACOTTA, Blocks.BLUE_GLAZED_TERRACOTTA, Blocks.BROWN_GLAZED_TERRACOTTA, Blocks.GREEN_GLAZED_TERRACOTTA, Blocks.RED_GLAZED_TERRACOTTA, Blocks.BLACK_GLAZED_TERRACOTTA);
        tag(GRAVEL).add(Blocks.GRAVEL); // forge:gravel
        tag(SKULLS).add(Blocks.SKELETON_SKULL, Blocks.SKELETON_WALL_SKULL, Blocks.WITHER_SKELETON_SKULL, Blocks.WITHER_SKELETON_WALL_SKULL, Blocks.PLAYER_HEAD, Blocks.PLAYER_WALL_HEAD, Blocks.ZOMBIE_HEAD, Blocks.ZOMBIE_WALL_HEAD, Blocks.CREEPER_HEAD, Blocks.CREEPER_WALL_HEAD, Blocks.PIGLIN_HEAD, Blocks.PIGLIN_WALL_HEAD, Blocks.DRAGON_HEAD, Blocks.DRAGON_WALL_HEAD);
        tag(HIDDEN_FROM_RECIPE_VIEWERS);
        tag(NETHERRACK).add(Blocks.NETHERRACK); // forge:netherrack
        tag(OBSIDIANS_NORMAL).add(Blocks.OBSIDIAN);
        tag(OBSIDIANS_CRYING).add(Blocks.CRYING_OBSIDIAN);
        tag(OBSIDIANS)
                .addTags(OBSIDIANS_NORMAL, OBSIDIANS_CRYING)
                .addOptionalTag(forgeTagKey("obsidian"));
        tag(ORE_BEARING_GROUND_DEEPSLATE).add(Blocks.DEEPSLATE); // forge:ore_bearing_ground/deepslate
        tag(ORE_BEARING_GROUND_NETHERRACK).add(Blocks.NETHERRACK); // forge:ore_bearing_ground/netherrack
        tag(ORE_BEARING_GROUND_STONE).add(Blocks.STONE); // forge:ore_bearing_ground/stone
        tag(ORE_RATES_DENSE).add(Blocks.COPPER_ORE, Blocks.DEEPSLATE_COPPER_ORE, Blocks.DEEPSLATE_LAPIS_ORE, Blocks.DEEPSLATE_REDSTONE_ORE, Blocks.LAPIS_ORE, Blocks.REDSTONE_ORE);
        tag(ORE_RATES_SINGULAR).add(Blocks.ANCIENT_DEBRIS, Blocks.COAL_ORE, Blocks.DEEPSLATE_COAL_ORE, Blocks.DEEPSLATE_DIAMOND_ORE, Blocks.DEEPSLATE_EMERALD_ORE, Blocks.DEEPSLATE_GOLD_ORE, Blocks.DEEPSLATE_IRON_ORE, Blocks.DIAMOND_ORE, Blocks.EMERALD_ORE, Blocks.GOLD_ORE, Blocks.IRON_ORE, Blocks.NETHER_QUARTZ_ORE);
        tag(ORE_RATES_SPARSE).add(Blocks.NETHER_GOLD_ORE);
        tag(ORES)
                .addTags(ORES_COAL, ORES_COPPER, ORES_DIAMOND, ORES_EMERALD, ORES_GOLD, ORES_IRON, ORES_LAPIS, ORES_NETHERITE_SCRAP, ORES_REDSTONE, ORES_QUARTZ)
                .addOptionalTag(forgeTagKey("ores"));
        tag(ORES_COAL).addTag(BlockTags.COAL_ORES); // forge:ores/coal
        tag(ORES_COPPER).addTag(BlockTags.COPPER_ORES); // forge:ores/copper
        tag(ORES_DIAMOND).addTag(BlockTags.DIAMOND_ORES); // forge:ores/diamond
        tag(ORES_EMERALD).addTag(BlockTags.EMERALD_ORES); // forge:ores/emerald
        tag(ORES_GOLD).addTag(BlockTags.GOLD_ORES); // forge:ores/gold
        tag(ORES_IRON).addTag(BlockTags.IRON_ORES); // forge:ores/iron
        tag(ORES_LAPIS).addTag(BlockTags.LAPIS_ORES); // forge:ores/lapis
        tag(ORES_QUARTZ)
                .add(Blocks.NETHER_QUARTZ_ORE)
                .addOptionalTag(forgeTagKey("ores/quartz"));
        tag(ORES_REDSTONE).addTag(BlockTags.REDSTONE_ORES); // forge:ores/redstone
        tag(ORES_NETHERITE_SCRAP)
                .add(Blocks.ANCIENT_DEBRIS)
                .addOptionalTag(forgeTagKey("ores/netherite_scrap"));
        tag(ORES_IN_GROUND_DEEPSLATE).add(Blocks.DEEPSLATE_COAL_ORE, Blocks.DEEPSLATE_COPPER_ORE, Blocks.DEEPSLATE_DIAMOND_ORE, Blocks.DEEPSLATE_EMERALD_ORE, Blocks.DEEPSLATE_GOLD_ORE, Blocks.DEEPSLATE_IRON_ORE, Blocks.DEEPSLATE_LAPIS_ORE, Blocks.DEEPSLATE_REDSTONE_ORE);
        tag(ORES_IN_GROUND_NETHERRACK).add(Blocks.NETHER_GOLD_ORE, Blocks.NETHER_QUARTZ_ORE);
        tag(ORES_IN_GROUND_STONE).add(Blocks.COAL_ORE, Blocks.COPPER_ORE, Blocks.DIAMOND_ORE, Blocks.EMERALD_ORE, Blocks.GOLD_ORE, Blocks.IRON_ORE, Blocks.LAPIS_ORE, Blocks.REDSTONE_ORE);
        tag(PLAYER_WORKSTATIONS_CRAFTING_TABLES).add(Blocks.CRAFTING_TABLE);
        tag(PLAYER_WORKSTATIONS_FURNACES).add(Blocks.FURNACE);
        tag(SAND).addTags(SAND_COLORLESS, SAND_RED); // forge:sand
        tag(RELOCATION_NOT_SUPPORTED);
        tag(ROPES);
        tag(SAND_COLORLESS).add(Blocks.SAND); // forge:sand/colorless
        tag(SAND_RED).add(Blocks.RED_SAND); // forge:sand/red

        tag(SANDSTONE_RED_BLOCKS).add(Blocks.RED_SANDSTONE, Blocks.CUT_RED_SANDSTONE, Blocks.CHISELED_RED_SANDSTONE, Blocks.SMOOTH_RED_SANDSTONE);
        tag(SANDSTONE_UNCOLORED_BLOCKS).add(Blocks.SANDSTONE, Blocks.CUT_SANDSTONE, Blocks.CHISELED_SANDSTONE, Blocks.SMOOTH_SANDSTONE);
        tag(SANDSTONE_BLOCKS)
                .addTags(SANDSTONE_RED_BLOCKS, SANDSTONE_UNCOLORED_BLOCKS)
                .addOptionalTag(forgeTagKey("sandstone"));
        tag(SANDSTONE_RED_SLABS).add(Blocks.RED_SANDSTONE_SLAB, Blocks.CUT_RED_SANDSTONE_SLAB, Blocks.SMOOTH_RED_SANDSTONE_SLAB);
        tag(SANDSTONE_UNCOLORED_SLABS).add(Blocks.SANDSTONE_SLAB, Blocks.CUT_SANDSTONE_SLAB, Blocks.SMOOTH_SANDSTONE_SLAB);
        tag(SANDSTONE_SLABS).addTags(SANDSTONE_RED_SLABS, SANDSTONE_UNCOLORED_SLABS);
        tag(SANDSTONE_RED_STAIRS).add(Blocks.RED_SANDSTONE_STAIRS, Blocks.SMOOTH_RED_SANDSTONE_STAIRS);
        tag(SANDSTONE_UNCOLORED_STAIRS).add(Blocks.SANDSTONE_STAIRS, Blocks.SMOOTH_SANDSTONE_STAIRS);
        tag(SANDSTONE_STAIRS).addTags(SANDSTONE_RED_STAIRS, SANDSTONE_UNCOLORED_STAIRS);

        tag(STONES)
                .add(Blocks.ANDESITE, Blocks.DIORITE, Blocks.GRANITE, Blocks.STONE, Blocks.DEEPSLATE, Blocks.TUFF);
                //.addOptionalTag(forgeTagKey("stone")); // can't add this because it would include infested/polished variants which aren't contained in Fabric's `c:stones`
        tag(STORAGE_BLOCKS)
                .addTags(STORAGE_BLOCKS_BONE_MEAL, STORAGE_BLOCKS_COAL,
                STORAGE_BLOCKS_COPPER, STORAGE_BLOCKS_DIAMOND, STORAGE_BLOCKS_DRIED_KELP,
                STORAGE_BLOCKS_EMERALD, STORAGE_BLOCKS_GOLD, STORAGE_BLOCKS_IRON,
                STORAGE_BLOCKS_LAPIS, STORAGE_BLOCKS_NETHERITE, STORAGE_BLOCKS_RAW_COPPER,
                STORAGE_BLOCKS_RAW_GOLD, STORAGE_BLOCKS_RAW_IRON, STORAGE_BLOCKS_REDSTONE,
                STORAGE_BLOCKS_SLIME, STORAGE_BLOCKS_WHEAT)
                //.addOptionalTag(forgeTagKey("storage_blocks")); // can't add this because it would include contents from the non-common forge:storage_blocks/amethyst and forge:storage_blocks/quartz, which are not in the c namespace
                .addOptionalTags(forgeTagKey("storage_blocks/coal"), forgeTagKey("storage_blocks/copper"),
                        forgeTagKey("storage_blocks/diamond"), forgeTagKey("storage_blocks/emerald"),
                        forgeTagKey("storage_blocks/gold"), forgeTagKey("storage_blocks/iron"),
                        forgeTagKey("storage_blocks/lapis"), forgeTagKey("storage_blocks/netherite"),
                        forgeTagKey("storage_blocks/raw_copper"), forgeTagKey("storage_blocks/raw_gold"),
                        forgeTagKey("storage_blocks/raw_iron"), forgeTagKey("storage_blocks/redstone"));
        tag(STORAGE_BLOCKS_BONE_MEAL).add(Blocks.BONE_BLOCK);
        tag(STORAGE_BLOCKS_COAL)
                .add(Blocks.COAL_BLOCK)
                .addOptionalTag(forgeTagKey("storage_blocks/coal"));
        tag(STORAGE_BLOCKS_COPPER)
                .add(Blocks.COPPER_BLOCK)
                .addOptionalTag(forgeTagKey("storage_blocks/copper"));
        tag(STORAGE_BLOCKS_DIAMOND)
                .add(Blocks.DIAMOND_BLOCK)
                .addOptionalTag(forgeTagKey("storage_blocks/diamond"));
        tag(STORAGE_BLOCKS_DRIED_KELP).add(Blocks.DRIED_KELP_BLOCK);
        tag(STORAGE_BLOCKS_EMERALD)
                .add(Blocks.EMERALD_BLOCK)
                .addOptionalTag(forgeTagKey("storage_blocks/emerald"));
        tag(STORAGE_BLOCKS_GOLD)
                .add(Blocks.GOLD_BLOCK)
                .addOptionalTag(forgeTagKey("storage_blocks/gold"));
        tag(STORAGE_BLOCKS_IRON)
                .add(Blocks.IRON_BLOCK)
                .addOptionalTag(forgeTagKey("storage_blocks/iron"));
        tag(STORAGE_BLOCKS_LAPIS)
                .add(Blocks.LAPIS_BLOCK)
                .addOptionalTag(forgeTagKey("storage_blocks/lapis"));
        tag(STORAGE_BLOCKS_NETHERITE)
                .add(Blocks.NETHERITE_BLOCK)
                .addOptionalTag(forgeTagKey("storage_blocks/netherite"));
        tag(STORAGE_BLOCKS_RAW_COPPER)
                .add(Blocks.RAW_COPPER_BLOCK)
                .addOptionalTag(forgeTagKey("storage_blocks/raw_copper"));
        tag(STORAGE_BLOCKS_RAW_GOLD)
                .add(Blocks.RAW_GOLD_BLOCK)
                .addOptionalTag(forgeTagKey("storage_blocks/raw_gold"));
        tag(STORAGE_BLOCKS_RAW_IRON)
                .add(Blocks.RAW_IRON_BLOCK)
                .addOptionalTag(forgeTagKey("storage_blocks/raw_iron"));
        tag(STORAGE_BLOCKS_REDSTONE)
                .add(Blocks.REDSTONE_BLOCK)
                .addOptionalTag(forgeTagKey("storage_blocks/redstone"));
        tag(STORAGE_BLOCKS_SLIME).add(Blocks.SLIME_BLOCK);
        tag(STORAGE_BLOCKS_WHEAT).add(Blocks.HAY_BLOCK);
        tag(Tags.Blocks.STRIPPED_LOGS).add(
                Blocks.STRIPPED_ACACIA_LOG, Blocks.STRIPPED_BAMBOO_BLOCK, Blocks.STRIPPED_BIRCH_LOG,
                Blocks.STRIPPED_CHERRY_LOG, Blocks.STRIPPED_DARK_OAK_LOG, Blocks.STRIPPED_JUNGLE_LOG,
                Blocks.STRIPPED_MANGROVE_LOG, Blocks.STRIPPED_OAK_LOG, Blocks.STRIPPED_SPRUCE_LOG);
        tag(Tags.Blocks.STRIPPED_WOODS).add(
                Blocks.STRIPPED_ACACIA_WOOD, Blocks.STRIPPED_BIRCH_WOOD, Blocks.STRIPPED_CHERRY_WOOD,
                Blocks.STRIPPED_DARK_OAK_WOOD, Blocks.STRIPPED_JUNGLE_WOOD, Blocks.STRIPPED_MANGROVE_WOOD,
                Blocks.STRIPPED_OAK_WOOD, Blocks.STRIPPED_SPRUCE_WOOD);
        tag(VILLAGER_JOB_SITES).add(
                Blocks.BARREL, Blocks.BLAST_FURNACE, Blocks.BREWING_STAND, Blocks.CARTOGRAPHY_TABLE,
                Blocks.CAULDRON, Blocks.WATER_CAULDRON, Blocks.LAVA_CAULDRON, Blocks.POWDER_SNOW_CAULDRON,
                Blocks.COMPOSTER, Blocks.FLETCHING_TABLE, Blocks.GRINDSTONE, Blocks.LECTERN,
                Blocks.LOOM, Blocks.SMITHING_TABLE, Blocks.SMOKER, Blocks.STONECUTTER);

        // Backwards compat definitions for pre-1.21 legacy `forge:` tags.
        // TODO: Remove backwards compat tag entries in 1.22
        tag(forgeTagKey("barrels")).addTag(forgeTagKey("barrels/wooden"));
        tag(forgeTagKey("barrels/wooden")).add(Blocks.BARREL);
        tag(forgeTagKey("bookshelves")).add(Blocks.BOOKSHELF);
        tag(forgeTagKey("chests")).addTags(forgeTagKey("chests/ender"), forgeTagKey("chests/trapped"), forgeTagKey("chests/wooden"));
        tag(forgeTagKey("chests/wooden")).add(Blocks.CHEST, Blocks.TRAPPED_CHEST);
        tag(forgeTagKey("cobblestone")).addTags(forgeTagKey("cobblestone/normal"), forgeTagKey("cobblestone/infested"), forgeTagKey("cobblestone/mossy"), forgeTagKey("cobblestone/deepslate"));
        tag(forgeTagKey("glass")).addTags(forgeTagKey("glass/colorless"), forgeTagKey("stained_glass"), forgeTagKey("glass/tinted"));
        tag(forgeTagKey("glass/colorless")).add(Blocks.GLASS);
        tag(forgeTagKey("glass/silica")).add(Blocks.GLASS, Blocks.BLACK_STAINED_GLASS, Blocks.BLUE_STAINED_GLASS, Blocks.BROWN_STAINED_GLASS, Blocks.CYAN_STAINED_GLASS, Blocks.GRAY_STAINED_GLASS, Blocks.GREEN_STAINED_GLASS, Blocks.LIGHT_BLUE_STAINED_GLASS, Blocks.LIGHT_GRAY_STAINED_GLASS, Blocks.LIME_STAINED_GLASS, Blocks.MAGENTA_STAINED_GLASS, Blocks.ORANGE_STAINED_GLASS, Blocks.PINK_STAINED_GLASS, Blocks.PURPLE_STAINED_GLASS, Blocks.RED_STAINED_GLASS, Blocks.WHITE_STAINED_GLASS, Blocks.YELLOW_STAINED_GLASS);
        tag(forgeTagKey("glass/tinted")).add(Blocks.TINTED_GLASS);
        addColored(tag(forgeTagKey("stained_glass"))::add, forgeTagKey("glass"), "{color}_stained_glass");
        tag(forgeTagKey("glass_panes")).addTags(forgeTagKey("glass_panes/colorless"), forgeTagKey("stained_glass_panes"));
        tag(forgeTagKey("glass_panes/colorless")).add(Blocks.GLASS_PANE);
        addColored(tag(forgeTagKey("stained_glass_panes"))::add, forgeTagKey("glass_panes"), "{color}_stained_glass_pane");
        tag(forgeTagKey("obsidian")).add(Blocks.OBSIDIAN);
        tag(forgeTagKey("ores")).addTags(forgeTagKey("ores/coal"), forgeTagKey("ores/copper"), forgeTagKey("ores/diamond"), forgeTagKey("ores/emerald"), forgeTagKey("ores/gold"), forgeTagKey("ores/iron"), forgeTagKey("ores/lapis"), forgeTagKey("ores/redstone"), forgeTagKey("ores/quartz"), forgeTagKey("ores/netherite_scrap"));
        tag(forgeTagKey("ores/quartz")).add(Blocks.NETHER_QUARTZ_ORE);
        tag(forgeTagKey("ores/netherite_scrap")).add(Blocks.ANCIENT_DEBRIS);
        tag(forgeTagKey("sandstone")).add(Blocks.SANDSTONE, Blocks.CUT_SANDSTONE, Blocks.CHISELED_SANDSTONE, Blocks.SMOOTH_SANDSTONE, Blocks.RED_SANDSTONE, Blocks.CUT_RED_SANDSTONE, Blocks.CHISELED_RED_SANDSTONE, Blocks.SMOOTH_RED_SANDSTONE);
        tag(forgeTagKey("stone")).add(Blocks.ANDESITE, Blocks.DIORITE, Blocks.GRANITE, Blocks.INFESTED_STONE, Blocks.STONE, Blocks.POLISHED_ANDESITE, Blocks.POLISHED_DIORITE, Blocks.POLISHED_GRANITE, Blocks.DEEPSLATE, Blocks.POLISHED_DEEPSLATE, Blocks.INFESTED_DEEPSLATE, Blocks.TUFF);
        tag(forgeTagKey("storage_blocks"))
                .addTags(forgeTagKey("storage_blocks/amethyst"), forgeTagKey("storage_blocks/coal"), forgeTagKey("storage_blocks/copper"), forgeTagKey("storage_blocks/diamond"), forgeTagKey("storage_blocks/emerald"), forgeTagKey("storage_blocks/gold"), forgeTagKey("storage_blocks/iron"), forgeTagKey("storage_blocks/lapis"), forgeTagKey("storage_blocks/quartz"), forgeTagKey("storage_blocks/raw_copper"), forgeTagKey("storage_blocks/raw_gold"), forgeTagKey("storage_blocks/raw_iron"), forgeTagKey("storage_blocks/redstone"), forgeTagKey("storage_blocks/netherite"));
        tag(forgeTagKey("storage_blocks/amethyst")).add(Blocks.AMETHYST_BLOCK);
        tag(forgeTagKey("storage_blocks/coal")).add(Blocks.COAL_BLOCK);
        tag(forgeTagKey("storage_blocks/copper")).add(Blocks.COPPER_BLOCK);
        tag(forgeTagKey("storage_blocks/diamond")).add(Blocks.DIAMOND_BLOCK);
        tag(forgeTagKey("storage_blocks/emerald")).add(Blocks.EMERALD_BLOCK);
        tag(forgeTagKey("storage_blocks/gold")).add(Blocks.GOLD_BLOCK);
        tag(forgeTagKey("storage_blocks/iron")).add(Blocks.IRON_BLOCK);
        tag(forgeTagKey("storage_blocks/lapis")).add(Blocks.LAPIS_BLOCK);
        tag(forgeTagKey("storage_blocks/quartz")).add(Blocks.QUARTZ_BLOCK);
        tag(forgeTagKey("storage_blocks/raw_copper")).add(Blocks.RAW_COPPER_BLOCK);
        tag(forgeTagKey("storage_blocks/raw_gold")).add(Blocks.RAW_GOLD_BLOCK);
        tag(forgeTagKey("storage_blocks/raw_iron")).add(Blocks.RAW_IRON_BLOCK);
        tag(forgeTagKey("storage_blocks/redstone")).add(Blocks.REDSTONE_BLOCK);
        tag(forgeTagKey("storage_blocks/netherite")).add(Blocks.NETHERITE_BLOCK);
    }

    private void addColored(TagKey<Block> group, String pattern) {
        String prefix = group.location().getPath().toUpperCase(Locale.ENGLISH) + '_';
        for (var color : DyeColor.values()) {
            var key = ResourceLocation.fromNamespaceAndPath("minecraft", pattern.replace("{color}", color.getName()));
            TagKey<Block> tag = getForgeTag(prefix + color.getName());
            var block = ForgeRegistries.BLOCKS.getValue(key);
            if (block == null || block == Blocks.AIR)
                throw new IllegalStateException("Unknown vanilla block: " + key);
            tag(tag).add(block);
        }
    }

    private void addColored(Consumer<Block> consumer, TagKey<Block> group, String pattern) {
        String prefix = group.location().getPath().toUpperCase(Locale.ENGLISH) + '_';
        for (DyeColor color  : DyeColor.values()) {
            ResourceLocation key = ResourceLocation.fromNamespaceAndPath("minecraft", pattern.replace("{color}",  color.getName()));
            TagKey<Block> tag = getForgeTag(prefix + color.getName());
            Block block = ForgeRegistries.BLOCKS.getValue(key);
            if (block == null || block  == Blocks.AIR)
                throw new IllegalStateException("Unknown vanilla block: " + key.toString());
            tag(tag).add(block);
            consumer.accept(block);
        }
    }

    private static void addColoredTags(Consumer<TagKey<Block>> consumer, TagKey<Block> group) {
        String prefix = group.location().getPath().toUpperCase(Locale.ENGLISH) + '_';
        for (var color : DyeColor.values()) {
            TagKey<Block> tag = getForgeTag(prefix + color.getName());
            consumer.accept(tag);
        }
    }

    @SuppressWarnings("unchecked")
    private static TagKey<Block> getForgeTag(String name) {
        try {
            name = name.toUpperCase(Locale.ENGLISH);
            return (TagKey<Block>) Tags.Blocks.class.getDeclaredField(name).get(null);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            throw new IllegalStateException(Tags.Blocks.class.getName() + " is missing tag name: " + name);
        }
    }

    private static TagKey<Block> forgeTagKey(String path) {
        return BlockTags.create(ResourceLocation.fromNamespaceAndPath("forge", path));
    }

    @Override
    public String getName() {
        return "Forge Block Tags";
    }
}
