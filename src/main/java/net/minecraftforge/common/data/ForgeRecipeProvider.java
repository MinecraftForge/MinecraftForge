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
import net.minecraft.tags.ITag;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;

public class ForgeRecipeProvider extends RecipeProvider
{
    private Map<Item, ITag<Item>> replacements = new HashMap<>();
    private Set<ResourceLocation> excludes = new HashSet<>();

    public ForgeRecipeProvider(DataGenerator generatorIn)
    {
        super(generatorIn);
    }

    private void exclude(IItemProvider item)
    {
        excludes.add(item.asItem().getRegistryName());
    }

    private void replace(IItemProvider item, ITag<Item> tag)
    {
        replacements.put(item.asItem(), tag);
    }

    @Override
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer)
    {
        replace(Items.STICK,        Tags.Items.RODS_WOODEN);
        replace(Items.GOLD_INGOT,   Tags.Items.INGOTS_GOLD);
        replace(Items.IRON_INGOT,   Tags.Items.INGOTS_IRON);
        replace(Items.DIAMOND,      Tags.Items.GEMS_DIAMOND);
        replace(Items.EMERALD,      Tags.Items.GEMS_EMERALD);
        replace(Items.CHEST,        Tags.Items.CHESTS_WOODEN);
        replace(Blocks.COBBLESTONE, Tags.Items.COBBLESTONE);

        exclude(Blocks.GOLD_BLOCK);
        exclude(Items.GOLD_NUGGET);
        exclude(Blocks.IRON_BLOCK);
        exclude(Items.IRON_NUGGET);
        exclude(Blocks.DIAMOND_BLOCK);
        exclude(Blocks.EMERALD_BLOCK);
        
        exclude(Blocks.COBBLESTONE_STAIRS);
        exclude(Blocks.COBBLESTONE_SLAB);
        exclude(Blocks.COBBLESTONE_WALL);

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
        IItemList[] vanillaItems = getField(Ingredient.class, vanilla, 2); //This will probably crash between versions, if null fix index
        for (IItemList entry : vanillaItems)
        {
            if (entry instanceof SingleItemList)
            {
                ItemStack stack = entry.getStacks().stream().findFirst().orElse(ItemStack.EMPTY);
                ITag<Item> replacement = replacements.get(stack.getItem());
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
