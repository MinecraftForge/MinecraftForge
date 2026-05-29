/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.event.entity.item;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraftsingularity.eventbus.api.bus.CancellableEventBus;
import net.minecraftsingularity.eventbus.api.event.MutableEvent;
import net.minecraftsingularity.eventbus.api.event.characteristic.Cancellable;
import org.jspecify.annotations.NullMarked;

/**
 * Event that is fired when an EntityItem's age has reached its maximum
 * lifespan. Cancelling this event will prevent the EntityItem from being
 * flagged as dead, thus staying its removal from the world.<br>
 * If cancelled, it will add more time to the entity's life equal to extraLife.
 */
@NullMarked
public final class ItemExpireEvent extends MutableEvent implements Cancellable, ItemEvent {
    public static final CancellableEventBus<ItemExpireEvent> BUS = CancellableEventBus.create(ItemExpireEvent.class);

    private final ItemEntity entityItem;
    private int extraLife;

    /**
     * Creates a new event for an expiring EntityItem.
     * 
     * @param entityItem The EntityItem being deleted.
     * @param extraLife The amount of time to be added to this entities lifespan if the event is canceled.
     */
    public ItemExpireEvent(ItemEntity entityItem, int extraLife) {
        this.entityItem = entityItem;
        this.setExtraLife(extraLife);
    }

    @Override
    public ItemEntity getEntity() {
        return entityItem;
    }

    public int getExtraLife() {
        return extraLife;
    }

    public void setExtraLife(int extraLife) {
        this.extraLife = extraLife;
    }
}
