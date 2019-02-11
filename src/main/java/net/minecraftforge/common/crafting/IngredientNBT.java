/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

import javax.annotation.Nullable;

import com.google.gson.JsonObject;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;

public class IngredientNBT extends Ingredient
{
    private final ItemStack stack;
    protected IngredientNBT(ItemStack stack)
    {
        super(Stream.of(new Ingredient.SingleItemList(stack)));
        this.stack = stack;
    }

    @Override
    public boolean test(@Nullable ItemStack input)
    {
        if (input == null)
            return false;
        //Can't use areItemStacksEqualUsingNBTShareTag because it compares stack size as well
        return this.stack.getItem() == input.getItem() && this.stack.getDamage() == input.getDamage() && this.stack.areShareTagsEqual(input);
    }

    @Override
    public boolean isSimple()
    {
        return false;
    }

    public static class Serializer implements IIngredientSerializer<IngredientNBT>
    {
        @Override
        public IngredientNBT parse(PacketBuffer buffer) {
            return new IngredientNBT(buffer.readItemStack());
        }

        @Override
        public IngredientNBT parse(JsonObject json) {
            return new IngredientNBT(CraftingHelper.getItemStack(json, true));
        }

        @Override
        public void write(PacketBuffer buffer, IngredientNBT ingredient) {
            buffer.writeItemStack(ingredient.stack);
        }
    }
}
