package net.minecraftforge.event.terraingen;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.event.world.*;

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
    
    /**
     * This event is fired when a chunk is populated with a terrain feature.
     * 
     * You can set the result to DENY to prevent the default generation
     * of a terrain feature.
     */
    @HasResult
    public static class Populate extends PopulateChunkEvent
    {
        /** Use CUSTOM to filter custom event types
         */
        public static enum EventType { DUNGEON, FIRE, GLOWSTONE, ICE, LAKE, LAVA, NETHER_LAVA, CUSTOM }
        
        public final EventType type;

        public Populate(IChunkProvider chunkProvider, World world, Random rand, int chunkX, int chunkZ, boolean hasVillageGenerated, EventType type)
        {
            super(chunkProvider, world, rand, chunkX, chunkZ, hasVillageGenerated);
            this.type = type;
        }
    }
}
