package net.minecraftforge.event.world;

import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * ChunkEvent is fired when an event involving a chunk occurs.<br>
 * If a method utilizes this {@link Event} as its parameter, the method will
 * receive every child event of this class.<br>
 * <br>
 * {@link #chunk} contains the Chunk this event is affecting.<br>
 * <br>
 * All children of this event are fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.<br>
 **/
public class ChunkEvent extends WorldEvent
{
    private final Chunk chunk;

    public ChunkEvent(Chunk chunk)
    {
        super(chunk.getWorld());
        this.chunk = chunk;
    }

    public Chunk getChunk()
    {
        return chunk;
    }

    /**
     * ChunkEvent.Load is fired when vanilla Minecraft attempts to load a Chunk into the world.<br>
     * This event is fired during chunk loading in <br>
     * {@link ChunkProviderClient#loadChunk(int, int)}, <br>
     * Chunk.onChunkLoad(). <br>
     * <br>
     * This event is not {@link net.minecraftforge.fml.common.eventhandler.Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link net.minecraftforge.fml.common.eventhandler.Event.HasResult} <br>
     * <br>
     * This event is fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.<br>
     **/
    public static class Load extends ChunkEvent
    {
        public Load(Chunk chunk)
        {
            super(chunk);
        }
    }

    /**
     * ChunkEvent.Unload is fired when vanilla Minecraft attempts to unload a Chunk from the world.<br>
     * This event is fired during chunk unloading in <br>
     * Chunk.onChunkUnload(). <br>
     * <br>
     * This event is not {@link net.minecraftforge.fml.common.eventhandler.Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link net.minecraftforge.fml.common.eventhandler.Event.HasResult} <br>
     * <br>
     * This event is fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.<br>
     **/
    public static class Unload extends ChunkEvent
    {
        public Unload(Chunk chunk)
        {
            super(chunk);
        }
    }
}
