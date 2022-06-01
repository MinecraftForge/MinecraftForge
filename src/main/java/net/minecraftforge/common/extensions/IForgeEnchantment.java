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
}
