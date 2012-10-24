package net.minecraftforge.event.world.terraingen;

import net.minecraft.src.IChunkProvider;
import net.minecraft.src.MapGenMineshaft;
import net.minecraft.src.MapGenScatteredFeature;
import net.minecraft.src.MapGenStronghold;
import net.minecraft.src.MapGenVillage;
import net.minecraft.src.World;
import net.minecraftforge.event.Cancelable;

public abstract class ChunkMapFeatureLocateEvent extends ChunkProviderEvent
{

    public final MapGenMineshaft mineshaftGenerator;
    public final MapGenVillage villageGenerator;
    public final MapGenStronghold strongholdGenerator;
    public final MapGenScatteredFeature scatteredFeatureGenerator;
    
    public ChunkMapFeatureLocateEvent(IChunkProvider chunkProvider, World world, int chunkX, int chunkZ, byte[] terrainArray, MapGenMineshaft mineshaftGenerator, MapGenVillage villageGenerator, MapGenStronghold strongholdGenerator, MapGenScatteredFeature scatteredFeatureGenerator)
    {
        super(chunkProvider, world, chunkX, chunkZ, terrainArray);
        this.mineshaftGenerator = mineshaftGenerator;
        this.villageGenerator = villageGenerator;
        this.strongholdGenerator = strongholdGenerator;
        this.scatteredFeatureGenerator = scatteredFeatureGenerator;
    }

    public static class ChunkPreSetMapFeatureLocationsEvent extends ChunkMapFeatureLocateEvent
    {

        public ChunkPreSetMapFeatureLocationsEvent(IChunkProvider chunkProvider, World world, int chunkX, int chunkZ, byte[] terrainArray, MapGenMineshaft mineshaftGenerator, MapGenVillage villageGenerator, MapGenStronghold strongholdGenerator, MapGenScatteredFeature scatteredFeatureGenerator)
        {
            super(chunkProvider, world, chunkX, chunkZ, terrainArray, mineshaftGenerator, villageGenerator, strongholdGenerator, scatteredFeatureGenerator );
        }
    }

    @Cancelable
    public static class ChunkSetMapFeatureLocationsEvent extends ChunkMapFeatureLocateEvent
    {

        public ChunkSetMapFeatureLocationsEvent(IChunkProvider chunkProvider, World world, int chunkX, int chunkZ, byte[] terrainArray, MapGenMineshaft mineshaftGenerator, MapGenVillage villageGenerator, MapGenStronghold strongholdGenerator, MapGenScatteredFeature scatteredFeatureGenerator)
        {
            super(chunkProvider, world, chunkX, chunkZ, terrainArray, mineshaftGenerator, villageGenerator, strongholdGenerator, scatteredFeatureGenerator );
        }
    }

    public static class ChunkPostSetMapFeatureLocationsEvent extends ChunkMapFeatureLocateEvent
    {

        public ChunkPostSetMapFeatureLocationsEvent(IChunkProvider chunkProvider, World world, int chunkX, int chunkZ, byte[] terrainArray, MapGenMineshaft mineshaftGenerator, MapGenVillage villageGenerator, MapGenStronghold strongholdGenerator, MapGenScatteredFeature scatteredFeatureGenerator)
        {
            super(chunkProvider, world, chunkX, chunkZ, terrainArray, mineshaftGenerator, villageGenerator, strongholdGenerator, scatteredFeatureGenerator );
        }
    }
    
}
