/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.crafting;

import java.util.stream.Stream;

import javax.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;

//TODO: 1.15 Rename to NBTIngredient to match naming.
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

    @Override
    public IIngredientSerializer<? extends Ingredient> getSerializer()
    {
        return Serializer.INSTANCE;
    }

    @Override
    public JsonElement serialize()
    {
        JsonObject json = new JsonObject();
        json.addProperty("type", CraftingHelper.getID(Serializer.INSTANCE).toString());
        json.addProperty("item", stack.getItem().getRegistryName().toString());
        json.addProperty("count", stack.getCount());
        if (stack.hasTag())
            json.addProperty("nbt", stack.getTag().toString());
        return json;
    }

    public static class Serializer implements IIngredientSerializer<IngredientNBT>
    {
        public static final Serializer INSTANCE = new Serializer();

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
