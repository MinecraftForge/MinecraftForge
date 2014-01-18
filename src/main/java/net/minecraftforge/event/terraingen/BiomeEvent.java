package net.minecraftforge.event.terraingen;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.block.Block;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.biome.BiomeGenBase;

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

    public static class BiomeColor extends BiomeEvent
    {
        public final int originalColor;
        public int newColor;
        
        public BiomeColor(BiomeGenBase biome, int original)
        {
            super(biome);
            originalColor = original;
            newColor = original;
        }
    }
    
    /**
     * This event is fired when the village generator attempts to choose a block ID
     * based on the village's biome.
     * 
     * You can cancel the event to override default values
     */
    @HasResult
    public static class GetVillageBlockID extends BiomeEvent
    {
        public final Block original;
        public final int type;
        public Block replacement;

        public GetVillageBlockID(BiomeGenBase biome, Block original, int type)
        {
            super(biome);
            this.original = original;
            this.type = type;
        }
    }
    
    /**
     * This event is fired when the village generator attempts to choose a block
     * metadata based on the village's biome.
     * 
     * You can set the result to DENY to prevent the default block metadata selection.
     */
    @HasResult
    public static class GetVillageBlockMeta extends BiomeEvent
    {
        public final Block original;
        public final int type;
        public int replacement;

        public GetVillageBlockMeta(BiomeGenBase biome, Block original, int type)
        {
            super(biome);
            this.original = original;
            this.type = type;
        }
    }
    
    /**
     * This event is fired when a biome is queried for its grass color. 
     */
    public static class GetGrassColor extends BiomeColor
    {
        public GetGrassColor(BiomeGenBase biome, int original)
        {
            super(biome, original);
        }
    }
    
    /**
     * This event is fired when a biome is queried for its grass color. 
     */
    public static class GetFoliageColor extends BiomeColor
    {
        public GetFoliageColor(BiomeGenBase biome, int original)
        {
            super(biome, original);
        }
    }
    
    /**
     * This event is fired when a biome is queried for its water color. 
     */
    public static class GetWaterColor extends BiomeColor
    {
        public GetWaterColor(BiomeGenBase biome, int original)
        {
            super(biome, original);
        }
    }
}
