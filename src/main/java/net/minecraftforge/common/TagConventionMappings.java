package net.minecraftforge.common;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
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
            legacyToCommon(Registries.BLOCK, forgeRl("barrels"), Tags.Blocks.BARRELS), // forge:barrels -> c:barrels
            legacyToCommon(Registries.BLOCK, forgeRl("barrels/wooden"), Tags.Blocks.BARRELS_WOODEN),
            legacyToCommon(Registries.BLOCK, forgeRl("bookshelves"), Tags.Blocks.BOOKSHELVES),

            legacyToCommon(Registries.BLOCK, forgeRl("chests"), Tags.Blocks.CHESTS),
            legacyToCommon(Registries.BLOCK, forgeRl("chests/ender"), Tags.Blocks.CHESTS_ENDER),
            legacyToCommon(Registries.BLOCK, forgeRl("chests/trapped"), Tags.Blocks.CHESTS_TRAPPED),
            legacyToCommon(Registries.BLOCK, forgeRl("chests/wooden"), Tags.Blocks.CHESTS_WOODEN),

            legacyToCommon(Registries.BLOCK, forgeRl("cobblestone"), Tags.Blocks.COBBLESTONES),
            legacyToCommon(Registries.BLOCK, forgeRl("cobblestone/normal"), Tags.Blocks.COBBLESTONES_NORMAL),
            legacyToCommon(Registries.BLOCK, forgeRl("cobblestone/infested"), Tags.Blocks.COBBLESTONES_INFESTED),
            legacyToCommon(Registries.BLOCK, forgeRl("cobblestone/mossy"), Tags.Blocks.COBBLESTONES_MOSSY),
            legacyToCommon(Registries.BLOCK, forgeRl("cobblestone/deepslate"), Tags.Blocks.COBBLESTONES_DEEPSLATE),

            legacyToCommon(Registries.BLOCK, forgeRl("end_stones"), Tags.Blocks.END_STONES),

            legacyToCommon(Registries.BLOCK, forgeRl("fence_gates"), Tags.Blocks.FENCE_GATES),
            legacyToCommon(Registries.BLOCK, forgeRl("fence_gates/wooden"), Tags.Blocks.FENCE_GATES_WOODEN),
            legacyToCommon(Registries.BLOCK, forgeRl("fences"), Tags.Blocks.FENCES),
            legacyToCommon(Registries.BLOCK, forgeRl("fences/nether_brick"), Tags.Blocks.FENCES_NETHER_BRICK),
            legacyToCommon(Registries.BLOCK, forgeRl("fences/wooden"), Tags.Blocks.FENCES_WOODEN),

            legacyToCommon(Registries.BLOCK, forgeRl("glass"), Tags.Blocks.GLASS_BLOCKS),
            legacyToCommon(Registries.BLOCK, forgeRl("glass/black"), List.of(Tags.Blocks.GLASS_BLOCKS, Tags.Items.DYED_BLACK)),
            legacyToCommon(Registries.BLOCK, forgeRl("glass/blue"), List.of(Tags.Blocks.GLASS_BLOCKS, Tags.Items.DYED_BLUE)),
            legacyToCommon(Registries.BLOCK, forgeRl("glass/brown"), List.of(Tags.Blocks.GLASS_BLOCKS, Tags.Items.DYED_BROWN)),
            legacyToCommon(Registries.BLOCK, forgeRl("glass/colorless"), Tags.Blocks.GLASS_BLOCKS_COLORLESS),
            legacyToCommon(Registries.BLOCK, forgeRl("glass/cyan"), List.of(Tags.Blocks.GLASS_BLOCKS, Tags.Items.DYED_CYAN)),
            legacyToCommon(Registries.BLOCK, forgeRl("glass/gray"), List.of(Tags.Blocks.GLASS_BLOCKS, Tags.Items.DYED_GRAY)),
            legacyToCommon(Registries.BLOCK, forgeRl("glass/green"), List.of(Tags.Blocks.GLASS_BLOCKS, Tags.Items.DYED_GREEN)),
            legacyToCommon(Registries.BLOCK, forgeRl("glass/light_blue"), List.of(Tags.Blocks.GLASS_BLOCKS, Tags.Items.DYED_LIGHT_BLUE)),
            legacyToCommon(Registries.BLOCK, forgeRl("glass/light_gray"), List.of(Tags.Blocks.GLASS_BLOCKS, Tags.Items.DYED_LIGHT_GRAY)),
            legacyToCommon(Registries.BLOCK, forgeRl("glass/lime"), List.of(Tags.Blocks.GLASS_BLOCKS, Tags.Items.DYED_LIME)),
            legacyToCommon(Registries.BLOCK, forgeRl("glass/magenta"), List.of(Tags.Blocks.GLASS_BLOCKS, Tags.Items.DYED_MAGENTA)),
            legacyToCommon(Registries.BLOCK, forgeRl("glass/orange"), List.of(Tags.Blocks.GLASS_BLOCKS, Tags.Items.DYED_ORANGE)),
            legacyToCommon(Registries.BLOCK, forgeRl("glass/pink"), List.of(Tags.Blocks.GLASS_BLOCKS, Tags.Items.DYED_PINK)),
            legacyToCommon(Registries.BLOCK, forgeRl("glass/purple"), List.of(Tags.Blocks.GLASS_BLOCKS, Tags.Items.DYED_PURPLE)),
            legacyToCommon(Registries.BLOCK, forgeRl("glass/red"), List.of(Tags.Blocks.GLASS_BLOCKS, Tags.Items.DYED_RED)),
            legacyToCommon(Registries.BLOCK, forgeRl("glass/silica"), Tags.Blocks.GLASS_BLOCKS_CHEAP),
            legacyToCommon(Registries.BLOCK, forgeRl("glass/tinted"), Tags.Blocks.GLASS_BLOCKS_TINTED),
            legacyToCommon(Registries.BLOCK, forgeRl("glass/white"), List.of(Tags.Blocks.GLASS_BLOCKS, Tags.Items.DYED_WHITE)),
            legacyToCommon(Registries.BLOCK, forgeRl("glass/yellow"), List.of(Tags.Blocks.GLASS_BLOCKS, Tags.Items.DYED_YELLOW)),
            legacyToCommon(Registries.BLOCK, forgeRl("glass_panes"), Tags.Blocks.GLASS_PANES),
            legacyToCommon(Registries.BLOCK, forgeRl("glass_panes/colorless"), Tags.Blocks.GLASS_PANES_COLORLESS),
            legacyToCommon(Registries.BLOCK, forgeRl("glass_panes/black"), List.of(Tags.Blocks.GLASS_PANES, Tags.Items.DYED_BLACK)),
            legacyToCommon(Registries.BLOCK, forgeRl("glass_panes/blue"), List.of(Tags.Blocks.GLASS_PANES, Tags.Items.DYED_BLUE)),
            legacyToCommon(Registries.BLOCK, forgeRl("glass_panes/brown"), List.of(Tags.Blocks.GLASS_PANES, Tags.Items.DYED_BROWN)),
            legacyToCommon(Registries.BLOCK, forgeRl("glass_panes/cyan"), List.of(Tags.Blocks.GLASS_PANES, Tags.Items.DYED_CYAN)),
            legacyToCommon(Registries.BLOCK, forgeRl("glass_panes/gray"), List.of(Tags.Blocks.GLASS_PANES, Tags.Items.DYED_GRAY)),
            legacyToCommon(Registries.BLOCK, forgeRl("glass_panes/green"), List.of(Tags.Blocks.GLASS_PANES, Tags.Items.DYED_GREEN)),
            legacyToCommon(Registries.BLOCK, forgeRl("glass_panes/light_blue"), List.of(Tags.Blocks.GLASS_PANES, Tags.Items.DYED_LIGHT_BLUE)),
            legacyToCommon(Registries.BLOCK, forgeRl("glass_panes/light_gray"), List.of(Tags.Blocks.GLASS_PANES, Tags.Items.DYED_LIGHT_GRAY)),
            legacyToCommon(Registries.BLOCK, forgeRl("glass_panes/lime"), List.of(Tags.Blocks.GLASS_PANES, Tags.Items.DYED_LIME)),
            legacyToCommon(Registries.BLOCK, forgeRl("glass_panes/magenta"), List.of(Tags.Blocks.GLASS_PANES, Tags.Items.DYED_MAGENTA)),
            legacyToCommon(Registries.BLOCK, forgeRl("glass_panes/orange"), List.of(Tags.Blocks.GLASS_PANES, Tags.Items.DYED_ORANGE)),
            legacyToCommon(Registries.BLOCK, forgeRl("glass_panes/pink"), List.of(Tags.Blocks.GLASS_PANES, Tags.Items.DYED_PINK)),
            legacyToCommon(Registries.BLOCK, forgeRl("glass_panes/purple"), List.of(Tags.Blocks.GLASS_PANES, Tags.Items.DYED_PURPLE)),
            legacyToCommon(Registries.BLOCK, forgeRl("glass_panes/red"), List.of(Tags.Blocks.GLASS_PANES, Tags.Items.DYED_RED)),
            legacyToCommon(Registries.BLOCK, forgeRl("glass_panes/white"), List.of(Tags.Blocks.GLASS_PANES, Tags.Items.DYED_WHITE)),
            legacyToCommon(Registries.BLOCK, forgeRl("glass_panes/yellow"), List.of(Tags.Blocks.GLASS_PANES, Tags.Items.DYED_YELLOW)),
            legacyToCommon(Registries.BLOCK, forgeRl("gravel"), Tags.Blocks.GRAVELS),

            legacyToCommon(Registries.BLOCK, forgeRl("obsidian"), Tags.Blocks.OBSIDIANS),
            legacyToCommon(Registries.BLOCK, forgeRl("ore_bearing_ground/deepslate"), Tags.Blocks.ORE_BEARING_GROUND_DEEPSLATE),
            legacyToCommon(Registries.BLOCK, forgeRl("ore_bearing_ground/netherrack"), Tags.Blocks.ORE_BEARING_GROUND_NETHERRACK),
            legacyToCommon(Registries.BLOCK, forgeRl("ore_bearing_ground/stone"), Tags.Blocks.ORE_BEARING_GROUND_STONE),
            legacyToCommon(Registries.BLOCK, forgeRl("ore_rates/dense"), Tags.Blocks.ORE_RATES_DENSE),
            legacyToCommon(Registries.BLOCK, forgeRl("ore_rates/singular"), Tags.Blocks.ORE_RATES_SINGULAR),
            legacyToCommon(Registries.BLOCK, forgeRl("ore_rates/sparse"), Tags.Blocks.ORE_RATES_SPARSE),
            legacyToCommon(Registries.BLOCK, forgeRl("ores"), Tags.Blocks.ORES),
            legacyToCommon(Registries.BLOCK, forgeRl("ores/coal"), Tags.Blocks.ORES_COAL),
            legacyToCommon(Registries.BLOCK, forgeRl("ores/copper"), Tags.Blocks.ORES_COPPER),
            legacyToCommon(Registries.BLOCK, forgeRl("ores/diamond"), Tags.Blocks.ORES_DIAMOND),
            legacyToCommon(Registries.BLOCK, forgeRl("ores/emerald"), Tags.Blocks.ORES_EMERALD),
            legacyToCommon(Registries.BLOCK, forgeRl("ores/gold"), Tags.Blocks.ORES_GOLD),
            legacyToCommon(Registries.BLOCK, forgeRl("ores/iron"), Tags.Blocks.ORES_IRON),
            legacyToCommon(Registries.BLOCK, forgeRl("ores/lapis"), Tags.Blocks.ORES_LAPIS),
            legacyToCommon(Registries.BLOCK, forgeRl("ores/netherite_scrap"), Tags.Blocks.ORES_NETHERITE_SCRAP),
            legacyToCommon(Registries.BLOCK, forgeRl("ores/quartz"), Tags.Blocks.ORES_QUARTZ),
            legacyToCommon(Registries.BLOCK, forgeRl("ores/redstone"), Tags.Blocks.ORES_REDSTONE),
            legacyToCommon(Registries.BLOCK, forgeRl("ores_in_ground/deepslate"), Tags.Blocks.ORES_IN_GROUND_DEEPSLATE),
            legacyToCommon(Registries.BLOCK, forgeRl("ores_in_ground/netherrack"), Tags.Blocks.ORES_IN_GROUND_NETHERRACK),
            legacyToCommon(Registries.BLOCK, forgeRl("ores_in_ground/stone"), Tags.Blocks.ORES_IN_GROUND_STONE),

            legacyToCommon(Registries.BLOCK, forgeRl("sand"), Tags.Blocks.SANDS),
            legacyToCommon(Registries.BLOCK, forgeRl("sand/colorless"), Tags.Blocks.SANDS_COLORLESS),
            legacyToCommon(Registries.BLOCK, forgeRl("sand/red"), Tags.Blocks.SANDS_RED),
            legacyToCommon(Registries.BLOCK, forgeRl("sandstone"), Tags.Blocks.SANDSTONE_BLOCKS),
            legacyToCommon(Registries.BLOCK, forgeRl("stained_glass"), List.of(Tags.Blocks.GLASS_BLOCKS, Tags.Blocks.DYED)),
            legacyToCommon(Registries.BLOCK, forgeRl("stained_glass_panes"), List.of(Tags.Blocks.GLASS_PANES, Tags.Blocks.DYED)),
            legacyToCommon(Registries.BLOCK, forgeRl("stone"), Tags.Blocks.STONES),
            legacyToCommon(Registries.BLOCK, forgeRl("storage_blocks"), Tags.Blocks.STORAGE_BLOCKS),
            legacyToCommon(Registries.BLOCK, forgeRl("storage_blocks/coal"), Tags.Blocks.STORAGE_BLOCKS_COAL),
            legacyToCommon(Registries.BLOCK, forgeRl("storage_blocks/copper"), Tags.Blocks.STORAGE_BLOCKS_COPPER),
            legacyToCommon(Registries.BLOCK, forgeRl("storage_blocks/diamond"), Tags.Blocks.STORAGE_BLOCKS_DIAMOND),
            legacyToCommon(Registries.BLOCK, forgeRl("storage_blocks/emerald"), Tags.Blocks.STORAGE_BLOCKS_EMERALD),
            legacyToCommon(Registries.BLOCK, forgeRl("storage_blocks/gold"), Tags.Blocks.STORAGE_BLOCKS_GOLD),
            legacyToCommon(Registries.BLOCK, forgeRl("storage_blocks/iron"), Tags.Blocks.STORAGE_BLOCKS_IRON),
            legacyToCommon(Registries.BLOCK, forgeRl("storage_blocks/lapis"), Tags.Blocks.STORAGE_BLOCKS_LAPIS),
            legacyToCommon(Registries.BLOCK, forgeRl("storage_blocks/netherite"), Tags.Blocks.STORAGE_BLOCKS_NETHERITE),
            legacyToCommon(Registries.BLOCK, forgeRl("storage_blocks/quartz"), Tags.Blocks.STORAGE_BLOCKS_QUARTZ),
            legacyToCommon(Registries.BLOCK, forgeRl("storage_blocks/raw_copper"), Tags.Blocks.STORAGE_BLOCKS_RAW_COPPER),
            legacyToCommon(Registries.BLOCK, forgeRl("storage_blocks/raw_gold"), Tags.Blocks.STORAGE_BLOCKS_RAW_GOLD),
            legacyToCommon(Registries.BLOCK, forgeRl("storage_blocks/raw_iron"), Tags.Blocks.STORAGE_BLOCKS_RAW_IRON),
            legacyToCommon(Registries.BLOCK, forgeRl("storage_blocks/redstone"), Tags.Blocks.STORAGE_BLOCKS_REDSTONE),
            //endregion

            //region Entity Types
            legacyToCommon(Registries.ENTITY_TYPE, forgeRl("bosses"), Tags.EntityTypes.BOSSES),
            //endregion

            //region Items
            legacyToCommon(Registries.ITEM, forgeRl("barrels"), Tags.Items.BARRELS),
            legacyToCommon(Registries.ITEM, forgeRl("barrels/wooden"), Tags.Items.BARRELS_WOODEN),
            legacyToCommon(Registries.ITEM, forgeRl("bones"), Tags.Items.BONES),
            legacyToCommon(Registries.ITEM, forgeRl("bookshelves"), Tags.Items.BOOKSHELVES),

            legacyToCommon(Registries.ITEM, forgeRl("chests"), Tags.Items.CHESTS),
            legacyToCommon(Registries.ITEM, forgeRl("chests/ender"), Tags.Items.CHESTS_ENDER),
            legacyToCommon(Registries.ITEM, forgeRl("chests/trapped"), Tags.Items.CHESTS_TRAPPED),
            legacyToCommon(Registries.ITEM, forgeRl("chests/wooden"), Tags.Items.CHESTS_WOODEN),
            legacyToCommon(Registries.ITEM, forgeRl("cobblestone"), Tags.Items.COBBLESTONES),
            legacyToCommon(Registries.ITEM, forgeRl("cobblestone/normal"), Tags.Items.COBBLESTONES_NORMAL),
            legacyToCommon(Registries.ITEM, forgeRl("cobblestone/infested"), Tags.Items.COBBLESTONES_INFESTED),
            legacyToCommon(Registries.ITEM, forgeRl("cobblestone/mossy"), Tags.Items.COBBLESTONES_MOSSY),
            legacyToCommon(Registries.ITEM, forgeRl("cobblestone/deepslate"), Tags.Items.COBBLESTONES_DEEPSLATE),
            legacyToCommon(Registries.ITEM, forgeRl("crops"), Tags.Items.CROPS),
            legacyToCommon(Registries.ITEM, forgeRl("crops/beetroot"), Tags.Items.CROPS_BEETROOT),
            legacyToCommon(Registries.ITEM, forgeRl("crops/carrot"), Tags.Items.CROPS_CARROT),
            legacyToCommon(Registries.ITEM, forgeRl("crops/potato"), Tags.Items.CROPS_POTATO),
            legacyToCommon(Registries.ITEM, forgeRl("crops/wheat"), Tags.Items.CROPS_WHEAT),

            legacyToCommon(Registries.ITEM, forgeRl("dusts"), Tags.Items.DUSTS),
            legacyToCommon(Registries.ITEM, forgeRl("dusts/redstone"), Tags.Items.DUSTS_REDSTONE),
            legacyToCommon(Registries.ITEM, forgeRl("dusts/glowstone"), Tags.Items.DUSTS_GLOWSTONE),
            legacyToCommon(Registries.ITEM, forgeRl("dyes"), Tags.Items.DYES),
            legacyToCommon(Registries.ITEM, forgeRl("dyes/black"), Tags.Items.DYES_BLACK),
            legacyToCommon(Registries.ITEM, forgeRl("dyes/red"), Tags.Items.DYES_RED),
            legacyToCommon(Registries.ITEM, forgeRl("dyes/green"), Tags.Items.DYES_GREEN),
            legacyToCommon(Registries.ITEM, forgeRl("dyes/brown"), Tags.Items.DYES_BROWN),
            legacyToCommon(Registries.ITEM, forgeRl("dyes/blue"), Tags.Items.DYES_BLUE),
            legacyToCommon(Registries.ITEM, forgeRl("dyes/purple"), Tags.Items.DYES_PURPLE),
            legacyToCommon(Registries.ITEM, forgeRl("dyes/cyan"), Tags.Items.DYES_CYAN),
            legacyToCommon(Registries.ITEM, forgeRl("dyes/light_gray"), Tags.Items.DYES_LIGHT_GRAY),
            legacyToCommon(Registries.ITEM, forgeRl("dyes/gray"), Tags.Items.DYES_GRAY),
            legacyToCommon(Registries.ITEM, forgeRl("dyes/pink"), Tags.Items.DYES_PINK),
            legacyToCommon(Registries.ITEM, forgeRl("dyes/lime"), Tags.Items.DYES_LIME),
            legacyToCommon(Registries.ITEM, forgeRl("dyes/yellow"), Tags.Items.DYES_YELLOW),
            legacyToCommon(Registries.ITEM, forgeRl("dyes/light_blue"), Tags.Items.DYES_LIGHT_BLUE),
            legacyToCommon(Registries.ITEM, forgeRl("dyes/magenta"), Tags.Items.DYES_MAGENTA),
            legacyToCommon(Registries.ITEM, forgeRl("dyes/orange"), Tags.Items.DYES_ORANGE),
            legacyToCommon(Registries.ITEM, forgeRl("dyes/white"), Tags.Items.DYES_WHITE),

            legacyToCommon(Registries.ITEM, forgeRl("eggs"), Tags.Items.EGGS),
            legacyToCommon(Registries.ITEM, forgeRl("end_stones"), Tags.Items.END_STONES),
            legacyToCommon(Registries.ITEM, forgeRl("ender_pearls"), Tags.Items.ENDER_PEARLS),

            legacyToCommon(Registries.ITEM, forgeRl("feathers"), Tags.Items.FEATHERS),
            legacyToCommon(Registries.ITEM, forgeRl("fence_gates"), Tags.Items.FENCE_GATES),
            legacyToCommon(Registries.ITEM, forgeRl("fence_gates/wooden"), Tags.Items.FENCE_GATES_WOODEN),
            legacyToCommon(Registries.ITEM, forgeRl("fences"), Tags.Items.FENCES),
            legacyToCommon(Registries.ITEM, forgeRl("fences/nether_brick"), Tags.Items.FENCES_NETHER_BRICK),
            legacyToCommon(Registries.ITEM, forgeRl("fences/wooden"), Tags.Items.FENCES_WOODEN),

            legacyToCommon(Registries.ITEM, forgeRl("gems"), Tags.Items.GEMS),
            legacyToCommon(Registries.ITEM, forgeRl("gems/diamond"), Tags.Items.GEMS_DIAMOND),
            legacyToCommon(Registries.ITEM, forgeRl("gems/emerald"), Tags.Items.GEMS_EMERALD),
            legacyToCommon(Registries.ITEM, forgeRl("gems/amethyst"), Tags.Items.GEMS_AMETHYST),
            legacyToCommon(Registries.ITEM, forgeRl("gems/lapis"), Tags.Items.GEMS_LAPIS),
            legacyToCommon(Registries.ITEM, forgeRl("gems/quartz"), Tags.Items.GEMS_QUARTZ),
            legacyToCommon(Registries.ITEM, forgeRl("glass"), Tags.Items.GLASS_BLOCKS),
            legacyToCommon(Registries.ITEM, forgeRl("glass/black"), List.of(Tags.Items.GLASS_BLOCKS, Tags.Items.DYED_BLACK)),
            legacyToCommon(Registries.ITEM, forgeRl("glass/blue"), List.of(Tags.Items.GLASS_BLOCKS, Tags.Items.DYED_BLUE)),
            legacyToCommon(Registries.ITEM, forgeRl("glass/brown"), List.of(Tags.Items.GLASS_BLOCKS, Tags.Items.DYED_BROWN)),
            legacyToCommon(Registries.ITEM, forgeRl("glass/colorless"), Tags.Items.GLASS_BLOCKS_COLORLESS),
            legacyToCommon(Registries.ITEM, forgeRl("glass/cyan"), List.of(Tags.Items.GLASS_BLOCKS, Tags.Items.DYED_CYAN)),
            legacyToCommon(Registries.ITEM, forgeRl("glass/gray"), List.of(Tags.Items.GLASS_BLOCKS, Tags.Items.DYED_GRAY)),
            legacyToCommon(Registries.ITEM, forgeRl("glass/green"), List.of(Tags.Items.GLASS_BLOCKS, Tags.Items.DYED_GREEN)),
            legacyToCommon(Registries.ITEM, forgeRl("glass/light_blue"), List.of(Tags.Items.GLASS_BLOCKS, Tags.Items.DYED_LIGHT_BLUE)),
            legacyToCommon(Registries.ITEM, forgeRl("glass/light_gray"), List.of(Tags.Items.GLASS_BLOCKS, Tags.Items.DYED_LIGHT_GRAY)),
            legacyToCommon(Registries.ITEM, forgeRl("glass/lime"), List.of(Tags.Items.GLASS_BLOCKS, Tags.Items.DYED_LIME)),
            legacyToCommon(Registries.ITEM, forgeRl("glass/magenta"), List.of(Tags.Items.GLASS_BLOCKS, Tags.Items.DYED_MAGENTA)),
            legacyToCommon(Registries.ITEM, forgeRl("glass/orange"), List.of(Tags.Items.GLASS_BLOCKS, Tags.Items.DYED_ORANGE)),
            legacyToCommon(Registries.ITEM, forgeRl("glass/pink"), List.of(Tags.Items.GLASS_BLOCKS, Tags.Items.DYED_PINK)),
            legacyToCommon(Registries.ITEM, forgeRl("glass/purple"), List.of(Tags.Items.GLASS_BLOCKS, Tags.Items.DYED_PURPLE)),
            legacyToCommon(Registries.ITEM, forgeRl("glass/red"), List.of(Tags.Items.GLASS_BLOCKS, Tags.Items.DYED_RED)),
            legacyToCommon(Registries.ITEM, forgeRl("glass/silica"), Tags.Items.GLASS_BLOCKS_CHEAP),
            legacyToCommon(Registries.ITEM, forgeRl("glass/tinted"), Tags.Items.GLASS_BLOCKS_TINTED),
            legacyToCommon(Registries.ITEM, forgeRl("glass/white"), List.of(Tags.Items.GLASS_BLOCKS, Tags.Items.DYED_WHITE)),
            legacyToCommon(Registries.ITEM, forgeRl("glass/yellow"), List.of(Tags.Items.GLASS_BLOCKS, Tags.Items.DYED_YELLOW)),
            legacyToCommon(Registries.ITEM, forgeRl("glass_panes"), Tags.Items.GLASS_PANES),
            legacyToCommon(Registries.ITEM, forgeRl("glass_panes/black"), List.of(Tags.Items.GLASS_PANES, Tags.Items.DYED_BLACK)),
            legacyToCommon(Registries.ITEM, forgeRl("glass_panes/blue"), List.of(Tags.Items.GLASS_PANES, Tags.Items.DYED_BLUE)),
            legacyToCommon(Registries.ITEM, forgeRl("glass_panes/brown"), List.of(Tags.Items.GLASS_PANES, Tags.Items.DYED_BROWN)),
            legacyToCommon(Registries.ITEM, forgeRl("glass_panes/colorless"), Tags.Items.GLASS_PANES_COLORLESS),
            legacyToCommon(Registries.ITEM, forgeRl("glass_panes/cyan"), List.of(Tags.Items.GLASS_PANES, Tags.Items.DYED_CYAN)),
            legacyToCommon(Registries.ITEM, forgeRl("glass_panes/gray"), List.of(Tags.Items.GLASS_PANES, Tags.Items.DYED_GRAY)),
            legacyToCommon(Registries.ITEM, forgeRl("glass_panes/green"), List.of(Tags.Items.GLASS_PANES, Tags.Items.DYED_GREEN)),
            legacyToCommon(Registries.ITEM, forgeRl("glass_panes/light_blue"), List.of(Tags.Items.GLASS_PANES, Tags.Items.DYED_LIGHT_BLUE)),
            legacyToCommon(Registries.ITEM, forgeRl("glass_panes/light_gray"), List.of(Tags.Items.GLASS_PANES, Tags.Items.DYED_LIGHT_GRAY)),
            legacyToCommon(Registries.ITEM, forgeRl("glass_panes/lime"), List.of(Tags.Items.GLASS_PANES, Tags.Items.DYED_LIME)),
            legacyToCommon(Registries.ITEM, forgeRl("glass_panes/magenta"), List.of(Tags.Items.GLASS_PANES, Tags.Items.DYED_MAGENTA)),
            legacyToCommon(Registries.ITEM, forgeRl("glass_panes/orange"), List.of(Tags.Items.GLASS_PANES, Tags.Items.DYED_ORANGE)),
            legacyToCommon(Registries.ITEM, forgeRl("glass_panes/pink"), List.of(Tags.Items.GLASS_PANES, Tags.Items.DYED_PINK)),
            legacyToCommon(Registries.ITEM, forgeRl("glass_panes/purple"), List.of(Tags.Items.GLASS_PANES, Tags.Items.DYED_PURPLE)),
            legacyToCommon(Registries.ITEM, forgeRl("glass_panes/red"), List.of(Tags.Items.GLASS_PANES, Tags.Items.DYED_RED)),
            legacyToCommon(Registries.ITEM, forgeRl("glass_panes/white"), List.of(Tags.Items.GLASS_PANES, Tags.Items.DYED_WHITE)),
            legacyToCommon(Registries.ITEM, forgeRl("glass_panes/yellow"), List.of(Tags.Items.GLASS_PANES, Tags.Items.DYED_YELLOW)),
            legacyToCommon(Registries.ITEM, forgeRl("gravel"), Tags.Items.GRAVELS),
            legacyToCommon(Registries.ITEM, forgeRl("gunpowder"), Tags.Items.GUNPOWDERS),

            legacyToCommon(Registries.ITEM, forgeRl("ingots"), Tags.Items.INGOTS),
            legacyToCommon(Registries.ITEM, forgeRl("ingots/brick"), Tags.Items.BRICKS_NORMAL),
            legacyToCommon(Registries.ITEM, forgeRl("ingots/copper"), Tags.Items.INGOTS_COPPER),
            legacyToCommon(Registries.ITEM, forgeRl("ingots/gold"), Tags.Items.INGOTS_GOLD),
            legacyToCommon(Registries.ITEM, forgeRl("ingots/iron"), Tags.Items.INGOTS_IRON),
            legacyToCommon(Registries.ITEM, forgeRl("ingots/netherite"), Tags.Items.INGOTS_NETHERITE),
            legacyToCommon(Registries.ITEM, forgeRl("ingots/nether_brick"), Tags.Items.BRICKS_NETHER),

            legacyToCommon(Registries.ITEM, forgeRl("leather"), Tags.Items.LEATHERS),

            legacyToCommon(Registries.ITEM, forgeRl("mushrooms"), Tags.Items.MUSHROOMS),

            legacyToCommon(Registries.ITEM, forgeRl("nether_stars"), Tags.Items.NETHER_STARS),
            legacyToCommon(Registries.ITEM, forgeRl("netherrack"), Tags.Items.NETHERRACKS),
            legacyToCommon(Registries.ITEM, forgeRl("nuggets"), Tags.Items.NUGGETS),
            legacyToCommon(Registries.ITEM, forgeRl("nuggets/gold"), Tags.Items.NUGGETS_GOLD),
            legacyToCommon(Registries.ITEM, forgeRl("nuggets/iron"), Tags.Items.NUGGETS_IRON),

            legacyToCommon(Registries.ITEM, forgeRl("obsidian"), Tags.Items.OBSIDIANS),
            legacyToCommon(Registries.ITEM, forgeRl("ore_bearing_ground/deepslate"), Tags.Items.ORE_BEARING_GROUND_DEEPSLATE),
            legacyToCommon(Registries.ITEM, forgeRl("ore_bearing_ground/netherrack"), Tags.Items.ORE_BEARING_GROUND_NETHERRACK),
            legacyToCommon(Registries.ITEM, forgeRl("ore_bearing_ground/stone"), Tags.Items.ORE_BEARING_GROUND_STONE),
            legacyToCommon(Registries.ITEM, forgeRl("ore_rates/dense"), Tags.Items.ORE_RATES_DENSE),
            legacyToCommon(Registries.ITEM, forgeRl("ore_rates/singular"), Tags.Items.ORE_RATES_SINGULAR),
            legacyToCommon(Registries.ITEM, forgeRl("ore_rates/sparse"), Tags.Items.ORE_RATES_SPARSE),
            legacyToCommon(Registries.ITEM, forgeRl("ores"), Tags.Items.ORES),
            legacyToCommon(Registries.ITEM, forgeRl("ores/coal"), Tags.Items.ORES_COAL),
            legacyToCommon(Registries.ITEM, forgeRl("ores/copper"), Tags.Items.ORES_COPPER),
            legacyToCommon(Registries.ITEM, forgeRl("ores/diamond"), Tags.Items.ORES_DIAMOND),
            legacyToCommon(Registries.ITEM, forgeRl("ores/emerald"), Tags.Items.ORES_EMERALD),
            legacyToCommon(Registries.ITEM, forgeRl("ores/gold"), Tags.Items.ORES_GOLD),
            legacyToCommon(Registries.ITEM, forgeRl("ores/iron"), Tags.Items.ORES_IRON),
            legacyToCommon(Registries.ITEM, forgeRl("ores/lapis"), Tags.Items.ORES_LAPIS),
            legacyToCommon(Registries.ITEM, forgeRl("ores/netherite_scrap"), Tags.Items.ORES_NETHERITE_SCRAP),
            legacyToCommon(Registries.ITEM, forgeRl("ores/quartz"), Tags.Items.ORES_QUARTZ),
            legacyToCommon(Registries.ITEM, forgeRl("ores/redstone"), Tags.Items.ORES_REDSTONE),
            legacyToCommon(Registries.ITEM, forgeRl("ores_in_ground/deepslate"), Tags.Items.ORES_IN_GROUND_DEEPSLATE),
            legacyToCommon(Registries.ITEM, forgeRl("ores_in_ground/netherrack"), Tags.Items.ORES_IN_GROUND_NETHERRACK),
            legacyToCommon(Registries.ITEM, forgeRl("ores_in_ground/stone"), Tags.Items.ORES_IN_GROUND_STONE),

            legacyToCommon(Registries.ITEM, forgeRl("raw_materials"), Tags.Items.RAW_MATERIALS),
            legacyToCommon(Registries.ITEM, forgeRl("raw_materials/copper"), Tags.Items.RAW_MATERIALS_COPPER),
            legacyToCommon(Registries.ITEM, forgeRl("raw_materials/gold"), Tags.Items.RAW_MATERIALS_GOLD),
            legacyToCommon(Registries.ITEM, forgeRl("raw_materials/iron"), Tags.Items.RAW_MATERIALS_IRON),
            legacyToCommon(Registries.ITEM, forgeRl("rods"), Tags.Items.RODS),
            legacyToCommon(Registries.ITEM, forgeRl("rods/blaze"), Tags.Items.RODS_BLAZE),
            legacyToCommon(Registries.ITEM, forgeRl("rods/wooden"), Tags.Items.RODS_WOODEN),

            legacyToCommon(Registries.ITEM, forgeRl("sand"), Tags.Items.SANDS),
            legacyToCommon(Registries.ITEM, forgeRl("sand/colorless"), Tags.Items.SANDS_COLORLESS),
            legacyToCommon(Registries.ITEM, forgeRl("sand/red"), Tags.Items.SANDS_RED),
            legacyToCommon(Registries.ITEM, forgeRl("sandstone"), Tags.Items.SANDSTONE_BLOCKS),
            legacyToCommon(Registries.ITEM, forgeRl("seeds"), Tags.Items.SEEDS),
            legacyToCommon(Registries.ITEM, forgeRl("seeds/beetroot"), Tags.Items.SEEDS_BEETROOT),
            legacyToCommon(Registries.ITEM, forgeRl("seeds/melon"), Tags.Items.SEEDS_MELON),
            legacyToCommon(Registries.ITEM, forgeRl("seeds/pumpkin"), Tags.Items.SEEDS_PUMPKIN),
            legacyToCommon(Registries.ITEM, forgeRl("seeds/wheat"), Tags.Items.SEEDS_WHEAT),

            legacyToCommon(Registries.ITEM, forgeRl("slimeballs"), Tags.Items.SLIMEBALLS),
            legacyToCommon(Registries.ITEM, forgeRl("stained_glass"), List.of(Tags.Items.GLASS_BLOCKS, Tags.Blocks.DYED)),
            legacyToCommon(Registries.ITEM, forgeRl("stained_glass_panes"), List.of(Tags.Items.GLASS_BLOCKS, Tags.Blocks.DYED)),
            legacyToCommon(Registries.ITEM, forgeRl("stone"), Tags.Items.STONES),
            legacyToCommon(Registries.ITEM, forgeRl("storage_blocks"), Tags.Items.STORAGE_BLOCKS),
            legacyToCommon(Registries.ITEM, forgeRl("storage_blocks/coal"), Tags.Items.STORAGE_BLOCKS_COAL),
            legacyToCommon(Registries.ITEM, forgeRl("storage_blocks/copper"), Tags.Items.STORAGE_BLOCKS_COPPER),
            legacyToCommon(Registries.ITEM, forgeRl("storage_blocks/diamond"), Tags.Items.STORAGE_BLOCKS_DIAMOND),
            legacyToCommon(Registries.ITEM, forgeRl("storage_blocks/emerald"), Tags.Items.STORAGE_BLOCKS_EMERALD),
            legacyToCommon(Registries.ITEM, forgeRl("storage_blocks/gold"), Tags.Items.STORAGE_BLOCKS_GOLD),
            legacyToCommon(Registries.ITEM, forgeRl("storage_blocks/iron"), Tags.Items.STORAGE_BLOCKS_IRON),
            legacyToCommon(Registries.ITEM, forgeRl("storage_blocks/lapis"), Tags.Items.STORAGE_BLOCKS_LAPIS),
            legacyToCommon(Registries.ITEM, forgeRl("storage_blocks/netherite"), Tags.Items.STORAGE_BLOCKS_NETHERITE),
            legacyToCommon(Registries.ITEM, forgeRl("storage_blocks/quartz"), Tags.Items.STORAGE_BLOCKS_QUARTZ),
            legacyToCommon(Registries.ITEM, forgeRl("storage_blocks/raw_copper"), Tags.Items.STORAGE_BLOCKS_RAW_COPPER),
            legacyToCommon(Registries.ITEM, forgeRl("storage_blocks/raw_gold"), Tags.Items.STORAGE_BLOCKS_RAW_GOLD),
            legacyToCommon(Registries.ITEM, forgeRl("storage_blocks/raw_iron"), Tags.Items.STORAGE_BLOCKS_RAW_IRON),
            legacyToCommon(Registries.ITEM, forgeRl("storage_blocks/redstone"), Tags.Items.STORAGE_BLOCKS_REDSTONE),
            legacyToCommon(Registries.ITEM, forgeRl("string"), Tags.Items.STRINGS),

            legacyToCommon(Registries.ITEM, forgeRl("tools"), Tags.Items.TOOLS),
            legacyToCommon(Registries.ITEM, forgeRl("tools/shields"), Tags.Items.TOOLS_SHIELDS),
            legacyToCommon(Registries.ITEM, forgeRl("tools/bows"), Tags.Items.TOOLS_BOWS),
            legacyToCommon(Registries.ITEM, forgeRl("tools/crossbows"), Tags.Items.TOOLS_CROSSBOWS),
            legacyToCommon(Registries.ITEM, forgeRl("tools/fishing_rods"), Tags.Items.TOOLS_FISHING_RODS),
            legacyToCommon(Registries.ITEM, forgeRl("tools/tridents"), Tags.Items.TOOLS_SPEARS),
            legacyToCommon(Registries.ITEM, forgeRl("tools/shears"), Tags.Items.TOOLS_SHEARS),

            legacyToCommon(Registries.ITEM, forgeRl("armors"), Tags.Items.ARMORS),
            //endregion

            //region Fluids
            legacyToCommon(Registries.FLUID, forgeRl("milk"), Tags.Fluids.MILK),
            legacyToCommon(Registries.FLUID, forgeRl("gaseous"), Tags.Fluids.GASEOUS),
            //endregion

            //region Biomes
            legacyToCommon(Registries.BIOME, forgeRl("is_void"), Tags.Biomes.IS_VOID),

            legacyToCommon(Registries.BIOME, forgeRl("is_hot"), Tags.Biomes.IS_HOT),
            legacyToCommon(Registries.BIOME, forgeRl("is_hot/overworld"), Tags.Biomes.IS_HOT_OVERWORLD),
            legacyToCommon(Registries.BIOME, forgeRl("is_hot/nether"), Tags.Biomes.IS_HOT_NETHER),
            legacyToCommon(Registries.BIOME, forgeRl("is_hot/end"), Tags.Biomes.IS_HOT_END),

            legacyToCommon(Registries.BIOME, forgeRl("is_cold"), Tags.Biomes.IS_COLD),
            legacyToCommon(Registries.BIOME, forgeRl("is_cold/overworld"), Tags.Biomes.IS_COLD_OVERWORLD),
            legacyToCommon(Registries.BIOME, forgeRl("is_cold/nether"), Tags.Biomes.IS_COLD_NETHER),
            legacyToCommon(Registries.BIOME, forgeRl("is_cold/end"), Tags.Biomes.IS_COLD_END),

            legacyToCommon(Registries.BIOME, forgeRl("is_sparse"), Tags.Biomes.IS_SPARSE_VEGETATION),
            legacyToCommon(Registries.BIOME, forgeRl("is_sparse/overworld"), Tags.Biomes.IS_SPARSE_VEGETATION_OVERWORLD),
            legacyToCommon(Registries.BIOME, forgeRl("is_sparse/nether"), Tags.Biomes.IS_SPARSE_VEGETATION_NETHER),
            legacyToCommon(Registries.BIOME, forgeRl("is_sparse/end"), Tags.Biomes.IS_SPARSE_VEGETATION_END),
            legacyToCommon(Registries.BIOME, forgeRl("is_dense"), Tags.Biomes.IS_DENSE_VEGETATION),
            legacyToCommon(Registries.BIOME, forgeRl("is_dense/overworld"), Tags.Biomes.IS_DENSE_VEGETATION_OVERWORLD),
            legacyToCommon(Registries.BIOME, forgeRl("is_dense/nether"), Tags.Biomes.IS_DENSE_VEGETATION_NETHER),
            legacyToCommon(Registries.BIOME, forgeRl("is_dense/end"), Tags.Biomes.IS_DENSE_VEGETATION_END),

            legacyToCommon(Registries.BIOME, forgeRl("is_wet"), Tags.Biomes.IS_WET),
            legacyToCommon(Registries.BIOME, forgeRl("is_wet/overworld"), Tags.Biomes.IS_WET_OVERWORLD),
            legacyToCommon(Registries.BIOME, forgeRl("is_wet/nether"), Tags.Biomes.IS_WET_NETHER),
            legacyToCommon(Registries.BIOME, forgeRl("is_wet/end"), Tags.Biomes.IS_WET_END),
            legacyToCommon(Registries.BIOME, forgeRl("is_dry"), Tags.Biomes.IS_DRY),
            legacyToCommon(Registries.BIOME, forgeRl("is_dry/overworld"), Tags.Biomes.IS_DRY_OVERWORLD),
            legacyToCommon(Registries.BIOME, forgeRl("is_dry/nether"), Tags.Biomes.IS_DRY_NETHER),
            legacyToCommon(Registries.BIOME, forgeRl("is_dry/end"), Tags.Biomes.IS_DRY_END),

            legacyToCommon(Registries.BIOME, forgeRl("is_coniferous"), Tags.Biomes.IS_CONIFEROUS_TREE),

            legacyToCommon(Registries.BIOME, forgeRl("is_mountain"), Tags.Biomes.IS_MOUNTAIN),
            legacyToCommon(Registries.BIOME, forgeRl("is_peak"), Tags.Biomes.IS_MOUNTAIN_PEAK),
            legacyToCommon(Registries.BIOME, forgeRl("is_slope"), Tags.Biomes.IS_MOUNTAIN_SLOPE),

            legacyToCommon(Registries.BIOME, forgeRl("is_plains"), Tags.Biomes.IS_PLAINS),
            legacyToCommon(Registries.BIOME, forgeRl("is_swamp"), Tags.Biomes.IS_SWAMP),
            legacyToCommon(Registries.BIOME, forgeRl("is_desert"), Tags.Biomes.IS_DESERT),
            legacyToCommon(Registries.BIOME, forgeRl("is_mushroom"), Tags.Biomes.IS_MUSHROOM),

            legacyToCommon(Registries.BIOME, forgeRl("is_underground"), Tags.Biomes.IS_UNDERGROUND),
            legacyToCommon(Registries.BIOME, forgeRl("is_cave"), Tags.Biomes.IS_CAVE),

            legacyToCommon(Registries.BIOME, forgeRl("is_lush"), Tags.Biomes.IS_LUSH),
            legacyToCommon(Registries.BIOME, forgeRl("is_magical"), Tags.Biomes.IS_MAGICAL),
            legacyToCommon(Registries.BIOME, forgeRl("is_rare"), Tags.Biomes.IS_RARE),
            legacyToCommon(Registries.BIOME, forgeRl("is_plateau"), Tags.Biomes.IS_PLATEAU),
            legacyToCommon(Registries.BIOME, forgeRl("is_modified"), Tags.Biomes.IS_MODIFIED),
            legacyToCommon(Registries.BIOME, forgeRl("is_spooky"), Tags.Biomes.IS_SPOOKY),
            legacyToCommon(Registries.BIOME, forgeRl("is_wasteland"), Tags.Biomes.IS_WASTELAND),
            legacyToCommon(Registries.BIOME, forgeRl("is_dead"), Tags.Biomes.IS_DEAD),
            legacyToCommon(Registries.BIOME, forgeRl("is_water"), Tags.Biomes.IS_AQUATIC),
            //endregion

            //region Unofficial
            // These tags have technically never been included with Forge in the past, but now that common convention
            // equivalents exist that Forge does include, these mappings have been added for convenience, just in case
            // some mods have been using them as a de-facto standard.

            // Workstations
            legacyToCommon(Registries.BLOCK, forgeRl("crafting_table"), Tags.Blocks.PLAYER_WORKSTATIONS_CRAFTING_TABLES),
            legacyToCommon(Registries.BLOCK, forgeRl("crafting_tables"), Tags.Blocks.PLAYER_WORKSTATIONS_CRAFTING_TABLES),
            legacyToCommon(Registries.BLOCK, forgeRl("furnace"), Tags.Blocks.PLAYER_WORKSTATIONS_FURNACES),
            legacyToCommon(Registries.BLOCK, forgeRl("furnace"), Tags.Blocks.PLAYER_WORKSTATIONS_FURNACES),
            legacyToCommon(Registries.BLOCK, forgeRl("workbench"), Tags.Blocks.PLAYER_WORKSTATIONS_CRAFTING_TABLES),
            legacyToCommon(Registries.BLOCK, forgeRl("workbenches"), Tags.Blocks.PLAYER_WORKSTATIONS_CRAFTING_TABLES),

            legacyToCommon(Registries.ITEM, forgeRl("crafting_table"), Tags.Items.PLAYER_WORKSTATIONS_CRAFTING_TABLES),
            legacyToCommon(Registries.ITEM, forgeRl("crafting_tables"), Tags.Items.PLAYER_WORKSTATIONS_CRAFTING_TABLES),
            legacyToCommon(Registries.ITEM, forgeRl("furnace"), Tags.Items.PLAYER_WORKSTATIONS_FURNACES),
            legacyToCommon(Registries.ITEM, forgeRl("furnaces"), Tags.Items.PLAYER_WORKSTATIONS_FURNACES),
            legacyToCommon(Registries.ITEM, forgeRl("workbench"), Tags.Items.PLAYER_WORKSTATIONS_CRAFTING_TABLES),
            legacyToCommon(Registries.ITEM, forgeRl("workbenches"), Tags.Items.PLAYER_WORKSTATIONS_CRAFTING_TABLES),

            // Foods
            legacyToCommon(Registries.ITEM, forgeRl("food"), Tags.Items.FOODS),
            legacyToCommon(Registries.ITEM, forgeRl("foods"), Tags.Items.FOODS),
            legacyToCommon(Registries.ITEM, forgeRl("fruit"), Tags.Items.FOODS_FRUITS),
            legacyToCommon(Registries.ITEM, forgeRl("fruits"), Tags.Items.FOODS_FRUITS),
            legacyToCommon(Registries.ITEM, forgeRl("vegetable"), Tags.Items.FOODS_VEGETABLES),
            legacyToCommon(Registries.ITEM, forgeRl("vegetables"), Tags.Items.FOODS_VEGETABLES),
            legacyToCommon(Registries.ITEM, forgeRl("berry"), Tags.Items.FOODS_BERRIES),
            legacyToCommon(Registries.ITEM, forgeRl("berries"), Tags.Items.FOODS_BERRIES),
            legacyToCommon(Registries.ITEM, forgeRl("bread"), Tags.Items.FOODS_BREADS),
            legacyToCommon(Registries.ITEM, forgeRl("breads"), Tags.Items.FOODS_BREADS),
            legacyToCommon(Registries.ITEM, forgeRl("cookie"), Tags.Items.FOODS_COOKIES),
            legacyToCommon(Registries.ITEM, forgeRl("cookies"), Tags.Items.FOODS_COOKIES),
            legacyToCommon(Registries.ITEM, forgeRl("raw_meat"), Tags.Items.FOODS_RAW_MEATS),
            legacyToCommon(Registries.ITEM, forgeRl("raw_meats"), Tags.Items.FOODS_RAW_MEATS),
            legacyToCommon(Registries.ITEM, forgeRl("raw_fish"), Tags.Items.FOODS_RAW_FISHES),
            legacyToCommon(Registries.ITEM, forgeRl("raw_fishes"), Tags.Items.FOODS_RAW_FISHES),
            legacyToCommon(Registries.ITEM, forgeRl("cooked_meat"), Tags.Items.FOODS_COOKED_MEATS),
            legacyToCommon(Registries.ITEM, forgeRl("cooked_meats"), Tags.Items.FOODS_COOKED_MEATS),
            legacyToCommon(Registries.ITEM, forgeRl("cooked_fish"), Tags.Items.FOODS_COOKED_FISHES),
            legacyToCommon(Registries.ITEM, forgeRl("cooked_fishes"), Tags.Items.FOODS_COOKED_FISHES),
            legacyToCommon(Registries.ITEM, forgeRl("soup"), Tags.Items.FOODS_SOUPS),
            legacyToCommon(Registries.ITEM, forgeRl("soups"), Tags.Items.FOODS_SOUPS),
            legacyToCommon(Registries.ITEM, forgeRl("stew"), Tags.Items.FOODS_SOUPS),
            legacyToCommon(Registries.ITEM, forgeRl("stews"), Tags.Items.FOODS_SOUPS),
            legacyToCommon(Registries.ITEM, forgeRl("candy"), Tags.Items.FOODS_CANDIES),
            legacyToCommon(Registries.ITEM, forgeRl("candies"), Tags.Items.FOODS_CANDIES),

            // Fluids
            legacyToCommon(Registries.FLUID, forgeRl("water"), Tags.Fluids.WATER),
            legacyToCommon(Registries.FLUID, forgeRl("lava"), Tags.Fluids.LAVA),
            legacyToCommon(Registries.FLUID, forgeRl("honey"), Tags.Fluids.HONEY),
            legacyToCommon(Registries.FLUID, forgeRl("potion"), Tags.Fluids.POTION),

            // Biomes
            legacyToCommon(Registries.BIOME, forgeRl("is_overworld"), Tags.Biomes.IS_OVERWORLD),
            legacyToCommon(Registries.BIOME, forgeRl("is_nether"), Tags.Biomes.IS_NETHER),
            legacyToCommon(Registries.BIOME, forgeRl("is_nether_forest"), Tags.Biomes.IS_NETHER_FOREST),
            legacyToCommon(Registries.BIOME, forgeRl("is_end"), Tags.Biomes.IS_END),

            legacyToCommon(Registries.BIOME, forgeRl("is_forest"), Tags.Biomes.IS_FOREST),
            legacyToCommon(Registries.BIOME, forgeRl("is_jungle"), Tags.Biomes.IS_JUNGLE),
            legacyToCommon(Registries.BIOME, forgeRl("is_sandy"), Tags.Biomes.IS_SANDY),
            legacyToCommon(Registries.BIOME, forgeRl("is_savanna"), Tags.Biomes.IS_SAVANNA),
            legacyToCommon(Registries.BIOME, forgeRl("is_floral"), Tags.Biomes.IS_FLORAL),
            legacyToCommon(Registries.BIOME, forgeRl("is_snowy"), Tags.Biomes.IS_SNOWY),
            legacyToCommon(Registries.BIOME, forgeRl("is_icy"), Tags.Biomes.IS_ICY),
            legacyToCommon(Registries.BIOME, forgeRl("is_ocean"), Tags.Biomes.IS_OCEAN)
            //endregion
    );

    /**
     * Creates a mapping from a legacy tag to a common convention equivalent.
     * <p>
     *     Example: {@code legacyToCommon(Registries.BLOCK, forgeRl("cobblestone"), Tags.Blocks.COBBLESTONES)} for {@code forge:cobblestone} -> {@code c:cobblestones}
     * </p>
     * @param registryKey Example: {@link net.minecraft.core.registries.Registries.BLOCK}
     * @param legacyTagResourceLocation Example: {@code forgeRl("cobblestone")}
     * @param replacementTag Example: {@link Tags.Blocks#COBBLESTONES}
     * @return A map entry with the legacy tag's TagKey and the suggested replacement tag ID.
     */
    private static <T, R> Map.Entry<TagKey<T>, String> legacyToCommon(ResourceKey<Registry<T>> registryKey,
                                                                      ResourceLocation legacyTagResourceLocation,
                                                                      TagKey<R> replacementTag) {
        return Map.entry(TagKey.create(registryKey, legacyTagResourceLocation), replacementTag.toString());
    }

    /**
     * Same as {@link #legacyToCommon(ResourceKey, ResourceLocation, TagKey)} but for multiple replacement tags.
     * This is useful when a single legacy tag is intended to be replaced by combining multiple common convention tags.
     */
    private static <T> Map.Entry<TagKey<T>, String> legacyToCommon(ResourceKey<Registry<T>> registryKey,
                                                                   ResourceLocation legacyTagRessourceLocation,
                                                                   List<TagKey<?>> replacementTags) {
        return Map.entry(
                TagKey.create(registryKey, legacyTagRessourceLocation),
                replacementTags.stream().map(TagKey::toString).collect(Collectors.joining(" and "))
        );
    }

    private static ResourceLocation forgeRl(String path) {
        return ResourceLocation.fromNamespaceAndPath("forge", path);
    }
}
