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

package net.minecraftforge.common.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.item.DyeColor;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.BlockTagsProvider;

import static net.minecraftforge.common.Tags.Blocks.*;

import java.util.Locale;
import java.util.function.Consumer;

public class ForgeBlockTagsProvider extends BlockTagsProvider
{
    public ForgeBlockTagsProvider(DataGenerator gen, ExistingFileHelper existingFileHelper)
    {
        super(gen, "forge", existingFileHelper);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void registerTags()
    {
        func_240522_a_(CHESTS).addTags(CHESTS_ENDER, CHESTS_TRAPPED, CHESTS_WOODEN);
        func_240522_a_(CHESTS_ENDER).func_240534_a_(Blocks.ENDER_CHEST);
        func_240522_a_(CHESTS_TRAPPED).func_240534_a_(Blocks.TRAPPED_CHEST);
        func_240522_a_(CHESTS_WOODEN).func_240534_a_(Blocks.CHEST, Blocks.TRAPPED_CHEST);
        func_240522_a_(COBBLESTONE).func_240534_a_(Blocks.COBBLESTONE, Blocks.INFESTED_COBBLESTONE, Blocks.MOSSY_COBBLESTONE);
        func_240522_a_(DIRT).func_240534_a_(Blocks.DIRT, Blocks.GRASS_BLOCK, Blocks.COARSE_DIRT, Blocks.PODZOL, Blocks.MYCELIUM);
        func_240522_a_(END_STONES).func_240534_a_(Blocks.END_STONE);
        func_240522_a_(FENCE_GATES).addTags(FENCE_GATES_WOODEN);
        func_240522_a_(FENCE_GATES_WOODEN).func_240534_a_(Blocks.OAK_FENCE_GATE, Blocks.SPRUCE_FENCE_GATE, Blocks.BIRCH_FENCE_GATE, Blocks.JUNGLE_FENCE_GATE, Blocks.ACACIA_FENCE_GATE, Blocks.DARK_OAK_FENCE_GATE, Blocks.field_235354_mM_, Blocks.field_235355_mN_);
        func_240522_a_(FENCES).addTags(FENCES_NETHER_BRICK, FENCES_WOODEN);
        func_240522_a_(FENCES_NETHER_BRICK).func_240534_a_(Blocks.NETHER_BRICK_FENCE);
        func_240522_a_(FENCES_WOODEN).func_240534_a_(Blocks.OAK_FENCE, Blocks.SPRUCE_FENCE, Blocks.BIRCH_FENCE, Blocks.JUNGLE_FENCE, Blocks.ACACIA_FENCE, Blocks.DARK_OAK_FENCE,  Blocks.field_235350_mI_, Blocks.field_235351_mJ_);
        func_240522_a_(GLASS).addTags(GLASS_COLORLESS, STAINED_GLASS);
        func_240522_a_(GLASS_COLORLESS).func_240534_a_(Blocks.GLASS);
        addColored(func_240522_a_(STAINED_GLASS)::func_240534_a_, GLASS, "{color}_stained_glass");
        func_240522_a_(GLASS_PANES).addTags(GLASS_PANES_COLORLESS, STAINED_GLASS_PANES);
        func_240522_a_(GLASS_PANES_COLORLESS).func_240534_a_(Blocks.GLASS_PANE);
        addColored(func_240522_a_(STAINED_GLASS_PANES)::func_240534_a_, GLASS_PANES, "{color}_stained_glass_pane");
        func_240522_a_(GRAVEL).func_240534_a_(Blocks.GRAVEL);
        func_240522_a_(NETHERRACK).func_240534_a_(Blocks.NETHERRACK);
        func_240522_a_(OBSIDIAN).func_240534_a_(Blocks.OBSIDIAN);
        func_240522_a_(ORES).addTags(ORES_COAL, ORES_DIAMOND, ORES_EMERALD, ORES_GOLD, ORES_IRON, ORES_LAPIS, ORES_REDSTONE, ORES_QUARTZ, ORES_NETHERITE_SCRAP);
        func_240522_a_(ORES_COAL).func_240534_a_(Blocks.COAL_ORE);
        func_240522_a_(ORES_DIAMOND).func_240534_a_(Blocks.DIAMOND_ORE);
        func_240522_a_(ORES_EMERALD).func_240534_a_(Blocks.EMERALD_ORE);
        func_240522_a_(ORES_GOLD).func_240531_a_(BlockTags.field_232866_P_);
        func_240522_a_(ORES_IRON).func_240534_a_(Blocks.IRON_ORE);
        func_240522_a_(ORES_LAPIS).func_240534_a_(Blocks.LAPIS_ORE);
        func_240522_a_(ORES_QUARTZ).func_240534_a_(Blocks.NETHER_QUARTZ_ORE);
        func_240522_a_(ORES_REDSTONE).func_240534_a_(Blocks.REDSTONE_ORE);
        func_240522_a_(ORES_NETHERITE_SCRAP).func_240534_a_(Blocks.field_235398_nh_);
        func_240522_a_(SAND).addTags(SAND_COLORLESS, SAND_RED);
        func_240522_a_(SAND_COLORLESS).func_240534_a_(Blocks.SAND);
        func_240522_a_(SAND_RED).func_240534_a_(Blocks.RED_SAND);
        func_240522_a_(SANDSTONE).func_240534_a_(Blocks.SANDSTONE, Blocks.CUT_SANDSTONE, Blocks.CHISELED_SANDSTONE, Blocks.SMOOTH_SANDSTONE, Blocks.RED_SANDSTONE, Blocks.CUT_RED_SANDSTONE, Blocks.CHISELED_RED_SANDSTONE, Blocks.SMOOTH_RED_SANDSTONE);
        func_240522_a_(STONE).func_240534_a_(Blocks.ANDESITE, Blocks.DIORITE, Blocks.GRANITE, Blocks.INFESTED_STONE, Blocks.STONE, Blocks.POLISHED_ANDESITE, Blocks.POLISHED_DIORITE, Blocks.POLISHED_GRANITE);
        func_240522_a_(STORAGE_BLOCKS).addTags(STORAGE_BLOCKS_COAL, STORAGE_BLOCKS_DIAMOND, STORAGE_BLOCKS_EMERALD, STORAGE_BLOCKS_GOLD, STORAGE_BLOCKS_IRON, STORAGE_BLOCKS_LAPIS, STORAGE_BLOCKS_QUARTZ, STORAGE_BLOCKS_REDSTONE, STORAGE_BLOCKS_NETHERITE);
        func_240522_a_(STORAGE_BLOCKS_COAL).func_240534_a_(Blocks.COAL_BLOCK);
        func_240522_a_(STORAGE_BLOCKS_DIAMOND).func_240534_a_(Blocks.DIAMOND_BLOCK);
        func_240522_a_(STORAGE_BLOCKS_EMERALD).func_240534_a_(Blocks.EMERALD_BLOCK);
        func_240522_a_(STORAGE_BLOCKS_GOLD).func_240534_a_(Blocks.GOLD_BLOCK);
        func_240522_a_(STORAGE_BLOCKS_IRON).func_240534_a_(Blocks.IRON_BLOCK);
        func_240522_a_(STORAGE_BLOCKS_LAPIS).func_240534_a_(Blocks.LAPIS_BLOCK);
        func_240522_a_(STORAGE_BLOCKS_QUARTZ).func_240534_a_(Blocks.QUARTZ_BLOCK);
        func_240522_a_(STORAGE_BLOCKS_REDSTONE).func_240534_a_(Blocks.REDSTONE_BLOCK);
        func_240522_a_(STORAGE_BLOCKS_NETHERITE).func_240534_a_(Blocks.field_235397_ng_);
    }

    private void addColored(Consumer<Block> consumer, ITag.INamedTag<Block> group, String pattern)
    {
        String prefix = group.func_230234_a_().getPath().toUpperCase(Locale.ENGLISH) + '_';
        for (DyeColor color  : DyeColor.values())
        {
            ResourceLocation key = new ResourceLocation("minecraft", pattern.replace("{color}",  color.getTranslationKey()));
            ITag.INamedTag<Block> tag = getForgeTag(prefix + color.getTranslationKey());
            Block block = ForgeRegistries.BLOCKS.getValue(key);
            if (block == null || block  == Blocks.AIR)
                throw new IllegalStateException("Unknown vanilla block: " + key.toString());
            func_240522_a_(tag).func_240534_a_(block);
            consumer.accept(block);
        }
    }

    @SuppressWarnings("unchecked")
    private ITag.INamedTag<Block> getForgeTag(String name)
    {
        try
        {
            name = name.toUpperCase(Locale.ENGLISH);
            return (ITag.INamedTag<Block>)Tags.Blocks.class.getDeclaredField(name).get(null);
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
