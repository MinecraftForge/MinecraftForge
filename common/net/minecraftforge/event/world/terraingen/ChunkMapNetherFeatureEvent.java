package net.minecraftforge.event.world.terraingen;

import net.minecraft.src.IChunkProvider;
import net.minecraft.src.MapGenMineshaft;
import net.minecraft.src.MapGenNetherBridge;
import net.minecraft.src.MapGenScatteredFeature;
import net.minecraft.src.MapGenStronghold;
import net.minecraft.src.MapGenVillage;
import net.minecraft.src.World;
import net.minecraftforge.event.Cancelable;

public abstract class ChunkMapNetherFeatureEvent extends ChunkPopulateEvent
{

    public final MapGenNetherBridge netherBridgeGenerator;
    
    public ChunkMapNetherFeatureEvent(IChunkProvider chunkProvider, World world, int chunkX, int chunkZ, MapGenNetherBridge netherBridgeGenerator)
    {
        super(chunkProvider, world, chunkX, chunkZ);
        this.netherBridgeGenerator = netherBridgeGenerator;
    }

    public static class ChunkPreMapNetherFeaturesGenerateEvent extends ChunkMapNetherFeatureEvent
    {

        public ChunkPreMapNetherFeaturesGenerateEvent(IChunkProvider chunkProvider, World world, int chunkX, int chunkZ, MapGenNetherBridge netherBridgeGenerator)
        {
            super(chunkProvider, world, chunkX, chunkZ, netherBridgeGenerator);
        }

    }

    @Cancelable
    public static class ChunkMapNetherFeaturesGenerateEvent extends ChunkMapNetherFeatureEvent
    {

        public ChunkMapNetherFeaturesGenerateEvent(IChunkProvider chunkProvider, World world, int chunkX, int chunkZ, MapGenNetherBridge netherBridgeGenerator)
        {
            super(chunkProvider, world, chunkX, chunkZ, netherBridgeGenerator);
        }

    }

    public static class ChunkPostMapNetherFeaturesGenerateEvent extends ChunkMapNetherFeatureEvent
    {

        public ChunkPostMapNetherFeaturesGenerateEvent(IChunkProvider chunkProvider, World world, int chunkX, int chunkZ, MapGenNetherBridge netherBridgeGenerator)
        {
            super(chunkProvider, world, chunkX, chunkZ, netherBridgeGenerator);
        }

    }
    
}
