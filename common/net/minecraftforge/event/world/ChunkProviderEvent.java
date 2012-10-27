package net.minecraftforge.event.world;

import net.minecraft.src.*;
import net.minecraftforge.event.*;

public class ChunkProviderEvent extends Event
{

    public final IChunkProvider chunkProvider;
    
    public ChunkProviderEvent(IChunkProvider chunkProvider)
    {
        this.chunkProvider = chunkProvider;
    }
    
    @HasResult
    public static class ReplaceBiomeBlocks extends ChunkProviderEvent 
    {
        public final int chunkX;
        public final int chunkZ;
        public final byte[] blockArray;
        public final BiomeGenBase[] biomeArray;
        
        public ReplaceBiomeBlocks(IChunkProvider chunkProvider, int chunkX, int chunkZ, byte[] blockArray, BiomeGenBase[] biomeArray)
        {
            super(chunkProvider);
            this.chunkX = chunkX;
            this.chunkZ = chunkZ;
            this.blockArray = blockArray;
            this.biomeArray = biomeArray;
        }
       
    }
}
