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
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public record BrewingRecipe(@Nonnull ResourceLocation id,
                            @Nonnull Ingredient base,
                            @Nonnull Ingredient reagent,
                            @Nonnull ItemStack result) implements IBrewingRecipe
{
    @Override
    public boolean matches(final BrewingContainerWrapper container, final Level level)
    {
        final ItemStack reagent = container.getItem(container.ingredientSlot());
        if (reagent.isEmpty() || !this.reagent.test(reagent))
        {
            return false;
        }
        for (int slot : container.potionSlots())
        {
            final ItemStack base = container.getItem(slot);
            if (!base.isEmpty() && this.base.test(base))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public ItemStack assemble(final BrewingContainerWrapper p_44001_)
    {
        return result().copy();
    }

    @Override
    public ItemStack getResultItem()
    {
        return result();
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
    public Ingredient getReagent()
    {
        return reagent();
    }

    @Override
    public Ingredient getBase()
    {
        return base();
    }

    public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<BrewingRecipe>
    {

        @Override
        public BrewingRecipe fromJson(final ResourceLocation p_199425_1_, final JsonObject p_199425_2_)
        {
            Ingredient input = Ingredient.fromJson(GsonHelper.getAsJsonObject(p_199425_2_, "base"));
            Ingredient ingredient = Ingredient.fromJson(GsonHelper.getAsJsonObject(p_199425_2_, "reagent"));
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(p_199425_2_, "result"));
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
            p_199427_2_.base.toNetwork(p_199427_1_);
            p_199427_2_.reagent.toNetwork(p_199427_1_);
            p_199427_1_.writeItem(p_199427_2_.result);
        }
    }
}
