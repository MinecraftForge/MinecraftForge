/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

package net.minecraftforge.common.chunkio;

import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkDataEvent;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

class ChunkIOProvider implements Runnable
{
    private final QueuedChunk chunkInfo;
    private final AnvilChunkLoader loader;
    private final ChunkProviderServer provider;

    private Chunk chunk;
    private NBTTagCompound nbt;
    private final ConcurrentLinkedQueue<Runnable> callbacks = new ConcurrentLinkedQueue<Runnable>();
    private boolean ran = false;

    ChunkIOProvider(QueuedChunk chunk, AnvilChunkLoader loader, ChunkProviderServer provider)
    {
        this.chunkInfo = chunk;
        this.loader = loader;
        this.provider = provider;
    }

    public void addCallback(Runnable callback)
    {
        this.callbacks.add(callback);
    }
    public void removeCallback(Runnable callback)
    {
        this.callbacks.remove(callback);
    }

    @Override
    public void run() // async stuff
    {
        synchronized(this)
        {
            try
            {
                Object[] data = null;
                try
                {
                    data = this.loader.loadChunk__Async(chunkInfo.world, chunkInfo.x, chunkInfo.z);
                }
                catch (IOException e)
                {
                    throw new RuntimeException(e); // Allow exception to bubble up to afterExecute
                }
    
                if (data != null)
                {
                    this.nbt   = (NBTTagCompound)data[1];
                    this.chunk = (Chunk)data[0];
                }
            }
            finally 
            {
                this.ran = true;
                this.notifyAll();
            }
        }
    }

    // sync stuff
    public void syncCallback()
    {
        if (chunk == null)
        {
            this.runCallbacks();
            return;
        }

        // Load Entities
        this.loader.loadEntities(this.chunkInfo.world, this.nbt.getCompoundTag("Level"), this.chunk);

        MinecraftForge.EVENT_BUS.post(new ChunkDataEvent.Load(this.chunk, this.nbt)); // Don't call ChunkDataEvent.Load async

        this.chunk.setLastSaveTime(provider.world.getTotalWorldTime());
        this.provider.chunkGenerator.recreateStructures(this.chunk, this.chunkInfo.x, this.chunkInfo.z);

        provider.id2ChunkMap.put(ChunkPos.asLong(this.chunkInfo.x, this.chunkInfo.z), this.chunk);
        this.chunk.onLoad();
        this.chunk.populate(provider, provider.chunkGenerator);

        this.runCallbacks();
    }

    public Chunk getChunk()
    {
        return this.chunk;
    }

    public boolean runFinished()
    {
        return this.ran;
    }

    public boolean hasCallback()
    {
        return this.callbacks.size() > 0;
    }

    public void runCallbacks()
    {
        for (Runnable r : this.callbacks)
        {
            r.run();
        }

        this.callbacks.clear();
    }

    public QueuedChunk getChunkInfo() 
    {
    	return chunkInfo;
    }
}
