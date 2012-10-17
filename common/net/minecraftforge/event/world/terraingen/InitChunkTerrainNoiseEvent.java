package net.minecraftforge.event.world.terraingen;

import net.minecraft.src.IChunkProvider;
import net.minecraft.src.World;

public class InitChunkTerrainNoiseEvent extends ChunkProviderEvent
{

    /** Holds the overall noise array used in chunk generation */
    public final double[] noiseArray;

    public InitChunkTerrainNoiseEvent(double[] noiseArray, IChunkProvider chunkProvider, World world)
    {
        super(chunkProvider, world);
        this.noiseArray = noiseArray;
    }

}
