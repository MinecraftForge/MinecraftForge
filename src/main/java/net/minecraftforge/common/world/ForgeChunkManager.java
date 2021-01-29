package net.minecraftforge.common.world;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
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
          TicketType<OwnedTicketInfo<T>> type, Function<ForcedChunksSaveData, Object2LongMap<TicketOwner<T>>> ticketGetter)
    {
        if (!ModList.get().isLoaded(modId))
        {
            LOGGER.warn("A mod attempted to force a chunk for an unloaded mod of id: {}", modId);
            return false;
        }
        ForcedChunksSaveData data = world.getSavedData().getOrCreate(ForcedChunksSaveData::new, "chunks");
        Object2LongMap<TicketOwner<T>> tickets = ticketGetter.apply(data);
        TicketOwner<T> ticketOwner = new TicketOwner<>(modId, owner);
        boolean contains = tickets.containsKey(ticketOwner);
        if (add != contains)
        {
            //If we are adding it doesn't already exist, if we are removing it already exists
            ChunkPos pos = new ChunkPos(chunkX, chunkZ);
            if (add)
            {
                tickets.put(ticketOwner, pos.asLong());
                world.getChunk(chunkX, chunkZ);
            }
            else
            {
                tickets.removeLong(ticketOwner);
            }
            data.setDirty(true);
            forceChunk(world, pos, type, ticketOwner, add);
            return true;
        }
        return false;
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
        reinstatePersistentChunks(world, BLOCK, saveData.getBlockForcedChunks());
        reinstatePersistentChunks(world, ENTITY, saveData.getEntityForcedChunks());
    }

    private static <T extends Comparable<? super T>> void reinstatePersistentChunks(ServerWorld world, TicketType<OwnedTicketInfo<T>> type, Object2LongMap<TicketOwner<T>> tickets)
    {
        for (Object2LongMap.Entry<TicketOwner<T>> entry : tickets.object2LongEntrySet())
        {
            forceChunk(world, new ChunkPos(entry.getLongValue()), type, entry.getKey(), true);
        }
    }

    public static void writeForgeForcedChunks(CompoundNBT nbt, Object2LongMap<TicketOwner<BlockPos>> blockForcedChunks, Object2LongMap<TicketOwner<UUID>> entityForcedChunks)
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
          Object2LongMap<TicketOwner<T>> forcedChunks, String listKey, int listType, BiConsumer<T, ListNBT> ownerWriter)
    {
        for (Object2LongMap.Entry<TicketOwner<T>> entry : forcedChunks.object2LongEntrySet())
        {
            Long2ObjectMap<CompoundNBT> modForced = forcedEntries.computeIfAbsent(entry.getKey().modId, modId -> new Long2ObjectOpenHashMap<>());
            CompoundNBT modEntry = modForced.computeIfAbsent(entry.getLongValue(), chunkPos -> {
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

    //List{modid, List{ChunkPos, List{BlockPos}, List{UUID}}}
    public static void readForgeForcedChunks(CompoundNBT nbt, Object2LongMap<TicketOwner<BlockPos>> blockForcedChunks, Object2LongMap<TicketOwner<UUID>> entityForcedChunks)
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
                        blockForcedChunks.put(new TicketOwner<>(modId, NBTUtil.readBlockPos(forcedBlocks.getCompound(k))), chunkPos);
                    }
                    ListNBT forcedEntities = modEntry.getList("Entities", Constants.NBT.TAG_INT_ARRAY);
                    for (int k = 0; k < forcedEntities.size(); k++)
                    {
                        //Based on how vanilla's NBTUtil handles UUIDs
                        int[] rawUUID = forcedEntities.getIntArray(k);
                        if (rawUUID.length == 4)
                        {
                            entityForcedChunks.put(new TicketOwner<>(modId, UUIDCodec.decodeUUID(rawUUID)), chunkPos);
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