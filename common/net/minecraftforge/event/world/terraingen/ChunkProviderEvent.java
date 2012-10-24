package net.minecraftforge.event.world.terraingen;

import net.minecraft.src.IChunkProvider;
import net.minecraft.src.World;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.world.WorldEvent;

public abstract class ChunkProviderEvent extends WorldEvent
{

    public final IChunkProvider chunkProvider;
    public final int chunkX;
    public final int chunkZ;
    public final byte[] terrainArray;

    public ChunkProviderEvent(IChunkProvider chunkProvider, World world, int chunkX, int chunkZ, byte[] terrainArray)
    {
        super(world);
        this.chunkProvider = chunkProvider;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.terrainArray = terrainArray;
    }

    public static class ChunkPreGenerateCavesAndRavinesEvent extends ChunkProviderEvent
    {

        public ChunkPreGenerateCavesAndRavinesEvent(IChunkProvider chunkProvider, World world, int chunkX, int chunkZ, byte[] terrainArray)
        {
            super(chunkProvider, world, chunkX, chunkZ, terrainArray);
        }

    }

    @Cancelable
    public static class ChunkGenerateCavesAndRavinesEvent extends ChunkProviderEvent
    {

        public ChunkGenerateCavesAndRavinesEvent(IChunkProvider chunkProvider, World world, int chunkX, int chunkZ, byte[] terrainArray)
        {
            super(chunkProvider, world, chunkX, chunkZ, terrainArray);
        }

    }

    public static class ChunkPostGenerateCavesAndRavinesEvent extends ChunkProviderEvent
    {

        public ChunkPostGenerateCavesAndRavinesEvent(IChunkProvider chunkProvider, World world, int chunkX, int chunkZ, byte[] terrainArray)
        {
            super(chunkProvider, world, chunkX, chunkZ, terrainArray);
        }

    }
  
}
