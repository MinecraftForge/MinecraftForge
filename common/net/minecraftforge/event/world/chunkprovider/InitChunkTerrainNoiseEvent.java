package net.minecraftforge.event.world.chunkprovider;

import net.minecraft.src.IChunkProvider;
import net.minecraft.src.World;

public class InitChunkTerrainNoiseEvent extends ChunkProviderEvent
{

    /** Holds the overall noise array used in chunk generation */
    private double[] noiseArray;

    public InitChunkTerrainNoiseEvent(double[] noiseArray, IChunkProvider chunkProvider, World world)
    {
        super(chunkProvider, world);
        this.noiseArray = noiseArray;
    }

    public double[] getNoiseArray()
    {
        return noiseArray;
    }

    public void setNoiseArray(double[] noiseArray)
    {
        this.noiseArray = noiseArray;
    }

}
