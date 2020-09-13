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

import net.minecraft.block.Block;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.tags.ITag.INamedTag;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.registries.ForgeRegistries;

public class Tags
{
    public static void init ()
    {
        Blocks.init();
        Items.init();
        Biomes.init();
    }

    public static class Blocks
    {
        private static void init(){}

        public static final IOptionalNamedTag<Block> CHESTS = tag("chests");
        public static final IOptionalNamedTag<Block> CHESTS_ENDER = tag("chests/ender");
        public static final IOptionalNamedTag<Block> CHESTS_TRAPPED = tag("chests/trapped");
        public static final IOptionalNamedTag<Block> CHESTS_WOODEN = tag("chests/wooden");
        public static final IOptionalNamedTag<Block> COBBLESTONE = tag("cobblestone");
        public static final IOptionalNamedTag<Block> DIRT = tag("dirt");
        public static final IOptionalNamedTag<Block> END_STONES = tag("end_stones");
        public static final IOptionalNamedTag<Block> FENCE_GATES = tag("fence_gates");
        public static final IOptionalNamedTag<Block> FENCE_GATES_WOODEN = tag("fence_gates/wooden");
        public static final IOptionalNamedTag<Block> FENCES = tag("fences");
        public static final IOptionalNamedTag<Block> FENCES_NETHER_BRICK = tag("fences/nether_brick");
        public static final IOptionalNamedTag<Block> FENCES_WOODEN = tag("fences/wooden");

        public static final IOptionalNamedTag<Block> GLASS = tag("glass");
        public static final IOptionalNamedTag<Block> GLASS_BLACK = tag("glass/black");
        public static final IOptionalNamedTag<Block> GLASS_BLUE = tag("glass/blue");
        public static final IOptionalNamedTag<Block> GLASS_BROWN = tag("glass/brown");
        public static final IOptionalNamedTag<Block> GLASS_COLORLESS = tag("glass/colorless");
        public static final IOptionalNamedTag<Block> GLASS_CYAN = tag("glass/cyan");
        public static final IOptionalNamedTag<Block> GLASS_GRAY = tag("glass/gray");
        public static final IOptionalNamedTag<Block> GLASS_GREEN = tag("glass/green");
        public static final IOptionalNamedTag<Block> GLASS_LIGHT_BLUE = tag("glass/light_blue");
        public static final IOptionalNamedTag<Block> GLASS_LIGHT_GRAY = tag("glass/light_gray");
        public static final IOptionalNamedTag<Block> GLASS_LIME = tag("glass/lime");
        public static final IOptionalNamedTag<Block> GLASS_MAGENTA = tag("glass/magenta");
        public static final IOptionalNamedTag<Block> GLASS_ORANGE = tag("glass/orange");
        public static final IOptionalNamedTag<Block> GLASS_PINK = tag("glass/pink");
        public static final IOptionalNamedTag<Block> GLASS_PURPLE = tag("glass/purple");
        public static final IOptionalNamedTag<Block> GLASS_RED = tag("glass/red");
        public static final IOptionalNamedTag<Block> GLASS_WHITE = tag("glass/white");
        public static final IOptionalNamedTag<Block> GLASS_YELLOW = tag("glass/yellow");

        public static final IOptionalNamedTag<Block> GLASS_PANES = tag("glass_panes");
        public static final IOptionalNamedTag<Block> GLASS_PANES_BLACK = tag("glass_panes/black");
        public static final IOptionalNamedTag<Block> GLASS_PANES_BLUE = tag("glass_panes/blue");
        public static final IOptionalNamedTag<Block> GLASS_PANES_BROWN = tag("glass_panes/brown");
        public static final IOptionalNamedTag<Block> GLASS_PANES_COLORLESS = tag("glass_panes/colorless");
        public static final IOptionalNamedTag<Block> GLASS_PANES_CYAN = tag("glass_panes/cyan");
        public static final IOptionalNamedTag<Block> GLASS_PANES_GRAY = tag("glass_panes/gray");
        public static final IOptionalNamedTag<Block> GLASS_PANES_GREEN = tag("glass_panes/green");
        public static final IOptionalNamedTag<Block> GLASS_PANES_LIGHT_BLUE = tag("glass_panes/light_blue");
        public static final IOptionalNamedTag<Block> GLASS_PANES_LIGHT_GRAY = tag("glass_panes/light_gray");
        public static final IOptionalNamedTag<Block> GLASS_PANES_LIME = tag("glass_panes/lime");
        public static final IOptionalNamedTag<Block> GLASS_PANES_MAGENTA = tag("glass_panes/magenta");
        public static final IOptionalNamedTag<Block> GLASS_PANES_ORANGE = tag("glass_panes/orange");
        public static final IOptionalNamedTag<Block> GLASS_PANES_PINK = tag("glass_panes/pink");
        public static final IOptionalNamedTag<Block> GLASS_PANES_PURPLE = tag("glass_panes/purple");
        public static final IOptionalNamedTag<Block> GLASS_PANES_RED = tag("glass_panes/red");
        public static final IOptionalNamedTag<Block> GLASS_PANES_WHITE = tag("glass_panes/white");
        public static final IOptionalNamedTag<Block> GLASS_PANES_YELLOW = tag("glass_panes/yellow");

        public static final IOptionalNamedTag<Block> GRAVEL = tag("gravel");
        public static final IOptionalNamedTag<Block> NETHERRACK = tag("netherrack");
        public static final IOptionalNamedTag<Block> OBSIDIAN = tag("obsidian");
        public static final IOptionalNamedTag<Block> ORES = tag("ores");
        public static final IOptionalNamedTag<Block> ORES_COAL = tag("ores/coal");
        public static final IOptionalNamedTag<Block> ORES_DIAMOND = tag("ores/diamond");
        public static final IOptionalNamedTag<Block> ORES_EMERALD = tag("ores/emerald");
        public static final IOptionalNamedTag<Block> ORES_GOLD = tag("ores/gold");
        public static final IOptionalNamedTag<Block> ORES_IRON = tag("ores/iron");
        public static final IOptionalNamedTag<Block> ORES_LAPIS = tag("ores/lapis");
        public static final IOptionalNamedTag<Block> ORES_NETHERITE_SCRAP = tag("ores/netherite_scrap");
        public static final IOptionalNamedTag<Block> ORES_QUARTZ = tag("ores/quartz");
        public static final IOptionalNamedTag<Block> ORES_REDSTONE = tag("ores/redstone");

        public static final IOptionalNamedTag<Block> SAND = tag("sand");
        public static final IOptionalNamedTag<Block> SAND_COLORLESS = tag("sand/colorless");
        public static final IOptionalNamedTag<Block> SAND_RED = tag("sand/red");

        public static final IOptionalNamedTag<Block> SANDSTONE = tag("sandstone");
        public static final IOptionalNamedTag<Block> STAINED_GLASS = tag("stained_glass");
        public static final IOptionalNamedTag<Block> STAINED_GLASS_PANES = tag("stained_glass_panes");
        public static final IOptionalNamedTag<Block> STONE = tag("stone");
        public static final IOptionalNamedTag<Block> STORAGE_BLOCKS = tag("storage_blocks");
        public static final IOptionalNamedTag<Block> STORAGE_BLOCKS_COAL = tag("storage_blocks/coal");
        public static final IOptionalNamedTag<Block> STORAGE_BLOCKS_DIAMOND = tag("storage_blocks/diamond");
        public static final IOptionalNamedTag<Block> STORAGE_BLOCKS_EMERALD = tag("storage_blocks/emerald");
        public static final IOptionalNamedTag<Block> STORAGE_BLOCKS_GOLD = tag("storage_blocks/gold");
        public static final IOptionalNamedTag<Block> STORAGE_BLOCKS_IRON = tag("storage_blocks/iron");
        public static final IOptionalNamedTag<Block> STORAGE_BLOCKS_LAPIS = tag("storage_blocks/lapis");
        public static final IOptionalNamedTag<Block> STORAGE_BLOCKS_NETHERITE = tag("storage_blocks/netherite");
        public static final IOptionalNamedTag<Block> STORAGE_BLOCKS_QUARTZ = tag("storage_blocks/quartz");
        public static final IOptionalNamedTag<Block> STORAGE_BLOCKS_REDSTONE = tag("storage_blocks/redstone");

        private static IOptionalNamedTag<Block> tag(String name)
        {
            return BlockTags.createOptional(new ResourceLocation("forge", name));
        }
    }

    public static class Items
    {
        private static void init(){}

        public static final IOptionalNamedTag<Item> BONES = tag("bones");
        public static final IOptionalNamedTag<Item> BOOKSHELVES = tag("bookshelves");
        public static final IOptionalNamedTag<Item> CHESTS = tag("chests");
        public static final IOptionalNamedTag<Item> CHESTS_ENDER = tag("chests/ender");
        public static final IOptionalNamedTag<Item> CHESTS_TRAPPED = tag("chests/trapped");
        public static final IOptionalNamedTag<Item> CHESTS_WOODEN = tag("chests/wooden");
        public static final IOptionalNamedTag<Item> COBBLESTONE = tag("cobblestone");
        public static final IOptionalNamedTag<Item> CROPS = tag("crops");
        public static final IOptionalNamedTag<Item> CROPS_BEETROOT = tag("crops/beetroot");
        public static final IOptionalNamedTag<Item> CROPS_CARROT = tag("crops/carrot");
        public static final IOptionalNamedTag<Item> CROPS_NETHER_WART = tag("crops/nether_wart");
        public static final IOptionalNamedTag<Item> CROPS_POTATO = tag("crops/potato");
        public static final IOptionalNamedTag<Item> CROPS_WHEAT = tag("crops/wheat");
        public static final IOptionalNamedTag<Item> DUSTS = tag("dusts");
        public static final IOptionalNamedTag<Item> DUSTS_PRISMARINE = tag("dusts/prismarine");
        public static final IOptionalNamedTag<Item> DUSTS_REDSTONE = tag("dusts/redstone");
        public static final IOptionalNamedTag<Item> DUSTS_GLOWSTONE = tag("dusts/glowstone");

        public static final IOptionalNamedTag<Item> DYES = tag("dyes");
        public static final IOptionalNamedTag<Item> DYES_BLACK = DyeColor.BLACK.getTag();
        public static final IOptionalNamedTag<Item> DYES_RED = DyeColor.RED.getTag();
        public static final IOptionalNamedTag<Item> DYES_GREEN = DyeColor.GREEN.getTag();
        public static final IOptionalNamedTag<Item> DYES_BROWN = DyeColor.BROWN.getTag();
        public static final IOptionalNamedTag<Item> DYES_BLUE = DyeColor.BLUE.getTag();
        public static final IOptionalNamedTag<Item> DYES_PURPLE = DyeColor.PURPLE.getTag();
        public static final IOptionalNamedTag<Item> DYES_CYAN = DyeColor.CYAN.getTag();
        public static final IOptionalNamedTag<Item> DYES_LIGHT_GRAY = DyeColor.LIGHT_GRAY.getTag();
        public static final IOptionalNamedTag<Item> DYES_GRAY = DyeColor.GRAY.getTag();
        public static final IOptionalNamedTag<Item> DYES_PINK = DyeColor.PINK.getTag();
        public static final IOptionalNamedTag<Item> DYES_LIME = DyeColor.LIME.getTag();
        public static final IOptionalNamedTag<Item> DYES_YELLOW = DyeColor.YELLOW.getTag();
        public static final IOptionalNamedTag<Item> DYES_LIGHT_BLUE = DyeColor.LIGHT_BLUE.getTag();
        public static final IOptionalNamedTag<Item> DYES_MAGENTA = DyeColor.MAGENTA.getTag();
        public static final IOptionalNamedTag<Item> DYES_ORANGE = DyeColor.ORANGE.getTag();
        public static final IOptionalNamedTag<Item> DYES_WHITE = DyeColor.WHITE.getTag();

        public static final IOptionalNamedTag<Item> EGGS = tag("eggs");
        public static final IOptionalNamedTag<Item> END_STONES = tag("end_stones");
        public static final IOptionalNamedTag<Item> ENDER_PEARLS = tag("ender_pearls");
        public static final IOptionalNamedTag<Item> FEATHERS = tag("feathers");
        public static final IOptionalNamedTag<Item> FENCE_GATES = tag("fence_gates");
        public static final IOptionalNamedTag<Item> FENCE_GATES_WOODEN = tag("fence_gates/wooden");
        public static final IOptionalNamedTag<Item> FENCES = tag("fences");
        public static final IOptionalNamedTag<Item> FENCES_NETHER_BRICK = tag("fences/nether_brick");
        public static final IOptionalNamedTag<Item> FENCES_WOODEN = tag("fences/wooden");
        public static final IOptionalNamedTag<Item> GEMS = tag("gems");
        public static final IOptionalNamedTag<Item> GEMS_DIAMOND = tag("gems/diamond");
        public static final IOptionalNamedTag<Item> GEMS_EMERALD = tag("gems/emerald");
        public static final IOptionalNamedTag<Item> GEMS_LAPIS = tag("gems/lapis");
        public static final IOptionalNamedTag<Item> GEMS_PRISMARINE = tag("gems/prismarine");
        public static final IOptionalNamedTag<Item> GEMS_QUARTZ = tag("gems/quartz");

        public static final IOptionalNamedTag<Item> GLASS = tag("glass");
        public static final IOptionalNamedTag<Item> GLASS_BLACK = tag("glass/black");
        public static final IOptionalNamedTag<Item> GLASS_BLUE = tag("glass/blue");
        public static final IOptionalNamedTag<Item> GLASS_BROWN = tag("glass/brown");
        public static final IOptionalNamedTag<Item> GLASS_COLORLESS = tag("glass/colorless");
        public static final IOptionalNamedTag<Item> GLASS_CYAN = tag("glass/cyan");
        public static final IOptionalNamedTag<Item> GLASS_GRAY = tag("glass/gray");
        public static final IOptionalNamedTag<Item> GLASS_GREEN = tag("glass/green");
        public static final IOptionalNamedTag<Item> GLASS_LIGHT_BLUE = tag("glass/light_blue");
        public static final IOptionalNamedTag<Item> GLASS_LIGHT_GRAY = tag("glass/light_gray");
        public static final IOptionalNamedTag<Item> GLASS_LIME = tag("glass/lime");
        public static final IOptionalNamedTag<Item> GLASS_MAGENTA = tag("glass/magenta");
        public static final IOptionalNamedTag<Item> GLASS_ORANGE = tag("glass/orange");
        public static final IOptionalNamedTag<Item> GLASS_PINK = tag("glass/pink");
        public static final IOptionalNamedTag<Item> GLASS_PURPLE = tag("glass/purple");
        public static final IOptionalNamedTag<Item> GLASS_RED = tag("glass/red");
        public static final IOptionalNamedTag<Item> GLASS_WHITE = tag("glass/white");
        public static final IOptionalNamedTag<Item> GLASS_YELLOW = tag("glass/yellow");

        public static final IOptionalNamedTag<Item> GLASS_PANES = tag("glass_panes");
        public static final IOptionalNamedTag<Item> GLASS_PANES_BLACK = tag("glass_panes/black");
        public static final IOptionalNamedTag<Item> GLASS_PANES_BLUE = tag("glass_panes/blue");
        public static final IOptionalNamedTag<Item> GLASS_PANES_BROWN = tag("glass_panes/brown");
        public static final IOptionalNamedTag<Item> GLASS_PANES_COLORLESS = tag("glass_panes/colorless");
        public static final IOptionalNamedTag<Item> GLASS_PANES_CYAN = tag("glass_panes/cyan");
        public static final IOptionalNamedTag<Item> GLASS_PANES_GRAY = tag("glass_panes/gray");
        public static final IOptionalNamedTag<Item> GLASS_PANES_GREEN = tag("glass_panes/green");
        public static final IOptionalNamedTag<Item> GLASS_PANES_LIGHT_BLUE = tag("glass_panes/light_blue");
        public static final IOptionalNamedTag<Item> GLASS_PANES_LIGHT_GRAY = tag("glass_panes/light_gray");
        public static final IOptionalNamedTag<Item> GLASS_PANES_LIME = tag("glass_panes/lime");
        public static final IOptionalNamedTag<Item> GLASS_PANES_MAGENTA = tag("glass_panes/magenta");
        public static final IOptionalNamedTag<Item> GLASS_PANES_ORANGE = tag("glass_panes/orange");
        public static final IOptionalNamedTag<Item> GLASS_PANES_PINK = tag("glass_panes/pink");
        public static final IOptionalNamedTag<Item> GLASS_PANES_PURPLE = tag("glass_panes/purple");
        public static final IOptionalNamedTag<Item> GLASS_PANES_RED = tag("glass_panes/red");
        public static final IOptionalNamedTag<Item> GLASS_PANES_WHITE = tag("glass_panes/white");
        public static final IOptionalNamedTag<Item> GLASS_PANES_YELLOW = tag("glass_panes/yellow");

        public static final IOptionalNamedTag<Item> GRAVEL = tag("gravel");
        public static final IOptionalNamedTag<Item> GUNPOWDER = tag("gunpowder");
        public static final IOptionalNamedTag<Item> HEADS = tag("heads");
        public static final IOptionalNamedTag<Item> INGOTS = tag("ingots");
        public static final IOptionalNamedTag<Item> INGOTS_BRICK = tag("ingots/brick");
        public static final IOptionalNamedTag<Item> INGOTS_GOLD = tag("ingots/gold");
        public static final IOptionalNamedTag<Item> INGOTS_IRON = tag("ingots/iron");
        public static final IOptionalNamedTag<Item> INGOTS_NETHERITE = tag("ingots/netherite");
        public static final IOptionalNamedTag<Item> INGOTS_NETHER_BRICK = tag("ingots/nether_brick");
        public static final IOptionalNamedTag<Item> LEATHER = tag("leather");
        public static final IOptionalNamedTag<Item> MUSHROOMS = tag("mushrooms");
        public static final IOptionalNamedTag<Item> NETHER_STARS = tag("nether_stars");
        public static final IOptionalNamedTag<Item> NETHERRACK = tag("netherrack");
        public static final IOptionalNamedTag<Item> NUGGETS = tag("nuggets");
        public static final IOptionalNamedTag<Item> NUGGETS_GOLD = tag("nuggets/gold");
        public static final IOptionalNamedTag<Item> NUGGETS_IRON = tag("nuggets/iron");
        public static final IOptionalNamedTag<Item> OBSIDIAN = tag("obsidian");
        public static final IOptionalNamedTag<Item> ORES = tag("ores");
        public static final IOptionalNamedTag<Item> ORES_COAL = tag("ores/coal");
        public static final IOptionalNamedTag<Item> ORES_DIAMOND = tag("ores/diamond");
        public static final IOptionalNamedTag<Item> ORES_EMERALD = tag("ores/emerald");
        public static final IOptionalNamedTag<Item> ORES_GOLD = tag("ores/gold");
        public static final IOptionalNamedTag<Item> ORES_IRON = tag("ores/iron");
        public static final IOptionalNamedTag<Item> ORES_LAPIS = tag("ores/lapis");
        public static final IOptionalNamedTag<Item> ORES_NETHERITE_SCRAP = tag("ores/netherite_scrap");
        public static final IOptionalNamedTag<Item> ORES_QUARTZ = tag("ores/quartz");
        public static final IOptionalNamedTag<Item> ORES_REDSTONE = tag("ores/redstone");
        public static final IOptionalNamedTag<Item> RODS = tag("rods");
        public static final IOptionalNamedTag<Item> RODS_BLAZE = tag("rods/blaze");
        public static final IOptionalNamedTag<Item> RODS_WOODEN = tag("rods/wooden");

        public static final IOptionalNamedTag<Item> SAND = tag("sand");
        public static final IOptionalNamedTag<Item> SAND_COLORLESS = tag("sand/colorless");
        public static final IOptionalNamedTag<Item> SAND_RED = tag("sand/red");

        public static final IOptionalNamedTag<Item> SANDSTONE = tag("sandstone");
        public static final IOptionalNamedTag<Item> SEEDS = tag("seeds");
        public static final IOptionalNamedTag<Item> SEEDS_BEETROOT = tag("seeds/beetroot");
        public static final IOptionalNamedTag<Item> SEEDS_MELON = tag("seeds/melon");
        public static final IOptionalNamedTag<Item> SEEDS_PUMPKIN = tag("seeds/pumpkin");
        public static final IOptionalNamedTag<Item> SEEDS_WHEAT = tag("seeds/wheat");
        public static final IOptionalNamedTag<Item> SHEARS = tag("shears");
        public static final IOptionalNamedTag<Item> SLIMEBALLS = tag("slimeballs");
        public static final IOptionalNamedTag<Item> STAINED_GLASS = tag("stained_glass");
        public static final IOptionalNamedTag<Item> STAINED_GLASS_PANES = tag("stained_glass_panes");
        public static final IOptionalNamedTag<Item> STONE = tag("stone");
        public static final IOptionalNamedTag<Item> STORAGE_BLOCKS = tag("storage_blocks");
        public static final IOptionalNamedTag<Item> STORAGE_BLOCKS_COAL = tag("storage_blocks/coal");
        public static final IOptionalNamedTag<Item> STORAGE_BLOCKS_DIAMOND = tag("storage_blocks/diamond");
        public static final IOptionalNamedTag<Item> STORAGE_BLOCKS_EMERALD = tag("storage_blocks/emerald");
        public static final IOptionalNamedTag<Item> STORAGE_BLOCKS_GOLD = tag("storage_blocks/gold");
        public static final IOptionalNamedTag<Item> STORAGE_BLOCKS_IRON = tag("storage_blocks/iron");
        public static final IOptionalNamedTag<Item> STORAGE_BLOCKS_LAPIS = tag("storage_blocks/lapis");
        public static final IOptionalNamedTag<Item> STORAGE_BLOCKS_NETHERITE = tag("storage_blocks/netherite");
        public static final IOptionalNamedTag<Item> STORAGE_BLOCKS_QUARTZ = tag("storage_blocks/quartz");
        public static final IOptionalNamedTag<Item> STORAGE_BLOCKS_REDSTONE = tag("storage_blocks/redstone");
        public static final IOptionalNamedTag<Item> STRING = tag("string");

        private static IOptionalNamedTag<Item> tag(String name)
        {
            return ItemTags.createOptional(new ResourceLocation("forge", name));
        }
    }

    public static class Biomes
    {
        private static void init(){}

        public static final IOptionalNamedTag<Biome> HOT = tag("hot");
        public static final IOptionalNamedTag<Biome> COLD = tag("cold");

        /*Tags specifying the amount of vegetation a biome has. Specifying neither implies a biome to have moderate amounts*/
        public static final IOptionalNamedTag<Biome> SPARSE = tag("sparse");
        public static final IOptionalNamedTag<Biome> DENSE = tag("dense");

        /*Tags specifying how moist a biome is. Specifying neither implies the biome as having moderate humidity*/
        public static final IOptionalNamedTag<Biome> WET = tag("wet");
        public static final IOptionalNamedTag<Biome> DRY = tag("dry");

        /*Tree-based tags, SAVANNA refers to dry, desert-like trees (Such as Acacia), CONIFEROUS refers to snowy trees (Such as Spruce) and JUNGLE refers to jungle trees.
         * Specifying no tag implies a biome has temperate trees (Such as Oak)*/
        public static final IOptionalNamedTag<Biome> SAVANNA = tag("savanna");
        public static final IOptionalNamedTag<Biome> CONIFEROUS = tag("coniferous");
        public static final IOptionalNamedTag<Biome> JUNGLE = tag("jungle");

        /*Tags specifying the nature of a biome*/
        public static final IOptionalNamedTag<Biome> SPOOKY = tag("spooky");
        public static final IOptionalNamedTag<Biome> DEAD = tag("dead");
        public static final IOptionalNamedTag<Biome> LUSH = tag("lush");
        public static final IOptionalNamedTag<Biome> MUSHROOM = tag("mushroom");
        public static final IOptionalNamedTag<Biome> MAGICAL = tag("magical");
        public static final IOptionalNamedTag<Biome> RARE = tag("rare");
        public static final IOptionalNamedTag<Biome> PLATEAU = tag("plateau");
        public static final IOptionalNamedTag<Biome> MODIFIED = tag("modified");

        public static final IOptionalNamedTag<Biome> OCEAN = tag("water/ocean");
        public static final IOptionalNamedTag<Biome> RIVER = tag("water/river");
        public static final IOptionalNamedTag<Biome> WATER = tag("water");

        /*Generic types which a biome can be*/
        public static final IOptionalNamedTag<Biome> MESA = tag("mesa");
        public static final IOptionalNamedTag<Biome> FOREST = tag("forest");
        public static final IOptionalNamedTag<Biome> PLAINS = tag("plains");
        public static final IOptionalNamedTag<Biome> MOUNTAIN = tag("mountain");
        public static final IOptionalNamedTag<Biome> HILLS = tag("hills");
        public static final IOptionalNamedTag<Biome> SWAMP = tag("swamp");
        public static final IOptionalNamedTag<Biome> SANDY = tag("sandy");
        public static final IOptionalNamedTag<Biome> SNOWY = tag("snowy");
        public static final IOptionalNamedTag<Biome> WASTELAND = tag("wasteland");
        public static final IOptionalNamedTag<Biome> BEACH = tag("beach");
        public static final IOptionalNamedTag<Biome> VOID = tag("void");

        /*Tags specifying the dimension a biome generates in. Specifying none implies a biome that generates in a modded dimension*/
        public static final IOptionalNamedTag<Biome> OVERWORLD = tag("overworld");
        public static final IOptionalNamedTag<Biome> NETHER = tag("nether");
        public static final IOptionalNamedTag<Biome> END = tag("end");

        private static IOptionalNamedTag<Biome> tag(String name)
        {
            //Uses ForgeRegistries.Keys.BIOMES, since ForgeRegistries.BIOMES is still null when Forge initializes tags
            return ForgeTagHandler.createOptionalTag(ForgeRegistries.Keys.BIOMES.func_240901_a_(), new ResourceLocation("forge", name));
        }
    }

    public interface IOptionalNamedTag<T> extends INamedTag<T>
    {
        /**
         * Returns true if the current state is defaulted. This means we have connected to a server that does not contain this tag.
         * The values referenced in this tag may be empty, or some values specified by the original tag creator.
         */
        boolean isDefaulted();
    }
}
