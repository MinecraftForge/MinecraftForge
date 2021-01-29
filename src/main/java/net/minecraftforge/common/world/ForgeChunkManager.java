package net.minecraftforge.common.world;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.UUIDCodec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ForcedChunksSaveData;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.server.TicketType;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.ModList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ParametersAreNonnullByDefault
public class ForgeChunkManager
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final TicketType<OwnedTicketInfo<BlockPos>> BLOCK = TicketType.create("forge:block", Comparator.comparing(info -> info));
    private static final TicketType<OwnedTicketInfo<UUID>> ENTITY = TicketType.create("forge:entity", Comparator.comparing(info -> info));
    private static final Map<String, LoadingCallback> callbacks = new HashMap<>();

    //TODO: Note that this should be called from enqueueWork in CommonSetupEvent (given it just has a simple hashmap as the backing map)
    public static void setForcedChunkLoadingCallback(String modId, LoadingCallback callback)
    {
        if (ModList.get().isLoaded(modId))
            callbacks.put(modId, callback);
        else
            LOGGER.warn("A mod attempted to set the forced chunk validation loading callback for an unloaded mod of id: {}", modId);
    }

    public static boolean forceChunk(ServerWorld world, String modId, BlockPos owner, int chunkX, int chunkZ, boolean add)
    {
        return forceChunk(world, modId, owner, chunkX, chunkZ, add, BLOCK, ForcedChunksSaveData::getBlockForcedChunks);
    }

    public static boolean forceChunk(ServerWorld world, String modId, Entity owner, int chunkX, int chunkZ, boolean add)
    {
        return forceChunk(world, modId, owner.getUniqueID(), chunkX, chunkZ, add);
    }

    public static boolean forceChunk(ServerWorld world, String modId, UUID owner, int chunkX, int chunkZ, boolean add)
    {
        return forceChunk(world, modId, owner, chunkX, chunkZ, add, ENTITY, ForcedChunksSaveData::getEntityForcedChunks);
    }

    //Based on ServerWorld#forceChunk
    private static <T extends Comparable<? super T>> boolean forceChunk(ServerWorld world, String modId, T owner, int chunkX, int chunkZ, boolean add,
          TicketType<OwnedTicketInfo<T>> type, Function<ForcedChunksSaveData, Map<TicketOwner<T>, LongSet>> ticketGetter)
    {
        if (!ModList.get().isLoaded(modId))
        {
            LOGGER.warn("A mod attempted to force a chunk for an unloaded mod of id: {}", modId);
            return false;
        }
        ForcedChunksSaveData saveData = world.getSavedData().getOrCreate(ForcedChunksSaveData::new, "chunks");
        ChunkPos pos = new ChunkPos(chunkX, chunkZ);
        long chunk = pos.asLong();
        Map<TicketOwner<T>, LongSet> tickets = ticketGetter.apply(saveData);
        TicketOwner<T> ticketOwner = new TicketOwner<>(modId, owner);
        boolean success = false;
        if (add)
        {
            success = tickets.computeIfAbsent(ticketOwner, o -> new LongOpenHashSet()).add(chunk);
            if (success)
                world.getChunk(chunkX, chunkZ);
        }
        else if (tickets.containsKey(ticketOwner))
        {
            LongSet chunks = tickets.get(ticketOwner);
            success = chunks.remove(chunk);
            if (success && chunks.isEmpty())
                tickets.remove(ticketOwner);
        }
        if (success)
        {
            saveData.setDirty(true);
            forceChunk(world, pos, type, ticketOwner, add);
        }
        return success;
    }

    private static <T extends Comparable<? super T>> void forceChunk(ServerWorld world, ChunkPos pos, TicketType<OwnedTicketInfo<T>> type, TicketOwner<T> value, boolean add)
    {
        //We use distance 2, as when using register/releaseTicket the ticket's level is set to 33 - distance
        // and the level that forced chunks use is 31
        OwnedTicketInfo<T> ticketInfo = new OwnedTicketInfo<>(value, pos);
        if (add)
            world.getChunkProvider().registerTicket(type, pos, 2, ticketInfo);
        else
            world.getChunkProvider().releaseTicket(type, pos, 2, ticketInfo);
    }

    public static void reinstatePersistentChunks(ServerWorld world, ForcedChunksSaveData saveData)
    {
        //Gather owned tickets by modid for both block and entity
        Map<String, Map<BlockPos, LongSet>> blockTickets = gatherTicketsByModId(saveData.getBlockForcedChunks());
        Map<String, Map<UUID, LongSet>> entityTickets = gatherTicketsByModId(saveData.getEntityForcedChunks());
        //Fire the callbacks allowing them to remove any tickets they don't want anymore
        for (Map.Entry<String, LoadingCallback> entry : callbacks.entrySet())
        {
            String modId = entry.getKey();
            boolean hasBlockTicket = blockTickets.containsKey(modId);
            boolean hasEntityTicket = entityTickets.containsKey(modId);
            if (hasBlockTicket || hasEntityTicket)
            {
                Map<BlockPos, LongSet> ownedBlockTickets = hasBlockTicket ? Collections.unmodifiableMap(blockTickets.get(modId)) : Collections.emptyMap();
                Map<UUID, LongSet> ownedEntityTickets = hasEntityTicket ? Collections.unmodifiableMap(entityTickets.get(modId)) : Collections.emptyMap();
                entry.getValue().validateTickets(world, new TicketHelper(saveData, modId, ownedBlockTickets, ownedEntityTickets));
            }
        }
        //Reinstate the chunks that we want to load
        reinstatePersistentChunks(world, BLOCK, saveData.getBlockForcedChunks());
        reinstatePersistentChunks(world, ENTITY, saveData.getEntityForcedChunks());
    }

    private static <T extends Comparable<? super T>> Map<String, Map<T, LongSet>> gatherTicketsByModId(Map<TicketOwner<T>, LongSet> tickets)
    {
        Map<String, Map<T, LongSet>> modSortedOwnedChunks = new HashMap<>();
        for (Map.Entry<TicketOwner<T>, LongSet> entry : tickets.entrySet())
        {
            modSortedOwnedChunks.computeIfAbsent(entry.getKey().modId, modId -> new HashMap<>())
                  .computeIfAbsent(entry.getKey().owner, owner -> new LongOpenHashSet())
                  .addAll(entry.getValue());
        }
        return modSortedOwnedChunks;
    }

    private static <T extends Comparable<? super T>> void reinstatePersistentChunks(ServerWorld world, TicketType<OwnedTicketInfo<T>> type,
          Map<TicketOwner<T>, LongSet> tickets)
    {
        for (Map.Entry<TicketOwner<T>, LongSet> entry : tickets.entrySet())
        {
            for (long chunk : entry.getValue())
            {
                forceChunk(world, new ChunkPos(chunk), type, entry.getKey(), true);
            }
        }
    }

    public static void writeForgeForcedChunks(CompoundNBT nbt, Map<TicketOwner<BlockPos>, LongSet> blockForcedChunks, Map<TicketOwner<UUID>, LongSet> entityForcedChunks)
    {
        if (!blockForcedChunks.isEmpty() || !entityForcedChunks.isEmpty())
        {
            //TODO: Some sort of docs that the second part being a map is mainly for purpose of looking up same chunk entries
            // but different other data but the data also contains the long
            Map<String, Long2ObjectMap<CompoundNBT>> forcedEntries = new HashMap<>();
            writeForcedChunkOwners(forcedEntries, blockForcedChunks, "Blocks", Constants.NBT.TAG_COMPOUND, (pos, forcedBlocks) -> forcedBlocks.add(NBTUtil.writeBlockPos(pos)));
            writeForcedChunkOwners(forcedEntries, entityForcedChunks, "Entities", Constants.NBT.TAG_INT_ARRAY, (uuid, forcedEntities) -> forcedEntities.add(NBTUtil.func_240626_a_(uuid)));
            ListNBT forcedChunks = new ListNBT();
            for (Map.Entry<String, Long2ObjectMap<CompoundNBT>> entry : forcedEntries.entrySet())
            {
                CompoundNBT forcedEntry = new CompoundNBT();
                forcedEntry.putString("Mod", entry.getKey());
                ListNBT modForced = new ListNBT();
                modForced.addAll(entry.getValue().values());
                forcedEntry.put("ModForced", modForced);
                forcedChunks.add(forcedEntry);
            }
            nbt.put("ForgeForced", forcedChunks);
        }
    }

    private static <T extends Comparable<? super T>> void writeForcedChunkOwners(Map<String, Long2ObjectMap<CompoundNBT>> forcedEntries,
          Map<TicketOwner<T>, LongSet> forcedChunks, String listKey, int listType, BiConsumer<T, ListNBT> ownerWriter)
    {
        for (Map.Entry<TicketOwner<T>, LongSet> entry : forcedChunks.entrySet())
        {
            Long2ObjectMap<CompoundNBT> modForced = forcedEntries.computeIfAbsent(entry.getKey().modId, modId -> new Long2ObjectOpenHashMap<>());
            for (long chunk : entry.getValue())
            {
                CompoundNBT modEntry = modForced.computeIfAbsent(chunk, chunkPos -> {
                    CompoundNBT baseEntry = new CompoundNBT();
                    baseEntry.putLong("Chunk", chunkPos);
                    return baseEntry;
                });
                ListNBT ownerList = modEntry.getList(listKey, listType);
                ownerWriter.accept(entry.getKey().owner, ownerList);
                //TODO: Some sort of note that we can't just check if our entry already contains a list of listKey because
                // if the type is mismatched somehow then it just returns a new list and to make sure we properly persist
                // we are going to have to set it anyways
                modEntry.put(listKey, ownerList);
            }
        }
    }

    //List{modid, List{ChunkPos, List{BlockPos}, List{UUID}}}
    public static void readForgeForcedChunks(CompoundNBT nbt, Map<TicketOwner<BlockPos>, LongSet> blockForcedChunks, Map<TicketOwner<UUID>, LongSet> entityForcedChunks)
    {
        ListNBT forcedChunks = nbt.getList("ForgeForced", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < forcedChunks.size(); i++)
        {
            CompoundNBT forcedEntry = forcedChunks.getCompound(i);
            String modId = forcedEntry.getString("Mod");
            if (ModList.get().isLoaded(modId))
            {
                ListNBT modForced = forcedEntry.getList("ModForced", Constants.NBT.TAG_COMPOUND);
                for (int j = 0; j < modForced.size(); j++)
                {
                    CompoundNBT modEntry = modForced.getCompound(j);
                    long chunkPos = modEntry.getLong("Chunk");
                    ListNBT forcedBlocks = modEntry.getList("Blocks", Constants.NBT.TAG_COMPOUND);
                    for (int k = 0; k < forcedBlocks.size(); k++)
                    {
                        blockForcedChunks.computeIfAbsent(new TicketOwner<>(modId, NBTUtil.readBlockPos(forcedBlocks.getCompound(k))), owner -> new LongOpenHashSet()).add(chunkPos);
                    }
                    ListNBT forcedEntities = modEntry.getList("Entities", Constants.NBT.TAG_INT_ARRAY);
                    for (int k = 0; k < forcedEntities.size(); k++)
                    {
                        //Based on how vanilla's NBTUtil handles UUIDs
                        int[] rawUUID = forcedEntities.getIntArray(k);
                        if (rawUUID.length == 4)
                        {
                            entityForcedChunks.computeIfAbsent(new TicketOwner<>(modId, UUIDCodec.decodeUUID(rawUUID)), owner -> new LongOpenHashSet()).add(chunkPos);
                        }
                        else
                        {
                            //Something went wrong, this should never happen unless the data gets corrupted, but validate it just in case
                            LOGGER.warn("Found chunk loading data for mod {} with invalid UUID-Array of length {}, but expected length 4 - it will be removed from the world save.", modId, rawUUID.length);
                        }
                    }
                }
            }
            else
            {
                LOGGER.warn("Found chunk loading data for mod {} which is currently not available or active - it will be removed from the world save.", modId);
            }
        }
    }

    @FunctionalInterface
    public interface LoadingCallback
    {
        void validateTickets(ServerWorld world, TicketHelper ticketHelper);
    }

    public static class TicketHelper
    {
        private final Map<BlockPos, LongSet> blockTickets;
        private final Map<UUID, LongSet> entityTickets;
        private final ForcedChunksSaveData saveData;
        private final String modId;

        private TicketHelper(ForcedChunksSaveData saveData, String modId, Map<BlockPos, LongSet> blockTickets, Map<UUID, LongSet> entityTickets)
        {
            this.saveData = saveData;
            this.modId = modId;
            this.blockTickets = blockTickets;
            this.entityTickets = entityTickets;
        }

        //TODO: Note that it is immutable
        public Map<BlockPos, LongSet> getBlockTickets()
        {
            return blockTickets;
        }

        //TODO: Note that it is immutable
        public Map<UUID, LongSet> getEntityTickets()
        {
            return entityTickets;
        }

        public void removeAllTickets(BlockPos owner)
        {
            saveData.getBlockForcedChunks().remove(new TicketOwner<>(modId, owner));
        }

        public void removeAllTickets(UUID owner)
        {
            saveData.getEntityForcedChunks().remove(new TicketOwner<>(modId, owner));
        }

        public void removeTicket(BlockPos owner, long chunk)
        {
            removeTicket(saveData.getBlockForcedChunks(), owner, chunk);
        }

        public void removeTicket(UUID owner, long chunk)
        {
            removeTicket(saveData.getEntityForcedChunks(), owner, chunk);
        }

        private <T extends Comparable<? super T>> void removeTicket(Map<TicketOwner<T>, LongSet> tickets, T owner, long chunk)
        {
            TicketOwner<T> ticketOwner = new TicketOwner<>(modId, owner);
            if (tickets.containsKey(ticketOwner))
            {
                LongSet chunks = tickets.get(ticketOwner);
                if (chunks.remove(chunk))
                {
                    if (chunks.isEmpty())
                    {
                        tickets.remove(ticketOwner);
                    }
                    saveData.setDirty(true);
                }
            }
        }
    }

    public static class TicketOwner<T extends Comparable<? super T>> implements Comparable<TicketOwner<T>>
    {
        private final String modId;
        private final T owner;

        private TicketOwner(String modId, T owner)
        {
            this.modId = modId;
            this.owner = owner;
        }

        @Override
        public int compareTo(TicketOwner<T> other)
        {
            int res = modId.compareTo(other.modId);
            return res == 0 ? owner.compareTo(other.owner) : res;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TicketOwner<?> that = (TicketOwner<?>) o;
            return Objects.equals(modId, that.modId) && Objects.equals(owner, that.owner);
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(modId, owner);
        }
    }

    private static class OwnedTicketInfo<T extends Comparable<? super T>> implements Comparable<OwnedTicketInfo<T>>
    {
        private final ChunkPos chunkPos;
        private final TicketOwner<T> owner;

        public OwnedTicketInfo(TicketOwner<T> owner, ChunkPos chunkPos)
        {
            this.chunkPos = chunkPos;
            this.owner = owner;
        }

        @Override
        public int compareTo(OwnedTicketInfo<T> other)
        {
            int res = Long.compare(chunkPos.asLong(), other.chunkPos.asLong());
            return res == 0 ? owner.compareTo(other.owner) : res;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            OwnedTicketInfo<?> other = (OwnedTicketInfo<?>) o;
            return chunkPos.equals(other.chunkPos) && owner.equals(other.owner);
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(chunkPos, owner);
        }
    }
}