/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.living;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * LivingSwapHandItemsEvent is fired when a living entity is about to swap the items in their main and offhand. <br>
 * This event is fired whenever an Entity tries to swap hand items in {@link LivingEntity#swapHandItems()} <br>
 *
 * <p>This event is {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.
 * If this event is cancelled, the Entity will not swap hand items</p>
 */
@Cancelable
public class LivingSwapHandItemsEvent extends LivingEvent
{

    private ItemStack mainHandItem;
    private ItemStack offHandItem;

    public LivingSwapHandItemsEvent(LivingEntity entity)
    {
        super(entity);
        mainHandItem = entity.getMainHandItem();
        offHandItem = entity.getOffhandItem();
    }

    public ItemStack getMainHandItem()
    {
        return mainHandItem;
    }

    public void setMainHandItem(ItemStack mainHandItem)
    {
        this.mainHandItem = mainHandItem;
    }

    public ItemStack getOffHandItem()
    {
        return offHandItem;
    }

    public void setOffHandItem(ItemStack offHandItem)
    {
        this.offHandItem = offHandItem;
    }
}
