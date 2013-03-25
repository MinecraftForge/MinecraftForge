package net.minecraftforge.event.terraingen;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.biome.BiomeGenBase;
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
    

    @SideOnly(Side.CLIENT)
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
    
    /**
     * This event is fired when a biome is queried for its grass color. 
     */

    @SideOnly(Side.CLIENT)
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
    @SideOnly(Side.CLIENT)
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
    @SideOnly(Side.CLIENT)
    public static class GetWaterColor extends BiomeColor
    {
        public GetWaterColor(BiomeGenBase biome, int original)
        {
            super(biome, original);
        }
    }
}
