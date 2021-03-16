/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.event.world;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * ChunkDataEvent is fired when an event involving chunk data occurs.<br>
 * If a method utilizes this {@link Event} as its parameter, the method will
 * receive every child event of this class.<br>
 * <br>
 * {@link #data} contains the NBTTagCompound containing the chunk data for this event.<br>
 * <br>
 * All children of this event are fired on the {@link MinecraftForge#EVENT_BUS}.<br>
 **/
public class ChunkDataEvent extends ChunkEvent
{
    private final CompoundNBT data;

    public ChunkDataEvent(IChunk chunk, CompoundNBT data)
    {
        super(chunk);
        this.data = data;
    }

    public ChunkDataEvent(IChunk chunk, IWorld world, CompoundNBT data)
    {
        super(chunk, world);
        this.data = data;
    }

    public CompoundNBT getData()
    {
        return data;
    }

    /**
     * ChunkDataEvent.Load is fired when vanilla Minecraft attempts to load Chunk data.<br>
     * This event is fired during chunk loading in
     * {@link net.minecraft.world.chunk.storage.ChunkSerializer.read(ServerWorld, TemplateManager, PointOfInterestManager, ChunkPos, CompoundNBT)} which means it is async, so be careful.<br>
     * <br>
     * This event is not {@link Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult} <br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
     **/
    public static class Load extends ChunkDataEvent
    {
        private ChunkStatus.Type status;

        public Load(IChunk chunk, CompoundNBT data, ChunkStatus.Type status)
        {
            super(chunk, data);
            this.status = status;
        }

        public ChunkStatus.Type getStatus()
        {
            return this.status;
        }
    }

    /**
     * ChunkDataEvent.Save is fired when vanilla Minecraft attempts to save Chunk data.<br>
     * This event is fired during chunk saving in
     * {@link AnvilChunkLoader#saveChunk(World, Chunk)}. <br>
     * <br>
     * This event is not {@link net.minecraftforge.eventbus.api.Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult} <br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
     **/
    public static class Save extends ChunkDataEvent
    {
        public Save(IChunk chunk, IWorld world, CompoundNBT data)
        {
            super(chunk, world, data);
        }
    }
}
