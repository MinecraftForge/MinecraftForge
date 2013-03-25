package net.minecraftforge.event.world;

import net.minecraft.world.chunk.Chunk;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ChunkEvent extends WorldEvent
{
    private final Chunk chunk;
    
    public ChunkEvent(Chunk chunk)
    {
        super(chunk.worldObj);
        this.chunk = chunk;
    }
    
    public Chunk getChunk()
    {
        return chunk;
    }
    
    public static class Load extends ChunkEvent
    {
        public Load(Chunk chunk)
        {
            super(chunk);
        }
    }

    public static class Unload extends ChunkEvent
    {
        public Unload(Chunk chunk)
        {
            super(chunk);
        }
    }
}
