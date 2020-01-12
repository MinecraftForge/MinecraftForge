package net.minecraftforge.common;

import it.unimi.dsi.fastutil.longs.Long2ShortMap;
import it.unimi.dsi.fastutil.longs.Long2ShortRBTreeMap;
import it.unimi.dsi.fastutil.longs.LongRBTreeSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.util.math.ChunkPos;

public class ForcedChunkManager 
{
    private Long2ShortMap forcedChunksMap = new Long2ShortRBTreeMap();
    
    private void forceChunk(long pos)
    {
        
    }
    
    private void unforceChunk(long pos)
    {
        
    }
    
    public abstract class Ticket<T>
    {
        private LongSet forcedChunksSet = new LongRBTreeSet();
        public final T owner;
        
        public Ticket(T owner)
        {
            this.owner = owner;
        }
        
        public boolean isChunkForced(ChunkPos pos)
        {
            return forcedChunksSet.contains(pos.asLong());
        }
        
        /**
         * @param pos the ChunkPos
         * @return true if the chunk is noit forced, false is the chuk already was forced
         */
        public boolean forceChunk(ChunkPos pos)
        {
            if(isChunkForced(pos))
                return false;
            else
            {
                forcedChunksMap.put(pos.asLong(), (short) (forcedChunksMap.getOrDefault(pos.asLong(), (short)0) +1));
                forcedChunksSet.add(pos.asLong());
                return true;
            }
        }
    }
}
