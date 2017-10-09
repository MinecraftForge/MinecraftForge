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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
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
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;

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
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv)
    {
        NonNullList<ItemStack> remainder = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
        List<Ingredient> copyOfInput = new ArrayList<>(input);

        for (int index = 0; index < inv.getSizeInventory(); index++)
        {
            ItemStack itemStack = inv.getStackInSlot(index);
            if (!itemStack.isEmpty())
            {
                boolean hasSettled = false;
                Iterator<Ingredient> remainingIngredient = copyOfInput.iterator();
                while (remainingIngredient.hasNext())
                {
                    Ingredient ingredient = remainingIngredient.next();
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
                            hasSettled = true;
                            remainingIngredient.remove();
                            break;
                        }
                    }
                }

                if (!hasSettled)
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
