package net.minecraftforge.event.world.terraingen;

import net.minecraft.src.IChunkProvider;
import net.minecraft.src.World;
import net.minecraftforge.event.world.WorldEvent;

public class ChunkInitTerrainNoiseEvent  extends WorldEvent
{

    public final IChunkProvider chunkProvider;
    public final int chunkX;
    public final int chunkZ;
    public double[] noiseArray;

    public ChunkInitTerrainNoiseEvent(IChunkProvider chunkProvider, World world, int chunkX, int chunkZ, double[] noiseArray)
    {
        super(world);
        this.chunkProvider = chunkProvider;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.noiseArray = noiseArray;
    }

}
