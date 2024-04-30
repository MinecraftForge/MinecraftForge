/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.crafting.ingredients;

import java.util.Arrays;

import com.mojang.serialization.MapCodec;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public interface IIngredientSerializer<T extends Ingredient> {
    MapCodec<? extends T> codec();

    void write(RegistryFriendlyByteBuf buffer, T value);
    T read(RegistryFriendlyByteBuf buffer);


    IIngredientSerializer<Ingredient> VANILLA = new IIngredientSerializer<>() {
        @Override
        public MapCodec<? extends Ingredient> codec() {
            return Ingredient.VANILLA_MAP_CODEC;
        }

        @Override
        public void write(RegistryFriendlyByteBuf buffer, Ingredient value) {
            ItemStack.LIST_STREAM_CODEC.encode(buffer, Arrays.asList(value.getItems()));
        }

        @Override
        public Ingredient read(RegistryFriendlyByteBuf buffer) {
            return Ingredient.of(ItemStack.LIST_STREAM_CODEC.decode(buffer).stream());
        }
    };
}
