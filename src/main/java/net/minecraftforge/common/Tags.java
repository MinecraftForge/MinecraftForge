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
        public static final Tag<Block> END_STONES = tag("end_stones");
        public static final Tag<Block> FENCE_GATES = tag("fence_gates");
        public static final Tag<Block> FENCE_GATES_WOODEN = tag("fence_gates/wooden");
        public static final Tag<Block> FENCES = tag("fences");
        public static final Tag<Block> FENCES_NETHER_BRICK = tag("fences/nether_brick");
        public static final Tag<Block> FENCES_WOODEN = tag("fences/wooden");

        public static final Tag<Block> GLASS = tag("glass");
        public static final Tag<Block> GLASS_BLACK = tag("glass/black");
        public static final Tag<Block> GLASS_BLUE = tag("glass/blue");
        public static final Tag<Block> GLASS_BROWN = tag("glass/brown");
        public static final Tag<Block> GLASS_COLORLESS = tag("glass/colorless");
        public static final Tag<Block> GLASS_CYAN = tag("glass/cyan");
        public static final Tag<Block> GLASS_GRAY = tag("glass/gray");
        public static final Tag<Block> GLASS_GREEN = tag("glass/green");
        public static final Tag<Block> GLASS_LIGHT_BLUE = tag("glass/light_blue");
        public static final Tag<Block> GLASS_LIGHT_GRAY = tag("glass/light_gray");
        public static final Tag<Block> GLASS_LIME = tag("glass/lime");
        public static final Tag<Block> GLASS_MAGENTA = tag("glass/magenta");
        public static final Tag<Block> GLASS_ORANGE = tag("glass/orange");
        public static final Tag<Block> GLASS_PINK = tag("glass/pink");
        public static final Tag<Block> GLASS_PURPLE = tag("glass/purple");
        public static final Tag<Block> GLASS_RED = tag("glass/red");
        public static final Tag<Block> GLASS_WHITE = tag("glass/white");
        public static final Tag<Block> GLASS_YELLOW = tag("glass/yellow");

        public static final Tag<Block> GLASS_PANES = tag("glass_panes");
        public static final Tag<Block> GLASS_PANES_BLACK = tag("glass_panes/black");
        public static final Tag<Block> GLASS_PANES_BLUE = tag("glass_panes/blue");
        public static final Tag<Block> GLASS_PANES_BROWN = tag("glass_panes/brown");
        public static final Tag<Block> GLASS_PANES_COLORLESS = tag("glass_panes/colorless");
        public static final Tag<Block> GLASS_PANES_CYAN = tag("glass_panes/cyan");
        public static final Tag<Block> GLASS_PANES_GRAY = tag("glass_panes/gray");
        public static final Tag<Block> GLASS_PANES_GREEN = tag("glass_panes/green");
        public static final Tag<Block> GLASS_PANES_LIGHT_BLUE = tag("glass_panes/light_blue");
        public static final Tag<Block> GLASS_PANES_LIGHT_GRAY = tag("glass_panes/light_gray");
        public static final Tag<Block> GLASS_PANES_LIME = tag("glass_panes/lime");
        public static final Tag<Block> GLASS_PANES_MAGENTA = tag("glass_panes/magenta");
        public static final Tag<Block> GLASS_PANES_ORANGE = tag("glass_panes/orange");
        public static final Tag<Block> GLASS_PANES_PINK = tag("glass_panes/pink");
        public static final Tag<Block> GLASS_PANES_PURPLE = tag("glass_panes/purple");
        public static final Tag<Block> GLASS_PANES_RED = tag("glass_panes/red");
        public static final Tag<Block> GLASS_PANES_WHITE = tag("glass_panes/white");
        public static final Tag<Block> GLASS_PANES_YELLOW = tag("glass_panes/yellow");

        public static final Tag<Block> GRAVEL = tag("gravel");
        public static final Tag<Block> NETHERRACK = tag("netherrack");
        public static final Tag<Block> OBSIDIAN = tag("obsidian");
        public static final Tag<Block> ORES = tag("ores");
        public static final Tag<Block> ORES_COAL = tag("ores/coal");
        public static final Tag<Block> ORES_DIAMOND = tag("ores/diamond");
        public static final Tag<Block> ORES_EMERALD = tag("ores/emerald");
        public static final Tag<Block> ORES_GOLD = tag("ores/gold");
        public static final Tag<Block> ORES_IRON = tag("ores/iron");
        public static final Tag<Block> ORES_LAPIS = tag("ores/lapis");
        public static final Tag<Block> ORES_QUARTZ = tag("ores/quartz");
        public static final Tag<Block> ORES_REDSTONE = tag("ores/redstone");

        public static final Tag<Block> SAND = tag("sand");
        public static final Tag<Block> SAND_COLORLESS = tag("sand/colorless");
        public static final Tag<Block> SAND_RED = tag("sand/red");

        public static final Tag<Block> SANDSTONE = tag("sandstone");
        public static final Tag<Block> STAINED_GLASS = tag("stained_glass");
        public static final Tag<Block> STAINED_GLASS_PANES = tag("stained_glass_panes");
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
        public static final Tag<Item> ARROWS = tag("arrows");
        public static final Tag<Item> BEACON_PAYMENT = tag("beacon_payment");
        public static final Tag<Item> BONES = tag("bones");
        public static final Tag<Item> BOOKSHELVES = tag("bookshelves");
        public static final Tag<Item> CHESTS = tag("chests");
        public static final Tag<Item> CHESTS_ENDER = tag("chests/ender");
        public static final Tag<Item> CHESTS_TRAPPED = tag("chests/trapped");
        public static final Tag<Item> CHESTS_WOODEN = tag("chests/wooden");
        public static final Tag<Item> COBBLESTONE = tag("cobblestone");
        public static final Tag<Item> CROPS = tag("crops");
        public static final Tag<Item> CROPS_BEETROOT = tag("crops/beetroot");
        public static final Tag<Item> CROPS_CARROT = tag("crops/carrot");
        public static final Tag<Item> CROPS_NETHER_WART = tag("crops/nether_wart");
        public static final Tag<Item> CROPS_POTATO = tag("crops/potato");
        public static final Tag<Item> CROPS_WHEAT = tag("crops/wheat");
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

        public static final Tag<Item> EGGS = tag("eggs");
        public static final Tag<Item> END_STONES = tag("end_stones");
        public static final Tag<Item> ENDER_PEARLS = tag("ender_pearls");
        public static final Tag<Item> FEATHERS = tag("feathers");
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

        public static final Tag<Item> GLASS = tag("glass");
        public static final Tag<Item> GLASS_BLACK = tag("glass/black");
        public static final Tag<Item> GLASS_BLUE = tag("glass/blue");
        public static final Tag<Item> GLASS_BROWN = tag("glass/brown");
        public static final Tag<Item> GLASS_COLORLESS = tag("glass/colorless");
        public static final Tag<Item> GLASS_CYAN = tag("glass/cyan");
        public static final Tag<Item> GLASS_GRAY = tag("glass/gray");
        public static final Tag<Item> GLASS_GREEN = tag("glass/green");
        public static final Tag<Item> GLASS_LIGHT_BLUE = tag("glass/light_blue");
        public static final Tag<Item> GLASS_LIGHT_GRAY = tag("glass/light_gray");
        public static final Tag<Item> GLASS_LIME = tag("glass/lime");
        public static final Tag<Item> GLASS_MAGENTA = tag("glass/magenta");
        public static final Tag<Item> GLASS_ORANGE = tag("glass/orange");
        public static final Tag<Item> GLASS_PINK = tag("glass/pink");
        public static final Tag<Item> GLASS_PURPLE = tag("glass/purple");
        public static final Tag<Item> GLASS_RED = tag("glass/red");
        public static final Tag<Item> GLASS_WHITE = tag("glass/white");
        public static final Tag<Item> GLASS_YELLOW = tag("glass/yellow");

        public static final Tag<Item> GLASS_PANES = tag("glass_panes");
        public static final Tag<Item> GLASS_PANES_BLACK = tag("glass_panes/black");
        public static final Tag<Item> GLASS_PANES_BLUE = tag("glass_panes/blue");
        public static final Tag<Item> GLASS_PANES_BROWN = tag("glass_panes/brown");
        public static final Tag<Item> GLASS_PANES_COLORLESS = tag("glass_panes/colorless");
        public static final Tag<Item> GLASS_PANES_CYAN = tag("glass_panes/cyan");
        public static final Tag<Item> GLASS_PANES_GRAY = tag("glass_panes/gray");
        public static final Tag<Item> GLASS_PANES_GREEN = tag("glass_panes/green");
        public static final Tag<Item> GLASS_PANES_LIGHT_BLUE = tag("glass_panes/light_blue");
        public static final Tag<Item> GLASS_PANES_LIGHT_GRAY = tag("glass_panes/light_gray");
        public static final Tag<Item> GLASS_PANES_LIME = tag("glass_panes/lime");
        public static final Tag<Item> GLASS_PANES_MAGENTA = tag("glass_panes/magenta");
        public static final Tag<Item> GLASS_PANES_ORANGE = tag("glass_panes/orange");
        public static final Tag<Item> GLASS_PANES_PINK = tag("glass_panes/pink");
        public static final Tag<Item> GLASS_PANES_PURPLE = tag("glass_panes/purple");
        public static final Tag<Item> GLASS_PANES_RED = tag("glass_panes/red");
        public static final Tag<Item> GLASS_PANES_WHITE = tag("glass_panes/white");
        public static final Tag<Item> GLASS_PANES_YELLOW = tag("glass_panes/yellow");

        public static final Tag<Item> GRAVEL = tag("gravel");
        public static final Tag<Item> GUNPOWDER = tag("gunpowder");
        public static final Tag<Item> HEADS = tag("heads");
        public static final Tag<Item> INGOTS = tag("ingots");
        public static final Tag<Item> INGOTS_BRICK = tag("ingots/brick");
        public static final Tag<Item> INGOTS_GOLD = tag("ingots/gold");
        public static final Tag<Item> INGOTS_IRON = tag("ingots/iron");
        public static final Tag<Item> INGOTS_NETHER_BRICK = tag("ingots/nether_brick");
        public static final Tag<Item> LEATHER = tag("leather");
        public static final Tag<Item> MUSHROOMS = tag("mushrooms");
        public static final Tag<Item> MUSIC_DISCS = tag("music_discs");
        public static final Tag<Item> NETHER_STARS = tag("nether_stars");
        public static final Tag<Item> NETHERRACK = tag("netherrack");
        public static final Tag<Item> NUGGETS = tag("nuggets");
        public static final Tag<Item> NUGGETS_GOLD = tag("nuggets/gold");
        public static final Tag<Item> NUGGETS_IRON = tag("nuggets/iron");
        public static final Tag<Item> OBSIDIAN = tag("obsidian");
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

        public static final Tag<Item> SAND = tag("sand");
        public static final Tag<Item> SAND_COLORLESS = tag("sand/colorless");
        public static final Tag<Item> SAND_RED = tag("sand/red");

        public static final Tag<Item> SANDSTONE = tag("sandstone");
        public static final Tag<Item> SEEDS = tag("seeds");
        public static final Tag<Item> SEEDS_BEETROOT = tag("seeds/beetroot");
        public static final Tag<Item> SEEDS_MELON = tag("seeds/melon");
        public static final Tag<Item> SEEDS_PUMPKIN = tag("seeds/pumpkin");
        public static final Tag<Item> SEEDS_WHEAT = tag("seeds/wheat");
        public static final Tag<Item> SHEARS = tag("shears");
        public static final Tag<Item> SLIMEBALLS = tag("slimeballs");
        public static final Tag<Item> STAINED_GLASS = tag("stained_glass");
        public static final Tag<Item> STAINED_GLASS_PANES = tag("stained_glass_panes");
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
        public static final Tag<Item> STRING = tag("string");

        private static Tag<Item> tag(String name)
        {
            return new ItemTags.Wrapper(new ResourceLocation("forge", name));
        }
    }
}
