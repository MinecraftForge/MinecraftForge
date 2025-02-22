/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.player;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.HasResult;
import net.minecraftforge.common.util.Result;
import net.minecraftforge.eventbus.api.bus.CancellableEventBus;
import net.minecraftforge.eventbus.api.event.characteristic.Cancellable;

/**
 * This event is called when a player collides with a EntityItem on the ground.
 * The event can be canceled, and no further processing will be done.
 *
 *  You can set the result of this event to ALLOW which will trigger the
 *  processing of achievements, FML's event, play the sound, and kill the
 *  entity if all the items are picked up.
 *
 *  setResult(ALLOW) is the same as the old setHandled()
 */
public record EntityItemPickupEvent(Player entity, ItemEntity item, Result.Holder resultHolder)
        implements Cancellable, PlayerEvent, HasResult.Record {
    public static final CancellableEventBus<EntityItemPickupEvent> BUS = CancellableEventBus.create(EntityItemPickupEvent.class);
}
