package net.minecraftforge.event.world.chunkprovider;

import net.minecraft.src.BiomeGenBase;
import net.minecraft.src.IChunkProvider;
import net.minecraft.src.World;

public class ReplaceBlocksForBiomeEvent extends ChunkProviderEvent
{

    /** The biomes that are used to generate the chunk */
    public final BiomeGenBase[] biomesForGeneration;
    public final int chunkX;
    public final int chunkZ;
    
    /** The 16x16x128 array of blockIDs in the chunk */
    public final byte[] chunkBlocks;
        
    private boolean handled = false;

    public ReplaceBlocksForBiomeEvent(IChunkProvider chunkProvider, World world, int chunkX, int chunkZ, byte[] chunkBlocks, BiomeGenBase[] biomesForGeneration)
    {
        super(chunkProvider, world);
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.chunkBlocks = chunkBlocks;
        this.biomesForGeneration = biomesForGeneration;
    }

    public boolean isHandeled()
    {
        return handled;
    }
    
    public void setHandeled()
    {
        handled = true;
    }

}
