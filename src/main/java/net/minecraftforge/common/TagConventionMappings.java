/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.common;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class TagConventionMappings {
    private TagConventionMappings() {}

    /**
     * A map of known legacy tags to their common convention equivalents.
     */
    public static final Map<TagKey<?>, String> MAPPINGS = Map.<TagKey<?>, String>ofEntries(
            //region Blocks
            legacyToCommon(Registries.BLOCK, singularityRl("barrels"), Tags.Blocks.BARRELS), // singularity:barrels -> c:barrels
            legacyToCommon(Registries.BLOCK, singularityRl("barrels/wooden"), Tags.Blocks.BARRELS_WOODEN),
            legacyToCommon(Registries.BLOCK, singularityRl("bookshelves"), Tags.Blocks.BOOKSHELVES),

            legacyToCommon(Registries.BLOCK, singularityRl("chests"), Tags.Blocks.CHESTS),
            legacyToCommon(Registries.BLOCK, singularityRl("chests/wooden"), Tags.Blocks.CHESTS_WOODEN),

            legacyToCommon(Registries.BLOCK, singularityRl("cobblestone"), Tags.Blocks.COBBLESTONES),

            legacyToCommon(Registries.BLOCK, singularityRl("glass"), Tags.Blocks.GLASS_BLOCKS),
            legacyToCommon(Registries.BLOCK, singularityRl("glass/black"), List.of(Tags.Blocks.GLASS_BLOCKS, Tags.Items.DYED_BLACK)),
            legacyToCommon(Registries.BLOCK, singularityRl("glass/blue"), List.of(Tags.Blocks.GLASS_BLOCKS, Tags.Items.DYED_BLUE)),
            legacyToCommon(Registries.BLOCK, singularityRl("glass/brown"), List.of(Tags.Blocks.GLASS_BLOCKS, Tags.Items.DYED_BROWN)),
            legacyToCommon(Registries.BLOCK, singularityRl("glass/colorless"), Tags.Blocks.GLASS_BLOCKS_COLORLESS),
            legacyToCommon(Registries.BLOCK, singularityRl("glass/cyan"), List.of(Tags.Blocks.GLASS_BLOCKS, Tags.Items.DYED_CYAN)),
            legacyToCommon(Registries.BLOCK, singularityRl("glass/gray"), List.of(Tags.Blocks.GLASS_BLOCKS, Tags.Items.DYED_GRAY)),
            legacyToCommon(Registries.BLOCK, singularityRl("glass/green"), List.of(Tags.Blocks.GLASS_BLOCKS, Tags.Items.DYED_GREEN)),
            legacyToCommon(Registries.BLOCK, singularityRl("glass/light_blue"), List.of(Tags.Blocks.GLASS_BLOCKS, Tags.Items.DYED_LIGHT_BLUE)),
            legacyToCommon(Registries.BLOCK, singularityRl("glass/light_gray"), List.of(Tags.Blocks.GLASS_BLOCKS, Tags.Items.DYED_LIGHT_GRAY)),
            legacyToCommon(Registries.BLOCK, singularityRl("glass/lime"), List.of(Tags.Blocks.GLASS_BLOCKS, Tags.Items.DYED_LIME)),
            legacyToCommon(Registries.BLOCK, singularityRl("glass/magenta"), List.of(Tags.Blocks.GLASS_BLOCKS, Tags.Items.DYED_MAGENTA)),
            legacyToCommon(Registries.BLOCK, singularityRl("glass/orange"), List.of(Tags.Blocks.GLASS_BLOCKS, Tags.Items.DYED_ORANGE)),
            legacyToCommon(Registries.BLOCK, singularityRl("glass/pink"), List.of(Tags.Blocks.GLASS_BLOCKS, Tags.Items.DYED_PINK)),
            legacyToCommon(Registries.BLOCK, singularityRl("glass/purple"), List.of(Tags.Blocks.GLASS_BLOCKS, Tags.Items.DYED_PURPLE)),
            legacyToCommon(Registries.BLOCK, singularityRl("glass/red"), List.of(Tags.Blocks.GLASS_BLOCKS, Tags.Items.DYED_RED)),
            legacyToCommon(Registries.BLOCK, singularityRl("glass/silica"), Tags.Blocks.GLASS_BLOCKS_CHEAP),
            legacyToCommon(Registries.BLOCK, singularityRl("glass/tinted"), Tags.Blocks.GLASS_BLOCKS_TINTED),
            legacyToCommon(Registries.BLOCK, singularityRl("glass/white"), List.of(Tags.Blocks.GLASS_BLOCKS, Tags.Items.DYED_WHITE)),
            legacyToCommon(Registries.BLOCK, singularityRl("glass/yellow"), List.of(Tags.Blocks.GLASS_BLOCKS, Tags.Items.DYED_YELLOW)),
            legacyToCommon(Registries.BLOCK, singularityRl("glass_panes"), Tags.Blocks.GLASS_PANES),
            legacyToCommon(Registries.BLOCK, singularityRl("glass_panes/colorless"), Tags.Blocks.GLASS_PANES_COLORLESS),
            legacyToCommon(Registries.BLOCK, singularityRl("glass_panes/black"), List.of(Tags.Blocks.GLASS_PANES, Tags.Items.DYED_BLACK)),
            legacyToCommon(Registries.BLOCK, singularityRl("glass_panes/blue"), List.of(Tags.Blocks.GLASS_PANES, Tags.Items.DYED_BLUE)),
            legacyToCommon(Registries.BLOCK, singularityRl("glass_panes/brown"), List.of(Tags.Blocks.GLASS_PANES, Tags.Items.DYED_BROWN)),
            legacyToCommon(Registries.BLOCK, singularityRl("glass_panes/cyan"), List.of(Tags.Blocks.GLASS_PANES, Tags.Items.DYED_CYAN)),
            legacyToCommon(Registries.BLOCK, singularityRl("glass_panes/gray"), List.of(Tags.Blocks.GLASS_PANES, Tags.Items.DYED_GRAY)),
            legacyToCommon(Registries.BLOCK, singularityRl("glass_panes/green"), List.of(Tags.Blocks.GLASS_PANES, Tags.Items.DYED_GREEN)),
            legacyToCommon(Registries.BLOCK, singularityRl("glass_panes/light_blue"), List.of(Tags.Blocks.GLASS_PANES, Tags.Items.DYED_LIGHT_BLUE)),
            legacyToCommon(Registries.BLOCK, singularityRl("glass_panes/light_gray"), List.of(Tags.Blocks.GLASS_PANES, Tags.Items.DYED_LIGHT_GRAY)),
            legacyToCommon(Registries.BLOCK, singularityRl("glass_panes/lime"), List.of(Tags.Blocks.GLASS_PANES, Tags.Items.DYED_LIME)),
            legacyToCommon(Registries.BLOCK, singularityRl("glass_panes/magenta"), List.of(Tags.Blocks.GLASS_PANES, Tags.Items.DYED_MAGENTA)),
            legacyToCommon(Registries.BLOCK, singularityRl("glass_panes/orange"), List.of(Tags.Blocks.GLASS_PANES, Tags.Items.DYED_ORANGE)),
            legacyToCommon(Registries.BLOCK, singularityRl("glass_panes/pink"), List.of(Tags.Blocks.GLASS_PANES, Tags.Items.DYED_PINK)),
            legacyToCommon(Registries.BLOCK, singularityRl("glass_panes/purple"), List.of(Tags.Blocks.GLASS_PANES, Tags.Items.DYED_PURPLE)),
            legacyToCommon(Registries.BLOCK, singularityRl("glass_panes/red"), List.of(Tags.Blocks.GLASS_PANES, Tags.Items.DYED_RED)),
            legacyToCommon(Registries.BLOCK, singularityRl("glass_panes/white"), List.of(Tags.Blocks.GLASS_PANES, Tags.Items.DYED_WHITE)),
            legacyToCommon(Registries.BLOCK, singularityRl("glass_panes/yellow"), List.of(Tags.Blocks.GLASS_PANES, Tags.Items.DYED_YELLOW)),

            legacyToCommon(Registries.BLOCK, singularityRl("obsidian"), Tags.Blocks.OBSIDIANS),
            legacyToCommon(Registries.BLOCK, singularityRl("ores"), Tags.Blocks.ORES),
            legacyToCommon(Registries.BLOCK, singularityRl("ores/netherite_scrap"), Tags.Blocks.ORES_NETHERITE_SCRAP),
            legacyToCommon(Registries.BLOCK, singularityRl("ores/quartz"), Tags.Blocks.ORES_QUARTZ),

            legacyToCommon(Registries.BLOCK, singularityRl("sandstone"), Tags.Blocks.SANDSTONE_BLOCKS),
            legacyToCommon(Registries.BLOCK, singularityRl("stained_glass"), List.of(Tags.Blocks.GLASS_BLOCKS, Tags.Blocks.DYED)),
            legacyToCommon(Registries.BLOCK, singularityRl("stained_glass_panes"), List.of(Tags.Blocks.GLASS_PANES, Tags.Blocks.DYED)),
            legacyToCommon(Registries.BLOCK, singularityRl("stone"), Tags.Blocks.STONES),
            legacyToCommon(Registries.BLOCK, singularityRl("storage_blocks"), Tags.Blocks.STORAGE_BLOCKS),
            legacyToCommon(Registries.BLOCK, singularityRl("storage_blocks/coal"), Tags.Blocks.STORAGE_BLOCKS_COAL),
            legacyToCommon(Registries.BLOCK, singularityRl("storage_blocks/copper"), Tags.Blocks.STORAGE_BLOCKS_COPPER),
            legacyToCommon(Registries.BLOCK, singularityRl("storage_blocks/diamond"), Tags.Blocks.STORAGE_BLOCKS_DIAMOND),
            legacyToCommon(Registries.BLOCK, singularityRl("storage_blocks/emerald"), Tags.Blocks.STORAGE_BLOCKS_EMERALD),
            legacyToCommon(Registries.BLOCK, singularityRl("storage_blocks/gold"), Tags.Blocks.STORAGE_BLOCKS_GOLD),
            legacyToCommon(Registries.BLOCK, singularityRl("storage_blocks/iron"), Tags.Blocks.STORAGE_BLOCKS_IRON),
            legacyToCommon(Registries.BLOCK, singularityRl("storage_blocks/lapis"), Tags.Blocks.STORAGE_BLOCKS_LAPIS),
            legacyToCommon(Registries.BLOCK, singularityRl("storage_blocks/netherite"), Tags.Blocks.STORAGE_BLOCKS_NETHERITE),
            legacyToCommon(Registries.BLOCK, singularityRl("storage_blocks/quartz"), Tags.Blocks.STORAGE_BLOCKS_QUARTZ),
            legacyToCommon(Registries.BLOCK, singularityRl("storage_blocks/raw_copper"), Tags.Blocks.STORAGE_BLOCKS_RAW_COPPER),
            legacyToCommon(Registries.BLOCK, singularityRl("storage_blocks/raw_gold"), Tags.Blocks.STORAGE_BLOCKS_RAW_GOLD),
            legacyToCommon(Registries.BLOCK, singularityRl("storage_blocks/raw_iron"), Tags.Blocks.STORAGE_BLOCKS_RAW_IRON),
            legacyToCommon(Registries.BLOCK, singularityRl("storage_blocks/redstone"), Tags.Blocks.STORAGE_BLOCKS_REDSTONE),
            //endregion

            //region Entity Types
            legacyToCommon(Registries.ENTITY_TYPE, singularityRl("bosses"), Tags.EntityTypes.BOSSES),
            //endregion

            //region Items
            legacyToCommon(Registries.ITEM, singularityRl("barrels"), Tags.Items.BARRELS),
            legacyToCommon(Registries.ITEM, singularityRl("barrels/wooden"), Tags.Items.BARRELS_WOODEN),
            legacyToCommon(Registries.ITEM, singularityRl("bookshelves"), Tags.Items.BOOKSHELVES),

            legacyToCommon(Registries.ITEM, singularityRl("chests"), Tags.Items.CHESTS),
            legacyToCommon(Registries.ITEM, singularityRl("chests/wooden"), Tags.Items.CHESTS_WOODEN),
            legacyToCommon(Registries.ITEM, singularityRl("cobblestone"), Tags.Items.COBBLESTONES),
            legacyToCommon(Registries.ITEM, singularityRl("crops"), Tags.Items.CROPS),
            legacyToCommon(Registries.ITEM, singularityRl("crops/beetroot"), Tags.Items.CROPS_BEETROOT),
            legacyToCommon(Registries.ITEM, singularityRl("crops/carrot"), Tags.Items.CROPS_CARROT),
            legacyToCommon(Registries.ITEM, singularityRl("crops/potato"), Tags.Items.CROPS_POTATO),
            legacyToCommon(Registries.ITEM, singularityRl("crops/wheat"), Tags.Items.CROPS_WHEAT),

            legacyToCommon(Registries.ITEM, singularityRl("dusts"), Tags.Items.DUSTS),
            legacyToCommon(Registries.ITEM, singularityRl("dusts/redstone"), Tags.Items.DUSTS_REDSTONE),
            legacyToCommon(Registries.ITEM, singularityRl("dusts/glowstone"), Tags.Items.DUSTS_GLOWSTONE),
            legacyToCommon(Registries.ITEM, singularityRl("dyes"), Tags.Items.DYES),
            legacyToCommon(Registries.ITEM, singularityRl("dyes/black"), Tags.Items.DYES_BLACK),
            legacyToCommon(Registries.ITEM, singularityRl("dyes/red"), Tags.Items.DYES_RED),
            legacyToCommon(Registries.ITEM, singularityRl("dyes/green"), Tags.Items.DYES_GREEN),
            legacyToCommon(Registries.ITEM, singularityRl("dyes/brown"), Tags.Items.DYES_BROWN),
            legacyToCommon(Registries.ITEM, singularityRl("dyes/blue"), Tags.Items.DYES_BLUE),
            legacyToCommon(Registries.ITEM, singularityRl("dyes/purple"), Tags.Items.DYES_PURPLE),
            legacyToCommon(Registries.ITEM, singularityRl("dyes/cyan"), Tags.Items.DYES_CYAN),
            legacyToCommon(Registries.ITEM, singularityRl("dyes/light_gray"), Tags.Items.DYES_LIGHT_GRAY),
            legacyToCommon(Registries.ITEM, singularityRl("dyes/gray"), Tags.Items.DYES_GRAY),
            legacyToCommon(Registries.ITEM, singularityRl("dyes/pink"), Tags.Items.DYES_PINK),
            legacyToCommon(Registries.ITEM, singularityRl("dyes/lime"), Tags.Items.DYES_LIME),
            legacyToCommon(Registries.ITEM, singularityRl("dyes/yellow"), Tags.Items.DYES_YELLOW),
            legacyToCommon(Registries.ITEM, singularityRl("dyes/light_blue"), Tags.Items.DYES_LIGHT_BLUE),
            legacyToCommon(Registries.ITEM, singularityRl("dyes/magenta"), Tags.Items.DYES_MAGENTA),
            legacyToCommon(Registries.ITEM, singularityRl("dyes/orange"), Tags.Items.DYES_ORANGE),
            legacyToCommon(Registries.ITEM, singularityRl("dyes/white"), Tags.Items.DYES_WHITE),

            legacyToCommon(Registries.ITEM, singularityRl("ender_pearls"), Tags.Items.ENDER_PEARLS),

            legacyToCommon(Registries.ITEM, singularityRl("foods/pie"), Tags.Items.FOODS_PIE),

            legacyToCommon(Registries.ITEM, singularityRl("gems"), Tags.Items.GEMS),
            legacyToCommon(Registries.ITEM, singularityRl("gems/diamond"), Tags.Items.GEMS_DIAMOND),
            legacyToCommon(Registries.ITEM, singularityRl("gems/emerald"), Tags.Items.GEMS_EMERALD),
            legacyToCommon(Registries.ITEM, singularityRl("gems/amethyst"), Tags.Items.GEMS_AMETHYST),
            legacyToCommon(Registries.ITEM, singularityRl("gems/lapis"), Tags.Items.GEMS_LAPIS),
            legacyToCommon(Registries.ITEM, singularityRl("gems/quartz"), Tags.Items.GEMS_QUARTZ),
            legacyToCommon(Registries.ITEM, singularityRl("glass"), Tags.Items.GLASS_BLOCKS),
            legacyToCommon(Registries.ITEM, singularityRl("glass/black"), List.of(Tags.Items.GLASS_BLOCKS, Tags.Items.DYED_BLACK)),
            legacyToCommon(Registries.ITEM, singularityRl("glass/blue"), List.of(Tags.Items.GLASS_BLOCKS, Tags.Items.DYED_BLUE)),
            legacyToCommon(Registries.ITEM, singularityRl("glass/brown"), List.of(Tags.Items.GLASS_BLOCKS, Tags.Items.DYED_BROWN)),
            legacyToCommon(Registries.ITEM, singularityRl("glass/colorless"), Tags.Items.GLASS_BLOCKS_COLORLESS),
            legacyToCommon(Registries.ITEM, singularityRl("glass/cyan"), List.of(Tags.Items.GLASS_BLOCKS, Tags.Items.DYED_CYAN)),
            legacyToCommon(Registries.ITEM, singularityRl("glass/gray"), List.of(Tags.Items.GLASS_BLOCKS, Tags.Items.DYED_GRAY)),
            legacyToCommon(Registries.ITEM, singularityRl("glass/green"), List.of(Tags.Items.GLASS_BLOCKS, Tags.Items.DYED_GREEN)),
            legacyToCommon(Registries.ITEM, singularityRl("glass/light_blue"), List.of(Tags.Items.GLASS_BLOCKS, Tags.Items.DYED_LIGHT_BLUE)),
            legacyToCommon(Registries.ITEM, singularityRl("glass/light_gray"), List.of(Tags.Items.GLASS_BLOCKS, Tags.Items.DYED_LIGHT_GRAY)),
            legacyToCommon(Registries.ITEM, singularityRl("glass/lime"), List.of(Tags.Items.GLASS_BLOCKS, Tags.Items.DYED_LIME)),
            legacyToCommon(Registries.ITEM, singularityRl("glass/magenta"), List.of(Tags.Items.GLASS_BLOCKS, Tags.Items.DYED_MAGENTA)),
            legacyToCommon(Registries.ITEM, singularityRl("glass/orange"), List.of(Tags.Items.GLASS_BLOCKS, Tags.Items.DYED_ORANGE)),
            legacyToCommon(Registries.ITEM, singularityRl("glass/pink"), List.of(Tags.Items.GLASS_BLOCKS, Tags.Items.DYED_PINK)),
            legacyToCommon(Registries.ITEM, singularityRl("glass/purple"), List.of(Tags.Items.GLASS_BLOCKS, Tags.Items.DYED_PURPLE)),
            legacyToCommon(Registries.ITEM, singularityRl("glass/red"), List.of(Tags.Items.GLASS_BLOCKS, Tags.Items.DYED_RED)),
            legacyToCommon(Registries.ITEM, singularityRl("glass/silica"), Tags.Items.GLASS_BLOCKS_CHEAP),
            legacyToCommon(Registries.ITEM, singularityRl("glass/tinted"), Tags.Items.GLASS_BLOCKS_TINTED),
            legacyToCommon(Registries.ITEM, singularityRl("glass/white"), List.of(Tags.Items.GLASS_BLOCKS, Tags.Items.DYED_WHITE)),
            legacyToCommon(Registries.ITEM, singularityRl("glass/yellow"), List.of(Tags.Items.GLASS_BLOCKS, Tags.Items.DYED_YELLOW)),
            legacyToCommon(Registries.ITEM, singularityRl("glass_panes"), Tags.Items.GLASS_PANES),
            legacyToCommon(Registries.ITEM, singularityRl("glass_panes/black"), List.of(Tags.Items.GLASS_PANES, Tags.Items.DYED_BLACK)),
            legacyToCommon(Registries.ITEM, singularityRl("glass_panes/blue"), List.of(Tags.Items.GLASS_PANES, Tags.Items.DYED_BLUE)),
            legacyToCommon(Registries.ITEM, singularityRl("glass_panes/brown"), List.of(Tags.Items.GLASS_PANES, Tags.Items.DYED_BROWN)),
            legacyToCommon(Registries.ITEM, singularityRl("glass_panes/colorless"), Tags.Items.GLASS_PANES_COLORLESS),
            legacyToCommon(Registries.ITEM, singularityRl("glass_panes/cyan"), List.of(Tags.Items.GLASS_PANES, Tags.Items.DYED_CYAN)),
            legacyToCommon(Registries.ITEM, singularityRl("glass_panes/gray"), List.of(Tags.Items.GLASS_PANES, Tags.Items.DYED_GRAY)),
            legacyToCommon(Registries.ITEM, singularityRl("glass_panes/green"), List.of(Tags.Items.GLASS_PANES, Tags.Items.DYED_GREEN)),
            legacyToCommon(Registries.ITEM, singularityRl("glass_panes/light_blue"), List.of(Tags.Items.GLASS_PANES, Tags.Items.DYED_LIGHT_BLUE)),
            legacyToCommon(Registries.ITEM, singularityRl("glass_panes/light_gray"), List.of(Tags.Items.GLASS_PANES, Tags.Items.DYED_LIGHT_GRAY)),
            legacyToCommon(Registries.ITEM, singularityRl("glass_panes/lime"), List.of(Tags.Items.GLASS_PANES, Tags.Items.DYED_LIME)),
            legacyToCommon(Registries.ITEM, singularityRl("glass_panes/magenta"), List.of(Tags.Items.GLASS_PANES, Tags.Items.DYED_MAGENTA)),
            legacyToCommon(Registries.ITEM, singularityRl("glass_panes/orange"), List.of(Tags.Items.GLASS_PANES, Tags.Items.DYED_ORANGE)),
            legacyToCommon(Registries.ITEM, singularityRl("glass_panes/pink"), List.of(Tags.Items.GLASS_PANES, Tags.Items.DYED_PINK)),
            legacyToCommon(Registries.ITEM, singularityRl("glass_panes/purple"), List.of(Tags.Items.GLASS_PANES, Tags.Items.DYED_PURPLE)),
            legacyToCommon(Registries.ITEM, singularityRl("glass_panes/red"), List.of(Tags.Items.GLASS_PANES, Tags.Items.DYED_RED)),
            legacyToCommon(Registries.ITEM, singularityRl("glass_panes/white"), List.of(Tags.Items.GLASS_PANES, Tags.Items.DYED_WHITE)),
            legacyToCommon(Registries.ITEM, singularityRl("glass_panes/yellow"), List.of(Tags.Items.GLASS_PANES, Tags.Items.DYED_YELLOW)),

            legacyToCommon(Registries.ITEM, singularityRl("obsidian"), Tags.Items.OBSIDIANS),
            legacyToCommon(Registries.ITEM, singularityRl("ingots"), Tags.Items.INGOTS),
            legacyToCommon(Registries.ITEM, singularityRl("ingots/brick"), Tags.Items.BRICKS_NORMAL),
            legacyToCommon(Registries.ITEM, singularityRl("ingots/copper"), Tags.Items.INGOTS_COPPER),
            legacyToCommon(Registries.ITEM, singularityRl("ingots/gold"), Tags.Items.INGOTS_GOLD),
            legacyToCommon(Registries.ITEM, singularityRl("ingots/iron"), Tags.Items.INGOTS_IRON),
            legacyToCommon(Registries.ITEM, singularityRl("ingots/netherite"), Tags.Items.INGOTS_NETHERITE),
            legacyToCommon(Registries.ITEM, singularityRl("ingots/nether_brick"), Tags.Items.BRICKS_NETHER),

            legacyToCommon(Registries.ITEM, singularityRl("leather"), Tags.Items.LEATHERS),

            legacyToCommon(Registries.ITEM, singularityRl("nuggets"), Tags.Items.NUGGETS),
            legacyToCommon(Registries.ITEM, singularityRl("nuggets/gold"), Tags.Items.NUGGETS_GOLD),
            legacyToCommon(Registries.ITEM, singularityRl("nuggets/iron"), Tags.Items.NUGGETS_IRON),

            legacyToCommon(Registries.ITEM, singularityRl("ores"), Tags.Items.ORES),
            legacyToCommon(Registries.ITEM, singularityRl("ores/netherite_scrap"), Tags.Items.ORES_NETHERITE_SCRAP),
            legacyToCommon(Registries.ITEM, singularityRl("ores/quartz"), Tags.Items.ORES_QUARTZ),

            legacyToCommon(Registries.ITEM, singularityRl("raw_materials"), Tags.Items.RAW_MATERIALS),
            legacyToCommon(Registries.ITEM, singularityRl("raw_materials/copper"), Tags.Items.RAW_MATERIALS_COPPER),
            legacyToCommon(Registries.ITEM, singularityRl("raw_materials/gold"), Tags.Items.RAW_MATERIALS_GOLD),
            legacyToCommon(Registries.ITEM, singularityRl("raw_materials/iron"), Tags.Items.RAW_MATERIALS_IRON),
            legacyToCommon(Registries.ITEM, singularityRl("rods"), Tags.Items.RODS),
            legacyToCommon(Registries.ITEM, singularityRl("rods/blaze"), Tags.Items.RODS_BLAZE),
            legacyToCommon(Registries.ITEM, singularityRl("rods/wooden"), Tags.Items.RODS_WOODEN),

            legacyToCommon(Registries.ITEM, singularityRl("sandstone"), Tags.Items.SANDSTONE_BLOCKS),

            legacyToCommon(Registries.ITEM, singularityRl("slimeballs"), Tags.Items.SLIME_BALLS),
            legacyToCommon(Registries.ITEM, singularityRl("stained_glass"), List.of(Tags.Items.GLASS_BLOCKS, Tags.Blocks.DYED)),
            legacyToCommon(Registries.ITEM, singularityRl("stained_glass_panes"), List.of(Tags.Items.GLASS_BLOCKS, Tags.Blocks.DYED)),
            legacyToCommon(Registries.ITEM, singularityRl("stone"), Tags.Items.STONES),
            legacyToCommon(Registries.ITEM, singularityRl("storage_blocks"), Tags.Items.STORAGE_BLOCKS),
            legacyToCommon(Registries.ITEM, singularityRl("storage_blocks/coal"), Tags.Items.STORAGE_BLOCKS_COAL),
            legacyToCommon(Registries.ITEM, singularityRl("storage_blocks/copper"), Tags.Items.STORAGE_BLOCKS_COPPER),
            legacyToCommon(Registries.ITEM, singularityRl("storage_blocks/diamond"), Tags.Items.STORAGE_BLOCKS_DIAMOND),
            legacyToCommon(Registries.ITEM, singularityRl("storage_blocks/emerald"), Tags.Items.STORAGE_BLOCKS_EMERALD),
            legacyToCommon(Registries.ITEM, singularityRl("storage_blocks/gold"), Tags.Items.STORAGE_BLOCKS_GOLD),
            legacyToCommon(Registries.ITEM, singularityRl("storage_blocks/iron"), Tags.Items.STORAGE_BLOCKS_IRON),
            legacyToCommon(Registries.ITEM, singularityRl("storage_blocks/lapis"), Tags.Items.STORAGE_BLOCKS_LAPIS),
            legacyToCommon(Registries.ITEM, singularityRl("storage_blocks/netherite"), Tags.Items.STORAGE_BLOCKS_NETHERITE),
            legacyToCommon(Registries.ITEM, singularityRl("storage_blocks/quartz"), Tags.Items.STORAGE_BLOCKS_QUARTZ),
            legacyToCommon(Registries.ITEM, singularityRl("storage_blocks/raw_copper"), Tags.Items.STORAGE_BLOCKS_RAW_COPPER),
            legacyToCommon(Registries.ITEM, singularityRl("storage_blocks/raw_gold"), Tags.Items.STORAGE_BLOCKS_RAW_GOLD),
            legacyToCommon(Registries.ITEM, singularityRl("storage_blocks/raw_iron"), Tags.Items.STORAGE_BLOCKS_RAW_IRON),
            legacyToCommon(Registries.ITEM, singularityRl("storage_blocks/redstone"), Tags.Items.STORAGE_BLOCKS_REDSTONE),
            legacyToCommon(Registries.ITEM, singularityRl("string"), Tags.Items.STRINGS),

            legacyToCommon(Registries.ITEM, singularityRl("tools"), Tags.Items.TOOLS),
            legacyToCommon(Registries.ITEM, singularityRl("tools/shields"), Tags.Items.TOOLS_SHIELD),
            legacyToCommon(Registries.ITEM, singularityRl("tools/bows"), Tags.Items.TOOLS_BOW),
            legacyToCommon(Registries.ITEM, singularityRl("tools/crossbows"), Tags.Items.TOOLS_CROSSBOW),
            legacyToCommon(Registries.ITEM, singularityRl("tools/fishing_rods"), Tags.Items.TOOLS_FISHING_ROD),
            legacyToCommon(Registries.ITEM, singularityRl("tools/tridents"), Tags.Items.TOOLS_TRIDENT),
            legacyToCommon(Registries.ITEM, singularityRl("tools/shears"), Tags.Items.TOOLS_SHEAR),

            legacyToCommon(Registries.ITEM, singularityRl("armors"), Tags.Items.ARMORS),
            //endregion

            //region Fluids
            legacyToCommon(Registries.FLUID, singularityRl("milk"), Tags.Fluids.MILK),
            //endregion

            //region Biomes
            legacyToCommon(Registries.BIOME, singularityRl("is_void"), Tags.Biomes.IS_VOID),

            legacyToCommon(Registries.BIOME, singularityRl("is_hot"), Tags.Biomes.IS_HOT),
            legacyToCommon(Registries.BIOME, singularityRl("is_hot/overworld"), Tags.Biomes.IS_HOT_OVERWORLD),
            legacyToCommon(Registries.BIOME, singularityRl("is_hot/nether"), Tags.Biomes.IS_HOT_NETHER),

            legacyToCommon(Registries.BIOME, singularityRl("is_cold"), Tags.Biomes.IS_COLD),
            legacyToCommon(Registries.BIOME, singularityRl("is_cold/overworld"), Tags.Biomes.IS_COLD_OVERWORLD),
            legacyToCommon(Registries.BIOME, singularityRl("is_cold/end"), Tags.Biomes.IS_COLD_END),

            legacyToCommon(Registries.BIOME, singularityRl("is_sparse"), Tags.Biomes.IS_SPARSE_VEGETATION),
            legacyToCommon(Registries.BIOME, singularityRl("is_sparse/overworld"), Tags.Biomes.IS_SPARSE_VEGETATION_OVERWORLD),
            legacyToCommon(Registries.BIOME, singularityRl("is_dense"), Tags.Biomes.IS_DENSE_VEGETATION),
            legacyToCommon(Registries.BIOME, singularityRl("is_dense/overworld"), Tags.Biomes.IS_DENSE_VEGETATION_OVERWORLD),

            legacyToCommon(Registries.BIOME, singularityRl("is_wet"), Tags.Biomes.IS_WET),
            legacyToCommon(Registries.BIOME, singularityRl("is_wet/overworld"), Tags.Biomes.IS_WET_OVERWORLD),
            legacyToCommon(Registries.BIOME, singularityRl("is_dry"), Tags.Biomes.IS_DRY),
            legacyToCommon(Registries.BIOME, singularityRl("is_dry/overworld"), Tags.Biomes.IS_DRY_OVERWORLD),
            legacyToCommon(Registries.BIOME, singularityRl("is_dry/nether"), Tags.Biomes.IS_DRY_NETHER),
            legacyToCommon(Registries.BIOME, singularityRl("is_dry/end"), Tags.Biomes.IS_DRY_END),

            legacyToCommon(Registries.BIOME, singularityRl("is_coniferous"), Tags.Biomes.IS_CONIFEROUS_TREE),

            legacyToCommon(Registries.BIOME, singularityRl("is_mountain"), Tags.Biomes.IS_MOUNTAIN),
            legacyToCommon(Registries.BIOME, singularityRl("is_peak"), Tags.Biomes.IS_MOUNTAIN_PEAK),
            legacyToCommon(Registries.BIOME, singularityRl("is_slope"), Tags.Biomes.IS_MOUNTAIN_SLOPE),

            legacyToCommon(Registries.BIOME, singularityRl("is_plains"), Tags.Biomes.IS_PLAINS),
            legacyToCommon(Registries.BIOME, singularityRl("is_swamp"), Tags.Biomes.IS_SWAMP),
            legacyToCommon(Registries.BIOME, singularityRl("is_desert"), Tags.Biomes.IS_DESERT),
            legacyToCommon(Registries.BIOME, singularityRl("is_mushroom"), Tags.Biomes.IS_MUSHROOM),

            legacyToCommon(Registries.BIOME, singularityRl("is_underground"), Tags.Biomes.IS_UNDERGROUND),
            legacyToCommon(Registries.BIOME, singularityRl("is_cave"), Tags.Biomes.IS_CAVE),

            legacyToCommon(Registries.BIOME, singularityRl("is_wasteland"), Tags.Biomes.IS_WASTELAND),
            legacyToCommon(Registries.BIOME, singularityRl("is_dead"), Tags.Biomes.IS_DEAD),
            legacyToCommon(Registries.BIOME, singularityRl("is_water"), Tags.Biomes.IS_AQUATIC),
            //endregion

            //region Unofficial
            // These tags have technically never been included with singularity in the past, but now that common convention
            // equivalents exist that singularity does include, these mappings have been added for convenience, just in case
            // some mods have been using them as a de-facto standard.

            // Workstations
            legacyToCommon(Registries.BLOCK, singularityRl("crafting_table"), Tags.Blocks.PLAYER_WORKSTATIONS_CRAFTING_TABLES),
            legacyToCommon(Registries.BLOCK, singularityRl("crafting_tables"), Tags.Blocks.PLAYER_WORKSTATIONS_CRAFTING_TABLES),
            legacyToCommon(Registries.BLOCK, singularityRl("furnace"), Tags.Blocks.PLAYER_WORKSTATIONS_FURNACES),
            legacyToCommon(Registries.BLOCK, singularityRl("furnaces"), Tags.Blocks.PLAYER_WORKSTATIONS_FURNACES),
            legacyToCommon(Registries.BLOCK, singularityRl("workbench"), Tags.Blocks.PLAYER_WORKSTATIONS_CRAFTING_TABLES),
            legacyToCommon(Registries.BLOCK, singularityRl("workbenches"), Tags.Blocks.PLAYER_WORKSTATIONS_CRAFTING_TABLES),

            legacyToCommon(Registries.ITEM, singularityRl("crafting_table"), Tags.Items.PLAYER_WORKSTATIONS_CRAFTING_TABLES),
            legacyToCommon(Registries.ITEM, singularityRl("crafting_tables"), Tags.Items.PLAYER_WORKSTATIONS_CRAFTING_TABLES),
            legacyToCommon(Registries.ITEM, singularityRl("furnace"), Tags.Items.PLAYER_WORKSTATIONS_FURNACES),
            legacyToCommon(Registries.ITEM, singularityRl("furnaces"), Tags.Items.PLAYER_WORKSTATIONS_FURNACES),
            legacyToCommon(Registries.ITEM, singularityRl("workbench"), Tags.Items.PLAYER_WORKSTATIONS_CRAFTING_TABLES),
            legacyToCommon(Registries.ITEM, singularityRl("workbenches"), Tags.Items.PLAYER_WORKSTATIONS_CRAFTING_TABLES),

            // Foods
            legacyToCommon(Registries.ITEM, singularityRl("food"), Tags.Items.FOODS),
            legacyToCommon(Registries.ITEM, singularityRl("foods"), Tags.Items.FOODS),
            legacyToCommon(Registries.ITEM, singularityRl("fruit"), Tags.Items.FOODS_FRUIT),
            legacyToCommon(Registries.ITEM, singularityRl("fruits"), Tags.Items.FOODS_FRUIT),
            legacyToCommon(Registries.ITEM, singularityRl("vegetable"), Tags.Items.FOODS_VEGETABLE),
            legacyToCommon(Registries.ITEM, singularityRl("vegetables"), Tags.Items.FOODS_VEGETABLE),
            legacyToCommon(Registries.ITEM, singularityRl("berry"), Tags.Items.FOODS_BERRY),
            legacyToCommon(Registries.ITEM, singularityRl("berries"), Tags.Items.FOODS_BERRY),
            legacyToCommon(Registries.ITEM, singularityRl("bread"), Tags.Items.FOODS_BREAD),
            legacyToCommon(Registries.ITEM, singularityRl("breads"), Tags.Items.FOODS_BREAD),
            legacyToCommon(Registries.ITEM, singularityRl("cookie"), Tags.Items.FOODS_COOKIE),
            legacyToCommon(Registries.ITEM, singularityRl("cookies"), Tags.Items.FOODS_COOKIE),
            legacyToCommon(Registries.ITEM, singularityRl("raw_meat"), Tags.Items.FOODS_RAW_MEAT),
            legacyToCommon(Registries.ITEM, singularityRl("raw_meats"), Tags.Items.FOODS_RAW_MEAT),
            legacyToCommon(Registries.ITEM, singularityRl("raw_fish"), Tags.Items.FOODS_RAW_FISH),
            legacyToCommon(Registries.ITEM, singularityRl("raw_fishes"), Tags.Items.FOODS_RAW_FISH),
            legacyToCommon(Registries.ITEM, singularityRl("cooked_meat"), Tags.Items.FOODS_COOKED_MEAT),
            legacyToCommon(Registries.ITEM, singularityRl("cooked_meats"), Tags.Items.FOODS_COOKED_MEAT),
            legacyToCommon(Registries.ITEM, singularityRl("cooked_fish"), Tags.Items.FOODS_COOKED_FISH),
            legacyToCommon(Registries.ITEM, singularityRl("cooked_fishes"), Tags.Items.FOODS_COOKED_FISH),
            legacyToCommon(Registries.ITEM, singularityRl("soup"), Tags.Items.FOODS_SOUP),
            legacyToCommon(Registries.ITEM, singularityRl("soups"), Tags.Items.FOODS_SOUP),
            legacyToCommon(Registries.ITEM, singularityRl("stew"), Tags.Items.FOODS_SOUP),
            legacyToCommon(Registries.ITEM, singularityRl("stews"), Tags.Items.FOODS_SOUP),
            legacyToCommon(Registries.ITEM, singularityRl("candy"), Tags.Items.FOODS_CANDY),
            legacyToCommon(Registries.ITEM, singularityRl("candies"), Tags.Items.FOODS_CANDY),

            // Fluids
            legacyToCommon(Registries.FLUID, singularityRl("water"), Tags.Fluids.WATER),
            legacyToCommon(Registries.FLUID, singularityRl("lava"), Tags.Fluids.LAVA),
            legacyToCommon(Registries.FLUID, singularityRl("honey"), Tags.Fluids.HONEY),

            // Biomes
            legacyToCommon(Registries.BIOME, singularityRl("is_overworld"), Tags.Biomes.IS_OVERWORLD),
            legacyToCommon(Registries.BIOME, singularityRl("is_nether"), Tags.Biomes.IS_NETHER),
            legacyToCommon(Registries.BIOME, singularityRl("is_nether_forest"), Tags.Biomes.IS_NETHER_FOREST),
            legacyToCommon(Registries.BIOME, singularityRl("is_end"), Tags.Biomes.IS_END),

            legacyToCommon(Registries.BIOME, singularityRl("is_forest"), Tags.Biomes.IS_FOREST),
            legacyToCommon(Registries.BIOME, singularityRl("is_jungle"), Tags.Biomes.IS_JUNGLE),
            legacyToCommon(Registries.BIOME, singularityRl("is_savanna"), Tags.Biomes.IS_SAVANNA),
            legacyToCommon(Registries.BIOME, singularityRl("is_floral"), Tags.Biomes.IS_FLORAL),
            legacyToCommon(Registries.BIOME, singularityRl("is_snowy"), Tags.Biomes.IS_SNOWY),
            legacyToCommon(Registries.BIOME, singularityRl("is_icy"), Tags.Biomes.IS_ICY),
            legacyToCommon(Registries.BIOME, singularityRl("is_ocean"), Tags.Biomes.IS_OCEAN)
            //endregion
    );

    /**
     * Creates a mapping from a legacy tag to a common convention equivalent.
     * <p>
     *     Example: {@code legacyToCommon(Registries.BLOCK, singularityRl("cobblestone"), Tags.Blocks.COBBLESTONES)} for {@code singularity:cobblestone} -> {@code c:cobblestones}
     * </p>
     * @param registryKey Example: {@link net.minecraft.core.registries.Registries.BLOCK}
     * @param legacyTagIdentifier Example: {@code singularityRl("cobblestone")}
     * @param replacementTag Example: {@link Tags.Blocks#COBBLESTONES}
     * @return A map entry with the legacy tag's TagKey and the suggested replacement tag ID.
     */
    private static <T, R> Map.Entry<TagKey<T>, String> legacyToCommon(ResourceKey<Registry<T>> registryKey,
                                                                      Identifier legacyTagIdentifier,
                                                                      TagKey<R> replacementTag) {
        return Map.entry(TagKey.create(registryKey, legacyTagIdentifier), replacementTag.toString());
    }

    /**
     * Same as {@link #legacyToCommon(ResourceKey, Identifier, TagKey)} but for multiple replacement tags.
     * This is useful when a single legacy tag is intended to be replaced by combining multiple common convention tags.
     */
    private static <T> Map.Entry<TagKey<T>, String> legacyToCommon(ResourceKey<Registry<T>> registryKey,
                                                                   Identifier legacyTagRessourceLocation,
                                                                   List<TagKey<?>> replacementTags) {
        return Map.entry(
                TagKey.create(registryKey, legacyTagRessourceLocation),
                replacementTags.stream().map(TagKey::toString).collect(Collectors.joining(" and "))
        );
    }

    private static Identifier singularityRl(String path) {
        return Identifier.fromNamespaceAndPath("singularity", path);
    }
}
