/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.crafting;

import com.google.gson.JsonObject;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.network.FriendlyByteBuf;

public interface IIngredientSerializer<T extends Ingredient>
{
    T parse(FriendlyByteBuf buffer);

    T parse(JsonObject json);

    void write(FriendlyByteBuf buffer, T ingredient);
}
