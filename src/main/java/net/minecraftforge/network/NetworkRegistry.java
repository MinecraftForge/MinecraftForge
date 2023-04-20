/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network;

import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.event.EventNetworkChannel;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.DataPackRegistriesHooks;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * The impl registry. Tracks channels on behalf of mods.
 */
public class NetworkRegistry
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker NETREGISTRY = MarkerManager.getMarker("NETREGISTRY");

    private static Map<ResourceLocation, NetworkInstance> instances = Collections.synchronizedMap(new HashMap<>());

    /**
     * Special value for clientAcceptedVersions and serverAcceptedVersions predicates indicating the other side lacks
     * this channel.
     */
    public static ServerStatusPing.ChannelData ABSENT = new ServerStatusPing.ChannelData(new ResourceLocation("absent"), "ABSENT \uD83E\uDD14", false);

    public static String ACCEPTVANILLA  = new String("ALLOWVANILLA \uD83D\uDC93\uD83D\uDC93\uD83D\uDC93");

    /**
     * Makes a version predicate that accepts connections to vanilla or without the channel.
     * @param protocolVersion The protocol version, which will be matched exactly.
     * @return A new predicate with the new conditions.
     */
    public static Predicate<String> acceptMissingOr(final String protocolVersion)
    {
        return acceptMissingOr(protocolVersion::equals);
    }

    /**
     * Makes a version predicate that accepts connections to vanilla or without the channel.
     * @param versionCheck The main version predicate, which should check the version number of the protocol.
     * @return A new predicate with the new conditions.
     */
    public static Predicate<String> acceptMissingOr(Predicate<String> versionCheck)
    {
        return versionCheck.or(ABSENT.version()::equals).or(ACCEPTVANILLA::equals);
    }

    public static List<String> getServerNonVanillaNetworkMods()
    {
        return listRejectedVanillaMods(NetworkInstance::tryClientVersionOnServer);
    }

    public static List<String> getClientNonVanillaNetworkMods()
    {
        return listRejectedVanillaMods(NetworkInstance::tryServerVersionOnClient);
    }

    public static boolean acceptsVanillaClientConnections() {
        return (instances.isEmpty() || getServerNonVanillaNetworkMods().isEmpty()) && DataPackRegistriesHooks.getSyncedCustomRegistries().isEmpty();
    }

    public static boolean canConnectToVanillaServer() {
        return instances.isEmpty() || getClientNonVanillaNetworkMods().isEmpty();
    }


    /**
     * Create a new {@link SimpleChannel}.
     *
     * @param name The registry name for this channel. Must be unique
     * @param networkProtocolVersion The impl protocol version string that will be offered to the remote side {@link ChannelBuilder#networkProtocolVersion(Supplier)}
     * @param clientAcceptedVersions Called on the client with the networkProtocolVersion string from the server {@link ChannelBuilder#clientAcceptedVersions(Predicate)}
     * @param serverAcceptedVersions Called on the server with the networkProtocolVersion string from the client {@link ChannelBuilder#serverAcceptedVersions(Predicate)}
     * @return A new {@link SimpleChannel}
     *
     * @see ChannelBuilder#newSimpleChannel(ResourceLocation, Supplier, Predicate, Predicate)
     */
    public static SimpleChannel newSimpleChannel(final ResourceLocation name, Supplier<String> networkProtocolVersion, Predicate<String> clientAcceptedVersions, Predicate<String> serverAcceptedVersions) {
        return new SimpleChannel(createInstance(name, networkProtocolVersion, clientAcceptedVersions, serverAcceptedVersions));
    }

    /**
     * Create a new {@link EventNetworkChannel}.
     *
     * @param name The registry name for this channel. Must be unique
     * @param networkProtocolVersion The impl protocol version string that will be offered to the remote side {@link ChannelBuilder#networkProtocolVersion(Supplier)}
     * @param clientAcceptedVersions Called on the client with the networkProtocolVersion string from the server {@link ChannelBuilder#clientAcceptedVersions(Predicate)}
     * @param serverAcceptedVersions Called on the server with the networkProtocolVersion string from the client {@link ChannelBuilder#serverAcceptedVersions(Predicate)}

     * @return A new {@link EventNetworkChannel}
     *
     * @see ChannelBuilder#newEventChannel(ResourceLocation, Supplier, Predicate, Predicate)
     */
    public static EventNetworkChannel newEventChannel(final ResourceLocation name, Supplier<String> networkProtocolVersion, Predicate<String> clientAcceptedVersions, Predicate<String> serverAcceptedVersions) {
        return new EventNetworkChannel(createInstance(name, networkProtocolVersion, clientAcceptedVersions, serverAcceptedVersions));
    }

    /**
     * Creates the internal {@link NetworkInstance} that tracks the channel data.
     * @param name registry name
     * @param networkProtocolVersion The protocol version string
     * @param clientAcceptedVersions The client accepted predicate
     * @param serverAcceptedVersions The server accepted predicate
     * @return The {@link NetworkInstance}
     * @throws IllegalArgumentException if the name already exists
     */
    private static NetworkInstance createInstance(ResourceLocation name, Supplier<String> networkProtocolVersion, Predicate<String> clientAcceptedVersions, Predicate<String> serverAcceptedVersions)
    {
        if(lock) {
            LOGGER.error(NETREGISTRY, "Attempted to register channel {} even though registry phase is over", name);
            throw new IllegalArgumentException("Registration of impl channels is locked");
        }
        if (instances.containsKey(name)) {
            LOGGER.error(NETREGISTRY, "NetworkDirection channel {} already registered.", name);
            throw new IllegalArgumentException("NetworkDirection Channel {"+ name +"} already registered");
        }
        final NetworkInstance networkInstance = new NetworkInstance(name, networkProtocolVersion, clientAcceptedVersions, serverAcceptedVersions);
        instances.put(name, networkInstance);
        return networkInstance;
    }

    /**
     * Find the {@link NetworkInstance}, if possible
     *
     * @param resourceLocation The impl instance to lookup
     * @return The {@link Optional} {@link NetworkInstance}
     */
    static Optional<NetworkInstance> findTarget(ResourceLocation resourceLocation)
    {
        return Optional.ofNullable(instances.get(resourceLocation));
    }

    /**
     * Construct the Map representation of the channel list, for use during login handshaking
     *
     * @see HandshakeMessages.S2CModList
     * @see HandshakeMessages.C2SModListReply
     */
    static Map<ResourceLocation, String> buildChannelVersions() {
        return instances.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getNetworkProtocolVersion()));
    }

    /**
     * Construct the Map representation of the channel list, for the client to check against during list ping
     *
     * @see HandshakeMessages.S2CModList
     * @see HandshakeMessages.C2SModListReply
     */
    static Map<ResourceLocation, ServerStatusPing.ChannelData> buildChannelVersionsForListPing() {
        return instances.entrySet().stream().
                filter(p -> !p.getKey().getNamespace().equals("fml")).
                collect(Collectors.toMap(Map.Entry::getKey, val -> new ServerStatusPing.ChannelData(val.getKey(), val.getValue().getNetworkProtocolVersion(), val.getValue().tryClientVersionOnServer(ABSENT.version()))));
    }

    static List<String> listRejectedVanillaMods(BiFunction<NetworkInstance, String, Boolean> testFunction) {
        final List<Pair<ResourceLocation, Boolean>> results = instances.values().stream().
                map(ni -> {
                    final String incomingVersion = ACCEPTVANILLA;
                    final boolean test = testFunction.apply(ni, incomingVersion);
                    LOGGER.debug(NETREGISTRY, "Channel '{}' : Vanilla acceptance test: {}", ni.getChannelName(), test ? "ACCEPTED" : "REJECTED");
                    return Pair.of(ni.getChannelName(), test);
                }).filter(p->!p.getRight()).toList();

        if (!results.isEmpty()) {
            LOGGER.error(NETREGISTRY, "Channels [{}] rejected vanilla connections",
                    results.stream().map(Pair::getLeft).map(Object::toString).collect(Collectors.joining(",")));
            return results.stream().map(Pair::getLeft).map(Object::toString).collect(Collectors.toList());
        }
        LOGGER.debug(NETREGISTRY, "Accepting channel list from vanilla");
        return Collections.emptyList();
    }
    /**
     * Validate the channels from the server on the client. Tests the client predicates against the server
     * supplied impl protocol version.
     *
     * @param channels An @{@link Map} of name->version pairs for testing
     * @return a map of mismatched channel ids and versions, or an empty map if all channels accept themselves
     */
    static Map<ResourceLocation, String> validateClientChannels(final Map<ResourceLocation, String> channels) {
        return validateChannels(channels, "server", NetworkInstance::tryServerVersionOnClient);
    }

    /**
     * Validate the channels from the client on the server. Tests the server predicates against the client
     * supplied impl protocol version.
     * @param channels An @{@link Map} of name->version pairs for testing
     * @return a map of mismatched channel ids and versions, or an empty map if all channels accept themselves
     */
    static Map<ResourceLocation, String> validateServerChannels(final Map<ResourceLocation, String> channels) {
        return validateChannels(channels, "client", NetworkInstance::tryClientVersionOnServer);
    }

    /**
     * Tests if the map matches with the supplied predicate tester
     *
     * @param incoming An @{@link Map} of name->version pairs for testing
     * @param originName A label for use in logging (where the version pairs came from)
     * @param testFunction The test function to use for testing
     * @return a map of mismatched channel ids and versions, or an empty map if all channels accept themselves
     */
    private static Map<ResourceLocation, String> validateChannels(final Map<ResourceLocation, String> incoming, final String originName, BiFunction<NetworkInstance, String, Boolean> testFunction) {
        final Map<ResourceLocation, String> results = instances.values().stream().
                map(ni -> {
                    final String incomingVersion = incoming.getOrDefault(ni.getChannelName(), ABSENT.version());
                    final boolean test = testFunction.apply(ni, incomingVersion);
                    LOGGER.debug(NETREGISTRY, "Channel '{}' : Version test of '{}' from {} : {}", ni.getChannelName(), incomingVersion, originName, test ? "ACCEPTED" : "REJECTED");
                    return Pair.of(Pair.of(ni.getChannelName(), incomingVersion), test);
                }).filter(p->!p.getRight()).map(Pair::getLeft).collect(Collectors.toMap(Pair::getLeft, Pair::getRight));

        if (!results.isEmpty()) {
            LOGGER.error(NETREGISTRY, "Channels [{}] rejected their {} side version number",
                    results.keySet().stream().map(Object::toString).collect(Collectors.joining(",")),
                    originName);
            return results;
        }
        LOGGER.debug(NETREGISTRY, "Accepting channel list from {}", originName);
        return results;
    }

    /**
     * Retrieve the {@link LoginPayload} list for dispatch during {@link HandshakeHandler#tickLogin(Connection)} handling.
     * Dispatches {@link NetworkEvent.GatherLoginPayloadsEvent} to each {@link NetworkInstance}.
     *
     * @return The {@link LoginPayload} list
     * @param direction the impl direction for the request - only gathers for LOGIN_TO_CLIENT
     */
    static List<LoginPayload> gatherLoginPayloads(final NetworkDirection direction, boolean isLocal) {
        if (direction!=NetworkDirection.LOGIN_TO_CLIENT) return Collections.emptyList();
        List<LoginPayload> gatheredPayloads = new ArrayList<>();
        instances.values().forEach(ni->ni.dispatchGatherLogin(gatheredPayloads, isLocal));
        return gatheredPayloads;
    }

    public static boolean checkListPingCompatibilityForClient(Map<ResourceLocation, ServerStatusPing.ChannelData> incoming) {
        Set<ResourceLocation> handled = new HashSet<>();
        final List<Pair<ResourceLocation, Boolean>> results = instances.values().stream().
                filter(p -> !p.getChannelName().getNamespace().equals("fml")).
                map(ni -> {
                    final ServerStatusPing.ChannelData incomingVersion = incoming.getOrDefault(ni.getChannelName(), ABSENT);
                    final boolean test = ni.tryServerVersionOnClient(incomingVersion.version());
                    handled.add(ni.getChannelName());
                    LOGGER.debug(NETREGISTRY, "Channel '{}' : Version test of '{}' during listping : {}", ni.getChannelName(), incomingVersion, test ? "ACCEPTED" : "REJECTED");
                    return Pair.of(ni.getChannelName(), test);
                }).filter(p->!p.getRight()).collect(Collectors.toList());
        final List<ResourceLocation> missingButRequired = incoming.entrySet().stream().
                filter(p -> !p.getKey().getNamespace().equals("fml")).
                filter(p -> !p.getValue().required()).
                filter(p -> !handled.contains(p.getKey())).
                map(Map.Entry::getKey).
                collect(Collectors.toList());

        if (!results.isEmpty()) {
            LOGGER.error(NETREGISTRY, "Channels [{}] rejected their server side version number during listping",
                    results.stream().map(Pair::getLeft).map(Object::toString).collect(Collectors.joining(",")));
            return false;
        }
        if(!missingButRequired.isEmpty()){
            LOGGER.error(NETREGISTRY, "The server is likely to require channel [{}] to be present, yet we don't have it",
                    missingButRequired);
            return false;
        }
        LOGGER.debug(NETREGISTRY, "Accepting channel list during listping");
        return true;
    }

    private static boolean lock = false;
    public boolean isLocked(){
        return lock;
    }

    public static void lock() {
        lock=true;
    }

    /**
     * Tracks individual outbound messages for dispatch to clients during login handling. Gathered by dispatching
     * {@link NetworkEvent.GatherLoginPayloadsEvent} during early connection handling.
     */
    public static class LoginPayload {
        /**
         * The data for sending
         */
        private final FriendlyByteBuf data;
        /**
         * A channel which will receive a {@link NetworkEvent.LoginPayloadEvent} from the {@link LoginWrapper}
         */
        private final ResourceLocation channelName;

        /**
         * Some context for logging purposes
         */
        private final String messageContext;

        /**
         * If the connection should await a response to this packet to continue with the handshake
         */
        private final boolean needsResponse;

        public LoginPayload(final FriendlyByteBuf buffer, final ResourceLocation channelName, final String messageContext) {
            this(buffer, channelName, messageContext, true);
        }

        public LoginPayload(final FriendlyByteBuf buffer, final ResourceLocation channelName, final String messageContext, final boolean needsResponse)
        {
            this.data = buffer;
            this.channelName = channelName;
            this.messageContext = messageContext;
            this.needsResponse = needsResponse;
        }

        public FriendlyByteBuf getData() {
            return data;
        }

        public ResourceLocation getChannelName() {
            return channelName;
        }

        public String getMessageContext() {
            return messageContext;
        }

        public boolean needsResponse()
        {
            return needsResponse;
        }
    }

    /**
     * Builder for constructing impl channels using a builder style API.
     */
    public static class ChannelBuilder {
        private ResourceLocation channelName;
        private Supplier<String> networkProtocolVersion;
        private Predicate<String> clientAcceptedVersions;
        private Predicate<String> serverAcceptedVersions;

        /**
         * The name of the channel. Must be unique.
         * @param channelName The name of the channel
         * @return the channel builder
         */
        public static ChannelBuilder named(ResourceLocation channelName)
        {
            ChannelBuilder builder = new ChannelBuilder();
            builder.channelName = channelName;
            return builder;
        }

        /**
         * The impl protocol string for this channel. This will be gathered during login and sent to
         * the remote partner, where it will be tested with against the relevant predicate.
         *
         * @see #serverAcceptedVersions(Predicate)
         * @see #clientAcceptedVersions(Predicate)
         * @param networkProtocolVersion A supplier of strings for impl protocol version testing
         * @return the channel builder
         */
        public ChannelBuilder networkProtocolVersion(Supplier<String> networkProtocolVersion)
        {
            this.networkProtocolVersion = networkProtocolVersion;
            return this;
        }

        /**
         * A predicate run on the client, with the {@link #networkProtocolVersion(Supplier)} string from
         * the server, or the special value {@link NetworkRegistry#ABSENT} indicating the absence of
         * the channel on the remote side.
         * @param clientAcceptedVersions A predicate for testing
         * @return the channel builder
         */
        public ChannelBuilder clientAcceptedVersions(Predicate<String> clientAcceptedVersions)
        {
            this.clientAcceptedVersions = clientAcceptedVersions;
            return this;
        }

        /**
         * A predicate run on the server, with the {@link #networkProtocolVersion(Supplier)} string from
         * the server, or the special value {@link NetworkRegistry#ABSENT} indicating the absence of
         * the channel on the remote side.
         * @param serverAcceptedVersions A predicate for testing
         * @return the channel builder
         */
        public ChannelBuilder serverAcceptedVersions(Predicate<String> serverAcceptedVersions)
        {
            this.serverAcceptedVersions = serverAcceptedVersions;
            return this;
        }

        /**
         * Create the impl instance
         * @return the {@link NetworkInstance}
         */
        private NetworkInstance createNetworkInstance() {
            return createInstance(channelName, networkProtocolVersion, clientAcceptedVersions, serverAcceptedVersions);
        }

        /**
         * Build a new {@link SimpleChannel} with this builder's configuration.
         *
         * @return A new {@link SimpleChannel}
         */
        public SimpleChannel simpleChannel() {
            return new SimpleChannel(createNetworkInstance());
        }

        /**
         * Build a new {@link EventNetworkChannel} with this builder's configuration.
         * @return A new {@link EventNetworkChannel}
         */
        public EventNetworkChannel eventNetworkChannel() {
            return new EventNetworkChannel(createNetworkInstance());
        }
    }
}
