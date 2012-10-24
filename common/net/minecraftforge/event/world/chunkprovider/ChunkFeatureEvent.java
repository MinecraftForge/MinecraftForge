package net.minecraftforge.event.world.chunkprovider;

import net.minecraft.src.IChunkProvider;
import net.minecraft.src.World;

public class ChunkFeatureEvent extends ChunkProviderEvent
{

    public final int chunkX;
    public final int chunkZ;
    
    /** The 16x16x128 array of blockIDs in the chunk */
    public final byte[] chunkBlocks;
        
    private boolean handled = false;

    public ChunkFeatureEvent(IChunkProvider chunkProvider, World world, int chunkX, int chunkZ, byte[] chunkBlocks)
    {
        super(chunkProvider, world);
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.chunkBlocks = chunkBlocks;
     }

    public boolean isHandled()
    {
        return handled;
    }
    
    public void setHandled()
    {
        handled = true;
    }

    public static class GenerateCave extends ChunkFeatureEvent
    {

        public GenerateCave(IChunkProvider chunkProvider, World world, int chunkX, int chunkZ, byte[] chunkBlocks)
        {
            super(chunkProvider, world, chunkX, chunkZ, chunkBlocks);
        }

    }
    
}
