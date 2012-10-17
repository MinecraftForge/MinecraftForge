package net.minecraftforge.event.world.terraingen;

import net.minecraft.src.IChunkProvider;
import net.minecraft.src.World;
import net.minecraftforge.event.world.WorldEvent;

public class ChunkProviderEvent extends WorldEvent
{

    public final IChunkProvider chunkProvider;
    
    public ChunkProviderEvent(IChunkProvider chunkProvider, World world)
    {
        super(world);
        this.chunkProvider = chunkProvider;
    }

}
