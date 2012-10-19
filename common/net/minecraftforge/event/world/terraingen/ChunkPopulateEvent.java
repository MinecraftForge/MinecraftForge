package net.minecraftforge.event.world.terraingen;

import net.minecraft.src.IChunkProvider;
import net.minecraft.src.World;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.world.WorldEvent;

public abstract class ChunkPopulateEvent extends WorldEvent
{

    public final IChunkProvider chunkProvider;
    public final int chunkX;
    public final int chunkZ;

    public ChunkPopulateEvent(IChunkProvider chunkProvider, World world, int chunkX, int chunkZ)
    {
        super(world);
        this.chunkProvider = chunkProvider;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
    }

    public static class ChunkPreGenerateLakesAndDungeonsEvent extends ChunkPopulateEvent
    {

        public ChunkPreGenerateLakesAndDungeonsEvent(IChunkProvider chunkProvider, World world, int chunkX, int chunkZ)
        {
            super(chunkProvider, world, chunkX, chunkZ);
         }
    }

    @Cancelable
    public static class ChunkGenerateLakesAndDungeonsEvent extends ChunkPopulateEvent
    {

        public ChunkGenerateLakesAndDungeonsEvent(IChunkProvider chunkProvider, World world, int chunkX, int chunkZ)
        {
            super(chunkProvider, world, chunkX, chunkZ);
        }
    }

    public static class ChunkPreBiomeDecorateEvent extends ChunkPopulateEvent
    {

        public ChunkPreBiomeDecorateEvent(IChunkProvider chunkProvider, World world, int chunkX, int chunkZ)
        {
            super(chunkProvider, world, chunkX, chunkZ);
         }
    }

    public static class ChunkPreGenerateSnowAndIceEvent extends ChunkPopulateEvent
    {

        public ChunkPreGenerateSnowAndIceEvent(IChunkProvider chunkProvider, World world, int chunkX, int chunkZ)
        {
            super(chunkProvider, world, chunkX, chunkZ);
         }
    }

    @Cancelable
    public static class ChunkGenerateSnowAndIceEvent extends ChunkPopulateEvent
    {

        public ChunkGenerateSnowAndIceEvent(IChunkProvider chunkProvider, World world, int chunkX, int chunkZ)
        {
            super(chunkProvider, world, chunkX, chunkZ);
        }
    }

    public static class ChunkPostGenerateSnowAndIceEvent extends ChunkPopulateEvent
    {

        public ChunkPostGenerateSnowAndIceEvent(IChunkProvider chunkProvider, World world, int chunkX, int chunkZ)
        {
            super(chunkProvider, world, chunkX, chunkZ);
         }
    }

    public static class ChunkPreNetherPopulateEvent extends ChunkPopulateEvent
    {

        public ChunkPreNetherPopulateEvent(IChunkProvider chunkProvider, World world, int chunkX, int chunkZ)
        {
            super(chunkProvider, world, chunkX, chunkZ);
         }
    }

    @Cancelable
    public static class ChunkNetherPopulateEvent extends ChunkPopulateEvent
    {

        public ChunkNetherPopulateEvent(IChunkProvider chunkProvider, World world, int chunkX, int chunkZ)
        {
            super(chunkProvider, world, chunkX, chunkZ);
        }
    }

    public static class ChunkPostNetherPopulateEvent extends ChunkPopulateEvent
    {

        public ChunkPostNetherPopulateEvent(IChunkProvider chunkProvider, World world, int chunkX, int chunkZ)
        {
            super(chunkProvider, world, chunkX, chunkZ);
         }
    }
  
}
