/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.world;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import org.apache.commons.lang3.mutable.MutableObject;

/**
 * ChunkWatchEvent is fired when an event involving a chunk being watched occurs.
 * <p>
 * If a method utilizes this {@link Event} as its parameter, the method will
 * receive every child event of this class.
 * <p>
 * {@link #pos} contains the ChunkPos of the Chunk this event is affecting.<br>
 * {@link #world} contains the World of the Chunk this event is affecting.<br>
 * {@link #player} contains the {@link ServerPlayer} that is involved with this chunk being watched.
 * <p>
 * The {@link #player}'s world may not be the same as the world of the chunk
 * when the player is teleporting to another dimension.
 * <p>
 * All children of this event are fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
public class ChunkWatchEvent extends Event
{
    private final ServerLevel world;
    private final ServerPlayer player;
    private final ChunkPos pos;

    public ChunkWatchEvent(ServerPlayer player, ChunkPos pos, ServerLevel world)
    {
        this.player = player;
        this.pos = pos;
        this.world = world;
    }

    public ServerPlayer getPlayer()
    {
        return this.player;
    }

    public ChunkPos getPos()
    {
        return this.pos;
    }

    public ServerLevel getWorld()
    {
        return this.world;
    }

    /**
     * ChunkWatchEvent.Watch is fired when a {@link ServerPlayer} begins watching a chunk.
     * <p>
     * This event is fired when a chunk is added to the watched chunks of a {@link ServerPlayer}
     * and the chunk's data is sent to the client (see
     * {@code net.minecraft.server.level.ChunkMap#playerLoadedChunk(ServerPlayer, MutableObject, LevelChunk)}).
     * <p>
     * This event may be used to send additional chunk-related data to the client.
     * <p>
     * This event is not {@link net.minecraftforge.eventbus.api.Cancelable} and
     * {@link HasResult does not have a result}.
     **/
    public static class Watch extends ChunkWatchEvent
    {
        private final LevelChunk chunk;

        public Watch(ServerPlayer player, LevelChunk chunk, ServerLevel world)
        {
            super(player, chunk.getPos(), world);
            this.chunk = chunk;
        }

        public LevelChunk getChunk()
        {
            return this.chunk;
        }
    }

    /**
     * ChunkWatchEvent.UnWatch is fired when a {@link ServerPlayer} stops watching a chunk.
     * <p>
     * This event is fired when a chunk is removed from the watched chunks of an {@link ServerPlayer}
     * in {@code net.minecraft.server.level.ChunkMap#updateChunkTracking(ServerPlayer, ChunkPos, Packet[], boolean, boolean)}.
     * <p>
     * This event is not {@link net.minecraftforge.eventbus.api.Cancelable} and
     * {@link HasResult does not have a result}.
     **/
    public static class UnWatch extends ChunkWatchEvent
    {
        public UnWatch(ServerPlayer player, ChunkPos pos, ServerLevel world) {super(player, pos, world);}
    }
}
