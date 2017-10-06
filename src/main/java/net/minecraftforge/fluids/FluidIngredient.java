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

package net.minecraftforge.fluids;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntLists;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.JsonContext;

import javax.annotation.Nonnull;
import java.util.Locale;

public class FluidIngredient extends Ingredient
{

    public enum MatchingStrategy
    {
        /**
         * EXACT matching indicates that it will try matching exact amount of fluid.
         * Example: if the FluidIngredient asks for 500 mB water, a vanilla water bucket
         * will not be matched, as it holds 1000 mB water.
         */
        EXACT,

        /**
         * EXCEED matching is the default matching strategy, when not specified.
         * It will try matching those containers that can drain the specified amount of
         * fluid out. Example: if the FluidIngredient asks for 500 mB water, a vanilla bucket
         * will be matched.
         */
        EXCEED,

        /**
         * VOIDING is similar to EXCEED, with only difference that it will try to void exceeded
         * amount of fluid. Example: consider about an IC2 Universal Fluid Cell, which can holds
         * fluids with arbitrary amount, up to 1000 mB. Under VOIDING strategy, if FluidIngredient
         * asks for 500 mB water, and an Fluid Cell with 1000 mB water presents, it will be matched,
         * and only empty Fluid Cell will be given back upon getRemainingItems() is called, i.e.
         * the exceeded 500 mB water is voided.
         *
         * Note that for vanilla bucket, the default behavior will be VOIDING as well.
         */
        VOIDING
    }

    private final FluidStack fluidStack;
    private final MatchingStrategy strategy;

    public FluidIngredient(FluidStack fluidStack, MatchingStrategy strategy)
    {
        super(0);
        Preconditions.checkNotNull(fluidStack, "Null FluidStack is not permitted in FluidIngredient");
        Preconditions.checkNotNull(strategy, "MatchingStrategy cannot be null");
        this.fluidStack = fluidStack;
        this.strategy = strategy;
    }

    public FluidStack getFluidStack()
    {
        return fluidStack.copy();
    }

    public MatchingStrategy getMatchingStrategy()
    {
        return strategy;
    }

    @Override
    public ItemStack[] getMatchingStacks()
    {
        return new ItemStack[0];
    }

    @Override
    public boolean apply(@Nonnull ItemStack itemStackIn)
    {
        FluidStack fluidStackToTest = FluidUtil.getFluidContained(itemStackIn);
        if (fluidStackToTest == null)
            return false;

        switch (this.strategy)
        {
            case EXCEED:
            case VOIDING:
                return fluidStack.isFluidEqual(fluidStackToTest) && fluidStack.amount <= fluidStackToTest.amount;

            case EXACT:
                return fluidStack.isFluidStackIdentical(fluidStackToTest);

            default: // Should be impossible
                return false;
        }
    }

    @Override
    public IntList getValidItemStacksPacked()
    {
        return IntLists.EMPTY_LIST;
    }

    @Nonnull
    public static FluidIngredient factory(JsonContext context, JsonObject json)
    {
        String fluidId = JsonUtils.getString(json, "fluid");
        Fluid fluid = FluidRegistry.getFluid(fluidId);
        if (fluid == null)
            throw new JsonSyntaxException("Invalid fluid id: " + fluidId);
        int amount = JsonUtils.getInt(json, "amount");
        FluidStack fluidStack = new FluidStack(fluid, amount);
        if (json.has("nbt"))
        {
            try
            {
                fluidStack.tag = JsonToNBT.getTagFromJson(JsonUtils.getString(json, "nbt"));
            }
            catch (NBTException e)
            {
                throw new JsonSyntaxException("Invalid NBT entry: " + e.toString());
            }
        }

        MatchingStrategy strategy;
        try
        {
            strategy = MatchingStrategy.valueOf(JsonUtils.getString(json, "match").toUpperCase(Locale.ENGLISH));
        }
        catch (Exception e)
        {
            strategy = MatchingStrategy.EXCEED; // Default to EXCEED;
        }

        return new FluidIngredient(fluidStack, strategy);
    }
}
