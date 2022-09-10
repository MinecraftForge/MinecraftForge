/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.item;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraftforge.event.entity.EntityEvent;

/**
 * Base class for all {@link ItemEntity} events. Contains a reference to the
 * ItemEntity of interest. For most ItemEntity events, there's little to no
 * additional useful data from the firing method that isn't already contained
 * within the ItemEntity instance.
 */
public class ItemEvent extends EntityEvent
{
    private final ItemEntity itemEntity;

    /**
     * Creates a new event for an {@link ItemEntity}.
     *
     * @param itemEntity The ItemEntity for this event
     */
    public ItemEvent(ItemEntity itemEntity)
    {
        super(itemEntity);
        this.itemEntity = itemEntity;
    }

    /**
     * The relevant {@link ItemEntity} for this event.
     */
    @Override
    public ItemEntity getEntity()
    {
        return itemEntity;
    }
}
