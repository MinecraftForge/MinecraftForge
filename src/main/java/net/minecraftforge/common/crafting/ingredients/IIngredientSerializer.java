/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.crafting.ingredients;

import com.mojang.serialization.Codec;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.network.FriendlyByteBuf;

public interface IIngredientSerializer<T extends Ingredient> {
    Codec<? extends T> codec();
    void write(FriendlyByteBuf buffer, T value);
    T read(FriendlyByteBuf buffer);

    IIngredientSerializer<Ingredient> VANILLA = new IIngredientSerializer<>() {
        @Override
        public Codec<? extends Ingredient> codec() {
            return Ingredient.VANILLA_CODEC;
        }

        @Override
        public void write(FriendlyByteBuf buffer, Ingredient value) {
            value.toNetwork(buffer);
        }

        @Override
        public Ingredient read(FriendlyByteBuf buffer) {
            return Ingredient.fromNetwork(buffer);
        }
    };
}
