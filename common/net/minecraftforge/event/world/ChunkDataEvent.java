package net.minecraftforge.event.world;

import net.minecraft.world.chunk.Chunk;
import net.minecraft.nbt.NBTTagCompound;

public class ChunkDataEvent extends ChunkEvent
{
    private final NBTTagCompound data;

    public ChunkDataEvent(Chunk chunk, NBTTagCompound data)
    {
        super(chunk);
        this.data = data;
    }
    
    public NBTTagCompound getData()
    {
        return data;
    }
    
    public static class Load extends ChunkDataEvent
    {
        public Load(Chunk chunk, NBTTagCompound data)
        {
            super(chunk, data);
        }
    }

    public static class Save extends ChunkDataEvent
    {
        public Save(Chunk chunk, NBTTagCompound data)
        {
            super(chunk, data);
        }
    }
}
