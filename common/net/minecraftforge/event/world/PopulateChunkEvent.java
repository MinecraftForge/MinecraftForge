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
    public static class Lake extends PopulateChunkEvent
    {
        public Block fluid;

        public Lake(IChunkProvider chunkProvider, World world, Random rand, int chunkX, int chunkZ, boolean hasVillageGenerated, Block fluid)
        {
            super(chunkProvider, world, rand, chunkX, chunkZ, hasVillageGenerated);
            this.fluid = fluid;
        }
    }
    
    @HasResult
    public static class Dungeon extends PopulateChunkEvent
    {
        public Dungeon(IChunkProvider chunkProvider, World world, Random rand, int chunkX, int chunkZ, boolean hasVillageGenerated)
        {
            super(chunkProvider, world, rand, chunkX, chunkZ, hasVillageGenerated);
        }
    }
    
    @HasResult
    public static class IceAndSnow extends PopulateChunkEvent
    {
        public IceAndSnow(IChunkProvider chunkProvider, World world, Random rand, int chunkX, int chunkZ, boolean hasVillageGenerated)
        {
            super(chunkProvider, world, rand, chunkX, chunkZ, hasVillageGenerated);
        }
    }
    
    @HasResult
    public static class Fire extends PopulateChunkEvent
    {
        public Fire(IChunkProvider chunkProvider, World world, Random rand, int chunkX, int chunkZ, boolean hasVillageGenerated)
        {
            super(chunkProvider, world, rand, chunkX, chunkZ, hasVillageGenerated);
        }
    }
    
    @HasResult
    public static class GlowStone extends PopulateChunkEvent
    {
        public GlowStone(IChunkProvider chunkProvider, World world, Random rand, int chunkX, int chunkZ, boolean hasVillageGenerated)
        {
            super(chunkProvider, world, rand, chunkX, chunkZ, hasVillageGenerated);
        }
    }
}
