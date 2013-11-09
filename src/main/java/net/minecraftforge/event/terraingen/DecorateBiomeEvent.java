package net.minecraftforge.event.terraingen;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraftforge.event.*;

public class DecorateBiomeEvent extends Event
{
    public final World world;
    public final Random rand;
    public final int chunkX;
    public final int chunkZ;
    
    public DecorateBiomeEvent(World world, Random rand, int worldX, int worldZ)
    {
        this.world = world;
        this.rand = rand;
        this.chunkX = worldX;
        this.chunkZ = worldZ;
    }
    
    public static class Pre extends DecorateBiomeEvent
    {
        public Pre(World world, Random rand, int worldX, int worldZ)
        {
            super(world, rand, worldX, worldZ);
        }
    }
    
    public static class Post extends DecorateBiomeEvent
    {
        public Post(World world, Random rand, int worldX, int worldZ)
        {
            super(world, rand, worldX, worldZ);
        }
    }

    /**
     * This event is fired when a chunk is decorated with a biome feature.
     * 
     * You can set the result to DENY to prevent the default biome decoration.
     */
    @HasResult
    public static class Decorate extends DecorateBiomeEvent
    {
        /** Use CUSTOM to filter custom event types
         */
        public static enum EventType { BIG_SHROOM, CACTUS, CLAY, DEAD_BUSH, LILYPAD, FLOWERS, GRASS, LAKE, PUMPKIN, REED, SAND, SAND_PASS2, SHROOM, TREE, CUSTOM }
        
        public final EventType type;
        
        public Decorate(World world, Random rand, int worldX, int worldZ, EventType type)
        {
            super(world, rand, worldX, worldZ);
            this.type = type;
        }
    }
}
