/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

package net.minecraftforge.oreregistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.common.util.RecipeUtil;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class ShapedForgeRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe
{
    @Nonnull
    protected final Supplier<ItemStack> result;
    @Nonnull
    protected final NonNullList<Ingredient> input;
    protected int width = 0;
    protected int height = 0;
    protected boolean mirrored = true;
    @Nullable
    protected ResourceLocation group;

    public ShapedForgeRecipe(@Nullable ResourceLocation group, @Nonnull Block result, Object... recipe)
    {
        this(group, RecipeUtil.CheckedItemStackSupplier.create(result), recipe);
    }
    public ShapedForgeRecipe(@Nullable ResourceLocation group, @Nonnull Item result, Object... recipe)
    {
        this(group, RecipeUtil.CheckedItemStackSupplier.create(result), recipe);
    }
    public ShapedForgeRecipe(@Nullable ResourceLocation group, @Nonnull ItemStack result, Object... recipe)
    {
        this(group, RecipeUtil.CheckedItemStackSupplier.create(result), CraftingHelper.parseShaped(recipe));
    }
    public ShapedForgeRecipe(@Nullable ResourceLocation group, @Nonnull Supplier<ItemStack> result, Object... recipe)
    {
        this(group, result, CraftingHelper.parseShaped(recipe));
    }
    public ShapedForgeRecipe(@Nullable ResourceLocation group, @Nonnull Supplier<ItemStack> result, CraftingHelper.ShapedPrimer primer)
    {
        this.group = group;
        this.result = result;
        this.width = primer.width;
        this.height = primer.height;
        this.input = primer.input;
        this.mirrored = primer.mirrored;
    }

    @Override
    @Nonnull
    public ItemStack getCraftingResult(@Nonnull InventoryCrafting var1)
    {
        return result.get();
    }

    @Override
    @Nonnull
    public ItemStack getRecipeOutput()
    {
        return result.get();
    }

    @Override
    public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World world)
    {
        for (int x = 0; x <= 3 - width; x++)
        {
            for (int y = 0; y <= 3 - height; ++y)
            {
                if (RecipeUtil.checkMatch(inv, input, x, y, width, height, false))
                {
                    return true;
                }

                if (mirrored && RecipeUtil.checkMatch(inv, input, x, y, width, height, true))
                {
                    return true;
                }
            }
        }

        return false;
    }

    public ShapedForgeRecipe setMirrored(boolean mirror)
    {
        mirrored = mirror;
        return this;
    }

    @Override
    @Nonnull
    public NonNullList<Ingredient> getIngredients()
    {
        return this.input;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    @Override
    @Nonnull
    public String getGroup()
    {
        return this.group == null ? "" : this.group.toString();
    }

    @Override
    public boolean canFit(int p_194133_1_, int p_194133_2_)
    {
        return p_194133_1_ >= this.width && p_194133_2_ >= this.height;
    }

    public static ShapedForgeRecipe factory(JsonContext context, JsonObject json)
    {
        String group = JsonUtils.getString(json, "group", "");
        CraftingHelper.ShapedPrimer primer = RecipeUtil.getShapedPrimer(context, json);
        JsonObject jsonObject = JsonUtils.getJsonObject(json, "result");
        Supplier<ItemStack> result = CraftingHelper.getItemStackSupplier(jsonObject, context);
        return new ShapedForgeRecipe(group.isEmpty() ? null : new ResourceLocation(group), result, primer);
    }
}
