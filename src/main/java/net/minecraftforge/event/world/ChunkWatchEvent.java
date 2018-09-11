package net.minecraftforge.event.world;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;

/**
 * ChunkWatchEvent is fired when an event involving a chunk being watched occurs.<br>
 * If a method utilizes this {@link Event} as its parameter, the method will
 * receive every child event of this class.<br>
 * <br>
 * {@link #chunk} contains the ChunkPos of the Chunk this event is affecting.<br>
 * {@link #player} contains the EntityPlayer that is involved with this chunk being watched. <br>
 * <br>
 * The {@link #player}'s world may not be the same as the world of the chunk
 * when the player is teleporting to another dimension.<br>
 * <br>
 * All children of this event are fired on the {@link MinecraftForge#EVENT_BUS}.<br>
 **/
public class ChunkWatchEvent extends ChunkEvent
{

    private final EntityPlayerMP player;
    private final ChunkPos pos;

    public ChunkWatchEvent(Chunk chunk, EntityPlayerMP player)
    {
        super(chunk);
        this.player = player;
        this.pos = chunk.getPos();
    }

    public EntityPlayerMP getPlayer()
    {
        return this.player;
    }

    public ChunkPos getPos()
    {
        return this.pos;
    }

    /**
     * ChunkWatchEvent.Watch is fired when an EntityPlayer begins watching a chunk.<br>
     * This event is fired when a chunk is added to the watched chunks of an EntityPlayer in
     * {@link PlayerChunkMapEntry#addPlayer(EntityPlayerMP)} and {@link PlayerChunkMapEntry#sendToPlayers()}. <br>
     * <br>
     * This event is not {@link net.minecraftforge.eventbus.api.Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult} <br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
     **/
    public static class Watch extends ChunkWatchEvent
    {
        public Watch(Chunk chunk, EntityPlayerMP player) {super(chunk, player);}
    }

    /**
     * ChunkWatchEvent.UnWatch is fired when an EntityPlayer stops watching a chunk.<br>
     * This event is fired when a chunk is removed from the watched chunks of an EntityPlayer in
     * {@link PlayerChunkMapEntry#removePlayer(EntityPlayerMP)}. <br>
     * <br>
     * This event is not {@link net.minecraftforge.eventbus.api.Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult} <br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
     **/
    public static class UnWatch extends ChunkWatchEvent
    {
        public UnWatch(Chunk chunk, EntityPlayerMP player) {super(chunk, player);}
    }
}
