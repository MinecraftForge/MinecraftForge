package net.minecraftforge.event.world.chunkprovider;

import net.minecraft.src.IChunkProvider;
import net.minecraft.src.World;
import net.minecraftforge.event.world.WorldEvent;

public class ChunkProviderEvent extends WorldEvent
{

    private final IChunkProvider chunkProvider;
    
    public ChunkProviderEvent(IChunkProvider chunkProvider, World world)
    {
        super(world);
        this.chunkProvider = chunkProvider;
    }

    public IChunkProvider getChunkProvider()
    {
        return chunkProvider;
    }

}
