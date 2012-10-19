package net.minecraftforge.event.world.terraingen;

import net.minecraft.src.IChunkProvider;
import net.minecraft.src.MapGenNetherBridge;
import net.minecraft.src.World;
import net.minecraftforge.event.Cancelable;

public abstract class ChunkMapNetherFeatureLocateEvent extends ChunkProviderEvent
{

    public final MapGenNetherBridge netherBridgeGenerator;
    
    public ChunkMapNetherFeatureLocateEvent(IChunkProvider chunkProvider, World world, int chunkX, int chunkZ, byte[] terrainArray, MapGenNetherBridge netherBridgeGenerator)
    {
        super(chunkProvider, world, chunkX, chunkZ, terrainArray);
        this.netherBridgeGenerator = netherBridgeGenerator;
    }

    public static class ChunkPreSetMapNetherFeatureLocationsEvent extends ChunkMapNetherFeatureLocateEvent
    {

        public ChunkPreSetMapNetherFeatureLocationsEvent(IChunkProvider chunkProvider, World world, int chunkX, int chunkZ, byte[] terrainArray,
                MapGenNetherBridge netherBridgeGenerator)
        {
            super(chunkProvider, world, chunkX, chunkZ, terrainArray, netherBridgeGenerator);
        }
        
    }

    @Cancelable
    public static class ChunkSetMapNetherFeatureLocationsEvent extends ChunkMapNetherFeatureLocateEvent
    {

        public ChunkSetMapNetherFeatureLocationsEvent(IChunkProvider chunkProvider, World world, int chunkX, int chunkZ, byte[] terrainArray,
                MapGenNetherBridge netherBridgeGenerator)
        {
            super(chunkProvider, world, chunkX, chunkZ, terrainArray, netherBridgeGenerator);
        }

    }

    public static class ChunkPostSetMapNetherFeatureLocationsEvent extends ChunkMapNetherFeatureLocateEvent
    {

        public ChunkPostSetMapNetherFeatureLocationsEvent(IChunkProvider chunkProvider, World world, int chunkX, int chunkZ, byte[] terrainArray,
                MapGenNetherBridge netherBridgeGenerator)
        {
            super(chunkProvider, world, chunkX, chunkZ, terrainArray, netherBridgeGenerator);
        }

    }
    
}
