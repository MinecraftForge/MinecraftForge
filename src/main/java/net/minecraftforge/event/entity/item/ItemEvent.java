/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.event.entity.item;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraftsingularity.event.entity.EntityEvent;
import org.jspecify.annotations.NullMarked;

/**
 * Base class for all {@link ItemEntity} events. Contains a reference to the
 * ItemEntity of interest. For most ItemEntity events, there's little to no
 * additional useful data from the firing method that isn't already contained
 * within the ItemEntity instance.
 */
@NullMarked
public sealed interface ItemEvent extends EntityEvent permits ItemExpireEvent, ItemTossEvent {
    /**
     * The relevant {@link ItemEntity} for this event.
     */
    @Override
    ItemEntity getEntity();
}
