/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.brewing;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

public record BrewingRecipe(Ingredient input, Ingredient ingredient, ItemStack output) implements IBrewingRecipe {

    @Override
    public boolean isInput(@NotNull ItemStack stack) {
        return this.input.test(stack);
    }

    @Override
    public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
        return isInput(input) && isIngredient(ingredient) ? output().copy() : ItemStack.EMPTY;
    }

    @Override
    public Ingredient input() {
        return input;
    }

    @Override
    public Ingredient ingredient() {
        return ingredient;
    }

    @Override
    public ItemStack output() {
        return output;
    }

    @Override
    public boolean isIngredient(ItemStack ingredient) {
        return this.ingredient.test(ingredient);
    }
}
