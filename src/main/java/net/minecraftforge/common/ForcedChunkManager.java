package net.minecraftforge.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.unimi.dsi.fastutil.longs.Long2ShortMap;
import it.unimi.dsi.fastutil.longs.Long2ShortRBTreeMap;
import it.unimi.dsi.fastutil.longs.LongRBTreeSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;

/**
 * New Ticket based Chunk Manager
 * 
 * @author MCenderdragon
 */
public class ForcedChunkManager extends WorldSavedData
{
    private static final Logger LOGGER = LogManager.getLogger();
    
    private Long2ShortMap forcedChunksMap = new Long2ShortRBTreeMap();
    private List<Ticket<?>> tickets = new ArrayList<ForcedChunkManager.Ticket<?>>();
    private ServerWorld world;
    
    public ForcedChunkManager(ServerWorld world) 
    {
        super("forge_chunk_tickets");
        this.world = world;
    }
    
    private void forceChunk(long pos)
    {
        short forceAmound = forcedChunksMap.get(pos);
        forceAmound++;
        if (forceAmound == 1)
        {
            world.forceChunk(ChunkPos.getX(pos), ChunkPos.getZ(pos), true);
        }
        forcedChunksMap.put(pos, forceAmound);
    }
    
    private void unforceChunk(long pos)
    {
        short forceAmound = forcedChunksMap.get(pos);
        forceAmound--;
        if (forceAmound == 0)
        {
            world.forceChunk(ChunkPos.getX(pos), ChunkPos.getZ(pos), true);
        }
        forcedChunksMap.put(pos, forceAmound);
    }
    
    @Override
    public void read(CompoundNBT nbt) 
    {
        ByteArrayInputStream bin = new ByteArrayInputStream(nbt.getByteArray("chunkmap"));
        DataInputStream din = new DataInputStream(bin);
        try
        {
            int size = din.readInt();
            for (int i=0;i<size;i++)
            {
                forcedChunksMap.put(din.readLong(), din.readShort());
            }
            din.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        ListNBT list = nbt.getList("String", nbt.getId());
        for (int i=0;i<list.size();i++)
        {
            CompoundNBT ticketNBT = list.getCompound(i);
            try
            {
                Ticket<?>  ticket = readTicket(ticketNBT);
                if (ticket!=null && tickets.get(tickets.size()-1)!=ticket)
                {
                    if (!tickets.contains(ticket))
                    {
                        tickets.add(ticket);
                    }
                }
            }
            catch (RuntimeException e)
            {
                LOGGER.error("Unable to load Ticket with NBT {}", ticketNBT.toString());
                LOGGER.catching(Level.ERROR, e);
            }
        }
    }
    
    @Override
    public CompoundNBT write(CompoundNBT nbt) 
    {
        ByteArrayOutputStream bout = new ByteArrayOutputStream(4 + (2+8) * forcedChunksMap.size());
        DataOutputStream dout = new DataOutputStream(bout);
        try 
        {
            dout.writeInt(forcedChunksMap.size());
            for (Long2ShortMap.Entry entry : forcedChunksMap.long2ShortEntrySet()) 
            {
                dout.writeLong(entry.getLongKey());
                dout.writeShort(entry.getShortValue());
            }
            dout.close();
            nbt.putByteArray("chunkmap", bout.toByteArray());
        }
        catch (IOException e) 
        {
            e.printStackTrace();
        }
        
        ListNBT list = new ListNBT();
        for (Ticket<?> t : tickets)
        {
            list.add(writeTicket(t));
        }
        nbt.put("tickets", list);
        
        return nbt;
    }
    
    private CompoundNBT writeTicket(Ticket<?> t)
    {
        CompoundNBT nbt = t.serializeNBT();
        
        int id;
        Class<? extends Ticket> cls = t.getClass();
        if (cls == EntityTicket.class)
        {
            id = 0;
        }
        else if (cls == BlockTicket.class)
        {
            id = 1;
        }
        else if (cls == StringTicket.class)
        {
            id = 2;
        }
        else
        {
            id = -1;
            LOGGER.warn("Unknown Ticket {} of class {}", t, t.getClass());
            nbt.putString("clazz", t.getClass().toString());
        }
        nbt.putInt("ID", id);
        return nbt;
    }
    
    private Ticket<?> readTicket(CompoundNBT nbt)
    {
        int id = nbt.getInt("ID");
        switch (id) {
        case 0:
            return new EntityTicket(nbt);
        case 1:
            return new BlockTicket(nbt);
        case 2:
            return new StringTicket(nbt);
        case -1:
            try
            {
                Class<Ticket<?>> c = (Class<Ticket<?>>) Class.forName(nbt.getString("clazz"));
                Constructor<Ticket<?>> create = c.getConstructor(ForcedChunkManager.class, CompoundNBT.class);
                LOGGER.warn("Unknown Class {} has matching constructor! register it properly", c);
                
                return create.newInstance(this, nbt);
            }
            catch (ClassNotFoundException e) {
                LOGGER.warn(e);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        default:
            LOGGER.warn("Unknown Ticket ID {} of class {}", id);
            return null;
        }
    }
    
    public <T extends Entity> Ticket<Entity> ofEntity(ModContainer mod, T t)
    {
        return new EntityTicket(mod, t);
    }
    
    public <T extends BlockPos> Ticket<BlockPos> ofBlock(ModContainer mod, T t)
    {
        return new BlockTicket(mod, t);
    }
    
    public Ticket<String> ofCause(ModContainer mod, String t)
    {
        return new StringTicket(mod, t);
    }
    
    public Stream<Ticket<?>> getAllTicketsFrom(ModContainer mod)
    {
        return this.tickets.stream().filter(t -> t.owner == mod);
    }
    
    public static ForcedChunkManager getManager(ServerWorld w)
    {
        return w.getSavedData().getOrCreate(() -> new ForcedChunkManager(w), "forge_chunk_tickets");
    }
    
    public abstract class Ticket<T> implements INBTSerializable<CompoundNBT>
    {
        private LongSet forcedChunksSet = new LongRBTreeSet();
        public T place;
        public final ModContainer owner;
        
        public Ticket(ModContainer owner, T place)
        {
            this.place = place;
            this.owner = owner;
            ForcedChunkManager.this.tickets.add(this);
        }
        
        public Ticket(CompoundNBT nbt)
        {
            this.owner = ModList.get().getModContainerById(nbt.getString("modid")).orElseThrow(RuntimeException::new);
            this.deserializeNBT(nbt);
        }
        
        public boolean isChunkForced(ChunkPos pos)
        {
            return forcedChunksSet.contains(pos.asLong());
        }
        
        @Override
        public void deserializeNBT(CompoundNBT nbt) 
        {
            for (long l : nbt.getLongArray("chunks"))
                forcedChunksSet.add(l);
            
            this.place = readFromNBT(nbt);
        }
        
        @Override
        public CompoundNBT serializeNBT() 
        {
            CompoundNBT nbt = new CompoundNBT();
            nbt.putLongArray("chunks", forcedChunksSet.toLongArray());
            nbt.putString("modid", owner.getModId());
            writeToNBT(nbt, place);
            return nbt;
        }
        
        protected abstract T readFromNBT(CompoundNBT nbt);
        
        protected abstract void writeToNBT(CompoundNBT nbt, T t);
        
        /**
         * @param pos the ChunkPos
         * @return true if the chunk is not forced, false is the chunk already was forced
         */
        public boolean forceChunk(ChunkPos pos)
        {
            if (isChunkForced(pos))
                return false;
            else
            {
                forcedChunksMap.put(pos.asLong(), (short) (forcedChunksMap.getOrDefault(pos.asLong(), (short)0) +1));
                forcedChunksSet.add(pos.asLong());
                ForcedChunkManager.this.forceChunk(pos.asLong());
                return true;
            }
        }
        
        /**
         * @param pos the ChunkPos
         * @return true if the chunk was forced, false is the chunk was not forced
         */
        public boolean unforceChunk(ChunkPos pos)
        {
            if (isChunkForced(pos))
            {
                ForcedChunkManager.this.unforceChunk(pos.asLong());
                forcedChunksSet.remove(pos.asLong());
                return true;
            }
            else
            {
                return false;
            }
        }
        
        /**
         * Unfoces all chunks
         */
        public void unforceAll()
        {
            for (long l : forcedChunksSet)
            {
                ForcedChunkManager.this.unforceChunk(l);
            }
            forcedChunksSet.clear();
        }
        
        /**
         * Unforces all chunks and deletes the ticket
         */
        public void deleteTicket()
        {
            unforceAll();
            ForcedChunkManager.this.tickets.remove(this);
        }
    }
    
    public class EntityTicket extends Ticket<Entity>
    {
        public EntityTicket(ModContainer owner, Entity place) 
        {
            super(owner, place);
        }

        public EntityTicket(CompoundNBT nbt) 
        {
            super(nbt);
        }

        @Override
        protected Entity readFromNBT(CompoundNBT nbt) 
        {
            return world.getEntityByUuid(nbt.getUniqueId("entityID"));//TODO: check if entity is already loaded
        }

        @Override
        protected void writeToNBT(CompoundNBT nbt, Entity t) 
        {
            nbt.putUniqueId("entityID", t.getUniqueID());
        }
    }
    
    public class BlockTicket extends Ticket<BlockPos>
    {
        public BlockTicket(CompoundNBT nbt) 
        {
            super(nbt);
        }
        
        public BlockTicket(ModContainer owner, BlockPos place) 
        {
            super(owner, place);
        }

        @Override
        protected BlockPos readFromNBT(CompoundNBT nbt) 
        {
            int[] pos = nbt.getIntArray("pos");
            return new BlockPos(pos[0], pos[1], pos[2]);
        }

        @Override
        protected void writeToNBT(CompoundNBT nbt, BlockPos t) 
        {
            nbt.putIntArray("pos", new int[] {t.getX(),t.getY(),t.getZ()});
        }
    }
    
    /**
     * For any other chunk loading
     */
    public class StringTicket extends Ticket<String>
    {

        public StringTicket(ModContainer owner, String place) 
        {
            super(owner, place);
        }
        
        public StringTicket(CompoundNBT nbt) 
        {
            super(nbt);
        }

        @Override
        protected String readFromNBT(CompoundNBT nbt) 
        {
            return nbt.getString("cause");
        }

        @Override
        protected void writeToNBT(CompoundNBT nbt, String t) 
        {
            nbt.putString("cause", t);
        }
    }
}
