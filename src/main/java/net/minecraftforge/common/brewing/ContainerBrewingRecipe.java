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
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Predicate;

public record ContainerBrewingRecipe(@Nonnull ResourceLocation id,
                                     @Nonnull Item base,
                                     @Nonnull Ingredient reagent,
                                     @Nonnull Item result) implements IBrewingRecipe
{
    @Override
    public boolean matches(final BrewingContainerWrapper container, final Level level)
    {
        return getBase().test(container.base()) && getReagent().test(container.reagent());
    }

    @Override
    public ItemStack assemble(final BrewingContainerWrapper container)
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
    public Ingredient getReagent()
    {
        return reagent();
    }

    @Override
    public Ingredient getBase()
    {
        return Ingredient.of(base());
    }

    public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<ContainerBrewingRecipe>
    {

        @Override
        public ContainerBrewingRecipe fromJson(final ResourceLocation p_199425_1_, final JsonObject p_199425_2_)
        {
            Item input = ForgeRegistries.ITEMS.getValue(new ResourceLocation(GsonHelper.getAsString(p_199425_2_, "base")));
            Ingredient ingredient = Ingredient.fromJson(GsonHelper.getAsJsonObject(p_199425_2_, "reagent"));
            Item output = ForgeRegistries.ITEMS.getValue(new ResourceLocation(GsonHelper.getAsString(p_199425_2_, "result")));
            if (input == null)
                throw new JsonParseException("Invalid item supplied for base in " + p_199425_1_);
            if (output == null)
                throw new JsonParseException("Invalid item supplied for result in " + p_199425_1_);
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
            p_199427_1_.writeResourceLocation(ForgeRegistries.ITEMS.getKey(p_199427_2_.base));
            p_199427_2_.reagent.toNetwork(p_199427_1_);
            p_199427_1_.writeResourceLocation(ForgeRegistries.ITEMS.getKey(p_199427_2_.result));
        }
    }
}
