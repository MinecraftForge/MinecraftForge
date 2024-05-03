/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.extensions;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import javax.annotation.Nullable;

public interface IForgeEnchantment {
    private Enchantment self() {
        return (Enchantment)this;
    }

    /**
     * ItemStack aware version of {@link Enchantment#getDamageBonus(int, MobType)}
     * @param level the level of the enchantment
     * @param mobType the mob type being attacked
     * @param enchantedItem the item used for the attack
     * @return the damage bonus
     */
    @SuppressWarnings("deprecation")
    default float getDamageBonus(int level, @Nullable EntityType<?> mobType, ItemStack enchantedItem) {
        return self().getDamageBonus(level, mobType);
    }

   /**
    * Is this enchantment allowed to be enchanted on books via Enchantment Table
    * @return false to disable the vanilla feature
    */
   default boolean isAllowedOnBooks() {
       return true;
   }

   /**
    * This applies specifically to applying at the enchanting table. The other method {@link #canEnchant(ItemStack)}
    * applies for <i>all possible</i> enchantments.
    */
   default boolean canApplyAtEnchantingTable(ItemStack stack) {
       return stack.canApplyAtEnchantingTable(self());
   }
}
