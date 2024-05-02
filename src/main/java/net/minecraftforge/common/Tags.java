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
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;

public class Tags {
    public static void init() {
        Blocks.init();
        EntityTypes.init();
        Items.init();
        Fluids.init();
        Biomes.init();
    }

    public static class Blocks {
        private static void init() {}

        //region `forge` tags for functional behavior provided by Forge
        /**
         * Controls what blocks Endermen cannot place blocks onto.
         * <p></p>
         * This is patched into the following method: {@link net.minecraft.world.entity.monster.EnderMan.EndermanLeaveBlockGoal#canPlaceBlock(Level, BlockPos, BlockState, BlockState, BlockState, BlockPos)}
         */
        public static final TagKey<Block> ENDERMAN_PLACE_ON_BLACKLIST = forgeTag("enderman_place_on_blacklist");
        public static final TagKey<Block> NEEDS_WOOD_TOOL = forgeTag("needs_wood_tool");
        public static final TagKey<Block> NEEDS_GOLD_TOOL = forgeTag("needs_gold_tool");
        public static final TagKey<Block> NEEDS_NETHERITE_TOOL = forgeTag("needs_netherite_tool");
        //endregion

        //region `c` tags for common conventions
        public static final TagKey<Block> BARRELS = tag("barrels");
        public static final TagKey<Block> BARRELS_WOODEN = tag("barrels/wooden");
        public static final TagKey<Block> BOOKSHELVES = tag("bookshelves");
        /**
         * For blocks that are similar to amethyst where their budding block produces buds and cluster blocks
         */
        public static final TagKey<Block> BUDDING_BLOCKS = tag("budding_blocks");
        /**
         * For blocks that are similar to amethyst where they have buddings forming from budding blocks
         */
        public static final TagKey<Block> BUDS = tag("buds");
        public static final TagKey<Block> CHAINS = tag("chains");
        public static final TagKey<Block> CHESTS = tag("chests");
        public static final TagKey<Block> CHESTS_ENDER = tag("chests/ender");
        public static final TagKey<Block> CHESTS_TRAPPED = tag("chests/trapped");
        public static final TagKey<Block> CHESTS_WOODEN = tag("chests/wooden");
        /**
         * For blocks that are similar to amethyst where they have clusters forming from budding blocks
         */
        public static final TagKey<Block> CLUSTERS = tag("clusters");
        public static final TagKey<Block> COBBLESTONES = tag("cobblestones");
        public static final TagKey<Block> COBBLESTONES_NORMAL = tag("cobblestones/normal");
        public static final TagKey<Block> COBBLESTONES_INFESTED = tag("cobblestones/infested");
        public static final TagKey<Block> COBBLESTONES_MOSSY = tag("cobblestones/mossy");
        public static final TagKey<Block> COBBLESTONES_DEEPSLATE = tag("cobblestones/deepslate");

        /**
         * Tag that holds all blocks that can be dyed a specific color.
         * (Does not include color blending blocks that would behave similar to leather armor item)
         */
        public static final TagKey<Block> DYED = tag("dyed");
        public static final TagKey<Block> DYED_BLACK = tag("dyed/black");
        public static final TagKey<Block> DYED_BLUE = tag("dyed/blue");
        public static final TagKey<Block> DYED_BROWN = tag("dyed/brown");
        public static final TagKey<Block> DYED_CYAN = tag("dyed/cyan");
        public static final TagKey<Block> DYED_GRAY = tag("dyed/gray");
        public static final TagKey<Block> DYED_GREEN = tag("dyed/green");
        public static final TagKey<Block> DYED_LIGHT_BLUE = tag("dyed/light_blue");
        public static final TagKey<Block> DYED_LIGHT_GRAY = tag("dyed/light_gray");
        public static final TagKey<Block> DYED_LIME = tag("dyed/lime");
        public static final TagKey<Block> DYED_MAGENTA = tag("dyed/magenta");
        public static final TagKey<Block> DYED_ORANGE = tag("dyed/orange");
        public static final TagKey<Block> DYED_PINK = tag("dyed/pink");
        public static final TagKey<Block> DYED_PURPLE = tag("dyed/purple");
        public static final TagKey<Block> DYED_RED = tag("dyed/red");
        public static final TagKey<Block> DYED_WHITE = tag("dyed/white");
        public static final TagKey<Block> DYED_YELLOW = tag("dyed/yellow");
        public static final TagKey<Block> END_STONES = tag("end_stones");
        public static final TagKey<Block> FENCE_GATES = tag("fence_gates");
        public static final TagKey<Block> FENCE_GATES_WOODEN = tag("fence_gates/wooden");
        public static final TagKey<Block> FENCES = tag("fences");
        public static final TagKey<Block> FENCES_NETHER_BRICK = tag("fences/nether_brick");
        public static final TagKey<Block> FENCES_WOODEN = tag("fences/wooden");

        public static final TagKey<Block> GLASS_BLOCKS = tag("glass_blocks");
        public static final TagKey<Block> GLASS_BLOCKS_COLORLESS = tag("glass_blocks/colorless");
        /**
         * Glass which is made from cheap resources like sand and only minor additional ingredients like dyes
         */
        public static final TagKey<Block> GLASS_BLOCKS_CHEAP = tag("glass_blocks/cheap");
        public static final TagKey<Block> GLASS_BLOCKS_TINTED = tag("glass_blocks/tinted");

        public static final TagKey<Block> GLASS_PANES = tag("glass_panes");
        public static final TagKey<Block> GLASS_PANES_COLORLESS = tag("glass_panes/colorless");

        public static final TagKey<Block> GRAVELS = tag("gravel");
        /**
         * Tag that holds all blocks that recipe viewers should not show to users.
         * Recipe viewers may use this to automatically find the corresponding BlockItem to hide.
         */
        public static final TagKey<Block> HIDDEN_FROM_RECIPE_VIEWERS = tag("hidden_from_recipe_viewers");
        public static final TagKey<Block> NETHERRACKS = tag("netherrack");
        public static final TagKey<Block> OBSIDIANS = tag("obsidians");
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
        public static final TagKey<Block> PLAYER_WORKSTATIONS_CRAFTING_TABLES = tag("player_workstations/crafting_tables");
        public static final TagKey<Block> PLAYER_WORKSTATIONS_FURNACES = tag("player_workstations/furnaces");
        /**
         * Blocks should be included in this tag if their movement/relocation can cause serious issues such
         * as world corruption upon being moved or for balance reason where the block should not be able to be relocated.
         * Example: Chunk loaders or pipes where other mods that move blocks do not respect
         * {@link BlockBehaviour.BlockStateBase#getPistonPushReaction}.
         */
        public static final TagKey<Block> RELOCATION_NOT_SUPPORTED = tag("relocation_not_supported");
        public static final TagKey<Block> ROPES = tag("ropes");

        public static final TagKey<Block> SANDS = tag("sands");
        public static final TagKey<Block> SANDS_COLORLESS = tag("sands/colorless");
        public static final TagKey<Block> SANDS_RED = tag("sands/red");

        public static final TagKey<Block> SANDSTONE_BLOCKS = tag("sandstone/blocks");
        public static final TagKey<Block> SANDSTONE_SLABS = tag("sandstone/slabs");
        public static final TagKey<Block> SANDSTONE_STAIRS = tag("sandstone/stairs");
        public static final TagKey<Block> SANDSTONE_RED_BLOCKS = tag("sandstone/red_blocks");
        public static final TagKey<Block> SANDSTONE_RED_SLABS = tag("sandstone/red_slabs");
        public static final TagKey<Block> SANDSTONE_RED_STAIRS = tag("sandstone/red_stairs");
        public static final TagKey<Block> SANDSTONE_UNCOLORED_BLOCKS = tag("sandstone/uncolored_blocks");
        public static final TagKey<Block> SANDSTONE_UNCOLORED_SLABS = tag("sandstone/uncolored_slabs");
        public static final TagKey<Block> SANDSTONE_UNCOLORED_STAIRS = tag("sandstone/uncolored_stairs");
        /**
         * Tag that holds all head based blocks such as Skeleton Skull or Player Head. (Named skulls to match minecraft:skulls item tag)
         */
        public static final TagKey<Block> SKULLS = tag("skulls");
        /**
         * Natural stone-like blocks that can be used as a base ingredient in recipes that takes stone.
         */
        public static final TagKey<Block> STONES = tag("stones");
        /**
         * A storage block is generally a block that has a recipe to craft a bulk of 1 kind of resource to a block
         * and has a mirror recipe to reverse the crafting with no loss in resources.
         * <p></p>
         * Honey Block is special in that the reversing recipe is not a perfect mirror of the crafting recipe
         * and so, it is considered a special case and not given a storage block tag.
         */
        public static final TagKey<Block> STORAGE_BLOCKS = tag("storage_blocks");
        public static final TagKey<Block> STORAGE_BLOCKS_BONE_MEAL = tag("storage_blocks/bone_meal");
        public static final TagKey<Block> STORAGE_BLOCKS_COAL = tag("storage_blocks/coal");
        public static final TagKey<Block> STORAGE_BLOCKS_COPPER = tag("storage_blocks/copper");
        public static final TagKey<Block> STORAGE_BLOCKS_DIAMOND = tag("storage_blocks/diamond");
        public static final TagKey<Block> STORAGE_BLOCKS_DRIED_KELP = tag("storage_blocks/dried_kelp");
        public static final TagKey<Block> STORAGE_BLOCKS_EMERALD = tag("storage_blocks/emerald");
        public static final TagKey<Block> STORAGE_BLOCKS_GOLD = tag("storage_blocks/gold");
        public static final TagKey<Block> STORAGE_BLOCKS_IRON = tag("storage_blocks/iron");
        public static final TagKey<Block> STORAGE_BLOCKS_LAPIS = tag("storage_blocks/lapis");
        public static final TagKey<Block> STORAGE_BLOCKS_NETHERITE = tag("storage_blocks/netherite");
        public static final TagKey<Block> STORAGE_BLOCKS_RAW_COPPER = tag("storage_blocks/raw_copper");
        public static final TagKey<Block> STORAGE_BLOCKS_RAW_GOLD = tag("storage_blocks/raw_gold");
        public static final TagKey<Block> STORAGE_BLOCKS_RAW_IRON = tag("storage_blocks/raw_iron");
        public static final TagKey<Block> STORAGE_BLOCKS_REDSTONE = tag("storage_blocks/redstone");
        public static final TagKey<Block> STORAGE_BLOCKS_SLIME = tag("storage_blocks/slime");
        public static final TagKey<Block> STORAGE_BLOCKS_WHEAT = tag("storage_blocks/wheat");
        public static final TagKey<Block> VILLAGER_JOB_SITES = tag("villager_job_sites");
        //endregion

        private static TagKey<Block> tag(String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", name));
        }

        private static TagKey<Block> forgeTag(String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath("forge", name));
        }
    }

    public static class EntityTypes {
        private static void init() {}

        public static final TagKey<EntityType<?>> BOSSES = tag("bosses");
        public static final TagKey<EntityType<?>> MINECARTS = tag("minecarts");
        public static final TagKey<EntityType<?>> BOATS = tag("boats");

        /**
         * Entities should be included in this tag if they are not allowed to be picked up by items or grabbed in a way
         * that a player can easily move the entity to anywhere they want. Ideal for special entities that should not
         * be able to be put into a mob jar for example.
         */
        public static final TagKey<EntityType<?>> CAPTURING_NOT_SUPPORTED = tag("capturing_not_supported");

        /**
         * Entities should be included in this tag if they are not allowed to be teleported in any way.
         * This is more for mods that allow teleporting entities within the same dimension. Any mod that is
         * teleporting entities to new dimensions should be checking canChangeDimensions method on the entity itself.
         */
        public static final TagKey<EntityType<?>> TELEPORTING_NOT_SUPPORTED = tag("teleporting_not_supported");

        private static TagKey<EntityType<?>> tag(String name) {
            return TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath("c", name));
        }
    }

    public static class Items {
        private static void init() {}

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
        /**
         * A tag containing all existing tools.
         *
         * Note: This tag is not an alternative or a substitute to {@link net.minecraftforge.common.ToolActions}.
         *
         * @see net.minecraftforge.common.ToolAction
         * @see net.minecraftforge.common.ToolActions
         */
        public static final TagKey<Item> TOOLS = tag("tools");
        /**
         * A tag containing all existing shields.
         *
         * Note: This tag is not an alternative or a substitute to {@link net.minecraftforge.common.ToolActions}.
         *
         * @see net.minecraftforge.common.ToolAction
         * @see net.minecraftforge.common.ToolActions
         */
        public static final TagKey<Item> TOOLS_SHIELDS = tag("tools/shields");
        /**
         * A tag containing all existing bows.
         *
         * Note: This tag is not an alternative or a substitute to {@link net.minecraftforge.common.ToolActions}.
         *
         * @see net.minecraftforge.common.ToolAction
         * @see net.minecraftforge.common.ToolActions
         */
        public static final TagKey<Item> TOOLS_BOWS = tag("tools/bows");
        /**
         * A tag containing all existing crossbows.
         *
         * Note: This tag is not an alternative or a substitute to {@link net.minecraftforge.common.ToolActions}.
         *
         * @see net.minecraftforge.common.ToolAction
         * @see net.minecraftforge.common.ToolActions
         */
        public static final TagKey<Item> TOOLS_CROSSBOWS = tag("tools/crossbows");
        /**
         * A tag containing all existing fishing rods.
         *
         * Note: This tag is not an alternative or a substitute to {@link net.minecraftforge.common.ToolActions}.
         *
         * @see net.minecraftforge.common.ToolAction
         * @see net.minecraftforge.common.ToolActions
         */
        public static final TagKey<Item> TOOLS_FISHING_RODS = tag("tools/fishing_rods");
        /**
         * A tag containing all existing tridents.
         *
         * Note: This tag is not an alternative or a substitute to {@link net.minecraftforge.common.ToolActions}.
         *
         * @see net.minecraftforge.common.ToolAction
         * @see net.minecraftforge.common.ToolActions
         */
        public static final TagKey<Item> TOOLS_TRIDENTS = tag("tools/tridents");
        /**
         * A tag containing all existing armors.
         */
        public static final TagKey<Item> ARMORS = tag("armors");
        /**
         * A tag containing all existing helmets.
         */
        public static final TagKey<Item> ARMORS_HELMETS = tag("armors/helmets");
        /**
         * A tag containing all chestplates.
         */
        public static final TagKey<Item> ARMORS_CHESTPLATES = tag("armors/chestplates");
        /**
         * A tag containing all existing leggings.
         */
        public static final TagKey<Item> ARMORS_LEGGINGS = tag("armors/leggings");
        /**
         * A tag containing all existing boots.
         */
        public static final TagKey<Item> ARMORS_BOOTS = tag("armors/boots");

        private static TagKey<Item> tag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath("forge", name));
        }
    }

    public static class Fluids {
        private static void init() {}

        /**
         * Holds all fluids related to water.
         * This tag is done to help out multi-loader mods/datapacks where the vanilla water tag has attached behaviors outside Forge.
         */
        public static final TagKey<Fluid> WATER = tag("water");
        /**
         * Holds all fluids related to lava.
         * This tag is done to help out multi-loader mods/datapacks where the vanilla lava tag has attached behaviors outside Forge.
         */
        public static final TagKey<Fluid> LAVA = tag("lava");
        /**
         * Holds all fluids related to milk.
         */
        public static final TagKey<Fluid> MILK = tag("milk");
        /**
         * Holds all fluids that are gaseous at room temperature.
         */
        public static final TagKey<Fluid> GASEOUS = tag("gaseous");
        /**
         * Holds all fluids related to honey.<br></br>
         * (Standard unit for honey bottle is 250mb per bottle)
         */
        public static final TagKey<Fluid> HONEY = tag("honey");
        /**
         * Holds all fluids related to potions. The effects of the potion fluid should be read from NBT.
         * The effects and color of the potion fluid should be read from {@link net.minecraft.core.component.DataComponents#POTION_CONTENTS}
         * component that people should be attaching to the fluidstack of this fluid.<br></br>
         * (Standard unit for potions is 250mb per bottle)
         */
        public static final TagKey<Fluid> POTION = tag("potion");
        /**
         * Holds all fluids related to Suspicious Stew.
         * The effects of the suspicious stew fluid should be read from {@link net.minecraft.core.component.DataComponents#SUSPICIOUS_STEW_EFFECTS}
         * component that people should be attaching to the fluidstack of this fluid.<br></br>
         * (Standard unit for suspicious stew is 250mb per bowl)
         */
        public static final TagKey<Fluid> SUSPICIOUS_STEW = tag("suspicious_stew");
        /**
         * Holds all fluids related to Mushroom Stew.<br></br>
         * (Standard unit for mushroom stew is 250mb per bowl)
         */
        public static final TagKey<Fluid> MUSHROOM_STEW = tag("mushroom_stew");
        /**
         * Holds all fluids related to Rabbit Stew.<br></br>
         * (Standard unit for rabbit stew is 250mb per bowl)
         */
        public static final TagKey<Fluid> RABBIT_STEW = tag("rabbit_stew");
        /**
         * Holds all fluids related to Beetroot Soup.<br></br>
         * (Standard unit for beetroot soup is 250mb per bowl)
         */
        public static final TagKey<Fluid> BEETROOT_SOUP = tag("beetroot_soup");
        /**
         * Tag that holds all fluids that recipe viewers should not show to users.
         */
        public static final TagKey<Fluid> HIDDEN_FROM_RECIPE_VIEWERS = tag("hidden_from_recipe_viewers");

        private static TagKey<Fluid> tag(String name) {
            return FluidTags.create(ResourceLocation.fromNamespaceAndPath("c", name));
        }
    }

    public static class Biomes {
        private static void init() {}

        public static final TagKey<Biome> IS_HOT = tag("is_hot");
        public static final TagKey<Biome> IS_HOT_OVERWORLD = tag("is_hot/overworld");
        public static final TagKey<Biome> IS_HOT_NETHER = tag("is_hot/nether");
        public static final TagKey<Biome> IS_HOT_END = tag("is_hot/end");

        public static final TagKey<Biome> IS_COLD = tag("is_cold");
        public static final TagKey<Biome> IS_COLD_OVERWORLD = tag("is_cold/overworld");
        public static final TagKey<Biome> IS_COLD_NETHER = tag("is_cold/nether");
        public static final TagKey<Biome> IS_COLD_END = tag("is_cold/end");

        public static final TagKey<Biome> IS_SPARSE = tag("is_sparse");
        public static final TagKey<Biome> IS_SPARSE_OVERWORLD = tag("is_sparse/overworld");
        public static final TagKey<Biome> IS_SPARSE_NETHER = tag("is_sparse/nether");
        public static final TagKey<Biome> IS_SPARSE_END = tag("is_sparse/end");
        public static final TagKey<Biome> IS_DENSE = tag("is_dense");
        public static final TagKey<Biome> IS_DENSE_OVERWORLD = tag("is_dense/overworld");
        public static final TagKey<Biome> IS_DENSE_NETHER = tag("is_dense/nether");
        public static final TagKey<Biome> IS_DENSE_END = tag("is_dense/end");

        public static final TagKey<Biome> IS_WET = tag("is_wet");
        public static final TagKey<Biome> IS_WET_OVERWORLD = tag("is_wet/overworld");
        public static final TagKey<Biome> IS_WET_NETHER = tag("is_wet/nether");
        public static final TagKey<Biome> IS_WET_END = tag("is_wet/end");
        public static final TagKey<Biome> IS_DRY = tag("is_dry");
        public static final TagKey<Biome> IS_DRY_OVERWORLD = tag("is_dry/overworld");
        public static final TagKey<Biome> IS_DRY_NETHER = tag("is_dry/nether");
        public static final TagKey<Biome> IS_DRY_END = tag("is_dry/end");

        public static final TagKey<Biome> IS_CONIFEROUS = tag("is_coniferous");

        public static final TagKey<Biome> IS_SPOOKY = tag("is_spooky");
        public static final TagKey<Biome> IS_DEAD = tag("is_dead");
        public static final TagKey<Biome> IS_LUSH = tag("is_lush");
        public static final TagKey<Biome> IS_MUSHROOM = tag("is_mushroom");
        public static final TagKey<Biome> IS_MAGICAL = tag("is_magical");
        public static final TagKey<Biome> IS_RARE = tag("is_rare");
        public static final TagKey<Biome> IS_PLATEAU = tag("is_plateau");
        public static final TagKey<Biome> IS_MODIFIED = tag("is_modified");

        public static final TagKey<Biome> IS_WATER = tag("is_water");
        public static final TagKey<Biome> IS_DESERT = tag("is_desert");
        public static final TagKey<Biome> IS_PLAINS = tag("is_plains");
        public static final TagKey<Biome> IS_SWAMP = tag("is_swamp");
        public static final TagKey<Biome> IS_SANDY = tag("is_sandy");
        public static final TagKey<Biome> IS_SNOWY = tag("is_snowy");
        public static final TagKey<Biome> IS_WASTELAND = tag("is_wasteland");
        public static final TagKey<Biome> IS_VOID = tag("is_void");
        public static final TagKey<Biome> IS_UNDERGROUND = tag("is_underground");

        public static final TagKey<Biome> IS_CAVE = tag("is_cave");
        public static final TagKey<Biome> IS_PEAK = tag("is_peak");
        public static final TagKey<Biome> IS_SLOPE = tag("is_slope");
        public static final TagKey<Biome> IS_MOUNTAIN = tag("is_mountain");

        private static TagKey<Biome> tag(String name) {
            return TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath("forge", name));
        }
    }
}
