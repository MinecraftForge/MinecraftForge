/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.function.Supplier;

public class Tags
{
    public static void init ()
    {
        Blocks.init();
        Items.init();
        Fluids.init();
    }

    public static class Blocks
    {
        private static void init(){}

        public static final TagKey<Block> BARRELS = tag("barrels");
        public static final TagKey<Block> BARRELS_WOODEN = tag("barrels/wooden");
        public static final TagKey<Block> CHESTS = tag("chests");
        public static final TagKey<Block> CHESTS_ENDER = tag("chests/ender");
        public static final TagKey<Block> CHESTS_TRAPPED = tag("chests/trapped");
        public static final TagKey<Block> CHESTS_WOODEN = tag("chests/wooden");
        public static final TagKey<Block> COBBLESTONE = tag("cobblestone");
        public static final TagKey<Block> COBBLESTONE_NORMAL = tag("cobblestone/normal");
        public static final TagKey<Block> COBBLESTONE_INFESTED = tag("cobblestone/infested");
        public static final TagKey<Block> COBBLESTONE_MOSSY = tag("cobblestone/mossy");
        public static final TagKey<Block> COBBLESTONE_DEEPSLATE = tag("cobblestone/deepslate");
        public static final TagKey<Block> END_STONES = tag("end_stones");
        public static final TagKey<Block> ENDERMAN_PLACE_ON_BLACKLIST = tag("enderman_place_on_blacklist");
        public static final TagKey<Block> FENCE_GATES = tag("fence_gates");
        public static final TagKey<Block> FENCE_GATES_WOODEN = tag("fence_gates/wooden");
        public static final TagKey<Block> FENCES = tag("fences");
        public static final TagKey<Block> FENCES_NETHER_BRICK = tag("fences/nether_brick");
        public static final TagKey<Block> FENCES_WOODEN = tag("fences/wooden");

        public static final TagKey<Block> GLASS = tag("glass");
        public static final TagKey<Block> GLASS_BLACK = tag("glass/black");
        public static final TagKey<Block> GLASS_BLUE = tag("glass/blue");
        public static final TagKey<Block> GLASS_BROWN = tag("glass/brown");
        public static final TagKey<Block> GLASS_COLORLESS = tag("glass/colorless");
        public static final TagKey<Block> GLASS_CYAN = tag("glass/cyan");
        public static final TagKey<Block> GLASS_GRAY = tag("glass/gray");
        public static final TagKey<Block> GLASS_GREEN = tag("glass/green");
        public static final TagKey<Block> GLASS_LIGHT_BLUE = tag("glass/light_blue");
        public static final TagKey<Block> GLASS_LIGHT_GRAY = tag("glass/light_gray");
        public static final TagKey<Block> GLASS_LIME = tag("glass/lime");
        public static final TagKey<Block> GLASS_MAGENTA = tag("glass/magenta");
        public static final TagKey<Block> GLASS_ORANGE = tag("glass/orange");
        public static final TagKey<Block> GLASS_PINK = tag("glass/pink");
        public static final TagKey<Block> GLASS_PURPLE = tag("glass/purple");
        public static final TagKey<Block> GLASS_RED = tag("glass/red");
        /**
         * Glass which is made from sand and only minor additional ingredients like dyes
         */
        public static final TagKey<Block> GLASS_SILICA = tag("glass/silica");
        public static final TagKey<Block> GLASS_TINTED = tag("glass/tinted");
        public static final TagKey<Block> GLASS_WHITE = tag("glass/white");
        public static final TagKey<Block> GLASS_YELLOW = tag("glass/yellow");

        public static final TagKey<Block> GLASS_PANES = tag("glass_panes");
        public static final TagKey<Block> GLASS_PANES_BLACK = tag("glass_panes/black");
        public static final TagKey<Block> GLASS_PANES_BLUE = tag("glass_panes/blue");
        public static final TagKey<Block> GLASS_PANES_BROWN = tag("glass_panes/brown");
        public static final TagKey<Block> GLASS_PANES_COLORLESS = tag("glass_panes/colorless");
        public static final TagKey<Block> GLASS_PANES_CYAN = tag("glass_panes/cyan");
        public static final TagKey<Block> GLASS_PANES_GRAY = tag("glass_panes/gray");
        public static final TagKey<Block> GLASS_PANES_GREEN = tag("glass_panes/green");
        public static final TagKey<Block> GLASS_PANES_LIGHT_BLUE = tag("glass_panes/light_blue");
        public static final TagKey<Block> GLASS_PANES_LIGHT_GRAY = tag("glass_panes/light_gray");
        public static final TagKey<Block> GLASS_PANES_LIME = tag("glass_panes/lime");
        public static final TagKey<Block> GLASS_PANES_MAGENTA = tag("glass_panes/magenta");
        public static final TagKey<Block> GLASS_PANES_ORANGE = tag("glass_panes/orange");
        public static final TagKey<Block> GLASS_PANES_PINK = tag("glass_panes/pink");
        public static final TagKey<Block> GLASS_PANES_PURPLE = tag("glass_panes/purple");
        public static final TagKey<Block> GLASS_PANES_RED = tag("glass_panes/red");
        public static final TagKey<Block> GLASS_PANES_WHITE = tag("glass_panes/white");
        public static final TagKey<Block> GLASS_PANES_YELLOW = tag("glass_panes/yellow");

        public static final TagKey<Block> GRAVEL = tag("gravel");
        public static final TagKey<Block> NETHERRACK = tag("netherrack");
        public static final TagKey<Block> OBSIDIAN = tag("obsidian");
        /**
         * Blocks which are often replaced by deepslate ores, i.e. the ores in the tag {@link #ORES_IN_GROUND_DEEPSLATE}, during world generation
         */
        public static final TagKey<Block> ORE_BEARING_GROUND_DEEPSLATE = tag("ore_bearing_ground/deepslate");
        /**
         * Blocks which are often replaced by netherrack ores, i.e. the ores in the tag {@link #ORES_IN_GROUND_NETHERRACK}, during world generation
         */
        public static final TagKey<Block> ORE_BEARING_GROUND_NETHERRACK = tag("ore_bearing_ground/netherrack");
        /**
         * Blocks which are often replaced by stone ores, i.e. the ores in the tag {@link #ORES_IN_GROUND_STONE}, during world generation
         */
        public static final TagKey<Block> ORE_BEARING_GROUND_STONE = tag("ore_bearing_ground/stone");
        /**
         * Ores which on average result in more than one resource worth of materials
         */
        public static final TagKey<Block> ORE_RATES_DENSE = tag("ore_rates/dense");
        /**
         * Ores which on average result in one resource worth of materials
         */
        public static final TagKey<Block> ORE_RATES_SINGULAR = tag("ore_rates/singular");
        /**
         * Ores which on average result in less than one resource worth of materials
         */
        public static final TagKey<Block> ORE_RATES_SPARSE = tag("ore_rates/sparse");
        public static final TagKey<Block> ORES = tag("ores");
        public static final TagKey<Block> ORES_COAL = tag("ores/coal");
        public static final TagKey<Block> ORES_COPPER = tag("ores/copper");
        public static final TagKey<Block> ORES_DIAMOND = tag("ores/diamond");
        public static final TagKey<Block> ORES_EMERALD = tag("ores/emerald");
        public static final TagKey<Block> ORES_GOLD = tag("ores/gold");
        public static final TagKey<Block> ORES_IRON = tag("ores/iron");
        public static final TagKey<Block> ORES_LAPIS = tag("ores/lapis");
        public static final TagKey<Block> ORES_NETHERITE_SCRAP = tag("ores/netherite_scrap");
        public static final TagKey<Block> ORES_QUARTZ = tag("ores/quartz");
        public static final TagKey<Block> ORES_REDSTONE = tag("ores/redstone");
        /**
         * Ores in deepslate (or in equivalent blocks in the tag {@link #ORE_BEARING_GROUND_DEEPSLATE}) which could logically use deepslate as recipe input or output
         */
        public static final TagKey<Block> ORES_IN_GROUND_DEEPSLATE = tag("ores_in_ground/deepslate");
        /**
         * Ores in netherrack (or in equivalent blocks in the tag {@link #ORE_BEARING_GROUND_NETHERRACK}) which could logically use netherrack as recipe input or output
         */
        public static final TagKey<Block> ORES_IN_GROUND_NETHERRACK = tag("ores_in_ground/netherrack");
        /**
         * Ores in stone (or in equivalent blocks in the tag {@link #ORE_BEARING_GROUND_STONE}) which could logically use stone as recipe input or output
         */
        public static final TagKey<Block> ORES_IN_GROUND_STONE = tag("ores_in_ground/stone");

        public static final TagKey<Block> SAND = tag("sand");
        public static final TagKey<Block> SAND_COLORLESS = tag("sand/colorless");
        public static final TagKey<Block> SAND_RED = tag("sand/red");

        public static final TagKey<Block> SANDSTONE = tag("sandstone");
        public static final TagKey<Block> STAINED_GLASS = tag("stained_glass");
        public static final TagKey<Block> STAINED_GLASS_PANES = tag("stained_glass_panes");
        public static final TagKey<Block> STONE = tag("stone");
        public static final TagKey<Block> STORAGE_BLOCKS = tag("storage_blocks");
        public static final TagKey<Block> STORAGE_BLOCKS_AMETHYST = tag("storage_blocks/amethyst");
        public static final TagKey<Block> STORAGE_BLOCKS_COAL = tag("storage_blocks/coal");
        public static final TagKey<Block> STORAGE_BLOCKS_COPPER = tag("storage_blocks/copper");
        public static final TagKey<Block> STORAGE_BLOCKS_DIAMOND = tag("storage_blocks/diamond");
        public static final TagKey<Block> STORAGE_BLOCKS_EMERALD = tag("storage_blocks/emerald");
        public static final TagKey<Block> STORAGE_BLOCKS_GOLD = tag("storage_blocks/gold");
        public static final TagKey<Block> STORAGE_BLOCKS_IRON = tag("storage_blocks/iron");
        public static final TagKey<Block> STORAGE_BLOCKS_LAPIS = tag("storage_blocks/lapis");
        public static final TagKey<Block> STORAGE_BLOCKS_NETHERITE = tag("storage_blocks/netherite");
        public static final TagKey<Block> STORAGE_BLOCKS_QUARTZ = tag("storage_blocks/quartz");
        public static final TagKey<Block> STORAGE_BLOCKS_RAW_COPPER = tag("storage_blocks/raw_copper");
        public static final TagKey<Block> STORAGE_BLOCKS_RAW_GOLD = tag("storage_blocks/raw_gold");
        public static final TagKey<Block> STORAGE_BLOCKS_RAW_IRON = tag("storage_blocks/raw_iron");
        public static final TagKey<Block> STORAGE_BLOCKS_REDSTONE = tag("storage_blocks/redstone");

        public static final TagKey<Block> NEEDS_WOOD_TOOL = tag("needs_wood_tool");
        public static final TagKey<Block> NEEDS_GOLD_TOOL = tag("needs_gold_tool");
        public static final TagKey<Block> NEEDS_NETHERITE_TOOL = tag("needs_netherite_tool");

        private static TagKey<Block> tag(String name)
        {
            return BlockTags.create(new ResourceLocation("forge", name));
        }
    }

    public static class Items
    {
        private static void init(){}

        public static final TagKey<Item> BARRELS = tag("barrels");
        public static final TagKey<Item> BARRELS_WOODEN = tag("barrels/wooden");
        public static final TagKey<Item> BONES = tag("bones");
        public static final TagKey<Item> BOOKSHELVES = tag("bookshelves");
        public static final TagKey<Item> CHESTS = tag("chests");
        public static final TagKey<Item> CHESTS_ENDER = tag("chests/ender");
        public static final TagKey<Item> CHESTS_TRAPPED = tag("chests/trapped");
        public static final TagKey<Item> CHESTS_WOODEN = tag("chests/wooden");
        public static final TagKey<Item> COBBLESTONE = tag("cobblestone");
        public static final TagKey<Item> COBBLESTONE_NORMAL = tag("cobblestone/normal");
        public static final TagKey<Item> COBBLESTONE_INFESTED = tag("cobblestone/infested");
        public static final TagKey<Item> COBBLESTONE_MOSSY = tag("cobblestone/mossy");
        public static final TagKey<Item> COBBLESTONE_DEEPSLATE = tag("cobblestone/deepslate");
        public static final TagKey<Item> CROPS = tag("crops");
        public static final TagKey<Item> CROPS_BEETROOT = tag("crops/beetroot");
        public static final TagKey<Item> CROPS_CARROT = tag("crops/carrot");
        public static final TagKey<Item> CROPS_NETHER_WART = tag("crops/nether_wart");
        public static final TagKey<Item> CROPS_POTATO = tag("crops/potato");
        public static final TagKey<Item> CROPS_WHEAT = tag("crops/wheat");
        public static final TagKey<Item> DUSTS = tag("dusts");
        public static final TagKey<Item> DUSTS_PRISMARINE = tag("dusts/prismarine");
        public static final TagKey<Item> DUSTS_REDSTONE = tag("dusts/redstone");
        public static final TagKey<Item> DUSTS_GLOWSTONE = tag("dusts/glowstone");

        public static final TagKey<Item> DYES = tag("dyes");
        public static final TagKey<Item> DYES_BLACK = DyeColor.BLACK.getTag();
        public static final TagKey<Item> DYES_RED = DyeColor.RED.getTag();
        public static final TagKey<Item> DYES_GREEN = DyeColor.GREEN.getTag();
        public static final TagKey<Item> DYES_BROWN = DyeColor.BROWN.getTag();
        public static final TagKey<Item> DYES_BLUE = DyeColor.BLUE.getTag();
        public static final TagKey<Item> DYES_PURPLE = DyeColor.PURPLE.getTag();
        public static final TagKey<Item> DYES_CYAN = DyeColor.CYAN.getTag();
        public static final TagKey<Item> DYES_LIGHT_GRAY = DyeColor.LIGHT_GRAY.getTag();
        public static final TagKey<Item> DYES_GRAY = DyeColor.GRAY.getTag();
        public static final TagKey<Item> DYES_PINK = DyeColor.PINK.getTag();
        public static final TagKey<Item> DYES_LIME = DyeColor.LIME.getTag();
        public static final TagKey<Item> DYES_YELLOW = DyeColor.YELLOW.getTag();
        public static final TagKey<Item> DYES_LIGHT_BLUE = DyeColor.LIGHT_BLUE.getTag();
        public static final TagKey<Item> DYES_MAGENTA = DyeColor.MAGENTA.getTag();
        public static final TagKey<Item> DYES_ORANGE = DyeColor.ORANGE.getTag();
        public static final TagKey<Item> DYES_WHITE = DyeColor.WHITE.getTag();

        public static final TagKey<Item> EGGS = tag("eggs");
        /**
         * This tag defaults to {@link net.minecraft.world.item.Items#LAPIS_LAZULI} when not present in any datapacks, including forge client on vanilla server
         */
        public static final TagKey<Item> ENCHANTING_FUELS = tag("enchanting_fuels");
        public static final TagKey<Item> END_STONES = tag("end_stones");
        public static final TagKey<Item> ENDER_PEARLS = tag("ender_pearls");
        public static final TagKey<Item> FEATHERS = tag("feathers");
        public static final TagKey<Item> FENCE_GATES = tag("fence_gates");
        public static final TagKey<Item> FENCE_GATES_WOODEN = tag("fence_gates/wooden");
        public static final TagKey<Item> FENCES = tag("fences");
        public static final TagKey<Item> FENCES_NETHER_BRICK = tag("fences/nether_brick");
        public static final TagKey<Item> FENCES_WOODEN = tag("fences/wooden");
        public static final TagKey<Item> GEMS = tag("gems");
        public static final TagKey<Item> GEMS_DIAMOND = tag("gems/diamond");
        public static final TagKey<Item> GEMS_EMERALD = tag("gems/emerald");
        public static final TagKey<Item> GEMS_AMETHYST = tag("gems/amethyst");
        public static final TagKey<Item> GEMS_LAPIS = tag("gems/lapis");
        public static final TagKey<Item> GEMS_PRISMARINE = tag("gems/prismarine");
        public static final TagKey<Item> GEMS_QUARTZ = tag("gems/quartz");

        public static final TagKey<Item> GLASS = tag("glass");
        public static final TagKey<Item> GLASS_BLACK = tag("glass/black");
        public static final TagKey<Item> GLASS_BLUE = tag("glass/blue");
        public static final TagKey<Item> GLASS_BROWN = tag("glass/brown");
        public static final TagKey<Item> GLASS_COLORLESS = tag("glass/colorless");
        public static final TagKey<Item> GLASS_CYAN = tag("glass/cyan");
        public static final TagKey<Item> GLASS_GRAY = tag("glass/gray");
        public static final TagKey<Item> GLASS_GREEN = tag("glass/green");
        public static final TagKey<Item> GLASS_LIGHT_BLUE = tag("glass/light_blue");
        public static final TagKey<Item> GLASS_LIGHT_GRAY = tag("glass/light_gray");
        public static final TagKey<Item> GLASS_LIME = tag("glass/lime");
        public static final TagKey<Item> GLASS_MAGENTA = tag("glass/magenta");
        public static final TagKey<Item> GLASS_ORANGE = tag("glass/orange");
        public static final TagKey<Item> GLASS_PINK = tag("glass/pink");
        public static final TagKey<Item> GLASS_PURPLE = tag("glass/purple");
        public static final TagKey<Item> GLASS_RED = tag("glass/red");
        /**
         * Glass which is made from sand and only minor additional ingredients like dyes
         */
        public static final TagKey<Item> GLASS_SILICA = tag("glass/silica");
        public static final TagKey<Item> GLASS_TINTED = tag("glass/tinted");
        public static final TagKey<Item> GLASS_WHITE = tag("glass/white");
        public static final TagKey<Item> GLASS_YELLOW = tag("glass/yellow");

        public static final TagKey<Item> GLASS_PANES = tag("glass_panes");
        public static final TagKey<Item> GLASS_PANES_BLACK = tag("glass_panes/black");
        public static final TagKey<Item> GLASS_PANES_BLUE = tag("glass_panes/blue");
        public static final TagKey<Item> GLASS_PANES_BROWN = tag("glass_panes/brown");
        public static final TagKey<Item> GLASS_PANES_COLORLESS = tag("glass_panes/colorless");
        public static final TagKey<Item> GLASS_PANES_CYAN = tag("glass_panes/cyan");
        public static final TagKey<Item> GLASS_PANES_GRAY = tag("glass_panes/gray");
        public static final TagKey<Item> GLASS_PANES_GREEN = tag("glass_panes/green");
        public static final TagKey<Item> GLASS_PANES_LIGHT_BLUE = tag("glass_panes/light_blue");
        public static final TagKey<Item> GLASS_PANES_LIGHT_GRAY = tag("glass_panes/light_gray");
        public static final TagKey<Item> GLASS_PANES_LIME = tag("glass_panes/lime");
        public static final TagKey<Item> GLASS_PANES_MAGENTA = tag("glass_panes/magenta");
        public static final TagKey<Item> GLASS_PANES_ORANGE = tag("glass_panes/orange");
        public static final TagKey<Item> GLASS_PANES_PINK = tag("glass_panes/pink");
        public static final TagKey<Item> GLASS_PANES_PURPLE = tag("glass_panes/purple");
        public static final TagKey<Item> GLASS_PANES_RED = tag("glass_panes/red");
        public static final TagKey<Item> GLASS_PANES_WHITE = tag("glass_panes/white");
        public static final TagKey<Item> GLASS_PANES_YELLOW = tag("glass_panes/yellow");

        public static final TagKey<Item> GRAVEL = tag("gravel");
        public static final TagKey<Item> GUNPOWDER = tag("gunpowder");
        public static final TagKey<Item> HEADS = tag("heads");
        public static final TagKey<Item> INGOTS = tag("ingots");
        public static final TagKey<Item> INGOTS_BRICK = tag("ingots/brick");
        public static final TagKey<Item> INGOTS_COPPER = tag("ingots/copper");
        public static final TagKey<Item> INGOTS_GOLD = tag("ingots/gold");
        public static final TagKey<Item> INGOTS_IRON = tag("ingots/iron");
        public static final TagKey<Item> INGOTS_NETHERITE = tag("ingots/netherite");
        public static final TagKey<Item> INGOTS_NETHER_BRICK = tag("ingots/nether_brick");
        public static final TagKey<Item> LEATHER = tag("leather");
        public static final TagKey<Item> MUSHROOMS = tag("mushrooms");
        public static final TagKey<Item> NETHER_STARS = tag("nether_stars");
        public static final TagKey<Item> NETHERRACK = tag("netherrack");
        public static final TagKey<Item> NUGGETS = tag("nuggets");
        public static final TagKey<Item> NUGGETS_GOLD = tag("nuggets/gold");
        public static final TagKey<Item> NUGGETS_IRON = tag("nuggets/iron");
        public static final TagKey<Item> OBSIDIAN = tag("obsidian");
        /**
         * Blocks which are often replaced by deepslate ores, i.e. the ores in the tag {@link #ORES_IN_GROUND_DEEPSLATE}, during world generation
         */
        public static final TagKey<Item> ORE_BEARING_GROUND_DEEPSLATE = tag("ore_bearing_ground/deepslate");
        /**
         * Blocks which are often replaced by netherrack ores, i.e. the ores in the tag {@link #ORES_IN_GROUND_NETHERRACK}, during world generation
         */
        public static final TagKey<Item> ORE_BEARING_GROUND_NETHERRACK = tag("ore_bearing_ground/netherrack");
        /**
         * Blocks which are often replaced by stone ores, i.e. the ores in the tag {@link #ORES_IN_GROUND_STONE}, during world generation
         */
        public static final TagKey<Item> ORE_BEARING_GROUND_STONE = tag("ore_bearing_ground/stone");
        /**
         * Ores which on average result in more than one resource worth of materials
         */
        public static final TagKey<Item> ORE_RATES_DENSE = tag("ore_rates/dense");
        /**
         * Ores which on average result in one resource worth of materials
         */
        public static final TagKey<Item> ORE_RATES_SINGULAR = tag("ore_rates/singular");
        /**
         * Ores which on average result in less than one resource worth of materials
         */
        public static final TagKey<Item> ORE_RATES_SPARSE = tag("ore_rates/sparse");
        public static final TagKey<Item> ORES = tag("ores");
        public static final TagKey<Item> ORES_COAL = tag("ores/coal");
        public static final TagKey<Item> ORES_COPPER = tag("ores/copper");
        public static final TagKey<Item> ORES_DIAMOND = tag("ores/diamond");
        public static final TagKey<Item> ORES_EMERALD = tag("ores/emerald");
        public static final TagKey<Item> ORES_GOLD = tag("ores/gold");
        public static final TagKey<Item> ORES_IRON = tag("ores/iron");
        public static final TagKey<Item> ORES_LAPIS = tag("ores/lapis");
        public static final TagKey<Item> ORES_NETHERITE_SCRAP = tag("ores/netherite_scrap");
        public static final TagKey<Item> ORES_QUARTZ = tag("ores/quartz");
        public static final TagKey<Item> ORES_REDSTONE = tag("ores/redstone");
        /**
         * Ores in deepslate (or in equivalent blocks in the tag {@link #ORE_BEARING_GROUND_DEEPSLATE}) which could logically use deepslate as recipe input or output
         */
        public static final TagKey<Item> ORES_IN_GROUND_DEEPSLATE = tag("ores_in_ground/deepslate");
        /**
         * Ores in netherrack (or in equivalent blocks in the tag {@link #ORE_BEARING_GROUND_NETHERRACK}) which could logically use netherrack as recipe input or output
         */
        public static final TagKey<Item> ORES_IN_GROUND_NETHERRACK = tag("ores_in_ground/netherrack");
        /**
         * Ores in stone (or in equivalent blocks in the tag {@link #ORE_BEARING_GROUND_STONE}) which could logically use stone as recipe input or output
         */
        public static final TagKey<Item> ORES_IN_GROUND_STONE = tag("ores_in_ground/stone");
        public static final TagKey<Item> RAW_MATERIALS = tag("raw_materials");
        public static final TagKey<Item> RAW_MATERIALS_COPPER = tag("raw_materials/copper");
        public static final TagKey<Item> RAW_MATERIALS_GOLD = tag("raw_materials/gold");
        public static final TagKey<Item> RAW_MATERIALS_IRON = tag("raw_materials/iron");
        public static final TagKey<Item> RODS = tag("rods");
        public static final TagKey<Item> RODS_BLAZE = tag("rods/blaze");
        public static final TagKey<Item> RODS_WOODEN = tag("rods/wooden");

        public static final TagKey<Item> SAND = tag("sand");
        public static final TagKey<Item> SAND_COLORLESS = tag("sand/colorless");
        public static final TagKey<Item> SAND_RED = tag("sand/red");

        public static final TagKey<Item> SANDSTONE = tag("sandstone");
        public static final TagKey<Item> SEEDS = tag("seeds");
        public static final TagKey<Item> SEEDS_BEETROOT = tag("seeds/beetroot");
        public static final TagKey<Item> SEEDS_MELON = tag("seeds/melon");
        public static final TagKey<Item> SEEDS_PUMPKIN = tag("seeds/pumpkin");
        public static final TagKey<Item> SEEDS_WHEAT = tag("seeds/wheat");
        public static final TagKey<Item> SHEARS = tag("shears");
        public static final TagKey<Item> SLIMEBALLS = tag("slimeballs");
        public static final TagKey<Item> STAINED_GLASS = tag("stained_glass");
        public static final TagKey<Item> STAINED_GLASS_PANES = tag("stained_glass_panes");
        public static final TagKey<Item> STONE = tag("stone");
        public static final TagKey<Item> STORAGE_BLOCKS = tag("storage_blocks");
        public static final TagKey<Item> STORAGE_BLOCKS_AMETHYST = tag("storage_blocks/amethyst");
        public static final TagKey<Item> STORAGE_BLOCKS_COAL = tag("storage_blocks/coal");
        public static final TagKey<Item> STORAGE_BLOCKS_COPPER = tag("storage_blocks/copper");
        public static final TagKey<Item> STORAGE_BLOCKS_DIAMOND = tag("storage_blocks/diamond");
        public static final TagKey<Item> STORAGE_BLOCKS_EMERALD = tag("storage_blocks/emerald");
        public static final TagKey<Item> STORAGE_BLOCKS_GOLD = tag("storage_blocks/gold");
        public static final TagKey<Item> STORAGE_BLOCKS_IRON = tag("storage_blocks/iron");
        public static final TagKey<Item> STORAGE_BLOCKS_LAPIS = tag("storage_blocks/lapis");
        public static final TagKey<Item> STORAGE_BLOCKS_NETHERITE = tag("storage_blocks/netherite");
        public static final TagKey<Item> STORAGE_BLOCKS_QUARTZ = tag("storage_blocks/quartz");
        public static final TagKey<Item> STORAGE_BLOCKS_RAW_COPPER = tag("storage_blocks/raw_copper");
        public static final TagKey<Item> STORAGE_BLOCKS_RAW_GOLD = tag("storage_blocks/raw_gold");
        public static final TagKey<Item> STORAGE_BLOCKS_RAW_IRON = tag("storage_blocks/raw_iron");
        public static final TagKey<Item> STORAGE_BLOCKS_REDSTONE = tag("storage_blocks/redstone");
        public static final TagKey<Item> STRING                  = tag("string");

        private static TagKey<Item> tag(String name)
        {
            return ItemTags.create(new ResourceLocation("forge", name));
        }
    }

    public static class Fluids
    {
        private static void init() {}

        public static final TagKey<Fluid> MILK = tag("milk");

        private static TagKey<Fluid> tag(String name)
        {
            return FluidTags.create(new ResourceLocation("forge", name));
        }
    }
}
