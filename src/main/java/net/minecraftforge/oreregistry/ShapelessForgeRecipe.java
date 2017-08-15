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
import java.util.Iterator;
import java.util.function.Supplier;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
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

public class ShapelessForgeRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe
{
    @Nonnull
    protected final Supplier<ItemStack> result;
    @Nonnull
    protected final NonNullList<Ingredient> input;
    @Nullable
    protected final ResourceLocation group;

    public ShapelessForgeRecipe(@Nullable ResourceLocation group, Block result, Object... recipe)
    {
        this(group, RecipeUtil.CheckedItemStackSupplier.create(result), recipe);
    }
    public ShapelessForgeRecipe(@Nullable ResourceLocation group, Item  result, Object... recipe)
    {
        this(group, RecipeUtil.CheckedItemStackSupplier.create(result), recipe);
    }
    public ShapelessForgeRecipe(@Nullable ResourceLocation group, @Nonnull ItemStack result, Object... recipe)
    {
        this(group, RecipeUtil.CheckedItemStackSupplier.create(result), recipe);
    }
    public ShapelessForgeRecipe(@Nullable ResourceLocation group, @Nonnull Supplier<ItemStack> result, Object... recipe)
    {
        this.group = group;
        this.result = result;
        this.input = NonNullList.create();
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
                ret += result.get();
                throw new RuntimeException(ret);
            }
        }
    }
    public ShapelessForgeRecipe(@Nullable ResourceLocation group, @Nonnull Supplier<ItemStack> result, @Nonnull NonNullList<Ingredient> input)
    {
        this.group = group;
        this.result = result;
        this.input = input;
    }

    @Override
    @Nonnull
    public ItemStack getRecipeOutput(){ return this.result.get(); }

    @Override
    @Nonnull
    public ItemStack getCraftingResult(@Nonnull InventoryCrafting var1){ return this.result.get(); }

    @Override
    public boolean matches(@Nonnull InventoryCrafting var1, @Nonnull World world)
    {
        NonNullList<Ingredient> required = NonNullList.create();
        required.addAll(input);

        for (int x = 0; x < var1.getSizeInventory(); x++)
        {
            ItemStack slot = var1.getStackInSlot(x);

            if (!slot.isEmpty())
            {
                boolean inRecipe = false;
                Iterator<Ingredient> req = required.iterator();

                while (req.hasNext())
                {
                    if (req.next().apply(slot))
                    {
                        inRecipe = true;
                        req.remove();
                        break;
                    }
                }

                if (!inRecipe)
                {
                    return false;
                }
            }
        }

        return required.isEmpty();
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

    public static ShapelessForgeRecipe factory(JsonContext context, JsonObject json)
    {
        String group = JsonUtils.getString(json, "group", "");

        NonNullList<Ingredient> ings = NonNullList.create();
        for (JsonElement ele : JsonUtils.getJsonArray(json, "ingredients"))
            ings.add(CraftingHelper.getIngredient(ele, context));

        if (ings.isEmpty())
            throw new JsonParseException("No ingredients for shapeless recipe");

        JsonObject jsonObject = JsonUtils.getJsonObject(json, "result");
        Supplier<ItemStack> result = CraftingHelper.getItemStackSupplier(jsonObject, context);
        return new ShapelessForgeRecipe(group.isEmpty() ? null : new ResourceLocation(group), result, ings);
    }
}
