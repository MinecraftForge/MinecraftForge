/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockItemTagId;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;

public class Tags {
    public static void init() {
        BlockItems.init();
        Blocks.init();
        EntityTypes.init();
        Items.init();
        Fluids.init();
        Enchantments.init();
        Biomes.init();
        Structures.init();
    }

    public static class BlockItems {
        private static void init() {}
        //region forge specific tags
        public static final BlockItemTagId STORAGE_BLOCKS_AMETHYST = forgeTag("storage_blocks/amethyst");
        public static final BlockItemTagId STORAGE_BLOCKS_QUARTZ = forgeTag("storage_blocks/quartz");
        //endregion


        //region `c` tags for common conventions
        // Note: Other loaders have additional `c` tags that are exclusive to their loader.
        //       Forge only adopts `c` tags that are common across all loaders.
        public static final BlockItemTagId BARRELS = cTag("barrels");
        public static final BlockItemTagId BARRELS_WOODEN = cTag("barrels/wooden");
        /**
         * Equivalent to the "minecraft:bars" block tag.
         */
        public static final BlockItemTagId BARS = cTag("bars");
        public static final BlockItemTagId BARS_COPPER = cTag("bars/copper");
        public static final BlockItemTagId BARS_IRON = cTag("bars/iron");
        public static final BlockItemTagId BOOKSHELVES = cTag("bookshelves");
        /**
         * For blocks that are similar to amethyst where their budding block produces buds and cluster blocks
         */
        public static final BlockItemTagId BUDDING_BLOCKS = cTag("budding_blocks");
        /**
         * For blocks that are similar to amethyst where they have buddings forming from budding blocks
         */
        public static final BlockItemTagId BUDS = cTag("buds");
        public static final BlockItemTagId CHAINS = cTag("chains");
        public static final BlockItemTagId CHESTS = cTag("chests");
        public static final BlockItemTagId CHESTS_ENDER = cTag("chests/ender");
        public static final BlockItemTagId CHESTS_TRAPPED = cTag("chests/trapped");
        public static final BlockItemTagId CHESTS_WOODEN = cTag("chests/wooden");
        /**
         * For blocks that are similar to amethyst where they have clusters forming from budding blocks
         */
        public static final BlockItemTagId CLUSTERS = cTag("clusters");
        public static final BlockItemTagId COBBLESTONES = cTag("cobblestones");
        public static final BlockItemTagId COBBLESTONES_DEEPSLATE = cTag("cobblestones/deepslate");
        public static final BlockItemTagId COBBLESTONES_INFESTED = cTag("cobblestones/infested");
        public static final BlockItemTagId COBBLESTONES_MOSSY = cTag("cobblestones/mossy");
        public static final BlockItemTagId COBBLESTONES_NORMAL = cTag("cobblestones/normal");
        public static final BlockItemTagId CONCRETES = cTag("concretes");

        public static final BlockItemTagId END_STONES = cTag("end_stones");

        public static final BlockItemTagId FENCE_GATES = cTag("fence_gates");
        public static final BlockItemTagId FENCE_GATES_WOODEN = cTag("fence_gates/wooden");

        public static final BlockItemTagId FENCES = cTag("fences");
        public static final BlockItemTagId FENCES_NETHER_BRICK = cTag("fences/nether_brick");
        public static final BlockItemTagId FENCES_WOODEN = cTag("fences/wooden");

        /**
         * Contains living ground-based flowers that are 1 block tall such as Dandelions or Poppy.
         * Equivalent to the {@code minecraft:small_flowers} block tag.
         * This is NOT aliased with {@link BlockTags#SMALL_FLOWERS} because the vanilla tag is used to make the block weak to swords.
         */
        public static final BlockItemTagId FLOWERS_SMALL = cTag("flowers/small");
        /**
         * Contains living ground-based flowers that are 2 block tall such as Rose Bush or Peony.
         * Equivalent to the {@code minecraft:tall_flowers} block tag in past Minecraft versions.
         */
        public static final BlockItemTagId FLOWERS_TALL = cTag("flowers/tall");
        /**
         * Contains any living plant block that contains flowers or is a flower itself.
         * Equivalent to the {@code minecraft:flowers} block tag.
         * Aliased with {@link BlockTags#FLOWERS}.
         */
        public static final BlockItemTagId FLOWERS = cTag("flowers");

        public static final BlockItemTagId GRAVELS = cTag("gravels");

        public static final BlockItemTagId GLASS_BLOCKS = cTag("glass_blocks");
        public static final BlockItemTagId GLASS_BLOCKS_COLORLESS = cTag("glass_blocks/colorless");
        /**
         * Glass which is made from cheap resources like sand and only minor additional ingredients like dyes
         */
        public static final BlockItemTagId GLASS_BLOCKS_CHEAP = cTag("glass_blocks/cheap");
        public static final BlockItemTagId GLASS_BLOCKS_TINTED = cTag("glass_blocks/tinted");

        public static final BlockItemTagId GLASS_PANES = cTag("glass_panes");
        public static final BlockItemTagId GLASS_PANES_COLORLESS = cTag("glass_panes/colorless");
        public static final BlockItemTagId GLAZED_TERRACOTTAS = cTag("glazed_terracottas");

        public static final BlockItemTagId NATURAL_LOGS = cTag("natural_logs");
        public static final BlockItemTagId NATURAL_LOGS_NETHER = cTag("natural_logs/nether");
        public static final BlockItemTagId NATURAL_LOGS_OVERWORLD = cTag("natural_logs/overworld");

        public static final BlockItemTagId NATURAL_WOODS = cTag("natural_woods");

        public static final BlockItemTagId NETHERRACKS = cTag("netherracks");

        public static final BlockItemTagId OBSIDIANS = cTag("obsidians");
        /**
         * For common obsidian that has no special quirks or behaviours - ideal for recipe use.
         * Crying Obsidian, for example, is a light block and harder to obtain. So it gets its own tag instead of being under normal tag.
         */
        public static final BlockItemTagId OBSIDIANS_NORMAL = cTag("obsidians/normal");
        public static final BlockItemTagId OBSIDIANS_CRYING = cTag("obsidians/crying");
        /**
         * Blocks which are often replaced by deepslate ores, i.e. the ores in the tag {@link #ORES_IN_GROUND_DEEPSLATE}, during world generation
         */
        public static final BlockItemTagId ORE_BEARING_GROUND_DEEPSLATE = cTag("ore_bearing_ground/deepslate");
        /**
         * Blocks which are often replaced by netherrack ores, i.e. the ores in the tag {@link #ORES_IN_GROUND_NETHERRACK}, during world generation
         */
        public static final BlockItemTagId ORE_BEARING_GROUND_NETHERRACK = cTag("ore_bearing_ground/netherrack");
        /**
         * Blocks which are often replaced by stone ores, i.e. the ores in the tag {@link #ORES_IN_GROUND_STONE}, during world generation
         */
        public static final BlockItemTagId ORE_BEARING_GROUND_STONE = cTag("ore_bearing_ground/stone");
        /**
         * Ores which on average result in more than one resource worth of materials
         */
        public static final BlockItemTagId ORE_RATES_DENSE = cTag("ore_rates/dense");
        /**
         * Ores which on average result in one resource worth of materials
         */
        public static final BlockItemTagId ORE_RATES_SINGULAR = cTag("ore_rates/singular");
        /**
         * Ores which on average result in less than one resource worth of materials
         */
        public static final BlockItemTagId ORE_RATES_SPARSE = cTag("ore_rates/sparse");
        public static final BlockItemTagId ORES = cTag("ores");
        public static final BlockItemTagId ORES_NETHERITE_SCRAP = cTag("ores/netherite_scrap");
        public static final BlockItemTagId ORES_QUARTZ = cTag("ores/quartz");
        public static final BlockItemTagId ORES_COAL = cTag("ores/coal");
        public static final BlockItemTagId ORES_COPPER = cTag("ores/copper");
        public static final BlockItemTagId ORES_DIAMOND = cTag("ores/diamond");
        public static final BlockItemTagId ORES_EMERALD = cTag("ores/emerald");
        public static final BlockItemTagId ORES_GOLD = cTag("ores/gold");
        public static final BlockItemTagId ORES_IRON = cTag("ores/iron");
        public static final BlockItemTagId ORES_LAPIS = cTag("ores/lapis");
        public static final BlockItemTagId ORES_REDSTONE = cTag("ores/redstone");
        /**
         * Ores in deepslate (or in equivalent blocks in the tag {@link #ORE_BEARING_GROUND_DEEPSLATE}) which could logically use deepslate as recipe input or output
         */
        public static final BlockItemTagId ORES_IN_GROUND_DEEPSLATE = cTag("ores_in_ground/deepslate");
        /**
         * Ores in netherrack (or in equivalent blocks in the tag {@link #ORE_BEARING_GROUND_NETHERRACK}) which could logically use netherrack as recipe input or output
         */
        public static final BlockItemTagId ORES_IN_GROUND_NETHERRACK = cTag("ores_in_ground/netherrack");
        /**
         * Ores in stone (or in equivalent blocks in the tag {@link #ORE_BEARING_GROUND_STONE}) which could logically use stone as recipe input or output
         */
        public static final BlockItemTagId ORES_IN_GROUND_STONE = cTag("ores_in_ground/stone");
        public static final BlockItemTagId PLAYER_WORKSTATIONS_CRAFTING_TABLES = cTag("player_workstations/crafting_tables");
        public static final BlockItemTagId PLAYER_WORKSTATIONS_FURNACES = cTag("player_workstations/furnaces");
        public static final BlockItemTagId PUMPKINS = cTag("pumpkins");
        /** For pumpkins that are not carved. */
        public static final BlockItemTagId PUMPKINS_NORMAL = cTag("pumpkins/normal");
        /** For pumpkins that are already carved but not a light source. */
        public static final BlockItemTagId PUMPKINS_CARVED = cTag("pumpkins/carved");
        /** For pumpkins that are already carved and a light source. */
        public static final BlockItemTagId PUMPKINS_JACK_O_LANTERNS = cTag("pumpkins/jack_o_lanterns");
        public static final BlockItemTagId ROPES = cTag("ropes");

        public static final BlockItemTagId SANDS = cTag("sands");
        public static final BlockItemTagId SANDS_COLORLESS = cTag("sands/colorless");
        public static final BlockItemTagId SANDS_RED = cTag("sands/red");

        public static final BlockItemTagId SANDSTONE_BLOCKS = cTag("sandstone/blocks");
        public static final BlockItemTagId SANDSTONE_SLABS = cTag("sandstone/slabs");
        public static final BlockItemTagId SANDSTONE_STAIRS = cTag("sandstone/stairs");
        public static final BlockItemTagId SANDSTONE_RED_BLOCKS = cTag("sandstone/red_blocks");
        public static final BlockItemTagId SANDSTONE_RED_SLABS = cTag("sandstone/red_slabs");
        public static final BlockItemTagId SANDSTONE_RED_STAIRS = cTag("sandstone/red_stairs");
        public static final BlockItemTagId SANDSTONE_UNCOLORED_BLOCKS = cTag("sandstone/uncolored_blocks");
        public static final BlockItemTagId SANDSTONE_UNCOLORED_SLABS = cTag("sandstone/uncolored_slabs");
        public static final BlockItemTagId SANDSTONE_UNCOLORED_STAIRS = cTag("sandstone/uncolored_stairs");
        /**
         * Natural stone-like blocks that can be used as a base ingredient in recipes that takes stone.
         */
        public static final BlockItemTagId STONES = cTag("stones");
        /**
         * A storage block is generally a block that has a recipe to craft a bulk of 1 kind of resource to a block
         * and has a mirror recipe to reverse the crafting with no loss in resources.
         * <p></p>
         * Honey Block is special in that the reversing recipe is not a perfect mirror of the crafting recipe
         * and so, it is considered a special case and not given a storage block tag.
         */
        public static final BlockItemTagId STORAGE_BLOCKS = cTag("storage_blocks");
        public static final BlockItemTagId STORAGE_BLOCKS_BONE_MEAL = cTag("storage_blocks/bone_meal");
        public static final BlockItemTagId STORAGE_BLOCKS_COAL = cTag("storage_blocks/coal");
        public static final BlockItemTagId STORAGE_BLOCKS_COPPER = cTag("storage_blocks/copper");
        public static final BlockItemTagId STORAGE_BLOCKS_DIAMOND = cTag("storage_blocks/diamond");
        public static final BlockItemTagId STORAGE_BLOCKS_DRIED_KELP = cTag("storage_blocks/dried_kelp");
        public static final BlockItemTagId STORAGE_BLOCKS_EMERALD = cTag("storage_blocks/emerald");
        public static final BlockItemTagId STORAGE_BLOCKS_GOLD = cTag("storage_blocks/gold");
        public static final BlockItemTagId STORAGE_BLOCKS_IRON = cTag("storage_blocks/iron");
        public static final BlockItemTagId STORAGE_BLOCKS_LAPIS = cTag("storage_blocks/lapis");
        public static final BlockItemTagId STORAGE_BLOCKS_NETHERITE = cTag("storage_blocks/netherite");
        public static final BlockItemTagId STORAGE_BLOCKS_RAW_COPPER = cTag("storage_blocks/raw_copper");
        public static final BlockItemTagId STORAGE_BLOCKS_RAW_GOLD = cTag("storage_blocks/raw_gold");
        public static final BlockItemTagId STORAGE_BLOCKS_RAW_IRON = cTag("storage_blocks/raw_iron");
        public static final BlockItemTagId STORAGE_BLOCKS_REDSTONE = cTag("storage_blocks/redstone");
        public static final BlockItemTagId STORAGE_BLOCKS_RESIN = cTag("storage_blocks/resin");
        public static final BlockItemTagId STORAGE_BLOCKS_SLIME = cTag("storage_blocks/slime");
        public static final BlockItemTagId STORAGE_BLOCKS_WHEAT = cTag("storage_blocks/wheat");
        public static final BlockItemTagId STRIPPED_LOGS = cTag("stripped_logs");
        public static final BlockItemTagId STRIPPED_WOODS = cTag("stripped_woods");
        //endregion

        private static BlockItemTagId cTag(String name) {
            return create(Identifier.fromNamespaceAndPath("c", name));
        }

        private static BlockItemTagId forgeTag(String name) {
            return create(Identifier.fromNamespaceAndPath("forge", name));
        }

        private static BlockItemTagId create(Identifier id) {
            return BlockItemTagId.create(id, id);
        }
    }

    public static class Blocks {
        private static void init() {}

        //region `forge` tags for Forge-specific tags
        /**
         * Controls what blocks Endermen cannot place blocks onto.
         * <p></p>
         * This is patched into the following method: {@link EnderMan.EndermanLeaveBlockGoal#canPlaceBlock(Level, BlockPos, BlockState, BlockState, BlockState, BlockPos)}
         */
        public static final TagKey<Block> ENDERMAN_PLACE_ON_BLACKLIST = forgeTag("enderman_place_on_blacklist");
        public static final TagKey<Block> NEEDS_WOOD_TOOL = forgeTag("needs_wood_tool");
        public static final TagKey<Block> NEEDS_GOLD_TOOL = forgeTag("needs_gold_tool");
        public static final TagKey<Block> NEEDS_NETHERITE_TOOL = forgeTag("needs_netherite_tool");
        public static final TagKey<Block> STORAGE_BLOCKS_AMETHYST = BlockItems.STORAGE_BLOCKS_AMETHYST.block();
        public static final TagKey<Block> STORAGE_BLOCKS_QUARTZ = BlockItems.STORAGE_BLOCKS_QUARTZ.block();
        //endregion

        //region `c` tags for common conventions
        // Note: Other loaders have additional `c` tags that are exclusive to their loader.
        //       Forge only adopts `c` tags that are common across all loaders.
        public static final TagKey<Block> BARRELS = BlockItems.BARRELS.block();
        public static final TagKey<Block> BARRELS_WOODEN = BlockItems.BARRELS_WOODEN.block();
        /**
         * Equivalent to the "minecraft:bars" block tag.
         */
        public static final TagKey<Block> BARS = BlockItems.BARS.block();
        public static final TagKey<Block> BARS_COPPER = BlockItems.BARS_COPPER.block();
        public static final TagKey<Block> BARS_IRON = BlockItems.BARS_IRON.block();
        public static final TagKey<Block> BOOKSHELVES = BlockItems.BOOKSHELVES.block();
        /**
         * For blocks that are similar to amethyst where their budding block produces buds and cluster blocks
         */
        public static final TagKey<Block> BUDDING_BLOCKS = BlockItems.BUDDING_BLOCKS.block();
        /**
         * For blocks that are similar to amethyst where they have buddings forming from budding blocks
         */
        public static final TagKey<Block> BUDS = BlockItems.BUDS.block();
        public static final TagKey<Block> CHAINS = BlockItems.CHAINS.block();
        public static final TagKey<Block> CHESTS = BlockItems.CHESTS.block();
        public static final TagKey<Block> CHESTS_ENDER = BlockItems.CHESTS_ENDER.block();
        public static final TagKey<Block> CHESTS_TRAPPED = BlockItems.CHESTS_TRAPPED.block();
        public static final TagKey<Block> CHESTS_WOODEN = BlockItems.CHESTS_WOODEN.block();
        /**
         * For blocks that are similar to amethyst where they have clusters forming from budding blocks
         */
        public static final TagKey<Block> CLUSTERS = BlockItems.CLUSTERS.block();
        public static final TagKey<Block> COBBLESTONES = BlockItems.COBBLESTONES.block();
        public static final TagKey<Block> COBBLESTONES_DEEPSLATE = BlockItems.COBBLESTONES_DEEPSLATE.block();
        public static final TagKey<Block> COBBLESTONES_INFESTED = BlockItems.COBBLESTONES_INFESTED.block();
        public static final TagKey<Block> COBBLESTONES_MOSSY = BlockItems.COBBLESTONES_MOSSY.block();
        public static final TagKey<Block> COBBLESTONES_NORMAL = BlockItems.COBBLESTONES_NORMAL.block();
        public static final TagKey<Block> CONCRETES = BlockItems.CONCRETES.block();

        /**
         * Tag that holds all blocks that can be dyed a specific color.
         * (Does not include color blending blocks that would behave similar to leather armor item)
         */
        public static final TagKey<Block> DYED = cTag("dyed");
        public static final TagKey<Block> DYED_BLACK = cTag("dyed/black");
        public static final TagKey<Block> DYED_BLUE = cTag("dyed/blue");
        public static final TagKey<Block> DYED_BROWN = cTag("dyed/brown");
        public static final TagKey<Block> DYED_CYAN = cTag("dyed/cyan");
        public static final TagKey<Block> DYED_GRAY = cTag("dyed/gray");
        public static final TagKey<Block> DYED_GREEN = cTag("dyed/green");
        public static final TagKey<Block> DYED_LIGHT_BLUE = cTag("dyed/light_blue");
        public static final TagKey<Block> DYED_LIGHT_GRAY = cTag("dyed/light_gray");
        public static final TagKey<Block> DYED_LIME = cTag("dyed/lime");
        public static final TagKey<Block> DYED_MAGENTA = cTag("dyed/magenta");
        public static final TagKey<Block> DYED_ORANGE = cTag("dyed/orange");
        public static final TagKey<Block> DYED_PINK = cTag("dyed/pink");
        public static final TagKey<Block> DYED_PURPLE = cTag("dyed/purple");
        public static final TagKey<Block> DYED_RED = cTag("dyed/red");
        public static final TagKey<Block> DYED_WHITE = cTag("dyed/white");
        public static final TagKey<Block> DYED_YELLOW = cTag("dyed/yellow");

        public static final TagKey<Block> END_STONES = BlockItems.END_STONES.block();

        public static final TagKey<Block> FENCE_GATES = BlockItems.FENCE_GATES.block();
        public static final TagKey<Block> FENCE_GATES_WOODEN = BlockItems.FENCE_GATES_WOODEN.block();

        public static final TagKey<Block> FENCES = BlockItems.FENCES.block();
        public static final TagKey<Block> FENCES_NETHER_BRICK = BlockItems.FENCES_NETHER_BRICK.block();
        public static final TagKey<Block> FENCES_WOODEN = BlockItems.FENCES_WOODEN.block();

        /**
         * Contains living ground-based flowers that are 1 block tall such as Dandelions or Poppy.
         * Equivalent to the {@code minecraft:small_flowers} block tag.
         * This is NOT aliased with {@link BlockTags#SMALL_FLOWERS} because the vanilla tag is used to make the block weak to swords.
         */
        public static final TagKey<Block> FLOWERS_SMALL = BlockItems.FLOWERS_SMALL.block();
        /**
         * Contains living ground-based flowers that are 2 block tall such as Rose Bush or Peony.
         * Equivalent to the {@code minecraft:tall_flowers} block tag in past Minecraft versions.
         */
        public static final TagKey<Block> FLOWERS_TALL = BlockItems.FLOWERS_TALL.block();
        /**
         * Contains any living plant block that contains flowers or is a flower itself.
         * Equivalent to the {@code minecraft:flowers} block tag.
         * Aliased with {@link BlockTags#FLOWERS}.
         */
        public static final TagKey<Block> FLOWERS = BlockItems.FLOWERS.block();

        public static final TagKey<Block> GRAVELS = BlockItems.GRAVELS.block();

        public static final TagKey<Block> GLASS_BLOCKS = BlockItems.GLASS_BLOCKS.block();
        public static final TagKey<Block> GLASS_BLOCKS_COLORLESS = BlockItems.GLASS_BLOCKS_COLORLESS.block();
        /**
         * Glass which is made from cheap resources like sand and only minor additional ingredients like dyes
         */
        public static final TagKey<Block> GLASS_BLOCKS_CHEAP = BlockItems.GLASS_BLOCKS_CHEAP.block();
        public static final TagKey<Block> GLASS_BLOCKS_TINTED = BlockItems.GLASS_BLOCKS_TINTED.block();

        public static final TagKey<Block> GLASS_PANES = BlockItems.GLASS_PANES.block();
        public static final TagKey<Block> GLASS_PANES_COLORLESS = BlockItems.GLASS_PANES_COLORLESS.block();
        public static final TagKey<Block> GLAZED_TERRACOTTAS = BlockItems.GLAZED_TERRACOTTAS.block();

        /**
         * Tag that holds all blocks that recipe viewers should not show to users.
         * Recipe viewers may use this to automatically find the corresponding BlockItem to hide.
         */
        public static final TagKey<Block> HIDDEN_FROM_RECIPE_VIEWERS = cTag("hidden_from_recipe_viewers");

        public static final TagKey<Block> NATURAL_LOGS = BlockItems.NATURAL_LOGS.block();
        public static final TagKey<Block> NATURAL_LOGS_NETHER = BlockItems.NATURAL_LOGS_NETHER.block();
        public static final TagKey<Block> NATURAL_LOGS_OVERWORLD = BlockItems.NATURAL_LOGS_OVERWORLD.block();

        public static final TagKey<Block> NATURAL_WOODS = BlockItems.NATURAL_WOODS.block();

        public static final TagKey<Block> NETHERRACKS = BlockItems.NETHERRACKS.block();

        public static final TagKey<Block> OBSIDIANS = BlockItems.OBSIDIANS.block();
        /**
         * For common obsidian that has no special quirks or behaviours - ideal for recipe use.
         * Crying Obsidian, for example, is a light block and harder to obtain. So it gets its own tag instead of being under normal tag.
         */
        public static final TagKey<Block> OBSIDIANS_NORMAL = BlockItems.OBSIDIANS_NORMAL.block();
        public static final TagKey<Block> OBSIDIANS_CRYING = BlockItems.OBSIDIANS_CRYING.block();
        /**
         * Blocks which are often replaced by deepslate ores, i.e. the ores in the tag {@link #ORES_IN_GROUND_DEEPSLATE}, during world generation
         */
        public static final TagKey<Block> ORE_BEARING_GROUND_DEEPSLATE = BlockItems.ORE_BEARING_GROUND_DEEPSLATE.block();
        /**
         * Blocks which are often replaced by netherrack ores, i.e. the ores in the tag {@link #ORES_IN_GROUND_NETHERRACK}, during world generation
         */
        public static final TagKey<Block> ORE_BEARING_GROUND_NETHERRACK = BlockItems.ORE_BEARING_GROUND_NETHERRACK.block();
        /**
         * Blocks which are often replaced by stone ores, i.e. the ores in the tag {@link #ORES_IN_GROUND_STONE}, during world generation
         */
        public static final TagKey<Block> ORE_BEARING_GROUND_STONE = BlockItems.ORE_BEARING_GROUND_STONE.block();
        /**
         * Ores which on average result in more than one resource worth of materials
         */
        public static final TagKey<Block> ORE_RATES_DENSE = BlockItems.ORE_RATES_DENSE.block();
        /**
         * Ores which on average result in one resource worth of materials
         */
        public static final TagKey<Block> ORE_RATES_SINGULAR = BlockItems.ORE_RATES_SINGULAR.block();
        /**
         * Ores which on average result in less than one resource worth of materials
         */
        public static final TagKey<Block> ORE_RATES_SPARSE = BlockItems.ORE_RATES_SPARSE.block();
        public static final TagKey<Block> ORES = BlockItems.ORES.block();
        public static final TagKey<Block> ORES_NETHERITE_SCRAP = BlockItems.ORES_NETHERITE_SCRAP.block();
        public static final TagKey<Block> ORES_QUARTZ = BlockItems.ORES_QUARTZ.block();
        public static final TagKey<Block> ORES_COAL = BlockItems.ORES_COAL.block();
        public static final TagKey<Block> ORES_COPPER = BlockItems.ORES_COPPER.block();
        public static final TagKey<Block> ORES_DIAMOND = BlockItems.ORES_DIAMOND.block();
        public static final TagKey<Block> ORES_EMERALD = BlockItems.ORES_EMERALD.block();
        public static final TagKey<Block> ORES_GOLD = BlockItems.ORES_GOLD.block();
        public static final TagKey<Block> ORES_IRON = BlockItems.ORES_IRON.block();
        public static final TagKey<Block> ORES_LAPIS = BlockItems.ORES_LAPIS.block();
        public static final TagKey<Block> ORES_REDSTONE = BlockItems.ORES_REDSTONE.block();
        /**
         * Ores in deepslate (or in equivalent blocks in the tag {@link #ORE_BEARING_GROUND_DEEPSLATE}) which could logically use deepslate as recipe input or output
         */
        public static final TagKey<Block> ORES_IN_GROUND_DEEPSLATE = BlockItems.ORES_IN_GROUND_DEEPSLATE.block();
        /**
         * Ores in netherrack (or in equivalent blocks in the tag {@link #ORE_BEARING_GROUND_NETHERRACK}) which could logically use netherrack as recipe input or output
         */
        public static final TagKey<Block> ORES_IN_GROUND_NETHERRACK = BlockItems.ORES_IN_GROUND_NETHERRACK.block();
        /**
         * Ores in stone (or in equivalent blocks in the tag {@link #ORE_BEARING_GROUND_STONE}) which could logically use stone as recipe input or output
         */
        public static final TagKey<Block> ORES_IN_GROUND_STONE = BlockItems.ORES_IN_GROUND_STONE.block();
        public static final TagKey<Block> PLAYER_WORKSTATIONS_CRAFTING_TABLES = BlockItems.PLAYER_WORKSTATIONS_CRAFTING_TABLES.block();
        public static final TagKey<Block> PLAYER_WORKSTATIONS_FURNACES = BlockItems.PLAYER_WORKSTATIONS_FURNACES.block();
        public static final TagKey<Block> PUMPKINS = BlockItems.PUMPKINS.block();
        /** For pumpkins that are not carved. */
        public static final TagKey<Block> PUMPKINS_NORMAL = BlockItems.PUMPKINS_NORMAL.block();
        /** For pumpkins that are already carved but not a light source. */
        public static final TagKey<Block> PUMPKINS_CARVED = BlockItems.PUMPKINS_CARVED.block();
        /** For pumpkins that are already carved and a light source. */
        public static final TagKey<Block> PUMPKINS_JACK_O_LANTERNS = BlockItems.PUMPKINS_JACK_O_LANTERNS.block();
        /**
         * Blocks should be included in this tag if their movement/relocation can cause serious issues such
         * as world corruption upon being moved or for balance reason where the block should not be able to be relocated.
         * Example: Chunk loaders or pipes where other mods that move blocks do not respect
         * {@link BlockBehaviour.BlockStateBase#getPistonPushReaction}.
         */
        public static final TagKey<Block> RELOCATION_NOT_SUPPORTED = cTag("relocation_not_supported");
        public static final TagKey<Block> ROPES = BlockItems.ROPES.block();

        public static final TagKey<Block> SANDS = BlockItems.SANDS.block();
        public static final TagKey<Block> SANDS_COLORLESS = BlockItems.SANDS_COLORLESS.block();
        public static final TagKey<Block> SANDS_RED = BlockItems.SANDS_RED.block();

        public static final TagKey<Block> SANDSTONE_BLOCKS = BlockItems.SANDSTONE_BLOCKS.block();
        public static final TagKey<Block> SANDSTONE_SLABS = BlockItems.SANDSTONE_SLABS.block();
        public static final TagKey<Block> SANDSTONE_STAIRS = BlockItems.SANDSTONE_STAIRS.block();
        public static final TagKey<Block> SANDSTONE_RED_BLOCKS = BlockItems.SANDSTONE_RED_BLOCKS.block();
        public static final TagKey<Block> SANDSTONE_RED_SLABS = BlockItems.SANDSTONE_RED_SLABS.block();
        public static final TagKey<Block> SANDSTONE_RED_STAIRS = BlockItems.SANDSTONE_RED_STAIRS.block();
        public static final TagKey<Block> SANDSTONE_UNCOLORED_BLOCKS = BlockItems.SANDSTONE_UNCOLORED_BLOCKS.block();
        public static final TagKey<Block> SANDSTONE_UNCOLORED_SLABS = BlockItems.SANDSTONE_UNCOLORED_SLABS.block();
        public static final TagKey<Block> SANDSTONE_UNCOLORED_STAIRS = BlockItems.SANDSTONE_UNCOLORED_STAIRS.block();
        /**
         * Tag that holds all head based blocks such as Skeleton Skull or Player Head. (Named skulls to match minecraft:skulls item tag)
         */
        public static final TagKey<Block> SKULLS = cTag("skulls");
        /**
         * Natural stone-like blocks that can be used as a base ingredient in recipes that takes stone.
         */
        public static final TagKey<Block> STONES = BlockItems.STONES.block();
        /**
         * A storage block is generally a block that has a recipe to craft a bulk of 1 kind of resource to a block
         * and has a mirror recipe to reverse the crafting with no loss in resources.
         * <p></p>
         * Honey Block is special in that the reversing recipe is not a perfect mirror of the crafting recipe
         * and so, it is considered a special case and not given a storage block tag.
         */
        public static final TagKey<Block> STORAGE_BLOCKS = BlockItems.STORAGE_BLOCKS.block();
        public static final TagKey<Block> STORAGE_BLOCKS_BONE_MEAL = BlockItems.STORAGE_BLOCKS_BONE_MEAL.block();
        public static final TagKey<Block> STORAGE_BLOCKS_COAL = BlockItems.STORAGE_BLOCKS_COAL.block();
        public static final TagKey<Block> STORAGE_BLOCKS_COPPER = BlockItems.STORAGE_BLOCKS_COPPER.block();
        public static final TagKey<Block> STORAGE_BLOCKS_DIAMOND = BlockItems.STORAGE_BLOCKS_DIAMOND.block();
        public static final TagKey<Block> STORAGE_BLOCKS_DRIED_KELP = BlockItems.STORAGE_BLOCKS_DRIED_KELP.block();
        public static final TagKey<Block> STORAGE_BLOCKS_EMERALD = BlockItems.STORAGE_BLOCKS_EMERALD.block();
        public static final TagKey<Block> STORAGE_BLOCKS_GOLD = BlockItems.STORAGE_BLOCKS_GOLD.block();
        public static final TagKey<Block> STORAGE_BLOCKS_IRON = BlockItems.STORAGE_BLOCKS_IRON.block();
        public static final TagKey<Block> STORAGE_BLOCKS_LAPIS = BlockItems.STORAGE_BLOCKS_LAPIS.block();
        public static final TagKey<Block> STORAGE_BLOCKS_NETHERITE = BlockItems.STORAGE_BLOCKS_NETHERITE.block();
        public static final TagKey<Block> STORAGE_BLOCKS_RAW_COPPER = BlockItems.STORAGE_BLOCKS_RAW_COPPER.block();
        public static final TagKey<Block> STORAGE_BLOCKS_RAW_GOLD = BlockItems.STORAGE_BLOCKS_RAW_GOLD.block();
        public static final TagKey<Block> STORAGE_BLOCKS_RAW_IRON = BlockItems.STORAGE_BLOCKS_RAW_IRON.block();
        public static final TagKey<Block> STORAGE_BLOCKS_REDSTONE = BlockItems.STORAGE_BLOCKS_REDSTONE.block();
        public static final TagKey<Block> STORAGE_BLOCKS_RESIN = BlockItems.STORAGE_BLOCKS_RESIN.block();
        public static final TagKey<Block> STORAGE_BLOCKS_SLIME = BlockItems.STORAGE_BLOCKS_SLIME.block();
        public static final TagKey<Block> STORAGE_BLOCKS_WHEAT = BlockItems.STORAGE_BLOCKS_WHEAT.block();
        public static final TagKey<Block> STRIPPED_LOGS = BlockItems.STRIPPED_LOGS.block();
        public static final TagKey<Block> STRIPPED_WOODS = BlockItems.STRIPPED_WOODS.block();
        public static final TagKey<Block> VILLAGER_JOB_SITES = cTag("villager_job_sites");
        //endregion

        private static TagKey<Block> cTag(String name) {
            return BlockTags.create(Identifier.fromNamespaceAndPath("c", name));
        }

        private static TagKey<Block> forgeTag(String name) {
            return BlockTags.create(Identifier.fromNamespaceAndPath("forge", name));
        }
    }

    public static class EntityTypes {
        private static void init() {}

        //region `c` tags for common conventions
        // Note: Other loaders have additional `c` tags that are exclusive to their loader.
        //       Forge only adopts `c` tags that are common across all loaders.
        public static final TagKey<EntityType<?>> BOSSES = cTag("bosses");
        public static final TagKey<EntityType<?>> MINECARTS = cTag("minecarts");
        public static final TagKey<EntityType<?>> BOATS = cTag("boats");

        /// Tag containing entity types, generally extending {@link ItemFrame},
        /// that can be placed on the surfaces of blocks to display an item.
        public static final TagKey<EntityType<?>> ITEM_FRAMES = cTag("item_frames");

        /**
         * Entities should be included in this tag if they are not allowed to be picked up by items or grabbed in a way
         * that a player can easily move the entity to anywhere they want. Ideal for special entities that should not
         * be able to be put into a mob jar for example.
         */
        public static final TagKey<EntityType<?>> CAPTURING_NOT_SUPPORTED = cTag("capturing_not_supported");

        /**
         * Entities should be included in this tag if they are not allowed to be teleported in any way.
         * This is more for mods that allow teleporting entities within the same dimension. Any mod that is
         * teleporting entities to new dimensions should be checking canChangeDimensions method on the entity itself.
         */
        public static final TagKey<EntityType<?>> TELEPORTING_NOT_SUPPORTED = cTag("teleporting_not_supported");
        //endregion

        private static TagKey<EntityType<?>> cTag(String name) {
            return TagKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("c", name));
        }
    }

    public static class Items {
        private static void init() {}

        //region `forge` tags for Forge-specific tags
        /**
         * Controls what items can be consumed for enchanting such as Enchanting Tables.
         * This tag defaults to {@link net.minecraft.world.item.Items#LAPIS_LAZULI} when not present in any datapacks, including forge client on vanilla server
         */
        public static final TagKey<Item> ENCHANTING_FUELS = forgeTag("enchanting_fuels");

        public static final TagKey<Item> STORAGE_BLOCKS_AMETHYST = BlockItems.STORAGE_BLOCKS_AMETHYST.item();
        public static final TagKey<Item> STORAGE_BLOCKS_QUARTZ = BlockItems.STORAGE_BLOCKS_QUARTZ.item();
        //endregion

        //region `c` tags for common conventions
        // Note: Other loaders have additional `c` tags that are exclusive to their loader.
        //       Forge only adopts `c` tags that are common across all loaders.
        public static final TagKey<Item> BARRELS = BlockItems.BARRELS.item();
        public static final TagKey<Item> BARRELS_WOODEN = BlockItems.BARRELS_WOODEN.item();
        /**
         * Equivalent to the "minecraft:bars" item tag.
         */
        public static final TagKey<Item> BARS = BlockItems.BARS.item();
        public static final TagKey<Item> BARS_COPPER = BlockItems.BARS_COPPER.item();
        public static final TagKey<Item> BARS_IRON = BlockItems.BARS_IRON.item();
        public static final TagKey<Item> BONES = cTag("bones");
        public static final TagKey<Item> BOOKSHELVES = BlockItems.BOOKSHELVES.item();
        public static final TagKey<Item> BRICKS = cTag("bricks");
        public static final TagKey<Item> BRICKS_NORMAL = cTag("bricks/normal");
        public static final TagKey<Item> BRICKS_NETHER = cTag("bricks/nether");
        public static final TagKey<Item> BRICKS_RESIN = cTag("bricks/resin");
        public static final TagKey<Item> BUCKETS = cTag("buckets");
        public static final TagKey<Item> BUCKETS_EMPTY = cTag("buckets/empty");
        /**
         * Does not include entity water buckets.
         * If checking for the fluid this bucket holds in code, please use {@link FluidBucketWrapper#getFluid} instead.
         */
        public static final TagKey<Item> BUCKETS_WATER = cTag("buckets/water");
        /**
         * If checking for the fluid this bucket holds in code, please use {@link FluidBucketWrapper#getFluid} instead.
         */
        public static final TagKey<Item> BUCKETS_LAVA = cTag("buckets/lava");
        public static final TagKey<Item> BUCKETS_MILK = cTag("buckets/milk");
        public static final TagKey<Item> BUCKETS_POWDER_SNOW = cTag("buckets/powder_snow");
        public static final TagKey<Item> BUCKETS_ENTITY_WATER = cTag("buckets/entity_water");
        /**
         * For blocks that are similar to amethyst where their budding block produces buds and cluster blocks
         */
        public static final TagKey<Item> BUDDING_BLOCKS = BlockItems.BUDDING_BLOCKS.item();
        /**
         * For blocks that are similar to amethyst where they have buddings forming from budding blocks
         */
        public static final TagKey<Item> BUDS = BlockItems.BUDS.item();
        public static final TagKey<Item> CHAINS = BlockItems.CHAINS.item();
        public static final TagKey<Item> CHESTS = BlockItems.CHESTS.item();
        public static final TagKey<Item> CHESTS_WOODEN = BlockItems.CHESTS_WOODEN.item();
        public static final TagKey<Item> CHESTS_ENDER = BlockItems.CHESTS_ENDER.item();
        public static final TagKey<Item> CHESTS_TRAPPED = BlockItems.CHESTS_TRAPPED.item();
        public static final TagKey<Item> COBBLESTONES = BlockItems.COBBLESTONES.item();
        public static final TagKey<Item> COBBLESTONES_NORMAL = BlockItems.COBBLESTONES_NORMAL.item();
        public static final TagKey<Item> COBBLESTONES_INFESTED = BlockItems.COBBLESTONES_INFESTED.item();
        public static final TagKey<Item> COBBLESTONES_MOSSY = BlockItems.COBBLESTONES_MOSSY.item();
        public static final TagKey<Item> COBBLESTONES_DEEPSLATE = BlockItems.COBBLESTONES_DEEPSLATE.item();
        public static final TagKey<Item> CONCRETES = BlockItems.CONCRETES.item();
        /**
         * Block tag equivalent is {@link BlockTags#CONCRETE_POWDER}
         */
        public static final TagKey<Item> CONCRETE_POWDERS = cTag("concrete_powders");
        /**
         * For blocks that are similar to amethyst where they have clusters forming from budding blocks
         */
        public static final TagKey<Item> CLUSTERS = BlockItems.CLUSTERS.item();
        public static final TagKey<Item> CLUMPS = cTag("clumps");
        public static final TagKey<Item> CLUMPS_RESIN = cTag("clumps/resin");
        /**
         * For raw materials harvested from growable plants. Crop items can be edible like carrots or
         * non-edible like wheat and cocoa beans.
         */
        public static final TagKey<Item> CROPS = cTag("crops");
        public static final TagKey<Item> CROPS_BEETROOT = cTag("crops/beetroot");
        public static final TagKey<Item> CROPS_CACTUS = cTag("crops/cactus");
        public static final TagKey<Item> CROPS_CARROT = cTag("crops/carrot");
        public static final TagKey<Item> CROPS_COCOA_BEAN = cTag("crops/cocoa_bean");
        public static final TagKey<Item> CROPS_MELON = cTag("crops/melon");
        public static final TagKey<Item> CROPS_NETHER_WART = cTag("crops/nether_wart");
        public static final TagKey<Item> CROPS_POTATO = cTag("crops/potato");
        public static final TagKey<Item> CROPS_PUMPKIN = cTag("crops/pumpkin");
        public static final TagKey<Item> CROPS_SUGAR_CANE = cTag("crops/sugar_cane");
        public static final TagKey<Item> CROPS_WHEAT = cTag("crops/wheat");
        public static final TagKey<Item> DUSTS = cTag("dusts");
        public static final TagKey<Item> DUSTS_REDSTONE = cTag("dusts/redstone");
        public static final TagKey<Item> DUSTS_GLOWSTONE = cTag("dusts/glowstone");

        /**
         * Drinks are defined as (1) consumable items that (2) use the
         * {@linkplain ItemUseAnimation#DRINK drink item use animation}, (3) can be consumed regardless of the
         * player's current hunger.
         *
         * <p>Drinks may provide nutrition and saturation, but are not required to do so.
         *
         * <p>More specific types of drinks, such as Water, Milk, or Juice should be placed in a sub-tag, such as
         * {@code #c:drinks/water}, {@code #c:drinks/milk}, and {@code #c:drinks/juice}.
         */
        public static final TagKey<Item> DRINKS = cTag("drinks");
        public static final TagKey<Item> DRINKS_HONEY = cTag("drinks/honey");
        /**
         * Plant based fruit and vegetable juices belong in this tag, for example apple juice and carrot juice.
         *
         * <p>If tags for specific types of juices are desired, they may go in a sub-tag, using their regular name such as
         * {@code #c:drinks/apple_juice}.
         */
        public static final TagKey<Item> DRINKS_JUICE = cTag("drinks/juice");
        public static final TagKey<Item> DRINKS_MAGIC = cTag("drinks/magic");
        public static final TagKey<Item> DRINKS_MILK = cTag("drinks/milk");
        /**
         * For drinks that always grant the {@linkplain MobEffects#BAD_OMEN Bad Omen} effect.
         */
        public static final TagKey<Item> DRINKS_OMINOUS = cTag("drinks/ominous");
        /**
         * For consumable drinks that contain only water.
         */
        public static final TagKey<Item> DRINKS_WATER = cTag("drinks/water");
        /**
         * For consumable drinks that are generally watery (such as potions).
         */
        public static final TagKey<Item> DRINKS_WATERY = cTag("drinks/watery");

        /**
         * For non-empty bottles that are {@linkplain #DRINKS drinkable}.
         */
        public static final TagKey<Item> DRINK_CONTAINING_BOTTLE = cTag("drink_containing/bottle");
        /**
         * For non-empty buckets that are {@linkplain #DRINKS drinkable}.
         */
        public static final TagKey<Item> DRINK_CONTAINING_BUCKET = cTag("drink_containing/bucket");

        /**
         * Tag that holds all blocks and items that can be dyed a specific color.
         * (Does not include color blending items like leather armor
         * Use {@link net.minecraft.tags.ItemTags#DYEABLE} tag instead for color blending items)
         */
        public static final TagKey<Item> DYED = cTag("dyed");
        public static final TagKey<Item> DYED_BLACK = cTag("dyed/black");
        public static final TagKey<Item> DYED_BLUE = cTag("dyed/blue");
        public static final TagKey<Item> DYED_BROWN = cTag("dyed/brown");
        public static final TagKey<Item> DYED_CYAN = cTag("dyed/cyan");
        public static final TagKey<Item> DYED_GRAY = cTag("dyed/gray");
        public static final TagKey<Item> DYED_GREEN = cTag("dyed/green");
        public static final TagKey<Item> DYED_LIGHT_BLUE = cTag("dyed/light_blue");
        public static final TagKey<Item> DYED_LIGHT_GRAY = cTag("dyed/light_gray");
        public static final TagKey<Item> DYED_LIME = cTag("dyed/lime");
        public static final TagKey<Item> DYED_MAGENTA = cTag("dyed/magenta");
        public static final TagKey<Item> DYED_ORANGE = cTag("dyed/orange");
        public static final TagKey<Item> DYED_PINK = cTag("dyed/pink");
        public static final TagKey<Item> DYED_PURPLE = cTag("dyed/purple");
        public static final TagKey<Item> DYED_RED = cTag("dyed/red");
        public static final TagKey<Item> DYED_WHITE = cTag("dyed/white");
        public static final TagKey<Item> DYED_YELLOW = cTag("dyed/yellow");

        public static final TagKey<Item> DYES = cTag("dyes");
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

        public static final TagKey<Item> EGGS = cTag("eggs");
        public static final TagKey<Item> END_STONES = BlockItems.END_STONES.item();
        public static final TagKey<Item> ENDER_PEARLS = cTag("ender_pearls");

        public static final TagKey<Item> FEATHERS = cTag("feathers");
        public static final TagKey<Item> FENCE_GATES = BlockItems.FENCE_GATES.item();
        public static final TagKey<Item> FENCE_GATES_WOODEN = BlockItems.FENCE_GATES_WOODEN.item();
        public static final TagKey<Item> FENCES = BlockItems.FENCES.item();
        public static final TagKey<Item> FENCES_NETHER_BRICK = BlockItems.FENCES_NETHER_BRICK.item();
        public static final TagKey<Item> FENCES_WOODEN = BlockItems.FENCES_WOODEN.item();
        /**
         * For bonemeal-like items that can grow plants.
         */
        public static final TagKey<Item> FERTILIZERS = cTag("fertilizers");
        /**
         * Contains living ground-based flowers that are 1 block tall such as Dandelions or Poppy.
         * Equivalent to the {@code minecraft:small_flowers} item tag.
         * Aliased with {@link ItemTags#SMALL_FLOWERS}.
         */
        public static final TagKey<Item> FLOWERS_SMALL = BlockItems.FLOWERS_SMALL.item();
        /**
         * Contains living ground-based flowers that are 2 block tall such as Rose Bush or Peony.
         * Equivalent to the {@code minecraft:tall_flowers} item tag in past Minecraft versions.
         */
        public static final TagKey<Item> FLOWERS_TALL = BlockItems.FLOWERS_TALL.item();
        /**
         * Contains any living plant block that contains flowers or is a flower itself.
         * Equivalent to the {@code minecraft:flowers} item tag in past Minecraft versions.
         */
        public static final TagKey<Item> FLOWERS = BlockItems.FLOWERS.item();
        public static final TagKey<Item> FOODS = cTag("foods");
        /**
         * Apples and other foods that are considered fruits in the culinary field belong in this tag.
         * Cherries would go here as they are considered a "stone fruit" within culinary fields.
         */
        public static final TagKey<Item> FOODS_FRUIT = cTag("foods/fruit");
        /**
         * Tomatoes and other foods that are considered vegetables in the culinary field belong in this tag.
         */
        public static final TagKey<Item> FOODS_VEGETABLE = cTag("foods/vegetable");
        /**
         * Strawberries, raspberries, and other berry foods belong in this tag.
         * Cherries would NOT go here as they are considered a "stone fruit" within culinary fields.
         */
        public static final TagKey<Item> FOODS_BERRY = cTag("foods/berry");
        public static final TagKey<Item> FOODS_BREAD = cTag("foods/bread");
        public static final TagKey<Item> FOODS_COOKIE = cTag("foods/cookie");
        /**
         * For all doughs regardless of type, specific types of dough should fall under their respective sub-tag.<br/>
         * For example:<br/>
         * - Wheat dough (which generally results in bread) would go in "#c:foods/dough/wheat"<br/>
         * - Rye dough (which has rye as it's main ingredient) would go in "#c:foods/dough/rye"<br/>
         * - Sub-tags should also be added to this tag, for example: "#c:foods/dough/wheat" should be added to "#c:foods/dough"<br/>
         * <br/>
         * There are some important assumptions that should be kept in mind.<br/>
         * - It is assumed that "1 dough = result", which in the case of wheat dough would be "1 dough = 1 bread"<br/>
         * - It is assumed that this dough can be baked into another item<br/>
         * - It is *not* assumed that all doughs result in bread, there can be doughs in this tag that result in things like pizza, etc.
         * This means that this tag should *not* be used for furnace recipes, mods should add their own dough to result recipes for their respective items.
         */
        public static final TagKey<Item> FOODS_DOUGH = cTag("foods/dough");
        public static final TagKey<Item> FOODS_RAW_MEAT = cTag("foods/raw_meat");
        public static final TagKey<Item> FOODS_COOKED_MEAT = cTag("foods/cooked_meat");
        public static final TagKey<Item> FOODS_RAW_FISH = cTag("foods/raw_fish");
        public static final TagKey<Item> FOODS_COOKED_FISH = cTag("foods/cooked_fish");
        /**
         * Soups, stews, and other liquid food in bowls belongs in this tag.
         */
        public static final TagKey<Item> FOODS_SOUP = cTag("foods/soup");
        /**
         * Sweets and candies like lollipops or chocolate belong in this tag.
         */
        public static final TagKey<Item> FOODS_CANDY = cTag("foods/candy");
        /**
         * Pies and other pie-like foods belong in this tag.
         */
        public static final TagKey<Item> FOODS_PIE = cTag("foods/pie");
        /**
         * Any gold-based foods would go in this tag. Such as Golden Apples or Glistering Melon Slice.
         */
        public static final TagKey<Item> FOODS_GOLDEN = cTag("foods/golden");
        /**
         * Foods like cake that can be eaten when placed in the world belong in this tag.
         */
        public static final TagKey<Item> FOODS_EDIBLE_WHEN_PLACED = cTag("foods/edible_when_placed");
        /**
         * For foods that inflict food poisoning-like effects.
         * Examples are Rotten Flesh's Hunger or Pufferfish's Nausea, or Poisonous Potato's Poison.
         */
        public static final TagKey<Item> FOODS_FOOD_POISONING = cTag("foods/food_poisoning");
        /**
         * All foods edible by animals excluding poisonous foods.
         * (Does not include {@link ItemTags#PARROT_POISONOUS_FOOD})
         */
        public static final TagKey<Item> ANIMAL_FOODS = cTag("animal_foods");
        public static final TagKey<Item> GEMS = cTag("gems");
        public static final TagKey<Item> GEMS_DIAMOND = cTag("gems/diamond");
        public static final TagKey<Item> GEMS_EMERALD = cTag("gems/emerald");
        public static final TagKey<Item> GEMS_AMETHYST = cTag("gems/amethyst");
        public static final TagKey<Item> GEMS_LAPIS = cTag("gems/lapis");
        public static final TagKey<Item> GEMS_PRISMARINE = cTag("gems/prismarine");
        public static final TagKey<Item> GEMS_QUARTZ = cTag("gems/quartz");

        public static final TagKey<Item> GLASS_BLOCKS = BlockItems.GLASS_BLOCKS.item();
        public static final TagKey<Item> GLASS_BLOCKS_COLORLESS = BlockItems.GLASS_BLOCKS_COLORLESS.item();
        /**
         * Glass which is made from cheap resources like sand and only minor additional ingredients like dyes
         */
        public static final TagKey<Item> GLASS_BLOCKS_CHEAP = BlockItems.GLASS_BLOCKS_CHEAP.item();
        public static final TagKey<Item> GLASS_BLOCKS_TINTED = BlockItems.GLASS_BLOCKS_TINTED.item();

        public static final TagKey<Item> GLASS_PANES = BlockItems.GLASS_PANES.item();
        public static final TagKey<Item> GLASS_PANES_COLORLESS = BlockItems.GLASS_PANES_COLORLESS.item();
        public static final TagKey<Item> GLAZED_TERRACOTTAS = BlockItems.GLAZED_TERRACOTTAS.item();

        public static final TagKey<Item> GRAVELS = BlockItems.GRAVELS.item();
        public static final TagKey<Item> GUNPOWDERS = cTag("gunpowders");

        /**
         * Tag that holds all items that recipe viewers should not show to users.
         */
        public static final TagKey<Item> HIDDEN_FROM_RECIPE_VIEWERS = cTag("hidden_from_recipe_viewers");
        public static final TagKey<Item> OBSIDIANS = BlockItems.OBSIDIANS.item();
        /**
         * For common obsidian that has no special quirks or behaviours - ideal for recipe use.
         * Crying Obsidian, for example, is a light block and harder to obtain. So it gets its own tag instead of being under normal tag.
         */
        public static final TagKey<Item> OBSIDIANS_NORMAL = BlockItems.OBSIDIANS_NORMAL.item();
        public static final TagKey<Item> OBSIDIANS_CRYING = BlockItems.OBSIDIANS_CRYING.item();
        /**
         * Blocks which are often replaced by deepslate ores, i.e. the ores in the tag {@link #ORES_IN_GROUND_DEEPSLATE}, during world generation
         */
        public static final TagKey<Item> ORE_BEARING_GROUND_DEEPSLATE = BlockItems.ORE_BEARING_GROUND_DEEPSLATE.item();
        /**
         * Blocks which are often replaced by netherrack ores, i.e. the ores in the tag {@link #ORES_IN_GROUND_NETHERRACK}, during world generation
         */
        public static final TagKey<Item> ORE_BEARING_GROUND_NETHERRACK = BlockItems.ORE_BEARING_GROUND_NETHERRACK.item();
        /**
         * Blocks which are often replaced by stone ores, i.e. the ores in the tag {@link #ORES_IN_GROUND_STONE}, during world generation
         */
        public static final TagKey<Item> ORE_BEARING_GROUND_STONE = BlockItems.ORE_BEARING_GROUND_STONE.item();
        /**
         * Ores which on average result in more than one resource worth of materials
         */
        public static final TagKey<Item> ORE_RATES_DENSE = BlockItems.ORE_RATES_DENSE.item();
        /**
         * Ores which on average result in one resource worth of materials
         */
        public static final TagKey<Item> ORE_RATES_SINGULAR = BlockItems.ORE_RATES_SINGULAR.item();
        /**
         * Ores which on average result in less than one resource worth of materials
         */
        public static final TagKey<Item> ORE_RATES_SPARSE = BlockItems.ORE_RATES_SPARSE.item();
        public static final TagKey<Item> ORES = BlockItems.ORES.item();
        public static final TagKey<Item> ORES_COAL = BlockItems.ORES_COAL.item();
        public static final TagKey<Item> ORES_COPPER = BlockItems.ORES_COPPER.item();
        public static final TagKey<Item> ORES_DIAMOND = BlockItems.ORES_DIAMOND.item();
        public static final TagKey<Item> ORES_EMERALD = BlockItems.ORES_EMERALD.item();
        public static final TagKey<Item> ORES_GOLD = BlockItems.ORES_GOLD.item();
        public static final TagKey<Item> ORES_IRON = BlockItems.ORES_IRON.item();
        public static final TagKey<Item> ORES_LAPIS = BlockItems.ORES_LAPIS.item();
        public static final TagKey<Item> ORES_NETHERITE_SCRAP = BlockItems.ORES_NETHERITE_SCRAP.item();
        public static final TagKey<Item> ORES_QUARTZ = BlockItems.ORES_QUARTZ.item();
        public static final TagKey<Item> ORES_REDSTONE = BlockItems.ORES_REDSTONE.item();
        /**
         * Ores in deepslate (or in equivalent blocks in the tag {@link #ORE_BEARING_GROUND_DEEPSLATE}) which could logically use deepslate as recipe input or output
         */
        public static final TagKey<Item> ORES_IN_GROUND_DEEPSLATE = BlockItems.ORES_IN_GROUND_DEEPSLATE.item();
        /**
         * Ores in netherrack (or in equivalent blocks in the tag {@link #ORE_BEARING_GROUND_NETHERRACK}) which could logically use netherrack as recipe input or output
         */
        public static final TagKey<Item> ORES_IN_GROUND_NETHERRACK = BlockItems.ORES_IN_GROUND_NETHERRACK.item();
        /**
         * Ores in stone (or in equivalent blocks in the tag {@link #ORE_BEARING_GROUND_STONE}) which could logically use stone as recipe input or output
         */
        public static final TagKey<Item> ORES_IN_GROUND_STONE = BlockItems.ORES_IN_GROUND_STONE.item();
        public static final TagKey<Item> INGOTS = cTag("ingots");
        public static final TagKey<Item> INGOTS_COPPER = cTag("ingots/copper");
        public static final TagKey<Item> INGOTS_GOLD = cTag("ingots/gold");
        public static final TagKey<Item> INGOTS_IRON = cTag("ingots/iron");
        public static final TagKey<Item> INGOTS_NETHERITE = cTag("ingots/netherite");
        public static final TagKey<Item> LEATHERS = cTag("leathers");
        public static final TagKey<Item> MUSHROOMS = cTag("mushrooms");
        /**
         * For music disc-like materials to be used in recipes.
         * A pancake with a JUKEBOX_PLAYABLE component attached to play in Jukeboxes as an Easter Egg is not a music disc and would not go in this tag.
         */
        public static final TagKey<Item> MUSIC_DISCS = cTag("music_discs");
        public static final TagKey<Item> NATURAL_LOGS = BlockItems.NATURAL_LOGS.item();
        public static final TagKey<Item> NATURAL_LOGS_NETHER = BlockItems.NATURAL_LOGS_NETHER.item();
        public static final TagKey<Item> NATURAL_LOGS_OVERWORLD = BlockItems.NATURAL_LOGS_OVERWORLD.item();
        public static final TagKey<Item> NATURAL_WOODS = BlockItems.NATURAL_WOODS.item();
        public static final TagKey<Item> NETHER_STARS = cTag("nether_stars");
        public static final TagKey<Item> NETHERRACKS = BlockItems.NETHERRACKS.item();
        public static final TagKey<Item> NUGGETS = cTag("nuggets");
        public static final TagKey<Item> NUGGETS_COPPER = cTag("nuggets/copper");
        public static final TagKey<Item> NUGGETS_GOLD = cTag("nuggets/gold");
        public static final TagKey<Item> NUGGETS_IRON = cTag("nuggets/iron");
        public static final TagKey<Item> POTIONS = cTag("potions");
        public static final TagKey<Item> POTIONS_BOTTLE = cTag("potions/bottle");
        public static final TagKey<Item> PLAYER_WORKSTATIONS_CRAFTING_TABLES = BlockItems.PLAYER_WORKSTATIONS_CRAFTING_TABLES.item();
        public static final TagKey<Item> PLAYER_WORKSTATIONS_FURNACES = BlockItems.PLAYER_WORKSTATIONS_FURNACES.item();
        public static final TagKey<Item> PUMPKINS = BlockItems.PUMPKINS.item();
        /** For pumpkins that are not carved. */
        public static final TagKey<Item> PUMPKINS_NORMAL = BlockItems.PUMPKINS_NORMAL.item();
        /** For pumpkins that are already carved but not a light source. */
        public static final TagKey<Item> PUMPKINS_CARVED = BlockItems.PUMPKINS_CARVED.item();
        /** For pumpkins that are already carved and a light source. */
        public static final TagKey<Item> PUMPKINS_JACK_O_LANTERNS = BlockItems.PUMPKINS_JACK_O_LANTERNS.item();
        public static final TagKey<Item> RAW_MATERIALS = cTag("raw_materials");
        public static final TagKey<Item> RAW_MATERIALS_COPPER = cTag("raw_materials/copper");
        public static final TagKey<Item> RAW_MATERIALS_GOLD = cTag("raw_materials/gold");
        public static final TagKey<Item> RAW_MATERIALS_IRON = cTag("raw_materials/iron");
        /**
         * For rod-like materials to be used in recipes.
         */
        public static final TagKey<Item> RODS = cTag("rods");
        public static final TagKey<Item> RODS_BLAZE = cTag("rods/blaze");
        public static final TagKey<Item> RODS_BREEZE = cTag("rods/breeze");
        /**
         * For stick-like materials to be used in recipes.
         * One example is a mod adds stick variants such as Spruce Sticks but would like stick recipes to be able to use it.
         */
        public static final TagKey<Item> RODS_WOODEN = cTag("rods/wooden");
        public static final TagKey<Item> ROPES = BlockItems.ROPES.item();

        public static final TagKey<Item> SANDS = BlockItems.SANDS.item();
        public static final TagKey<Item> SANDS_COLORLESS = BlockItems.SANDS_COLORLESS.item();
        public static final TagKey<Item> SANDS_RED = BlockItems.SANDS_RED.item();

        public static final TagKey<Item> SEEDS = cTag("seeds");
        public static final TagKey<Item> SEEDS_BEETROOT = cTag("seeds/beetroot");
        public static final TagKey<Item> SEEDS_MELON = cTag("seeds/melon");
        public static final TagKey<Item> SEEDS_PITCHER_PLANT = cTag("seeds/pitcher_plant");
        public static final TagKey<Item> SEEDS_PUMPKIN = cTag("seeds/pumpkin");
        public static final TagKey<Item> SEEDS_TORCHFLOWER = cTag("seeds/torchflower");
        public static final TagKey<Item> SEEDS_WHEAT = cTag("seeds/wheat");

        public static final TagKey<Item> SANDSTONE_BLOCKS = BlockItems.SANDSTONE_BLOCKS.item();
        public static final TagKey<Item> SANDSTONE_SLABS = BlockItems.SANDSTONE_SLABS.item();
        public static final TagKey<Item> SANDSTONE_STAIRS = BlockItems.SANDSTONE_STAIRS.item();
        public static final TagKey<Item> SANDSTONE_RED_BLOCKS = BlockItems.SANDSTONE_RED_BLOCKS.item();
        public static final TagKey<Item> SANDSTONE_RED_SLABS = BlockItems.SANDSTONE_RED_SLABS.item();
        public static final TagKey<Item> SANDSTONE_RED_STAIRS = BlockItems.SANDSTONE_RED_STAIRS.item();
        public static final TagKey<Item> SANDSTONE_UNCOLORED_BLOCKS = BlockItems.SANDSTONE_UNCOLORED_BLOCKS.item();
        public static final TagKey<Item> SANDSTONE_UNCOLORED_SLABS = BlockItems.SANDSTONE_UNCOLORED_SLABS.item();
        public static final TagKey<Item> SANDSTONE_UNCOLORED_STAIRS = BlockItems.SANDSTONE_UNCOLORED_STAIRS.item();

        /**
         * Block tag equivalent is {@link BlockTags#SHULKER_BOXES}
         */
        public static final TagKey<Item> SHULKER_BOXES = cTag("shulker_boxes");
        public static final TagKey<Item> SLIME_BALLS = cTag("slime_balls");
        /**
         * Natural stone-like blocks that can be used as a base ingredient in recipes that takes stone.
         */
        public static final TagKey<Item> STONES = BlockItems.STONES.item();
        /**
         * A storage block is generally a block that has a recipe to craft a bulk of 1 kind of resource to a block
         * and has a mirror recipe to reverse the crafting with no loss in resources.
         * <p></p>
         * Honey Block is special in that the reversing recipe is not a perfect mirror of the crafting recipe
         * and so, it is considered a special case and not given a storage block tag.
         */
        public static final TagKey<Item> STORAGE_BLOCKS = BlockItems.STORAGE_BLOCKS.item();
        public static final TagKey<Item> STORAGE_BLOCKS_BONE_MEAL = BlockItems.STORAGE_BLOCKS_BONE_MEAL.item();
        public static final TagKey<Item> STORAGE_BLOCKS_COAL = BlockItems.STORAGE_BLOCKS_COAL.item();
        public static final TagKey<Item> STORAGE_BLOCKS_COPPER = BlockItems.STORAGE_BLOCKS_COPPER.item();
        public static final TagKey<Item> STORAGE_BLOCKS_DIAMOND = BlockItems.STORAGE_BLOCKS_DIAMOND.item();
        public static final TagKey<Item> STORAGE_BLOCKS_DRIED_KELP = BlockItems.STORAGE_BLOCKS_DRIED_KELP.item();
        public static final TagKey<Item> STORAGE_BLOCKS_EMERALD = BlockItems.STORAGE_BLOCKS_EMERALD.item();
        public static final TagKey<Item> STORAGE_BLOCKS_GOLD = BlockItems.STORAGE_BLOCKS_GOLD.item();
        public static final TagKey<Item> STORAGE_BLOCKS_IRON = BlockItems.STORAGE_BLOCKS_IRON.item();
        public static final TagKey<Item> STORAGE_BLOCKS_LAPIS = BlockItems.STORAGE_BLOCKS_LAPIS.item();
        public static final TagKey<Item> STORAGE_BLOCKS_NETHERITE = BlockItems.STORAGE_BLOCKS_NETHERITE.item();
        public static final TagKey<Item> STORAGE_BLOCKS_RAW_COPPER = BlockItems.STORAGE_BLOCKS_RAW_COPPER.item();
        public static final TagKey<Item> STORAGE_BLOCKS_RAW_GOLD = BlockItems.STORAGE_BLOCKS_RAW_GOLD.item();
        public static final TagKey<Item> STORAGE_BLOCKS_RAW_IRON = BlockItems.STORAGE_BLOCKS_RAW_IRON.item();
        public static final TagKey<Item> STORAGE_BLOCKS_REDSTONE = BlockItems.STORAGE_BLOCKS_REDSTONE.item();
        public static final TagKey<Item> STORAGE_BLOCKS_RESIN = BlockItems.STORAGE_BLOCKS_RESIN.item();
        public static final TagKey<Item> STORAGE_BLOCKS_SLIME = BlockItems.STORAGE_BLOCKS_SLIME.item();
        public static final TagKey<Item> STORAGE_BLOCKS_WHEAT = BlockItems.STORAGE_BLOCKS_WHEAT.item();
        public static final TagKey<Item> STRINGS = cTag("strings");
        public static final TagKey<Item> STRIPPED_LOGS = BlockItems.STRIPPED_LOGS.item();
        public static final TagKey<Item> STRIPPED_WOODS = BlockItems.STRIPPED_WOODS.item();
        public static final TagKey<Item> VILLAGER_JOB_SITES = cTag("villager_job_sites");

        // Tools and Armors
        /**
         * A tag containing all existing tools. Do not use this tag for determining a tool's behavior.
         * Please use {@link ToolActions} instead for what action a tool can do.
         *
         * @see ToolAction
         * @see ToolActions
         */
        public static final TagKey<Item> TOOLS = cTag("tools");
        /**
         * A tag containing all existing shields. Do not use this tag for determining a tool's behavior.
         * Please use {@link ToolActions} instead for what action a tool can do.
         *
         * @see ToolAction
         * @see ToolActions
         */
        public static final TagKey<Item> TOOLS_SHIELD = cTag("tools/shield");
        /**
         * A tag containing all existing bows. Do not use this tag for determining a tool's behavior.
         * Please use {@link ToolActions} instead for what action a tool can do.
         *
         * @see ToolAction
         * @see ToolActions
         */
        public static final TagKey<Item> TOOLS_BOW = cTag("tools/bow");
        /**
         * A tag containing all existing crossbows. Do not use this tag for determining a tool's behavior.
         * Please use {@link ToolActions} instead for what action a tool can do.
         *
         * @see ToolAction
         * @see ToolActions
         */
        public static final TagKey<Item> TOOLS_CROSSBOW = cTag("tools/crossbow");
        /**
         * A tag containing all existing fishing rods. Do not use this tag for determining a tool's behavior.
         * Please use {@link ToolActions} instead for what action a tool can do.
         *
         * @see ToolAction
         * @see ToolActions
         */
        public static final TagKey<Item> TOOLS_FISHING_ROD = cTag("tools/fishing_rod");
        /**
         * A tag containing all existing throwable stick-like weapons like tridents.
         * Other tools such as throwing knives or boomerangs should not be put into
         * this tag and should be put into their own tool tags.
         * Do not use this tag for determining a tool's behavior.
         * Please use {@link ToolActions} instead for what action a tool can do.
         *
         * @see ToolAction
         * @see ToolActions
         */
        public static final TagKey<Item> TOOLS_TRIDENT = cTag("tools/trident");
        /**
         * A tag containing all existing shears. Do not use this tag for determining a tool's behavior.
         * Please use {@link ToolActions} instead for what action a tool can do.
         *
         * @see ToolAction
         * @see ToolActions
         */
        public static final TagKey<Item> TOOLS_SHEAR = cTag("tools/shear");
        /**
         * A tag containing all existing brushes. Do not use this tag for determining a tool's behavior.
         * Please use {@link ToolActions} instead for what action a tool can do.
         *
         * @see ToolAction
         * @see ToolActions
         */
        public static final TagKey<Item> TOOLS_BRUSH = cTag("tools/brush");
        /**
         * A tag containing all existing fire starting tools such as Flint and Steel.
         * Fire Charge is not a tool (no durability) and thus, does not go in this tag.
         * Please use {@link ToolActions} instead for what action a tool can do.
         *
         * @see ToolAction
         * @see ToolActions
         */
        public static final TagKey<Item> TOOLS_IGNITER = cTag("tools/igniter");
        /**
         * A tag containing all existing maces. Do not use this tag for determining a tool's behavior.
         * Please use {@link ToolActions} instead for what action a tool can do.
         *
         * @see ToolAction
         * @see ToolActions
         */
        public static final TagKey<Item> TOOLS_MACE = cTag("tools/mace");
        public static final TagKey<Item> TOOLS_WRENCH = cTag("tools/wrench");
        /**
         * A tag containing melee-based weapons for recipes and loot tables.
         * Tools are considered melee if they are intentionally intended to be used for melee attack as a primary purpose.
         * (In other words, Pickaxes are not melee weapons as they are not intended to be a weapon as a primary purpose)
         * Please use {@link ToolActions} instead for what action a tool can do.
         *
         * @see ToolAction
         * @see ToolActions
         */
        public static final TagKey<Item> MELEE_WEAPON_TOOLS = cTag("tools/melee_weapon");
        /**
         * A tag containing ranged-based weapons for recipes and loot tables.
         * Tools are considered ranged if they can damage entities beyond the weapon's and player's melee attack range.
         * Please use {@link ToolActions} instead for what action a tool can do.
         *
         * @see ToolAction
         * @see ToolActions
         */
        public static final TagKey<Item> RANGED_WEAPON_TOOLS = cTag("tools/ranged_weapon");
        /**
         * A tag containing mining-based tools for recipes and loot tables.
         * Do not use this tag for determining a tool's behavior in-code.
         * Please use {@link ToolActions} instead for what action a tool can do.
         *
         * @see ToolAction
         * @see ToolActions
         */
        public static final TagKey<Item> MINING_TOOL_TOOLS = cTag("tools/mining_tool");
        /**
         * Collects the 4 vanilla armor tags into one parent collection for ease.
         */
        public static final TagKey<Item> ARMORS = cTag("armors");
        public static final TagKey<Item> ARMORS_HORSE = cTag("armors/horse");
        public static final TagKey<Item> ARMORS_HUMANOID = cTag("armors/humanoid");
        public static final TagKey<Item> ARMORS_NAUTILUS = cTag("armors/nautilus");
        public static final TagKey<Item> ARMORS_WOLF = cTag("armors/wolf");
        /**
         * Collects the many enchantable tags into one parent collection for ease.
         */
        public static final TagKey<Item> ENCHANTABLES = cTag("enchantables");
        //endregion

        private static TagKey<Item> cTag(String name) {
            return ItemTags.create(Identifier.fromNamespaceAndPath("c", name));
        }

        private static TagKey<Item> forgeTag(String name) {
            return ItemTags.create(Identifier.fromNamespaceAndPath("forge", name));
        }
    }

    public static class Fluids {
        private static void init() {}

        //region `c` tags for common conventions
        // Note: Other loaders have additional `c` tags that are exclusive to their loader.
        //       Forge only adopts `c` tags that are common across all loaders.
        /**
         * Holds all fluids related to Beetroot Soup.<br></br>
         * (Standard unit for beetroot soup is 250mb per bowl)
         */
        public static final TagKey<Fluid> BEETROOT_SOUP = cTag("beetroot_soup");
        public static final TagKey<Fluid> EXPERIENCE = cTag("experience");
        /**
         * Holds all fluids that are gaseous at room temperature.
         */
        public static final TagKey<Fluid> GASEOUS = cTag("gaseous");
        /**
         * Holds all fluids related to Mushroom Stew.<br></br>
         * (Standard unit for mushroom stew is 250mb per bowl)
         */
        public static final TagKey<Fluid> MUSHROOM_STEW = cTag("mushroom_stew");
        /**
         * Holds all fluids related to potions. The effects of the potion fluid should be read from NBT.
         * The effects and color of the potion fluid should be read from {@link net.minecraft.core.component.DataComponents#POTION_CONTENTS}
         * component that people should be attaching to the fluidstack of this fluid.<br></br>
         * (Standard unit for potions is 250mb per bottle)
         */
        public static final TagKey<Fluid> POTION = cTag("potion");
        /**
         * Holds all fluids related to Rabbit Stew.<br></br>
         * (Standard unit for rabbit stew is 250mb per bowl)
         */
        public static final TagKey<Fluid> RABBIT_STEW = cTag("rabbit_stew");
        /**
         * Holds all fluids related to Suspicious Stew.
         * The effects of the suspicious stew fluid should be read from {@link net.minecraft.core.component.DataComponents#SUSPICIOUS_STEW_EFFECTS}
         * component that people should be attaching to the fluidstack of this fluid.<br></br>
         * (Standard unit for suspicious stew is 250mb per bowl)
         */
        public static final TagKey<Fluid> SUSPICIOUS_STEW = cTag("suspicious_stew");
        /**
         * Holds all fluids related to water.
         * This tag is done to help out multi-loader mods/datapacks where the vanilla water tag has attached behaviors outside Forge.
         */
        public static final TagKey<Fluid> WATER = cTag("water");
        /**
         * Holds all fluids related to lava.
         * This tag is done to help out multi-loader mods/datapacks where the vanilla lava tag has attached behaviors outside Forge.
         */
        public static final TagKey<Fluid> LAVA = cTag("lava");
        /**
         * Holds all fluids related to milk.
         */
        public static final TagKey<Fluid> MILK = cTag("milk");
        /**
         * Holds all fluids related to honey.<br></br>
         * (Standard unit for honey bottle is 250mb per bottle)
         */
        public static final TagKey<Fluid> HONEY = cTag("honey");
        /**
         * Tag that holds all fluids that recipe viewers should not show to users.
         */
        public static final TagKey<Fluid> HIDDEN_FROM_RECIPE_VIEWERS = cTag("hidden_from_recipe_viewers");
        //endregion

        private static TagKey<Fluid> cTag(String name) {
            return FluidTags.create(Identifier.fromNamespaceAndPath("c", name));
        }

        private static TagKey<Fluid> forgeTag(String name) {
            return FluidTags.create(Identifier.fromNamespaceAndPath("forge", name));
        }
    }

    public static class Enchantments {
        private static void init() {}

        //region `c` tags for common conventions
        // Note: Other loaders have additional `c` tags that are exclusive to their loader.
        //       Forge only adopts `c` tags that are common across all loaders.
        /**
         * A tag containing enchantments that increase the amount or
         * quality of drops from blocks, such as {@link net.minecraft.world.item.enchantment.Enchantments#FORTUNE}.
         */
        public static final TagKey<Enchantment> INCREASE_BLOCK_DROPS = cTag("increase_block_drops");
        /**
         * A tag containing enchantments that increase the amount or
         * quality of drops from entities, such as {@link net.minecraft.world.item.enchantment.Enchantments#LOOTING}.
         */
        public static final TagKey<Enchantment> INCREASE_ENTITY_DROPS = cTag("increase_entity_drops");
        /**
         * For enchantments that increase the damage dealt by an item.
         */
        public static final TagKey<Enchantment> WEAPON_DAMAGE_ENHANCEMENTS = cTag("weapon_damage_enhancements");
        /**
         * For enchantments that increase movement speed for entity wearing armor enchanted with it.
         */
        public static final TagKey<Enchantment> ENTITY_SPEED_ENHANCEMENTS = cTag("entity_speed_enhancements");
        /**
         * For enchantments that applies movement-based benefits unrelated to speed for the entity wearing armor enchanted with it.
         * Example: Reducing falling speeds ({@link net.minecraft.world.item.enchantment.Enchantments#FEATHER_FALLING}) or allowing walking on water ({@link net.minecraft.world.item.enchantment.Enchantments#FROST_WALKER})
         */
        public static final TagKey<Enchantment> ENTITY_AUXILIARY_MOVEMENT_ENHANCEMENTS = cTag("entity_auxiliary_movement_enhancements");
        /**
         * For enchantments that decrease damage taken or otherwise benefit, in regard to damage, the entity wearing armor enchanted with it.
         */
        public static final TagKey<Enchantment> ENTITY_DEFENSE_ENHANCEMENTS = cTag("entity_defense_enhancements");
        //endregion

        private static TagKey<Enchantment> cTag(String name) {
            return TagKey.create(Registries.ENCHANTMENT, Identifier.fromNamespaceAndPath("c", name));
        }
    }

    public static class Biomes {
        private static void init() {}

        //region `c` tags for common conventions
        // Note: Other loaders have additional `c` tags that are exclusive to their loader.
        //       Forge only adopts `c` tags that are common across all loaders.
        /**
         * For biomes that should not spawn monsters over time the normal way.
         * In other words, their Spawners and Spawn Cost entries have the monster category empty.
         * Example: Mushroom Biomes not having Zombies, Creepers, Skeleton, nor any other normal monsters.
         */
        public static final TagKey<Biome> NO_DEFAULT_MONSTERS = cTag("no_default_monsters");
        /**
         * Biomes that should not be locatable/selectable by modded biome-locating items or abilities.
         */
        public static final TagKey<Biome> HIDDEN_FROM_LOCATOR_SELECTION = cTag("hidden_from_locator_selection");

        public static final TagKey<Biome> IS_VOID = cTag("is_void");

        public static final TagKey<Biome> IS_HOT = cTag("is_hot");
        public static final TagKey<Biome> IS_HOT_OVERWORLD = cTag("is_hot/overworld");
        public static final TagKey<Biome> IS_HOT_NETHER = cTag("is_hot/nether");
        public static final TagKey<Biome> IS_HOT_END = cTag("is_hot/end");

        public static final TagKey<Biome> IS_COLD = cTag("is_cold");
        public static final TagKey<Biome> IS_COLD_NETHER = cTag("is_cold/nether");
        public static final TagKey<Biome> IS_COLD_OVERWORLD = cTag("is_cold/overworld");
        public static final TagKey<Biome> IS_COLD_END = cTag("is_cold/end");

        public static final TagKey<Biome> IS_SPARSE_VEGETATION = cTag("is_sparse_vegetation");
        public static final TagKey<Biome> IS_SPARSE_VEGETATION_OVERWORLD = cTag("is_sparse_vegetation/overworld");
        public static final TagKey<Biome> IS_SPARSE_VEGETATION_NETHER = cTag("is_sparse_vegetation/nether");
        public static final TagKey<Biome> IS_SPARSE_VEGETATION_END = cTag("is_sparse_vegetation/end");
        public static final TagKey<Biome> IS_DENSE_VEGETATION = cTag("is_dense_vegetation");
        public static final TagKey<Biome> IS_DENSE_VEGETATION_OVERWORLD = cTag("is_dense_vegetation/overworld");
        public static final TagKey<Biome> IS_DENSE_VEGETATION_NETHER = cTag("is_dense_vegetation/nether");
        public static final TagKey<Biome> IS_DENSE_VEGETATION_END = cTag("is_dense_vegetation/end");

        public static final TagKey<Biome> IS_WET = cTag("is_wet");
        public static final TagKey<Biome> IS_WET_OVERWORLD = cTag("is_wet/overworld");
        public static final TagKey<Biome> IS_WET_NETHER = cTag("is_wet/nether");
        public static final TagKey<Biome> IS_WET_END = cTag("is_wet/end");
        public static final TagKey<Biome> IS_DRY = cTag("is_dry");
        public static final TagKey<Biome> IS_DRY_OVERWORLD = cTag("is_dry/overworld");
        public static final TagKey<Biome> IS_DRY_NETHER = cTag("is_dry/nether");
        public static final TagKey<Biome> IS_DRY_END = cTag("is_dry/end");

        /**
         * Biomes that spawn in the Overworld.
         * (This is for people who want to tag their biomes without getting
         * side effects from {@link net.minecraft.tags.BiomeTags#IS_OVERWORLD}
         * <p></p>
         * NOTE: If you do not add to the vanilla Overworld tag, be sure to add to
         * {@link net.minecraft.tags.BiomeTags#HAS_STRONGHOLD} so some Strongholds do not go missing.)
         */
        public static final TagKey<Biome> IS_OVERWORLD = cTag("is_overworld");

        public static final TagKey<Biome> IS_CONIFEROUS_TREE = cTag("is_tree/coniferous");
        public static final TagKey<Biome> IS_SAVANNA_TREE = cTag("is_tree/savanna");
        public static final TagKey<Biome> IS_JUNGLE_TREE = cTag("is_tree/jungle");
        public static final TagKey<Biome> IS_DECIDUOUS_TREE = cTag("is_tree/deciduous");

        public static final TagKey<Biome> IS_DARK_FOREST = cTag("is_dark_forest");

        /**
         * Biomes that spawn as part of giant mountains.
         * (This is for people who want to tag their biomes without getting
         * side effects from {@link net.minecraft.tags.BiomeTags#IS_MOUNTAIN})
         */
        public static final TagKey<Biome> IS_MOUNTAIN = cTag("is_mountain");
        public static final TagKey<Biome> IS_MOUNTAIN_PEAK = cTag("is_mountain/peak");
        public static final TagKey<Biome> IS_MOUNTAIN_SLOPE = cTag("is_mountain/slope");

        /**
         * For temperate or warmer plains-like biomes.
         * For snowy plains-like biomes, see {@link #IS_SNOWY_PLAINS}.
         */
        public static final TagKey<Biome> IS_PLAINS = cTag("is_plains");
        /**
         * For snowy plains-like biomes.
         * For warmer plains-like biomes, see {@link #IS_PLAINS}.
         */
        public static final TagKey<Biome> IS_SNOWY_PLAINS = cTag("is_snowy_plains");
        /**
         * Biomes densely populated with deciduous trees.
         * (This is for people who want to tag their biomes without getting
         * side effects from {@link net.minecraft.tags.BiomeTags#IS_FOREST})
         */
        public static final TagKey<Biome> IS_FOREST = cTag("is_forest");
        public static final TagKey<Biome> IS_BIRCH_FOREST = cTag("is_birch_forest");
        public static final TagKey<Biome> IS_FLOWER_FOREST = cTag("is_flower_forest");
        /**
         * Biomes that spawn as a taiga.
         * (This is for people who want to tag their biomes without getting
         * side effects from {@link net.minecraft.tags.BiomeTags#IS_TAIGA})
         */
        public static final TagKey<Biome> IS_TAIGA = cTag("is_taiga");
        public static final TagKey<Biome> IS_OLD_GROWTH = cTag("is_old_growth");
        /**
         * Biomes that spawn as a hills biome. (Previously was called Extreme Hills biome in past)
         * (This is for people who want to tag their biomes without getting
         * side effects from {@link net.minecraft.tags.BiomeTags#IS_HILL})
         */
        public static final TagKey<Biome> IS_HILL = cTag("is_hill");
        public static final TagKey<Biome> IS_WINDSWEPT = cTag("is_windswept");
        /**
         * Biomes that spawn as a jungle.
         * (This is for people who want to tag their biomes without getting
         * side effects from {@link net.minecraft.tags.BiomeTags#IS_JUNGLE})
         */
        public static final TagKey<Biome> IS_JUNGLE = cTag("is_jungle");
        /**
         * Biomes that spawn as a savanna.
         * (This is for people who want to tag their biomes without getting
         * side effects from {@link net.minecraft.tags.BiomeTags#IS_SAVANNA})
         */
        public static final TagKey<Biome> IS_SAVANNA = cTag("is_savanna");
        public static final TagKey<Biome> IS_SWAMP = cTag("is_swamp");
        public static final TagKey<Biome> IS_DESERT = cTag("is_desert");
        /**
         * Biomes that spawn as a badlands.
         * (This is for people who want to tag their biomes without getting
         * side effects from {@link net.minecraft.tags.BiomeTags#IS_BADLANDS})
         */
        public static final TagKey<Biome> IS_BADLANDS = cTag("is_badlands");
        /**
         * Biomes that are dedicated to spawning on the shoreline of a body of water.
         * (This is for people who want to tag their biomes without getting
         * side effects from {@link net.minecraft.tags.BiomeTags#IS_BEACH})
         */
        public static final TagKey<Biome> IS_BEACH = cTag("is_beach");
        public static final TagKey<Biome> IS_STONY_SHORES = cTag("is_stony_shores");
        public static final TagKey<Biome> IS_MUSHROOM = cTag("is_mushroom");

        /**
         * Biomes that spawn as a river.
         * (This is for people who want to tag their biomes without getting
         * side effects from {@link net.minecraft.tags.BiomeTags#IS_RIVER})
         */
        public static final TagKey<Biome> IS_RIVER = cTag("is_river");
        /**
         * Biomes that spawn as part of the world's oceans.
         * (This is for people who want to tag their biomes without getting
         * side effects from {@link net.minecraft.tags.BiomeTags#IS_OCEAN})
         */
        public static final TagKey<Biome> IS_OCEAN = cTag("is_ocean");
        /**
         * Biomes that spawn as part of the world's oceans that have low depth.
         * (This is for people who want to tag their biomes without getting
         * side effects from {@link net.minecraft.tags.BiomeTags#IS_DEEP_OCEAN})
         */
        public static final TagKey<Biome> IS_DEEP_OCEAN = cTag("is_deep_ocean");
        public static final TagKey<Biome> IS_SHALLOW_OCEAN = cTag("is_shallow_ocean");

        public static final TagKey<Biome> IS_UNDERGROUND = cTag("is_underground");
        public static final TagKey<Biome> IS_CAVE = cTag("is_cave");

        /**
         * Biomes that lack any natural life or vegetation.
         * (Example, land destroyed and sterilized by nuclear weapons)
         */
        public static final TagKey<Biome> IS_WASTELAND = cTag("is_wasteland");
        /**
         * Biomes whose flora primarily consists of dead or decaying vegetation.
         */
        public static final TagKey<Biome> IS_DEAD = cTag("is_dead");
        /**
         * Biomes with a large amount of flowers.
         */
        public static final TagKey<Biome> IS_FLORAL = cTag("is_floral");
        /**
         * For biomes that contains lots of naturally spawned snow.
         * For biomes where lot of ice is present, see {@link #IS_ICY}.
         * Biome with lots of both snow and ice may be in both tags.
         */
        public static final TagKey<Biome> IS_SNOWY = cTag("is_snowy");
        /**
         * For land biomes where ice naturally spawns.
         * For biomes where snow alone spawns, see {@link #IS_SNOWY}.
         */
        public static final TagKey<Biome> IS_ICY = cTag("is_icy");
        /**
         * Biomes consisting primarily of water.
         */
        public static final TagKey<Biome> IS_AQUATIC = cTag("is_aquatic");
        /**
         * For water biomes where ice naturally spawns.
         * For biomes where snow alone spawns, see {@link #IS_SNOWY}.
         */
        public static final TagKey<Biome> IS_AQUATIC_ICY = cTag("is_aquatic_icy");

        /**
         * Biomes that spawn in the Nether.
         * (This is for people who want to tag their biomes without getting
         * side effects from {@link net.minecraft.tags.BiomeTags#IS_NETHER})
         */
        public static final TagKey<Biome> IS_NETHER = cTag("is_nether");
        public static final TagKey<Biome> IS_NETHER_FOREST = cTag("is_nether_forest");

        /**
         * Biomes that spawn in the End.
         * (This is for people who want to tag their biomes without getting
         * side effects from {@link net.minecraft.tags.BiomeTags#IS_END})
         */
        public static final TagKey<Biome> IS_END = cTag("is_end");
        /**
         * Biomes that spawn as part of the large islands outside the center island in The End dimension.
         */
        public static final TagKey<Biome> IS_OUTER_END_ISLAND = cTag("is_outer_end_island");

        public static final TagKey<Biome> IS_LUSH = cTag("is_lush");
        public static final TagKey<Biome> IS_MAGICAL = cTag("is_magical");
        public static final TagKey<Biome> IS_RARE = cTag("is_rare");
        public static final TagKey<Biome> IS_PLATEAU = cTag("is_plateau");
        /**
         * Biomes that are able to spawn sand-based blocks on the surface.
         */
        public static final TagKey<Biome> IS_SANDY = cTag("is_sandy");
        public static final TagKey<Biome> IS_SPOOKY = cTag("is_spooky");

        public static final TagKey<Biome> IS_TEMPERATE = cTag("is_temperate");
        public static final TagKey<Biome> IS_TEMPERATE_END = cTag("is_temperate/end");
        public static final TagKey<Biome> IS_TEMPERATE_NETHER = cTag("is_temperate/nether");
        public static final TagKey<Biome> IS_TEMPERATE_OVERWORLD = cTag("is_temperate/overworld");

        public static final TagKey<Biome> PRIMARY_WOOD_TYPE = cTag("primary_wood_type");
        public static final TagKey<Biome> PRIMARY_WOOD_TYPE_ACACIA = cTag("primary_wood_type/acacia");
        public static final TagKey<Biome> PRIMARY_WOOD_TYPE_BAMBOO = cTag("primary_wood_type/bamboo");
        public static final TagKey<Biome> PRIMARY_WOOD_TYPE_BIRCH = cTag("primary_wood_type/birch");
        public static final TagKey<Biome> PRIMARY_WOOD_TYPE_CHERRY = cTag("primary_wood_type/cherry");
        public static final TagKey<Biome> PRIMARY_WOOD_TYPE_CRIMSON = cTag("primary_wood_type/crimson");
        public static final TagKey<Biome> PRIMARY_WOOD_TYPE_DARK_OAK = cTag("primary_wood_type/dark_oak");
        public static final TagKey<Biome> PRIMARY_WOOD_TYPE_JUNGLE = cTag("primary_wood_type/jungle");
        public static final TagKey<Biome> PRIMARY_WOOD_TYPE_MANGROVE = cTag("primary_wood_type/mangrove");
        public static final TagKey<Biome> PRIMARY_WOOD_TYPE_OAK = cTag("primary_wood_type/oak");
        public static final TagKey<Biome> PRIMARY_WOOD_TYPE_PALE_OAK = cTag("primary_wood_type/pale_oak");
        public static final TagKey<Biome> PRIMARY_WOOD_TYPE_SPRUCE = cTag("primary_wood_type/spruce");
        public static final TagKey<Biome> PRIMARY_WOOD_TYPE_WARPED = cTag("primary_wood_type/warped");
        //endregion

        private static TagKey<Biome> cTag(String name) {
            return TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath("c", name));
        }

        private static TagKey<Biome> forgeTag(String name) {
            return TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath("forge", name));
        }
    }

    public static class Structures {
        private static void init() {}

        //region `c` tags for common conventions
        // Note: Other loaders have additional `c` tags that are exclusive to their loader.
        //       Forge only adopts `c` tags that are common across all loaders.
        /**
         * Structures that should not show up on minimaps or world map views from mods/sites.
         * No effect on vanilla map items.
         */
        public static final TagKey<Structure> HIDDEN_FROM_DISPLAYERS = cTag("hidden_from_displayers");

        /**
         * Structures that should not be locatable/selectable by modded structure-locating items or abilities.
         * No effect on vanilla map items.
         */
        public static final TagKey<Structure> HIDDEN_FROM_LOCATOR_SELECTION = cTag("hidden_from_locator_selection");
        //endregion

        private static TagKey<Structure> cTag(String name) {
            return TagKey.create(Registries.STRUCTURE, Identifier.fromNamespaceAndPath("c", name));
        }
    }
}
