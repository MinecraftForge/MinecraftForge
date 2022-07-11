/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.level;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;

/**
 * This event is fired whenever a chunk has a watch-related action.
 * <p>
 * The {@linkplain #getPlayer() player}'s level may not be the same as the {@linkplain #getLevel() level of the chunk}
 * when the player is teleporting to another dimension.
 * <p>
 * This event is not {@linkplain Cancelable cancellable} and does not {@linkplain HasResult have a result}.
 * <p>
 * This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus}
 * only on the {@linkplain LogicalSide#SERVER logical server}.
 **/
public class ChunkWatchEvent extends Event
{
    private final ServerLevel level;
    private final ServerPlayer player;
    private final ChunkPos pos;

    public ChunkWatchEvent(ServerPlayer player, ChunkPos pos, ServerLevel level)
    {
        this.player = player;
        this.pos = pos;
        this.level = level;
    }

    /**
     * {@return the server player involved with the watch action}
     */
    public ServerPlayer getPlayer()
    {
        return this.player;
    }

    /**
     * {@return the chunk position this watch event is affecting}
     */
    public ChunkPos getPos()
    {
        return this.pos;
    }

    /**
     * {@return the server level containing the chunk}
     */
    public ServerLevel getLevel()
    {
        return this.level;
    }

    /**
     * This event is fired whenever a {@link ServerPlayer} begins watching a chunk.
     * <p>
     * This event is fired when a chunk is added to the watched chunks of a {@link ServerPlayer}
     * and the chunk's data is sent to the client (see
     * {@code net.minecraft.server.level.ChunkMap#playerLoadedChunk(ServerPlayer, MutableObject, LevelChunk)}).
     * <p>
     * This event may be used to send additional chunk-related data to the client.
     * <p>
     * This event is not {@linkplain Cancelable cancellable} and does not {@linkplain HasResult have a result}.
     * <p>
     * This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus}
     * only on the {@linkplain LogicalSide#SERVER logical server}.
     **/
    public static class Watch extends ChunkWatchEvent
    {
        private final LevelChunk chunk;

        public Watch(ServerPlayer player, LevelChunk chunk, ServerLevel level)
        {
            super(player, chunk.getPos(), level);
            this.chunk = chunk;
        }

        public LevelChunk getChunk()
        {
            return this.chunk;
        }
    }

    /**
     * This event is fired whenever a {@link ServerPlayer} stops watching a chunk.
     * <p>
     * This event is fired when a chunk is removed from the watched chunks of an {@link ServerPlayer}
     * in {@code net.minecraft.server.level.ChunkMap#updateChunkTracking(ServerPlayer, ChunkPos, Packet[], boolean, boolean)}.
     * <p>
     * This event is not {@linkplain Cancelable cancellable} and does not {@linkplain HasResult have a result}.
     * <p>
     * This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus}
     * only on the {@linkplain LogicalSide#SERVER logical server}.
     **/
    public static class UnWatch extends ChunkWatchEvent
    {
        public UnWatch(ServerPlayer player, ChunkPos pos, ServerLevel level) {super(player, pos, level);}
    }
}
