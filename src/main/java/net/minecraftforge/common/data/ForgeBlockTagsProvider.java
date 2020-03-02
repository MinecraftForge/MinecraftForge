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

package net.minecraftforge.common.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.item.DyeColor;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.BlockTagsProvider;

import static net.minecraftforge.common.Tags.Blocks.*;

import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ForgeBlockTagsProvider extends BlockTagsProvider
{
    private Set<ResourceLocation> filter = null;

    public ForgeBlockTagsProvider(DataGenerator gen)
    {
        super(gen);
    }

    @Override
    public void registerTags()
    {
        super.registerTags();
        filter = this.tagToBuilder.entrySet().stream().map(e -> e.getKey().getId()).collect(Collectors.toSet());

        getBuilder(CHESTS).add(CHESTS_ENDER, CHESTS_TRAPPED, CHESTS_WOODEN);
        getBuilder(CHESTS_ENDER).add(Blocks.ENDER_CHEST);
        getBuilder(CHESTS_TRAPPED).add(Blocks.TRAPPED_CHEST);
        getBuilder(CHESTS_WOODEN).add(Blocks.CHEST, Blocks.TRAPPED_CHEST);
        getBuilder(COBBLESTONE).add(Blocks.COBBLESTONE, Blocks.INFESTED_COBBLESTONE, Blocks.MOSSY_COBBLESTONE);
        getBuilder(DIRT).add(Blocks.DIRT, Blocks.GRASS_BLOCK, Blocks.COARSE_DIRT, Blocks.PODZOL, Blocks.MYCELIUM);
        getBuilder(END_STONES).add(Blocks.END_STONE);
        getBuilder(FENCE_GATES).add(FENCE_GATES_WOODEN);
        getBuilder(FENCE_GATES_WOODEN).add(Blocks.OAK_FENCE_GATE, Blocks.SPRUCE_FENCE_GATE, Blocks.BIRCH_FENCE_GATE, Blocks.JUNGLE_FENCE_GATE, Blocks.ACACIA_FENCE_GATE, Blocks.DARK_OAK_FENCE_GATE);
        getBuilder(FENCES).add(FENCES_NETHER_BRICK, FENCES_WOODEN);
        getBuilder(FENCES_NETHER_BRICK).add(Blocks.NETHER_BRICK_FENCE);
        getBuilder(FENCES_WOODEN).add(Blocks.OAK_FENCE, Blocks.SPRUCE_FENCE, Blocks.BIRCH_FENCE, Blocks.JUNGLE_FENCE, Blocks.ACACIA_FENCE, Blocks.DARK_OAK_FENCE);
        getBuilder(GLASS).add(GLASS_COLORLESS, STAINED_GLASS);
        getBuilder(GLASS_COLORLESS).add(Blocks.GLASS);
        addColored(getBuilder(STAINED_GLASS)::add, GLASS, "{color}_stained_glass");
        getBuilder(GLASS_PANES).add(GLASS_PANES_COLORLESS, STAINED_GLASS_PANES);
        getBuilder(GLASS_PANES_COLORLESS).add(Blocks.GLASS_PANE);
        addColored(getBuilder(STAINED_GLASS_PANES)::add, GLASS_PANES, "{color}_stained_glass_pane");
        getBuilder(GRAVEL).add(Blocks.GRAVEL);
        getBuilder(NETHERRACK).add(Blocks.NETHERRACK);
        getBuilder(OBSIDIAN).add(Blocks.OBSIDIAN);
        getBuilder(ORES).add(ORES_COAL, ORES_DIAMOND, ORES_EMERALD, ORES_GOLD, ORES_IRON, ORES_LAPIS, ORES_REDSTONE, ORES_QUARTZ);
        getBuilder(ORES_COAL).add(Blocks.COAL_ORE);
        getBuilder(ORES_DIAMOND).add(Blocks.DIAMOND_ORE);
        getBuilder(ORES_EMERALD).add(Blocks.EMERALD_ORE);
        getBuilder(ORES_GOLD).add(Blocks.GOLD_ORE);
        getBuilder(ORES_IRON).add(Blocks.IRON_ORE);
        getBuilder(ORES_LAPIS).add(Blocks.LAPIS_ORE);
        getBuilder(ORES_QUARTZ).add(Blocks.NETHER_QUARTZ_ORE);
        getBuilder(ORES_REDSTONE).add(Blocks.REDSTONE_ORE);
        getBuilder(SAND).add(SAND_COLORLESS, SAND_RED);
        getBuilder(SAND_COLORLESS).add(Blocks.SAND);
        getBuilder(SAND_RED).add(Blocks.RED_SAND);
        getBuilder(SANDSTONE).add(Blocks.SANDSTONE, Blocks.CUT_SANDSTONE, Blocks.CHISELED_SANDSTONE, Blocks.SMOOTH_SANDSTONE, Blocks.RED_SANDSTONE, Blocks.CUT_RED_SANDSTONE, Blocks.CHISELED_RED_SANDSTONE, Blocks.SMOOTH_RED_SANDSTONE);
        getBuilder(STONE).add(Blocks.ANDESITE, Blocks.DIORITE, Blocks.GRANITE, Blocks.INFESTED_STONE, Blocks.STONE, Blocks.POLISHED_ANDESITE, Blocks.POLISHED_DIORITE, Blocks.POLISHED_GRANITE);
        getBuilder(STORAGE_BLOCKS).add(STORAGE_BLOCKS_COAL, STORAGE_BLOCKS_DIAMOND, STORAGE_BLOCKS_EMERALD, STORAGE_BLOCKS_GOLD, STORAGE_BLOCKS_IRON, STORAGE_BLOCKS_LAPIS, STORAGE_BLOCKS_QUARTZ, STORAGE_BLOCKS_REDSTONE);
        getBuilder(STORAGE_BLOCKS_COAL).add(Blocks.COAL_BLOCK);
        getBuilder(STORAGE_BLOCKS_DIAMOND).add(Blocks.DIAMOND_BLOCK);
        getBuilder(STORAGE_BLOCKS_EMERALD).add(Blocks.EMERALD_BLOCK);
        getBuilder(STORAGE_BLOCKS_GOLD).add(Blocks.GOLD_BLOCK);
        getBuilder(STORAGE_BLOCKS_IRON).add(Blocks.IRON_BLOCK);
        getBuilder(STORAGE_BLOCKS_LAPIS).add(Blocks.LAPIS_BLOCK);
        getBuilder(STORAGE_BLOCKS_QUARTZ).add(Blocks.QUARTZ_BLOCK);
        getBuilder(STORAGE_BLOCKS_REDSTONE).add(Blocks.REDSTONE_BLOCK);
    }

    private void addColored(Consumer<Block> consumer, Tag<Block> group, String pattern)
    {
        String prefix = group.getId().getPath().toUpperCase(Locale.ENGLISH) + '_';
        for (DyeColor color  : DyeColor.values())
        {
            ResourceLocation key = new ResourceLocation("minecraft", pattern.replace("{color}",  color.getTranslationKey()));
            Tag<Block> tag = getForgeTag(prefix + color.getTranslationKey());
            Block block = ForgeRegistries.BLOCKS.getValue(key);
            if (block == null || block  == Blocks.AIR)
                throw new IllegalStateException("Unknown vanilla block: " + key.toString());
            getBuilder(tag).add(block);
            consumer.accept(block);
        }
    }

    @SuppressWarnings("unchecked")
    private Tag<Block> getForgeTag(String name)
    {
        try
        {
            name = name.toUpperCase(Locale.ENGLISH);
            return (Tag<Block>)Tags.Blocks.class.getDeclaredField(name).get(null);
        }
        catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e)
        {
            throw new IllegalStateException(Tags.Blocks.class.getName() + " is missing tag name: " + name);
        }
    }

    @Override
    protected Path makePath(ResourceLocation id)
    {
        return filter != null && filter.contains(id) ? null : super.makePath(id); //We don't want to save vanilla tags.
    }

    @Override
    public String getName()
    {
        return "Forge Block Tags";
    }
}
