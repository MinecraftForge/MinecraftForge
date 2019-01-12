/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.common;

import java.io.*;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTSizeTracker;
import net.minecraftforge.versions.forge.ForgeVersion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.storage.ThreadedFileIOBase;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;

/**
 * Manages chunkloading for mods.
 *
 * The basic principle is a ticket based system.
 * 1. Mods register a callback {@link #setForcedChunkLoadingCallback(Object, LoadingCallback)}
 * 2. Mods ask for a ticket {@link #requestTicket(Object, World, Type)} and then hold on to that ticket.
 * 3. Mods request chunks to stay loaded {@link #forceChunk(Ticket, ChunkPos)} or remove chunks from force loading {@link #unforceChunk(Ticket, ChunkPos)}.
 * 4. When a world unloads, the tickets associated with that world are saved by the chunk manager.
 * 5. When a world loads, saved tickets are offered to the mods associated with the tickets. The {@link Ticket#getModData()} that is set by the mod should be used to re-register
 * chunks to stay loaded (and maybe take other actions).
 *
 * The chunkloading is configurable at runtime. The file "config/forgeChunkLoading.cfg" contains both default configuration for chunkloading, and a sample individual mod
 * specific override section.
 *
 */
public class ForgeChunkManager
{
    public static Marker CHUNK_MANAGER = MarkerManager.getMarker("CHUNKMANAGER");
    private static Logger LOGGER = LogManager.getLogger();

    private static Map<World, Multimap<String, Ticket>> tickets = new MapMaker().weakKeys().makeMap();

    private static SetMultimap<String, Ticket> playerTickets = HashMultimap.create();

    private static Map<String, LoadingCallback> callbacks = Maps.newHashMap();

    private static Map<World, ImmutableSetMultimap<ChunkPos,Ticket>> forcedChunks = new MapMaker().weakKeys().makeMap();
    private static BiMap<UUID,Ticket> pendingEntities = HashBiMap.create();

    private static Map<World,Cache<Long, ChunkEntry>> dormantChunkCache = new MapMaker().weakKeys().makeMap();

    public static boolean asyncChunkLoading;

    private static Set<String> warnedMods = Sets.newHashSet();

    private static class ChunkEntry
    {
        public final Chunk chunk;
        public final NBTTagCompound nbt;

        public ChunkEntry(Chunk chunk)
        {
            this.chunk = chunk;
            this.nbt = new NBTTagCompound();
        }
    }

    /**
     * All mods requiring chunkloading need to implement this to handle the
     * re-registration of chunk tickets at world loading time
     *
     * @author cpw
     *
     */
    public interface LoadingCallback
    {
        /**
         * Called back when tickets are loaded from the world to allow the
         * mod to re-register the chunks associated with those tickets. The list supplied
         * here is truncated to length prior to use. Tickets unwanted by the
         * mod must be disposed of manually unless the mod is an OrderedLoadingCallback instance
         * in which case, they will have been disposed of by the earlier callback.
         *
         * @param tickets The tickets to re-register. The list is immutable and cannot be manipulated directly. Copy it first.
         * @param world the world
         */
        void ticketsLoaded(List<Ticket> tickets, World world);
    }

    /**
     * This is a special LoadingCallback that can be implemented as well as the
     * LoadingCallback to provide access to additional behaviour.
     * Specifically, this callback will fire prior to Forge dropping excess
     * tickets. Tickets in the returned list are presumed ordered and excess will
     * be truncated from the returned list.
     * This allows the mod to control not only if they actually <em>want</em> a ticket but
     * also their preferred ticket ordering.
     *
     * @author cpw
     *
     */
    public interface OrderedLoadingCallback extends LoadingCallback
    {
        /**
         * Called back when tickets are loaded from the world to allow the
         * mod to decide if it wants the ticket still, and prioritise overflow
         * based on the ticket count.
         * WARNING: You cannot force chunks in this callback, it is strictly for allowing the mod
         * to be more selective in which tickets it wishes to preserve in an overflow situation
         *
         * @param tickets The tickets that you will want to select from. The list is immutable and cannot be manipulated directly. Copy it first.
         * @param world The world
         * @param maxTicketCount The maximum number of tickets that will be allowed.
         * @return A list of the tickets this mod wishes to continue using. This list will be truncated
         * to "maxTicketCount" size after the call returns and then offered to the other callback
         * method
         */
        List<Ticket> ticketsLoaded(List<Ticket> tickets, World world, int maxTicketCount);
    }

    public interface PlayerOrderedLoadingCallback extends LoadingCallback
    {
        /**
         * Called back when tickets are loaded from the world to allow the
         * mod to decide if it wants the ticket still.
         * This is for player bound tickets rather than mod bound tickets. It is here so mods can
         * decide they want to dump all player tickets
         *
         * WARNING: You cannot force chunks in this callback, it is strictly for allowing the mod
         * to be more selective in which tickets it wishes to preserve
         *
         * @param tickets The tickets that you will want to select from. The list is immutable and cannot be manipulated directly. Copy it first.
         * @param world The world
         * @return A list of the tickets this mod wishes to use. This list will subsequently be offered
         * to the main callback for action
         */
        ListMultimap<String, Ticket> playerTicketsLoaded(ListMultimap<String, Ticket> tickets, World world);
    }
    public enum Type
    {

        /**
         * For non-entity registrations
         */
        NORMAL,
        /**
         * For entity registrations
         */
        ENTITY
    }
    public static class Ticket
    {
        private String modId;
        private Type ticketType;
        private LinkedHashSet<ChunkPos> requestedChunks;
        private NBTTagCompound modData;
        public final World world;
        private int maxDepth;
        //private String entityClazz;
        private int entityChunkX;
        private int entityChunkZ;
        private Entity entity;
        private String player;

        Ticket(String modId, Type type, World world)
        {
            this.modId = modId;
            this.ticketType = type;
            this.world = world;
            this.maxDepth = ForgeConfig.CHUNK.chunksPerTicket(modId);
            this.requestedChunks = Sets.newLinkedHashSet();
        }

        Ticket(String modId, Type type, World world, String player)
        {
            this(modId, type, world);
            if (player != null)
            {
                this.player = player;
            }
            else
            {
                LOGGER.error(CHUNK_MANAGER, "Attempt to create a player ticket without a valid player");
                throw new RuntimeException();
            }
        }
        /**
         * The chunk list depth can be manipulated up to the maximal grant allowed for the mod. This value is configurable. Once the maximum is reached,
         * the least recently forced chunk, by original registration time, is removed from the forced chunk list.
         *
         * @param depth The new depth to set
         */
        public void setChunkListDepth(int depth)
        {

            if (depth > maxDepth || (depth <= 0 && maxDepth > 0))
            {
                LOGGER.warn(CHUNK_MANAGER, "The mod {} tried to modify the chunk ticket depth to: {}, its allowed maximum is: {}", modId, depth, maxDepth);
            }
            else
            {
                this.maxDepth = depth;
            }
        }

        /**
         * Gets the current max depth for this ticket.
         * Should be the same as getMaxChunkListDepth()
         * unless setChunkListDepth has been called.
         *
         * @return Current max depth
         */
        public int getChunkListDepth()
        {
            return maxDepth;
        }

        /**
         * Get the maximum chunk depth size
         *
         * @return The maximum chunk depth size
         */
        public int getMaxChunkListDepth()
        {
            return maxDepth;
        }

        /**
         * Bind the entity to the ticket for {@link Type#ENTITY} type tickets. Other types will throw a runtime exception.
         *
         * @param entity The entity to bind
         */
        public void bindEntity(Entity entity)
        {
            if (ticketType!=Type.ENTITY)
            {
                throw new RuntimeException("Cannot bind an entity to a non-entity ticket");
            }
            this.entity = entity;
        }

        /**
         * Retrieve the {@link NBTTagCompound} that stores mod specific data for the chunk ticket.
         * Example data to store would be a TileEntity or Block location. This is persisted with the ticket and
         * provided to the {@link LoadingCallback} for the mod. It is recommended to use this to recover
         * useful state information for the forced chunks.
         *
         * @return The custom compound tag for mods to store additional chunkloading data
         */
        public NBTTagCompound getModData()
        {
            if (this.modData == null)
            {
                this.modData = new NBTTagCompound();
            }
            return modData;
        }

        /**
         * Get the entity associated with this {@link Type#ENTITY} type ticket
         * @return the entity
         */
        public Entity getEntity()
        {
            return entity;
        }

        /**
         * Is this a player associated ticket rather than a mod associated ticket?
         */
        public boolean isPlayerTicket()
        {
            return player != null;
        }

        /**
         * Get the player associated with this ticket
         */
        public String getPlayerName()
        {
            return player;
        }

        /**
         * Get the associated mod id
         */
        public String getModId()
        {
            return modId;
        }

        /**
         * Gets the ticket type
         */
        public Type getType()
        {
            return ticketType;
        }

        /**
         * Gets a list of requested chunks for this ticket.
         */
        public ImmutableSet<ChunkPos> getChunkList()
        {
            return ImmutableSet.copyOf(requestedChunks);
        }
    }

    public static class ForceChunkEvent extends net.minecraftforge.eventbus.api.Event
    {
        private final Ticket ticket;
        private final ChunkPos location;

        public ForceChunkEvent(Ticket ticket, ChunkPos location)
        {
            this.ticket = ticket;
            this.location = location;
        }

        public Ticket getTicket()
        {
            return ticket;
        }

        public ChunkPos getLocation()
        {
            return location;
        }
    }

    public static class UnforceChunkEvent extends net.minecraftforge.eventbus.api.Event
    {
        private final Ticket ticket;
        private final ChunkPos location;

        public UnforceChunkEvent(Ticket ticket, ChunkPos location)
        {
            this.ticket = ticket;
            this.location = location;
        }

        public Ticket getTicket()
        {
            return ticket;
        }

        public ChunkPos getLocation()
        {
            return location;
        }
    }


    /**
     * Allows dynamically loading world mods to test if there are chunk tickets in the world
     * Mods that add dynamically generated worlds (like Mystcraft) should call this method
     * to determine if the world should be loaded during server starting.
     *
     * @param chunkDir The chunk directory to test: should be equivalent to {@link WorldServer#getChunkSaveLocation()}
     * @return if there are tickets outstanding for this world or not
     */
    public static boolean savedWorldHasForcedChunkTickets(File chunkDir)
    {
        File chunkLoaderData = new File(chunkDir, "forcedchunks.dat");

        if (chunkLoaderData.exists() && chunkLoaderData.isFile())
        {
            ;
            try
            {
                NBTTagCompound forcedChunkData = CompressedStreamTools.read(chunkLoaderData);
                return forcedChunkData.getList("TicketList", Constants.NBT.TAG_COMPOUND).size() > 0;
            }
            catch (IOException e)
            {
            }
        }
        return false;
    }

    static void loadWorld(IWorld iworld)
    {
        if (!(iworld instanceof World)) return;
        World world = (World)iworld;

        ArrayListMultimap<String, Ticket> newTickets = ArrayListMultimap.create();
        tickets.put(world, newTickets);

        forcedChunks.put(world, ImmutableSetMultimap.of());

        if (!(world instanceof WorldServer))
        {
            return;
        }

        if (ForgeConfig.CHUNK.dormantChunkCacheSize() != 0)
        { // only put into cache if we're using dormant chunk caching
            dormantChunkCache.put(world, CacheBuilder.newBuilder().maximumSize(ForgeConfig.CHUNK.dormantChunkCacheSize()).build());
        }
        WorldServer worldServer = (WorldServer) world;
        File chunkDir = worldServer.getChunkSaveLocation();
        File chunkLoaderData = new File(chunkDir, "forcedchunks.dat");

        if (chunkLoaderData.exists() && chunkLoaderData.isFile())
        {
            ArrayListMultimap<String, Ticket> loadedTickets = ArrayListMultimap.create();
            Map<String,ListMultimap<String,Ticket>> playerLoadedTickets = Maps.newHashMap();
            NBTTagCompound forcedChunkData;
            try (DataInputStream datainputstream = new DataInputStream(new FileInputStream(chunkLoaderData)))
            {
                forcedChunkData = CompressedStreamTools.read(datainputstream, NBTSizeTracker.INFINITE);
            }
            catch (IOException e)
            {
                LOGGER.warn(CHUNK_MANAGER, "Unable to read forced chunk data at {} - it will be ignored", chunkLoaderData.getAbsolutePath(), e);
                return;
            }
            NBTTagList ticketList = forcedChunkData.getList("TicketList", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < ticketList.size(); i++)
            {
                NBTTagCompound ticketHolder = ticketList.getCompound(i);
                String modId = ticketHolder.getString("Owner");
                boolean isPlayer = ForgeVersion.MOD_ID.equals(modId);

                if (!isPlayer && !ModList.get().isLoaded(modId))
                {
                    LOGGER.warn(CHUNK_MANAGER, "Found chunkloading data for mod {} which is currently not available or active - it will be removed from the world save", modId);
                    continue;
                }

                if (!isPlayer && !callbacks.containsKey(modId))
                {
                    LOGGER.warn(CHUNK_MANAGER, "The mod {} has registered persistent chunkloading data but doesn't seem to want to be called back with it - it will be removed from the world save", modId);
                    continue;
                }

                NBTTagList tickets = ticketHolder.getList("Tickets", Constants.NBT.TAG_COMPOUND);
                for (int j = 0; j < tickets.size(); j++)
                {
                    NBTTagCompound ticket = tickets.getCompound(j);
                    modId = ticket.hasKey("ModId") ? ticket.getString("ModId") : modId;
                    Type type = Type.values()[ticket.getByte("Type")];
                    //byte ticketChunkDepth = ticket.getByte("ChunkListDepth");
                    Ticket tick = new Ticket(modId, type, world);
                    if (ticket.hasKey("ModData"))
                    {
                        tick.modData = ticket.getCompound("ModData");
                    }
                    if (ticket.hasKey("Player"))
                    {
                        tick.player = ticket.getString("Player");
                        if (!playerLoadedTickets.containsKey(tick.modId))
                        {
                            playerLoadedTickets.put(modId, ArrayListMultimap.create());
                        }
                        playerLoadedTickets.get(tick.modId).put(tick.player, tick);
                    }
                    else
                    {
                        loadedTickets.put(modId, tick);
                    }
                    if (type == Type.ENTITY)
                    {
                        tick.entityChunkX = ticket.getInt("chunkX");
                        tick.entityChunkZ = ticket.getInt("chunkZ");
                        UUID uuid = new UUID(ticket.getLong("PersistentIDMSB"), ticket.getLong("PersistentIDLSB"));
                        // add the ticket to the "pending entity" list
                        pendingEntities.put(uuid, tick);
                    }
                }
            }

            for (Ticket tick : ImmutableSet.copyOf(pendingEntities.values()))
            {
                if (tick.ticketType == Type.ENTITY && tick.entity == null)
                {
                    // force the world to load the entity's chunk
                    // the load will come back through the loadEntity method and attach the entity
                    // to the ticket
                    world.getChunk(tick.entityChunkX, tick.entityChunkZ);
                }
            }
            for (Ticket tick : ImmutableSet.copyOf(pendingEntities.values()))
            {
                if (tick.ticketType == Type.ENTITY && tick.entity == null)
                {
                    LOGGER.warn(CHUNK_MANAGER, "Failed to load persistent chunkloading entity {} from store.", pendingEntities.inverse().get(tick));
                    loadedTickets.remove(tick.modId, tick);
                }
            }
            pendingEntities.clear();
            // send callbacks
            for (String modId : loadedTickets.keySet())
            {
                LoadingCallback loadingCallback = callbacks.get(modId);
                if (loadingCallback == null)
                {
                    continue;
                }
                int maxTicketLength = ForgeConfig.CHUNK.maxTickets(modId);
                List<Ticket> tickets = loadedTickets.get(modId);
                if (loadingCallback instanceof OrderedLoadingCallback)
                {
                    OrderedLoadingCallback orderedLoadingCallback = (OrderedLoadingCallback) loadingCallback;
                    tickets = orderedLoadingCallback.ticketsLoaded(ImmutableList.copyOf(tickets), world, maxTicketLength);
                }
                if (tickets.size() > maxTicketLength)
                {
                    LOGGER.warn(CHUNK_MANAGER, "The mod {} has too many open chunkloading tickets {}. Excess will be dropped", modId, tickets.size());
                    tickets.subList(maxTicketLength, tickets.size()).clear();
                }
                ForgeChunkManager.tickets.get(world).putAll(modId, tickets);
                loadingCallback.ticketsLoaded(ImmutableList.copyOf(tickets), world);
            }
            for (String modId : playerLoadedTickets.keySet())
            {
                LoadingCallback loadingCallback = callbacks.get(modId);
                if (loadingCallback == null)
                {
                    continue;
                }
                ListMultimap<String,Ticket> tickets = playerLoadedTickets.get(modId);
                if (loadingCallback instanceof PlayerOrderedLoadingCallback)
                {
                    PlayerOrderedLoadingCallback orderedLoadingCallback = (PlayerOrderedLoadingCallback) loadingCallback;
                    tickets = orderedLoadingCallback.playerTicketsLoaded(ImmutableListMultimap.copyOf(tickets), world);
                    playerTickets.putAll(tickets);
                }
                ForgeChunkManager.tickets.get(world).putAll(ForgeVersion.MOD_ID, tickets.values());
                loadingCallback.ticketsLoaded(ImmutableList.copyOf(tickets.values()), world);
            }
        }
    }

    static void unloadWorld(IWorld iworld)
    {
        if (!(iworld instanceof World)) return;
        World world = (World)iworld;
        // World save fires before this event so the chunk loading info will be done
        if (!(world instanceof WorldServer))
        {
            return;
        }

        forcedChunks.remove(world);
        if (ForgeConfig.CHUNK.dormantChunkCacheSize() != 0) // only if in use
        {
            dormantChunkCache.remove(world);
        }
        // integrated server is shutting down
        if (!ServerLifecycleHooks.getCurrentServer().isServerRunning())
        {
            playerTickets.clear();
            tickets.clear();
        }
    }

    /**
     * Set a chunkloading callback for the supplied mod object
     *
     * @param mod  The mod instance registering the callback
     * @param callback The code to call back when forced chunks are loaded
     */
    public static void setForcedChunkLoadingCallback(Object mod, LoadingCallback callback)
    {
        ModContainer container = getContainer(mod);
        if (container == null)
        {
            LOGGER.warn(CHUNK_MANAGER, "Unable to register a callback for an unknown mod {} ({} : {})", mod, mod.getClass().getName(), Integer.toHexString(System.identityHashCode(mod)));
            return;
        }

        callbacks.put(container.getModId(), callback);
    }

    /**
     * Discover the available tickets for the mod in the world
     *
     * @param mod The mod that will own the tickets
     * @param world The world
     * @return The count of tickets left for the mod in the supplied world
     */
    public static int ticketCountAvailableFor(Object mod, World world)
    {
        ModContainer container = getContainer(mod);
        if (container!=null)
        {
            String modId = container.getModId();
            int allowedCount = ForgeConfig.CHUNK.maxTickets(modId);
            return allowedCount - tickets.get(world).get(modId).size();
        }
        else
        {
            return 0;
        }
    }

    private static ModContainer getContainer(Object mod)
    {
        ModContainer container = ModList.get().getModContainerByObject(mod).orElse(null);
        return container;
    }

    public static int ticketCountAvailableFor(String username)
    {
        return ForgeConfig.CHUNK.playerTicketCount() - playerTickets.get(username).size();
    }

    @Nullable
    public static Ticket requestPlayerTicket(Object mod, String player, World world, Type type)
    {
        ModContainer mc = getContainer(mod);
        if (mc == null)
        {
            LOGGER.error(CHUNK_MANAGER, "Failed to locate the container for mod instance {} ({} : {})", mod, mod.getClass().getName(), Integer.toHexString(System.identityHashCode(mod)));
            return null;
        }
        if (playerTickets.get(player).size() > ForgeConfig.CHUNK.playerTicketCount())
        {
            LOGGER.warn(CHUNK_MANAGER, "Unable to assign further chunkloading tickets to player {} (on behalf of mod {})", player, mc.getModId());
            return null;
        }
        Ticket ticket = new Ticket(mc.getModId(),type,world,player);
        playerTickets.put(player, ticket);
        tickets.get(world).put(ForgeVersion.MOD_ID, ticket);
        return ticket;
    }
    /**
     * Request a chunkloading ticket of the appropriate type for the supplied mod
     *
     * @param mod The mod requesting a ticket
     * @param world The world in which it is requesting the ticket
     * @param type The type of ticket
     * @return A ticket with which to register chunks for loading, or null if no further tickets are available
     */
    @Nullable
    public static Ticket requestTicket(Object mod, World world, Type type)
    {
        ModContainer container = getContainer(mod);
        if (container == null)
        {
            LOGGER.error(CHUNK_MANAGER, "Failed to locate the container for mod instance {} ({} : {})", mod, mod.getClass().getName(), Integer.toHexString(System.identityHashCode(mod)));
            return null;
        }
        String modId = container.getModId();
        if (!callbacks.containsKey(modId))
        {
            LOGGER.fatal(CHUNK_MANAGER, "The mod {} has attempted to request a ticket without a listener in place", modId);
            throw new RuntimeException("Invalid ticket request");
        }

        int allowedCount = ForgeConfig.CHUNK.maxTickets(modId);

        if (tickets.get(world).get(modId).size() >= allowedCount)
        {
            if (!warnedMods.contains(modId))
            {
                LOGGER.info(CHUNK_MANAGER, "The mod {} has attempted to allocate a chunkloading ticket beyond it's currently allocated maximum: {}", modId, allowedCount);
                warnedMods.add(modId);
            }
            return null;
        }
        Ticket ticket = new Ticket(modId, type, world);
        tickets.get(world).put(modId, ticket);

        return ticket;
    }

    /**
     * Release the ticket back to the system. This will also unforce any chunks held by the ticket so that they can be unloaded and/or stop ticking.
     *
     * @param ticket The ticket to release
     */
    public static void releaseTicket(Ticket ticket)
    {
        if (ticket == null)
        {
            return;
        }
        if (ticket.isPlayerTicket() ? !playerTickets.containsValue(ticket) : !tickets.get(ticket.world).containsEntry(ticket.modId, ticket))
        {
            return;
        }
        if (ticket.requestedChunks!=null)
        {
            for (ChunkPos chunk : ImmutableSet.copyOf(ticket.requestedChunks))
            {
                unforceChunk(ticket, chunk);
            }
        }
        if (ticket.isPlayerTicket())
        {
            playerTickets.remove(ticket.player, ticket);
            tickets.get(ticket.world).remove(ForgeVersion.MOD_ID, ticket);
        }
        else
        {
            tickets.get(ticket.world).remove(ticket.modId, ticket);
        }
    }

    /**
     * Force the supplied chunk coordinate to be loaded by the supplied ticket. If the ticket's {@link Ticket#maxDepth} is exceeded, the least
     * recently registered chunk is unforced and may be unloaded.
     * It is safe to force the chunk several times for a ticket, it will not generate duplication or change the ordering.
     *
     * @param ticket The ticket registering the chunk
     * @param chunk The chunk to force
     */
    public static void forceChunk(Ticket ticket, ChunkPos chunk)
    {
        if (ticket == null || chunk == null)
        {
            return;
        }
        if (ticket.ticketType == Type.ENTITY && ticket.entity == null)
        {
            throw new RuntimeException("Attempted to use an entity ticket to force a chunk, without an entity");
        }
        if (ticket.isPlayerTicket() ? !playerTickets.containsValue(ticket) : !tickets.get(ticket.world).containsEntry(ticket.modId, ticket))
        {
            LOGGER.fatal(CHUNK_MANAGER, "The mod {} attempted to force load a chunk with an invalid ticket. This is not permitted.", ticket.modId);
            return;
        }
        ticket.requestedChunks.add(chunk);
        MinecraftForge.EVENT_BUS.post(new ForceChunkEvent(ticket, chunk));

        ImmutableSetMultimap<ChunkPos, Ticket> newMap = ImmutableSetMultimap.<ChunkPos,Ticket>builder().putAll(forcedChunks.get(ticket.world)).put(chunk, ticket).build();
        forcedChunks.put(ticket.world, newMap);
        if (ticket.maxDepth > 0 && ticket.requestedChunks.size() > ticket.maxDepth)
        {
            ChunkPos removed = ticket.requestedChunks.iterator().next();
            unforceChunk(ticket,removed);
        }
    }

    /**
     * Reorganize the internal chunk list so that the chunk supplied is at the *end* of the list
     * This helps if you wish to guarantee a certain "automatic unload ordering" for the chunks
     * in the ticket list
     *
     * @param ticket The ticket holding the chunk list
     * @param chunk The chunk you wish to push to the end (so that it would be unloaded last)
     */
    public static void reorderChunk(Ticket ticket, ChunkPos chunk)
    {
        if (ticket == null || chunk == null || !ticket.requestedChunks.contains(chunk))
        {
            return;
        }
        ticket.requestedChunks.remove(chunk);
        ticket.requestedChunks.add(chunk);
    }
    /**
     * Unforce the supplied chunk, allowing it to be unloaded and stop ticking.
     *
     * @param ticket The ticket holding the chunk
     * @param chunk The chunk to unforce
     */
    public static void unforceChunk(Ticket ticket, ChunkPos chunk)
    {
        if (ticket == null || chunk == null)
        {
            return;
        }
        ticket.requestedChunks.remove(chunk);
        MinecraftForge.EVENT_BUS.post(new UnforceChunkEvent(ticket, chunk));
        LinkedHashMultimap<ChunkPos, Ticket> copy = LinkedHashMultimap.create(forcedChunks.get(ticket.world));
        copy.remove(chunk, ticket);
        ImmutableSetMultimap<ChunkPos, Ticket> newMap = ImmutableSetMultimap.copyOf(copy);
        forcedChunks.put(ticket.world,newMap);
    }

    /**
     * The list of persistent chunks in the world. This set is immutable.
     * @param world
     * @return the list of persistent chunks in the world
     */
    public static ImmutableSetMultimap<ChunkPos, Ticket> getPersistentChunksFor(World world)
    {
        return forcedChunks.containsKey(world) ? forcedChunks.get(world) : ImmutableSetMultimap.of();
    }

    static void saveWorld(IWorld iworld)
    {
        if (!(iworld instanceof World)) return;
        World world = (World)iworld;
        // only persist persistent worlds
        if (!(world instanceof WorldServer))
        {
            return;
        }
        WorldServer worldServer = (WorldServer) world;
        File chunkDir = worldServer.getChunkSaveLocation();
        File chunkLoaderData = new File(chunkDir, "forcedchunks.dat");

        NBTTagCompound forcedChunkData = new NBTTagCompound();
        NBTTagList ticketList = new NBTTagList();
        forcedChunkData.setTag("TicketList", ticketList);

        Multimap<String, Ticket> ticketSet = tickets.get(worldServer);
        if (ticketSet == null) return;
        for (String modId : ticketSet.keySet())
        {
            NBTTagCompound ticketHolder = new NBTTagCompound();
            ticketList.add(ticketHolder);

            ticketHolder.setString("Owner", modId);
            NBTTagList tickets = new NBTTagList();
            ticketHolder.setTag("Tickets", tickets);

            for (Ticket tick : ticketSet.get(modId))
            {
                NBTTagCompound ticket = new NBTTagCompound();
                ticket.setByte("Type", (byte) tick.ticketType.ordinal());
                ticket.setByte("ChunkListDepth", (byte) tick.maxDepth);
                if (tick.isPlayerTicket())
                {
                    ticket.setString("ModId", tick.modId);
                    ticket.setString("Player", tick.player);
                }
                if (tick.modData != null)
                {
                    ticket.setTag("ModData", tick.modData);
                }
                if (tick.ticketType == Type.ENTITY && tick.entity != null && tick.entity.writeUnlessPassenger(new NBTTagCompound()))
                {
                    ticket.setInt("chunkX", MathHelper.floor(tick.entity.chunkCoordX));
                    ticket.setInt("chunkZ", MathHelper.floor(tick.entity.chunkCoordZ));
                    ticket.setLong("PersistentIDMSB", tick.entity.getUniqueID().getMostSignificantBits());
                    ticket.setLong("PersistentIDLSB", tick.entity.getUniqueID().getLeastSignificantBits());
                    tickets.add(ticket);
                }
                else if (tick.ticketType != Type.ENTITY)
                {
                    tickets.add(ticket);
                }
            }
        }

        // Write the actual file on the IO thread rather than blocking the server thread
        ThreadedFileIOBase.getThreadedIOInstance().queueIO(() -> {
            try (DataOutputStream dataoutputstream = new DataOutputStream(new FileOutputStream(chunkLoaderData))) {
                CompressedStreamTools.write(forcedChunkData, dataoutputstream);
            }
            catch (IOException e)
            {
                LOGGER.warn(CHUNK_MANAGER, "Unable to write forced chunk data to {} - chunkloading won't work", chunkLoaderData.getAbsolutePath(), e);
            }
            return false;
        });
    }

    static void loadEntity(Entity entity)
    {
        UUID id = entity.getUniqueID();
        Ticket tick = pendingEntities.get(id);
        if (tick != null)
        {
            tick.bindEntity(entity);
            pendingEntities.remove(id);
        }
    }

    public static void putDormantChunk(long coords, Chunk chunk)
    {
        if (ForgeConfig.CHUNK.dormantChunkCacheSize() == 0) return; // Skip if we're not dormant caching chunks
        Cache<Long, ChunkEntry> cache = dormantChunkCache.get(chunk.getWorld());
        if (cache != null)
        {
            cache.put(coords, new ChunkEntry(chunk));
        }
    }

    public static void storeChunkNBT(World world, IChunk ichunk, NBTTagCompound nbt)
    {
        if (ForgeConfig.CHUNK.dormantChunkCacheSize() == 0) return;

        Cache<Long, ChunkEntry> cache = dormantChunkCache.get(world);
        if (cache == null) return;

        ChunkEntry entry = cache.getIfPresent(ichunk.getPos().asLong());
        if (entry != null)
        {
            entry.nbt.setTag("Entities", nbt.getList("Entities", Constants.NBT.TAG_COMPOUND));
            entry.nbt.setTag("TileEntities", nbt.getList("TileEntities", Constants.NBT.TAG_COMPOUND));

            if (ichunk instanceof Chunk)
            {
                Chunk chunk = (Chunk)ichunk;
                ClassInheritanceMultiMap<Entity>[] entityLists = chunk.getEntityLists();
                for (int i = 0; i < entityLists.length; ++i)
                {
                    entityLists[i] = new ClassInheritanceMultiMap<>(Entity.class);
                }
                chunk.getTileEntityMap().clear();
            }
        }
    }

    @Nullable
    public static Chunk fetchDormantChunk(long coords, World world)
    {
        if (ForgeConfig.CHUNK.dormantChunkCacheSize() == 0) return null; // Don't bother with maps at all if its never gonna get a response

        Cache<Long, ChunkEntry> cache = dormantChunkCache.get(world);
        if (cache == null) return null;

        ChunkEntry entry = cache.getIfPresent(coords);
        if (entry == null) return null;

        loadChunkEntities(entry.chunk, entry.nbt, world);

        cache.invalidate(coords);
        return entry.chunk;
    }

    private static void loadChunkEntities(Chunk chunk, NBTTagCompound nbt, World world)
    {
        NBTTagList entities = nbt.getList("Entities", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < entities.size(); ++i)
        {
            AnvilChunkLoader.readChunkEntity(entities.getCompound(i), world, chunk);
            chunk.setHasEntities(true);
        }

        NBTTagList tileEntities = nbt.getList("TileEntities", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < tileEntities.size(); ++i)
        {
            TileEntity tileEntity = TileEntity.create(tileEntities.getCompound(i));
            if (tileEntity != null) chunk.addTileEntity(tileEntity);
        }
    }
}
