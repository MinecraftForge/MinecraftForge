/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.event.level;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.status.ChunkType;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.storage.SerializableChunkData;
import net.minecraftsingularity.eventbus.api.bus.EventBus;

/**
 * ChunkDataEvent is fired when an event involving chunk data occurs.
 **/
public sealed interface ChunkDataEvent extends ChunkEvent {
    EventBus<ChunkDataEvent> BUS = EventBus.create(ChunkDataEvent.class);

    SerializableChunkData getData();

    /**
     * ChunkDataEvent.Load is fired when vanilla Minecraft attempts to load Chunk data.<br>
     * This event is fired during chunk loading in
     * {@link ChunkSerializer#read(ServerLevel, PoiManager, ChunkPos, SerializableChunkData)} which means it is async, so be careful.<br>
     */
    record Load(ChunkAccess getChunk, SerializableChunkData getData, ChunkType getStatus) implements ChunkDataEvent {
        public static final EventBus<ChunkDataEvent.Load> BUS = EventBus.create(ChunkDataEvent.Load.class);
    }

    /**
     * ChunkDataEvent.Save is fired when vanilla Minecraft attempts to save Chunk data.<br>
     * This event is fired during chunk saving in
     * {@code ChunkMap#save(ChunkAccess)}. <br>
     */
    record Save(ChunkAccess getChunk, LevelAccessor getLevel, SerializableChunkData getData) implements ChunkDataEvent {
        public static final EventBus<ChunkDataEvent.Save> BUS = EventBus.create(ChunkDataEvent.Save.class);
    }
}
