/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.brewing;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

public class BrewingRecipe implements IBrewingRecipe
{
    @NotNull private final Ingredient input;
    @NotNull private final Ingredient ingredient;
    @NotNull private final ItemStack output;

    public BrewingRecipe(Ingredient input, Ingredient ingredient, ItemStack output)
    {
        this.input = input;
        this.ingredient = ingredient;
        this.output = output;
    }

    @Override
    public boolean isInput(@NotNull ItemStack stack)
    {
        return this.input.test(stack);
    }

    @Override
    public ItemStack getOutput(ItemStack input, ItemStack ingredient)
    {
        return isInput(input) && isIngredient(ingredient) ? getOutput().copy() : ItemStack.EMPTY;
    }

    public Ingredient getInput()
    {
        return input;
    }

    public Ingredient getIngredient()
    {
        return ingredient;
    }

    public ItemStack getOutput()
    {
        return output;
    }

    @Override
    public boolean isIngredient(ItemStack ingredient)
    {
        return this.ingredient.test(ingredient);
    }
}
