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
import java.util.Locale;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;

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

        getBuilder(Tags.Items.ARROWS).add(Items.ARROW, Items.TIPPED_ARROW, Items.SPECTRAL_ARROW);
        getBuilder(Tags.Items.BONES).add(Items.BONE);
        getBuilder(Tags.Items.BOOKSHELVES).add(Items.BOOKSHELF);
        copy(Tags.Blocks.CHESTS, Tags.Items.CHESTS);
        copy(Tags.Blocks.CHESTS_ENDER, Tags.Items.CHESTS_ENDER);
        copy(Tags.Blocks.CHESTS_TRAPPED, Tags.Items.CHESTS_TRAPPED);
        copy(Tags.Blocks.CHESTS_WOODEN, Tags.Items.CHESTS_WOODEN);
        copy(Tags.Blocks.COBBLESTONE, Tags.Items.COBBLESTONE);
        getBuilder(Tags.Items.CROPS).add(Tags.Items.CROPS_BEETROOT, Tags.Items.CROPS_CARROT, Tags.Items.CROPS_NETHER_WART, Tags.Items.CROPS_POTATO, Tags.Items.CROPS_WHEAT);
        getBuilder(Tags.Items.CROPS_BEETROOT).add(Items.BEETROOT);
        getBuilder(Tags.Items.CROPS_CARROT).add(Items.CARROT);
        getBuilder(Tags.Items.CROPS_NETHER_WART).add(Items.NETHER_WART);
        getBuilder(Tags.Items.CROPS_POTATO).add(Items.POTATO);
        getBuilder(Tags.Items.CROPS_WHEAT).add(Items.WHEAT);
        getBuilder(Tags.Items.DUSTS).add(Tags.Items.DUSTS_GLOWSTONE, Tags.Items.DUSTS_PRISMARINE, Tags.Items.DUSTS_REDSTONE);
        getBuilder(Tags.Items.DUSTS_GLOWSTONE).add(Items.GLOWSTONE_DUST);
        getBuilder(Tags.Items.DUSTS_PRISMARINE).add(Items.PRISMARINE_SHARD);
        getBuilder(Tags.Items.DUSTS_REDSTONE).add(Items.REDSTONE);
        addColored(getBuilder(Tags.Items.DYES)::add, Tags.Items.DYES, "{color}_dye");
        getBuilder(Tags.Items.EGGS).add(Items.EGG);
        copy(Tags.Blocks.END_STONES, Tags.Items.END_STONES);
        getBuilder(Tags.Items.ENDER_PEARLS).add(Items.ENDER_PEARL);
        getBuilder(Tags.Items.FEATHERS).add(Items.FEATHER);
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
        copy(Tags.Blocks.GLASS, Tags.Items.GLASS);
        copyColored(Tags.Blocks.GLASS, Tags.Items.GLASS);
        copy(Tags.Blocks.GLASS_PANES, Tags.Items.GLASS_PANES);
        copyColored(Tags.Blocks.GLASS_PANES, Tags.Items.GLASS_PANES);
        copy(Tags.Blocks.GRAVEL, Tags.Items.GRAVEL);
        getBuilder(Tags.Items.GUNPOWDER).add(Items.GUNPOWDER);
        getBuilder(Tags.Items.HEADS).add(Items.CREEPER_HEAD, Items.DRAGON_HEAD, Items.PLAYER_HEAD, Items.SKELETON_SKULL, Items.WITHER_SKELETON_SKULL, Items.ZOMBIE_HEAD);
        getBuilder(Tags.Items.INGOTS).add(Tags.Items.INGOTS_IRON, Tags.Items.INGOTS_GOLD, Tags.Items.INGOTS_BRICK, Tags.Items.INGOTS_NETHER_BRICK);
        getBuilder(Tags.Items.INGOTS_BRICK).add(Items.BRICK);
        getBuilder(Tags.Items.INGOTS_GOLD).add(Items.GOLD_INGOT);
        getBuilder(Tags.Items.INGOTS_IRON).add(Items.IRON_INGOT);
        getBuilder(Tags.Items.INGOTS_NETHER_BRICK).add(Items.NETHER_BRICK);
        getBuilder(Tags.Items.LEATHER).add(Items.LEATHER);
        getBuilder(Tags.Items.MUSHROOMS).add(Items.BROWN_MUSHROOM, Items.RED_MUSHROOM);
        getBuilder(Tags.Items.MUSIC_DISCS).add(Items.MUSIC_DISC_13, Items.MUSIC_DISC_CAT, Items.MUSIC_DISC_BLOCKS, Items.MUSIC_DISC_CHIRP, Items.MUSIC_DISC_FAR, Items.MUSIC_DISC_MALL, Items.MUSIC_DISC_MELLOHI, Items.MUSIC_DISC_STAL, Items.MUSIC_DISC_STRAD, Items.MUSIC_DISC_WARD, Items.MUSIC_DISC_11, Items.MUSIC_DISC_WAIT);
        getBuilder(Tags.Items.NETHER_STARS).add(Items.NETHER_STAR);
        copy(Tags.Blocks.NETHERRACK, Tags.Items.NETHERRACK);
        getBuilder(Tags.Items.NUGGETS).add(Tags.Items.NUGGETS_IRON, Tags.Items.NUGGETS_GOLD);
        getBuilder(Tags.Items.NUGGETS_IRON).add(Items.IRON_NUGGET);
        getBuilder(Tags.Items.NUGGETS_GOLD).add(Items.GOLD_NUGGET);
        copy(Tags.Blocks.OBSIDIAN, Tags.Items.OBSIDIAN);
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
        copy(Tags.Blocks.SAND, Tags.Items.SAND);
        copy(Tags.Blocks.SAND_COLORLESS, Tags.Items.SAND_COLORLESS);
        copy(Tags.Blocks.SAND_RED, Tags.Items.SAND_RED);
        copy(Tags.Blocks.SANDSTONE, Tags.Items.SANDSTONE);
        getBuilder(Tags.Items.SEEDS).add(Tags.Items.SEEDS_BEETROOT, Tags.Items.SEEDS_MELON, Tags.Items.SEEDS_PUMPKIN, Tags.Items.SEEDS_WHEAT);
        getBuilder(Tags.Items.SEEDS_BEETROOT).add(Items.BEETROOT_SEEDS);
        getBuilder(Tags.Items.SEEDS_MELON).add(Items.MELON_SEEDS);
        getBuilder(Tags.Items.SEEDS_PUMPKIN).add(Items.PUMPKIN_SEEDS);
        getBuilder(Tags.Items.SEEDS_WHEAT).add(Items.WHEAT_SEEDS);
        getBuilder(Tags.Items.SLIMEBALLS).add(Items.SLIME_BALL);
        copy(Tags.Blocks.STAINED_GLASS, Tags.Items.STAINED_GLASS);
        copy(Tags.Blocks.STAINED_GLASS_PANES, Tags.Items.STAINED_GLASS_PANES);
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
        getBuilder(Tags.Items.STRING).add(Items.STRING);
        getBuilder(Tags.Items.WEAPONS).add(Tags.Items.SWORDS, Tags.Items.BOWS, Tags.Items.CROSSBOWS, Tags.Items.TRIDENTS);
        getBuilder(Tags.Items.SWORDS).add(Tags.Items.SWORDS_WOODEN, Tags.Items.SWORDS_STONE, Tags.Items.SWORDS_IRON, Tags.Items.SWORDS_GOLDEN, Tags.Items.SWORDS_DIAMOND);
        getBuilder(Tags.Items.SWORDS_WOODEN).add(Items.WOODEN_SWORD);
        getBuilder(Tags.Items.SWORDS_STONE).add(Items.STONE_SWORD);
        getBuilder(Tags.Items.SWORDS_IRON).add(Items.IRON_SWORD);
        getBuilder(Tags.Items.SWORDS_GOLDEN).add(Items.GOLDEN_SWORD);
        getBuilder(Tags.Items.SWORDS_DIAMOND).add(Items.DIAMOND_SWORD);
        getBuilder(Tags.Items.BOWS).add(Items.BOW);
        getBuilder(Tags.Items.CROSSBOWS).add(Items.CROSSBOW);
        getBuilder(Tags.Items.TRIDENTS).add(Items.TRIDENT);
        
        getBuilder(Tags.Items.TOOLS).add(Tags.Items.HOES, Tags.Items.AXES, Tags.Items.PICKAXES, Tags.Items.SHOVELS, Tags.Items.SHEARS);
        getBuilder(Tags.Items.HOES).add(Tags.Items.HOES_WOODEN, Tags.Items.HOES_STONE, Tags.Items.HOES_IRON, Tags.Items.HOES_GOLDEN, Tags.Items.HOES_DIAMOND);
        getBuilder(Tags.Items.HOES_WOODEN).add(Items.WOODEN_HOE);
        getBuilder(Tags.Items.HOES_STONE).add(Items.STONE_HOE);
        getBuilder(Tags.Items.HOES_IRON).add(Items.IRON_HOE);
        getBuilder(Tags.Items.HOES_GOLDEN).add(Items.GOLDEN_HOE);
        getBuilder(Tags.Items.HOES_DIAMOND).add(Items.DIAMOND_HOE);
        getBuilder(Tags.Items.AXES).add(Tags.Items.AXES_WOODEN, Tags.Items.AXES_STONE, Tags.Items.AXES_IRON, Tags.Items.AXES_GOLDEN, Tags.Items.AXES_DIAMOND);
        getBuilder(Tags.Items.AXES_WOODEN).add(Items.WOODEN_AXE);
        getBuilder(Tags.Items.AXES_STONE).add(Items.STONE_AXE);
        getBuilder(Tags.Items.AXES_IRON).add(Items.IRON_AXE);
        getBuilder(Tags.Items.AXES_GOLDEN).add(Items.GOLDEN_AXE);
        getBuilder(Tags.Items.AXES_DIAMOND).add(Items.DIAMOND_AXE);
        getBuilder(Tags.Items.PICKAXES).add(Tags.Items.PICKAXES_WOODEN, Tags.Items.PICKAXES_STONE, Tags.Items.PICKAXES_IRON, Tags.Items.PICKAXES_GOLDEN, Tags.Items.PICKAXES_DIAMOND);
        getBuilder(Tags.Items.PICKAXES_WOODEN).add(Items.WOODEN_PICKAXE);
        getBuilder(Tags.Items.PICKAXES_STONE).add(Items.STONE_PICKAXE);
        getBuilder(Tags.Items.PICKAXES_IRON).add(Items.IRON_PICKAXE);
        getBuilder(Tags.Items.PICKAXES_GOLDEN).add(Items.GOLDEN_PICKAXE);
        getBuilder(Tags.Items.PICKAXES_DIAMOND).add(Items.DIAMOND_PICKAXE);
        getBuilder(Tags.Items.SHOVELS).add(Tags.Items.SHOVELS_WOODEN, Tags.Items.SHOVELS_STONE, Tags.Items.SHOVELS_IRON, Tags.Items.SHOVELS_GOLDEN, Tags.Items.SHOVELS_DIAMOND);
        getBuilder(Tags.Items.SHOVELS_WOODEN).add(Items.WOODEN_SHOVEL);
        getBuilder(Tags.Items.SHOVELS_STONE).add(Items.STONE_SHOVEL);
        getBuilder(Tags.Items.SHOVELS_IRON).add(Items.IRON_SHOVEL);
        getBuilder(Tags.Items.SHOVELS_GOLDEN).add(Items.GOLDEN_SHOVEL);
        getBuilder(Tags.Items.SHOVELS_DIAMOND).add(Items.DIAMOND_SHOVEL);
        getBuilder(Tags.Items.SHEARS).add(Items.SHEARS);
        
        getBuilder(Tags.Items.ARMOR).add(Tags.Items.SHIELDS, Tags.Items.HELMETS, Tags.Items.CHESTPLATES, Tags.Items.LEGGINGS, Tags.Items.BOOTS);
        getBuilder(Tags.Items.SHIELDS).add(Items.SHIELD);
        getBuilder(Tags.Items.HELMETS).add(Tags.Items.HELMETS_LEATHER, Tags.Items.HELMETS_IRON, Tags.Items.HELMETS_CHAIN, Tags.Items.HELMETS_GOLDEN, Tags.Items.HELMETS_DIAMOND);
        getBuilder(Tags.Items.HELMETS_LEATHER).add(Items.LEATHER_HELMET);
        getBuilder(Tags.Items.HELMETS_IRON).add(Items.IRON_HELMET);
        getBuilder(Tags.Items.HELMETS_CHAIN).add(Items.CHAINMAIL_HELMET);
        getBuilder(Tags.Items.HELMETS_GOLDEN).add(Items.GOLDEN_HELMET);
        getBuilder(Tags.Items.HELMETS_DIAMOND).add(Items.DIAMOND_HELMET);
        getBuilder(Tags.Items.CHESTPLATES).add(Tags.Items.CHESTPLATES_LEATHER, Tags.Items.CHESTPLATES_IRON, Tags.Items.CHESTPLATES_CHAIN, Tags.Items.CHESTPLATES_GOLDEN, Tags.Items.CHESTPLATES_DIAMOND);
        getBuilder(Tags.Items.CHESTPLATES_LEATHER).add(Items.LEATHER_CHESTPLATE);
        getBuilder(Tags.Items.CHESTPLATES_IRON).add(Items.IRON_CHESTPLATE);
        getBuilder(Tags.Items.CHESTPLATES_CHAIN).add(Items.CHAINMAIL_CHESTPLATE);
        getBuilder(Tags.Items.CHESTPLATES_GOLDEN).add(Items.GOLDEN_CHESTPLATE);
        getBuilder(Tags.Items.CHESTPLATES_DIAMOND).add(Items.DIAMOND_CHESTPLATE);
        getBuilder(Tags.Items.LEGGINGS).add(Tags.Items.LEGGINGS_LEATHER, Tags.Items.LEGGINGS_IRON, Tags.Items.LEGGINGS_CHAIN, Tags.Items.LEGGINGS_GOLDEN, Tags.Items.LEGGINGS_DIAMOND);
        getBuilder(Tags.Items.LEGGINGS_LEATHER).add(Items.LEATHER_LEGGINGS);
        getBuilder(Tags.Items.LEGGINGS_IRON).add(Items.IRON_LEGGINGS);
        getBuilder(Tags.Items.LEGGINGS_CHAIN).add(Items.CHAINMAIL_LEGGINGS);
        getBuilder(Tags.Items.LEGGINGS_GOLDEN).add(Items.GOLDEN_LEGGINGS);
        getBuilder(Tags.Items.LEGGINGS_DIAMOND).add(Items.DIAMOND_LEGGINGS);
        getBuilder(Tags.Items.BOOTS).add(Tags.Items.BOOTS_LEATHER, Tags.Items.BOOTS_IRON, Tags.Items.BOOTS_CHAIN, Tags.Items.BOOTS_GOLDEN, Tags.Items.BOOTS_DIAMOND);
        getBuilder(Tags.Items.BOOTS_LEATHER).add(Items.LEATHER_BOOTS);
        getBuilder(Tags.Items.BOOTS_IRON).add(Items.IRON_BOOTS);
        getBuilder(Tags.Items.BOOTS_CHAIN).add(Items.CHAINMAIL_BOOTS);
        getBuilder(Tags.Items.BOOTS_GOLDEN).add(Items.GOLDEN_BOOTS);
        getBuilder(Tags.Items.BOOTS_DIAMOND).add(Items.DIAMOND_BOOTS);
    }

    private void addColored(Consumer<Item> consumer, Tag<Item> group, String pattern)
    {
        String prefix = group.getId().getPath().toUpperCase(Locale.ENGLISH) + '_';
        for (DyeColor color  : DyeColor.values())
        {
            ResourceLocation key = new ResourceLocation("minecraft", pattern.replace("{color}",  color.getTranslationKey()));
            Tag<Item> tag = getForgeItemTag(prefix + color.getTranslationKey());
            Item item = ForgeRegistries.ITEMS.getValue(key);
            if (item == null || item  == Items.AIR)
                throw new IllegalStateException("Unknown vanilla item: " + key.toString());
            getBuilder(tag).add(item);
            consumer.accept(item);
        }
    }

    private void copyColored(Tag<Block> blockGroup, Tag<Item> itemGroup)
    {
        String blockPre = blockGroup.getId().getPath().toUpperCase(Locale.ENGLISH) + '_';
        String itemPre = itemGroup.getId().getPath().toUpperCase(Locale.ENGLISH) + '_';
        for (DyeColor color  : DyeColor.values())
        {
            Tag<Block> from = getForgeBlockTag(blockPre + color.getTranslationKey());
            Tag<Item> to = getForgeItemTag(itemPre + color.getTranslationKey());
            copy(from, to);
        }
        copy(getForgeBlockTag(blockPre + "colorless"), getForgeItemTag(itemPre + "colorless"));
    }

    @SuppressWarnings("unchecked")
    private Tag<Block> getForgeBlockTag(String name)
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

    @SuppressWarnings("unchecked")
    private Tag<Item> getForgeItemTag(String name)
    {
        try
        {
            name = name.toUpperCase(Locale.ENGLISH);
            return (Tag<Item>)Tags.Items.class.getDeclaredField(name).get(null);
        }
        catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e)
        {
            throw new IllegalStateException(Tags.Items.class.getName() + " is missing tag name: " + name);
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
        return "Forge Item Tags";
    }
}
