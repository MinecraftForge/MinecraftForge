package net.minecraftforge.event.world.terraingen;

import net.minecraft.src.IChunkProvider;
import net.minecraft.src.World;
import net.minecraftforge.event.world.WorldEvent;

public abstract class ChunkProviderEvent extends WorldEvent
{

    public final IChunkProvider chunkProvider;
    
    public ChunkProviderEvent(IChunkProvider chunkProvider, World world)
    {
        super(world);
        this.chunkProvider = chunkProvider;
    }

    public static class ChunkPreBiomeDecorateEvent extends ChunkProviderEvent
    {

        public ChunkPreBiomeDecorateEvent(IChunkProvider chunkProvider, World world)
        {
            super(chunkProvider, world);
        }
        
    }

    public static class ChunkPostBiomeDecorateEvent extends ChunkProviderEvent
    {

        public ChunkPostBiomeDecorateEvent(IChunkProvider chunkProvider, World world)
        {
            super(chunkProvider, world);
        }
        
    }
    
}
