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

import java.util.Locale;
import java.util.function.Consumer;

import net.minecraft.block.Block;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;

public class ForgeItemTagsProvider extends ItemTagsProvider
{
    public ForgeItemTagsProvider(DataGenerator gen, BlockTagsProvider blockTagProvider, ExistingFileHelper existingFileHelper)
    {
        super(gen, blockTagProvider, "forge", existingFileHelper);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void registerTags()
    {
        func_240522_a_(Tags.Items.BONES).func_240534_a_(Items.BONE);
        func_240522_a_(Tags.Items.BOOKSHELVES).func_240534_a_(Items.BOOKSHELF);
        func_240521_a_(Tags.Blocks.CHESTS, Tags.Items.CHESTS);
        func_240521_a_(Tags.Blocks.CHESTS_ENDER, Tags.Items.CHESTS_ENDER);
        func_240521_a_(Tags.Blocks.CHESTS_TRAPPED, Tags.Items.CHESTS_TRAPPED);
        func_240521_a_(Tags.Blocks.CHESTS_WOODEN, Tags.Items.CHESTS_WOODEN);
        func_240521_a_(Tags.Blocks.COBBLESTONE, Tags.Items.COBBLESTONE);
        func_240522_a_(Tags.Items.CROPS).addTags(Tags.Items.CROPS_BEETROOT, Tags.Items.CROPS_CARROT, Tags.Items.CROPS_NETHER_WART, Tags.Items.CROPS_POTATO, Tags.Items.CROPS_WHEAT);
        func_240522_a_(Tags.Items.CROPS_BEETROOT).func_240534_a_(Items.BEETROOT);
        func_240522_a_(Tags.Items.CROPS_CARROT).func_240534_a_(Items.CARROT);
        func_240522_a_(Tags.Items.CROPS_NETHER_WART).func_240534_a_(Items.NETHER_WART);
        func_240522_a_(Tags.Items.CROPS_POTATO).func_240534_a_(Items.POTATO);
        func_240522_a_(Tags.Items.CROPS_WHEAT).func_240534_a_(Items.WHEAT);
        func_240522_a_(Tags.Items.DUSTS).addTags(Tags.Items.DUSTS_GLOWSTONE, Tags.Items.DUSTS_PRISMARINE, Tags.Items.DUSTS_REDSTONE);
        func_240522_a_(Tags.Items.DUSTS_GLOWSTONE).func_240534_a_(Items.GLOWSTONE_DUST);
        func_240522_a_(Tags.Items.DUSTS_PRISMARINE).func_240534_a_(Items.PRISMARINE_SHARD);
        func_240522_a_(Tags.Items.DUSTS_REDSTONE).func_240534_a_(Items.REDSTONE);
        addColored(func_240522_a_(Tags.Items.DYES)::addTags, Tags.Items.DYES, "{color}_dye");
        func_240522_a_(Tags.Items.EGGS).func_240534_a_(Items.EGG);
        func_240521_a_(Tags.Blocks.END_STONES, Tags.Items.END_STONES);
        func_240522_a_(Tags.Items.ENDER_PEARLS).func_240534_a_(Items.ENDER_PEARL);
        func_240522_a_(Tags.Items.FEATHERS).func_240534_a_(Items.FEATHER);
        func_240521_a_(Tags.Blocks.FENCE_GATES, Tags.Items.FENCE_GATES);
        func_240521_a_(Tags.Blocks.FENCE_GATES_WOODEN, Tags.Items.FENCE_GATES_WOODEN);
        func_240521_a_(Tags.Blocks.FENCES, Tags.Items.FENCES);
        func_240521_a_(Tags.Blocks.FENCES_NETHER_BRICK, Tags.Items.FENCES_NETHER_BRICK);
        func_240521_a_(Tags.Blocks.FENCES_WOODEN, Tags.Items.FENCES_WOODEN);
        func_240522_a_(Tags.Items.GEMS).addTags(Tags.Items.GEMS_DIAMOND, Tags.Items.GEMS_EMERALD, Tags.Items.GEMS_LAPIS, Tags.Items.GEMS_PRISMARINE, Tags.Items.GEMS_QUARTZ);
        func_240522_a_(Tags.Items.GEMS_DIAMOND).func_240534_a_(Items.DIAMOND);
        func_240522_a_(Tags.Items.GEMS_EMERALD).func_240534_a_(Items.EMERALD);
        func_240522_a_(Tags.Items.GEMS_LAPIS).func_240534_a_(Items.LAPIS_LAZULI);
        func_240522_a_(Tags.Items.GEMS_PRISMARINE).func_240534_a_(Items.PRISMARINE_CRYSTALS);
        func_240522_a_(Tags.Items.GEMS_QUARTZ).func_240534_a_(Items.QUARTZ);
        func_240521_a_(Tags.Blocks.GLASS, Tags.Items.GLASS);
        func_240521_a_Colored(Tags.Blocks.GLASS, Tags.Items.GLASS);
        func_240521_a_(Tags.Blocks.GLASS_PANES, Tags.Items.GLASS_PANES);
        func_240521_a_Colored(Tags.Blocks.GLASS_PANES, Tags.Items.GLASS_PANES);
        func_240521_a_(Tags.Blocks.GRAVEL, Tags.Items.GRAVEL);
        func_240522_a_(Tags.Items.GUNPOWDER).func_240534_a_(Items.GUNPOWDER);
        func_240522_a_(Tags.Items.HEADS).func_240534_a_(Items.CREEPER_HEAD, Items.DRAGON_HEAD, Items.PLAYER_HEAD, Items.SKELETON_SKULL, Items.WITHER_SKELETON_SKULL, Items.ZOMBIE_HEAD);
        func_240522_a_(Tags.Items.INGOTS).addTags(Tags.Items.INGOTS_IRON, Tags.Items.INGOTS_GOLD, Tags.Items.INGOTS_BRICK, Tags.Items.INGOTS_NETHER_BRICK, Tags.Items.INGOTS_NETHERITE);
        func_240522_a_(Tags.Items.INGOTS_BRICK).func_240534_a_(Items.BRICK);
        func_240522_a_(Tags.Items.INGOTS_GOLD).func_240534_a_(Items.GOLD_INGOT);
        func_240522_a_(Tags.Items.INGOTS_IRON).func_240534_a_(Items.IRON_INGOT);
        func_240522_a_(Tags.Items.INGOTS_NETHERITE).func_240534_a_(Items.field_234759_km_);
        func_240522_a_(Tags.Items.INGOTS_NETHER_BRICK).func_240534_a_(Items.NETHER_BRICK);
        func_240522_a_(Tags.Items.LEATHER).func_240534_a_(Items.LEATHER);
        func_240522_a_(Tags.Items.MUSHROOMS).func_240534_a_(Items.BROWN_MUSHROOM, Items.RED_MUSHROOM);
        func_240522_a_(Tags.Items.NETHER_STARS).func_240534_a_(Items.NETHER_STAR);
        func_240521_a_(Tags.Blocks.NETHERRACK, Tags.Items.NETHERRACK);
        func_240522_a_(Tags.Items.NUGGETS).addTags(Tags.Items.NUGGETS_IRON, Tags.Items.NUGGETS_GOLD);
        func_240522_a_(Tags.Items.NUGGETS_IRON).func_240534_a_(Items.IRON_NUGGET);
        func_240522_a_(Tags.Items.NUGGETS_GOLD).func_240534_a_(Items.GOLD_NUGGET);
        func_240521_a_(Tags.Blocks.OBSIDIAN, Tags.Items.OBSIDIAN);
        func_240521_a_(Tags.Blocks.ORES, Tags.Items.ORES);
        func_240521_a_(Tags.Blocks.ORES_COAL, Tags.Items.ORES_COAL);
        func_240521_a_(Tags.Blocks.ORES_DIAMOND, Tags.Items.ORES_DIAMOND);
        func_240521_a_(Tags.Blocks.ORES_EMERALD, Tags.Items.ORES_EMERALD);
        func_240521_a_(Tags.Blocks.ORES_GOLD, Tags.Items.ORES_GOLD);
        func_240521_a_(Tags.Blocks.ORES_IRON, Tags.Items.ORES_IRON);
        func_240521_a_(Tags.Blocks.ORES_LAPIS, Tags.Items.ORES_LAPIS);
        func_240521_a_(Tags.Blocks.ORES_QUARTZ, Tags.Items.ORES_QUARTZ);
        func_240521_a_(Tags.Blocks.ORES_REDSTONE, Tags.Items.ORES_REDSTONE);
        func_240521_a_(Tags.Blocks.ORES_NETHERITE_SCRAP, Tags.Items.ORES_NETHERITE_SCRAP);
        func_240522_a_(Tags.Items.RODS).addTags(Tags.Items.RODS_BLAZE, Tags.Items.RODS_WOODEN);
        func_240522_a_(Tags.Items.RODS_BLAZE).func_240534_a_(Items.BLAZE_ROD);
        func_240522_a_(Tags.Items.RODS_WOODEN).func_240534_a_(Items.STICK);
        func_240521_a_(Tags.Blocks.SAND, Tags.Items.SAND);
        func_240521_a_(Tags.Blocks.SAND_COLORLESS, Tags.Items.SAND_COLORLESS);
        func_240521_a_(Tags.Blocks.SAND_RED, Tags.Items.SAND_RED);
        func_240521_a_(Tags.Blocks.SANDSTONE, Tags.Items.SANDSTONE);
        func_240522_a_(Tags.Items.SEEDS).addTags(Tags.Items.SEEDS_BEETROOT, Tags.Items.SEEDS_MELON, Tags.Items.SEEDS_PUMPKIN, Tags.Items.SEEDS_WHEAT);
        func_240522_a_(Tags.Items.SEEDS_BEETROOT).func_240534_a_(Items.BEETROOT_SEEDS);
        func_240522_a_(Tags.Items.SEEDS_MELON).func_240534_a_(Items.MELON_SEEDS);
        func_240522_a_(Tags.Items.SEEDS_PUMPKIN).func_240534_a_(Items.PUMPKIN_SEEDS);
        func_240522_a_(Tags.Items.SEEDS_WHEAT).func_240534_a_(Items.WHEAT_SEEDS);
        func_240522_a_(Tags.Items.SHEARS).func_240534_a_(Items.SHEARS);
        func_240522_a_(Tags.Items.SLIMEBALLS).func_240534_a_(Items.SLIME_BALL);
        func_240521_a_(Tags.Blocks.STAINED_GLASS, Tags.Items.STAINED_GLASS);
        func_240521_a_(Tags.Blocks.STAINED_GLASS_PANES, Tags.Items.STAINED_GLASS_PANES);
        func_240521_a_(Tags.Blocks.STONE, Tags.Items.STONE);
        func_240521_a_(Tags.Blocks.STORAGE_BLOCKS, Tags.Items.STORAGE_BLOCKS);
        func_240521_a_(Tags.Blocks.STORAGE_BLOCKS_COAL, Tags.Items.STORAGE_BLOCKS_COAL);
        func_240521_a_(Tags.Blocks.STORAGE_BLOCKS_DIAMOND, Tags.Items.STORAGE_BLOCKS_DIAMOND);
        func_240521_a_(Tags.Blocks.STORAGE_BLOCKS_EMERALD, Tags.Items.STORAGE_BLOCKS_EMERALD);
        func_240521_a_(Tags.Blocks.STORAGE_BLOCKS_GOLD, Tags.Items.STORAGE_BLOCKS_GOLD);
        func_240521_a_(Tags.Blocks.STORAGE_BLOCKS_IRON, Tags.Items.STORAGE_BLOCKS_IRON);
        func_240521_a_(Tags.Blocks.STORAGE_BLOCKS_LAPIS, Tags.Items.STORAGE_BLOCKS_LAPIS);
        func_240521_a_(Tags.Blocks.STORAGE_BLOCKS_QUARTZ, Tags.Items.STORAGE_BLOCKS_QUARTZ);
        func_240521_a_(Tags.Blocks.STORAGE_BLOCKS_REDSTONE, Tags.Items.STORAGE_BLOCKS_REDSTONE);
        func_240521_a_(Tags.Blocks.STORAGE_BLOCKS_NETHERITE, Tags.Items.STORAGE_BLOCKS_NETHERITE);
        func_240522_a_(Tags.Items.STRING).func_240534_a_(Items.STRING);
    }

    private void addColored(Consumer<ITag.INamedTag<Item>> consumer, ITag.INamedTag<Item> group, String pattern)
    {
        String prefix = group.func_230234_a_().getPath().toUpperCase(Locale.ENGLISH) + '_';
        for (DyeColor color  : DyeColor.values())
        {
            ResourceLocation key = new ResourceLocation("minecraft", pattern.replace("{color}",  color.getTranslationKey()));
            ITag.INamedTag<Item> tag = getForgeItemTag(prefix + color.getTranslationKey());
            Item item = ForgeRegistries.ITEMS.getValue(key);
            if (item == null || item  == Items.AIR)
                throw new IllegalStateException("Unknown vanilla item: " + key.toString());
            func_240522_a_(tag).func_240534_a_(item);
            consumer.accept(tag);
        }
    }

    private void func_240521_a_Colored(ITag.INamedTag<Block> blockGroup, ITag.INamedTag<Item> itemGroup)
    {
        String blockPre = blockGroup.func_230234_a_().getPath().toUpperCase(Locale.ENGLISH) + '_';
        String itemPre = itemGroup.func_230234_a_().getPath().toUpperCase(Locale.ENGLISH) + '_';
        for (DyeColor color  : DyeColor.values())
        {
            ITag.INamedTag<Block> from = getForgeBlockTag(blockPre + color.getTranslationKey());
            ITag.INamedTag<Item> to = getForgeItemTag(itemPre + color.getTranslationKey());
            func_240521_a_(from, to);
        }
        func_240521_a_(getForgeBlockTag(blockPre + "colorless"), getForgeItemTag(itemPre + "colorless"));
    }

    @SuppressWarnings("unchecked")
    private ITag.INamedTag<Block> getForgeBlockTag(String name)
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

    @SuppressWarnings("unchecked")
    private ITag.INamedTag<Item> getForgeItemTag(String name)
    {
        try
        {
            name = name.toUpperCase(Locale.ENGLISH);
            return (ITag.INamedTag<Item>)Tags.Items.class.getDeclaredField(name).get(null);
        }
        catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e)
        {
            throw new IllegalStateException(Tags.Items.class.getName() + " is missing tag name: " + name);
        }
    }

    @Override
    public String getName()
    {
        return "Forge Item Tags";
    }
}
