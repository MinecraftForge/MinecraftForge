package net.minecraftforge.event.world;

import java.util.Random;

import net.minecraft.src.*;

public class PopulateChunkEvent extends ChunkProviderEvent
{
    public final World world;
    public final Random rand;
    public final int chunkX;
    public final int chunkZ;
    public final boolean hasVillageGenerated;
    
    public PopulateChunkEvent(IChunkProvider chunkProvider, World world, Random rand, int chunkX, int chunkZ, boolean hasVillageGenerated)
    {
        super(chunkProvider);
        this.world = world;
        this.rand = rand;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.hasVillageGenerated = hasVillageGenerated;
    }
    
    public static class Pre extends PopulateChunkEvent
    {
        public Pre(IChunkProvider chunkProvider, World world, Random rand, int chunkX, int chunkZ, boolean hasVillageGenerated)
        {
            super(chunkProvider, world, rand, chunkX, chunkZ, hasVillageGenerated);
        }
    }
    
    public static class Post extends PopulateChunkEvent
    {
        public Post(IChunkProvider chunkProvider, World world, Random rand, int chunkX, int chunkZ, boolean hasVillageGenerated)
        {
            super(chunkProvider, world, rand, chunkX, chunkZ, hasVillageGenerated);
        }
    }
    
    @HasResult
    public static class GenLake extends PopulateChunkEvent
    {
        public Block fluid;

        public GenLake(IChunkProvider chunkProvider, World world, Random rand, int chunkX, int chunkZ, boolean hasVillageGenerated, Block fluid)
        {
            super(chunkProvider, world, rand, chunkX, chunkZ, hasVillageGenerated);
            this.fluid = fluid;
        }
    }
    
    @HasResult
    public static class GenDungeon extends PopulateChunkEvent
    {
        public GenDungeon(IChunkProvider chunkProvider, World world, Random rand, int chunkX, int chunkZ, boolean hasVillageGenerated)
        {
            super(chunkProvider, world, rand, chunkX, chunkZ, hasVillageGenerated);
        }
    }
    
    @HasResult
    public static class GenIceAndSnow extends PopulateChunkEvent
    {
        public GenIceAndSnow(IChunkProvider chunkProvider, World world, Random rand, int chunkX, int chunkZ, boolean hasVillageGenerated)
        {
            super(chunkProvider, world, rand, chunkX, chunkZ, hasVillageGenerated);
        }
    }
}
