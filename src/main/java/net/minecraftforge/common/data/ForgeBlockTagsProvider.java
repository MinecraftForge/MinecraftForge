/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Locale;
import java.util.function.Consumer;

import static net.minecraftforge.common.Tags.Blocks.*;

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
        tag(COBBLESTONE).addTags(COBBLESTONE_NORMAL, COBBLESTONE_INFESTED, COBBLESTONE_MOSSY, COBBLESTONE_DEEPSLATE);
        tag(COBBLESTONE_NORMAL).add(Blocks.COBBLESTONE);
        tag(COBBLESTONE_INFESTED).add(Blocks.INFESTED_COBBLESTONE);
        tag(COBBLESTONE_MOSSY).add(Blocks.MOSSY_COBBLESTONE);
        tag(COBBLESTONE_DEEPSLATE).add(Blocks.COBBLED_DEEPSLATE);
        tag(END_STONES).add(Blocks.END_STONE);
        tag(ENDERMAN_PLACE_ON_BLACKLIST);
        tag(FENCE_GATES).addTags(FENCE_GATES_WOODEN);
        tag(FENCE_GATES_WOODEN).add(Blocks.OAK_FENCE_GATE, Blocks.SPRUCE_FENCE_GATE, Blocks.BIRCH_FENCE_GATE, Blocks.JUNGLE_FENCE_GATE, Blocks.ACACIA_FENCE_GATE, Blocks.DARK_OAK_FENCE_GATE, Blocks.CRIMSON_FENCE_GATE, Blocks.WARPED_FENCE_GATE);
        tag(FENCES).addTags(FENCES_NETHER_BRICK, FENCES_WOODEN);
        tag(FENCES_NETHER_BRICK).add(Blocks.NETHER_BRICK_FENCE);
        tag(FENCES_WOODEN).addTag(BlockTags.WOODEN_FENCES);
        tag(GLASS).addTags(GLASS_COLORLESS, STAINED_GLASS, GLASS_TINTED);
        tag(GLASS_COLORLESS).add(Blocks.GLASS);
        tag(GLASS_SILICA).add(Blocks.GLASS, Blocks.BLACK_STAINED_GLASS, Blocks.BLUE_STAINED_GLASS, Blocks.BROWN_STAINED_GLASS, Blocks.CYAN_STAINED_GLASS, Blocks.GRAY_STAINED_GLASS, Blocks.GREEN_STAINED_GLASS, Blocks.LIGHT_BLUE_STAINED_GLASS, Blocks.LIGHT_GRAY_STAINED_GLASS, Blocks.LIME_STAINED_GLASS, Blocks.MAGENTA_STAINED_GLASS, Blocks.ORANGE_STAINED_GLASS, Blocks.PINK_STAINED_GLASS, Blocks.PURPLE_STAINED_GLASS, Blocks.RED_STAINED_GLASS, Blocks.WHITE_STAINED_GLASS, Blocks.YELLOW_STAINED_GLASS);
        tag(GLASS_TINTED).add(Blocks.TINTED_GLASS);
        addColored(tag(STAINED_GLASS)::add, GLASS, "{color}_stained_glass");
        tag(GLASS_PANES).addTags(GLASS_PANES_COLORLESS, STAINED_GLASS_PANES);
        tag(GLASS_PANES_COLORLESS).add(Blocks.GLASS_PANE);
        addColored(tag(STAINED_GLASS_PANES)::add, GLASS_PANES, "{color}_stained_glass_pane");
        tag(GRAVEL).add(Blocks.GRAVEL);
        tag(NETHERRACK).add(Blocks.NETHERRACK);
        tag(OBSIDIAN).add(Blocks.OBSIDIAN);
        tag(ORE_BEARING_GROUND_DEEPSLATE).add(Blocks.DEEPSLATE);
        tag(ORE_BEARING_GROUND_NETHERRACK).add(Blocks.NETHERRACK);
        tag(ORE_BEARING_GROUND_STONE).add(Blocks.STONE);
        tag(ORE_RATES_DENSE).add(Blocks.COPPER_ORE, Blocks.DEEPSLATE_COPPER_ORE, Blocks.DEEPSLATE_LAPIS_ORE, Blocks.DEEPSLATE_REDSTONE_ORE, Blocks.LAPIS_ORE, Blocks.REDSTONE_ORE);
        tag(ORE_RATES_SINGULAR).add(Blocks.ANCIENT_DEBRIS, Blocks.COAL_ORE, Blocks.DEEPSLATE_COAL_ORE, Blocks.DEEPSLATE_DIAMOND_ORE, Blocks.DEEPSLATE_EMERALD_ORE, Blocks.DEEPSLATE_GOLD_ORE, Blocks.DEEPSLATE_IRON_ORE, Blocks.DIAMOND_ORE, Blocks.EMERALD_ORE, Blocks.GOLD_ORE, Blocks.IRON_ORE, Blocks.NETHER_QUARTZ_ORE);
        tag(ORE_RATES_SPARSE).add(Blocks.NETHER_GOLD_ORE);
        tag(ORES).addTags(ORES_COAL, ORES_COPPER, ORES_DIAMOND, ORES_EMERALD, ORES_GOLD, ORES_IRON, ORES_LAPIS, ORES_REDSTONE, ORES_QUARTZ, ORES_NETHERITE_SCRAP);
        tag(ORES_COAL).addTag(BlockTags.COAL_ORES);
        tag(ORES_COPPER).addTag(BlockTags.COPPER_ORES);
        tag(ORES_DIAMOND).addTag(BlockTags.DIAMOND_ORES);
        tag(ORES_EMERALD).addTag(BlockTags.EMERALD_ORES);
        tag(ORES_GOLD).addTag(BlockTags.GOLD_ORES);
        tag(ORES_IRON).addTag(BlockTags.IRON_ORES);
        tag(ORES_LAPIS).addTag(BlockTags.LAPIS_ORES);
        tag(ORES_QUARTZ).add(Blocks.NETHER_QUARTZ_ORE);
        tag(ORES_REDSTONE).addTag(BlockTags.REDSTONE_ORES);
        tag(ORES_NETHERITE_SCRAP).add(Blocks.ANCIENT_DEBRIS);
        tag(ORES_IN_GROUND_DEEPSLATE).add(Blocks.DEEPSLATE_COAL_ORE, Blocks.DEEPSLATE_COPPER_ORE, Blocks.DEEPSLATE_DIAMOND_ORE, Blocks.DEEPSLATE_EMERALD_ORE, Blocks.DEEPSLATE_GOLD_ORE, Blocks.DEEPSLATE_IRON_ORE, Blocks.DEEPSLATE_LAPIS_ORE, Blocks.DEEPSLATE_REDSTONE_ORE);
        tag(ORES_IN_GROUND_NETHERRACK).add(Blocks.NETHER_GOLD_ORE, Blocks.NETHER_QUARTZ_ORE);
        tag(ORES_IN_GROUND_STONE).add(Blocks.COAL_ORE, Blocks.COPPER_ORE, Blocks.DIAMOND_ORE, Blocks.EMERALD_ORE, Blocks.GOLD_ORE, Blocks.IRON_ORE, Blocks.LAPIS_ORE, Blocks.REDSTONE_ORE);
        tag(SAND).addTags(SAND_COLORLESS, SAND_RED);
        tag(SAND_COLORLESS).add(Blocks.SAND);
        tag(SAND_RED).add(Blocks.RED_SAND);
        tag(SANDSTONE).add(Blocks.SANDSTONE, Blocks.CUT_SANDSTONE, Blocks.CHISELED_SANDSTONE, Blocks.SMOOTH_SANDSTONE, Blocks.RED_SANDSTONE, Blocks.CUT_RED_SANDSTONE, Blocks.CHISELED_RED_SANDSTONE, Blocks.SMOOTH_RED_SANDSTONE);
        tag(STONE).add(Blocks.ANDESITE, Blocks.DIORITE, Blocks.GRANITE, Blocks.INFESTED_STONE, Blocks.STONE, Blocks.POLISHED_ANDESITE, Blocks.POLISHED_DIORITE, Blocks.POLISHED_GRANITE, Blocks.DEEPSLATE, Blocks.POLISHED_DEEPSLATE, Blocks.INFESTED_DEEPSLATE, Blocks.TUFF);
        tag(STORAGE_BLOCKS).addTags(STORAGE_BLOCKS_AMETHYST, STORAGE_BLOCKS_COAL, STORAGE_BLOCKS_COPPER, STORAGE_BLOCKS_DIAMOND, STORAGE_BLOCKS_EMERALD, STORAGE_BLOCKS_GOLD, STORAGE_BLOCKS_IRON, STORAGE_BLOCKS_LAPIS, STORAGE_BLOCKS_QUARTZ, STORAGE_BLOCKS_RAW_COPPER, STORAGE_BLOCKS_RAW_GOLD, STORAGE_BLOCKS_RAW_IRON, STORAGE_BLOCKS_REDSTONE, STORAGE_BLOCKS_NETHERITE);
        tag(STORAGE_BLOCKS_AMETHYST).add(Blocks.AMETHYST_BLOCK);
        tag(STORAGE_BLOCKS_COAL).add(Blocks.COAL_BLOCK);
        tag(STORAGE_BLOCKS_COPPER).add(Blocks.COPPER_BLOCK);
        tag(STORAGE_BLOCKS_DIAMOND).add(Blocks.DIAMOND_BLOCK);
        tag(STORAGE_BLOCKS_EMERALD).add(Blocks.EMERALD_BLOCK);
        tag(STORAGE_BLOCKS_GOLD).add(Blocks.GOLD_BLOCK);
        tag(STORAGE_BLOCKS_IRON).add(Blocks.IRON_BLOCK);
        tag(STORAGE_BLOCKS_LAPIS).add(Blocks.LAPIS_BLOCK);
        tag(STORAGE_BLOCKS_QUARTZ).add(Blocks.QUARTZ_BLOCK);
        tag(STORAGE_BLOCKS_RAW_COPPER).add(Blocks.RAW_COPPER_BLOCK);
        tag(STORAGE_BLOCKS_RAW_GOLD).add(Blocks.RAW_GOLD_BLOCK);
        tag(STORAGE_BLOCKS_RAW_IRON).add(Blocks.RAW_IRON_BLOCK);
        tag(STORAGE_BLOCKS_REDSTONE).add(Blocks.REDSTONE_BLOCK);
        tag(STORAGE_BLOCKS_NETHERITE).add(Blocks.NETHERITE_BLOCK);
    }

    private void addColored(Consumer<Block> consumer, TagKey<Block> group, String pattern)
    {
        String prefix = group.location().getPath().toUpperCase(Locale.ENGLISH) + '_';
        for (DyeColor color  : DyeColor.values())
        {
            ResourceLocation key = new ResourceLocation("minecraft", pattern.replace("{color}",  color.getName()));
            TagKey<Block> tag = getForgeTag(prefix + color.getName());
            Block block = ForgeRegistries.BLOCKS.getValue(key);
            if (block == null || block  == Blocks.AIR)
                throw new IllegalStateException("Unknown vanilla block: " + key.toString());
            tag(tag).add(block);
            consumer.accept(block);
        }
    }

    @SuppressWarnings("unchecked")
    private TagKey<Block> getForgeTag(String name)
    {
        try
        {
            name = name.toUpperCase(Locale.ENGLISH);
            return (TagKey<Block>) Tags.Blocks.class.getDeclaredField(name).get(null);
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
