package net.minecraftforge.event.terraingen;

import net.minecraft.src.*;
import net.minecraftforge.event.*;

public class BiomeEvent extends Event
{
    public final BiomeGenBase biome;

    public BiomeEvent(BiomeGenBase biome)
    {
        this.biome = biome;
    }
    
    public static class CreateDecorator extends BiomeEvent
    {
        public final BiomeDecorator originalBiomeDecorator;
        public BiomeDecorator newBiomeDecorator;
        
        public CreateDecorator(BiomeGenBase biome, BiomeDecorator original)
        {
            super(biome);
            originalBiomeDecorator = original;
            newBiomeDecorator = original;
        }
    }

    public static class BlockReplacement extends BiomeEvent
    {
        public final int original;
        public int replacement;

        public BlockReplacement(BiomeGenBase biome, int original, int replacement)
        {
            super(biome);
            this.original = original;
            this.replacement = replacement;
        }
    }
    
    /**
     * This event is fired when the village generator attempts to choose a block ID
     * based on the village's biome.
     * 
     * You can set the result to DENY to prevent the default block ID selection.
     */
    @HasResult
    public static class GetVillageBlockID extends BlockReplacement
    {
        public GetVillageBlockID(BiomeGenBase biome, int original, int replacement)
        {
            super(biome, original, replacement);
        }
    }
    
    /**
     * This event is fired when the village generator attempts to choose a block
     * metadata based on the village's biome.
     * 
     * You can set the result to DENY to prevent the default block metadata selection.
     */
    @HasResult
    public static class GetVillageBlockMeta extends BlockReplacement
    {
        public GetVillageBlockMeta(BiomeGenBase biome, int original, int replacement)
        {
            super(biome, original, replacement);
        }
    }
}
