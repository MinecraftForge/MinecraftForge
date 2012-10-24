package net.minecraftforge.event.world.terraingen;

import net.minecraft.src.IChunkProvider;
import net.minecraft.src.MapGenMineshaft;
import net.minecraft.src.MapGenScatteredFeature;
import net.minecraft.src.MapGenStronghold;
import net.minecraft.src.MapGenVillage;
import net.minecraft.src.World;
import net.minecraftforge.event.Cancelable;

public abstract class ChunkMapFeatureEvent extends ChunkPopulateEvent
{

    public final MapGenMineshaft mineshaftGenerator;
    public final MapGenVillage villageGenerator;
    public final MapGenStronghold strongholdGenerator;
    public final MapGenScatteredFeature scatteredFeatureGenerator;
    
    public ChunkMapFeatureEvent(IChunkProvider chunkProvider, World world, int chunkX, int chunkZ, MapGenMineshaft mineshaftGenerator, MapGenVillage villageGenerator, MapGenStronghold strongholdGenerator, MapGenScatteredFeature scatteredFeatureGenerator)
    {
        super(chunkProvider, world, chunkX, chunkZ);
        this.mineshaftGenerator = mineshaftGenerator;
        this.villageGenerator = villageGenerator;
        this.strongholdGenerator = strongholdGenerator;
        this.scatteredFeatureGenerator = scatteredFeatureGenerator;
    }

    public static class ChunkPreMapFeaturesGenerateEvent extends ChunkMapFeatureEvent
    {

        public ChunkPreMapFeaturesGenerateEvent(IChunkProvider chunkProvider, World world, int chunkX, int chunkZ, MapGenMineshaft mineshaftGenerator, MapGenVillage villageGenerator, MapGenStronghold strongholdGenerator, MapGenScatteredFeature scatteredFeatureGenerator)
        {
            super(chunkProvider, world, chunkX, chunkZ, mineshaftGenerator, villageGenerator, strongholdGenerator, scatteredFeatureGenerator );
        }
    }

    @Cancelable
    public static class ChunkMapFeaturesGenerateEvent extends ChunkMapFeatureEvent
    {

        public ChunkMapFeaturesGenerateEvent(IChunkProvider chunkProvider, World world, int chunkX, int chunkZ, MapGenMineshaft mineshaftGenerator, MapGenVillage villageGenerator, MapGenStronghold strongholdGenerator, MapGenScatteredFeature scatteredFeatureGenerator)
        {
            super(chunkProvider, world, chunkX, chunkZ, mineshaftGenerator, villageGenerator, strongholdGenerator, scatteredFeatureGenerator );
        }
    }

    public static class ChunkPostMapFeaturesGenerateEvent extends ChunkMapFeatureEvent
    {

        public ChunkPostMapFeaturesGenerateEvent(IChunkProvider chunkProvider, World world, int chunkX, int chunkZ, MapGenMineshaft mineshaftGenerator, MapGenVillage villageGenerator, MapGenStronghold strongholdGenerator, MapGenScatteredFeature scatteredFeatureGenerator)
        {
            super(chunkProvider, world, chunkX, chunkZ, mineshaftGenerator, villageGenerator, strongholdGenerator, scatteredFeatureGenerator );
        }
    }
    
}
