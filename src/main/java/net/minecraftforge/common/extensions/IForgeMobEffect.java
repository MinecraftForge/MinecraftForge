/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.extensions;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

public interface IForgeMobEffect
{
    private MobEffect self() {
        return (MobEffect)this;
    }

    /**
     * Get a fresh list of items that can cure this Potion.
     * All new PotionEffects created from this Potion will call this to initialize the default curative items
     * @see MobEffectInstance#getCurativeItems()
     * @return A list of items that can cure this Potion
     */
    default List<ItemStack> getCurativeItems() {
       ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
       ret.add(new ItemStack(Items.MILK_BUCKET));
       return ret;
    }

    /**
     * Used for determining {@code PotionEffect} sort order in GUIs.
     * Defaults to the {@code PotionEffect}'s liquid color.
     * @param effectInstance the {@code PotionEffect} instance containing the potion
     * @return a value used to sort {@code PotionEffect}s in GUIs
     */
    default int getSortOrder(MobEffectInstance effectInstance) {
       return self().getColor();
    }
}
