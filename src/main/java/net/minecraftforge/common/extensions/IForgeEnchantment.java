/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.extensions;

import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import java.util.Set;

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
        * Determines whether item variants of this enchantment can be added to a given creative tab with the allowed categories.
        *
        * @param book the item being added to the creative tab
        * @param allowedCategories the enchantment categories allowed in the creative tab
        * @return whether item variants of this enchantment can be added to a given creative tab with the allowed categories
        */
       default boolean allowedInCreativeTab(Item book, Set<EnchantmentCategory> allowedCategories)
       {
           return self().isAllowedOnBooks() && allowedCategories.contains(self().category);
       }
}
