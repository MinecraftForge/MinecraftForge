/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.item;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.event.MarkerEvent;

/**
 * Base class for all {@link ItemEntity} events. Contains a reference to the
 * ItemEntity of interest. For most ItemEntity events, there's little to no
 * additional useful data from the firing method that isn't already contained
 * within the ItemEntity instance.
 */
@MarkerEvent
public sealed interface ItemEvent extends EntityEvent permits ItemExpireEvent, ItemTossEvent {
    ItemEntity entity();
}
