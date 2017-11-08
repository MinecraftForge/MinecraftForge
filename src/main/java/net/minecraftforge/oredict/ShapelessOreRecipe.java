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

package net.minecraftforge.oredict;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.client.util.RecipeItemHelper;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class ShapelessOreRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe
{
    @Nonnull
    protected ItemStack output = ItemStack.EMPTY;
    protected NonNullList<Ingredient> input = NonNullList.create();
    protected ResourceLocation group;

    public ShapelessOreRecipe(ResourceLocation group, Block result, Object... recipe){ this(group, new ItemStack(result), recipe); }
    public ShapelessOreRecipe(ResourceLocation group, Item  result, Object... recipe){ this(group, new ItemStack(result), recipe); }
    public ShapelessOreRecipe(ResourceLocation group, NonNullList<Ingredient> input, @Nonnull ItemStack result)
    {
        this.group = group;
        output = result.copy();
        this.input = input;
    }
    public ShapelessOreRecipe(ResourceLocation group, @Nonnull ItemStack result, Object... recipe)
    {
        this.group = group;
        output = result.copy();
        for (Object in : recipe)
        {
            Ingredient ing = CraftingHelper.getIngredient(in);
            if (ing != null)
            {
                input.add(ing);
            }
            else
            {
                String ret = "Invalid shapeless ore recipe: ";
                for (Object tmp :  recipe)
                {
                    ret += tmp + ", ";
                }
                ret += output;
                throw new RuntimeException(ret);
            }
        }
    }

    @Override
    @Nonnull
    public ItemStack getRecipeOutput(){ return output; }

    @Override
    @Nonnull
    public ItemStack getCraftingResult(@Nonnull InventoryCrafting var1){ return output.copy(); }

    @Override
    public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World world)
    {
        int ingredientCount = 0;
        RecipeItemHelper recipeItemHelper = new RecipeItemHelper();

        for (int i = 0; i < inv.getSizeInventory(); ++i)
        {
            ItemStack itemstack = inv.getStackInSlot(i);
            if (!itemstack.isEmpty())
            {
                ++ingredientCount;
                recipeItemHelper.accountStack(itemstack);
            }
        }

        return ingredientCount == this.input.size() && recipeItemHelper.canCraft(this, null);
    }

    @Override
    @Nonnull
    public NonNullList<Ingredient> getIngredients()
    {
        return this.input;
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
        return p_194133_1_ * p_194133_2_ >= this.input.size();
    }

    public static ShapelessOreRecipe factory(JsonContext context, JsonObject json)
    {
        String group = JsonUtils.getString(json, "group", "");

        NonNullList<Ingredient> ings = NonNullList.create();
        for (JsonElement ele : JsonUtils.getJsonArray(json, "ingredients"))
            ings.add(CraftingHelper.getIngredient(ele, context));

        if (ings.isEmpty())
            throw new JsonParseException("No ingredients for shapeless recipe");

        ItemStack itemstack = CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "result"), context);
        return new ShapelessOreRecipe(group.isEmpty() ? null : new ResourceLocation(group), ings, itemstack);
    }
}
