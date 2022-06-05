/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common;

import com.google.common.collect.*;
import com.google.common.graph.ElementOrder;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import com.google.gson.*;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.toposort.TopologicalSort;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class TierSortingRegistry
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ResourceLocation ITEM_TIER_ORDERING_JSON = new ResourceLocation("forge", "item_tier_ordering.json");

    /**
     * Registers a tier into the tier sorting registry.
     * @param tier The tier to register
     * @param name The name to use internally for dependency resolution
     * @param after List of tiers to place this tier after (the tiers in the list will be considered lesser tiers)
     * @param before List of tiers to place this tier before (the tiers in the list will be considered better tiers)
     */
    public static synchronized Tier registerTier(Tier tier, ResourceLocation name, List<Object> after, List<Object> before)
    {
        if (tiers.containsKey(name))
            throw new IllegalStateException("Duplicate tier name " + name);

        processTier(tier, name, after, before);

        hasCustomTiers = true;
        return tier;
    }

    /**
     * Returns the list of tiers in the order defined by the dependencies.
     * This list will remain valid
     * @return An unmodifiable list of tiers ordered lesser to greater
     */
    public static List<Tier> getSortedTiers()
    {
        return sortedTiersUnmodifiable;
    }

    /**
     * Returns the tier associated with a name, if registered into the sorting system.
     * @param name The name to look up
     * @return The tier, or null if not registered
     */
    @Nullable
    public static Tier byName(ResourceLocation name)
    {
        return tiers.get(name);
    }

    /**
     * Returns the name associated with a tier, if the tier is registered into the sorting system.
     * @param tier The tier to look up
     * @return The name for the tier, or null if not registered
     */
    @Nullable
    public static ResourceLocation getName(Tier tier)
    {
        return tiers.inverse().get(tier);
    }

    /**
     * Queries if a tier should be evaluated using the sorting system, by calling isCorrectTierForDrops
     * @param tier The tier to query
     * @return True if isCorrectTierForDrops should be called for the tier
     */
    public static boolean isTierSorted(Tier tier)
    {
        return getName(tier) != null;
    }

    /**
     * Queries if a tier is high enough to be able to get drops for the given blockstate.
     * @param tier The tier to look up
     * @param state The state to test against
     * @return True if the tier is good enough
     */
    public static boolean isCorrectTierForDrops(Tier tier, BlockState state)
    {
        if (!isTierSorted(tier))
            return isCorrectTierVanilla(tier, state);

        for (int x = sortedTiers.indexOf(tier) + 1; x < sortedTiers.size(); x++) {
            TagKey<Block> tag = sortedTiers.get(x).getTag();
            if (tag != null && state.is(tag))
                return false;
        }
        return true;
    }

    /**
     * Helper to query all tiers that are lower than the given tier
     * @param tier The tier
     * @return All the lower tiers
     */
    public static List<Tier> getTiersLowerThan(Tier tier)
    {
        if (!isTierSorted(tier)) return List.of();
        return sortedTiers.stream().takeWhile(t -> t != tier).toList();
    }

    // ===================== PRIVATE INTERNAL STUFFS BELOW THIS LINE =====================

    /**
     * Fallback for when a tier isn't in the registry, copy of the logic in {@link DiggerItem#isCorrectToolForDrops}
     */
    private static boolean isCorrectTierVanilla(Tier tier, BlockState state)
    {
        int i = tier.getLevel();
        if (i < 3 && state.is(BlockTags.NEEDS_DIAMOND_TOOL))
        {
            return false;
        }
        else if (i < 2 && state.is(BlockTags.NEEDS_IRON_TOOL))
        {
            return false;
        }
        else if (i < 1 && state.is(BlockTags.NEEDS_STONE_TOOL))
        {
            return false;
        }
        return true;
    }

    private static void processTier(Tier tier, ResourceLocation name, List<Object> afters, List<Object> befores)
    {
        tiers.put(name, tier);
        for(Object after : afters)
        {
            ResourceLocation other = getTierName(after);
            edges.put(other, name);
        }
        for(Object before : befores)
        {
            ResourceLocation other = getTierName(before);
            edges.put(name, other);
        }
    }

    private static ResourceLocation getTierName(Object entry)
    {
        if (entry instanceof String s)
            return new ResourceLocation(s);
        if (entry instanceof ResourceLocation rl)
            return rl;
        if (entry instanceof Tier t)
            return Objects.requireNonNull(getName(t), "Can't have sorting dependencies for tiers not registered in the TierSortingRegistry");
        throw new IllegalStateException("Invalid object type passed into the tier dependencies " + entry.getClass());
    }

    private static boolean hasCustomTiers = false;
    private static final BiMap<ResourceLocation, Tier> tiers = HashBiMap.create();
    private static final Multimap<ResourceLocation, ResourceLocation> edges = HashMultimap.create();
    private static final Multimap<ResourceLocation, ResourceLocation> vanillaEdges = HashMultimap.create();

    static {
        var wood = new ResourceLocation("wood");
        var stone = new ResourceLocation("stone");
        var iron = new ResourceLocation("iron");
        var diamond = new ResourceLocation("diamond");
        var netherite = new ResourceLocation("netherite");
        var gold = new ResourceLocation("gold");
        processTier(Tiers.WOOD, wood, List.of(), List.of());
        processTier(Tiers.GOLD, gold, List.of(wood), List.of(stone));
        processTier(Tiers.STONE, stone, List.of(wood), List.of(iron));
        processTier(Tiers.IRON, iron, List.of(stone), List.of(diamond));
        processTier(Tiers.DIAMOND, diamond, List.of(iron), List.of(netherite));
        processTier(Tiers.NETHERITE, netherite, List.of(diamond), List.of());
        vanillaEdges.putAll(edges);
    }

    private static final List<Tier> sortedTiers = new ArrayList<>();
    private static final List<Tier> sortedTiersUnmodifiable = Collections.unmodifiableList(sortedTiers);

    private static final ResourceLocation CHANNEL_NAME = new ResourceLocation("forge:tier_sorting");
    private static final String PROTOCOL_VERSION = "1.0";
    private static final SimpleChannel SYNC_CHANNEL = NetworkRegistry.newSimpleChannel(
            CHANNEL_NAME, () -> "1.0",
            versionFromServer -> PROTOCOL_VERSION.equals(versionFromServer) || (allowVanilla() && NetworkRegistry.ACCEPTVANILLA.equals(versionFromServer)),
            versionFromClient -> PROTOCOL_VERSION.equals(versionFromClient) || (allowVanilla() && NetworkRegistry.ACCEPTVANILLA.equals(versionFromClient))
    );

    static boolean allowVanilla()
    {
        return !hasCustomTiers;
    }

    /*package private*/
    static void init()
    {
        SYNC_CHANNEL.registerMessage(0, SyncPacket.class, SyncPacket::encode, TierSortingRegistry::receive, TierSortingRegistry::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        MinecraftForge.EVENT_BUS.addListener(TierSortingRegistry::playerLoggedIn);
        if (FMLEnvironment.dist == Dist.CLIENT) ClientEvents.init();
    }

    /*package private*/
    static PreparableReloadListener getReloadListener()
    {
        return new SimplePreparableReloadListener<JsonObject>()
        {
            final Gson gson = (new GsonBuilder()).create();

            @Nonnull
            @Override
            protected JsonObject prepare(@Nonnull ResourceManager resourceManager, ProfilerFiller p)
            {
                if (!resourceManager.hasResource(ITEM_TIER_ORDERING_JSON))
                    return new JsonObject();

                try (Resource r = resourceManager.getResource(ITEM_TIER_ORDERING_JSON); InputStream stream = r.getInputStream(); Reader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8)))
                {
                    return gson.fromJson(reader, JsonObject.class);
                }
                catch (IOException e)
                {
                    LOGGER.error("Could not read Tier sorting file " + ITEM_TIER_ORDERING_JSON, e);
                    return new JsonObject();
                }
            }

            @Override
            protected void apply(@Nonnull JsonObject data, @Nonnull ResourceManager resourceManager, ProfilerFiller p)
            {
                try
                {
                    if (data.size() > 0)
                    {
                        JsonArray order = GsonHelper.getAsJsonArray(data, "order");
                        List<Tier> customOrder = new ArrayList<>();
                        for (JsonElement entry : order)
                        {
                            ResourceLocation id = new ResourceLocation(entry.getAsString());
                            Tier tier = byName(id);
                            if (tier == null) throw new IllegalStateException("Tier not found with name " + id);
                            customOrder.add(tier);
                        }

                        List<Tier> missingTiers = tiers.values().stream().filter(tier -> !customOrder.contains(tier)).toList();
                        if (missingTiers.size() > 0)
                            throw new IllegalStateException("Tiers missing from the ordered list: " + missingTiers.stream().map(tier -> Objects.toString(TierSortingRegistry.getName(tier))).collect(Collectors.joining(", ")));

                        setTierOrder(customOrder);
                        return;
                    }
                }
                catch(Exception e)
                {
                    LOGGER.error("Error parsing Tier sorting file " + ITEM_TIER_ORDERING_JSON, e);
                }

                recalculateItemTiers();
            }
        };
    }

    @SuppressWarnings("UnstableApiUsage")
    private static void recalculateItemTiers()
    {
        final MutableGraph<Tier> graph = GraphBuilder.directed().nodeOrder(ElementOrder.<Tier>insertion()).build();

        for(Tier tier : tiers.values())
        {
            graph.addNode(tier);
        }
        edges.forEach((key, value) -> {
            if (tiers.containsKey(key) && tiers.containsKey(value))
                graph.putEdge(tiers.get(key), tiers.get(value));
        });
        List<Tier> tierList = TopologicalSort.topologicalSort(graph, null);

        setTierOrder(tierList);
    }

    private static void setTierOrder(List<Tier> tierList)
    {
        runInServerThreadIfPossible(hasServer -> {
            sortedTiers.clear();
            sortedTiers.addAll(tierList);
            if(hasServer) syncToAll();
        });
    }

    private static void runInServerThreadIfPossible(BooleanConsumer runnable)
    {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) server.execute(() -> runnable.accept(true));
        else runnable.accept(false);
    }

    private static void syncToAll()
    {
        for(ServerPlayer serverPlayer : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers())
        {
            syncToPlayer(serverPlayer);
        }
    }

    private static void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
    {
        if (event.getPlayer() instanceof ServerPlayer serverPlayer)
        {
            syncToPlayer(serverPlayer);
        }
    }

    private static void syncToPlayer(ServerPlayer serverPlayer)
    {
        if (SYNC_CHANNEL.isRemotePresent(serverPlayer.connection.getConnection()) && !serverPlayer.connection.getConnection().isMemoryConnection())
        {
            SYNC_CHANNEL.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SyncPacket(sortedTiers.stream().map(TierSortingRegistry::getName).toList()));
        }
    }

    private static SyncPacket receive(FriendlyByteBuf buffer)
    {
        int count = buffer.readVarInt();
        List<ResourceLocation> list = new ArrayList<>();
        for(int i=0;i<count;i++)
            list.add(buffer.readResourceLocation());
        return new SyncPacket(list);
    }

    private static void handle(SyncPacket packet, Supplier<NetworkEvent.Context> context)
    {
        setTierOrder(packet.tiers.stream().map(TierSortingRegistry::byName).toList());
        context.get().setPacketHandled(true);
    }

    private record SyncPacket(List<ResourceLocation> tiers)
    {
        private void encode(FriendlyByteBuf buffer)
        {
            buffer.writeVarInt(tiers.size());
            for(ResourceLocation loc : tiers)
                buffer.writeResourceLocation(loc);
        }
    }

    private static class ClientEvents
    {
        public static void init()
        {
            MinecraftForge.EVENT_BUS.addListener(ClientEvents::clientLogInToServer);
        }

        private static void clientLogInToServer(ClientPlayerNetworkEvent.LoggedInEvent event)
        {
            if (event.getConnection() == null || !event.getConnection().isMemoryConnection())
                recalculateItemTiers();
        }
    }
}
