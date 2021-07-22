/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

package net.minecraftforge.common.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.DyeColor;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.data.tags.BlockTagsProvider;

import static net.minecraftforge.common.Tags.Blocks.*;

import java.util.Locale;
import java.util.function.Consumer;

public final class ForgeBlockTagsProvider extends BlockTagsProvider
{
    public ForgeBlockTagsProvider(DataGenerator gen, ExistingFileHelper existingFileHelper)
    {
        super(gen, "forge", existingFileHelper);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void addTags()
    {
        tag(BARRELS).addTag(BARRELS_WOODEN);
        tag(BARRELS_WOODEN).add(Blocks.BARREL);
        tag(CHESTS).addTags(CHESTS_ENDER, CHESTS_TRAPPED, CHESTS_WOODEN);
        tag(CHESTS_ENDER).add(Blocks.ENDER_CHEST);
        tag(CHESTS_TRAPPED).add(Blocks.TRAPPED_CHEST);
        tag(CHESTS_WOODEN).add(Blocks.CHEST, Blocks.TRAPPED_CHEST);
        tag(COBBLESTONE).add(Blocks.COBBLESTONE, Blocks.INFESTED_COBBLESTONE, Blocks.MOSSY_COBBLESTONE);
        tag(DIRT).add(Blocks.DIRT, Blocks.GRASS_BLOCK, Blocks.COARSE_DIRT, Blocks.PODZOL, Blocks.MYCELIUM);
        tag(END_STONES).add(Blocks.END_STONE);
        tag(ENDERMAN_PLACE_ON_BLACKLIST);
        tag(FENCE_GATES).addTags(FENCE_GATES_WOODEN);
        tag(FENCE_GATES_WOODEN).add(Blocks.OAK_FENCE_GATE, Blocks.SPRUCE_FENCE_GATE, Blocks.BIRCH_FENCE_GATE, Blocks.JUNGLE_FENCE_GATE, Blocks.ACACIA_FENCE_GATE, Blocks.DARK_OAK_FENCE_GATE, Blocks.CRIMSON_FENCE_GATE, Blocks.WARPED_FENCE_GATE);
        tag(FENCES).addTags(FENCES_NETHER_BRICK, FENCES_WOODEN);
        tag(FENCES_NETHER_BRICK).add(Blocks.NETHER_BRICK_FENCE);
        tag(FENCES_WOODEN).add(Blocks.OAK_FENCE, Blocks.SPRUCE_FENCE, Blocks.BIRCH_FENCE, Blocks.JUNGLE_FENCE, Blocks.ACACIA_FENCE, Blocks.DARK_OAK_FENCE,  Blocks.CRIMSON_FENCE, Blocks.WARPED_FENCE);
        tag(GLASS).addTags(GLASS_COLORLESS, STAINED_GLASS);
        tag(GLASS_COLORLESS).add(Blocks.GLASS);
        addColored(tag(STAINED_GLASS)::add, GLASS, "{color}_stained_glass");
        tag(GLASS_PANES).addTags(GLASS_PANES_COLORLESS, STAINED_GLASS_PANES);
        tag(GLASS_PANES_COLORLESS).add(Blocks.GLASS_PANE);
        addColored(tag(STAINED_GLASS_PANES)::add, GLASS_PANES, "{color}_stained_glass_pane");
        tag(GRAVEL).add(Blocks.GRAVEL);
        tag(NETHERRACK).add(Blocks.NETHERRACK);
        tag(OBSIDIAN).add(Blocks.OBSIDIAN);
        tag(ORES).addTags(ORES_COAL, ORES_DIAMOND, ORES_EMERALD, ORES_GOLD, ORES_IRON, ORES_LAPIS, ORES_REDSTONE, ORES_QUARTZ, ORES_NETHERITE_SCRAP);
        tag(ORES_COAL).add(Blocks.COAL_ORE);
        tag(ORES_DIAMOND).add(Blocks.DIAMOND_ORE);
        tag(ORES_EMERALD).add(Blocks.EMERALD_ORE);
        tag(ORES_GOLD).addTag(BlockTags.GOLD_ORES);
        tag(ORES_IRON).add(Blocks.IRON_ORE);
        tag(ORES_LAPIS).add(Blocks.LAPIS_ORE);
        tag(ORES_QUARTZ).add(Blocks.NETHER_QUARTZ_ORE);
        tag(ORES_REDSTONE).add(Blocks.REDSTONE_ORE);
        tag(ORES_NETHERITE_SCRAP).add(Blocks.ANCIENT_DEBRIS);
        tag(SAND).addTags(SAND_COLORLESS, SAND_RED);
        tag(SAND_COLORLESS).add(Blocks.SAND);
        tag(SAND_RED).add(Blocks.RED_SAND);
        tag(SANDSTONE).add(Blocks.SANDSTONE, Blocks.CUT_SANDSTONE, Blocks.CHISELED_SANDSTONE, Blocks.SMOOTH_SANDSTONE, Blocks.RED_SANDSTONE, Blocks.CUT_RED_SANDSTONE, Blocks.CHISELED_RED_SANDSTONE, Blocks.SMOOTH_RED_SANDSTONE);
        tag(STONE).add(Blocks.ANDESITE, Blocks.DIORITE, Blocks.GRANITE, Blocks.INFESTED_STONE, Blocks.STONE, Blocks.POLISHED_ANDESITE, Blocks.POLISHED_DIORITE, Blocks.POLISHED_GRANITE);
        tag(STORAGE_BLOCKS).addTags(STORAGE_BLOCKS_COAL, STORAGE_BLOCKS_DIAMOND, STORAGE_BLOCKS_EMERALD, STORAGE_BLOCKS_GOLD, STORAGE_BLOCKS_IRON, STORAGE_BLOCKS_LAPIS, STORAGE_BLOCKS_QUARTZ, STORAGE_BLOCKS_REDSTONE, STORAGE_BLOCKS_NETHERITE);
        tag(STORAGE_BLOCKS_COAL).add(Blocks.COAL_BLOCK);
        tag(STORAGE_BLOCKS_DIAMOND).add(Blocks.DIAMOND_BLOCK);
        tag(STORAGE_BLOCKS_EMERALD).add(Blocks.EMERALD_BLOCK);
        tag(STORAGE_BLOCKS_GOLD).add(Blocks.GOLD_BLOCK);
        tag(STORAGE_BLOCKS_IRON).add(Blocks.IRON_BLOCK);
        tag(STORAGE_BLOCKS_LAPIS).add(Blocks.LAPIS_BLOCK);
        tag(STORAGE_BLOCKS_QUARTZ).add(Blocks.QUARTZ_BLOCK);
        tag(STORAGE_BLOCKS_REDSTONE).add(Blocks.REDSTONE_BLOCK);
        tag(STORAGE_BLOCKS_NETHERITE).add(Blocks.NETHERITE_BLOCK);
    }

    private void addColored(Consumer<Block> consumer, Tag.Named<Block> group, String pattern)
    {
        String prefix = group.getName().getPath().toUpperCase(Locale.ENGLISH) + '_';
        for (DyeColor color  : DyeColor.values())
        {
            ResourceLocation key = new ResourceLocation("minecraft", pattern.replace("{color}",  color.getName()));
            Tag.Named<Block> tag = getForgeTag(prefix + color.getName());
            Block block = ForgeRegistries.BLOCKS.getValue(key);
            if (block == null || block  == Blocks.AIR)
                throw new IllegalStateException("Unknown vanilla block: " + key.toString());
            tag(tag).add(block);
            consumer.accept(block);
        }
    }

    @SuppressWarnings("unchecked")
    private Tag.Named<Block> getForgeTag(String name)
    {
        try
        {
            name = name.toUpperCase(Locale.ENGLISH);
            return (Tag.Named<Block>)Tags.Blocks.class.getDeclaredField(name).get(null);
        }
        catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e)
        {
            throw new IllegalStateException(Tags.Blocks.class.getName() + " is missing tag name: " + name);
        }
    }

    @Override
    public String getName()
    {
        return "Forge Block Tags";
    }
}
