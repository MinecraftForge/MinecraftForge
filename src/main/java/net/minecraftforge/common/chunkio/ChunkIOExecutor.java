package net.minecraftforge.common.chunkio;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.collect.Maps;

import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.fml.common.FMLLog;

public class ChunkIOExecutor
{
    static final int BASE_THREADS = 1;
    static final int PLAYERS_PER_THREAD = 50;

    private static final Map<QueuedChunk, ChunkIOProvider> tasks = Maps.newConcurrentMap();
    private static final ThreadPoolExecutor pool = new ThreadPoolExecutor(BASE_THREADS, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS,
        new LinkedBlockingQueue<Runnable>(),
        new ThreadFactory()
        {
            private AtomicInteger count = new AtomicInteger(1);
            @Override
            public Thread newThread(Runnable r)
            {
                Thread thread = new Thread(r, "Chunk I/O Executor Thread-" + count.getAndIncrement());
                thread.setDaemon(true);
                return thread;
            }
        }
    );

    //Load the chunk completely in this thread. Dequeue as needed...
    public static Chunk syncChunkLoad(World world, AnvilChunkLoader loader, ChunkProviderServer provider, int x, int z)
    {
        QueuedChunk key = new QueuedChunk(x, z, loader, world, provider);
        ChunkIOProvider task = tasks.get(key);
        if (task != null)
        {
            if (!pool.remove(task)) // If it wasn't in the pool, and run hasn't finished, then wait for the async thread.
            {
                synchronized(task)
                {
                    while (!task.runFinished())
                    {
                        try
                        {
                            task.wait();
                        }
                        catch (InterruptedException e)
                        {
                            e.printStackTrace(); // Something happened? Log it?
                        }
                    }
                }
            }
        }
        else
        {
            task = new ChunkIOProvider(key);
            task.run();
        }
        task.syncCallback();
        return task.getChunk();
    }

    //Queue the chunk to be loaded, and call the runnable when finished
    public static void queueChunkLoad(World world, AnvilChunkLoader loader, ChunkProviderServer provider, int x, int z, Runnable runnable)
    {
        QueuedChunk key = new QueuedChunk(x, z, loader, world, provider);
        ChunkIOProvider task = tasks.get(key);
        if (task == null)
        {
            task = new ChunkIOProvider(key);
            task.addCallback(runnable); // Add before calling execute for thread safety
            tasks.put(key, task);
            pool.execute(task);
        }
        else
        {
            task.addCallback(runnable);
        }
    }

    // Abuses the fact that hashCode and equals for QueuedChunk only use world and coords
    // Remove the chunk from the queue if it's in the list.
    public static void dropQueuedChunkLoad(World world, int x, int z, Runnable runnable)
    {
        QueuedChunk key = new QueuedChunk(x, z, null, world, null);
        ChunkIOProvider task = tasks.get(key);
        if (task == null)
        {
            FMLLog.warning("Attempted to dequeue chunk that wasn't queued? %d @ (%d, %d)", world.provider.getDimension(), x, z);
            return;
        }

        task.removeCallback(runnable);

        if (!task.hasCallback())
        {
            tasks.remove(key);
            pool.remove(task);
        }
    }

    public static void adjustPoolSize(int players)
    {
        int size = Math.max(BASE_THREADS, (int) Math.ceil(players / PLAYERS_PER_THREAD));
        pool.setCorePoolSize(size);
    }

    public static void tick()
    {
        Iterator<ChunkIOProvider> itr = tasks.values().iterator();
        while (itr.hasNext())
        {
            ChunkIOProvider task = itr.next();
            if (task.runFinished() && task.hasCallback())
            {
                task.syncCallback();
            }
            itr.remove();
        }
    }
}