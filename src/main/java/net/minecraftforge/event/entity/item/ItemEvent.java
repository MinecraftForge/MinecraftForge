/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.item;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraftforge.event.entity.EntityEvent;

/**
 * Base class for all EntityItem events. Contains a reference to the
 * EntityItem of interest. For most EntityItem events, there's little to no
 * additional useful data from the firing method that isn't already contained
 * within the EntityItem instance.
 */
public class ItemEvent extends EntityEvent
{
    private final ItemEntity entityItem;

    /**
     * Creates a new event for an EntityItem.
     * 
     * @param itemEntity The EntityItem for this event
     */
    public ItemEvent(ItemEntity itemEntity)
    {
        super(itemEntity);
        this.entityItem = itemEntity;
    }

    /**
     * The relevant EntityItem for this event, already cast for you.
     */
    public ItemEntity getEntityItem()
    {
        return entityItem;
    }
}
