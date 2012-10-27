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
    
    @HasResult
    public static class InitNoiseField extends ChunkProviderEvent 
    {
        public double[] noisefield;
        public final int posX;
        public final int posY;
        public final int posZ;
        public final int sizeX;
        public final int sizeY;
        public final int sizeZ;
        
        public InitNoiseField(IChunkProvider chunkProvider, double[] noisefield, int posX, int posY, int posZ, int sizeX, int sizeY, int sizeZ)
        {
            super(chunkProvider);
            this.noisefield = noisefield;
            this.posX = posX;
            this.posY = posY;
            this.posZ = posZ;
            this.sizeX = sizeX;
            this.sizeY = sizeX;
            this.sizeZ = sizeZ;
        }
       
    }
}
