/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.level;

import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.RecordEvent;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.Nullable;

/**
 * This event is fired whenever a chunk has its ticket level changed via the server's ChunkMap.
 * <p>
 * This event does not fire if the new ticket level is the same as the old level, or if both the
 * new <strong>AND</strong> old ticket levels represent values past the max chunk distance.
 * <p>
 * Due to how vanilla processes ticket level changes this event may be fired "twice" in one tick for the same chunk.
 * The scenario where this happens is when increasing the level from say 31 (ticking) to 32, the way vanilla does it
 * is by first changing it from 31 to 46, and then queuing the update from 46 to 32. However, when going from 32 to 31,
 * vanilla is able to go directly.
 * <p>
 * This event is not {@linkplain Cancelable cancellable} and does not {@linkplain HasResult have a result}.
 * <p>
 * This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus}
 * only on the {@linkplain LogicalSide#SERVER logical server}.
 *
 * @param level the server level containing the chunk
 * @param chunkPos the long representation of the chunk position the ticket level changed for
 * @param oldTicketLevel the previous ticket level the chunk had
 * @param newTicketLevel the new ticket level for the chunk
 * @param chunkHolder chunk that had its ticket level updated
 **/
public record ChunkTicketLevelUpdatedEvent(
        ServerLevel level,
        long chunkPos,
        int oldTicketLevel,
        int newTicketLevel,
        @Nullable ChunkHolder chunkHolder
) implements RecordEvent {
    public static final EventBus<ChunkTicketLevelUpdatedEvent> BUS = EventBus.create(ChunkTicketLevelUpdatedEvent.class);
}
