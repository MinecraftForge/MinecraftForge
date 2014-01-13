package net.minecraftforge.common.chunkio;

import net.minecraftforge.common.util.AsynchronousExecutor;

public class ChunkIOExecutor {
    static final int BASE_THREADS = 1;
    static final int PLAYERS_PER_THREAD = 50;

    private static final AsynchronousExecutor<QueuedChunk, net.minecraft.world.chunk.Chunk, Runnable, RuntimeException> instance = new AsynchronousExecutor<QueuedChunk, net.minecraft.world.chunk.Chunk, Runnable, RuntimeException>(new ChunkIOProvider(), BASE_THREADS);

    public static net.minecraft.world.chunk.Chunk syncChunkLoad(net.minecraft.world.World world, net.minecraft.world.chunk.storage.AnvilChunkLoader loader, net.minecraft.world.gen.ChunkProviderServer provider, int x, int z) {
        return instance.getSkipQueue(new QueuedChunk(x, z, loader, world, provider));
    }

    public static void queueChunkLoad(net.minecraft.world.World world, net.minecraft.world.chunk.storage.AnvilChunkLoader loader, net.minecraft.world.gen.ChunkProviderServer provider, int x, int z, Runnable runnable) {
        instance.add(new QueuedChunk(x, z, loader, world, provider), runnable);
    }

    // Abuses the fact that hashCode and equals for QueuedChunk only use world and coords
    public static void dropQueuedChunkLoad(net.minecraft.world.World world, int x, int z, Runnable runnable) {
        instance.drop(new QueuedChunk(x, z, null, world, null), runnable);
    }

    public static void adjustPoolSize(int players) {
        int size = Math.max(BASE_THREADS, (int) Math.ceil(players / PLAYERS_PER_THREAD));
        instance.setActiveThreads(size);
    }

    public static void tick() {
        instance.finishActive();
    }
}