/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.crafting;

import com.google.gson.JsonObject;

import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;

public interface IIngredientSerializer<T extends Ingredient>
{
    T parse(PacketBuffer buffer);

    T parse(JsonObject json);

    void write(PacketBuffer buffer, T ingredient);
}
