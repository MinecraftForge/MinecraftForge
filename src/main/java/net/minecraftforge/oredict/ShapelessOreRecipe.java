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
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fluids.FluidIngredient;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.common.util.RecipeMatcher;
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
    protected boolean isSimple = true;

    public ShapelessOreRecipe(ResourceLocation group, Block result, Object... recipe){ this(group, new ItemStack(result), recipe); }
    public ShapelessOreRecipe(ResourceLocation group, Item  result, Object... recipe){ this(group, new ItemStack(result), recipe); }
    public ShapelessOreRecipe(ResourceLocation group, NonNullList<Ingredient> input, @Nonnull ItemStack result)
    {
        this.group = group;
        output = result.copy();
        this.input = input;
        for (Ingredient i : input)
            this.isSimple &= i.isSimple();
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
                this.isSimple &= ing.isSimple();
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
        List<ItemStack> items = Lists.newArrayList();

        for (int i = 0; i < inv.getSizeInventory(); ++i)
        {
            ItemStack itemstack = inv.getStackInSlot(i);
            if (!itemstack.isEmpty())
            {
                ++ingredientCount;
                if (this.isSimple)
                    recipeItemHelper.accountStack(itemstack);
                else
                    items.add(itemstack);
            }
        }

        if (ingredientCount != this.input.size())
            return false;

        if (this.isSimple)
            return recipeItemHelper.canCraft(this, null);

        return RecipeMatcher.findMatches(items, this.input) != null;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv)
    {
        NonNullList<ItemStack> remainder = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
        NonNullList<ItemStack> items = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
        for (int index = 0; index < inv.getSizeInventory(); index++) {
            items.set(index, inv.getStackInSlot(index));
        }
        int[] mapping = RecipeMatcher.findMatches(items, this.input);
        if (mapping == null) {
            return remainder;
        }

        for (int index = 0; index < inv.getSizeInventory(); index++)
        {
            ItemStack itemStack = inv.getStackInSlot(index);
            if (!itemStack.isEmpty())
            {
                Ingredient ingredient = this.input.get(mapping[index]);
                if (ingredient instanceof FluidIngredient && ingredient.apply(itemStack))
                {
                    ItemStack container = itemStack.copy();
                    container.setCount(1);
                    IFluidHandlerItem fluidHandler = FluidUtil.getFluidHandler(container);
                    if (fluidHandler != null)
                    {
                        FluidStack drained = fluidHandler.drain(((FluidIngredient)ingredient).getFluidStack(), true);
                        if (drained == null)
                        {
                            // Fluid type is ensured by the FluidIngredient::apply call above, so it's safe to drain all of them
                            fluidHandler.drain(Integer.MAX_VALUE, true);
                        }
                        remainder.set(index, fluidHandler.getContainer());
                        break;
                    }
                }
                else
                {
                    remainder.set(index, ForgeHooks.getContainerItem(itemStack));
                }
            }
        }

        return remainder;
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
