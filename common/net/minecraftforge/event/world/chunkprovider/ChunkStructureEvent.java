package net.minecraftforge.event.world.chunkprovider;

import java.util.Random;

import net.minecraft.src.IChunkProvider;
import net.minecraft.src.World;
import net.minecraftforge.event.Cancelable;

public class ChunkStructureEvent extends ChunkProviderEvent
{

    public final int chunkX;
    public final int chunkZ;
    public final Random rand;
        
    private boolean handled = false;

    public ChunkStructureEvent(IChunkProvider chunkProvider, World world, Random rand, int chunkX, int chunkZ)
    {
        super(chunkProvider, world);
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.rand = rand;
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
    public static class GenerateMineshaft extends ChunkStructureEvent
    {

        public GenerateMineshaft(IChunkProvider chunkProvider, World world, Random rand, int chunkX, int chunkZ)
        {
            super(chunkProvider, world, rand, chunkX, chunkZ);
        }
        
   }

    @Cancelable
    public static class GenerateVillage extends ChunkStructureEvent
    {

        public GenerateVillage(IChunkProvider chunkProvider, World world, Random rand, int chunkX, int chunkZ)
        {
            super(chunkProvider, world, rand, chunkX, chunkZ);
        }

    }

    @Cancelable
    public static class GenerateStronghold extends ChunkStructureEvent
    {

        public GenerateStronghold(IChunkProvider chunkProvider, World world, Random rand, int chunkX, int chunkZ)
        {
            super(chunkProvider, world, rand, chunkX, chunkZ);
        }

    }

    @Cancelable
    public static class GenerateScatteredFeature extends ChunkStructureEvent
    {

        public GenerateScatteredFeature(IChunkProvider chunkProvider, World world, Random rand, int chunkX, int chunkZ)
        {
            super(chunkProvider, world, rand, chunkX, chunkZ);
        }

    }

    @Cancelable
    public static class GenerateNetherBridge extends ChunkStructureEvent
    {

        public GenerateNetherBridge(IChunkProvider chunkProvider, World world, Random rand, int chunkX, int chunkZ)
        {
            super(chunkProvider, world, rand, chunkX, chunkZ);
        }

    }
    
}
