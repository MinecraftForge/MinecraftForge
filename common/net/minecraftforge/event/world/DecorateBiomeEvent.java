package net.minecraftforge.event.world;

import java.util.Random;

import net.minecraft.src.*;
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

    @HasResult
    public static class BigMushrooms extends DecorateBiomeEvent
    {
        public BigMushrooms(World world, Random rand, int worldX, int worldZ)
        {
            super(world, rand, worldX, worldZ);
        }
    }

    @HasResult
    public static class SandPass2 extends DecorateBiomeEvent
    {
        public SandPass2(World world, Random rand, int worldX, int worldZ)
        {
            super(world, rand, worldX, worldZ);
        }
    }

    @HasResult
    public static class Sand extends DecorateBiomeEvent
    {
        public Sand(World world, Random rand, int worldX, int worldZ)
        {
            super(world, rand, worldX, worldZ);
        }
    }

    @HasResult
    public static class Reed extends DecorateBiomeEvent
    {
        public Reed(World world, Random rand, int worldX, int worldZ)
        {
            super(world, rand, worldX, worldZ);
        }
    }

    @HasResult
    public static class Pumpkin extends DecorateBiomeEvent
    {
        public Pumpkin(World world, Random rand, int worldX, int worldZ)
        {
            super(world, rand, worldX, worldZ);
        }
    }

    @HasResult
    public static class Cactus extends DecorateBiomeEvent
    {

        public Cactus(World world, Random rand, int worldX, int worldZ)
        {
            super(world, rand, worldX, worldZ);
            // TODO Auto-generated constructor stub
        }
    }

    @HasResult
    public static class Flowers extends DecorateBiomeEvent
    {
        public Flowers(World world, Random rand, int worldX, int worldZ)
        {
            super(world, rand, worldX, worldZ);
        }
    }

    @HasResult
    public static class Lake extends DecorateBiomeEvent
    {
        public Lake(World world, Random rand, int worldX, int worldZ)
        {
            super(world, rand, worldX, worldZ);
        }
    }

    @HasResult
    public static class Grass extends DecorateBiomeEvent
    {
        public Grass(World world, Random rand, int worldX, int worldZ)
        {
            super(world, rand, worldX, worldZ);
        }
    }

    @HasResult
    public static class DeadBushes extends DecorateBiomeEvent
    {
        public DeadBushes(World world, Random rand, int worldX, int worldZ)
        {
            super(world, rand, worldX, worldZ);
        }
    }

    @HasResult
    public static class LilyPads extends DecorateBiomeEvent
    {
        public LilyPads(World world, Random rand, int worldX, int worldZ)
        {
            super(world, rand, worldX, worldZ);
        }
    }

    @HasResult
    public static class Clay extends DecorateBiomeEvent
    {
        public Clay(World world, Random rand, int worldX, int worldZ)
        {
            super(world, rand, worldX, worldZ);
        }
    }

    @HasResult
    public static class Mushrooms extends DecorateBiomeEvent
    {
        public Mushrooms(World world, Random rand, int worldX, int worldZ)
        {
            super(world, rand, worldX, worldZ);
        }
    }

    @HasResult
    public static class Trees extends DecorateBiomeEvent
    {
        public Trees(World world, Random rand, int worldX, int worldZ)
        {
            super(world, rand, worldX, worldZ);
        }
    }
}
