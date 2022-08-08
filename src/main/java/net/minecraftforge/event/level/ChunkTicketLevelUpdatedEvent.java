/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.level;

import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
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
 **/
public class ChunkTicketLevelUpdatedEvent extends Event
{
    private final ServerLevel level;
    private final long chunkPos;
    private final int oldTicketLevel;
    private final int newTicketLevel;
    @Nullable
    private final ChunkHolder chunkHolder;

    public ChunkTicketLevelUpdatedEvent(ServerLevel level, long chunkPos, int oldTicketLevel, int newTicketLevel, @Nullable ChunkHolder chunkHolder)
    {
        this.level = level;
        this.chunkPos = chunkPos;
        this.oldTicketLevel = oldTicketLevel;
        this.newTicketLevel = newTicketLevel;
        this.chunkHolder = chunkHolder;
    }

    /**
     * {@return the server level containing the chunk}
     */
    public ServerLevel getLevel()
    {
        return this.level;
    }

    /**
     * {@return the long representation of the chunk position the ticket level changed for}
     */
    public long getChunkPos()
    {
        return this.chunkPos;
    }

    /**
     * {@return the previous ticket level the chunk had}
     */
    public int getOldTicketLevel()
    {
        return this.oldTicketLevel;
    }

    /**
     * {@return the new ticket level for the chunk}
     */
    public int getNewTicketLevel()
    {
        return this.newTicketLevel;
    }

    /**
     * {@return chunk that had its ticket level updated}
     */
    @Nullable
    public ChunkHolder getChunkHolder()
    {
        return this.chunkHolder;
    }
}
