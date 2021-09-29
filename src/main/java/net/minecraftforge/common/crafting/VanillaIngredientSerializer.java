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

import java.util.stream.Stream;

import com.google.gson.JsonObject;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.network.FriendlyByteBuf;

public class VanillaIngredientSerializer implements IIngredientSerializer<Ingredient>
{
    public static final VanillaIngredientSerializer INSTANCE  = new VanillaIngredientSerializer();

    public Ingredient parse(FriendlyByteBuf buffer)
    {
        return Ingredient.fromValues(Stream.generate(() -> new Ingredient.ItemValue(buffer.readItem())).limit(buffer.readVarInt()));
    }

    public Ingredient parse(JsonObject json)
    {
       return Ingredient.fromValues(Stream.of(Ingredient.valueFromJson(json)));
    }

    public void write(FriendlyByteBuf buffer, Ingredient ingredient)
    {
        ItemStack[] items = ingredient.getItems();
        buffer.writeVarInt(items.length);

        for (ItemStack stack : items)
            buffer.writeItem(stack);
    }
}
