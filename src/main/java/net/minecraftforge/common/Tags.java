/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
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
        Blocks.init();
        EntityTypes.init();
        Items.init();
        Fluids.init();
        Enchantments.init();
        Biomes.init();
        Structures.init();
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
        public static final TagKey<Block> STORAGE_BLOCKS_AMETHYST = forgeTag("storage_blocks/amethyst");
        public static final TagKey<Block> STORAGE_BLOCKS_QUARTZ = forgeTag("storage_blocks/quartz");

        public static final TagKey<Block> CHESTS_ENDER = forgeTag("chests/ender");
        public static final TagKey<Block> CHESTS_TRAPPED = forgeTag("chests/trapped");
        public static final TagKey<Block> CHORUS_ADDITIONALLY_GROWS_ON = forgeTag("chorus_additionally_grows_on");
        public static final TagKey<Block> COBBLESTONE_NORMAL = forgeTag("cobblestone/normal");
        public static final TagKey<Block> COBBLESTONE_INFESTED = forgeTag("cobblestone/infested");
        public static final TagKey<Block> COBBLESTONE_MOSSY = forgeTag("cobblestone/mossy");
        public static final TagKey<Block> COBBLESTONE_DEEPSLATE = forgeTag("cobblestone/deepslate");
        public static final TagKey<Block> END_STONES = forgeTag("end_stones");
        public static final TagKey<Block> FENCE_GATES = forgeTag("fence_gates");
        public static final TagKey<Block> FENCE_GATES_WOODEN = forgeTag("fence_gates/wooden");
        public static final TagKey<Block> FENCES = forgeTag("fences");
        public static final TagKey<Block> FENCES_NETHER_BRICK = forgeTag("fences/nether_brick");
        public static final TagKey<Block> FENCES_WOODEN = forgeTag("fences/wooden");

        public static final TagKey<Block> GRAVEL = forgeTag("gravel");
        public static final TagKey<Block> NETHERRACK = forgeTag("netherrack");
        /**
         * Blocks which are often replaced by deepslate ores, i.e. the ores in the tag {@link #ORES_IN_GROUND_DEEPSLATE}, during world generation
         */
        public static final TagKey<Block> ORE_BEARING_GROUND_DEEPSLATE = forgeTag("ore_bearing_ground/deepslate");
        /**
         * Blocks which are often replaced by netherrack ores, i.e. the ores in the tag {@link #ORES_IN_GROUND_NETHERRACK}, during world generation
         */
        public static final TagKey<Block> ORE_BEARING_GROUND_NETHERRACK = forgeTag("ore_bearing_ground/netherrack");
        /**
         * Blocks which are often replaced by stone ores, i.e. the ores in the tag {@link #ORES_IN_GROUND_STONE}, during world generation
         */
        public static final TagKey<Block> ORE_BEARING_GROUND_STONE = forgeTag("ore_bearing_ground/stone");
        /**
         * Ores which on average result in more than one resource worth of materials
         */
        public static final TagKey<Block> ORE_RATES_DENSE = forgeTag("ore_rates/dense");
        /**
         * Ores which on average result in one resource worth of materials
         */
        public static final TagKey<Block> ORE_RATES_SINGULAR = forgeTag("ore_rates/singular");
        /**
         * Ores which on average result in less than one resource worth of materials
         */
        public static final TagKey<Block> ORE_RATES_SPARSE = forgeTag("ore_rates/sparse");

        public static final TagKey<Block> ORES_COAL = forgeTag("ores/coal");
        public static final TagKey<Block> ORES_COPPER = forgeTag("ores/copper");
        public static final TagKey<Block> ORES_DIAMOND = forgeTag("ores/diamond");
        public static final TagKey<Block> ORES_EMERALD = forgeTag("ores/emerald");
        public static final TagKey<Block> ORES_GOLD = forgeTag("ores/gold");
        public static final TagKey<Block> ORES_IRON = forgeTag("ores/iron");
        public static final TagKey<Block> ORES_LAPIS = forgeTag("ores/lapis");
        public static final TagKey<Block> ORES_REDSTONE = forgeTag("ores/redstone");
        /**
         * Ores in deepslate (or in equivalent blocks in the tag {@link #ORE_BEARING_GROUND_DEEPSLATE}) which could logically use deepslate as recipe input or output
         */
        public static final TagKey<Block> ORES_IN_GROUND_DEEPSLATE = forgeTag("ores_in_ground/deepslate");
        /**
         * Ores in netherrack (or in equivalent blocks in the tag {@link #ORE_BEARING_GROUND_NETHERRACK}) which could logically use netherrack as recipe input or output
         */
        public static final TagKey<Block> ORES_IN_GROUND_NETHERRACK = forgeTag("ores_in_ground/netherrack");
        /**
         * Ores in stone (or in equivalent blocks in the tag {@link #ORE_BEARING_GROUND_STONE}) which could logically use stone as recipe input or output
         */
        public static final TagKey<Block> ORES_IN_GROUND_STONE = forgeTag("ores_in_ground/stone");
        public static final TagKey<Block> SAND = forgeTag("sand");
        public static final TagKey<Block> SAND_COLORLESS = forgeTag("sand/colorless");
        public static final TagKey<Block> SAND_RED = forgeTag("sand/red");
        //endregion

        //region `c` tags for common conventions
        // Note: Other loaders have additional `c` tags that are exclusive to their loader.
        //       Forge only adopts `c` tags that are common across all loaders.
        public static final TagKey<Block> BARRELS = cTag("barrels");
        public static final TagKey<Block> BARRELS_WOODEN = cTag("barrels/wooden");
        public static final TagKey<Block> BOOKSHELVES = cTag("bookshelves");
        /**
         * For blocks that are similar to amethyst where their budding block produces buds and cluster blocks
         */
        public static final TagKey<Block> BUDDING_BLOCKS = cTag("budding_blocks");
        /**
         * For blocks that are similar to amethyst where they have buddings forming from budding blocks
         */
        public static final TagKey<Block> BUDS = cTag("buds");
        public static final TagKey<Block> CHAINS = cTag("chains");
        public static final TagKey<Block> CHESTS = cTag("chests");
        public static final TagKey<Block> CHESTS_WOODEN = cTag("chests/wooden");
        /**
         * For blocks that are similar to amethyst where they have clusters forming from budding blocks
         */
        public static final TagKey<Block> CLUSTERS = cTag("clusters");
        public static final TagKey<Block> COBBLESTONES = cTag("cobblestones");
        public static final TagKey<Block> CONCRETES = cTag("concretes");

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

        public static final TagKey<Block> GLASS_BLOCKS = cTag("glass_blocks");
        public static final TagKey<Block> GLASS_BLOCKS_COLORLESS = cTag("glass_blocks/colorless");
        /**
         * Glass which is made from cheap resources like sand and only minor additional ingredients like dyes
         */
        public static final TagKey<Block> GLASS_BLOCKS_CHEAP = cTag("glass_blocks/cheap");
        public static final TagKey<Block> GLASS_BLOCKS_TINTED = cTag("glass_blocks/tinted");

        public static final TagKey<Block> GLASS_PANES = cTag("glass_panes");
        public static final TagKey<Block> GLASS_PANES_COLORLESS = cTag("glass_panes/colorless");
        public static final TagKey<Block> GLAZED_TERRACOTTAS = cTag("glazed_terracottas");

        /**
         * Tag that holds all blocks that recipe viewers should not show to users.
         * Recipe viewers may use this to automatically find the corresponding BlockItem to hide.
         */
        public static final TagKey<Block> HIDDEN_FROM_RECIPE_VIEWERS = cTag("hidden_from_recipe_viewers");
        public static final TagKey<Block> OBSIDIANS = cTag("obsidians");
        /**
         * For common obsidian that has no special quirks or behaviours - ideal for recipe use.
         * Crying Obsidian, for example, is a light block and harder to obtain. So it gets its own tag instead of being under normal tag.
         */
        public static final TagKey<Block> OBSIDIANS_NORMAL = cTag("obsidians/normal");
        public static final TagKey<Block> OBSIDIANS_CRYING = cTag("obsidians/crying");
        public static final TagKey<Block> ORES = cTag("ores");
        public static final TagKey<Block> ORES_NETHERITE_SCRAP = cTag("ores/netherite_scrap");
        public static final TagKey<Block> ORES_QUARTZ = cTag("ores/quartz");
        public static final TagKey<Block> PLAYER_WORKSTATIONS_CRAFTING_TABLES = cTag("player_workstations/crafting_tables");
        public static final TagKey<Block> PLAYER_WORKSTATIONS_FURNACES = cTag("player_workstations/furnaces");
        /**
         * Blocks should be included in this tag if their movement/relocation can cause serious issues such
         * as world corruption upon being moved or for balance reason where the block should not be able to be relocated.
         * Example: Chunk loaders or pipes where other mods that move blocks do not respect
         * {@link BlockBehaviour.BlockStateBase#getPistonPushReaction}.
         */
        public static final TagKey<Block> RELOCATION_NOT_SUPPORTED = cTag("relocation_not_supported");
        public static final TagKey<Block> ROPES = cTag("ropes");

        public static final TagKey<Block> SANDSTONE_BLOCKS = cTag("sandstone/blocks");
        public static final TagKey<Block> SANDSTONE_SLABS = cTag("sandstone/slabs");
        public static final TagKey<Block> SANDSTONE_STAIRS = cTag("sandstone/stairs");
        public static final TagKey<Block> SANDSTONE_RED_BLOCKS = cTag("sandstone/red_blocks");
        public static final TagKey<Block> SANDSTONE_RED_SLABS = cTag("sandstone/red_slabs");
        public static final TagKey<Block> SANDSTONE_RED_STAIRS = cTag("sandstone/red_stairs");
        public static final TagKey<Block> SANDSTONE_UNCOLORED_BLOCKS = cTag("sandstone/uncolored_blocks");
        public static final TagKey<Block> SANDSTONE_UNCOLORED_SLABS = cTag("sandstone/uncolored_slabs");
        public static final TagKey<Block> SANDSTONE_UNCOLORED_STAIRS = cTag("sandstone/uncolored_stairs");
        /**
         * Tag that holds all head based blocks such as Skeleton Skull or Player Head. (Named skulls to match minecraft:skulls item tag)
         */
        public static final TagKey<Block> SKULLS = cTag("skulls");
        /**
         * Natural stone-like blocks that can be used as a base ingredient in recipes that takes stone.
         */
        public static final TagKey<Block> STONES = cTag("stones");
        /**
         * A storage block is generally a block that has a recipe to craft a bulk of 1 kind of resource to a block
         * and has a mirror recipe to reverse the crafting with no loss in resources.
         * <p></p>
         * Honey Block is special in that the reversing recipe is not a perfect mirror of the crafting recipe
         * and so, it is considered a special case and not given a storage block tag.
         */
        public static final TagKey<Block> STORAGE_BLOCKS = cTag("storage_blocks");
        public static final TagKey<Block> STORAGE_BLOCKS_BONE_MEAL = cTag("storage_blocks/bone_meal");
        public static final TagKey<Block> STORAGE_BLOCKS_COAL = cTag("storage_blocks/coal");
        public static final TagKey<Block> STORAGE_BLOCKS_COPPER = cTag("storage_blocks/copper");
        public static final TagKey<Block> STORAGE_BLOCKS_DIAMOND = cTag("storage_blocks/diamond");
        public static final TagKey<Block> STORAGE_BLOCKS_DRIED_KELP = cTag("storage_blocks/dried_kelp");
        public static final TagKey<Block> STORAGE_BLOCKS_EMERALD = cTag("storage_blocks/emerald");
        public static final TagKey<Block> STORAGE_BLOCKS_GOLD = cTag("storage_blocks/gold");
        public static final TagKey<Block> STORAGE_BLOCKS_IRON = cTag("storage_blocks/iron");
        public static final TagKey<Block> STORAGE_BLOCKS_LAPIS = cTag("storage_blocks/lapis");
        public static final TagKey<Block> STORAGE_BLOCKS_NETHERITE = cTag("storage_blocks/netherite");
        public static final TagKey<Block> STORAGE_BLOCKS_RAW_COPPER = cTag("storage_blocks/raw_copper");
        public static final TagKey<Block> STORAGE_BLOCKS_RAW_GOLD = cTag("storage_blocks/raw_gold");
        public static final TagKey<Block> STORAGE_BLOCKS_RAW_IRON = cTag("storage_blocks/raw_iron");
        public static final TagKey<Block> STORAGE_BLOCKS_REDSTONE = cTag("storage_blocks/redstone");
        public static final TagKey<Block> STORAGE_BLOCKS_SLIME = cTag("storage_blocks/slime");
        public static final TagKey<Block> STORAGE_BLOCKS_WHEAT = cTag("storage_blocks/wheat");
        public static final TagKey<Block> STRIPPED_LOGS = cTag("stripped_logs");
        public static final TagKey<Block> STRIPPED_WOODS = cTag("stripped_woods");
        public static final TagKey<Block> VILLAGER_JOB_SITES = cTag("villager_job_sites");
        //endregion

        //region Redirect fields for improved backward-compatibility
        // TODO: Remove backwards compat redirect fields in 1.22
        /** @deprecated Use {@link #COBBLESTONES} instead */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Block> COBBLESTONE = COBBLESTONES;

        /** @deprecated Use {@link #GLASS_BLOCKS_CHEAP} instead */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Block> GLASS_SILICA = GLASS_BLOCKS_CHEAP;
        //endregion
        
        //region Forge tags that now map to combining multiple `c` tags
        // Kept for binary compatibility
        // TODO: Remove these in 1.22
        /** @deprecated Use {@link #GLASS_BLOCKS} and/or {@link #GLASS_PANES} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Block> GLASS = forgeTag("glass");
        /** @deprecated Use {@link #DYED_BLACK} combined with {@link #GLASS_BLOCKS} and/or {@link #GLASS_PANES} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Block> GLASS_BLACK = forgeTag("glass/black");
        /** @deprecated Use {@link #DYED_BLUE} combined with {@link #GLASS_BLOCKS} and/or {@link #GLASS_PANES} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Block> GLASS_BLUE = forgeTag("glass/blue");
        /** @deprecated Use {@link #DYED_BROWN} combined with {@link #GLASS_BLOCKS} and/or {@link #GLASS_PANES} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Block> GLASS_BROWN = forgeTag("glass/brown");
        /** @deprecated Use {@link #GLASS_BLOCKS_COLORLESS} and/or {@link #GLASS_PANES_COLORLESS} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Block> GLASS_COLORLESS = forgeTag("glass/colorless");
        /** @deprecated Use {@link #DYED_CYAN} combined with {@link #GLASS_BLOCKS} and/or {@link #GLASS_PANES} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Block> GLASS_CYAN = forgeTag("glass/cyan");
        /** @deprecated Use {@link #DYED_GRAY} combined with {@link #GLASS_BLOCKS} and/or {@link #GLASS_PANES} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Block> GLASS_GRAY = forgeTag("glass/gray");
        /** @deprecated Use {@link #DYED_GREEN} combined with {@link #GLASS_BLOCKS} and/or {@link #GLASS_PANES} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Block> GLASS_GREEN = forgeTag("glass/green");
        /** @deprecated Use {@link #DYED_LIGHT_BLUE} combined with {@link #GLASS_BLOCKS} and/or {@link #GLASS_PANES} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Block> GLASS_LIGHT_BLUE = forgeTag("glass/light_blue");
        /** @deprecated Use {@link #DYED_LIGHT_GRAY} combined with {@link #GLASS_BLOCKS} and/or {@link #GLASS_PANES} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Block> GLASS_LIGHT_GRAY = forgeTag("glass/light_gray");
        /** @deprecated Use {@link #DYED_LIME} combined with {@link #GLASS_BLOCKS} and/or {@link #GLASS_PANES} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Block> GLASS_LIME = forgeTag("glass/lime");
        /** @deprecated Use {@link #DYED_MAGENTA} combined with {@link #GLASS_BLOCKS} and/or {@link #GLASS_PANES} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Block> GLASS_MAGENTA = forgeTag("glass/magenta");
        /** @deprecated Use {@link #DYED_ORANGE} combined with {@link #GLASS_BLOCKS} and/or {@link #GLASS_PANES} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Block> GLASS_ORANGE = forgeTag("glass/orange");
        /** @deprecated Use {@link #DYED_PINK} combined with {@link #GLASS_BLOCKS} and/or {@link #GLASS_PANES} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Block> GLASS_PINK = forgeTag("glass/pink");
        /** @deprecated Use {@link #DYED_PURPLE} combined with {@link #GLASS_BLOCKS} and/or {@link #GLASS_PANES} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Block> GLASS_PURPLE = forgeTag("glass/purple");
        /** @deprecated Use {@link #DYED_RED} combined with {@link #GLASS_BLOCKS} and/or {@link #GLASS_PANES} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Block> GLASS_RED = forgeTag("glass/red");
        /** @deprecated Use {@link #DYED_WHITE} combined with {@link #GLASS_BLOCKS} and/or {@link #GLASS_PANES} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Block> GLASS_WHITE = forgeTag("glass/white");
        /** @deprecated Use {@link #DYED_YELLOW} combined with {@link #GLASS_BLOCKS} and/or {@link #GLASS_PANES} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Block> GLASS_YELLOW = forgeTag("glass/yellow");
        /** @deprecated Use {@link #DYED} combined with {@link #GLASS_BLOCKS} and/or {@link #GLASS_PANES} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Block> STAINED_GLASS = forgeTag("stained_glass");

        /** @deprecated Use {@link #DYED_BLACK} combined with {@link #GLASS_PANES} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Block> GLASS_PANES_BLACK = forgeTag("glass_panes/black");
        /** @deprecated Use {@link #DYED_BLUE} combined with {@link #GLASS_PANES} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Block> GLASS_PANES_BLUE = forgeTag("glass_panes/blue");
        /** @deprecated Use {@link #DYED_BROWN} combined with {@link #GLASS_PANES} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Block> GLASS_PANES_BROWN = forgeTag("glass_panes/brown");
        /** @deprecated Use {@link #DYED_CYAN} combined with {@link #GLASS_PANES} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Block> GLASS_PANES_CYAN = forgeTag("glass_panes/cyan");
        /** @deprecated Use {@link #DYED_GRAY} combined with {@link #GLASS_PANES} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Block> GLASS_PANES_GRAY = forgeTag("glass_panes/gray");
        /** @deprecated Use {@link #DYED_GREEN} combined with {@link #GLASS_PANES} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Block> GLASS_PANES_GREEN = forgeTag("glass_panes/green");
        /** @deprecated Use {@link #DYED_LIGHT_BLUE} combined with {@link #GLASS_PANES} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Block> GLASS_PANES_LIGHT_BLUE = forgeTag("glass_panes/light_blue");
        /** @deprecated Use {@link #DYED_LIGHT_GRAY} combined with {@link #GLASS_PANES} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Block> GLASS_PANES_LIGHT_GRAY = forgeTag("glass_panes/light_gray");
        /** @deprecated Use {@link #DYED_LIME} combined with {@link #GLASS_PANES} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Block> GLASS_PANES_LIME = forgeTag("glass_panes/lime");
        /** @deprecated Use {@link #DYED_MAGENTA} combined with {@link #GLASS_PANES} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Block> GLASS_PANES_MAGENTA = forgeTag("glass_panes/magenta");
        /** @deprecated Use {@link #DYED_ORANGE} combined with {@link #GLASS_PANES} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Block> GLASS_PANES_ORANGE = forgeTag("glass_panes/orange");
        /** @deprecated Use {@link #DYED_PINK} combined with {@link #GLASS_PANES} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Block> GLASS_PANES_PINK = forgeTag("glass_panes/pink");
        /** @deprecated Use {@link #DYED_PURPLE} combined with {@link #GLASS_PANES} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Block> GLASS_PANES_PURPLE = forgeTag("glass_panes/purple");
        /** @deprecated Use {@link #DYED_RED} combined with {@link #GLASS_PANES} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Block> GLASS_PANES_RED = forgeTag("glass_panes/red");
        /** @deprecated Use {@link #DYED_WHITE} combined with {@link #GLASS_PANES} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Block> GLASS_PANES_WHITE = forgeTag("glass_panes/white");
        /** @deprecated Use {@link #DYED_YELLOW} combined with {@link #GLASS_PANES} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Block> GLASS_PANES_YELLOW = forgeTag("glass_panes/yellow");
        /** @deprecated Use {@link #DYED} combined with {@link #GLASS_PANES} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Block> STAINED_GLASS_PANES = forgeTag("stained_glass_panes");
        //endregion

        private static TagKey<Block> cTag(String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", name));
        }

        private static TagKey<Block> forgeTag(String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath("forge", name));
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
            return TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath("c", name));
        }
    }

    public static class Items {
        private static void init() {}

        //region `forge` tags for Forge-specific tags
        public static final TagKey<Item> BONES = forgeTag("bones");
        public static final TagKey<Item> CHESTS_ENDER = forgeTag("chests/ender");
        public static final TagKey<Item> CHESTS_TRAPPED = forgeTag("chests/trapped");
        public static final TagKey<Item> COBBLESTONE_NORMAL = forgeTag("cobblestone/normal");
        public static final TagKey<Item> COBBLESTONE_INFESTED = forgeTag("cobblestone/infested");
        public static final TagKey<Item> COBBLESTONE_MOSSY = forgeTag("cobblestone/mossy");
        public static final TagKey<Item> COBBLESTONE_DEEPSLATE = forgeTag("cobblestone/deepslate");
        public static final TagKey<Item> EGGS = forgeTag("eggs");
        public static final TagKey<Item> END_STONES = forgeTag("end_stones");
        public static final TagKey<Item> FEATHERS = forgeTag("feathers");
        public static final TagKey<Item> FENCE_GATES = forgeTag("fence_gates");
        public static final TagKey<Item> FENCE_GATES_WOODEN = forgeTag("fence_gates/wooden");
        public static final TagKey<Item> FENCES = forgeTag("fences");
        public static final TagKey<Item> FENCES_NETHER_BRICK = forgeTag("fences/nether_brick");
        public static final TagKey<Item> FENCES_WOODEN = forgeTag("fences/wooden");
        public static final TagKey<Item> GRAVEL = forgeTag("gravel");
        public static final TagKey<Item> GUNPOWDER = forgeTag("gunpowder");
        public static final TagKey<Item> MUSHROOMS = forgeTag("mushrooms");
        public static final TagKey<Item> NETHER_STARS = forgeTag("nether_stars");
        public static final TagKey<Item> NETHERRACK = forgeTag("netherrack");
        /**
         * Blocks which are often replaced by deepslate ores, i.e. the ores in the tag {@link #ORES_IN_GROUND_DEEPSLATE}, during world generation
         */
        public static final TagKey<Item> ORE_BEARING_GROUND_DEEPSLATE = forgeTag("ore_bearing_ground/deepslate");
        /**
         * Blocks which are often replaced by netherrack ores, i.e. the ores in the tag {@link #ORES_IN_GROUND_NETHERRACK}, during world generation
         */
        public static final TagKey<Item> ORE_BEARING_GROUND_NETHERRACK = forgeTag("ore_bearing_ground/netherrack");
        /**
         * Blocks which are often replaced by stone ores, i.e. the ores in the tag {@link #ORES_IN_GROUND_STONE}, during world generation
         */
        public static final TagKey<Item> ORE_BEARING_GROUND_STONE = forgeTag("ore_bearing_ground/stone");
        /**
         * Ores which on average result in more than one resource worth of materials
         */
        public static final TagKey<Item> ORE_RATES_DENSE = forgeTag("ore_rates/dense");
        /**
         * Ores which on average result in one resource worth of materials
         */
        public static final TagKey<Item> ORE_RATES_SINGULAR = forgeTag("ore_rates/singular");
        /**
         * Ores which on average result in less than one resource worth of materials
         */
        public static final TagKey<Item> ORE_RATES_SPARSE = forgeTag("ore_rates/sparse");
        public static final TagKey<Item> ORES_COAL = forgeTag("ores/coal");
        public static final TagKey<Item> ORES_COPPER = forgeTag("ores/copper");
        public static final TagKey<Item> ORES_DIAMOND = forgeTag("ores/diamond");
        public static final TagKey<Item> ORES_EMERALD = forgeTag("ores/emerald");
        public static final TagKey<Item> ORES_GOLD = forgeTag("ores/gold");
        public static final TagKey<Item> ORES_IRON = forgeTag("ores/iron");
        public static final TagKey<Item> ORES_LAPIS = forgeTag("ores/lapis");
        public static final TagKey<Item> ORES_REDSTONE = forgeTag("ores/redstone");
        /**
         * Ores in deepslate (or in equivalent blocks in the tag {@link #ORE_BEARING_GROUND_DEEPSLATE}) which could logically use deepslate as recipe input or output
         */
        public static final TagKey<Item> ORES_IN_GROUND_DEEPSLATE = forgeTag("ores_in_ground/deepslate");
        /**
         * Ores in netherrack (or in equivalent blocks in the tag {@link #ORE_BEARING_GROUND_NETHERRACK}) which could logically use netherrack as recipe input or output
         */
        public static final TagKey<Item> ORES_IN_GROUND_NETHERRACK = forgeTag("ores_in_ground/netherrack");
        /**
         * Ores in stone (or in equivalent blocks in the tag {@link #ORE_BEARING_GROUND_STONE}) which could logically use stone as recipe input or output
         */
        public static final TagKey<Item> ORES_IN_GROUND_STONE = forgeTag("ores_in_ground/stone");

        public static final TagKey<Item> SAND = forgeTag("sand");
        public static final TagKey<Item> SAND_COLORLESS = forgeTag("sand/colorless");
        public static final TagKey<Item> SAND_RED = forgeTag("sand/red");

        public static final TagKey<Item> SEEDS = forgeTag("seeds");
        public static final TagKey<Item> SEEDS_BEETROOT = forgeTag("seeds/beetroot");
        public static final TagKey<Item> SEEDS_MELON = forgeTag("seeds/melon");
        public static final TagKey<Item> SEEDS_PUMPKIN = forgeTag("seeds/pumpkin");
        public static final TagKey<Item> SEEDS_WHEAT = forgeTag("seeds/wheat");

        /**
         * Controls what items can be consumed for enchanting such as Enchanting Tables.
         * This tag defaults to {@link net.minecraft.world.item.Items#LAPIS_LAZULI} when not present in any datapacks, including forge client on vanilla server
         */
        public static final TagKey<Item> ENCHANTING_FUELS = forgeTag("enchanting_fuels");

        public static final TagKey<Item> STORAGE_BLOCKS_AMETHYST = forgeTag("storage_blocks/amethyst");
        public static final TagKey<Item> STORAGE_BLOCKS_QUARTZ = forgeTag("storage_blocks/quartz");

        /** Deprecated for removal as misleading, because it also contained prismarine shards */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Item> DUSTS_PRISMARINE = forgeTag("dusts/prismarine");

        //endregion

        //region `c` tags for common conventions
        // Note: Other loaders have additional `c` tags that are exclusive to their loader.
        //       Forge only adopts `c` tags that are common across all loaders.
        public static final TagKey<Item> BARRELS = cTag("barrels");
        public static final TagKey<Item> BARRELS_WOODEN = cTag("barrels/wooden");
        public static final TagKey<Item> BOOKSHELVES = cTag("bookshelves");
        public static final TagKey<Item> BRICKS = cTag("bricks");
        public static final TagKey<Item> BRICKS_NORMAL = cTag("bricks/normal");
        public static final TagKey<Item> BRICKS_NETHER = cTag("bricks/nether");
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
        public static final TagKey<Item> BUDDING_BLOCKS = cTag("budding_blocks");
        /**
         * For blocks that are similar to amethyst where they have buddings forming from budding blocks
         */
        public static final TagKey<Item> BUDS = cTag("buds");
        public static final TagKey<Item> CHAINS = cTag("chains");
        public static final TagKey<Item> CHESTS = cTag("chests");
        public static final TagKey<Item> CHESTS_WOODEN = cTag("chests/wooden");
        public static final TagKey<Item> COBBLESTONES = cTag("cobblestones");
        public static final TagKey<Item> CONCRETES = cTag("concretes");
        /**
         * Block tag equivalent is {@link BlockTags#CONCRETE_POWDER}
         */
        public static final TagKey<Item> CONCRETE_POWDERS = cTag("concrete_powders");
        /**
         * For blocks that are similar to amethyst where they have clusters forming from budding blocks
         */
        public static final TagKey<Item> CLUSTERS = cTag("clusters");
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

        public static final TagKey<Item> ENDER_PEARLS = cTag("ender_pearls");
        /**
         * For bonemeal-like items that can grow plants.
         */
        public static final TagKey<Item> FERTILIZERS = cTag("fertilizers");
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

        public static final TagKey<Item> GLASS_BLOCKS = cTag("glass_blocks");
        public static final TagKey<Item> GLASS_BLOCKS_COLORLESS = cTag("glass_blocks/colorless");
        /**
         * Glass which is made from cheap resources like sand and only minor additional ingredients like dyes
         */
        public static final TagKey<Item> GLASS_BLOCKS_CHEAP = cTag("glass_blocks/cheap");
        public static final TagKey<Item> GLASS_BLOCKS_TINTED = cTag("glass_blocks/tinted");

        public static final TagKey<Item> GLASS_PANES = cTag("glass_panes");
        public static final TagKey<Item> GLASS_PANES_COLORLESS = cTag("glass_panes/colorless");
        public static final TagKey<Item> GLAZED_TERRACOTTAS = cTag("glazed_terracottas");

        /**
         * Tag that holds all items that recipe viewers should not show to users.
         */
        public static final TagKey<Item> HIDDEN_FROM_RECIPE_VIEWERS = cTag("hidden_from_recipe_viewers");
        public static final TagKey<Item> OBSIDIANS = cTag("obsidians");
        /**
         * For common obsidian that has no special quirks or behaviours - ideal for recipe use.
         * Crying Obsidian, for example, is a light block and harder to obtain. So it gets its own tag instead of being under normal tag.
         */
        public static final TagKey<Item> OBSIDIANS_NORMAL = cTag("obsidians/normal");
        public static final TagKey<Item> OBSIDIANS_CRYING = cTag("obsidians/crying");
        public static final TagKey<Item> INGOTS = cTag("ingots");
        public static final TagKey<Item> INGOTS_COPPER = cTag("ingots/copper");
        public static final TagKey<Item> INGOTS_GOLD = cTag("ingots/gold");
        public static final TagKey<Item> INGOTS_IRON = cTag("ingots/iron");
        public static final TagKey<Item> INGOTS_NETHERITE = cTag("ingots/netherite");
        public static final TagKey<Item> LEATHERS = cTag("leathers");
        /**
         * For music disc-like materials to be used in recipes.
         * A pancake with a JUKEBOX_PLAYABLE component attached to play in Jukeboxes as an Easter Egg is not a music disc and would not go in this tag.
         */
        public static final TagKey<Item> MUSIC_DISCS = cTag("music_discs");
        public static final TagKey<Item> NUGGETS = cTag("nuggets");
        public static final TagKey<Item> NUGGETS_GOLD = cTag("nuggets/gold");
        public static final TagKey<Item> NUGGETS_IRON = cTag("nuggets/iron");
        public static final TagKey<Item> ORES = cTag("ores");
        public static final TagKey<Item> ORES_NETHERITE_SCRAP = cTag("ores/netherite_scrap");
        public static final TagKey<Item> ORES_QUARTZ = cTag("ores/quartz");
        public static final TagKey<Item> PLAYER_WORKSTATIONS_CRAFTING_TABLES = cTag("player_workstations/crafting_tables");
        public static final TagKey<Item> PLAYER_WORKSTATIONS_FURNACES = cTag("player_workstations/furnaces");
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
        public static final TagKey<Item> ROPES = cTag("ropes");

        public static final TagKey<Item> SANDSTONE_BLOCKS = cTag("sandstone/blocks");
        public static final TagKey<Item> SANDSTONE_SLABS = cTag("sandstone/slabs");
        public static final TagKey<Item> SANDSTONE_STAIRS = cTag("sandstone/stairs");
        public static final TagKey<Item> SANDSTONE_RED_BLOCKS = cTag("sandstone/red_blocks");
        public static final TagKey<Item> SANDSTONE_RED_SLABS = cTag("sandstone/red_slabs");
        public static final TagKey<Item> SANDSTONE_RED_STAIRS = cTag("sandstone/red_stairs");
        public static final TagKey<Item> SANDSTONE_UNCOLORED_BLOCKS = cTag("sandstone/uncolored_blocks");
        public static final TagKey<Item> SANDSTONE_UNCOLORED_SLABS = cTag("sandstone/uncolored_slabs");
        public static final TagKey<Item> SANDSTONE_UNCOLORED_STAIRS = cTag("sandstone/uncolored_stairs");

        /**
         * Block tag equivalent is {@link BlockTags#SHULKER_BOXES}
         */
        public static final TagKey<Item> SHULKER_BOXES = cTag("shulker_boxes");
        public static final TagKey<Item> SLIME_BALLS = cTag("slime_balls");
        /**
         * Natural stone-like blocks that can be used as a base ingredient in recipes that takes stone.
         */
        public static final TagKey<Item> STONES = cTag("stones");
        /**
         * A storage block is generally a block that has a recipe to craft a bulk of 1 kind of resource to a block
         * and has a mirror recipe to reverse the crafting with no loss in resources.
         * <p></p>
         * Honey Block is special in that the reversing recipe is not a perfect mirror of the crafting recipe
         * and so, it is considered a special case and not given a storage block tag.
         */
        public static final TagKey<Item> STORAGE_BLOCKS = cTag("storage_blocks");
        public static final TagKey<Item> STORAGE_BLOCKS_BONE_MEAL = cTag("storage_blocks/bone_meal");
        public static final TagKey<Item> STORAGE_BLOCKS_COAL = cTag("storage_blocks/coal");
        public static final TagKey<Item> STORAGE_BLOCKS_COPPER = cTag("storage_blocks/copper");
        public static final TagKey<Item> STORAGE_BLOCKS_DIAMOND = cTag("storage_blocks/diamond");
        public static final TagKey<Item> STORAGE_BLOCKS_DRIED_KELP = cTag("storage_blocks/dried_kelp");
        public static final TagKey<Item> STORAGE_BLOCKS_EMERALD = cTag("storage_blocks/emerald");
        public static final TagKey<Item> STORAGE_BLOCKS_GOLD = cTag("storage_blocks/gold");
        public static final TagKey<Item> STORAGE_BLOCKS_IRON = cTag("storage_blocks/iron");
        public static final TagKey<Item> STORAGE_BLOCKS_LAPIS = cTag("storage_blocks/lapis");
        public static final TagKey<Item> STORAGE_BLOCKS_NETHERITE = cTag("storage_blocks/netherite");
        public static final TagKey<Item> STORAGE_BLOCKS_RAW_COPPER = cTag("storage_blocks/raw_copper");
        public static final TagKey<Item> STORAGE_BLOCKS_RAW_GOLD = cTag("storage_blocks/raw_gold");
        public static final TagKey<Item> STORAGE_BLOCKS_RAW_IRON = cTag("storage_blocks/raw_iron");
        public static final TagKey<Item> STORAGE_BLOCKS_REDSTONE = cTag("storage_blocks/redstone");
        public static final TagKey<Item> STORAGE_BLOCKS_SLIME = cTag("storage_blocks/slime");
        public static final TagKey<Item> STORAGE_BLOCKS_WHEAT = cTag("storage_blocks/wheat");
        public static final TagKey<Item> STRINGS = cTag("strings");
        public static final TagKey<Item> STRIPPED_LOGS = cTag("stripped_logs");
        public static final TagKey<Item> STRIPPED_WOODS = cTag("stripped_woods");
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
         * A tag containing all existing spears. Other tools such as throwing knives or boomerangs
         * should not be put into this tag and should be put into their own tool tags.
         * Do not use this tag for determining a tool's behavior.
         * Please use {@link ToolActions} instead for what action a tool can do.
         *
         * @see ToolAction
         * @see ToolActions
         */
        public static final TagKey<Item> TOOLS_SPEAR = cTag("tools/spear");
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
        /**
         * Collects the many enchantable tags into one parent collection for ease.
         */
        public static final TagKey<Item> ENCHANTABLES = cTag("enchantables");
        //endregion

        //region Redirect fields for improved backward-compatibility
        // TODO: Remove backwards compat redirect fields in 1.22
        /** @deprecated Use {@link #COBBLESTONES} instead */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Item> COBBLESTONE = COBBLESTONES;
        /** @deprecated Use {@link #LEATHERS} instead */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Item> LEATHER = LEATHERS;
        /** @deprecated Use {@link #SANDSTONE_BLOCKS} instead */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Item> SANDSTONE = SANDSTONE_BLOCKS;
        /** @deprecated Use {@link #STONES} instead */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Item> STONE = STONES;
        /** @deprecated Use {@link #TOOLS_SHEAR} instead */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Item> SHEARS = TOOLS_SHEAR;
        /** @deprecated Use {@link #TOOLS_SPEAR} instead */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Item> TOOLS_TRIDENTS = TOOLS_SPEAR;
        /** @deprecated Use {@link #STRINGS} instead */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Item> STRING = STRINGS;
        //endregion

        //region Forge tags that now map to combining multiple `c` tags
        // Kept for binary compatibility
        // TODO: Remove these in 1.22
        /** @deprecated Use {@link #GLASS_BLOCKS} and/or {@link #GLASS_PANES} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Item> GLASS = forgeTag("glass");
        /** @deprecated Use {@link #DYED_BLACK} combined with {@link #GLASS_BLOCKS} and/or {@link #GLASS_PANES} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Item> GLASS_BLACK = forgeTag("glass/black");
        /** @deprecated Use {@link #DYED_BLUE} combined with {@link #GLASS_BLOCKS} and/or {@link #GLASS_PANES} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Item> GLASS_BLUE = forgeTag("glass/blue");
        /** @deprecated Use {@link #DYED_BROWN} combined with {@link #GLASS_BLOCKS} and/or {@link #GLASS_PANES} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Item> GLASS_BROWN = forgeTag("glass/brown");
        /** @deprecated Use {@link #GLASS_BLOCKS_COLORLESS} and/or {@link #GLASS_PANES_COLORLESS} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Item> GLASS_COLORLESS = forgeTag("glass/colorless");
        /** @deprecated Use {@link #DYED_CYAN} combined with {@link #GLASS_BLOCKS} and/or {@link #GLASS_PANES} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Item> GLASS_CYAN = forgeTag("glass/cyan");
        /** @deprecated Use {@link #DYED_GRAY} combined with {@link #GLASS_BLOCKS} and/or {@link #GLASS_PANES} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Item> GLASS_GRAY = forgeTag("glass/gray");
        /** @deprecated Use {@link #DYED_GREEN} combined with {@link #GLASS_BLOCKS} and/or {@link #GLASS_PANES} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Item> GLASS_GREEN = forgeTag("glass/green");
        /** @deprecated Use {@link #DYED_LIGHT_BLUE} combined with {@link #GLASS_BLOCKS} and/or {@link #GLASS_PANES} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Item> GLASS_LIGHT_BLUE = forgeTag("glass/light_blue");
        /** @deprecated Use {@link #DYED_LIGHT_GRAY} combined with {@link #GLASS_BLOCKS} and/or {@link #GLASS_PANES} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Item> GLASS_LIGHT_GRAY = forgeTag("glass/light_gray");
        /** @deprecated Use {@link #DYED_LIME} combined with {@link #GLASS_BLOCKS} and/or {@link #GLASS_PANES} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Item> GLASS_LIME = forgeTag("glass/lime");
        /** @deprecated Use {@link #DYED_MAGENTA} combined with {@link #GLASS_BLOCKS} and/or {@link #GLASS_PANES} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Item> GLASS_MAGENTA = forgeTag("glass/magenta");
        /** @deprecated Use {@link #DYED_ORANGE} combined with {@link #GLASS_BLOCKS} and/or {@link #GLASS_PANES} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Item> GLASS_ORANGE = forgeTag("glass/orange");
        /** @deprecated Use {@link #DYED_PINK} combined with {@link #GLASS_BLOCKS} and/or {@link #GLASS_PANES} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Item> GLASS_PINK = forgeTag("glass/pink");
        /** @deprecated Use {@link #DYED_PURPLE} combined with {@link #GLASS_BLOCKS} and/or {@link #GLASS_PANES} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Item> GLASS_PURPLE = forgeTag("glass/purple");
        /** @deprecated Use {@link #DYED_RED} combined with {@link #GLASS_BLOCKS} and/or {@link #GLASS_PANES} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Item> GLASS_RED = forgeTag("glass/red");
        /** @deprecated Use {@link #DYED_WHITE} combined with {@link #GLASS_BLOCKS} and/or {@link #GLASS_PANES} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Item> GLASS_WHITE = forgeTag("glass/white");
        /** @deprecated Use {@link #DYED_YELLOW} combined with {@link #GLASS_BLOCKS} and/or {@link #GLASS_PANES} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Item> GLASS_YELLOW = forgeTag("glass/yellow");
        /** @deprecated Use {@link #DYED} combined with {@link #GLASS_BLOCKS} and/or {@link #GLASS_PANES} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Item> STAINED_GLASS = forgeTag("stained_glass");

        /** @deprecated Use {@link #DYED_BLACK} combined with {@link #GLASS_PANES} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Item> GLASS_PANES_BLACK = forgeTag("glass_panes/black");
        /** @deprecated Use {@link #DYED_BLUE} combined with {@link #GLASS_PANES} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Item> GLASS_PANES_BLUE = forgeTag("glass_panes/blue");
        /** @deprecated Use {@link #DYED_BROWN} combined with {@link #GLASS_PANES} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Item> GLASS_PANES_BROWN = forgeTag("glass_panes/brown");
        /** @deprecated Use {@link #DYED_CYAN} combined with {@link #GLASS_PANES} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Item> GLASS_PANES_CYAN = forgeTag("glass_panes/cyan");
        /** @deprecated Use {@link #DYED_GRAY} combined with {@link #GLASS_PANES} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Item> GLASS_PANES_GRAY = forgeTag("glass_panes/gray");
        /** @deprecated Use {@link #DYED_GREEN} combined with {@link #GLASS_PANES} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Item> GLASS_PANES_GREEN = forgeTag("glass_panes/green");
        /** @deprecated Use {@link #DYED_LIGHT_BLUE} combined with {@link #GLASS_PANES} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Item> GLASS_PANES_LIGHT_BLUE = forgeTag("glass_panes/light_blue");
        /** @deprecated Use {@link #DYED_LIGHT_GRAY} combined with {@link #GLASS_PANES} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Item> GLASS_PANES_LIGHT_GRAY = forgeTag("glass_panes/light_gray");
        /** @deprecated Use {@link #DYED_LIME} combined with {@link #GLASS_PANES} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Item> GLASS_PANES_LIME = forgeTag("glass_panes/lime");
        /** @deprecated Use {@link #DYED_MAGENTA} combined with {@link #GLASS_PANES} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Item> GLASS_PANES_MAGENTA = forgeTag("glass_panes/magenta");
        /** @deprecated Use {@link #DYED_ORANGE} combined with {@link #GLASS_PANES} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Item> GLASS_PANES_ORANGE = forgeTag("glass_panes/orange");
        /** @deprecated Use {@link #DYED_PINK} combined with {@link #GLASS_PANES} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Item> GLASS_PANES_PINK = forgeTag("glass_panes/pink");
        /** @deprecated Use {@link #DYED_PURPLE} combined with {@link #GLASS_PANES} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Item> GLASS_PANES_PURPLE = forgeTag("glass_panes/purple");
        /** @deprecated Use {@link #DYED_RED} combined with {@link #GLASS_PANES} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Item> GLASS_PANES_RED = forgeTag("glass_panes/red");
        /** @deprecated Use {@link #DYED_WHITE} combined with {@link #GLASS_PANES} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Item> GLASS_PANES_WHITE = forgeTag("glass_panes/white");
        /** @deprecated Use {@link #DYED_YELLOW} combined with {@link #GLASS_PANES} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Item> GLASS_PANES_YELLOW = forgeTag("glass_panes/yellow");
        /** @deprecated Use {@link #DYED} combined with {@link #GLASS_PANES} */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Item> STAINED_GLASS_PANES = forgeTag("stained_glass_panes");

        /** @deprecated Use the Vanilla tag {@link ItemTags#HEAD_ARMOR} instead */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Item> ARMORS_HELMETS = forgeTag("armors/helmets");
        /** @deprecated Use the Vanilla tag {@link ItemTags#CHEST_ARMOR} instead */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Item> ARMORS_CHESTPLATES = forgeTag("armors/chestplates");
        /** @deprecated Use the Vanilla tag {@link ItemTags#LEG_ARMOR} instead */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Item> ARMORS_LEGGINGS = forgeTag("armors/leggings");
        /** @deprecated Use the Vanilla tag {@link ItemTags#FOOT_ARMOR} instead */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Item> ARMORS_BOOTS = forgeTag("armors/boots");
        //endregion

        private static TagKey<Item> cTag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", name));
        }

        private static TagKey<Item> forgeTag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath("forge", name));
        }
    }

    public static class Fluids {
        private static void init() {}

        //region `forge` tags for Forge-specific tags
        //endregion

        //region `c` tags for common conventions
        // Note: Other loaders have additional `c` tags that are exclusive to their loader.
        //       Forge only adopts `c` tags that are common across all loaders.
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
         * Holds all fluids that are gaseous at room temperature.
         */
        public static final TagKey<Fluid> GASEOUS = cTag("gaseous");
        /**
         * Holds all fluids related to honey.<br></br>
         * (Standard unit for honey bottle is 250mb per bottle)
         */
        public static final TagKey<Fluid> HONEY = cTag("honey");
        /**
         * Holds all fluids related to experience.
         * <p>(Standard unit for experience is 20mb per 1 experience. However, extraction from Bottle o' Enchanting
         * should yield 250mb while smashing yields less)</p>
         */
        public static final TagKey<Fluid> EXPERIENCE = cTag("experience");
        /**
         * Holds all fluids related to potions. The effects of the potion fluid should be read from NBT.
         * The effects and color of the potion fluid should be read from {@link net.minecraft.core.component.DataComponents#POTION_CONTENTS}
         * component that people should be attaching to the fluidstack of this fluid.
         * <p>(Standard unit for potions is 250mb per bottle)</p>
         */
        public static final TagKey<Fluid> POTION = cTag("potion");
        /**
         * Holds all fluids related to Suspicious Stew.
         * The effects of the suspicious stew fluid should be read from {@link net.minecraft.core.component.DataComponents#SUSPICIOUS_STEW_EFFECTS}
         * component that people should be attaching to the fluidstack of this fluid.
         * <p>(Standard unit for suspicious stew is 250mb per bowl)</p>
         */
        public static final TagKey<Fluid> SUSPICIOUS_STEW = cTag("suspicious_stew");
        /**
         * Holds all fluids related to Mushroom Stew.
         * <p>(Standard unit for mushroom stew is 250mb per bowl)</p>
         */
        public static final TagKey<Fluid> MUSHROOM_STEW = cTag("mushroom_stew");
        /**
         * Holds all fluids related to Rabbit Stew.<br></br>
         * <p>(Standard unit for rabbit stew is 250mb per bowl)</p>
         */
        public static final TagKey<Fluid> RABBIT_STEW = cTag("rabbit_stew");
        /**
         * Holds all fluids related to Beetroot Soup.
         * <p>(Standard unit for beetroot soup is 250mb per bowl)</p>
         */
        public static final TagKey<Fluid> BEETROOT_SOUP = cTag("beetroot_soup");
        /**
         * Tag that holds all fluids that recipe viewers should not show to users.
         */
        public static final TagKey<Fluid> HIDDEN_FROM_RECIPE_VIEWERS = cTag("hidden_from_recipe_viewers");
        //endregion

        private static TagKey<Fluid> cTag(String name) {
            return FluidTags.create(ResourceLocation.fromNamespaceAndPath("c", name));
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
            return TagKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath("c", name));
        }
    }

    public static class Biomes {
        private static void init() {}

        //region `forge` tags for Forge-specific tags
        public static final TagKey<Biome> IS_COLD_NETHER = forgeTag("is_cold/nether");

        /**
         * Biomes in the Nether that have sparse vegetation.
         */
        public static final TagKey<Biome> IS_SPARSE_NETHER = forgeTag("is_sparse/nether");
        /**
         * Biomes in the End that have sparse vegetation.
         */
        public static final TagKey<Biome> IS_SPARSE_END = forgeTag("is_sparse/end");
        /**
         * Biomes in the Nether that have dense vegetation.
         */
        public static final TagKey<Biome> IS_DENSE_NETHER = forgeTag("is_dense/nether");
        /**
         * Biomes in the End that have dense vegetation.
         */
        public static final TagKey<Biome> IS_DENSE_END = forgeTag("is_dense/end");

        public static final TagKey<Biome> IS_HOT_END = forgeTag("is_hot/end");

        public static final TagKey<Biome> IS_LUSH = forgeTag("is_lush");
        public static final TagKey<Biome> IS_MAGICAL = forgeTag("is_magical");
        public static final TagKey<Biome> IS_MODIFIED = forgeTag("is_modified");

        public static final TagKey<Biome> IS_RARE = forgeTag("is_rare");
        public static final TagKey<Biome> IS_PLATEAU = forgeTag("is_plateau");

        /**
         * Biomes that are able to spawn sand-based blocks on the surface.
         */
        public static final TagKey<Biome> IS_SANDY = forgeTag("is_sandy");

        public static final TagKey<Biome> IS_WET_NETHER = forgeTag("is_wet/nether");
        public static final TagKey<Biome> IS_WET_END = forgeTag("is_wet/end");

        public static final TagKey<Biome> IS_SPOOKY = forgeTag("is_spooky");
        //endregion

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

        public static final TagKey<Biome> IS_COLD = cTag("is_cold");
        public static final TagKey<Biome> IS_COLD_OVERWORLD = cTag("is_cold/overworld");
        public static final TagKey<Biome> IS_COLD_END = cTag("is_cold/end");

        public static final TagKey<Biome> IS_SPARSE_VEGETATION = cTag("is_sparse_vegetation");
        public static final TagKey<Biome> IS_SPARSE_VEGETATION_OVERWORLD = cTag("is_sparse_vegetation/overworld");
        public static final TagKey<Biome> IS_DENSE_VEGETATION = cTag("is_dense_vegetation");
        public static final TagKey<Biome> IS_DENSE_VEGETATION_OVERWORLD = cTag("is_dense_vegetation/overworld");

        public static final TagKey<Biome> IS_WET = cTag("is_wet");
        public static final TagKey<Biome> IS_WET_OVERWORLD = cTag("is_wet/overworld");
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
        //endregion

        //region Redirect fields for improved backward-compatibility
        // TODO: Remove backwards compat redirect fields in 1.22
        /** @deprecated Use {@link #IS_SPARSE_VEGETATION} instead */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Biome> IS_SPARSE = IS_SPARSE_VEGETATION;
        /** @deprecated Use {@link #IS_SPARSE_VEGETATION_OVERWORLD} instead */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Biome> IS_SPARSE_OVERWORLD = IS_SPARSE_VEGETATION_OVERWORLD;

        /** @deprecated Use {@link #IS_DENSE_VEGETATION} instead */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Biome> IS_DENSE = IS_DENSE_VEGETATION;
        /** @deprecated Use {@link #IS_DENSE_VEGETATION_OVERWORLD} instead */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Biome> IS_DENSE_OVERWORLD = IS_DENSE_VEGETATION_OVERWORLD;

        /** @deprecated Use {@link #IS_AQUATIC} instead */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Biome> IS_WATER = IS_AQUATIC;

        /** @deprecated Use {@link #IS_MOUNTAIN_SLOPE} instead */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Biome> IS_SLOPE = IS_MOUNTAIN_SLOPE;
        /** @deprecated Use {@link #IS_MOUNTAIN_PEAK} instead */
        @Deprecated(forRemoval = true, since = "1.21")
        public static final TagKey<Biome> IS_PEAK = IS_MOUNTAIN_PEAK;
        //endregion

        private static TagKey<Biome> cTag(String name) {
            return TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath("c", name));
        }

        private static TagKey<Biome> forgeTag(String name) {
            return TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath("forge", name));
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
            return TagKey.create(Registries.STRUCTURE, ResourceLocation.fromNamespaceAndPath("c", name));
        }
    }
}
