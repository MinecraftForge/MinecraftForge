package net.minecraftforge.event.world.terraingen;

import net.minecraft.src.BiomeGenBase;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event;

@Cancelable
public class BiomeGetVillageBlockEvent extends Event
{

    public final BiomeGenBase biome;
    public final int blockIDToReplace;
    public final int defaultResult;
    public int result = 0;
    
    public BiomeGetVillageBlockEvent(BiomeGenBase biome, int blockIDToReplace, int defaultResult)
    {
        this.biome = biome;
        this.blockIDToReplace = blockIDToReplace;
        this.defaultResult = defaultResult;
    }

    public static class BiomeGetVillageBlockMetadataEvent extends BiomeGetVillageBlockEvent
    {

        public BiomeGetVillageBlockMetadataEvent(BiomeGenBase biome, int blockIDToReplace, int defaultResult)
        {
            super(biome, blockIDToReplace, defaultResult);
        }
        
    }
}
