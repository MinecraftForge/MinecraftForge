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

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import com.google.gson.JsonObject;

import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.Ingredient.IItemList;
import net.minecraft.item.crafting.Ingredient.TagList;
import net.minecraft.item.crafting.Ingredient.SingleItemList;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;

public class ForgeRecipeProvider extends RecipeProvider
{
    private Map<Item, Tag<Item>> replacements = new HashMap<>();
    private Set<ResourceLocation> excludes = new HashSet<>();

    public ForgeRecipeProvider(DataGenerator generatorIn)
    {
        super(generatorIn);
    }

    private void exclude(IItemProvider item)
    {
        excludes.add(item.asItem().getRegistryName());
    }

    private void exclude(String recipe)
    {
        excludes.add(new ResourceLocation("minecraft", recipe));
    }

    private void replace(IItemProvider item, Tag<Item> tag)
    {
        replacements.put(item.asItem(), tag);
    }

    @Override
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer)
    {
        replace(Items.STICK,               Tags.Items.RODS_WOODEN);
        replace(Items.BLAZE_ROD,           Tags.Items.RODS_BLAZE);
        
        replace(Items.GLOWSTONE_DUST,      Tags.Items.DUSTS_GLOWSTONE);
        replace(Items.PRISMARINE_SHARD,    Tags.Items.DUSTS_PRISMARINE);
        replace(Items.REDSTONE,            Tags.Items.DUSTS_REDSTONE);
        
        replace(Items.BRICK,               Tags.Items.INGOTS_BRICK);
        replace(Items.GOLD_INGOT,          Tags.Items.INGOTS_GOLD);
        replace(Items.IRON_INGOT,          Tags.Items.INGOTS_IRON);
        replace(Items.NETHER_BRICK,        Tags.Items.INGOTS_NETHER_BRICK);
        
        replace(Items.GOLD_NUGGET,         Tags.Items.NUGGETS_GOLD);
        replace(Items.IRON_NUGGET,         Tags.Items.NUGGETS_IRON);
        
        replace(Items.DIAMOND,             Tags.Items.GEMS_DIAMOND);
        replace(Items.EMERALD,             Tags.Items.GEMS_EMERALD);
        replace(Items.LAPIS_LAZULI,        Tags.Items.GEMS_LAPIS);
        replace(Items.PRISMARINE_CRYSTALS, Tags.Items.GEMS_PRISMARINE);
        replace(Items.QUARTZ,              Tags.Items.GEMS_QUARTZ);
        
        replace(Blocks.COAL_BLOCK,         Tags.Items.STORAGE_BLOCKS_COAL);
        replace(Blocks.DIAMOND_BLOCK,      Tags.Items.STORAGE_BLOCKS_DIAMOND);
        replace(Blocks.EMERALD_BLOCK,      Tags.Items.STORAGE_BLOCKS_EMERALD);
        replace(Blocks.GOLD_BLOCK,         Tags.Items.STORAGE_BLOCKS_GOLD);
        replace(Blocks.IRON_BLOCK,         Tags.Items.STORAGE_BLOCKS_IRON);
        replace(Blocks.LAPIS_BLOCK,        Tags.Items.STORAGE_BLOCKS_LAPIS);
        replace(Blocks.QUARTZ_BLOCK,       Tags.Items.STORAGE_BLOCKS_QUARTZ);
        replace(Blocks.REDSTONE_BLOCK,     Tags.Items.STORAGE_BLOCKS_REDSTONE);
        
        replace(Items.CHEST,               Tags.Items.CHESTS_WOODEN);
        replace(Blocks.COBBLESTONE,        Tags.Items.COBBLESTONE);
        
        replace(Items.BLACK_DYE,           Tags.Items.DYES_BLACK);
        replace(Items.BLUE_DYE,            Tags.Items.DYES_BLUE);
        replace(Items.BROWN_DYE,           Tags.Items.DYES_BROWN);
        replace(Items.CYAN_DYE,            Tags.Items.DYES_CYAN);
        replace(Items.GRAY_DYE,            Tags.Items.DYES_GRAY);
        replace(Items.GREEN_DYE,           Tags.Items.DYES_GREEN);
        replace(Items.LIGHT_BLUE_DYE,      Tags.Items.DYES_LIGHT_BLUE);
        replace(Items.LIGHT_GRAY_DYE,      Tags.Items.DYES_LIGHT_GRAY);
        replace(Items.LIME_DYE,            Tags.Items.DYES_LIME);
        replace(Items.MAGENTA_DYE,         Tags.Items.DYES_MAGENTA);
        replace(Items.ORANGE_DYE,          Tags.Items.DYES_ORANGE);
        replace(Items.PINK_DYE,            Tags.Items.DYES_PINK);
        replace(Items.PURPLE_DYE,          Tags.Items.DYES_PURPLE);
        replace(Items.RED_DYE,             Tags.Items.DYES_RED);
        replace(Items.WHITE_DYE,           Tags.Items.DYES_WHITE);
        replace(Items.YELLOW_DYE,          Tags.Items.DYES_YELLOW);

        exclude(Items.REDSTONE);
        exclude(Items.GOLD_INGOT);
        exclude("gold_ingot_from_gold_block");
        exclude("gold_ingot_from_nuggets");
        exclude(Items.IRON_INGOT);
        exclude("iron_ingot_from_iron_block");
        exclude("iron_ingot_from_nuggets");
        exclude(Items.GOLD_NUGGET);
        exclude(Items.IRON_NUGGET);
        exclude(Items.COAL);
        exclude(Items.DIAMOND);
        exclude(Items.EMERALD);
        exclude(Items.LAPIS_LAZULI);
        exclude(Items.PRISMARINE_CRYSTALS);
        exclude(Items.QUARTZ);
        exclude(Blocks.COAL_BLOCK);
        exclude(Blocks.DIAMOND_BLOCK);
        exclude(Blocks.EMERALD_BLOCK);
        exclude(Blocks.GOLD_BLOCK);
        exclude(Blocks.IRON_BLOCK);
        exclude(Blocks.LAPIS_BLOCK);
        exclude(Blocks.QUARTZ_BLOCK);
        exclude(Blocks.REDSTONE_BLOCK);

        super.registerRecipes(vanilla -> {
            IFinishedRecipe modified = enhance(vanilla);
            if (modified != null)
                consumer.accept(modified);
        });
    }

    @Override
    protected void saveRecipeAdvancement(DirectoryCache cache, JsonObject advancementJson, Path pathIn) {
        //NOOP - We dont replace any of the advancement things yet...
    }

    private IFinishedRecipe enhance(IFinishedRecipe vanilla)
    {
        if (vanilla instanceof ShapelessRecipeBuilder.Result)
            return enhance((ShapelessRecipeBuilder.Result)vanilla);
        if (vanilla instanceof ShapedRecipeBuilder.Result)
            return enhance((ShapedRecipeBuilder.Result)vanilla);
        return null;
    }

    private IFinishedRecipe enhance(ShapelessRecipeBuilder.Result vanilla)
    {
        List<Ingredient> ingredients = getField(ShapelessRecipeBuilder.Result.class, vanilla, 4);
        boolean modified = false;
        for (int x = 0; x < ingredients.size(); x++)
        {
            Ingredient ing = enhance(vanilla.getID(), ingredients.get(x));
            if (ing != null)
            {
                ingredients.set(x, ing);
                modified = true;
            }
        }
        return modified ? vanilla : null;
    }

    private IFinishedRecipe enhance(ShapedRecipeBuilder.Result vanilla)
    {
        Map<Character, Ingredient> ingredients = getField(ShapedRecipeBuilder.Result.class, vanilla, 5);
        boolean modified = false;
        for (Character x : ingredients.keySet())
        {
            Ingredient ing = enhance(vanilla.getID(), ingredients.get(x));
            if (ing != null)
            {
                ingredients.put(x, ing);
                modified = true;
            }
        }
        return modified ? vanilla : null;
    }

    private Ingredient enhance(ResourceLocation name, Ingredient vanilla)
    {
        if (excludes.contains(name))
            return null;

        boolean modified = false;
        List<IItemList> items = new ArrayList<>();
        IItemList[] vanillaItems = getField(Ingredient.class, vanilla, 3);
        for (IItemList entry : vanillaItems)
        {
            if (entry instanceof SingleItemList)
            {
                ItemStack stack = entry.getStacks().stream().findFirst().orElse(ItemStack.EMPTY);
                Tag<Item> replacement = replacements.get(stack.getItem());
                if (replacement != null)
                {
                    items.add(new TagList(replacement));
                    modified = true;
                }
                else
                    items.add(entry);
            }
            else
                items.add(entry);
        }
        return modified ? Ingredient.fromItemListStream(items.stream()) : null;
    }

    @SuppressWarnings("unchecked")
    private <T, R> R getField(Class<T> clz, T inst, int index)
    {
        Field fld = clz.getDeclaredFields()[index];
        fld.setAccessible(true);
        try
        {
            return (R)fld.get(inst);
        }
        catch (IllegalArgumentException | IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
    }
}
