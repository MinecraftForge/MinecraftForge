/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.extensions;

import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public interface IForgeEnchantment
{
    private Enchantment self()
    {
        return (Enchantment) this;
    }

    /**
     * ItemStack aware version of {@link Enchantment#getDamageBonus(int, MobType)}
     * @param level the level of the enchantment
     * @param mobType the mob type being attacked
     * @param enchantedItem the item used for the attack
     * @return the damage bonus
     */
    @SuppressWarnings("deprecation")
    default float getDamageBonus(int level, MobType mobType, ItemStack enchantedItem)
    {
        return self().getDamageBonus(level, mobType);
    }

    /**
     * Should the book for this enchantment appear in the creative tab
     * @param book Items.ENCHANTED_BOOK normally, but modded items may also use this hook.
     * @param tab The creative tab that items are being added to
     * @return true to put the item variant in the tab.
     */
    default boolean allowedInCreativeTab(net.minecraft.world.item.Item book, net.minecraft.world.item.CreativeModeTab tab) {
        if(!self().isAllowedOnBooks())
            return false;
        else if(tab == net.minecraft.world.item.CreativeModeTab.TAB_SEARCH)
            return self().category != null;
        else
            return tab.hasEnchantmentCategory(self().category);
    }
}
