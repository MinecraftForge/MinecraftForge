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
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
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
    private static final TicketType<TicketOwner<BlockPos>> BLOCK = TicketType.create("forge:block", Comparator.comparing(info -> info));
    private static final TicketType<TicketOwner<UUID>> ENTITY = TicketType.create("forge:entity", Comparator.comparing(info -> info));
    private static final Map<String, LoadingValidationCallback> callbacks = new HashMap<>();

    /**
     * Sets the forced chunk loading validation callback for the given mod. This allows for validating and removing no longer valid tickets on world load.
     *
     * @apiNote This method should be called from a {@link net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent} using one of the {@link
     * net.minecraftforge.fml.event.lifecycle.ParallelDispatchEvent} enqueueWork methods.
     */
    public static void setForcedChunkLoadingCallback(String modId, LoadingValidationCallback callback)
    {
        if (ModList.get().isLoaded(modId))
            callbacks.put(modId, callback);
        else
            LOGGER.warn("A mod attempted to set the forced chunk validation loading callback for an unloaded mod of id: {}", modId);
    }

    /**
     * Forces a chunk to be loaded for the given mod with the "owner" of the ticket being a given block position.
     *
     * @param add {@code true} to force the chunk, {@code false} to unforce the chunk.
     */
    public static boolean forceChunk(ServerWorld world, String modId, BlockPos owner, int chunkX, int chunkZ, boolean add)
    {
        return forceChunk(world, modId, owner, chunkX, chunkZ, add, BLOCK, ForcedChunksSaveData::getBlockForcedChunks);
    }

    /**
     * Forces a chunk to be loaded for the given mod with the "owner" of the ticket being the UUID of the given entity.
     *
     * @param add {@code true} to force the chunk, {@code false} to unforce the chunk.
     */
    public static boolean forceChunk(ServerWorld world, String modId, Entity owner, int chunkX, int chunkZ, boolean add)
    {
        return forceChunk(world, modId, owner.getUniqueID(), chunkX, chunkZ, add);
    }

    /**
     * Forces a chunk to be loaded for the given mod with the "owner" of the ticket being a given UUID.
     *
     * @param add {@code true} to force the chunk, {@code false} to unforce the chunk.
     */
    public static boolean forceChunk(ServerWorld world, String modId, UUID owner, int chunkX, int chunkZ, boolean add)
    {
        return forceChunk(world, modId, owner, chunkX, chunkZ, add, ENTITY, ForcedChunksSaveData::getEntityForcedChunks);
    }

    /**
     * Forces a chunk to be loaded for the given mod with the given "owner".
     *
     * @param add {@code true} to force the chunk, {@code false} to unforce the chunk.
     *
     * @implNote Based on {@link ServerWorld#forceChunk(int, int, boolean)}
     */
    private static <T extends Comparable<? super T>> boolean forceChunk(ServerWorld world, String modId, T owner, int chunkX, int chunkZ, boolean add,
          TicketType<TicketOwner<T>> type, Function<ForcedChunksSaveData, Map<TicketOwner<T>, LongSet>> ticketGetter)
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

    /**
     * Adds/Removes a ticket from the world's chunk provider with the proper levels to match the forced chunks.
     *
     * @param add {@code true} to force the chunk, {@code false} to unforce the chunk.
     *
     * @implNote We use distance 2 for what we pass, as when using register/releaseTicket the ticket's level is set to 33 - distance and the level that forced chunks use
     * is 31.
     */
    private static <T extends Comparable<? super T>> void forceChunk(ServerWorld world, ChunkPos pos, TicketType<TicketOwner<T>> type, TicketOwner<T> owner, boolean add)
    {
        if (add)
            world.getChunkProvider().registerTicket(type, pos, 2, owner);
        else
            world.getChunkProvider().releaseTicket(type, pos, 2, owner);
    }

    /**
     * Reinstates forge's forced chunks when vanilla initially loads a world and reinstates their forced chunks. This method also will validate all of forge's forced
     * chunks using and registered {@link LoadingValidationCallback}.
     *
     * @apiNote Internal
     */
    public static void reinstatePersistentChunks(ServerWorld world, ForcedChunksSaveData saveData)
    {
        if (!callbacks.isEmpty())
        {
            //If we have any callbacks, gather all owned tickets by modid for both blocks and entities
            Map<String, Map<BlockPos, LongSet>> blockTickets = gatherTicketsByModId(saveData.getBlockForcedChunks());
            Map<String, Map<UUID, LongSet>> entityTickets = gatherTicketsByModId(saveData.getEntityForcedChunks());
            //Fire the callbacks allowing them to remove any tickets they don't want anymore
            for (Map.Entry<String, LoadingValidationCallback> entry : callbacks.entrySet())
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
        }
        //Reinstate the chunks that we want to load
        reinstatePersistentChunks(world, BLOCK, saveData.getBlockForcedChunks());
        reinstatePersistentChunks(world, ENTITY, saveData.getEntityForcedChunks());
    }

    /**
     * Gathers tickets into a mod filtered map for use in providing all tickets a mod has registered to its {@link LoadingValidationCallback}/
     */
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

    /**
     * Adds back any persistent forced chunks to the world's chunk provider.
     */
    private static <T extends Comparable<? super T>> void reinstatePersistentChunks(ServerWorld world, TicketType<TicketOwner<T>> type, Map<TicketOwner<T>, LongSet> tickets)
    {
        for (Map.Entry<TicketOwner<T>, LongSet> entry : tickets.entrySet())
        {
            for (long chunk : entry.getValue())
            {
                forceChunk(world, new ChunkPos(chunk), type, entry.getKey(), true);
            }
        }
    }

    /**
     * Writes the forge forced chunks into the NBT compound. Format is List{modid, List{ChunkPos, List{BlockPos}, List{UUID}}}
     *
     * @apiNote Internal
     */
    public static void writeForgeForcedChunks(CompoundNBT nbt, Map<TicketOwner<BlockPos>, LongSet> blockForcedChunks, Map<TicketOwner<UUID>, LongSet> entityForcedChunks)
    {
        if (!blockForcedChunks.isEmpty() || !entityForcedChunks.isEmpty())
        {
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
                //Note: As getList returns a new list in the case the data is of the wrong type,
                // we need to mimic was vanilla does in various places and put our list back in
                // the CompoundNBT regardless.
                modEntry.put(listKey, ownerList);
            }
        }
    }

    /**
     * Reads the forge forced chunks into the NBT compound. Format is List{modid, List{ChunkPos, List{BlockPos}, List{UUID}}}
     *
     * @apiNote Internal
     */
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
                    for (INBT uuid : forcedEntities)
                    {
                        entityForcedChunks.computeIfAbsent(new TicketOwner<>(modId, NBTUtil.readUniqueId(uuid)), owner -> new LongOpenHashSet()).add(chunkPos);
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
    public interface LoadingValidationCallback {
        /**
         * Called back when tickets are about to be loaded and reinstated to allow mods to invalidate and remove specific tickets that may no longer be valid.
         *
         * @param world        The world
         * @param ticketHelper Ticket helper to remove any invalid tickets.
         */
        void validateTickets(ServerWorld world, TicketHelper ticketHelper);
    }

    /**
     * Class to help mods remove no longer valid tickets.
     */
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

        /**
         * Gets all "BLOCK" tickets this mod had registered and which block positions are forcing which chunks.
         *
         * @apiNote This map is unmodifiable and does not update to reflect removed tickets so it is safe to call the remove methods while iterating it.
         */
        public Map<BlockPos, LongSet> getBlockTickets()
        {
            return blockTickets;
        }

        /**
         * Gets all "ENTITY" tickets this mod had registered and which entity (UUID) is forcing which chunks.
         *
         * @apiNote This map is unmodifiable and does not update to reflect removed tickets so it is safe to call the remove methods while iterating it.
         */
        public Map<UUID, LongSet> getEntityTickets()
        {
            return entityTickets;
        }

        /**
         * Removes all tickets that a given block was responsible for.
         *
         * @param owner Block that was responsible.
         */
        public void removeAllTickets(BlockPos owner)
        {
            saveData.getBlockForcedChunks().remove(new TicketOwner<>(modId, owner));
        }

        /**
         * Removes all tickets that a given entity (UUID) was responsible for.
         *
         * @param owner Entity (UUID) that was responsible.
         */
        public void removeAllTickets(UUID owner)
        {
            saveData.getEntityForcedChunks().remove(new TicketOwner<>(modId, owner));
        }

        /**
         * Removes the ticket for the given chunk that a given block was responsible for.
         *
         * @param owner Block that was responsible.
         * @param chunk Chunk to remove ticket of.
         */
        public void removeTicket(BlockPos owner, long chunk)
        {
            removeTicket(saveData.getBlockForcedChunks(), owner, chunk);
        }

        /**
         * Removes the ticket for the given chunk that a given entity (UUID) was responsible for.
         *
         * @param owner Entity (UUID) that was responsible.
         * @param chunk Chunk to remove ticket of.
         */
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

    /**
     * Helper class to keep track of a ticket owner by modid and owner object
     */
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
}