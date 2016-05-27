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
    private QueuedChunk chunkInfo;
    private Chunk chunk;
    private NBTTagCompound nbt;
    private ConcurrentLinkedQueue<Runnable> callbacks = new ConcurrentLinkedQueue<Runnable>();
    private boolean ran = false;

    ChunkIOProvider(QueuedChunk chunk)
    {
        this.chunkInfo = chunk;
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
            AnvilChunkLoader loader = chunkInfo.loader;
            Object[] data = null;
            try
            {
                data = loader.loadChunk__Async(chunkInfo.world, chunkInfo.x, chunkInfo.z);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            if (data != null)
            {
                this.nbt   = (NBTTagCompound)data[1];
                this.chunk = (Chunk)data[0];
            }

            this.ran = true;
            this.notifyAll();
        }
    }

    // sync stuff
    public void syncCallback()
    {
        ChunkProviderServer provider = this.chunkInfo.provider;
        if (chunk == null)
        {
            // If the chunk loading failed just do it synchronously (may generate)
            this.chunk = provider.originalLoadChunk(this.chunkInfo.x, this.chunkInfo.z);
            this.runCallbacks();
            return;
        }

        // Load Entities
        this.chunkInfo.loader.loadEntities(this.chunkInfo.world, this.nbt.getCompoundTag("Level"), this.chunk);

        MinecraftForge.EVENT_BUS.post(new ChunkDataEvent.Load(this.chunk, this.nbt)); // Don't call ChunkDataEvent.Load async

        this.chunk.setLastSaveTime(provider.worldObj.getTotalWorldTime());
        provider.id2ChunkMap.put(ChunkPos.chunkXZ2Int(this.chunkInfo.x, this.chunkInfo.z), this.chunk);
        this.chunk.onChunkLoad();

        if (provider.chunkGenerator != null)
        {
            provider.chunkGenerator.recreateStructures(this.chunk, this.chunkInfo.x, this.chunkInfo.z);
        }

        this.chunk.populateChunk(provider, provider.chunkGenerator);
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
}