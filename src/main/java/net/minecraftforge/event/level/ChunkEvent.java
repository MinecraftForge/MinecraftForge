/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.level;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * ChunkEvent is fired when an event involving a chunk occurs.<br>
 * If a method utilizes this {@link Event} as its parameter, the method will
 * receive every child event of this class.<br>
 * <br>
 * {@link #chunk} contains the Chunk this event is affecting.<br>
 * <br>
 * All children of this event are fired on the {@link MinecraftForge#EVENT_BUS}.<br>
 **/
public class ChunkEvent extends LevelEvent
{
    private final ChunkAccess chunk;

    public ChunkEvent(ChunkAccess chunk)
    {
        super(chunk.getWorldForge());
        this.chunk = chunk;
    }

    public ChunkEvent(ChunkAccess chunk, LevelAccessor level)
    {
        super(level);
        this.chunk = chunk;
    }

    public ChunkAccess getChunk()
    {
        return chunk;
    }

    /**
     * ChunkEvent.Load is fired when vanilla Minecraft attempts to load a Chunk into the level.<br>
     * This event is fired during chunk loading in <br>
     *
     * Chunk.onChunkLoad(). <br>
     * <strong>Note:</strong> This event may be called before the underlying {@link LevelChunk} is promoted to {@link ChunkStatus#FULL}. You will cause chunk loading deadlocks if you don't delay your level interactions.<br>
     * <br>
     * This event is not {@link Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult} <br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
     **/
    public static class Load extends ChunkEvent
    {
        public Load(ChunkAccess chunk)
        {
            super(chunk);
        }
    }

    /**
     * ChunkEvent.Unload is fired when vanilla Minecraft attempts to unload a Chunk from the level.<br>
     * This event is fired during chunk unloading in <br>
     * Chunk.onChunkUnload(). <br>
     * <br>
     * This event is not {@link Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult} <br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
     **/
    public static class Unload extends ChunkEvent
    {
        public Unload(ChunkAccess chunk)
        {
            super(chunk);
        }
    }
}
