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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Brewing recipe representing a mix (i.e. any potion + redstone -> long version of potion, water bottle + netherwart -> awkward potion)
 */
public record MixingBrewingRecipe(@Nonnull ResourceLocation id,
                                  @Nonnull Potion base,
                                  @Nonnull Ingredient reagent,
                                  @Nonnull Potion result) implements IBrewingRecipe
{
    @Override
    public ItemStack assemble(final IBrewingContainer container)
    {
        return PotionUtils.setPotion(new ItemStack(container.base().getItem()), result());
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
        return ForgeMod.MIXING_BREWING_SERIALIZER.get();
    }

    @Override
    public boolean isReagent(final ItemStack reagent)
    {
        return reagent().test(reagent);
    }

    @Override
    public boolean isBase(final ItemStack base)
    {
        return PotionUtils.getPotion(base) == base() && base.is(Tags.Items.POTION_CONTAINERS);
    }

    public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<MixingBrewingRecipe>
    {

        @Override
        public MixingBrewingRecipe fromJson(final ResourceLocation recipeId, final JsonObject serializedRecipe)
        {
            Potion base = ForgeRegistries.POTIONS.getValue(new ResourceLocation(serializedRecipe.get("base").getAsString()));
            if (base == null)
                throw new JsonParseException("Invalid item supplied for base in " + recipeId);
            Ingredient reagent = Ingredient.fromJson(serializedRecipe.getAsJsonObject("reagent"));
            Potion result = ForgeRegistries.POTIONS.getValue(new ResourceLocation(serializedRecipe.get("result").getAsString()));
            if (result == null)
                throw new JsonParseException("Invalid item supplied for result in " + recipeId);
            return new MixingBrewingRecipe(recipeId, base, reagent, result);
        }

        @Nullable
        @Override
        public MixingBrewingRecipe fromNetwork(final ResourceLocation recipeId, final FriendlyByteBuf buffer)
        {
            Potion base = ForgeRegistries.POTIONS.getValue(buffer.readResourceLocation());
            Ingredient reagent = Ingredient.fromNetwork(buffer);
            Potion result = ForgeRegistries.POTIONS.getValue(buffer.readResourceLocation());
            return new MixingBrewingRecipe(recipeId, base, reagent, result);
        }

        @Override
        public void toNetwork(final FriendlyByteBuf buf, final MixingBrewingRecipe recipe)
        {
            buf.writeResourceLocation(ForgeRegistries.POTIONS.getKey(recipe.base()));
            recipe.reagent().toNetwork(buf);
            buf.writeResourceLocation(ForgeRegistries.POTIONS.getKey(recipe.result()));
        }
    }
}
