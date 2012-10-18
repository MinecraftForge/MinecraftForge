package net.minecraftforge.event.world.terraingen;

import java.util.Random;

import net.minecraft.src.World;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.world.WorldEvent;

public abstract class WorldGenEvent extends WorldEvent
{
    
    public final Random rand;
    public final int worldX;
    public final int worldZ;
    public int iterations;
    
    private boolean handled = false;

    public WorldGenEvent(World world, Random rand, int worldX, int worldZ, int iterations)
    {
        super(world);
        
        this.rand = rand;
        this.worldX = worldX;
        this.worldZ = worldZ;
        this.iterations = iterations;
    }

    public boolean isHandled()
    {
        return handled;
    }
    
    public void setHandled()
    {
        handled = true;
    }

    @Cancelable
    public static class WorldGenDungeonEvent extends WorldGenEvent
    {

        public WorldGenDungeonEvent(World world, Random rand, int worldX, int worldZ, int iterations)
        {
            super(world, rand, worldX, worldZ, iterations);
        }
        
    }
    
}
