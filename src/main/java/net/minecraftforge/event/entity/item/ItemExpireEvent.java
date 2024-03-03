/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.item;

import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraft.world.entity.item.ItemEntity;

/**
 * Event that is fired when an EntityItem's age has reached its maximum
 * lifespan. Canceling this event will prevent the EntityItem from being
 * flagged as dead, thus staying it's removal from the world. If canceled
 * it will add more time to the entities life equal to extraLife.
 */
@Cancelable
public class ItemExpireEvent extends ItemEvent
{

    private int extraLife;

    /**
     * Creates a new event for an expiring EntityItem.
     * 
     * @param entityItem The EntityItem being deleted.
     * @param extraLife The amount of time to be added to this entities lifespan if the event is canceled.
     */
    public ItemExpireEvent(ItemEntity entityItem, int extraLife)
    {
        super(entityItem);
        this.setExtraLife(extraLife);
    }

    public int getExtraLife()
    {
        return extraLife;
    }

    public void setExtraLife(int extraLife)
    {
        this.extraLife = extraLife;
    }
}
