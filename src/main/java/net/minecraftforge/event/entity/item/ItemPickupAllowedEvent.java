/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.item;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.items.pickup.ItemPickupReason;

import java.util.Set;

/**
 * Fired to check if the given item is currently allowed to be picked up or not.
 *
 * <p>This event is not {@linkplain Cancelable cancellable}, and {@linkplain HasResult has a result}.</p>
 * <ul>
 *     <li>{@link Result#ALLOW} - Forcibly allow this item to be picked up.</li>
 *     <li>{@link Result#DEFAULT} - Let the default logic dictate if this item should be picked up or not. This relies on the {@link ItemEntity#isPickupAllowed(Object, ItemPickupReason...)} method.</li>
 *     <li>{@link Result#DENY} - Forcibly disallow this item to be picked up.</li>
 * </ul>
 *
 * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
 * only on the {@linkplain LogicalSide#SERVER logical server}.</p>
 *
 * @see net.minecraftforge.items.pickup.ItemPickupReasons for possible collector types
 */
@Event.HasResult
public class ItemPickupAllowedEvent extends ItemEvent
{
    private final Object collector;
    private final Set<ItemPickupReason> pickupReasons;

    @ApiStatus.Internal
    public ItemPickupAllowedEvent(ItemEntity itemEntity, Object collector, Set<ItemPickupReason> pickupReasons)
    {
        super(itemEntity);

        this.collector = collector;
        this.pickupReasons = pickupReasons;
    }

    public Object getCollector()
    {
        return collector;
    }

    public boolean hasPickupReason(ItemPickupReason pickupReason)
    {
        return pickupReasons.contains(pickupReason);
    }
}
