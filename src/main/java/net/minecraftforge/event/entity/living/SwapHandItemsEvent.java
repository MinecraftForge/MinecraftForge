/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.living;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class SwapHandItemsEvent extends LivingEvent {

    private ItemStack offHand;
    private ItemStack mainHand;

    public SwapHandItemsEvent(final LivingEntity entity, final ItemStack mainHand, final ItemStack offHand) {
        super(entity);
        this.mainHand = mainHand;
        this.offHand = offHand;
    }

    public ItemStack getOffHand() {
        return this.offHand;
    }

    public void setOffHand(final ItemStack newOffHand) {
        this.offHand = newOffHand;
    }

    public ItemStack getMainHand() {
        return this.mainHand;
    }

    public void setMainHand(final ItemStack newMainHand) {
        this.mainHand = newMainHand;
    }
}
