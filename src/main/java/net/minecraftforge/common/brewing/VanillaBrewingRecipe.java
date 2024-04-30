/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.brewing;

import java.util.function.BiFunction;

import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionBrewing;

/**
 * Used in BrewingRecipeRegistry to maintain the vanilla behaviour.
 *
 * Most of the code was simply adapted from net.minecraft.tileentity.TileEntityBrewingStand
 */
public class VanillaBrewingRecipe implements IBrewingRecipe {
    private final PotionBrewing vanilla;
    private final BiFunction<ItemStack, ItemStack, ItemStack> vanillaMix;

    public VanillaBrewingRecipe(PotionBrewing vanilla, BiFunction<ItemStack, ItemStack, ItemStack> vanillaMix) {
        this.vanilla = vanilla;
        this.vanillaMix = vanillaMix;
    }

    /**
     * Code adapted from TileEntityBrewingStand.isItemValidForSlot(int index, ItemStack stack)
     */
    @Override
    public boolean isInput(ItemStack stack) {
        return BrewingStandMenu.PotionSlot.mayPlaceItem(stack);
    }

    /**
     * Code adapted from TileEntityBrewingStand.isItemValidForSlot(int index, ItemStack stack)
     */
    @Override
    public boolean isIngredient(ItemStack stack) {
        return vanilla.isContainerIngredient(stack) || vanilla.isPotionIngredient(stack);
    }

    /**
     * Code copied from TileEntityBrewingStand.brewPotions()
     * It brews the potion by doing the bit-shifting magic and then checking if the new PotionEffect list is different to the old one,
     * or if the new potion is a splash potion when the old one wasn't.
     */
    @Override
    public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
        if (!input.isEmpty() && !ingredient.isEmpty() && isIngredient(ingredient)) {
            ItemStack result = vanillaMix.apply(ingredient, input);
            if (result != input) {
                return result;
            }
            return ItemStack.EMPTY;
        }

        return ItemStack.EMPTY;
    }
}
