/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.event.entity.item;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftsingularity.eventbus.api.bus.CancellableEventBus;
import net.minecraftsingularity.eventbus.api.event.RecordEvent;
import net.minecraftsingularity.eventbus.api.event.characteristic.Cancellable;
import org.jspecify.annotations.NullMarked;

/**
 * Event that is fired whenever a player tosses (Q) an item or drag-n-drops a
 * stack of items outside the inventory GUI screens. Cancelling the event will
 * stop the items from entering the world, but will not prevent them being
 * removed from the inventory - and thus removed from the system.
 *
 * @param getEntity The EntityItem being tossed.
 * @param getPlayer The player tossing the item.
 */
@NullMarked
public record ItemTossEvent(ItemEntity getEntity, Player getPlayer) implements Cancellable, ItemEvent, RecordEvent {
    public static final CancellableEventBus<ItemTossEvent> BUS = CancellableEventBus.create(ItemTossEvent.class);
}
