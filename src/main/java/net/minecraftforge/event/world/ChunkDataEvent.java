package net.minecraftforge.event.world;

import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * ChunkDataEvent is fired when an event involving chunk data occurs.<br>
 * If a method utilizes this {@link Event} as its parameter, the method will
 * receive every child event of this class.<br>
 * <br>
 * {@link #data} contains the NBTTagCompound containing the chunk data for this event.<br>
 * <br>
 * All children of this event are fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.<br>
 **/
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
    
    /**
     * ChunkDataEvent.Load is fired when vanilla Minecraft attempts to load Chunk data.<br>
     * This event is fired during chunk loading in
     * {@link net.minecraftforge.common.chunkio.ChunkIOProvider#syncCallback()}.<br>
     * <br>
     * This event is not {@link net.minecraftforge.fml.common.eventhandler.Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link net.minecraftforge.fml.common.eventhandler.Event.HasResult} <br>
     * <br>
     * This event is fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.<br>
     **/
    public static class Load extends ChunkDataEvent
    {
        public Load(Chunk chunk, NBTTagCompound data)
        {
            super(chunk, data);
        }
    }
    
    /**
     * ChunkDataEvent.Save is fired when vanilla Minecraft attempts to save Chunk data.<br>
     * This event is fired during chunk saving in 
     * {@link AnvilChunkLoader#saveChunk(World, Chunk)}. <br>
     * <br>
     * This event is not {@link net.minecraftforge.fml.common.eventhandler.Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link net.minecraftforge.fml.common.eventhandler.Event.HasResult} <br>
     * <br>
     * This event is fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.<br>
     **/
    public static class Save extends ChunkDataEvent
    {
        public Save(Chunk chunk, NBTTagCompound data)
        {
            super(chunk, data);
        }
    }
}
