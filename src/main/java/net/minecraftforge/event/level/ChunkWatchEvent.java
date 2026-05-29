/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.event.level;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftsingularity.common.MinecraftForge;
import net.minecraftsingularity.eventbus.api.bus.EventBus;
import net.minecraftsingularity.eventbus.api.event.InheritableEvent;
import net.minecraftsingularity.fml.LogicalSide;

/**
 * This event is fired whenever a chunk has a watch-related action.
 * <p>
 * The {@linkplain #getPlayer() player}'s level may not be the same as the {@linkplain #getLevel() level of the chunk}
 * when the player is teleporting to another dimension.
 * <p>
 * This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main singularity event bus}
 * only on the {@linkplain LogicalSide#SERVER logical server}.
 **/
public sealed interface ChunkWatchEvent extends InheritableEvent {
    EventBus<ChunkWatchEvent> BUS = EventBus.create(ChunkWatchEvent.class);

    /**
     * {@return the server player involved with the watch action}
     */
    ServerPlayer getPlayer();

    /**
     * {@return the chunk position this watch event is affecting}
     */
    ChunkPos getPos();

    /**
     * {@return the server level containing the chunk}
     */
    ServerLevel getLevel();

    /**
     * This event is fired when chunk data is sent to the {@link ServerPlayer} (see {@link net.minecraft.server.network.PlayerChunkSender}).
     * <p>
     * This event may be used to send additional chunk-related data to the client.
     * <p>
     * This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main singularity event bus}
     * only on the {@linkplain LogicalSide#SERVER logical server}.
     **/
    record Watch(ServerPlayer getPlayer, ChunkPos getPos, LevelChunk getChunk, ServerLevel getLevel)
            implements ChunkWatchEvent {
        public static final EventBus<Watch> BUS = EventBus.create(Watch.class);

        public Watch(ServerPlayer player, LevelChunk chunk, ServerLevel level) {
            this(player, chunk.getPos(), chunk, level);
        }
    }

    /**
     * This event is fired when server sends "singularityt chunk" packet to the {@link ServerPlayer}.
     * <p>
     * This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main singularity event bus}
     * only on the {@linkplain LogicalSide#SERVER logical server}.
     **/
    record UnWatch(ServerPlayer getPlayer, ChunkPos getPos, ServerLevel getLevel) implements ChunkWatchEvent {
        public static final EventBus<UnWatch> BUS = EventBus.create(UnWatch.class);
    }
}
