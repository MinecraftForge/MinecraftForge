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

        getBuilder(Tags.Items.WEAPONS).add(Tags.Items.WEAPONS_SWORDS, Tags.Items.WEAPONS_BOWS, Tags.Items.WEAPONS_CROSSBOWS, Tags.Items.WEAPONS_TRIDENTS);
        getBuilder(Tags.Items.WEAPONS_SWORDS).add(Tags.Items.WEAPONS_SWORDS_WOODEN, Tags.Items.WEAPONS_SWORDS_STONE, Tags.Items.WEAPONS_SWORDS_IRON, Tags.Items.WEAPONS_SWORDS_GOLDEN, Tags.Items.WEAPONS_SWORDS_DIAMOND);
        getBuilder(Tags.Items.WEAPONS_SWORDS_WOODEN).add(Items.WOODEN_SWORD);
        getBuilder(Tags.Items.WEAPONS_SWORDS_STONE).add(Items.STONE_SWORD);
        getBuilder(Tags.Items.WEAPONS_SWORDS_IRON).add(Items.IRON_SWORD);
        getBuilder(Tags.Items.WEAPONS_SWORDS_GOLDEN).add(Items.GOLDEN_SWORD);
        getBuilder(Tags.Items.WEAPONS_SWORDS_DIAMOND).add(Items.DIAMOND_SWORD);
        getBuilder(Tags.Items.WEAPONS_BOWS).add(Items.BOW);
        getBuilder(Tags.Items.WEAPONS_CROSSBOWS).add(Items.CROSSBOW);
        getBuilder(Tags.Items.WEAPONS_TRIDENTS).add(Items.TRIDENT);

        getBuilder(Tags.Items.TOOLS).add(Tags.Items.TOOLS_HOES, Tags.Items.TOOLS_AXES, Tags.Items.TOOLS_PICKAXES, Tags.Items.TOOLS_SHOVELS, Tags.Items.TOOLS_SHEARS);
        getBuilder(Tags.Items.TOOLS_HOES).add(Tags.Items.TOOLS_HOES_WOODEN, Tags.Items.TOOLS_HOES_STONE, Tags.Items.TOOLS_HOES_IRON, Tags.Items.TOOLS_HOES_GOLDEN, Tags.Items.TOOLS_HOES_DIAMOND);
        getBuilder(Tags.Items.TOOLS_HOES_WOODEN).add(Items.WOODEN_HOE);
        getBuilder(Tags.Items.TOOLS_HOES_STONE).add(Items.STONE_HOE);
        getBuilder(Tags.Items.TOOLS_HOES_IRON).add(Items.IRON_HOE);
        getBuilder(Tags.Items.TOOLS_HOES_GOLDEN).add(Items.GOLDEN_HOE);
        getBuilder(Tags.Items.TOOLS_HOES_DIAMOND).add(Items.DIAMOND_HOE);
        getBuilder(Tags.Items.TOOLS_AXES).add(Tags.Items.TOOLS_AXES_WOODEN, Tags.Items.TOOLS_AXES_STONE, Tags.Items.TOOLS_AXES_IRON, Tags.Items.TOOLS_AXES_GOLDEN, Tags.Items.TOOLS_AXES_DIAMOND);
        getBuilder(Tags.Items.TOOLS_AXES_WOODEN).add(Items.WOODEN_AXE);
        getBuilder(Tags.Items.TOOLS_AXES_STONE).add(Items.STONE_AXE);
        getBuilder(Tags.Items.TOOLS_AXES_IRON).add(Items.IRON_AXE);
        getBuilder(Tags.Items.TOOLS_AXES_GOLDEN).add(Items.GOLDEN_AXE);
        getBuilder(Tags.Items.TOOLS_AXES_DIAMOND).add(Items.DIAMOND_AXE);
        getBuilder(Tags.Items.TOOLS_PICKAXES).add(Tags.Items.TOOLS_PICKAXES_WOODEN, Tags.Items.TOOLS_PICKAXES_STONE, Tags.Items.TOOLS_PICKAXES_IRON, Tags.Items.TOOLS_PICKAXES_GOLDEN, Tags.Items.TOOLS_PICKAXES_DIAMOND);
        getBuilder(Tags.Items.TOOLS_PICKAXES_WOODEN).add(Items.WOODEN_PICKAXE);
        getBuilder(Tags.Items.TOOLS_PICKAXES_STONE).add(Items.STONE_PICKAXE);
        getBuilder(Tags.Items.TOOLS_PICKAXES_IRON).add(Items.IRON_PICKAXE);
        getBuilder(Tags.Items.TOOLS_PICKAXES_GOLDEN).add(Items.GOLDEN_PICKAXE);
        getBuilder(Tags.Items.TOOLS_PICKAXES_DIAMOND).add(Items.DIAMOND_PICKAXE);
        getBuilder(Tags.Items.TOOLS_SHOVELS).add(Tags.Items.TOOLS_SHOVELS_WOODEN, Tags.Items.TOOLS_SHOVELS_STONE, Tags.Items.TOOLS_SHOVELS_IRON, Tags.Items.TOOLS_SHOVELS_GOLDEN, Tags.Items.TOOLS_SHOVELS_DIAMOND);
        getBuilder(Tags.Items.TOOLS_SHOVELS_WOODEN).add(Items.WOODEN_SHOVEL);
        getBuilder(Tags.Items.TOOLS_SHOVELS_STONE).add(Items.STONE_SHOVEL);
        getBuilder(Tags.Items.TOOLS_SHOVELS_IRON).add(Items.IRON_SHOVEL);
        getBuilder(Tags.Items.TOOLS_SHOVELS_GOLDEN).add(Items.GOLDEN_SHOVEL);
        getBuilder(Tags.Items.TOOLS_SHOVELS_DIAMOND).add(Items.DIAMOND_SHOVEL);
        getBuilder(Tags.Items.TOOLS_SHEARS).add(Items.SHEARS);

        getBuilder(Tags.Items.ARMOR).add(Tags.Items.ARMOR_SHIELDS, Tags.Items.ARMOR_HELMETS, Tags.Items.ARMOR_CHESTPLATES, Tags.Items.ARMOR_LEGGINGS, Tags.Items.ARMOR_BOOTS);
        getBuilder(Tags.Items.ARMOR_SHIELDS).add(Items.SHIELD);
        getBuilder(Tags.Items.ARMOR_HELMETS).add(Tags.Items.ARMOR_HELMETS_LEATHER, Tags.Items.ARMOR_HELMETS_IRON, Tags.Items.ARMOR_HELMETS_CHAINMAIL, Tags.Items.ARMOR_HELMETS_GOLDEN, Tags.Items.ARMOR_HELMETS_DIAMOND);
        getBuilder(Tags.Items.ARMOR_HELMETS_LEATHER).add(Items.LEATHER_HELMET);
        getBuilder(Tags.Items.ARMOR_HELMETS_IRON).add(Items.IRON_HELMET);
        getBuilder(Tags.Items.ARMOR_HELMETS_CHAINMAIL).add(Items.CHAINMAIL_HELMET);
        getBuilder(Tags.Items.ARMOR_HELMETS_GOLDEN).add(Items.GOLDEN_HELMET);
        getBuilder(Tags.Items.ARMOR_HELMETS_DIAMOND).add(Items.DIAMOND_HELMET);
        getBuilder(Tags.Items.ARMOR_CHESTPLATES).add(Tags.Items.ARMOR_CHESTPLATES_LEATHER, Tags.Items.ARMOR_CHESTPLATES_IRON, Tags.Items.ARMOR_CHESTPLATES_CHAINMAIL, Tags.Items.ARMOR_CHESTPLATES_GOLDEN, Tags.Items.ARMOR_CHESTPLATES_DIAMOND);
        getBuilder(Tags.Items.ARMOR_CHESTPLATES_LEATHER).add(Items.LEATHER_CHESTPLATE);
        getBuilder(Tags.Items.ARMOR_CHESTPLATES_IRON).add(Items.IRON_CHESTPLATE);
        getBuilder(Tags.Items.ARMOR_CHESTPLATES_CHAINMAIL).add(Items.CHAINMAIL_CHESTPLATE);
        getBuilder(Tags.Items.ARMOR_CHESTPLATES_GOLDEN).add(Items.GOLDEN_CHESTPLATE);
        getBuilder(Tags.Items.ARMOR_CHESTPLATES_DIAMOND).add(Items.DIAMOND_CHESTPLATE);
        getBuilder(Tags.Items.ARMOR_LEGGINGS).add(Tags.Items.ARMOR_LEGGINGS_LEATHER, Tags.Items.ARMOR_LEGGINGS_IRON, Tags.Items.ARMOR_LEGGINGS_CHAINMAIL, Tags.Items.ARMOR_LEGGINGS_GOLDEN, Tags.Items.ARMOR_LEGGINGS_DIAMOND);
        getBuilder(Tags.Items.ARMOR_LEGGINGS_LEATHER).add(Items.LEATHER_LEGGINGS);
        getBuilder(Tags.Items.ARMOR_LEGGINGS_IRON).add(Items.IRON_LEGGINGS);
        getBuilder(Tags.Items.ARMOR_LEGGINGS_CHAINMAIL).add(Items.CHAINMAIL_LEGGINGS);
        getBuilder(Tags.Items.ARMOR_LEGGINGS_GOLDEN).add(Items.GOLDEN_LEGGINGS);
        getBuilder(Tags.Items.ARMOR_LEGGINGS_DIAMOND).add(Items.DIAMOND_LEGGINGS);
        getBuilder(Tags.Items.ARMOR_BOOTS).add(Tags.Items.ARMOR_BOOTS_LEATHER, Tags.Items.ARMOR_BOOTS_IRON, Tags.Items.ARMOR_BOOTS_CHAINMAIL, Tags.Items.ARMOR_BOOTS_GOLDEN, Tags.Items.ARMOR_BOOTS_DIAMOND);
        getBuilder(Tags.Items.ARMOR_BOOTS_LEATHER).add(Items.LEATHER_BOOTS);
        getBuilder(Tags.Items.ARMOR_BOOTS_IRON).add(Items.IRON_BOOTS);
        getBuilder(Tags.Items.ARMOR_BOOTS_CHAINMAIL).add(Items.CHAINMAIL_BOOTS);
        getBuilder(Tags.Items.ARMOR_BOOTS_GOLDEN).add(Items.GOLDEN_BOOTS);
        getBuilder(Tags.Items.ARMOR_BOOTS_DIAMOND).add(Items.DIAMOND_BOOTS);
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
