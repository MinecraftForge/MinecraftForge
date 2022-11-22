/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.extensions;

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
