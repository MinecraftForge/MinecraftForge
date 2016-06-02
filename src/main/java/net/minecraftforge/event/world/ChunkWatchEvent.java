package net.minecraftforge.event.world;

import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.ChunkPos;

/**
 * ChunkWatchEvent is fired when an event involving a chunk being watched occurs.<br>
 * If a method utilizes this {@link Event} as its parameter, the method will
 * receive every child event of this class.<br>
 * <br>
 * {@link #chunk} contains the ChunkCoordIntPair of the Chunk this event is affecting.<br>
 * {@link #player} contains the EntityPlayer that is involved with this chunk being watched. <br>
 * <br>
 * All children of this event are fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.<br>
 **/
public class ChunkWatchEvent extends Event
{
    private final ChunkPos chunk;
    private final EntityPlayerMP player;

    public ChunkWatchEvent(ChunkPos chunk, EntityPlayerMP player)
    {
        this.chunk = chunk;
        this.player = player;
    }

    public ChunkPos getChunk()
    {
        return chunk;
    }

    public EntityPlayerMP getPlayer()
    {
        return player;
    }

    /**
     * ChunkWatchEvent.Watch is fired when an EntityPlayer begins watching a chunk.<br>
     * This event is fired when a chunk is added to the watched chunks of an EntityPlayer in
     * {@link EntityPlayerMP#onUpdate()}. <br>
     * <br>
     * This event is not {@link net.minecraftforge.fml.common.eventhandler.Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link net.minecraftforge.fml.common.eventhandler.Event.HasResult} <br>
     * <br>
     * This event is fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.<br>
     **/
    public static class Watch extends ChunkWatchEvent
    {
        public Watch(ChunkPos chunk, EntityPlayerMP player) { super(chunk, player); }
    }

    /**
     * ChunkWatchEvent.UnWatch is fired when an EntityPlayer stops watching a chunk.<br>
     * This event is fired when a chunk is removed from the watched chunks of an EntityPlayer in
     * {@link PlayerChunkMapEntry#removePlayer(EntityPlayerMP)}. <br>
     * <br>
     * This event is not {@link net.minecraftforge.fml.common.eventhandler.Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link net.minecraftforge.fml.common.eventhandler.Event.HasResult} <br>
     * <br>
     * This event is fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.<br>
     **/
    public static class UnWatch extends ChunkWatchEvent
    {
        public UnWatch(ChunkPos chunkLocation, EntityPlayerMP player) { super(chunkLocation, player); }
    }
}
