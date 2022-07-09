/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.extensions;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;

public interface IForgePotion
{
    private Potion self()
    {
        return (Potion) this;
    }

    /**
     * Determines what creative tabs this potion's variant of a potion-related item (e.g. bottles, tipped arrows) should appear in.
     * @param item The item being added to a creative tab
     * @param tab The creative tab that items are being added to
     * @param isDefaultTab whether the tab is the default tab for this item (e.g. Brewing for bottles, Combat for tipped arrows)
     * @return whether the given Item's variant for this enchantment should appear in the respective creative tab
     */
    default boolean allowedInCreativeTab(Item item, CreativeModeTab tab, boolean isDefaultTab)
    {
        return isDefaultTab;
    }

    /**
     * Determines whether the potion bottle item should be enchanted.
     * Not called for tipped arrows or if the item is already enchanted.
     * @param stack The potion bottle
     * @return whether the item should appear enchanted.
     */
    default boolean isFoil(ItemStack stack)
    {
        return !PotionUtils.getMobEffects(stack).isEmpty();
    }
}
