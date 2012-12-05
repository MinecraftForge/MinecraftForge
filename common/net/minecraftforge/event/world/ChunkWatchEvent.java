package net.minecraftforge.event.world;

import net.minecraft.src.ChunkCoordIntPair;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.PlayerInstance;
import net.minecraft.src.WorldServer;
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
