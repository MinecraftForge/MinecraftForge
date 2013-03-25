package net.minecraftforge.event.terraingen;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.event.*;

public class OreGenEvent extends Event
{
    public final World world;
    public final Random rand;
    public final int worldX;
    public final int worldZ;
    
    public OreGenEvent(World world, Random rand, int worldX, int worldZ)
    {
        this.world = world;
        this.rand = rand;
        this.worldX = worldX;
        this.worldZ = worldZ;
    }
    
    public static class Pre extends OreGenEvent
    {
        public Pre(World world, Random rand, int worldX, int worldZ)
        {
            super(world, rand, worldX, worldZ);
        }
    }
    
    public static class Post extends OreGenEvent
    {
        public Post(World world, Random rand, int worldX, int worldZ)
        {
            super(world, rand, worldX, worldZ);
        }
    }
    
    /**
     * This event is fired when an ore is generated in a chunk.
     * 
     * You can set the result to DENY to prevent the default ore generation.
     */
    @HasResult
    public static class GenerateMinable extends OreGenEvent
    {
        public static enum EventType { COAL, DIAMOND, DIRT, GOLD, GRAVEL, IRON, LAPIS, REDSTONE, CUSTOM }
        
        public final EventType type;
        public final WorldGenerator generator;
        
        public GenerateMinable(World world, Random rand, WorldGenerator generator, int worldX, int worldZ, EventType type)
        {
            super(world, rand, worldX, worldZ);
            this.generator = generator;
            this.type = type;
        }
    }
}
