/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.common.world;

import com.mojang.datafixers.util.Pair;
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
    private static final TicketType<TicketOwner<BlockPos>> BLOCK_TICKING = TicketType.create("forge:block_ticking", Comparator.comparing(info -> info));
    private static final TicketType<TicketOwner<UUID>> ENTITY = TicketType.create("forge:entity", Comparator.comparing(info -> info));
    private static final TicketType<TicketOwner<UUID>> ENTITY_TICKING = TicketType.create("forge:entity_ticking", Comparator.comparing(info -> info));
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
     * Checks if a world has any forced chunks. Mainly used for seeing if a world should continue ticking with no players in it.
     */
    public static boolean hasForcedChunks(ServerWorld world)
    {
        ForcedChunksSaveData data = world.getSavedData().get(ForcedChunksSaveData::new, "chunks");
        if (data == null) return false;
        return !data.getChunks().isEmpty() || !data.getBlockForcedChunks().isEmpty() || !data.getEntityForcedChunks().isEmpty();
    }

    /**
     * Forces a chunk to be loaded for the given mod with the "owner" of the ticket being a given block position.
     *
     * @param add     {@code true} to force the chunk, {@code false} to unforce the chunk.
     * @param ticking {@code true} to make the chunk receive full chunk ticks even if there is no player nearby.
     */
    public static boolean forceChunk(ServerWorld world, String modId, BlockPos owner, int chunkX, int chunkZ, boolean add, boolean ticking)
    {
        return forceChunk(world, modId, owner, chunkX, chunkZ, add, ticking, ticking ? BLOCK_TICKING : BLOCK, ForcedChunksSaveData::getBlockForcedChunks);
    }

    /**
     * Forces a chunk to be loaded for the given mod with the "owner" of the ticket being the UUID of the given entity.
     *
     * @param add     {@code true} to force the chunk, {@code false} to unforce the chunk.
     * @param ticking {@code true} to make the chunk receive full chunk ticks even if there is no player nearby.
     */
    public static boolean forceChunk(ServerWorld world, String modId, Entity owner, int chunkX, int chunkZ, boolean add, boolean ticking)
    {
        return forceChunk(world, modId, owner.getUniqueID(), chunkX, chunkZ, add, ticking);
    }

    /**
     * Forces a chunk to be loaded for the given mod with the "owner" of the ticket being a given UUID.
     *
     * @param add     {@code true} to force the chunk, {@code false} to unforce the chunk.
     * @param ticking {@code true} to make the chunk receive full chunk ticks even if there is no player nearby.
     */
    public static boolean forceChunk(ServerWorld world, String modId, UUID owner, int chunkX, int chunkZ, boolean add, boolean ticking)
    {
        return forceChunk(world, modId, owner, chunkX, chunkZ, add, ticking, ticking ? ENTITY_TICKING : ENTITY, ForcedChunksSaveData::getEntityForcedChunks);
    }

    /**
     * Forces a chunk to be loaded for the given mod with the given "owner".
     *
     * @param add {@code true} to force the chunk, {@code false} to unforce the chunk.
     *
     * @implNote Based on {@link ServerWorld#forceChunk(int, int, boolean)}
     */
    private static <T extends Comparable<? super T>> boolean forceChunk(ServerWorld world, String modId, T owner, int chunkX, int chunkZ, boolean add, boolean ticking,
          TicketType<TicketOwner<T>> type, Function<ForcedChunksSaveData, TicketTracker<T>> ticketGetter)
    {
        if (!ModList.get().isLoaded(modId))
        {
            LOGGER.warn("A mod attempted to force a chunk for an unloaded mod of id: {}", modId);
            return false;
        }
        ForcedChunksSaveData saveData = world.getSavedData().getOrCreate(ForcedChunksSaveData::new, "chunks");
        ChunkPos pos = new ChunkPos(chunkX, chunkZ);
        long chunk = pos.asLong();
        TicketTracker<T> tickets = ticketGetter.apply(saveData);
        TicketOwner<T> ticketOwner = new TicketOwner<>(modId, owner);
        boolean success;
        if (add)
        {
            success = tickets.add(ticketOwner, chunk, ticking);
            if (success)
                world.getChunk(chunkX, chunkZ);
        }
        else
        {
            success = tickets.remove(ticketOwner, chunk, ticking);
        }
        if (success)
        {
            saveData.setDirty(true);
            forceChunk(world, pos, type, ticketOwner, add, ticking);
        }
        return success;
    }

    /**
     * Adds/Removes a ticket from the world's chunk provider with the proper levels to match the forced chunks.
     *
     * @param add     {@code true} to force the chunk, {@code false} to unforce the chunk.
     * @param ticking {@code true} to make the chunk receive full chunk ticks even if there is no player nearby.
     *
     * @implNote We use distance 2 for what we pass, as when using register/releaseTicket the ticket's level is set to 33 - distance and the level that forced chunks use
     * is 31.
     */
    private static <T extends Comparable<? super T>> void forceChunk(ServerWorld world, ChunkPos pos, TicketType<TicketOwner<T>> type, TicketOwner<T> owner, boolean add,
          boolean ticking)
    {
        if (add)
        {
            if (ticking)
                world.getChunkProvider().registerTickingTicket(type, pos, 2, owner);
            else
                world.getChunkProvider().registerTicket(type, pos, 2, owner);
        }
        else if (ticking)
            world.getChunkProvider().releaseTickingTicket(type, pos, 2, owner);
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
            Map<String, Map<BlockPos, Pair<LongSet, LongSet>>> blockTickets = gatherTicketsByModId(saveData.getBlockForcedChunks());
            Map<String, Map<UUID, Pair<LongSet, LongSet>>> entityTickets = gatherTicketsByModId(saveData.getEntityForcedChunks());
            //Fire the callbacks allowing them to remove any tickets they don't want anymore
            for (Map.Entry<String, LoadingValidationCallback> entry : callbacks.entrySet())
            {
                String modId = entry.getKey();
                boolean hasBlockTicket = blockTickets.containsKey(modId);
                boolean hasEntityTicket = entityTickets.containsKey(modId);
                if (hasBlockTicket || hasEntityTicket)
                {
                    Map<BlockPos, Pair<LongSet, LongSet>> ownedBlockTickets = hasBlockTicket ? Collections.unmodifiableMap(blockTickets.get(modId)) : Collections.emptyMap();
                    Map<UUID, Pair<LongSet, LongSet>> ownedEntityTickets = hasEntityTicket ? Collections.unmodifiableMap(entityTickets.get(modId)) : Collections.emptyMap();
                    entry.getValue().validateTickets(world, new TicketHelper(saveData, modId, ownedBlockTickets, ownedEntityTickets));
                }
            }
        }
        //Reinstate the chunks that we want to load
        reinstatePersistentChunks(world, BLOCK, saveData.getBlockForcedChunks().chunks, false);
        reinstatePersistentChunks(world, BLOCK_TICKING, saveData.getBlockForcedChunks().tickingChunks, true);
        reinstatePersistentChunks(world, ENTITY, saveData.getEntityForcedChunks().chunks, false);
        reinstatePersistentChunks(world, ENTITY_TICKING, saveData.getEntityForcedChunks().tickingChunks, true);
    }

    /**
     * Gathers tickets into a mod filtered map for use in providing all tickets a mod has registered to its {@link LoadingValidationCallback}.
     */
    private static <T extends Comparable<? super T>> Map<String, Map<T, Pair<LongSet, LongSet>>> gatherTicketsByModId(TicketTracker<T> tickets)
    {
        Map<String, Map<T, Pair<LongSet, LongSet>>> modSortedOwnedChunks = new HashMap<>();
        gatherTicketsByModId(tickets.chunks, Pair::getFirst, modSortedOwnedChunks);
        gatherTicketsByModId(tickets.tickingChunks, Pair::getSecond, modSortedOwnedChunks);
        return modSortedOwnedChunks;
    }

    /**
     * Gathers tickets into a mod filtered map for use in providing all tickets a mod has registered to its {@link LoadingValidationCallback}.
     */
    private static <T extends Comparable<? super T>> void gatherTicketsByModId(Map<TicketOwner<T>, LongSet> tickets, Function<Pair<LongSet, LongSet>, LongSet> typeGetter,
          Map<String, Map<T, Pair<LongSet, LongSet>>> modSortedOwnedChunks)
    {
        for (Map.Entry<TicketOwner<T>, LongSet> entry : tickets.entrySet())
        {
            Pair<LongSet, LongSet> pair = modSortedOwnedChunks.computeIfAbsent(entry.getKey().modId, modId -> new HashMap<>())
                  .computeIfAbsent(entry.getKey().owner, owner -> new Pair<>(new LongOpenHashSet(), new LongOpenHashSet()));
            typeGetter.apply(pair).addAll(entry.getValue());
        }
    }

    /**
     * Adds back any persistent forced chunks to the world's chunk provider.
     */
    private static <T extends Comparable<? super T>> void reinstatePersistentChunks(ServerWorld world, TicketType<TicketOwner<T>> type,
          Map<TicketOwner<T>, LongSet> tickets, boolean ticking)
    {
        for (Map.Entry<TicketOwner<T>, LongSet> entry : tickets.entrySet())
        {
            for (long chunk : entry.getValue())
            {
                forceChunk(world, new ChunkPos(chunk), type, entry.getKey(), true, ticking);
            }
        }
    }

    /**
     * Writes the forge forced chunks into the NBT compound. Format is List{modid, List{ChunkPos, List{BlockPos}, List{UUID}}}
     *
     * @apiNote Internal
     */
    public static void writeForgeForcedChunks(CompoundNBT nbt, TicketTracker<BlockPos> blockForcedChunks, TicketTracker<UUID> entityForcedChunks)
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

    private static <T extends Comparable<? super T>> void writeForcedChunkOwners(Map<String, Long2ObjectMap<CompoundNBT>> forcedEntries, TicketTracker<T> tracker,
          String listKey, int listType, BiConsumer<T, ListNBT> ownerWriter)
    {
        writeForcedChunkOwners(forcedEntries, tracker.chunks, listKey,listType, ownerWriter);
        writeForcedChunkOwners(forcedEntries, tracker.tickingChunks, "Ticking" + listKey, listType, ownerWriter);
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
     * Reads the forge forced chunks from the NBT compound. Format is List{modid, List{ChunkPos, List{BlockPos}, List{UUID}}}
     *
     * @apiNote Internal
     */
    public static void readForgeForcedChunks(CompoundNBT nbt, TicketTracker<BlockPos> blockForcedChunks, TicketTracker<UUID> entityForcedChunks)
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
                    readBlockForcedChunks(modId, chunkPos, modEntry, "Blocks", blockForcedChunks.chunks);
                    readBlockForcedChunks(modId, chunkPos, modEntry, "TickingBlocks", blockForcedChunks.tickingChunks);
                    readEntityForcedChunks(modId, chunkPos, modEntry, "Entities", entityForcedChunks.chunks);
                    readEntityForcedChunks(modId, chunkPos, modEntry, "TickingEntities", entityForcedChunks.tickingChunks);
                }
            }
            else
            {
                LOGGER.warn("Found chunk loading data for mod {} which is currently not available or active - it will be removed from the world save.", modId);
            }
        }
    }

    /**
     * Reads the forge block forced chunks.
     */
    private static void readBlockForcedChunks(String modId, long chunkPos, CompoundNBT modEntry, String key, Map<TicketOwner<BlockPos>, LongSet> blockForcedChunks)
    {
        ListNBT forcedBlocks = modEntry.getList(key, Constants.NBT.TAG_COMPOUND);
        for (int k = 0; k < forcedBlocks.size(); k++)
        {
            blockForcedChunks.computeIfAbsent(new TicketOwner<>(modId, NBTUtil.readBlockPos(forcedBlocks.getCompound(k))), owner -> new LongOpenHashSet()).add(chunkPos);
        }
    }

    /**
     * Reads the forge entity forced chunks.
     */
    private static void readEntityForcedChunks(String modId, long chunkPos, CompoundNBT modEntry, String key, Map<TicketOwner<UUID>, LongSet> entityForcedChunks)
    {
        ListNBT forcedEntities = modEntry.getList(key, Constants.NBT.TAG_INT_ARRAY);
        for (INBT uuid : forcedEntities)
        {
            entityForcedChunks.computeIfAbsent(new TicketOwner<>(modId, NBTUtil.readUniqueId(uuid)), owner -> new LongOpenHashSet()).add(chunkPos);
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
        private final Map<BlockPos, Pair<LongSet, LongSet>> blockTickets;
        private final Map<UUID, Pair<LongSet, LongSet>> entityTickets;
        private final ForcedChunksSaveData saveData;
        private final String modId;

        private TicketHelper(ForcedChunksSaveData saveData, String modId, Map<BlockPos, Pair<LongSet, LongSet>> blockTickets, Map<UUID, Pair<LongSet, LongSet>> entityTickets)
        {
            this.saveData = saveData;
            this.modId = modId;
            this.blockTickets = blockTickets;
            this.entityTickets = entityTickets;
        }

        /**
         * Gets all "BLOCK" tickets this mod had registered and which block positions are forcing which chunks. First element of the pair is the non-fully ticking
         * tickets, second element is the fully ticking tickets.
         *
         * @apiNote This map is unmodifiable and does not update to reflect removed tickets so it is safe to call the remove methods while iterating it.
         */
        public Map<BlockPos, Pair<LongSet, LongSet>> getBlockTickets()
        {
            return blockTickets;
        }

        /**
         * Gets all "ENTITY" tickets this mod had registered and which entity (UUID) is forcing which chunks. First element of the pair is the non-fully ticking
         * tickets, second element is the fully ticking tickets.
         *
         * @apiNote This map is unmodifiable and does not update to reflect removed tickets so it is safe to call the remove methods while iterating it.
         */
        public Map<UUID, Pair<LongSet, LongSet>> getEntityTickets()
        {
            return entityTickets;
        }

        /**
         * Removes all tickets that a given block was responsible for; both ticking and not ticking.
         *
         * @param owner Block that was responsible.
         */
        public void removeAllTickets(BlockPos owner)
        {
            removeAllTickets(saveData.getBlockForcedChunks(), owner);
        }

        /**
         * Removes all tickets that a given entity (UUID) was responsible for; both ticking and not ticking.
         *
         * @param owner Entity (UUID) that was responsible.
         */
        public void removeAllTickets(UUID owner)
        {
            removeAllTickets(saveData.getEntityForcedChunks(), owner);
        }

        /**
         * Removes all tickets that a given owner was responsible for; both ticking and not ticking.
         */
        private <T extends Comparable<? super T>> void removeAllTickets(TicketTracker<T> tickets, T owner)
        {
            TicketOwner<T> ticketOwner = new TicketOwner<>(modId, owner);
            if (tickets.chunks.containsKey(ticketOwner) || tickets.tickingChunks.containsKey(ticketOwner))
            {
                tickets.chunks.remove(ticketOwner);
                tickets.tickingChunks.remove(ticketOwner);
                saveData.setDirty(true);
            }
        }

        /**
         * Removes the ticket for the given chunk that a given block was responsible for.
         *
         * @param owner   Block that was responsible.
         * @param chunk   Chunk to remove ticket of.
         * @param ticking Whether or not the ticket to remove represents a ticking set of tickets or not.
         */
        public void removeTicket(BlockPos owner, long chunk, boolean ticking)
        {
            removeTicket(saveData.getBlockForcedChunks(), owner, chunk, ticking);
        }

        /**
         * Removes the ticket for the given chunk that a given entity (UUID) was responsible for.
         *
         * @param owner   Entity (UUID) that was responsible.
         * @param chunk   Chunk to remove ticket of.
         * @param ticking Whether or not the ticket to remove represents a ticking set of tickets or not.
         */
        public void removeTicket(UUID owner, long chunk, boolean ticking)
        {
            removeTicket(saveData.getEntityForcedChunks(), owner, chunk, ticking);
        }

        private <T extends Comparable<? super T>> void removeTicket(TicketTracker<T> tickets, T owner, long chunk, boolean ticking)
        {
            if (tickets.remove(new TicketOwner<>(modId, owner), chunk, ticking))
                saveData.setDirty(true);
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

    /**
     * Helper class to manage tracking and handling loaded tickets.
     */
    public static class TicketTracker<T extends Comparable<? super T>>
    {
        private final Map<TicketOwner<T>, LongSet> chunks = new HashMap<>();
        private final Map<TicketOwner<T>, LongSet> tickingChunks = new HashMap<>();

        /**
         * Gets an unmodifiable view of the tracked chunks.
         */
        public Map<TicketOwner<T>, LongSet> getChunks()
        {
            return Collections.unmodifiableMap(chunks);
        }

        /**
         * Gets an unmodifiable view of the tracked fully ticking chunks.
         */
        public Map<TicketOwner<T>, LongSet> getTickingChunks()
        {
            return Collections.unmodifiableMap(tickingChunks);
        }

        /**
         * Checks if this tracker is empty.
         *
         * @return {@code true} if there are no chunks or ticking chunks being tracked.
         */
        public boolean isEmpty()
        {
            return chunks.isEmpty() && tickingChunks.isEmpty();
        }

        private Map<TicketOwner<T>, LongSet> getTickets(boolean ticking)
        {
            return ticking ? tickingChunks : chunks;
        }

        /**
         * @return {@code true} if the state changed.
         */
        private boolean remove(TicketOwner<T> owner, long chunk, boolean ticking)
        {
            Map<TicketOwner<T>, LongSet> tickets = getTickets(ticking);
            if (tickets.containsKey(owner))
            {
                LongSet ticketChunks = tickets.get(owner);
                if (ticketChunks.remove(chunk))
                {
                    if (ticketChunks.isEmpty())
                        tickets.remove(owner);
                    return true;
                }
            }
            return false;
        }

        /**
         * @return {@code true} if the state changed.
         */
        private boolean add(TicketOwner<T> owner, long chunk, boolean ticking)
        {
            return getTickets(ticking).computeIfAbsent(owner, o -> new LongOpenHashSet()).add(chunk);
        }
    }
}