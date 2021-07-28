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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public record ContainerBrewingRecipe(@Nonnull ResourceLocation id,
                                     @Nonnull Item input,
                                     @Nonnull Ingredient ingredient,
                                     @Nonnull Item output) implements IBrewingRecipe
{
    @ObjectHolder("forge:container_brewing")
    public static final RecipeSerializer<ContainerBrewingRecipe> SERIALIZER = null;

    @Override
    public boolean isInput(@Nonnull ItemStack stack)
    {
        return this.input == stack.getItem();
    }

    @Override
    public ItemStack getOutput(ItemStack input, ItemStack ingredient)
    {
        return isInput(input) && isIngredient(ingredient) ? PotionUtils.setPotion(new ItemStack(output()), PotionUtils.getPotion(input)) : ItemStack.EMPTY;
    }

    @Override
    public boolean isIngredient(ItemStack ingredient)
    {
        return this.ingredient.test(ingredient);
    }

    @Override
    public ItemStack getResultItem()
    {
        return new ItemStack(output());
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

    public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<ContainerBrewingRecipe>
    {

        @Override
        public ContainerBrewingRecipe fromJson(final ResourceLocation p_199425_1_, final JsonObject p_199425_2_)
        {
            Item input = ForgeRegistries.ITEMS.getValue(new ResourceLocation(p_199425_2_.get("input_container").getAsString()));
            Ingredient ingredient = Ingredient.fromJson(p_199425_2_.getAsJsonObject("ingredient"));
            Item output = ForgeRegistries.ITEMS.getValue(new ResourceLocation(p_199425_2_.get("output_container").getAsString()));
            return new ContainerBrewingRecipe(p_199425_1_, input, ingredient, output);
        }

        @Nullable
        @Override
        public ContainerBrewingRecipe fromNetwork(final ResourceLocation p_199426_1_, final FriendlyByteBuf p_199426_2_)
        {
            Item input = ForgeRegistries.ITEMS.getValue(p_199426_2_.readResourceLocation());
            Ingredient ingredient = Ingredient.fromNetwork(p_199426_2_);
            Item output = ForgeRegistries.ITEMS.getValue(p_199426_2_.readResourceLocation());
            return new ContainerBrewingRecipe(p_199426_1_, input, ingredient, output);
        }

        @Override
        public void toNetwork(final FriendlyByteBuf p_199427_1_, final ContainerBrewingRecipe p_199427_2_)
        {
            p_199427_1_.writeResourceLocation(ForgeRegistries.ITEMS.getKey(p_199427_2_.input));
            p_199427_2_.ingredient.toNetwork(p_199427_1_);
            p_199427_1_.writeResourceLocation(ForgeRegistries.ITEMS.getKey(p_199427_2_.output));
        }
    }
}
