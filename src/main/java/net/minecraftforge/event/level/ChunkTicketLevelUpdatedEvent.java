/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.level;

import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.RecordEvent;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

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
 * This event is fired only on the {@linkplain LogicalSide#SERVER logical server}.
 *
 * @param getLevel the server level containing the chunk
 * @param getChunkPos the long representation of the chunk position the ticket level changed for
 * @param getOldTicketLevel the previous ticket level the chunk had
 * @param getNewTicketLevel the new ticket level the chunk has
 * @param getChunkHolder the chunk that had its ticket level updated, may be null
 *
 * @deprecated This event is scheduled for removal in 26.4 due to a lack of adoption by mods, in order to ease porting.
 *             Please reach out to the Forge team if you are relying on this and would like to see it remain in Forge.
 *             We may keep it or add it back in a future version if there is enough demand.
 */
@NullMarked
@Deprecated(forRemoval = true, since = "26.4")
public record ChunkTicketLevelUpdatedEvent(
        ServerLevel getLevel,
        long getChunkPos,
        int getOldTicketLevel,
        int getNewTicketLevel,
        @Nullable ChunkHolder getChunkHolder
) implements RecordEvent {
    public static final EventBus<ChunkTicketLevelUpdatedEvent> BUS = EventBus.create(ChunkTicketLevelUpdatedEvent.class);
}
