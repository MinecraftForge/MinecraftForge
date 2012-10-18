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

    public static class ChunkPreDecorateEvent extends ChunkProviderEvent
    {

        public ChunkPreDecorateEvent(IChunkProvider chunkProvider, World world)
        {
            super(chunkProvider, world);
        }
        
    }

    public static class ChunkPostDecorateEvent extends ChunkProviderEvent
    {

        public ChunkPostDecorateEvent(IChunkProvider chunkProvider, World world)
        {
            super(chunkProvider, world);
        }
        
    }
    
}
