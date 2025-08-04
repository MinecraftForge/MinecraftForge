/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.item;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.bus.CancellableEventBus;
import net.minecraftforge.eventbus.api.event.characteristic.Cancellable;

/**
 * Event that is fired whenever a player tosses (Q) an item or drag-n-drops a
 * stack of items outside the inventory GUI screens. Canceling the event will
 * stop the items from entering the world, but will not prevent them being
 * removed from the inventory - and thus removed from the system.
 */
public final class ItemTossEvent extends ItemEvent implements Cancellable {
    public static final CancellableEventBus<ItemTossEvent> BUS = CancellableEventBus.create(ItemTossEvent.class);

    private final Player player;

    /**
     * Creates a new event for EntityItems tossed by a player.
     * 
     * @param entityItem The EntityItem being tossed.
     * @param player The player tossing the item.
     */
    public ItemTossEvent(ItemEntity entityItem, Player player) {
        super(entityItem);
        this.player = player;
    }

    /**
     * The player tossing the item.
     */
    public Player getPlayer() {
        return player;
    }
}
