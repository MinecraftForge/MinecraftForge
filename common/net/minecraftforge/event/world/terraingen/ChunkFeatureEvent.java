package net.minecraftforge.event.world.terraingen;

import net.minecraft.src.IChunkProvider;
import net.minecraft.src.World;
import net.minecraftforge.event.Cancelable;

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

    @Cancelable
    public static class GenerateCave extends ChunkFeatureEvent
    {

        public GenerateCave(IChunkProvider chunkProvider, World world, int chunkX, int chunkZ, byte[] chunkBlocks)
        {
            super(chunkProvider, world, chunkX, chunkZ, chunkBlocks);
        }

    }

    @Cancelable
    public static class GenerateRavine extends ChunkFeatureEvent
    {

        public GenerateRavine(IChunkProvider chunkProvider, World world, int chunkX, int chunkZ, byte[] chunkBlocks)
        {
            super(chunkProvider, world, chunkX, chunkZ, chunkBlocks);
        }

    }

    @Cancelable
    public static class SetMineshaftLocation extends ChunkFeatureEvent
    {

        public SetMineshaftLocation(IChunkProvider chunkProvider, World world, int chunkX, int chunkZ, byte[] chunkBlocks)
        {
            super(chunkProvider, world, chunkX, chunkZ, chunkBlocks);
        }

    }

    @Cancelable
    public static class SetVillageLocation extends ChunkFeatureEvent
    {

        public SetVillageLocation(IChunkProvider chunkProvider, World world, int chunkX, int chunkZ, byte[] chunkBlocks)
        {
            super(chunkProvider, world, chunkX, chunkZ, chunkBlocks);
        }

    }

    @Cancelable
    public static class SetStrongholdLocation extends ChunkFeatureEvent
    {

        public SetStrongholdLocation(IChunkProvider chunkProvider, World world, int chunkX, int chunkZ, byte[] chunkBlocks)
        {
            super(chunkProvider, world, chunkX, chunkZ, chunkBlocks);
        }

    }

    @Cancelable
    public static class SetScatteredFeatureLocation extends ChunkFeatureEvent
    {

        public SetScatteredFeatureLocation(IChunkProvider chunkProvider, World world, int chunkX, int chunkZ, byte[] chunkBlocks)
        {
            super(chunkProvider, world, chunkX, chunkZ, chunkBlocks);
        }

    }

    @Cancelable
    public static class SetNetherBridgeLocation extends ChunkFeatureEvent
    {

        public SetNetherBridgeLocation(IChunkProvider chunkProvider, World world, int chunkX, int chunkZ, byte[] chunkBlocks)
        {
            super(chunkProvider, world, chunkX, chunkZ, chunkBlocks);
        }

    }
    
}
