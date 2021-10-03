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
import com.google.gson.JsonParseException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Brewing recipe representing a container upgrade (i.e. potion + gunpowder -> splash potion, splash potion + dragon breath -> lingering potion)
 */
public record ContainerBrewingRecipe(@Nonnull ResourceLocation id,
                                     @Nonnull Item base,
                                     @Nonnull Ingredient reagent,
                                     @Nonnull Item result) implements IBrewingRecipe
{
    @Override
    public ItemStack assemble(final IBrewingContainer container)
    {
        return PotionUtils.setPotion(new ItemStack(result()), PotionUtils.getPotion(container.base()));
    }

    @Override
    public ItemStack getResultItem()
    {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId()
    {
        return id();
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return ForgeMod.CONTAINER_BREWING_SERIALIZER.get();
    }

    @Override
    public boolean isReagent(final ItemStack reagent)
    {
        return reagent().test(reagent);
    }

    @Override
    public boolean isBase(final ItemStack base)
    {
        return base.is(base());
    }

    public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<ContainerBrewingRecipe>
    {

        @Override
        public ContainerBrewingRecipe fromJson(final ResourceLocation recipeId, final JsonObject serializedRecipe)
        {
            Item base = ForgeRegistries.ITEMS.getValue(new ResourceLocation(GsonHelper.getAsString(serializedRecipe, "base")));
            if (base == null)
                throw new JsonParseException("Invalid item supplied for base in " + recipeId);
            Ingredient reagent = Ingredient.fromJson(GsonHelper.getAsJsonObject(serializedRecipe, "reagent"));
            Item result = ForgeRegistries.ITEMS.getValue(new ResourceLocation(GsonHelper.getAsString(serializedRecipe, "result")));
            if (result == null)
                throw new JsonParseException("Invalid item supplied for result in " + recipeId);
            return new ContainerBrewingRecipe(recipeId, base, reagent, result);
        }

        @Nullable
        @Override
        public ContainerBrewingRecipe fromNetwork(final ResourceLocation recipeId, final FriendlyByteBuf buffer)
        {
            Item base = ForgeRegistries.ITEMS.getValue(buffer.readResourceLocation());
            Ingredient reagent = Ingredient.fromNetwork(buffer);
            Item result = ForgeRegistries.ITEMS.getValue(buffer.readResourceLocation());
            return new ContainerBrewingRecipe(recipeId, base, reagent, result);
        }

        @Override
        public void toNetwork(final FriendlyByteBuf buffer, final ContainerBrewingRecipe recipe)
        {
            buffer.writeResourceLocation(ForgeRegistries.ITEMS.getKey(recipe.base()));
            recipe.reagent().toNetwork(buffer);
            buffer.writeResourceLocation(ForgeRegistries.ITEMS.getKey(recipe.result()));
        }
    }
}
