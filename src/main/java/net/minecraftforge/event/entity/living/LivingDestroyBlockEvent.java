/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.event.entity.living;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.core.BlockPos;
import net.minecraftsingularity.eventbus.api.bus.CancellableEventBus;
import net.minecraftsingularity.eventbus.api.event.RecordEvent;
import net.minecraftsingularity.eventbus.api.event.characteristic.Cancellable;

/**
 * Fired when the ender dragon or wither attempts to destroy a block and when ever a zombie attempts to break a door.
 * Basically an event version of {@link Block#canEntityDestroy(BlockState, BlockGetter, BlockPos, Entity)}<br>
 * <br>
 * This event is {@linkplain Cancellable cancellable}. If this event is cancelled, the block will not be destroyed.
 **/
public record LivingDestroyBlockEvent(LivingEntity getEntity, BlockPos getPos, BlockState getState)
        implements Cancellable, LivingEvent, RecordEvent {
    public static final CancellableEventBus<LivingDestroyBlockEvent> BUS = CancellableEventBus.create(LivingDestroyBlockEvent.class);
}
