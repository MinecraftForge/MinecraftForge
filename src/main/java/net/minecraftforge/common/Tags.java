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
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.material.Fluid;

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

        //region Redirect fields for improved backward-compatibility
        // TODO: Remove backwards compat redirect fields in 1.22
        /** @deprecated Use {@link #COBBLESTONES} instead */
        @Deprecated(forRemoval = true)
        public static final TagKey<Block> COBBLESTONE = COBBLESTONES;
        /** @deprecated Use {@link #COBBLESTONES_NORMAL} instead */
        @Deprecated(forRemoval = true)
        public static final TagKey<Block> COBBLESTONE_NORMAL = COBBLESTONES_NORMAL;
        /** @deprecated Use {@link #COBBLESTONES_INFESTED} instead */
        @Deprecated(forRemoval = true)
        public static final TagKey<Block> COBBLESTONE_INFESTED = COBBLESTONES_INFESTED;
        /** @deprecated Use {@link #COBBLESTONES_MOSSY} instead */
        @Deprecated(forRemoval = true)
        public static final TagKey<Block> COBBLESTONE_MOSSY = COBBLESTONES_MOSSY;
        /** @deprecated Use {@link #COBBLESTONES_DEEPSLATE} instead */
        @Deprecated(forRemoval = true)
        public static final TagKey<Block> COBBLESTONE_DEEPSLATE = COBBLESTONES_DEEPSLATE;

        /** @deprecated Use {@link #SANDS} instead */
        @Deprecated(forRemoval = true)
        public static final TagKey<Block> SAND = SANDS;
        /** @deprecated Use {@link #SANDS_COLORLESS} instead */
        @Deprecated(forRemoval = true)
        public static final TagKey<Block> SAND_COLORLESS = SANDS_COLORLESS;
        /** @deprecated Use {@link #SANDS_RED} instead */
        @Deprecated(forRemoval = true)
        public static final TagKey<Block> SAND_RED = SANDS_RED;
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

        //region `forge` tags for functional behavior provided by Forge
        /**
         * Controls what items can be consumed for enchanting such as Enchanting Tables.
         * This tag defaults to {@link net.minecraft.world.item.Items#LAPIS_LAZULI} when not present in any datapacks, including forge client on vanilla server
         */
        public static final TagKey<Item> ENCHANTING_FUELS = forgeTag("enchanting_fuels");
        //endregion

        //region `c` tags for common conventions
        public static final TagKey<Item> BARRELS = tag("barrels");
        public static final TagKey<Item> BARRELS_WOODEN = tag("barrels/wooden");
        public static final TagKey<Item> BONES = tag("bones");
        public static final TagKey<Item> BOOKSHELVES = tag("bookshelves");
        public static final TagKey<Item> BRICKS = tag("bricks");
        public static final TagKey<Item> BRICKS_NORMAL = tag("bricks/normal");
        public static final TagKey<Item> BRICKS_NETHER = tag("bricks/nether");
        /**
         * For blocks that are similar to amethyst where their budding block produces buds and cluster blocks
         */
        public static final TagKey<Item> BUDDING_BLOCKS = tag("budding_blocks");
        /**
         * For blocks that are similar to amethyst where they have buddings forming from budding blocks
         */
        public static final TagKey<Item> BUDS = tag("buds");
        public static final TagKey<Item> CHAINS = tag("chains");
        public static final TagKey<Item> CHESTS = tag("chests");
        public static final TagKey<Item> CHESTS_ENDER = tag("chests/ender");
        public static final TagKey<Item> CHESTS_TRAPPED = tag("chests/trapped");
        public static final TagKey<Item> CHESTS_WOODEN = tag("chests/wooden");
        public static final TagKey<Item> COBBLESTONES = tag("cobblestones");
        public static final TagKey<Item> COBBLESTONES_NORMAL = tag("cobblestones/normal");
        public static final TagKey<Item> COBBLESTONES_INFESTED = tag("cobblestones/infested");
        public static final TagKey<Item> COBBLESTONES_MOSSY = tag("cobblestones/mossy");
        public static final TagKey<Item> COBBLESTONES_DEEPSLATE = tag("cobblestones/deepslate");
        /**
         * For blocks that are similar to amethyst where they have clusters forming from budding blocks
         */
        public static final TagKey<Item> CLUSTERS = tag("clusters");
        public static final TagKey<Item> CROPS = tag("crops");
        public static final TagKey<Item> CROPS_BEETROOT = tag("crops/beetroot");
        public static final TagKey<Item> CROPS_CARROT = tag("crops/carrot");
        public static final TagKey<Item> CROPS_NETHER_WART = tag("crops/nether_wart");
        public static final TagKey<Item> CROPS_POTATO = tag("crops/potato");
        public static final TagKey<Item> CROPS_WHEAT = tag("crops/wheat");
        public static final TagKey<Item> DUSTS = tag("dusts");
        public static final TagKey<Item> DUSTS_REDSTONE = tag("dusts/redstone");
        public static final TagKey<Item> DUSTS_GLOWSTONE = tag("dusts/glowstone");

        /**
         * Tag that holds all blocks and items that can be dyed a specific color.
         * (Does not include color blending items like leather armor
         * Use {@link net.minecraft.tags.ItemTags#DYEABLE} tag instead for color blending items)
         */
        public static final TagKey<Item> DYED = tag("dyed");
        public static final TagKey<Item> DYED_BLACK = tag("dyed/black");
        public static final TagKey<Item> DYED_BLUE = tag("dyed/blue");
        public static final TagKey<Item> DYED_BROWN = tag("dyed/brown");
        public static final TagKey<Item> DYED_CYAN = tag("dyed/cyan");
        public static final TagKey<Item> DYED_GRAY = tag("dyed/gray");
        public static final TagKey<Item> DYED_GREEN = tag("dyed/green");
        public static final TagKey<Item> DYED_LIGHT_BLUE = tag("dyed/light_blue");
        public static final TagKey<Item> DYED_LIGHT_GRAY = tag("dyed/light_gray");
        public static final TagKey<Item> DYED_LIME = tag("dyed/lime");
        public static final TagKey<Item> DYED_MAGENTA = tag("dyed/magenta");
        public static final TagKey<Item> DYED_ORANGE = tag("dyed/orange");
        public static final TagKey<Item> DYED_PINK = tag("dyed/pink");
        public static final TagKey<Item> DYED_PURPLE = tag("dyed/purple");
        public static final TagKey<Item> DYED_RED = tag("dyed/red");
        public static final TagKey<Item> DYED_WHITE = tag("dyed/white");
        public static final TagKey<Item> DYED_YELLOW = tag("dyed/yellow");

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
        public static final TagKey<Item> END_STONES = tag("end_stones");
        public static final TagKey<Item> ENDER_PEARLS = tag("ender_pearls");
        public static final TagKey<Item> FEATHERS = tag("feathers");
        public static final TagKey<Item> FENCE_GATES = tag("fence_gates");
        public static final TagKey<Item> FENCE_GATES_WOODEN = tag("fence_gates/wooden");
        public static final TagKey<Item> FENCES = tag("fences");
        public static final TagKey<Item> FENCES_NETHER_BRICK = tag("fences/nether_brick");
        public static final TagKey<Item> FENCES_WOODEN = tag("fences/wooden");
        public static final TagKey<Item> FOODS = tag("foods");
        /**
         * Apples and other foods that are considered fruits in the culinary field belong in this tag.
         * Cherries would go here as they are considered a "stone fruit" within culinary fields.
         */
        public static final TagKey<Item> FOODS_FRUITS = tag("foods/fruits");
        /**
         * Tomatoes and other foods that are considered vegetables in the culinary field belong in this tag.
         */
        public static final TagKey<Item> FOODS_VEGETABLES = tag("foods/vegetables");
        /**
         * Strawberries, raspberries, and other berry foods belong in this tag.
         * Cherries would NOT go here as they are considered a "stone fruit" within culinary fields.
         */
        public static final TagKey<Item> FOODS_BERRIES = tag("foods/berries");
        public static final TagKey<Item> FOODS_BREADS = tag("foods/breads");
        public static final TagKey<Item> FOODS_COOKIES = tag("foods/cookies");
        public static final TagKey<Item> FOODS_RAW_MEATS = tag("foods/raw_meats");
        public static final TagKey<Item> FOODS_COOKED_MEATS = tag("foods/cooked_meats");
        public static final TagKey<Item> FOODS_RAW_FISHES = tag("foods/raw_fishes");
        public static final TagKey<Item> FOODS_COOKED_FISHES = tag("foods/cooked_fishes");
        /**
         * Soups, stews, and other liquid food in bowls belongs in this tag.
         */
        public static final TagKey<Item> FOODS_SOUPS = tag("foods/soups");
        /**
         * Sweets and candies like lollipops or chocolate belong in this tag.
         */
        public static final TagKey<Item> FOODS_CANDIES = tag("foods/candies");
        /**
         * Foods like cake that can be eaten when placed in the world belong in this tag.
         */
        public static final TagKey<Item> FOODS_EDIBLE_WHEN_PLACED = tag("foods/edible_when_placed");
        /**
         * For foods that inflict food poisoning-like effects.
         * Examples are Rotten Flesh's Hunger or Pufferfish's Nausea, or Poisonous Potato's Poison.
         */
        public static final TagKey<Item> FOODS_FOOD_POISONING = tag("foods/food_poisoning");
        public static final TagKey<Item> GEMS = tag("gems");
        public static final TagKey<Item> GEMS_DIAMOND = tag("gems/diamond");
        public static final TagKey<Item> GEMS_EMERALD = tag("gems/emerald");
        public static final TagKey<Item> GEMS_AMETHYST = tag("gems/amethyst");
        public static final TagKey<Item> GEMS_LAPIS = tag("gems/lapis");
        public static final TagKey<Item> GEMS_PRISMARINE = tag("gems/prismarine");
        public static final TagKey<Item> GEMS_QUARTZ = tag("gems/quartz");

        public static final TagKey<Item> GLASS_BLOCKS = tag("glass_blocks");
        public static final TagKey<Item> GLASS_BLOCKS_COLORLESS = tag("glass_blocks/colorless");
        /**
         * Glass which is made from cheap resources like sand and only minor additional ingredients like dyes
         */
        public static final TagKey<Item> GLASS_BLOCKS_CHEAP = tag("glass_blocks/cheap");
        public static final TagKey<Item> GLASS_BLOCKS_TINTED = tag("glass_blocks/tinted");

        public static final TagKey<Item> GLASS_PANES = tag("glass_panes");
        public static final TagKey<Item> GLASS_PANES_COLORLESS = tag("glass_panes/colorless");

        public static final TagKey<Item> GRAVELS = tag("gravel");
        public static final TagKey<Item> GUNPOWDERS = tag("gunpowder");
        /**
         * Tag that holds all items that recipe viewers should not show to users.
         */
        public static final TagKey<Item> HIDDEN_FROM_RECIPE_VIEWERS = tag("hidden_from_recipe_viewers");
        public static final TagKey<Item> INGOTS = tag("ingots");
        public static final TagKey<Item> INGOTS_COPPER = tag("ingots/copper");
        public static final TagKey<Item> INGOTS_GOLD = tag("ingots/gold");
        public static final TagKey<Item> INGOTS_IRON = tag("ingots/iron");
        public static final TagKey<Item> INGOTS_NETHERITE = tag("ingots/netherite");
        public static final TagKey<Item> LEATHERS = tag("leather");
        public static final TagKey<Item> MUSHROOMS = tag("mushrooms");
        public static final TagKey<Item> NETHER_STARS = tag("nether_stars");
        public static final TagKey<Item> NETHERRACKS = tag("netherrack");
        public static final TagKey<Item> NUGGETS = tag("nuggets");
        public static final TagKey<Item> NUGGETS_GOLD = tag("nuggets/gold");
        public static final TagKey<Item> NUGGETS_IRON = tag("nuggets/iron");
        public static final TagKey<Item> OBSIDIANS = tag("obsidians");
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
        public static final TagKey<Item> PLAYER_WORKSTATIONS_CRAFTING_TABLES = tag("player_workstations/crafting_tables");
        public static final TagKey<Item> PLAYER_WORKSTATIONS_FURNACES = tag("player_workstations/furnaces");
        public static final TagKey<Item> RAW_BLOCKS = tag("raw_blocks");
        public static final TagKey<Item> RAW_BLOCKS_COPPER = tag("raw_blocks/copper");
        public static final TagKey<Item> RAW_BLOCKS_GOLD = tag("raw_blocks/gold");
        public static final TagKey<Item> RAW_BLOCKS_IRON = tag("raw_blocks/iron");
        public static final TagKey<Item> RAW_MATERIALS = tag("raw_materials");
        public static final TagKey<Item> RAW_MATERIALS_COPPER = tag("raw_materials/copper");
        public static final TagKey<Item> RAW_MATERIALS_GOLD = tag("raw_materials/gold");
        public static final TagKey<Item> RAW_MATERIALS_IRON = tag("raw_materials/iron");
        /**
         * For rod-like materials to be used in recipes.
         */
        public static final TagKey<Item> RODS = tag("rods");
        public static final TagKey<Item> RODS_BLAZE = tag("rods/blaze");
        public static final TagKey<Item> RODS_BREEZE = tag("rods/breeze");
        /**
         * For stick-like materials to be used in recipes.
         * One example is a mod adds stick variants such as Spruce Sticks but would like stick recipes to be able to use it.
         */
        public static final TagKey<Item> RODS_WOODEN = tag("rods/wooden");
        public static final TagKey<Item> ROPES = tag("ropes");

        public static final TagKey<Item> SANDS = tag("sands");
        public static final TagKey<Item> SANDS_COLORLESS = tag("sands/colorless");
        public static final TagKey<Item> SANDS_RED = tag("sands/red");

        public static final TagKey<Item> SANDSTONE_BLOCKS = tag("sandstone/blocks");
        public static final TagKey<Item> SANDSTONE_SLABS = tag("sandstone/slabs");
        public static final TagKey<Item> SANDSTONE_STAIRS = tag("sandstone/stairs");
        public static final TagKey<Item> SANDSTONE_RED_BLOCKS = tag("sandstone/red_blocks");
        public static final TagKey<Item> SANDSTONE_RED_SLABS = tag("sandstone/red_slabs");
        public static final TagKey<Item> SANDSTONE_RED_STAIRS = tag("sandstone/red_stairs");
        public static final TagKey<Item> SANDSTONE_UNCOLORED_BLOCKS = tag("sandstone/uncolored_blocks");
        public static final TagKey<Item> SANDSTONE_UNCOLORED_SLABS = tag("sandstone/uncolored_slabs");
        public static final TagKey<Item> SANDSTONE_UNCOLORED_STAIRS = tag("sandstone/uncolored_stairs");

        public static final TagKey<Item> SEEDS = tag("seeds");
        public static final TagKey<Item> SEEDS_BEETROOT = tag("seeds/beetroot");
        public static final TagKey<Item> SEEDS_MELON = tag("seeds/melon");
        public static final TagKey<Item> SEEDS_PUMPKIN = tag("seeds/pumpkin");
        public static final TagKey<Item> SEEDS_WHEAT = tag("seeds/wheat");
        public static final TagKey<Item> SLIMEBALLS = tag("slimeballs");
        /**
         * Natural stone-like blocks that can be used as a base ingredient in recipes that takes stone.
         */
        public static final TagKey<Item> STONES = tag("stones");
        /**
         * A storage block is generally a block that has a recipe to craft a bulk of 1 kind of resource to a block
         * and has a mirror recipe to reverse the crafting with no loss in resources.
         * <p></p>
         * Honey Block is special in that the reversing recipe is not a perfect mirror of the crafting recipe
         * and so, it is considered a special case and not given a storage block tag.
         */
        public static final TagKey<Item> STORAGE_BLOCKS = tag("storage_blocks");
        public static final TagKey<Item> STORAGE_BLOCKS_BONE_MEAL = tag("storage_blocks/bone_meal");
        public static final TagKey<Item> STORAGE_BLOCKS_COAL = tag("storage_blocks/coal");
        public static final TagKey<Item> STORAGE_BLOCKS_COPPER = tag("storage_blocks/copper");
        public static final TagKey<Item> STORAGE_BLOCKS_DIAMOND = tag("storage_blocks/diamond");
        public static final TagKey<Item> STORAGE_BLOCKS_DRIED_KELP = tag("storage_blocks/dried_kelp");
        public static final TagKey<Item> STORAGE_BLOCKS_EMERALD = tag("storage_blocks/emerald");
        public static final TagKey<Item> STORAGE_BLOCKS_GOLD = tag("storage_blocks/gold");
        public static final TagKey<Item> STORAGE_BLOCKS_IRON = tag("storage_blocks/iron");
        public static final TagKey<Item> STORAGE_BLOCKS_LAPIS = tag("storage_blocks/lapis");
        public static final TagKey<Item> STORAGE_BLOCKS_NETHERITE = tag("storage_blocks/netherite");
        public static final TagKey<Item> STORAGE_BLOCKS_RAW_COPPER = tag("storage_blocks/raw_copper");
        public static final TagKey<Item> STORAGE_BLOCKS_RAW_GOLD = tag("storage_blocks/raw_gold");
        public static final TagKey<Item> STORAGE_BLOCKS_RAW_IRON = tag("storage_blocks/raw_iron");
        public static final TagKey<Item> STORAGE_BLOCKS_REDSTONE = tag("storage_blocks/redstone");
        public static final TagKey<Item> STORAGE_BLOCKS_SLIME = tag("storage_blocks/slime");
        public static final TagKey<Item> STORAGE_BLOCKS_WHEAT = tag("storage_blocks/wheat");
        public static final TagKey<Item> STRINGS = tag("strings");
        public static final TagKey<Item> VILLAGER_JOB_SITES = tag("villager_job_sites");

        // Tools and Armors
        /**
         * A tag containing all existing tools. Do not use this tag for determining a tool's behavior.
         * Please use {@link ToolActions} instead for what action a tool can do.
         *
         * @see ToolAction
         * @see ToolActions
         */
        public static final TagKey<Item> TOOLS = tag("tools");
        /**
         * A tag containing all existing shields. Do not use this tag for determining a tool's behavior.
         * Please use {@link ToolActions} instead for what action a tool can do.
         *
         * @see ToolAction
         * @see ToolActions
         */
        public static final TagKey<Item> TOOLS_SHIELDS = tag("tools/shields");
        /**
         * A tag containing all existing bows. Do not use this tag for determining a tool's behavior.
         * Please use {@link ToolActions} instead for what action a tool can do.
         *
         * @see ToolAction
         * @see ToolActions
         */
        public static final TagKey<Item> TOOLS_BOWS = tag("tools/bows");
        /**
         * A tag containing all existing crossbows. Do not use this tag for determining a tool's behavior.
         * Please use {@link ToolActions} instead for what action a tool can do.
         *
         * @see ToolAction
         * @see ToolActions
         */
        public static final TagKey<Item> TOOLS_CROSSBOWS = tag("tools/crossbows");
        /**
         * A tag containing all existing fishing rods. Do not use this tag for determining a tool's behavior.
         * Please use {@link ToolActions} instead for what action a tool can do.
         *
         * @see ToolAction
         * @see ToolActions
         */
        public static final TagKey<Item> TOOLS_FISHING_RODS = tag("tools/fishing_rods");
        /**
         * A tag containing all existing spears. Other tools such as throwing knives or boomerangs
         * should not be put into this tag and should be put into their own tool tags.
         * Do not use this tag for determining a tool's behavior.
         * Please use {@link ToolActions} instead for what action a tool can do.
         *
         * @see ToolAction
         * @see ToolActions
         */
        public static final TagKey<Item> TOOLS_SPEARS = tag("tools/spears");
        /**
         * A tag containing all existing shears. Do not use this tag for determining a tool's behavior.
         * Please use {@link ToolActions} instead for what action a tool can do.
         *
         * @see ToolAction
         * @see ToolActions
         */
        public static final TagKey<Item> TOOLS_SHEARS = tag("tools/shears");
        /**
         * A tag containing all existing brushes. Do not use this tag for determining a tool's behavior.
         * Please use {@link ToolActions} instead for what action a tool can do.
         *
         * @see ToolAction
         * @see ToolActions
         */
        public static final TagKey<Item> TOOLS_BRUSHES = tag("tools/brushes");
        /**
         * Collects the 4 vanilla armor tags into one parent collection for ease.
         */
        public static final TagKey<Item> ARMORS = tag("armors");
        /**
         * Collects the many enchantable tags into one parent collection for ease.
         */
        public static final TagKey<Item> ENCHANTABLES = tag("enchantables");
        //endregion

        //region Redirect fields for improved backward-compatibility
        // TODO: Remove backwards compat redirect fields in 1.22
        /** @deprecated Use {@link #COBBLESTONES} instead */
        @Deprecated(forRemoval = true)
        public static final TagKey<Item> COBBLESTONE = COBBLESTONES;
        /** @deprecated Use {@link #COBBLESTONES_NORMAL} instead */
        @Deprecated(forRemoval = true)
        public static final TagKey<Item> COBBLESTONE_NORMAL = COBBLESTONES_NORMAL;
        /** @deprecated Use {@link #COBBLESTONES_INFESTED} instead */
        @Deprecated(forRemoval = true)
        public static final TagKey<Item> COBBLESTONE_INFESTED = COBBLESTONES_INFESTED;
        /** @deprecated Use {@link #COBBLESTONES_MOSSY} instead */
        @Deprecated(forRemoval = true)
        public static final TagKey<Item> COBBLESTONE_MOSSY = COBBLESTONES_MOSSY;
        /** @deprecated Use {@link #COBBLESTONES_DEEPSLATE} instead */
        @Deprecated(forRemoval = true)
        public static final TagKey<Item> COBBLESTONE_DEEPSLATE = COBBLESTONES_DEEPSLATE;
        /** @deprecated Use {@link #LEATHERS} instead */
        @Deprecated(forRemoval = true)
        public static final TagKey<Item> LEATHER = LEATHERS;
        /** @deprecated Use {@link #NETHERRACKS} instead */
        @Deprecated(forRemoval = true)
        public static final TagKey<Item> NETHERRACK = NETHERRACKS;
        /** @deprecated Use {@link #OBSIDIANS} instead */
        @Deprecated(forRemoval = true)
        public static final TagKey<Item> OBSIDIAN = OBSIDIANS;
        /** @deprecated Use {@link #SANDS} instead */
        @Deprecated(forRemoval = true)
        public static final TagKey<Item> SAND = SANDS;
        /** @deprecated Use {@link #SANDS_COLORLESS} instead */
        @Deprecated(forRemoval = true)
        public static final TagKey<Item> SAND_COLORLESS = SANDS_COLORLESS;
        /** @deprecated Use {@link #SANDS_RED} instead */
        @Deprecated(forRemoval = true)
        public static final TagKey<Item> SAND_RED = SANDS_RED;
        /** @deprecated Use {@link #STONES} instead */
        @Deprecated(forRemoval = true)
        public static final TagKey<Item> STONE = STONES;
        /** @deprecated Use {@link #TOOLS_SHEARS} instead */
        @Deprecated(forRemoval = true)
        public static final TagKey<Item> SHEARS = TOOLS_SHEARS;
        /** @deprecated Use {@link #STRINGS} instead */
        @Deprecated(forRemoval = true)
        public static final TagKey<Item> STRING = STRINGS;
        //endregion

        private static TagKey<Item> tag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", name));
        }

        private static TagKey<Item> forgeTag(String name) {
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

    public static class Enchantments {
        private static void init() {}

        /**
         * A tag containing enchantments that increase the amount or
         * quality of drops from blocks, such as {@link net.minecraft.world.item.enchantment.Enchantments#FORTUNE}.
         */
        public static final TagKey<Enchantment> INCREASE_BLOCK_DROPS = tag("increase_block_drops");
        /**
         * A tag containing enchantments that increase the amount or
         * quality of drops from entities, such as {@link net.minecraft.world.item.enchantment.Enchantments#LOOTING}.
         */
        public static final TagKey<Enchantment> INCREASE_ENTITY_DROPS = tag("increase_entity_drops");
        /**
         * For enchantments that increase the damage dealt by an item.
         */
        public static final TagKey<Enchantment> WEAPON_DAMAGE_ENHANCEMENTS = tag("weapon_damage_enhancements");
        /**
         * For enchantments that increase movement speed for entity wearing armor enchanted with it.
         */
        public static final TagKey<Enchantment> ENTITY_SPEED_ENHANCEMENTS = tag("entity_speed_enhancements");
        /**
         * For enchantments that applies movement-based benefits unrelated to speed for the entity wearing armor enchanted with it.
         * Example: Reducing falling speeds ({@link net.minecraft.world.item.enchantment.Enchantments#FEATHER_FALLING}) or allowing walking on water ({@link net.minecraft.world.item.enchantment.Enchantments#FROST_WALKER})
         */
        public static final TagKey<Enchantment> ENTITY_AUXILIARY_MOVEMENT_ENHANCEMENTS = tag("entity_auxiliary_movement_enhancements");
        /**
         * For enchantments that decrease damage taken or otherwise benefit, in regard to damage, the entity wearing armor enchanted with it.
         */
        public static final TagKey<Enchantment> ENTITY_DEFENSE_ENHANCEMENTS = tag("entity_defense_enhancements");

        private static TagKey<Enchantment> tag(String name) {
            return TagKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath("c", name));
        }
    }

    public static class Biomes {
        private static void init() {}

        /**
         * For biomes that should not spawn monsters over time the normal way.
         * In other words, their Spawners and Spawn Cost entries have the monster category empty.
         * Example: Mushroom Biomes not having Zombies, Creepers, Skeleton, nor any other normal monsters.
         */
        public static final TagKey<Biome> NO_DEFAULT_MONSTERS = tag("no_default_monsters");
        /**
         * Biomes that should not be locatable/selectable by modded biome-locating items or abilities.
         */
        public static final TagKey<Biome> HIDDEN_FROM_LOCATOR_SELECTION = tag("hidden_from_locator_selection");

        public static final TagKey<Biome> IS_VOID = tag("is_void");

        public static final TagKey<Biome> IS_HOT = tag("is_hot");
        public static final TagKey<Biome> IS_HOT_OVERWORLD = tag("is_hot/overworld");
        public static final TagKey<Biome> IS_HOT_NETHER = tag("is_hot/nether");
        public static final TagKey<Biome> IS_HOT_END = tag("is_hot/end");

        public static final TagKey<Biome> IS_COLD = tag("is_cold");
        public static final TagKey<Biome> IS_COLD_OVERWORLD = tag("is_cold/overworld");
        public static final TagKey<Biome> IS_COLD_NETHER = tag("is_cold/nether");
        public static final TagKey<Biome> IS_COLD_END = tag("is_cold/end");

        public static final TagKey<Biome> IS_SPARSE_VEGETATION = tag("is_sparse_vegetation");
        public static final TagKey<Biome> IS_SPARSE_VEGETATION_OVERWORLD = tag("is_sparse_vegetation/overworld");
        public static final TagKey<Biome> IS_SPARSE_VEGETATION_NETHER = tag("is_sparse_vegetation/nether");
        public static final TagKey<Biome> IS_SPARSE_VEGETATION_END = tag("is_sparse_vegetation/end");
        public static final TagKey<Biome> IS_DENSE_VEGETATION = tag("is_dense_vegetation");
        public static final TagKey<Biome> IS_DENSE_VEGETATION_OVERWORLD = tag("is_dense_vegetation/overworld");
        public static final TagKey<Biome> IS_DENSE_VEGETATION_NETHER = tag("is_dense_vegetation/nether");
        public static final TagKey<Biome> IS_DENSE_VEGETATION_END = tag("is_dense_vegetation/end");

        public static final TagKey<Biome> IS_WET = tag("is_wet");
        public static final TagKey<Biome> IS_WET_OVERWORLD = tag("is_wet/overworld");
        public static final TagKey<Biome> IS_WET_NETHER = tag("is_wet/nether");
        public static final TagKey<Biome> IS_WET_END = tag("is_wet/end");
        public static final TagKey<Biome> IS_DRY = tag("is_dry");
        public static final TagKey<Biome> IS_DRY_OVERWORLD = tag("is_dry/overworld");
        public static final TagKey<Biome> IS_DRY_NETHER = tag("is_dry/nether");
        public static final TagKey<Biome> IS_DRY_END = tag("is_dry/end");

        /**
         * Biomes that spawn in the Overworld.
         * (This is for people who want to tag their biomes without getting
         * side effects from {@link net.minecraft.tags.BiomeTags#IS_OVERWORLD}
         * <p></p>
         * NOTE: If you do not add to the vanilla Overworld tag, be sure to add to
         * {@link net.minecraft.tags.BiomeTags#HAS_STRONGHOLD} so some Strongholds do not go missing.)
         */
        public static final TagKey<Biome> IS_OVERWORLD = tag("is_overworld");

        public static final TagKey<Biome> IS_CONIFEROUS_TREE = tag("is_tree/coniferous");
        public static final TagKey<Biome> IS_SAVANNA_TREE = tag("is_tree/savanna");
        public static final TagKey<Biome> IS_JUNGLE_TREE = tag("is_tree/jungle");
        public static final TagKey<Biome> IS_DECIDUOUS_TREE = tag("is_tree/deciduous");

        /**
         * Biomes that spawn as part of giant mountains.
         * (This is for people who want to tag their biomes without getting
         * side effects from {@link net.minecraft.tags.BiomeTags#IS_MOUNTAIN})
         */
        public static final TagKey<Biome> IS_MOUNTAIN = tag("is_mountain");
        public static final TagKey<Biome> IS_MOUNTAIN_PEAK = tag("is_mountain/peak");
        public static final TagKey<Biome> IS_MOUNTAIN_SLOPE = tag("is_mountain/slope");

        /**
         * For temperate or warmer plains-like biomes.
         * For snowy plains-like biomes, see {@link #IS_SNOWY_PLAINS}.
         */
        public static final TagKey<Biome> IS_PLAINS = tag("is_plains");
        /**
         * For snowy plains-like biomes.
         * For warmer plains-like biomes, see {@link #IS_PLAINS}.
         */
        public static final TagKey<Biome> IS_SNOWY_PLAINS = tag("is_snowy_plains");
        /**
         * Biomes densely populated with deciduous trees.
         * (This is for people who want to tag their biomes without getting
         * side effects from {@link net.minecraft.tags.BiomeTags#IS_FOREST})
         */
        public static final TagKey<Biome> IS_FOREST = tag("is_forest");
        public static final TagKey<Biome> IS_BIRCH_FOREST = tag("is_birch_forest");
        public static final TagKey<Biome> IS_FLOWER_FOREST = tag("is_flower_forest");
        /**
         * Biomes that spawn as a taiga.
         * (This is for people who want to tag their biomes without getting
         * side effects from {@link net.minecraft.tags.BiomeTags#IS_TAIGA})
         */
        public static final TagKey<Biome> IS_TAIGA = tag("is_taiga");
        public static final TagKey<Biome> IS_OLD_GROWTH = tag("is_old_growth");
        /**
         * Biomes that spawn as a hills biome. (Previously was called Extreme Hills biome in past)
         * (This is for people who want to tag their biomes without getting
         * side effects from {@link net.minecraft.tags.BiomeTags#IS_HILL})
         */
        public static final TagKey<Biome> IS_HILL = tag("is_hill");
        public static final TagKey<Biome> IS_WINDSWEPT = tag("is_windswept");
        /**
         * Biomes that spawn as a jungle.
         * (This is for people who want to tag their biomes without getting
         * side effects from {@link net.minecraft.tags.BiomeTags#IS_JUNGLE})
         */
        public static final TagKey<Biome> IS_JUNGLE = tag("is_jungle");
        /**
         * Biomes that spawn as a savanna.
         * (This is for people who want to tag their biomes without getting
         * side effects from {@link net.minecraft.tags.BiomeTags#IS_SAVANNA})
         */
        public static final TagKey<Biome> IS_SAVANNA = tag("is_savanna");
        public static final TagKey<Biome> IS_SWAMP = tag("is_swamp");
        public static final TagKey<Biome> IS_DESERT = tag("is_desert");
        /**
         * Biomes that spawn as a badlands.
         * (This is for people who want to tag their biomes without getting
         * side effects from {@link net.minecraft.tags.BiomeTags#IS_BADLANDS})
         */
        public static final TagKey<Biome> IS_BADLANDS = tag("is_badlands");
        /**
         * Biomes that are dedicated to spawning on the shoreline of a body of water.
         * (This is for people who want to tag their biomes without getting
         * side effects from {@link net.minecraft.tags.BiomeTags#IS_BEACH})
         */
        public static final TagKey<Biome> IS_BEACH = tag("is_beach");
        public static final TagKey<Biome> IS_STONY_SHORES = tag("is_stony_shores");
        public static final TagKey<Biome> IS_MUSHROOM = tag("is_mushroom");

        /**
         * Biomes that spawn as a river.
         * (This is for people who want to tag their biomes without getting
         * side effects from {@link net.minecraft.tags.BiomeTags#IS_RIVER})
         */
        public static final TagKey<Biome> IS_RIVER = tag("is_river");
        /**
         * Biomes that spawn as part of the world's oceans.
         * (This is for people who want to tag their biomes without getting
         * side effects from {@link net.minecraft.tags.BiomeTags#IS_OCEAN})
         */
        public static final TagKey<Biome> IS_OCEAN = tag("is_ocean");
        /**
         * Biomes that spawn as part of the world's oceans that have low depth.
         * (This is for people who want to tag their biomes without getting
         * side effects from {@link net.minecraft.tags.BiomeTags#IS_DEEP_OCEAN})
         */
        public static final TagKey<Biome> IS_DEEP_OCEAN = tag("is_deep_ocean");
        public static final TagKey<Biome> IS_SHALLOW_OCEAN = tag("is_shallow_ocean");

        public static final TagKey<Biome> IS_UNDERGROUND = tag("is_underground");
        public static final TagKey<Biome> IS_CAVE = tag("is_cave");

        public static final TagKey<Biome> IS_LUSH = tag("is_lush");
        public static final TagKey<Biome> IS_MAGICAL = tag("is_magical");
        public static final TagKey<Biome> IS_RARE = tag("is_rare");
        public static final TagKey<Biome> IS_PLATEAU = tag("is_plateau");
        public static final TagKey<Biome> IS_MODIFIED = tag("is_modified");
        public static final TagKey<Biome> IS_SPOOKY = tag("is_spooky");
        /**
         * Biomes that lack any natural life or vegetation.
         * (Example, land destroyed and sterilized by nuclear weapons)
         */
        public static final TagKey<Biome> IS_WASTELAND = tag("is_wasteland");
        /**
         * Biomes whose flora primarily consists of dead or decaying vegetation.
         */
        public static final TagKey<Biome> IS_DEAD = tag("is_dead");
        /**
         * Biomes with a large amount of flowers.
         */
        public static final TagKey<Biome> IS_FLORAL = tag("is_floral");
        /**
         * Biomes that are able to spawn sand-based blocks on the surface.
         */
        public static final TagKey<Biome> IS_SANDY = tag("is_sandy");
        /**
         * For biomes that contains lots of naturally spawned snow.
         * For biomes where lot of ice is present, see {@link #IS_ICY}.
         * Biome with lots of both snow and ice may be in both tags.
         */
        public static final TagKey<Biome> IS_SNOWY = tag("is_snowy");
        /**
         * For land biomes where ice naturally spawns.
         * For biomes where snow alone spawns, see {@link #IS_SNOWY}.
         */
        public static final TagKey<Biome> IS_ICY = tag("is_icy");
        /**
         * Biomes consisting primarily of water.
         */
        public static final TagKey<Biome> IS_AQUATIC = tag("is_aquatic");
        /**
         * For water biomes where ice naturally spawns.
         * For biomes where snow alone spawns, see {@link #IS_SNOWY}.
         */
        public static final TagKey<Biome> IS_AQUATIC_ICY = tag("is_aquatic_icy");

        /**
         * Biomes that spawn in the Nether.
         * (This is for people who want to tag their biomes without getting
         * side effects from {@link net.minecraft.tags.BiomeTags#IS_NETHER})
         */
        public static final TagKey<Biome> IS_NETHER = tag("is_nether");
        public static final TagKey<Biome> IS_NETHER_FOREST = tag("is_nether_forest");

        /**
         * Biomes that spawn in the End.
         * (This is for people who want to tag their biomes without getting
         * side effects from {@link net.minecraft.tags.BiomeTags#IS_END})
         */
        public static final TagKey<Biome> IS_END = tag("is_end");
        /**
         * Biomes that spawn as part of the large islands outside the center island in The End dimension.
         */
        public static final TagKey<Biome> IS_OUTER_END_ISLAND = tag("is_outer_end_island");

        private static TagKey<Biome> tag(String name) {
            return TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath("c", name));
        }
    }

    public static class Structures {
        private static void init() {}
        
        /**
         * Structures that should not show up on minimaps or world map views from mods/sites.
         * No effect on vanilla map items.
         */
        public static final TagKey<Structure> HIDDEN_FROM_DISPLAYERS = tag("hidden_from_displayers");

        /**
         * Structures that should not be locatable/selectable by modded structure-locating items or abilities.
         * No effect on vanilla map items.
         */
        public static final TagKey<Structure> HIDDEN_FROM_LOCATOR_SELECTION = tag("hidden_from_locator_selection");

        private static TagKey<Structure> tag(String name) {
            return TagKey.create(Registries.STRUCTURE, new ResourceLocation("c", name));
        }
    }
}
