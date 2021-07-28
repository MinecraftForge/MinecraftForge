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
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public record BrewingRecipe(@Nonnull ResourceLocation id,
                            @Nonnull Ingredient input,
                            @Nonnull Ingredient ingredient,
                            @Nonnull ItemStack output) implements IBrewingRecipe
{
    @ObjectHolder("forge:brewing")
    public static final RecipeSerializer<BrewingRecipe> SERIALIZER = null;

    @Override
    public boolean isInput(@Nonnull ItemStack stack)
    {
        return this.input.test(stack);
    }

    @Override
    public ItemStack getOutput(ItemStack input, ItemStack ingredient)
    {
        return isInput(input) && isIngredient(ingredient) ? output().copy() : ItemStack.EMPTY;
    }

    @Override
    public boolean isIngredient(ItemStack ingredient)
    {
        return this.ingredient.test(ingredient);
    }

    @Override
    public ItemStack getResultItem()
    {
        return output();
    }

    @Override
    public ResourceLocation getId()
    {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return SERIALIZER;
    }

    public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<BrewingRecipe>
    {

        @Override
        public BrewingRecipe fromJson(final ResourceLocation p_199425_1_, final JsonObject p_199425_2_)
        {
            Ingredient input = Ingredient.fromJson(p_199425_2_.getAsJsonObject("input"));
            Ingredient ingredient = Ingredient.fromJson(p_199425_2_.getAsJsonObject("ingredient"));
            ItemStack output = ShapedRecipe.itemStackFromJson(p_199425_2_.getAsJsonObject("output"));
            return new BrewingRecipe(p_199425_1_, input, ingredient, output);
        }

        @Nullable
        @Override
        public BrewingRecipe fromNetwork(final ResourceLocation p_199426_1_, final FriendlyByteBuf p_199426_2_)
        {
            Ingredient input = Ingredient.fromNetwork(p_199426_2_);
            Ingredient ingredient = Ingredient.fromNetwork(p_199426_2_);
            ItemStack output = p_199426_2_.readItem();
            return new BrewingRecipe(p_199426_1_, input, ingredient, output);
        }

        @Override
        public void toNetwork(final FriendlyByteBuf p_199427_1_, final BrewingRecipe p_199427_2_)
        {
            p_199427_2_.input.toNetwork(p_199427_1_);
            p_199427_2_.ingredient.toNetwork(p_199427_1_);
            p_199427_1_.writeItem(p_199427_2_.output);
        }
    }
}
