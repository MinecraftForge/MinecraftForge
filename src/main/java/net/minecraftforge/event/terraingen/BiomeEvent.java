package net.minecraftforge.event.terraingen;

import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.Event.HasResult;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.biome.BiomeGenBase;

/**
 * BiomeEvent is fired whenever an event involving biomes occurs.<br>
 * If a method utilizes this {@link Event} as its parameter, the method will
 * receive every child event of this class.<br>
 * <br>
 * All children of this event are fired on the {@link MinecraftForge#TERRAIN_GEN_BUS}.
 **/
public class BiomeEvent extends Event
{
    public final BiomeGenBase biome;

    public BiomeEvent(BiomeGenBase biome)
    {
        this.biome = biome;
    }

    /**
     * CreateDecorator is fired when a BiomeDecorator is created.<br>
     * This event is fired whenever a BiomeDecorator is created in
     * DeferredBiomeDecorator#fireCreateEventAndReplace(BiomeGenBase).<br>
     * <br>
     * {@link #originalBiomeDecorator} contains the original BiomeDecorator that would be used in vanilla.
     * {@link #newBiomeDecorator} contains the new BiomeDecoration to be used by Minecraft.
     * <br>
     * This event is not {@link Cancelable}.
     * <br>
     * This event does not have a result. {@link HasResult}
     * <br>
     * This event is fired on the {@link MinecraftForge#TERRAIN_GEN_BUS}.
     **/
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

    /**
     * BiomeColor is fired whenever an event involving biome colors occurs. <br>
     * If a method utilizes this {@link Event} as its parameter, the method will
     * receive every child event of this class.<br>
     * <br>
     * All children of this event are fired on the {@link MinecraftForge#TERRAIN_GEN_BUS}.
     **/
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
        public final IBlockState original;
        public IBlockState replacement;

        public GetVillageBlockID(BiomeGenBase biome, IBlockState original)
        {
            super(biome);
            this.original = original;
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
