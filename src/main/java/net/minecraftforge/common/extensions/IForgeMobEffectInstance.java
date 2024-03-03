/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.extensions;

import java.util.List;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.effect.MobEffectInstance;

public interface IForgeMobEffectInstance
{

    /***
     * Returns a list of curative items for the potion effect
     * By default, this list is initialized using {@link MobEffect#getCurativeItems()}
     *
     * @return The list (ItemStack) of curative items for the potion effect
     */
    List<ItemStack> getCurativeItems();

    /***
     * Checks the given ItemStack to see if it is in the list of curative items for the potion effect
     * @param stack The ItemStack being checked against the list of curative items for this PotionEffect
     * @return true if the given ItemStack is in the list of curative items for this PotionEffect, false otherwise
     */
    default boolean isCurativeItem(ItemStack stack) {
       return this.getCurativeItems().stream().anyMatch(e -> e.sameItem(stack));
    }

    /***
     * Sets the list of curative items for this potion effect, overwriting any already present
     * @param curativeItems The list of ItemStacks being set to the potion effect
     */
    void setCurativeItems(List<ItemStack> curativeItems);

    /***
     * Adds the given stack to the list of curative items for this PotionEffect
     * @param stack The ItemStack being added to the curative item list
     */
    default void addCurativeItem(ItemStack stack) {
       if (!this.isCurativeItem(stack))
          this.getCurativeItems().add(stack);
    }

    default void writeCurativeItems(CompoundTag nbt) {
       ListTag list = new ListTag();
       getCurativeItems().forEach(s -> list.add(s.save(new CompoundTag())));
       nbt.put("CurativeItems", list);
    }
}
