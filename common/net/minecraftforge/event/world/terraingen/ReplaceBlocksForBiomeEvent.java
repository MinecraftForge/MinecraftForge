package net.minecraftforge.event.world.terraingen;

import net.minecraft.src.BiomeGenBase;
import net.minecraft.src.IChunkProvider;
import net.minecraft.src.World;
import net.minecraftforge.event.Cancelable;

@Cancelable
public class ReplaceBlocksForBiomeEvent extends ChunkProviderEvent
{
    public final BiomeGenBase[] biomesForGeneration;
         
    private boolean handled = false;

    public ReplaceBlocksForBiomeEvent(IChunkProvider chunkProvider, World world, int chunkX, int chunkZ, byte[] chunkBlocks, BiomeGenBase[] biomesForGeneration)
    {
        super(chunkProvider, world, chunkX, chunkZ, chunkBlocks);
         this.biomesForGeneration = biomesForGeneration;
    }

}
