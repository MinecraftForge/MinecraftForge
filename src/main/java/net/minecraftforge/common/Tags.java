/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

import java.util.Arrays;
import java.util.stream.Collectors;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.util.ForgeNetworkTagManager;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class Tags
{
    public static void init ()
    {
        Blocks.init();
        Items.init();
    }

    public static class Blocks
    {
        private static void init(){}

        public static final ITag.INamedTag<Block> CHESTS = tag("chests");
        public static final ITag.INamedTag<Block> CHESTS_ENDER = tag("chests/ender");
        public static final ITag.INamedTag<Block> CHESTS_TRAPPED = tag("chests/trapped");
        public static final ITag.INamedTag<Block> CHESTS_WOODEN = tag("chests/wooden");
        public static final ITag.INamedTag<Block> COBBLESTONE = tag("cobblestone");
        public static final ITag.INamedTag<Block> DIRT = tag("dirt");
        public static final ITag.INamedTag<Block> END_STONES = tag("end_stones");
        public static final ITag.INamedTag<Block> FENCE_GATES = tag("fence_gates");
        public static final ITag.INamedTag<Block> FENCE_GATES_WOODEN = defaulted("fence_gates/wooden", net.minecraft.block.Blocks.OAK_FENCE_GATE, net.minecraft.block.Blocks.BIRCH_FENCE_GATE, net.minecraft.block.Blocks.SPRUCE_FENCE_GATE, net.minecraft.block.Blocks.JUNGLE_FENCE_GATE, net.minecraft.block.Blocks.DARK_OAK_FENCE_GATE, net.minecraft.block.Blocks.ACACIA_FENCE_GATE);
        public static final ITag.INamedTag<Block> FENCES = tag("fences");
        public static final ITag.INamedTag<Block> FENCES_NETHER_BRICK = tag("fences/nether_brick");
        public static final ITag.INamedTag<Block> FENCES_WOODEN = defaulted("fences/wooden", net.minecraft.block.Blocks.OAK_FENCE, net.minecraft.block.Blocks.BIRCH_FENCE, net.minecraft.block.Blocks.SPRUCE_FENCE, net.minecraft.block.Blocks.JUNGLE_FENCE, net.minecraft.block.Blocks.DARK_OAK_FENCE, net.minecraft.block.Blocks.ACACIA_FENCE);

        public static final ITag.INamedTag<Block> GLASS = tag("glass");
        public static final ITag.INamedTag<Block> GLASS_BLACK = tag("glass/black");
        public static final ITag.INamedTag<Block> GLASS_BLUE = tag("glass/blue");
        public static final ITag.INamedTag<Block> GLASS_BROWN = tag("glass/brown");
        public static final ITag.INamedTag<Block> GLASS_COLORLESS = tag("glass/colorless");
        public static final ITag.INamedTag<Block> GLASS_CYAN = tag("glass/cyan");
        public static final ITag.INamedTag<Block> GLASS_GRAY = tag("glass/gray");
        public static final ITag.INamedTag<Block> GLASS_GREEN = tag("glass/green");
        public static final ITag.INamedTag<Block> GLASS_LIGHT_BLUE = tag("glass/light_blue");
        public static final ITag.INamedTag<Block> GLASS_LIGHT_GRAY = tag("glass/light_gray");
        public static final ITag.INamedTag<Block> GLASS_LIME = tag("glass/lime");
        public static final ITag.INamedTag<Block> GLASS_MAGENTA = tag("glass/magenta");
        public static final ITag.INamedTag<Block> GLASS_ORANGE = tag("glass/orange");
        public static final ITag.INamedTag<Block> GLASS_PINK = tag("glass/pink");
        public static final ITag.INamedTag<Block> GLASS_PURPLE = tag("glass/purple");
        public static final ITag.INamedTag<Block> GLASS_RED = tag("glass/red");
        public static final ITag.INamedTag<Block> GLASS_WHITE = tag("glass/white");
        public static final ITag.INamedTag<Block> GLASS_YELLOW = tag("glass/yellow");

        public static final ITag.INamedTag<Block> GLASS_PANES = tag("glass_panes");
        public static final ITag.INamedTag<Block> GLASS_PANES_BLACK = tag("glass_panes/black");
        public static final ITag.INamedTag<Block> GLASS_PANES_BLUE = tag("glass_panes/blue");
        public static final ITag.INamedTag<Block> GLASS_PANES_BROWN = tag("glass_panes/brown");
        public static final ITag.INamedTag<Block> GLASS_PANES_COLORLESS = tag("glass_panes/colorless");
        public static final ITag.INamedTag<Block> GLASS_PANES_CYAN = tag("glass_panes/cyan");
        public static final ITag.INamedTag<Block> GLASS_PANES_GRAY = tag("glass_panes/gray");
        public static final ITag.INamedTag<Block> GLASS_PANES_GREEN = tag("glass_panes/green");
        public static final ITag.INamedTag<Block> GLASS_PANES_LIGHT_BLUE = tag("glass_panes/light_blue");
        public static final ITag.INamedTag<Block> GLASS_PANES_LIGHT_GRAY = tag("glass_panes/light_gray");
        public static final ITag.INamedTag<Block> GLASS_PANES_LIME = tag("glass_panes/lime");
        public static final ITag.INamedTag<Block> GLASS_PANES_MAGENTA = tag("glass_panes/magenta");
        public static final ITag.INamedTag<Block> GLASS_PANES_ORANGE = tag("glass_panes/orange");
        public static final ITag.INamedTag<Block> GLASS_PANES_PINK = tag("glass_panes/pink");
        public static final ITag.INamedTag<Block> GLASS_PANES_PURPLE = tag("glass_panes/purple");
        public static final ITag.INamedTag<Block> GLASS_PANES_RED = tag("glass_panes/red");
        public static final ITag.INamedTag<Block> GLASS_PANES_WHITE = tag("glass_panes/white");
        public static final ITag.INamedTag<Block> GLASS_PANES_YELLOW = tag("glass_panes/yellow");

        public static final ITag.INamedTag<Block> GRAVEL = tag("gravel");
        public static final ITag.INamedTag<Block> NETHERRACK = tag("netherrack");
        public static final ITag.INamedTag<Block> OBSIDIAN = tag("obsidian");
        public static final ITag.INamedTag<Block> ORES = tag("ores");
        public static final ITag.INamedTag<Block> ORES_COAL = tag("ores/coal");
        public static final ITag.INamedTag<Block> ORES_DIAMOND = tag("ores/diamond");
        public static final ITag.INamedTag<Block> ORES_EMERALD = tag("ores/emerald");
        public static final ITag.INamedTag<Block> ORES_GOLD = tag("ores/gold");
        public static final ITag.INamedTag<Block> ORES_IRON = tag("ores/iron");
        public static final ITag.INamedTag<Block> ORES_LAPIS = tag("ores/lapis");
        public static final ITag.INamedTag<Block> ORES_QUARTZ = tag("ores/quartz");
        public static final ITag.INamedTag<Block> ORES_REDSTONE = tag("ores/redstone");

        public static final ITag.INamedTag<Block> SAND = tag("sand");
        public static final ITag.INamedTag<Block> SAND_COLORLESS = tag("sand/colorless");
        public static final ITag.INamedTag<Block> SAND_RED = tag("sand/red");

        public static final ITag.INamedTag<Block> SANDSTONE = tag("sandstone");
        public static final ITag.INamedTag<Block> STAINED_GLASS = tag("stained_glass");
        public static final ITag.INamedTag<Block> STAINED_GLASS_PANES = tag("stained_glass_panes");
        public static final ITag.INamedTag<Block> STONE = tag("stone");
        public static final ITag.INamedTag<Block> STORAGE_BLOCKS = tag("storage_blocks");
        public static final ITag.INamedTag<Block> STORAGE_BLOCKS_COAL = tag("storage_blocks/coal");
        public static final ITag.INamedTag<Block> STORAGE_BLOCKS_DIAMOND = tag("storage_blocks/diamond");
        public static final ITag.INamedTag<Block> STORAGE_BLOCKS_EMERALD = tag("storage_blocks/emerald");
        public static final ITag.INamedTag<Block> STORAGE_BLOCKS_GOLD = tag("storage_blocks/gold");
        public static final ITag.INamedTag<Block> STORAGE_BLOCKS_IRON = tag("storage_blocks/iron");
        public static final ITag.INamedTag<Block> STORAGE_BLOCKS_LAPIS = tag("storage_blocks/lapis");
        public static final ITag.INamedTag<Block> STORAGE_BLOCKS_QUARTZ = tag("storage_blocks/quartz");
        public static final ITag.INamedTag<Block> STORAGE_BLOCKS_REDSTONE = tag("storage_blocks/redstone");

        private static ITag.INamedTag<Block> tag(String name)
        {
            return BlockTags.makeWrapperTag("forge:" + name);
        }

        @SuppressWarnings("deprecation")
        private static ITag.INamedTag<Block> defaulted(String name, Block... values)
        {
            return ForgeNetworkTagManager.defaulted(Registry.field_239711_l_, tag(name), Arrays.asList(values).stream().map(e -> e.getRegistryName()).collect(Collectors.toSet()));
        }
    }

    public static class Items
    {
        private static void init(){}

        public static final ITag.INamedTag<Item> ARROWS = tag("arrows");
        public static final ITag.INamedTag<Item> BONES = tag("bones");
        public static final ITag.INamedTag<Item> BOOKSHELVES = tag("bookshelves");
        public static final ITag.INamedTag<Item> CHESTS = tag("chests");
        public static final ITag.INamedTag<Item> CHESTS_ENDER = tag("chests/ender");
        public static final ITag.INamedTag<Item> CHESTS_TRAPPED = tag("chests/trapped");
        public static final ITag.INamedTag<Item> CHESTS_WOODEN = tag("chests/wooden");
        public static final ITag.INamedTag<Item> COBBLESTONE = tag("cobblestone");
        public static final ITag.INamedTag<Item> CROPS = tag("crops");
        public static final ITag.INamedTag<Item> CROPS_BEETROOT = tag("crops/beetroot");
        public static final ITag.INamedTag<Item> CROPS_CARROT = tag("crops/carrot");
        public static final ITag.INamedTag<Item> CROPS_NETHER_WART = tag("crops/nether_wart");
        public static final ITag.INamedTag<Item> CROPS_POTATO = tag("crops/potato");
        public static final ITag.INamedTag<Item> CROPS_WHEAT = tag("crops/wheat");
        public static final ITag.INamedTag<Item> DUSTS = tag("dusts");
        public static final ITag.INamedTag<Item> DUSTS_PRISMARINE = tag("dusts/prismarine");
        public static final ITag.INamedTag<Item> DUSTS_REDSTONE = tag("dusts/redstone");
        public static final ITag.INamedTag<Item> DUSTS_GLOWSTONE = tag("dusts/glowstone");

        public static final ITag.INamedTag<Item> DYES = tag("dyes");
        public static final ITag.INamedTag<Item> DYES_BLACK = tag("dyes/black");
        public static final ITag.INamedTag<Item> DYES_RED = tag("dyes/red");
        public static final ITag.INamedTag<Item> DYES_GREEN = tag("dyes/green");
        public static final ITag.INamedTag<Item> DYES_BROWN = tag("dyes/brown");
        public static final ITag.INamedTag<Item> DYES_BLUE = tag("dyes/blue");
        public static final ITag.INamedTag<Item> DYES_PURPLE = tag("dyes/purple");
        public static final ITag.INamedTag<Item> DYES_CYAN = tag("dyes/cyan");
        public static final ITag.INamedTag<Item> DYES_LIGHT_GRAY = tag("dyes/light_gray");
        public static final ITag.INamedTag<Item> DYES_GRAY = tag("dyes/gray");
        public static final ITag.INamedTag<Item> DYES_PINK = tag("dyes/pink");
        public static final ITag.INamedTag<Item> DYES_LIME = tag("dyes/lime");
        public static final ITag.INamedTag<Item> DYES_YELLOW = tag("dyes/yellow");
        public static final ITag.INamedTag<Item> DYES_LIGHT_BLUE = tag("dyes/light_blue");
        public static final ITag.INamedTag<Item> DYES_MAGENTA = tag("dyes/magenta");
        public static final ITag.INamedTag<Item> DYES_ORANGE = tag("dyes/orange");
        public static final ITag.INamedTag<Item> DYES_WHITE = tag("dyes/white");

        public static final ITag.INamedTag<Item> EGGS = tag("eggs");
        public static final ITag.INamedTag<Item> END_STONES = tag("end_stones");
        public static final ITag.INamedTag<Item> ENDER_PEARLS = tag("ender_pearls");
        public static final ITag.INamedTag<Item> FEATHERS = tag("feathers");
        public static final ITag.INamedTag<Item> FENCE_GATES = tag("fence_gates");
        public static final ITag.INamedTag<Item> FENCE_GATES_WOODEN = defaulted("fence_gates/wooden", Blocks.FENCE_GATES_WOODEN);
        public static final ITag.INamedTag<Item> FENCES = tag("fences");
        public static final ITag.INamedTag<Item> FENCES_NETHER_BRICK = tag("fences/nether_brick");
        public static final ITag.INamedTag<Item> FENCES_WOODEN = defaulted("fences/wooden", Blocks.FENCES_WOODEN);
        public static final ITag.INamedTag<Item> GEMS = tag("gems");
        public static final ITag.INamedTag<Item> GEMS_DIAMOND = tag("gems/diamond");
        public static final ITag.INamedTag<Item> GEMS_EMERALD = tag("gems/emerald");
        public static final ITag.INamedTag<Item> GEMS_LAPIS = tag("gems/lapis");
        public static final ITag.INamedTag<Item> GEMS_PRISMARINE = tag("gems/prismarine");
        public static final ITag.INamedTag<Item> GEMS_QUARTZ = tag("gems/quartz");

        public static final ITag.INamedTag<Item> GLASS = tag("glass");
        public static final ITag.INamedTag<Item> GLASS_BLACK = tag("glass/black");
        public static final ITag.INamedTag<Item> GLASS_BLUE = tag("glass/blue");
        public static final ITag.INamedTag<Item> GLASS_BROWN = tag("glass/brown");
        public static final ITag.INamedTag<Item> GLASS_COLORLESS = tag("glass/colorless");
        public static final ITag.INamedTag<Item> GLASS_CYAN = tag("glass/cyan");
        public static final ITag.INamedTag<Item> GLASS_GRAY = tag("glass/gray");
        public static final ITag.INamedTag<Item> GLASS_GREEN = tag("glass/green");
        public static final ITag.INamedTag<Item> GLASS_LIGHT_BLUE = tag("glass/light_blue");
        public static final ITag.INamedTag<Item> GLASS_LIGHT_GRAY = tag("glass/light_gray");
        public static final ITag.INamedTag<Item> GLASS_LIME = tag("glass/lime");
        public static final ITag.INamedTag<Item> GLASS_MAGENTA = tag("glass/magenta");
        public static final ITag.INamedTag<Item> GLASS_ORANGE = tag("glass/orange");
        public static final ITag.INamedTag<Item> GLASS_PINK = tag("glass/pink");
        public static final ITag.INamedTag<Item> GLASS_PURPLE = tag("glass/purple");
        public static final ITag.INamedTag<Item> GLASS_RED = tag("glass/red");
        public static final ITag.INamedTag<Item> GLASS_WHITE = tag("glass/white");
        public static final ITag.INamedTag<Item> GLASS_YELLOW = tag("glass/yellow");

        public static final ITag.INamedTag<Item> GLASS_PANES = tag("glass_panes");
        public static final ITag.INamedTag<Item> GLASS_PANES_BLACK = tag("glass_panes/black");
        public static final ITag.INamedTag<Item> GLASS_PANES_BLUE = tag("glass_panes/blue");
        public static final ITag.INamedTag<Item> GLASS_PANES_BROWN = tag("glass_panes/brown");
        public static final ITag.INamedTag<Item> GLASS_PANES_COLORLESS = tag("glass_panes/colorless");
        public static final ITag.INamedTag<Item> GLASS_PANES_CYAN = tag("glass_panes/cyan");
        public static final ITag.INamedTag<Item> GLASS_PANES_GRAY = tag("glass_panes/gray");
        public static final ITag.INamedTag<Item> GLASS_PANES_GREEN = tag("glass_panes/green");
        public static final ITag.INamedTag<Item> GLASS_PANES_LIGHT_BLUE = tag("glass_panes/light_blue");
        public static final ITag.INamedTag<Item> GLASS_PANES_LIGHT_GRAY = tag("glass_panes/light_gray");
        public static final ITag.INamedTag<Item> GLASS_PANES_LIME = tag("glass_panes/lime");
        public static final ITag.INamedTag<Item> GLASS_PANES_MAGENTA = tag("glass_panes/magenta");
        public static final ITag.INamedTag<Item> GLASS_PANES_ORANGE = tag("glass_panes/orange");
        public static final ITag.INamedTag<Item> GLASS_PANES_PINK = tag("glass_panes/pink");
        public static final ITag.INamedTag<Item> GLASS_PANES_PURPLE = tag("glass_panes/purple");
        public static final ITag.INamedTag<Item> GLASS_PANES_RED = tag("glass_panes/red");
        public static final ITag.INamedTag<Item> GLASS_PANES_WHITE = tag("glass_panes/white");
        public static final ITag.INamedTag<Item> GLASS_PANES_YELLOW = tag("glass_panes/yellow");

        public static final ITag.INamedTag<Item> GRAVEL = tag("gravel");
        public static final ITag.INamedTag<Item> GUNPOWDER = tag("gunpowder");
        public static final ITag.INamedTag<Item> HEADS = tag("heads");
        public static final ITag.INamedTag<Item> INGOTS = tag("ingots");
        public static final ITag.INamedTag<Item> INGOTS_BRICK = tag("ingots/brick");
        public static final ITag.INamedTag<Item> INGOTS_GOLD = tag("ingots/gold");
        public static final ITag.INamedTag<Item> INGOTS_IRON = tag("ingots/iron");
        public static final ITag.INamedTag<Item> INGOTS_NETHER_BRICK = tag("ingots/nether_brick");
        public static final ITag.INamedTag<Item> LEATHER = tag("leather");
        public static final ITag.INamedTag<Item> MUSHROOMS = tag("mushrooms");
        public static final ITag.INamedTag<Item> MUSIC_DISCS = tag("music_discs");
        public static final ITag.INamedTag<Item> NETHER_STARS = tag("nether_stars");
        public static final ITag.INamedTag<Item> NETHERRACK = tag("netherrack");
        public static final ITag.INamedTag<Item> NUGGETS = tag("nuggets");
        public static final ITag.INamedTag<Item> NUGGETS_GOLD = tag("nuggets/gold");
        public static final ITag.INamedTag<Item> NUGGETS_IRON = tag("nuggets/iron");
        public static final ITag.INamedTag<Item> OBSIDIAN = tag("obsidian");
        public static final ITag.INamedTag<Item> ORES = tag("ores");
        public static final ITag.INamedTag<Item> ORES_COAL = tag("ores/coal");
        public static final ITag.INamedTag<Item> ORES_DIAMOND = tag("ores/diamond");
        public static final ITag.INamedTag<Item> ORES_EMERALD = tag("ores/emerald");
        public static final ITag.INamedTag<Item> ORES_GOLD = tag("ores/gold");
        public static final ITag.INamedTag<Item> ORES_IRON = tag("ores/iron");
        public static final ITag.INamedTag<Item> ORES_LAPIS = tag("ores/lapis");
        public static final ITag.INamedTag<Item> ORES_QUARTZ = tag("ores/quartz");
        public static final ITag.INamedTag<Item> ORES_REDSTONE = tag("ores/redstone");
        public static final ITag.INamedTag<Item> RODS = tag("rods");
        public static final ITag.INamedTag<Item> RODS_BLAZE = tag("rods/blaze");
        public static final ITag.INamedTag<Item> RODS_WOODEN = tag("rods/wooden");

        public static final ITag.INamedTag<Item> SAND = tag("sand");
        public static final ITag.INamedTag<Item> SAND_COLORLESS = tag("sand/colorless");
        public static final ITag.INamedTag<Item> SAND_RED = tag("sand/red");

        public static final ITag.INamedTag<Item> SANDSTONE = tag("sandstone");
        public static final ITag.INamedTag<Item> SEEDS = tag("seeds");
        public static final ITag.INamedTag<Item> SEEDS_BEETROOT = tag("seeds/beetroot");
        public static final ITag.INamedTag<Item> SEEDS_MELON = tag("seeds/melon");
        public static final ITag.INamedTag<Item> SEEDS_PUMPKIN = tag("seeds/pumpkin");
        public static final ITag.INamedTag<Item> SEEDS_WHEAT = tag("seeds/wheat");
        public static final ITag.INamedTag<Item> SHEARS = tag("shears");
        public static final ITag.INamedTag<Item> SLIMEBALLS = tag("slimeballs");
        public static final ITag.INamedTag<Item> STAINED_GLASS = tag("stained_glass");
        public static final ITag.INamedTag<Item> STAINED_GLASS_PANES = tag("stained_glass_panes");
        public static final ITag.INamedTag<Item> STONE = tag("stone");
        public static final ITag.INamedTag<Item> STORAGE_BLOCKS = tag("storage_blocks");
        public static final ITag.INamedTag<Item> STORAGE_BLOCKS_COAL = tag("storage_blocks/coal");
        public static final ITag.INamedTag<Item> STORAGE_BLOCKS_DIAMOND = tag("storage_blocks/diamond");
        public static final ITag.INamedTag<Item> STORAGE_BLOCKS_EMERALD = tag("storage_blocks/emerald");
        public static final ITag.INamedTag<Item> STORAGE_BLOCKS_GOLD = tag("storage_blocks/gold");
        public static final ITag.INamedTag<Item> STORAGE_BLOCKS_IRON = tag("storage_blocks/iron");
        public static final ITag.INamedTag<Item> STORAGE_BLOCKS_LAPIS = tag("storage_blocks/lapis");
        public static final ITag.INamedTag<Item> STORAGE_BLOCKS_QUARTZ = tag("storage_blocks/quartz");
        public static final ITag.INamedTag<Item> STORAGE_BLOCKS_REDSTONE = tag("storage_blocks/redstone");
        public static final ITag.INamedTag<Item> STRING = tag("string");

        private static ITag.INamedTag<Item> tag(String name)
        {
            return ItemTags.makeWrapperTag("forge:" + name);
        }

        @SuppressWarnings("deprecation")
        private static ITag.INamedTag<Item> defaulted(String name, ITag.INamedTag<Block> parent)
        {
            return ForgeNetworkTagManager.defaulted(Registry.field_239714_o_, tag(name), RegistryKey.func_240903_a_(Registry.field_239711_l_, parent.func_230234_a_()));
        }
    }
}
