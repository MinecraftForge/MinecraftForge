/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.brewing;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.common.brewing.IBrewingRecipe;
import net.minecraftforge.eventbus.api.Event;

public class BrewingRecipeRegisterEvent extends Event {
    private final PotionBrewing.Builder builder;
    private final FeatureFlagSet features;

    @ApiStatus.Internal
    public BrewingRecipeRegisterEvent(PotionBrewing.Builder builder, FeatureFlagSet features) {
        this.builder = builder;
        this.features = features;
    }

    public PotionBrewing.Builder getBuilder() {
        return this.builder;
    }

    public FeatureFlagSet getFeatures() {
        return this.features;
    }

    /**
     * Adds a recipe to the registry. Due to the nature of the brewing stand
     * inputs that stack (a.k.a max stack size > 1) are not allowed.
     *
     * @param input
     *            The Ingredient that goes in same slots as the water bottles
     *            would.
     * @param ingredient
     *            The Ingredient that goes in the same slot as nether wart would.
     * @param output
     *            The ItemStack that will replace the input once the brewing is
     *            done.
     */
    public void addRecipe(Ingredient input, Ingredient ingredient, ItemStack output) {
        addRecipe(new BrewingRecipe(input, ingredient, output));
    }

    /**
     * Adds a recipe to the registry. Due to the nature of the brewing stand
     * inputs that stack (a.k.a max stack size > 1) are not allowed.
     */
    public void addRecipe(IBrewingRecipe recipe) {
        getBuilder().add(recipe);
    }
}
