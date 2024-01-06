/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.brewing;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionBrewing;

/**
 * Used in BrewingRecipeRegistry to maintain the vanilla behaviour.
 *
 * Most of the code was simply adapted from net.minecraft.tileentity.TileEntityBrewingStand
 */
public class VanillaBrewingRecipe implements IBrewingRecipe {

    /**
     * Code adapted from TileEntityBrewingStand.isItemValidForSlot(int index, ItemStack stack)
     */
    @Override
    public boolean isInput(ItemStack stack)
    {
        Item item = stack.getItem();
        return item == Items.POTION || item == Items.SPLASH_POTION || item == Items.LINGERING_POTION || item == Items.GLASS_BOTTLE;
    }

    /**
     * Code adapted from TileEntityBrewingStand.isItemValidForSlot(int index, ItemStack stack)
     */
    @Override
    public boolean isIngredient(ItemStack stack)
    {
        return PotionBrewing.isIngredient(stack);
    }

    /**
     * Code copied from TileEntityBrewingStand.brewPotions()
     * It brews the potion by doing the bit-shifting magic and then checking if the new PotionEffect list is different to the old one,
     * or if the new potion is a splash potion when the old one wasn't.
     */
    @Override
    public ItemStack getOutput(ItemStack input, ItemStack ingredient)
    {
        if (!input.isEmpty() && !ingredient.isEmpty() && isIngredient(ingredient))
        {
            ItemStack result = PotionBrewing.mix(ingredient, input);
            if (result != input)
            {
                return result;
            }
            return ItemStack.EMPTY;
        }

        return ItemStack.EMPTY;
    }
}
