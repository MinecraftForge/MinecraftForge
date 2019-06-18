/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

package net.minecraftforge.common;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

public class Tags
{
    public static class Blocks
    {
        public static final Tag<Block> CHESTS = tag("chests");
        public static final Tag<Block> CHESTS_ENDER = tag("chests/ender");
        public static final Tag<Block> CHESTS_TRAPPED = tag("chests/trapped");
        public static final Tag<Block> CHESTS_WOODEN = tag("chests/wooden");
        public static final Tag<Block> COBBLESTONE = tag("cobblestone");
        public static final Tag<Block> DIRT = tag("dirt");
        public static final Tag<Block> FENCE_GATES = tag("fence_gates");
        public static final Tag<Block> FENCE_GATES_WOODEN = tag("fence_gates/wooden");
        public static final Tag<Block> FENCES = tag("fences");
        public static final Tag<Block> FENCES_NETHER_BRICK = tag("fences/nether_brick");
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
            return new BlockTags.Wrapper(new ResourceLocation("forge", name));
        }
    }

    public static class Items
    {
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
        public static final Tag<Item> FENCES_NETHER_BRICK = tag("fences/nether_brick");
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
            return new ItemTags.Wrapper(new ResourceLocation("forge", name));
        }
    }
}
