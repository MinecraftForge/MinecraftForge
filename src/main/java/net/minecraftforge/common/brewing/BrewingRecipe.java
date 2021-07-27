/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

package net.minecraftforge.common.brewing;

import com.google.gson.JsonObject;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BrewingRecipe implements IBrewingRecipe, IRecipe<IInventory>
{
    public static final IRecipeSerializer<BrewingRecipe> SERIALIZER = new Serializer();

    @Nonnull private final ResourceLocation id;
    @Nonnull private final Ingredient input;
    @Nonnull private final Ingredient ingredient;
    @Nonnull private final ItemStack output;

    public BrewingRecipe(ResourceLocation id, Ingredient input, Ingredient ingredient, ItemStack output)
    {
        this.id = id;
        this.input = input;
        this.ingredient = ingredient;
        this.output = output;
    }

    @Override
    public boolean isInput(@Nonnull ItemStack stack)
    {
        return this.input.test(stack);
    }

    @Override
    public ItemStack getOutput(ItemStack input, ItemStack ingredient)
    {
        return isInput(input) && isIngredient(ingredient) ? getOutput().copy() : ItemStack.EMPTY;
    }

    public Ingredient getInput()
    {
        return input;
    }

    public Ingredient getIngredient()
    {
        return ingredient;
    }

    public ItemStack getOutput()
    {
        return output;
    }

    @Override
    public boolean isIngredient(ItemStack ingredient)
    {
        return this.ingredient.test(ingredient);
    }

    @Override
    public boolean matches(final IInventory p_77569_1_, final World p_77569_2_)
    {
        return isIngredient(p_77569_1_.getItem(3)) && (isInput(p_77569_1_.getItem(0)) || isInput(p_77569_1_.getItem(1)) || isInput(p_77569_1_.getItem(2)));
    }

    @Override
    public ItemStack assemble(final IInventory p_77572_1_)
    {
        return this.output.copy();
    }

    @Override
    public boolean canCraftInDimensions(final int p_194133_1_, final int p_194133_2_)
    {
        return true;
    }

    @Override
    public ItemStack getResultItem()
    {
        return this.output;
    }

    @Override
    public ResourceLocation getId()
    {
        return this.id;
    }

    @Override
    public IRecipeSerializer<?> getSerializer()
    {
        return SERIALIZER;
    }

    @Override
    public IRecipeType<?> getType()
    {
        return ForgeMod.BREWING;
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<BrewingRecipe>
    {

        @Override
        public BrewingRecipe fromJson(final ResourceLocation p_199425_1_, final JsonObject p_199425_2_)
        {
            Ingredient input = Ingredient.fromJson(JSONUtils.getAsJsonObject(p_199425_2_, "input"));
            Ingredient ingredient = Ingredient.fromJson(JSONUtils.getAsJsonObject(p_199425_2_, "ingredient"));
            ItemStack output = ShapedRecipe.itemFromJson(JSONUtils.getAsJsonObject(p_199425_2_, "output"));
            return new BrewingRecipe(p_199425_1_, input, ingredient, output);
        }

        @Nullable
        @Override
        public BrewingRecipe fromNetwork(final ResourceLocation p_199426_1_, final PacketBuffer p_199426_2_)
        {
            Ingredient input = Ingredient.fromNetwork(p_199426_2_);
            Ingredient ingredient = Ingredient.fromNetwork(p_199426_2_);
            ItemStack output = p_199426_2_.readItem();
            return new BrewingRecipe(p_199426_1_, input, ingredient, output);
        }

        @Override
        public void toNetwork(final PacketBuffer p_199427_1_, final BrewingRecipe p_199427_2_)
        {
            p_199427_2_.input.toNetwork(p_199427_1_);
            p_199427_2_.ingredient.toNetwork(p_199427_1_);
            p_199427_1_.writeItem(p_199427_2_.output);
        }
    }
}
