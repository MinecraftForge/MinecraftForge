/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.common.tags;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.tags.Tag;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public final class Tags {
    private Tags()
    {
    }

    public static final class Blocks {
        public static final Tag<Block> CHESTS = tag("chests");
        public static final Tag<Block> CHESTS_ENDER = tag("chests/ender");
        public static final Tag<Block> CHESTS_TRAPPED = tag("chests/trapped");
        public static final Tag<Block> CHESTS_WOODEN = tag("chests/wooden");
        public static final Tag<Block> COBBLESTONE = tag("cobblestone");
        public static final Tag<Block> FENCE_GATES = tag("fence_gates");
        public static final Tag<Block> FENCE_GATES_WOODEN = tag("fence_gates/wooden");
        public static final Tag<Block> FENCES = tag("fences");
        public static final Tag<Block> FENCES_NETHER_BRRICK = tag("fences/nether_brick");
        public static final Tag<Block> FENCES_WOODEN = tag("fences/wooden");
        public static final Tag<Block> ORES = tag("ores");
        public static final Tag<Block> ORES_COAL = tag("ores/coal");
        public static final Tag<Block> ORES_DIAMOND = tag("ores/diamond");
        public static final Tag<Block> ORES_EMERALD = tag("ores/emerald");
        public static final Tag<Block> ORES_GOLD = tag("ores/gold");
        public static final Tag<Block> ORES_IRON = tag("ores/iron");
        public static final Tag<Block> ORES_LAPIS = tag("ores/lapis");
        public static final Tag<Block> ORES_QUARTZ = tag("ores/quartz");
        public static final Tag<Block> ORES_REDSTONE = tag("ores/redstone");
        public static final Tag<Block> STONE = tag("stone");
        public static final Tag<Block> STORAGE_BLOCKS = tag("storage_blocks");
        public static final Tag<Block> STORAGE_BLOCKS_COAL = tag("storage_blocks/coal");
        public static final Tag<Block> STORAGE_BLOCKS_DIAMOND = tag("storage_blocks/diamond");
        public static final Tag<Block> STORAGE_BLOCKS_EMERALD = tag("storage_blocks/emerald");
        public static final Tag<Block> STORAGE_BLOCKS_GOLD = tag("storage_blocks/gold");
        public static final Tag<Block> STORAGE_BLOCKS_IRON = tag("storage_blocks/iron");
        public static final Tag<Block> STORAGE_BLOCKS_LAPIS = tag("storage_blocks/lapis");
        public static final Tag<Block> STORAGE_BLOCKS_QUARTZ = tag("storage_blocks/quartz");
        public static final Tag<Block> STORAGE_BLOCKS_REDSTONE = tag("storage_blocks/redstone");

        private static Tag<Block> tag(String name)
        {
            return ForgeRegistries.BLOCKS.getTagProvider().createWrapper(new ResourceLocation("minecraft", name));  // no need for the UnmodifiableWrapper - We already use ImmutableTags
        }

        private Blocks()
        {
        }
    }

    public static final class Items {
        //Forge added Tags
        public static final Tag<Item> CHESTS = tag("chests");
        public static final Tag<Item> CHESTS_ENDER = tag("chests/ender");
        public static final Tag<Item> CHESTS_TRAPPED = tag("chests/trapped");
        public static final Tag<Item> CHESTS_WOODEN = tag("chests/wooden");
        public static final Tag<Item> COBBLESTONE = tag("cobblestone");
        public static final Tag<Item> DUSTS = tag("dusts");
        public static final Tag<Item> DUSTS_PRISMARINE = tag("dusts/prismarine");
        public static final Tag<Item> DUSTS_REDSTONE = tag("dusts/redstone");
        public static final Tag<Item> DUSTS_GLOWSTONE = tag("dusts/glowstone");
        public static final Tag<Item> DYES = tag("dyes");
        public static final Tag<Item> DYES_BLACK = tag("dyes/black");
        public static final Tag<Item> DYES_RED = tag("dyes/red");
        public static final Tag<Item> DYES_GREEN = tag("dyes/green");
        public static final Tag<Item> DYES_BROWN = tag("dyes/brown");
        public static final Tag<Item> DYES_BLUE = tag("dyes/blue");
        public static final Tag<Item> DYES_PURPLE = tag("dyes/purple");
        public static final Tag<Item> DYES_CYAN = tag("dyes/cyan");
        public static final Tag<Item> DYES_LIGHT_GRAY = tag("dyes/light_gray");
        public static final Tag<Item> DYES_GRAY = tag("dyes/gray");
        public static final Tag<Item> DYES_PINK = tag("dyes/pink");
        public static final Tag<Item> DYES_LIME = tag("dyes/lime");
        public static final Tag<Item> DYES_YELLOW = tag("dyes/yellow");
        public static final Tag<Item> DYES_LIGHT_BLUE = tag("dyes/light_blue");
        public static final Tag<Item> DYES_MAGENTA = tag("dyes/magenta");
        public static final Tag<Item> DYES_ORANGE = tag("dyes/orange");
        public static final Tag<Item> DYES_WHITE = tag("dyes/white");
        public static final Tag<Item> FENCE_GATES = tag("fence_gates");
        public static final Tag<Item> FENCE_GATES_WOODEN = tag("fence_gates/wooden");
        public static final Tag<Item> FENCES = tag("fences");
        public static final Tag<Item> FENCES_NETHER_BRRICK = tag("fences/nether_brick");
        public static final Tag<Item> FENCES_WOODEN = tag("fences/wooden");
        public static final Tag<Item> GEMS = tag("gems");
        public static final Tag<Item> GEMS_DIAMOND = tag("gems/diamond");
        public static final Tag<Item> GEMS_EMERALD = tag("gems/emerald");
        public static final Tag<Item> GEMS_LAPIS = tag("gems/lapis");
        public static final Tag<Item> GEMS_PRISMARINE = tag("gems/prismarine");
        public static final Tag<Item> GEMS_QUARTZ = tag("gems/quartz");
        public static final Tag<Item> INGOTS = tag("ingots");
        public static final Tag<Item> INGOTS_BRICK = tag("ingots/brick");
        public static final Tag<Item> INGOTS_GOLD = tag("ingots/gold");
        public static final Tag<Item> INGOTS_IRON = tag("ingots/iron");
        public static final Tag<Item> INGOTS_NETHER_BRICK = tag("ingots/nether_brick");
        public static final Tag<Item> MUSIC_DISCS = tag("music_discs");
        public static final Tag<Item> NUGGETS = tag("nuggets");
        public static final Tag<Item> NUGGETS_GOLD = tag("nuggets/gold");
        public static final Tag<Item> NUGGETS_IRON = tag("nuggets/iron");
        public static final Tag<Item> ORES = tag("ores");
        public static final Tag<Item> ORES_COAL = tag("ores/coal");
        public static final Tag<Item> ORES_DIAMOND = tag("ores/diamond");
        public static final Tag<Item> ORES_EMERALD = tag("ores/emerald");
        public static final Tag<Item> ORES_GOLD = tag("ores/gold");
        public static final Tag<Item> ORES_IRON = tag("ores/iron");
        public static final Tag<Item> ORES_LAPIS = tag("ores/lapis");
        public static final Tag<Item> ORES_QUARTZ = tag("ores/quartz");
        public static final Tag<Item> ORES_REDSTONE = tag("ores/redstone");
        public static final Tag<Item> RODS = tag("rods");
        public static final Tag<Item> RODS_BLAZE = tag("rods/blaze");
        public static final Tag<Item> RODS_WOODEN = tag("rods/wooden");
        public static final Tag<Item> STONE = tag("stone");
        public static final Tag<Item> STORAGE_BLOCKS = tag("storage_blocks");
        public static final Tag<Item> STORAGE_BLOCKS_COAL = tag("storage_blocks/coal");
        public static final Tag<Item> STORAGE_BLOCKS_DIAMOND = tag("storage_blocks/diamond");
        public static final Tag<Item> STORAGE_BLOCKS_EMERALD = tag("storage_blocks/emerald");
        public static final Tag<Item> STORAGE_BLOCKS_GOLD = tag("storage_blocks/gold");
        public static final Tag<Item> STORAGE_BLOCKS_IRON = tag("storage_blocks/iron");
        public static final Tag<Item> STORAGE_BLOCKS_LAPIS = tag("storage_blocks/lapis");
        public static final Tag<Item> STORAGE_BLOCKS_QUARTZ = tag("storage_blocks/quartz");
        public static final Tag<Item> STORAGE_BLOCKS_REDSTONE = tag("storage_blocks/redstone");

        private static Tag<Item> tag(String name)
        {
            return ForgeRegistries.ITEMS.getTagProvider().createWrapper(new ResourceLocation("minecraft", name)); // no need for the UnmodifiableWrapper - We already use ImmutableTags
        }

        private Items()
        {
        }
    }

    public static final class Entities {
        public static final Tag<EntityType<?>> ANY_ENTITY_LIVING = tag("any_entity_living");
        public static final Tag<EntityType<?>> ARTHROPOD = tag("arthropod");
        public static final Tag<EntityType<?>> COWS = tag("cows");
        public static final Tag<EntityType<?>> FARM_ANIMALS = tag("farm_animals");
        public static final Tag<EntityType<?>> FISH = tag("fish");
        public static final Tag<EntityType<?>> HORSES = tag("horses");
        public static final Tag<EntityType<?>> ILLAGER = tag("illager");
        public static final Tag<EntityType<?>> NETHER = tag("nether");
        public static final Tag<EntityType<?>> RIDEABLE = tag("rideable");
        public static final Tag<EntityType<?>> RIDEABLE_LIVING = tag("rideable_living");
        public static final Tag<EntityType<?>> RIDEABLE_UNDEAD = tag("rideable_undead");
        public static final Tag<EntityType<?>> SKELETONS = tag("skeletons");
        public static final Tag<EntityType<?>> SLIMY = tag("slimy");
        public static final Tag<EntityType<?>> SPIDERS = tag("spiders");
        public static final Tag<EntityType<?>> TAMEABLE = tag("tameable");
        public static final Tag<EntityType<?>> UNDEAD = tag("undead");
        public static final Tag<EntityType<?>> UNDERWATER = tag("underwater");
        public static final Tag<EntityType<?>> VILLAGER = tag("villager");
        public static final Tag<EntityType<?>> ZOMBIES = tag("zombies");
        //MinecraftWiki categories
        public static final Tag<EntityType<?>> CATEGORY_BOSS = tag("category/boss");
        public static final Tag<EntityType<?>> CATEGORY_HOSTILE = tag("category/hostile");
        public static final Tag<EntityType<?>> CATEGORY_NEUTRAL = tag("category/neutral");
        public static final Tag<EntityType<?>> CATEGORY_NEUTRAL_ANIMALS = tag("category/neutral_animals");
        public static final Tag<EntityType<?>> CATEGORY_NEUTRAL_MONSTERS = tag("category/neutral_monsters");
        public static final Tag<EntityType<?>> CATEGORY_PASSIVE = tag("category/passive");
        public static final Tag<EntityType<?>> CATEGORY_PASSIVE_PEACEFUL = tag("category/passive_peaceful");
        public static final Tag<EntityType<?>> CATEGORY_PASSIVE_DEFENSIVE = tag("category/passive_defensive");
        public static final Tag<EntityType<?>> CATEGORY_MONSTERS = tag("category/monsters");
        public static final Tag<EntityType<?>> CATEGORY_UTILITY = tag("category/utility");
        //Different Attack types
        public static final Tag<EntityType<?>> ATTACK_ANY = tag("attack/any");
        public static final Tag<EntityType<?>> ATTACK_MAGIC = tag("attack/magic");
        public static final Tag<EntityType<?>> ATTACK_MELEE = tag("attack/melee");
        public static final Tag<EntityType<?>> ATTACK_NONE = tag("attack/none");
        public static final Tag<EntityType<?>> ATTACK_RANGED = tag("attack/ranged");
        //Different movement Types
        public static final Tag<EntityType<?>> MOVEMENT_AIR = tag("movement/air");
        public static final Tag<EntityType<?>> MOVEMENT_LAND = tag("movement/land");
        public static final Tag<EntityType<?>> MOVEMENT_MAGIC = tag("movement/magic");
        public static final Tag<EntityType<?>> MOVEMENT_WATER = tag("movement/water");

        private static Tag<EntityType<?>> tag(String name)
        {
            return ForgeRegistries.ENTITIES.getTagProvider().createWrapper(new ResourceLocation("minecraft", name));  // no need for the UnmodifiableWrapper - We already use ImmutableTags
        }

        private Entities()
        {
        }
    }

    public static final class TileEntities {
        //Should be used by mods who want to copy TileEntities - Other mods, please add your Tiles to it, if it doesn't harm for them to be copied (f.e. decorative Tiles)
        public static final Tag<TileEntityType<?>> ALLOW_COPY = tag("allow_copy");

        private static Tag<TileEntityType<?>> tag(String name)
        {
            return ForgeRegistries.TILE_ENTITIES.getTagProvider().createWrapper(new ResourceLocation("minecraft", name));  // no need for the UnmodifiableWrapper - We already use ImmutableTags
        }

        private TileEntities()
        {
        }
    }
}
