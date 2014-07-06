package net.minecraftforge.event.terraingen;

import java.util.Random;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.Event.HasResult;
import net.minecraft.world.World;

/**DecorateBiomeEvent is fired when a BiomeDecorator is created.
 * <br>
 * This event is fired whenever a BiomeDecorator is created in
 * DeferredBiomeDecorator#fireCreateEventAndReplace(BiomeGenBase).<br>
 * <br>
 * {@link #world} contains the world that is being decorated. <br>
 * {@link #rand} contains an instane of Random to be used. <br>
 * {@link #chunkX} contains the x-coordinate of the Chunk being decorated. <br>
 * {@link #chunkZ} contains the z-coordinate of the Chunk being decorated. <br>
 * <br>
 * This event is not {@link Cancelable}.
 * <br>
 * This event does not have a result. {@link HasResult}
 * <br>
 * This event is fired on the {@link MinecraftForge#TERRAIN_GEN_BUS}.
 **/
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
    
    /**
     * This event is fired before a chunk is decorated with a biome feature.
     */
    public static class Pre extends DecorateBiomeEvent
    {
        public Pre(World world, Random rand, int worldX, int worldZ)
        {
            super(world, rand, worldX, worldZ);
        }
    }
    
    /**
     * This event is fired after a chunk is decorated with a biome feature.
     */
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
