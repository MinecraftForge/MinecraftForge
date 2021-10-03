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
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Generic brewing recipe (base + reagent -> result)
 */
public record BrewingRecipe(@Nonnull ResourceLocation id,
                            @Nonnull Ingredient base,
                            @Nonnull Ingredient reagent,
                            @Nonnull ItemStack result) implements IBrewingRecipe
{
    @Override
    public ItemStack assemble(final IBrewingContainer container)
    {
        return result().copy();
    }

    @Override
    public ItemStack getResultItem()
    {
        return result().copy();
    }

    @Override
    public ResourceLocation getId()
    {
        return id();
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return ForgeMod.BREWING_SERIALIZER.get();
    }

    @Override
    public boolean isBase(final ItemStack base)
    {
        return base().test(base);
    }

    @Override
    public boolean isReagent(final ItemStack reagent)
    {
        return reagent().test(reagent);
    }

    public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<BrewingRecipe>
    {

        @Override
        public BrewingRecipe fromJson(final ResourceLocation recipeId, final JsonObject serializedRecipe)
        {
            Ingredient base = Ingredient.fromJson(GsonHelper.getAsJsonObject(serializedRecipe, "base"));
            Ingredient reagent = Ingredient.fromJson(GsonHelper.getAsJsonObject(serializedRecipe, "reagent"));
            ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(serializedRecipe, "result"));
            return new BrewingRecipe(recipeId, base, reagent, result);
        }

        @Nullable
        @Override
        public BrewingRecipe fromNetwork(final ResourceLocation recipeId, final FriendlyByteBuf buffer)
        {
            Ingredient base = Ingredient.fromNetwork(buffer);
            Ingredient reagent = Ingredient.fromNetwork(buffer);
            ItemStack result = buffer.readItem();
            return new BrewingRecipe(recipeId, base, reagent, result);
        }

        @Override
        public void toNetwork(final FriendlyByteBuf buffer, final BrewingRecipe recipe)
        {
            recipe.base().toNetwork(buffer);
            recipe.reagent().toNetwork(buffer);
            buffer.writeItem(recipe.result());
        }
    }
}
