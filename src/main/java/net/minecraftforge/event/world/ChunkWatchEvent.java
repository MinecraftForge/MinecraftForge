package net.minecraftforge.event.world;

import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerInstance;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.Event;

public class ChunkWatchEvent extends Event
{
    public final ChunkCoordIntPair chunk;
    public final EntityPlayerMP player;
    
    public ChunkWatchEvent(ChunkCoordIntPair chunk, EntityPlayerMP player)
    {
        this.chunk = chunk;
        this.player = player;
    }
    
    public static class Watch extends ChunkWatchEvent
    {
        public Watch(ChunkCoordIntPair chunk, EntityPlayerMP player) { super(chunk, player); }        
    }
    
    public static class UnWatch extends ChunkWatchEvent
    {
        public UnWatch(ChunkCoordIntPair chunkLocation, EntityPlayerMP player) { super(chunkLocation, player); }        
    }
}
