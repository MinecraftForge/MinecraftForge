package net.minecraftforge.event.terraingen;

import java.util.Random;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.Event.HasResult;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

/**DecorateBiomeEvent is fired when a BiomeDecorator is created.
 * <br>
 * This event is fired whenever a BiomeDecorator is created in
 * DeferredBiomeDecorator#fireCreateEventAndReplace(BiomeGenBase).<br>
 * <br>
 * {@link #world} contains the world that is being decorated. <br>
 * {@link #rand} contains an instance of Random to be used. <br>
 * {@link #pos} contains the coordinates of the Chunk being decorated. <br>
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
    public final BlockPos pos;

    public DecorateBiomeEvent(World world, Random rand, BlockPos pos)
    {
        this.world = world;
        this.rand = rand;
        this.pos = pos;
    }

    /**
     * This event is fired before a chunk is decorated with a biome feature.
     */
    public static class Pre extends DecorateBiomeEvent
    {
        public Pre(World world, Random rand, BlockPos pos)
        {
            super(world, rand, pos);
        }
    }

    /**
     * This event is fired after a chunk is decorated with a biome feature.
     */
    public static class Post extends DecorateBiomeEvent
    {
        public Post(World world, Random rand, BlockPos pos)
        {
            super(world, rand, pos);
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
        public static enum EventType { BIG_SHROOM, CACTUS, CLAY, DEAD_BUSH, LILYPAD, FLOWERS, GRASS, LAKE_WATER, LAKE_LAVA, PUMPKIN, REED, SAND, SAND_PASS2, SHROOM, TREE, CUSTOM }

        public final EventType type;

        public Decorate(World world, Random rand, BlockPos pos, EventType type)
        {
            super(world, rand, pos);
            this.type = type;
        }
    }
}
