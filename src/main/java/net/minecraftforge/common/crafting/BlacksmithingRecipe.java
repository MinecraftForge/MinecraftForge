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

package net.minecraftforge.common.crafting;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;

/**
 * A generic anvil recipe with 1 or 2 inputs and an output (can have an amount specified for each ingredient and also an experience cost)
 */
public record BlacksmithingRecipe(ResourceLocation id,
                                  Ingredient base,
                                  int baseCount,
                                  Ingredient additional,
                                  int additionalCount,
                                  ItemStack result,
                                  int xpCost) implements IBlacksmithingRecipe
{
    @Override
    public boolean matches(final ContainerWrapper container, final Level level)
    {
        var base = container.getItem(0);
        var additional = container.getItem(1);
        return base().test(base) && base.getCount() >= baseCount() && additional().test(additional) && additional.getCount() >= additionalCount();
    }

    @Override
    public ItemStack assemble(final ContainerWrapper container)
    {
        container.setXpCost(xpCost());
        container.setItemCost(0, baseCount());
        container.setItemCost(1, additionalCount());
        return result().copy();
    }

    @Override
    public ItemStack getResultItem()
    {
        return result().copy();
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return ForgeMod.BLACKSMITHING_SERIALIZER.get();
    }

    public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<BlacksmithingRecipe>
    {
        @Override
        public BlacksmithingRecipe fromJson(final ResourceLocation id, final JsonObject json)
        {
            return new BlacksmithingRecipe(id,
                    CraftingHelper.getIngredient(json.get("base")),
                    GsonHelper.getAsInt(json, "baseCount", 1),
                    json.has("additional") ? CraftingHelper.getIngredient(json.get("additional")) : Ingredient.of(),
                    json.has("additional") ? GsonHelper.getAsInt(json, "additionalCount", 1) : 0,
                    CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(json, "result"), true),
                    GsonHelper.getAsInt(json, "xpCost", 0));
        }

        @Nullable
        @Override
        public BlacksmithingRecipe fromNetwork(final ResourceLocation id, final FriendlyByteBuf buf)
        {
            return new BlacksmithingRecipe(id,
                    Ingredient.fromNetwork(buf),
                    buf.readInt(),
                    Ingredient.fromNetwork(buf),
                    buf.readInt(),
                    buf.readItem(),
                    buf.readInt());
        }

        @Override
        public void toNetwork(final FriendlyByteBuf buf, final BlacksmithingRecipe recipe)
        {
            recipe.base().toNetwork(buf);
            buf.writeInt(recipe.baseCount());
            recipe.additional().toNetwork(buf);
            buf.writeInt(recipe.additionalCount());
            buf.writeItem(recipe.result());
            buf.writeInt(recipe.xpCost());
        }
    }
}
