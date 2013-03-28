package net.minecraftforge.common;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.ForwardingSet;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Multiset;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import com.google.common.collect.TreeMultiset;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.event.Event;

/**
 * Manages chunkloading for mods.
 *
 * The basic principle is a ticket based system.
 * 1. Mods register a callback {@link #setForcedChunkLoadingCallback(Object, LoadingCallback)}
 * 2. Mods ask for a ticket {@link #requestTicket(Object, World, Type)} and then hold on to that ticket.
 * 3. Mods request chunks to stay loaded {@link #forceChunk(Ticket, ChunkCoordIntPair)} or remove chunks from force loading {@link #unforceChunk(Ticket, ChunkCoordIntPair)}.
 * 4. When a world unloads, the tickets associated with that world are saved by the chunk manager.
 * 5. When a world loads, saved tickets are offered to the mods associated with the tickets. The {@link Ticket#getModData()} that is set by the mod should be used to re-register
 * chunks to stay loaded (and maybe take other actions).
 *
 * The chunkloading is configurable at runtime. The file "config/forgeChunkLoading.cfg" contains both default configuration for chunkloading, and a sample individual mod
 * specific override section.
 *
 * @author cpw
 *
 */
public class ForgeChunkManager
{
    private static int defaultMaxCount;
    private static int defaultMaxChunks;
    private static boolean overridesEnabled;

    private static Map<World, Multimap<String, Ticket>> tickets = new MapMaker().weakKeys().makeMap();
    private static Map<String, Integer> ticketConstraints = Maps.newHashMap();
    private static Map<String, Integer> chunkConstraints = Maps.newHashMap();

    private static SetMultimap<String, Ticket> playerTickets = HashMultimap.create();

    private static Map<String, LoadingCallback> callbacks = Maps.newHashMap();

    private static Map<World, ImmutableSetMultimap<ChunkCoordIntPair,Ticket>> forcedChunks = new MapMaker().weakKeys().makeMap();
    private static BiMap<UUID,Ticket> pendingEntities = HashBiMap.create();

    private static Map<World,Cache<Long, Chunk>> dormantChunkCache = new MapMaker().weakKeys().makeMap();

    private static File cfgFile;
    private static Configuration config;
    private static int playerTicketLength;
    private static int dormantChunkCacheSize;

    private static Set<String> warnedMods = Sets.newHashSet();
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
        public void ticketsLoaded(List<Ticket> tickets, World world);
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
        public List<Ticket> ticketsLoaded(List<Ticket> tickets, World world, int maxTicketCount);
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
        public ListMultimap<String, Ticket> playerTicketsLoaded(ListMultimap<String, Ticket> tickets, World world);
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
        private LinkedHashSet<ChunkCoordIntPair> requestedChunks;
        private NBTTagCompound modData;
        public final World world;
        private int maxDepth;
        private String entityClazz;
        private int entityChunkX;
        private int entityChunkZ;
        private Entity entity;
        private String player;

        Ticket(String modId, Type type, World world)
        {
            this.modId = modId;
            this.ticketType = type;
            this.world = world;
            this.maxDepth = getMaxChunkDepthFor(modId);
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
                FMLLog.log(Level.SEVERE, "Attempt to create a player ticket without a valid player");
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
            if (depth > getMaxChunkDepthFor(modId) || (depth <= 0 && getMaxChunkDepthFor(modId) > 0))
            {
                FMLLog.warning("The mod %s tried to modify the chunk ticket depth to: %d, its allowed maximum is: %d", modId, depth, getMaxChunkDepthFor(modId));
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
            return getMaxChunkDepthFor(modId);
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
        public ImmutableSet getChunkList()
        {
            return ImmutableSet.copyOf(requestedChunks);
        }
    }

    public static class ForceChunkEvent extends Event {
        public final Ticket ticket;
        public final ChunkCoordIntPair location;

        public ForceChunkEvent(Ticket ticket, ChunkCoordIntPair location)
        {
            this.ticket = ticket;
            this.location = location;
        }
    }

    public static class UnforceChunkEvent extends Event {
        public final Ticket ticket;
        public final ChunkCoordIntPair location;

        public UnforceChunkEvent(Ticket ticket, ChunkCoordIntPair location)
        {
            this.ticket = ticket;
            this.location = location;
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
                return forcedChunkData.getTagList("TicketList").tagCount() > 0;
            }
            catch (IOException e)
            {
            }
        }
        return false;
    }

    static void loadWorld(World world)
    {
        ArrayListMultimap<String, Ticket> newTickets = ArrayListMultimap.<String, Ticket>create();
        tickets.put(world, newTickets);

        forcedChunks.put(world, ImmutableSetMultimap.<ChunkCoordIntPair,Ticket>of());

        if (!(world instanceof WorldServer))
        {
            return;
        }

        dormantChunkCache.put(world, CacheBuilder.newBuilder().maximumSize(dormantChunkCacheSize).<Long, Chunk>build());
        WorldServer worldServer = (WorldServer) world;
        File chunkDir = worldServer.getChunkSaveLocation();
        File chunkLoaderData = new File(chunkDir, "forcedchunks.dat");

        if (chunkLoaderData.exists() && chunkLoaderData.isFile())
        {
            ArrayListMultimap<String, Ticket> loadedTickets = ArrayListMultimap.<String, Ticket>create();
            Map<String,ListMultimap<String,Ticket>> playerLoadedTickets = Maps.newHashMap();
            NBTTagCompound forcedChunkData;
            try
            {
                forcedChunkData = CompressedStreamTools.read(chunkLoaderData);
            }
            catch (IOException e)
            {
                FMLLog.log(Level.WARNING, e, "Unable to read forced chunk data at %s - it will be ignored", chunkLoaderData.getAbsolutePath());
                return;
            }
            NBTTagList ticketList = forcedChunkData.getTagList("TicketList");
            for (int i = 0; i < ticketList.tagCount(); i++)
            {
                NBTTagCompound ticketHolder = (NBTTagCompound) ticketList.tagAt(i);
                String modId = ticketHolder.getString("Owner");
                boolean isPlayer = "Forge".equals(modId);

                if (!isPlayer && !Loader.isModLoaded(modId))
                {
                    FMLLog.warning("Found chunkloading data for mod %s which is currently not available or active - it will be removed from the world save", modId);
                    continue;
                }

                if (!isPlayer && !callbacks.containsKey(modId))
                {
                    FMLLog.warning("The mod %s has registered persistent chunkloading data but doesn't seem to want to be called back with it - it will be removed from the world save", modId);
                    continue;
                }

                NBTTagList tickets = ticketHolder.getTagList("Tickets");
                for (int j = 0; j < tickets.tagCount(); j++)
                {
                    NBTTagCompound ticket = (NBTTagCompound) tickets.tagAt(j);
                    modId = ticket.hasKey("ModId") ? ticket.getString("ModId") : modId;
                    Type type = Type.values()[ticket.getByte("Type")];
                    byte ticketChunkDepth = ticket.getByte("ChunkListDepth");
                    Ticket tick = new Ticket(modId, type, world);
                    if (ticket.hasKey("ModData"))
                    {
                        tick.modData = ticket.getCompoundTag("ModData");
                    }
                    if (ticket.hasKey("Player"))
                    {
                        tick.player = ticket.getString("Player");
                        if (!playerLoadedTickets.containsKey(tick.modId))
                        {
                            playerLoadedTickets.put(modId, ArrayListMultimap.<String,Ticket>create());
                        }
                        playerLoadedTickets.get(tick.modId).put(tick.player, tick);
                    }
                    else
                    {
                        loadedTickets.put(modId, tick);
                    }
                    if (type == Type.ENTITY)
                    {
                        tick.entityChunkX = ticket.getInteger("chunkX");
                        tick.entityChunkZ = ticket.getInteger("chunkZ");
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
                    world.getChunkFromChunkCoords(tick.entityChunkX, tick.entityChunkZ);
                }
            }
            for (Ticket tick : ImmutableSet.copyOf(pendingEntities.values()))
            {
                if (tick.ticketType == Type.ENTITY && tick.entity == null)
                {
                    FMLLog.warning("Failed to load persistent chunkloading entity %s from store.", pendingEntities.inverse().get(tick));
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
                int maxTicketLength = getMaxTicketLengthFor(modId);
                List<Ticket> tickets = loadedTickets.get(modId);
                if (loadingCallback instanceof OrderedLoadingCallback)
                {
                    OrderedLoadingCallback orderedLoadingCallback = (OrderedLoadingCallback) loadingCallback;
                    tickets = orderedLoadingCallback.ticketsLoaded(ImmutableList.copyOf(tickets), world, maxTicketLength);
                }
                if (tickets.size() > maxTicketLength)
                {
                    FMLLog.warning("The mod %s has too many open chunkloading tickets %d. Excess will be dropped", modId, tickets.size());
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
                ForgeChunkManager.tickets.get(world).putAll("Forge", tickets.values());
                loadingCallback.ticketsLoaded(ImmutableList.copyOf(tickets.values()), world);
            }
        }
    }

    static void unloadWorld(World world)
    {
        // World save fires before this event so the chunk loading info will be done
        if (!(world instanceof WorldServer))
        {
            return;
        }

        forcedChunks.remove(world);
        dormantChunkCache.remove(world);
     // integrated server is shutting down
        if (!MinecraftServer.getServer().isServerRunning())
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
            FMLLog.warning("Unable to register a callback for an unknown mod %s (%s : %x)", mod, mod.getClass().getName(), System.identityHashCode(mod));
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
            int allowedCount = getMaxTicketLengthFor(modId);
            return allowedCount - tickets.get(world).get(modId).size();
        }
        else
        {
            return 0;
        }
    }

    private static ModContainer getContainer(Object mod)
    {
        ModContainer container = Loader.instance().getModObjectList().inverse().get(mod);
        return container;
    }

    public static int getMaxTicketLengthFor(String modId)
    {
        int allowedCount = ticketConstraints.containsKey(modId) && overridesEnabled ? ticketConstraints.get(modId) : defaultMaxCount;
        return allowedCount;
    }

    public static int getMaxChunkDepthFor(String modId)
    {
        int allowedCount = chunkConstraints.containsKey(modId) && overridesEnabled ? chunkConstraints.get(modId) : defaultMaxChunks;
        return allowedCount;
    }

    public static int ticketCountAvailableFor(String username)
    {
        return playerTicketLength - playerTickets.get(username).size();
    }

    public static Ticket requestPlayerTicket(Object mod, String player, World world, Type type)
    {
        ModContainer mc = getContainer(mod);
        if (mc == null)
        {
            FMLLog.log(Level.SEVERE, "Failed to locate the container for mod instance %s (%s : %x)", mod, mod.getClass().getName(), System.identityHashCode(mod));
            return null;
        }
        if (playerTickets.get(player).size()>playerTicketLength)
        {
            FMLLog.warning("Unable to assign further chunkloading tickets to player %s (on behalf of mod %s)", player, mc.getModId());
            return null;
        }
        Ticket ticket = new Ticket(mc.getModId(),type,world,player);
        playerTickets.put(player, ticket);
        tickets.get(world).put("Forge", ticket);
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
    public static Ticket requestTicket(Object mod, World world, Type type)
    {
        ModContainer container = getContainer(mod);
        if (container == null)
        {
            FMLLog.log(Level.SEVERE, "Failed to locate the container for mod instance %s (%s : %x)", mod, mod.getClass().getName(), System.identityHashCode(mod));
            return null;
        }
        String modId = container.getModId();
        if (!callbacks.containsKey(modId))
        {
            FMLLog.severe("The mod %s has attempted to request a ticket without a listener in place", modId);
            throw new RuntimeException("Invalid ticket request");
        }

        int allowedCount = ticketConstraints.containsKey(modId) ? ticketConstraints.get(modId) : defaultMaxCount;

        if (tickets.get(world).get(modId).size() >= allowedCount)
        {
            if (!warnedMods.contains(modId))
            {
                FMLLog.info("The mod %s has attempted to allocate a chunkloading ticket beyond it's currently allocated maximum : %d", modId, allowedCount);
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
            for (ChunkCoordIntPair chunk : ImmutableSet.copyOf(ticket.requestedChunks))
            {
                unforceChunk(ticket, chunk);
            }
        }
        if (ticket.isPlayerTicket())
        {
            playerTickets.remove(ticket.player, ticket);
            tickets.get(ticket.world).remove("Forge",ticket);
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
    public static void forceChunk(Ticket ticket, ChunkCoordIntPair chunk)
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
            FMLLog.severe("The mod %s attempted to force load a chunk with an invalid ticket. This is not permitted.", ticket.modId);
            return;
        }
        ticket.requestedChunks.add(chunk);
        MinecraftForge.EVENT_BUS.post(new ForceChunkEvent(ticket, chunk));

        ImmutableSetMultimap<ChunkCoordIntPair, Ticket> newMap = ImmutableSetMultimap.<ChunkCoordIntPair,Ticket>builder().putAll(forcedChunks.get(ticket.world)).put(chunk, ticket).build();
        forcedChunks.put(ticket.world, newMap);
        if (ticket.maxDepth > 0 && ticket.requestedChunks.size() > ticket.maxDepth)
        {
            ChunkCoordIntPair removed = ticket.requestedChunks.iterator().next();
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
    public static void reorderChunk(Ticket ticket, ChunkCoordIntPair chunk)
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
    public static void unforceChunk(Ticket ticket, ChunkCoordIntPair chunk)
    {
        if (ticket == null || chunk == null)
        {
            return;
        }
        ticket.requestedChunks.remove(chunk);
        MinecraftForge.EVENT_BUS.post(new UnforceChunkEvent(ticket, chunk));
        LinkedHashMultimap<ChunkCoordIntPair, Ticket> copy = LinkedHashMultimap.create(forcedChunks.get(ticket.world));
        copy.remove(chunk, ticket);
        ImmutableSetMultimap<ChunkCoordIntPair, Ticket> newMap = ImmutableSetMultimap.copyOf(copy);
        forcedChunks.put(ticket.world,newMap);
    }

    static void loadConfiguration()
    {
        for (String mod : config.getCategoryNames())
        {
            if (mod.equals("Forge") || mod.equals("defaults"))
            {
                continue;
            }
            Property modTC = config.get(mod, "maximumTicketCount", 200);
            Property modCPT = config.get(mod, "maximumChunksPerTicket", 25);
            ticketConstraints.put(mod, modTC.getInt(200));
            chunkConstraints.put(mod, modCPT.getInt(25));
        }
        if (config.hasChanged())
        {
            config.save();
        }
    }

    /**
     * The list of persistent chunks in the world. This set is immutable.
     * @param world
     * @return the list of persistent chunks in the world
     */
    public static ImmutableSetMultimap<ChunkCoordIntPair, Ticket> getPersistentChunksFor(World world)
    {
        return forcedChunks.containsKey(world) ? forcedChunks.get(world) : ImmutableSetMultimap.<ChunkCoordIntPair,Ticket>of();
    }

    static void saveWorld(World world)
    {
        // only persist persistent worlds
        if (!(world instanceof WorldServer)) { return; }
        WorldServer worldServer = (WorldServer) world;
        File chunkDir = worldServer.getChunkSaveLocation();
        File chunkLoaderData = new File(chunkDir, "forcedchunks.dat");

        NBTTagCompound forcedChunkData = new NBTTagCompound();
        NBTTagList ticketList = new NBTTagList();
        forcedChunkData.setTag("TicketList", ticketList);

        Multimap<String, Ticket> ticketSet = tickets.get(worldServer);
        for (String modId : ticketSet.keySet())
        {
            NBTTagCompound ticketHolder = new NBTTagCompound();
            ticketList.appendTag(ticketHolder);

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
                    ticket.setCompoundTag("ModData", tick.modData);
                }
                if (tick.ticketType == Type.ENTITY && tick.entity != null && tick.entity.addEntityID(new NBTTagCompound()))
                {
                    ticket.setInteger("chunkX", MathHelper.floor_double(tick.entity.chunkCoordX));
                    ticket.setInteger("chunkZ", MathHelper.floor_double(tick.entity.chunkCoordZ));
                    ticket.setLong("PersistentIDMSB", tick.entity.getPersistentID().getMostSignificantBits());
                    ticket.setLong("PersistentIDLSB", tick.entity.getPersistentID().getLeastSignificantBits());
                    tickets.appendTag(ticket);
                }
                else if (tick.ticketType != Type.ENTITY)
                {
                    tickets.appendTag(ticket);
                }
            }
        }
        try
        {
            CompressedStreamTools.write(forcedChunkData, chunkLoaderData);
        }
        catch (IOException e)
        {
            FMLLog.log(Level.WARNING, e, "Unable to write forced chunk data to %s - chunkloading won't work", chunkLoaderData.getAbsolutePath());
            return;
        }
    }

    static void loadEntity(Entity entity)
    {
        UUID id = entity.getPersistentID();
        Ticket tick = pendingEntities.get(id);
        if (tick != null)
        {
            tick.bindEntity(entity);
            pendingEntities.remove(id);
        }
    }

    public static void putDormantChunk(long coords, Chunk chunk)
    {
        Cache<Long, Chunk> cache = dormantChunkCache.get(chunk.worldObj);
        if (cache != null)
        {
            cache.put(coords, chunk);
        }
    }

    public static Chunk fetchDormantChunk(long coords, World world)
    {
        Cache<Long, Chunk> cache = dormantChunkCache.get(world);
        if (cache == null)
        {
            return null;
        }
        Chunk chunk = cache.getIfPresent(coords);
        if (chunk != null)
        {
            for (List<Entity> eList : chunk.entityLists)
            {
                for (Entity e: eList)
                {
                    e.resetEntityId();
                }
            }
        }
        return chunk;
    }

    static void captureConfig(File configDir)
    {
        cfgFile = new File(configDir,"forgeChunkLoading.cfg");
        config = new Configuration(cfgFile, true);
        try
        {
            config.load();
        }
        catch (Exception e)
        {
            File dest = new File(cfgFile.getParentFile(),"forgeChunkLoading.cfg.bak");
            if (dest.exists())
            {
                dest.delete();
            }
            cfgFile.renameTo(dest);
            FMLLog.log(Level.SEVERE, e, "A critical error occured reading the forgeChunkLoading.cfg file, defaults will be used - the invalid file is backed up at forgeChunkLoading.cfg.bak");
        }
        config.addCustomCategoryComment("defaults", "Default configuration for forge chunk loading control");
        Property maxTicketCount = config.get("defaults", "maximumTicketCount", 200);
        maxTicketCount.comment = "The default maximum ticket count for a mod which does not have an override\n" +
                    "in this file. This is the number of chunk loading requests a mod is allowed to make.";
        defaultMaxCount = maxTicketCount.getInt(200);

        Property maxChunks = config.get("defaults", "maximumChunksPerTicket", 25);
        maxChunks.comment = "The default maximum number of chunks a mod can force, per ticket, \n" +
                    "for a mod without an override. This is the maximum number of chunks a single ticket can force.";
        defaultMaxChunks = maxChunks.getInt(25);

        Property playerTicketCount = config.get("defaults", "playerTicketCount", 500);
        playerTicketCount.comment = "The number of tickets a player can be assigned instead of a mod. This is shared across all mods and it is up to the mods to use it.";
        playerTicketLength = playerTicketCount.getInt(500);

        Property dormantChunkCacheSizeProperty = config.get("defaults", "dormantChunkCacheSize", 0);
        dormantChunkCacheSizeProperty.comment = "Unloaded chunks can first be kept in a dormant cache for quicker\n" +
                    "loading times. Specify the size (in chunks) of that cache here";
        dormantChunkCacheSize = dormantChunkCacheSizeProperty.getInt(0);
        FMLLog.info("Configured a dormant chunk cache size of %d", dormantChunkCacheSizeProperty.getInt(0));

        Property modOverridesEnabled = config.get("defaults", "enabled", true);
        modOverridesEnabled.comment = "Are mod overrides enabled?";
        overridesEnabled = modOverridesEnabled.getBoolean(true);

        config.addCustomCategoryComment("Forge", "Sample mod specific control section.\n" +
                "Copy this section and rename the with the modid for the mod you wish to override.\n" +
                "A value of zero in either entry effectively disables any chunkloading capabilities\n" +
                "for that mod");

        Property sampleTC = config.get("Forge", "maximumTicketCount", 200);
        sampleTC.comment = "Maximum ticket count for the mod. Zero disables chunkloading capabilities.";
        sampleTC = config.get("Forge", "maximumChunksPerTicket", 25);
        sampleTC.comment = "Maximum chunks per ticket for the mod.";
        for (String mod : config.getCategoryNames())
        {
            if (mod.equals("Forge") || mod.equals("defaults"))
            {
                continue;
            }
            Property modTC = config.get(mod, "maximumTicketCount", 200);
            Property modCPT = config.get(mod, "maximumChunksPerTicket", 25);
        }
    }


    public static ConfigCategory getConfigFor(Object mod)
    {
        ModContainer container = getContainer(mod);
        if (container != null)
        {
            return config.getCategory(container.getModId());
        }

        return null;
    }

    public static void addConfigProperty(Object mod, String propertyName, String value, Property.Type type)
    {
        ModContainer container = getContainer(mod);
        if (container != null)
        {
            ConfigCategory cat = config.getCategory(container.getModId());
            cat.put(propertyName, new Property(propertyName, value, type));
        }
    }
}
