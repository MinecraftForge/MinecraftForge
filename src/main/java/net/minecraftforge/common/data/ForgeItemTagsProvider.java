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

import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;

public class ForgeItemTagsProvider extends ItemTagsProvider
{
    private Set<ResourceLocation> filter = null;

    public ForgeItemTagsProvider(DataGenerator gen)
    {
        super(gen);
    }

    @Override
    public void registerTags()
    {
        super.registerTags();
        filter = this.tagToBuilder.entrySet().stream().map(e -> e.getKey().getId()).collect(Collectors.toSet());

        copy(Tags.Blocks.CHESTS, Tags.Items.CHESTS);
        copy(Tags.Blocks.CHESTS_ENDER, Tags.Items.CHESTS_ENDER);
        copy(Tags.Blocks.CHESTS_TRAPPED, Tags.Items.CHESTS_TRAPPED);
        copy(Tags.Blocks.CHESTS_WOODEN, Tags.Items.CHESTS_WOODEN);
        copy(Tags.Blocks.COBBLESTONE, Tags.Items.COBBLESTONE);
        getBuilder(Tags.Items.DUSTS).add(Tags.Items.DUSTS_GLOWSTONE, Tags.Items.DUSTS_PRISMARINE, Tags.Items.DUSTS_REDSTONE);
        getBuilder(Tags.Items.DUSTS_GLOWSTONE).add(Items.GLOWSTONE_DUST);
        getBuilder(Tags.Items.DUSTS_PRISMARINE).add(Items.PRISMARINE_SHARD);
        getBuilder(Tags.Items.DUSTS_REDSTONE).add(Items.REDSTONE);
        getBuilder(Tags.Items.DYES).add(Tags.Items.DYES_BLACK, Tags.Items.DYES_RED, Tags.Items.DYES_GREEN, Tags.Items.DYES_BROWN, Tags.Items.DYES_BLUE, Tags.Items.DYES_PURPLE, Tags.Items.DYES_CYAN, Tags.Items.DYES_LIGHT_GRAY, Tags.Items.DYES_GRAY, Tags.Items.DYES_PINK, Tags.Items.DYES_LIME, Tags.Items.DYES_YELLOW, Tags.Items.DYES_LIGHT_BLUE, Tags.Items.DYES_MAGENTA, Tags.Items.DYES_ORANGE, Tags.Items.DYES_WHITE);
        getBuilder(Tags.Items.DYES_BLACK).add(Items.BLACK_DYE);
        getBuilder(Tags.Items.DYES_BLUE).add(Items.BLUE_DYE);
        getBuilder(Tags.Items.DYES_BROWN).add(Items.BROWN_DYE);
        getBuilder(Tags.Items.DYES_CYAN).add(Items.CYAN_DYE);
        getBuilder(Tags.Items.DYES_GRAY).add(Items.GRAY_DYE);
        getBuilder(Tags.Items.DYES_GREEN).add(Items.GREEN_DYE);
        getBuilder(Tags.Items.DYES_LIGHT_BLUE).add(Items.LIGHT_BLUE_DYE);
        getBuilder(Tags.Items.DYES_LIGHT_GRAY).add(Items.LIGHT_GRAY_DYE);
        getBuilder(Tags.Items.DYES_LIME).add(Items.LIME_DYE);
        getBuilder(Tags.Items.DYES_MAGENTA).add(Items.MAGENTA_DYE);
        getBuilder(Tags.Items.DYES_ORANGE).add(Items.ORANGE_DYE);
        getBuilder(Tags.Items.DYES_PINK).add(Items.PINK_DYE);
        getBuilder(Tags.Items.DYES_PURPLE).add(Items.PURPLE_DYE);
        getBuilder(Tags.Items.DYES_RED).add(Items.RED_DYE);
        getBuilder(Tags.Items.DYES_WHITE).add(Items.WHITE_DYE);
        getBuilder(Tags.Items.DYES_YELLOW).add(Items.YELLOW_DYE);
        copy(Tags.Blocks.FENCE_GATES, Tags.Items.FENCE_GATES);
        copy(Tags.Blocks.FENCE_GATES_WOODEN, Tags.Items.FENCE_GATES_WOODEN);
        copy(Tags.Blocks.FENCES, Tags.Items.FENCES);
        copy(Tags.Blocks.FENCES_NETHER_BRICK, Tags.Items.FENCES_NETHER_BRICK);
        copy(Tags.Blocks.FENCES_WOODEN, Tags.Items.FENCES_WOODEN);
        getBuilder(Tags.Items.GEMS).add(Tags.Items.GEMS_DIAMOND, Tags.Items.GEMS_EMERALD, Tags.Items.GEMS_LAPIS, Tags.Items.GEMS_PRISMARINE, Tags.Items.GEMS_QUARTZ);
        getBuilder(Tags.Items.GEMS_DIAMOND).add(Items.DIAMOND);
        getBuilder(Tags.Items.GEMS_EMERALD).add(Items.EMERALD);
        getBuilder(Tags.Items.GEMS_LAPIS).add(Items.LAPIS_LAZULI);
        getBuilder(Tags.Items.GEMS_PRISMARINE).add(Items.PRISMARINE_CRYSTALS);
        getBuilder(Tags.Items.GEMS_QUARTZ).add(Items.QUARTZ);
        getBuilder(Tags.Items.INGOTS).add(Tags.Items.INGOTS_IRON, Tags.Items.INGOTS_GOLD, Tags.Items.INGOTS_BRICK, Tags.Items.INGOTS_NETHER_BRICK);
        getBuilder(Tags.Items.INGOTS_BRICK).add(Items.BRICK);
        getBuilder(Tags.Items.INGOTS_GOLD).add(Items.GOLD_INGOT);
        getBuilder(Tags.Items.INGOTS_IRON).add(Items.IRON_INGOT);
        getBuilder(Tags.Items.INGOTS_NETHER_BRICK).add(Items.NETHER_BRICK);
        getBuilder(Tags.Items.MUSIC_DISCS).add(Items.MUSIC_DISC_13, Items.MUSIC_DISC_CAT, Items.MUSIC_DISC_BLOCKS, Items.MUSIC_DISC_CHIRP, Items.MUSIC_DISC_FAR, Items.MUSIC_DISC_MALL, Items.MUSIC_DISC_MELLOHI, Items.MUSIC_DISC_STAL, Items.MUSIC_DISC_STRAD, Items.MUSIC_DISC_WARD, Items.MUSIC_DISC_11, Items.MUSIC_DISC_WAIT);
        getBuilder(Tags.Items.NUGGETS).add(Tags.Items.NUGGETS_IRON, Tags.Items.NUGGETS_GOLD);
        getBuilder(Tags.Items.NUGGETS_IRON).add(Items.IRON_NUGGET);
        getBuilder(Tags.Items.NUGGETS_GOLD).add(Items.GOLD_NUGGET);
        copy(Tags.Blocks.ORES, Tags.Items.ORES);
        copy(Tags.Blocks.ORES_COAL, Tags.Items.ORES_COAL);
        copy(Tags.Blocks.ORES_DIAMOND, Tags.Items.ORES_DIAMOND);
        copy(Tags.Blocks.ORES_EMERALD, Tags.Items.ORES_EMERALD);
        copy(Tags.Blocks.ORES_GOLD, Tags.Items.ORES_GOLD);
        copy(Tags.Blocks.ORES_IRON, Tags.Items.ORES_IRON);
        copy(Tags.Blocks.ORES_LAPIS, Tags.Items.ORES_LAPIS);
        copy(Tags.Blocks.ORES_QUARTZ, Tags.Items.ORES_QUARTZ);
        copy(Tags.Blocks.ORES_REDSTONE, Tags.Items.ORES_REDSTONE);
        getBuilder(Tags.Items.RODS).add(Tags.Items.RODS_BLAZE, Tags.Items.RODS_WOODEN);
        getBuilder(Tags.Items.RODS_BLAZE).add(Items.BLAZE_ROD);
        getBuilder(Tags.Items.RODS_WOODEN).add(Items.STICK);
        copy(Tags.Blocks.STONE, Tags.Items.STONE);
        copy(Tags.Blocks.STORAGE_BLOCKS, Tags.Items.STORAGE_BLOCKS);
        copy(Tags.Blocks.STORAGE_BLOCKS_COAL, Tags.Items.STORAGE_BLOCKS_COAL);
        copy(Tags.Blocks.STORAGE_BLOCKS_DIAMOND, Tags.Items.STORAGE_BLOCKS_DIAMOND);
        copy(Tags.Blocks.STORAGE_BLOCKS_EMERALD, Tags.Items.STORAGE_BLOCKS_EMERALD);
        copy(Tags.Blocks.STORAGE_BLOCKS_GOLD, Tags.Items.STORAGE_BLOCKS_GOLD);
        copy(Tags.Blocks.STORAGE_BLOCKS_IRON, Tags.Items.STORAGE_BLOCKS_IRON);
        copy(Tags.Blocks.STORAGE_BLOCKS_LAPIS, Tags.Items.STORAGE_BLOCKS_LAPIS);
        copy(Tags.Blocks.STORAGE_BLOCKS_QUARTZ, Tags.Items.STORAGE_BLOCKS_QUARTZ);
        copy(Tags.Blocks.STORAGE_BLOCKS_REDSTONE, Tags.Items.STORAGE_BLOCKS_REDSTONE);
    }

    @Override
    protected Path makePath(ResourceLocation id)
    {
        return filter != null && filter.contains(id) ? null : super.makePath(id); //We don't want to save vanilla tags.
    }

    @Override
    public String getName()
    {
        return "Forge Item Tags";
    }
}
