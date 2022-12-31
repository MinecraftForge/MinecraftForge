/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.item;

import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.items.pickup.ItemPickupReason;

/**
 * Event fired to check if the given item is currently allowed to be picked up or not
 *
 * <p>This event is not {@link Cancelable cancellable}, and {@link HasResult has a result}.</p>
 * <ul>
 *     <li>{@link Result#ALLOW} - forcibly allow this item to be picked up</li>
 *     <li>{@link Result#DEFAULT} - let the default logic dictate if this item should be picked up or not, this relies on the {@link ItemEntity#isPickupAllowed(Object, ItemPickupReason...)} method</li>
 *     <li>{@link Result#DENY} - forcibly disallow this item to be picked up</li>
 * </ul>
 *
 * <p>This event is fired on the {@link MinecraftForge#EVENT_BUS main Forge event bus}</p>
 */
@Event.HasResult
public class ItemPickupAllowedEvent extends ItemEvent
{
    private final Object collector;
    private final ItemPickupReason[] pickupReasons;

    public ItemPickupAllowedEvent(ItemEntity itemEntity, Object collector, ItemPickupReason... pickupReasons)
    {
        super(itemEntity);

        this.collector = collector;
        this.pickupReasons = pickupReasons;
    }

    public ItemStack getItemStack()
    {
        return getEntity().getItem();
    }

    public Level getLevel()
    {
        return getEntity().getLevel();
    }

    public BlockPos getPos()
    {
        return getEntity().blockPosition();
    }

    public Object getCollector()
    {
        return collector;
    }

    public boolean hasPickupReason(ItemPickupReason pickupReason)
    {
        return ArrayUtils.contains(pickupReasons, pickupReason);
    }
}
