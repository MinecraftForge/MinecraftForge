/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.brewing;

import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.RecordEvent;

import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.common.brewing.IBrewingRecipe;

public record BrewingRecipeRegisterEvent(PotionBrewing.Builder builder, FeatureFlagSet features) implements RecordEvent {
    public static final EventBus<BrewingRecipeRegisterEvent> BUS = EventBus.create(BrewingRecipeRegisterEvent.class);

    /**
     * Adds a recipe to the registry. Due to the nature of the brewing stand
     * inputs that stack (a.k.a max stack size > 1) are not allowed.
     *
     * @param input      The Ingredient that goes in same slots as the water bottles
     *                   would.
     * @param ingredient The Ingredient that goes in the same slot as nether wart would.
     * @param output     The ItemStack that will replace the input once the brewing is
     *                   done.
     */
    public void addRecipe(Ingredient input, Ingredient ingredient, ItemStack output) {
        addRecipe(new BrewingRecipe(input, ingredient, output));
    }

    /**
     * Adds a recipe to the registry. Due to the nature of the brewing stand
     * inputs that stack (a.k.a max stack size > 1) are not allowed.
     */
    public void addRecipe(IBrewingRecipe recipe) {
        builder().add(recipe);
    }
}
