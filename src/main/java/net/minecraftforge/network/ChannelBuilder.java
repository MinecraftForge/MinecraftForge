/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import io.netty.util.AttributeKey;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.Channel.VersionTest;
import net.minecraftforge.network.payload.PayloadConnection;

/**
 * Builder for constructing impl channels using a builder style API.
 */
public class ChannelBuilder {
    private final ResourceLocation name;
    private int networkProtocolVersion = 0;
    private Channel.VersionTest clientAcceptedVersions;
    private Channel.VersionTest serverAcceptedVersions;
    private Map<AttributeKey<?>, Function<Connection, ?>> attributes = new HashMap<>();
    private Consumer<Connection> connectionHandler;
    private boolean registerSelf = true;

    /**
     * Creates a new channel builder, The name of the channel must be unique.
     * @param channelName The name of the channel
     */
    public static ChannelBuilder named(String channelName) {
        return named(ResourceLocation.parse(channelName));
    }

    /**
     * Creates a new channel builder, The name of the channel must be unique.
     * @param channelName The name of the channel
     */
    public static ChannelBuilder named(ResourceLocation channelName) {
        return new ChannelBuilder(channelName);
    }

    private ChannelBuilder(ResourceLocation name) {
        this.name = name;
    }

    /**
     * The impl protocol version for this channel. This will be gathered during login and sent to
     * the remote partner, where it will be tested with against the relevant predicate.
     * <br />
     * Defaults to 0
     *
     * @see #serverAcceptedVersions(Predicate)
     * @see #clientAcceptedVersions(Predicate)
     *
     * @throws IllegalArgumentException if version is < 0
     *
     */
    public ChannelBuilder networkProtocolVersion(int version) {
        if (version < 0)
            throw new IllegalArgumentException("Invalid network protocol: " + version);
        this.networkProtocolVersion = version;
        return this;
    }

    /**
     * A predicate run on both sides, with the {@link #networkProtocolVersion(int)} from
     * the server, the channel on the remote side.
     * <br />
     * Defaults to only accepting current version and requiring it to be present.
     *
     * @param test A predicate for testing
     * @see VersionTest#accepts(VersionTest.Status, int)
     */
    public ChannelBuilder acceptedVersions(VersionTest test) {
        return clientAcceptedVersions(test).serverAcceptedVersions(test);
    }

    /**
     * A predicate run on the client, with the {@link #networkProtocolVersion(int)} from
     * the server, the channel on the remote side.
     * <br />
     * Defaults to only accepting current version.
     *
     * @param test A predicate for testing
     * @see VersionTest#accepts(VersionTest.Status, int)
     */
    public ChannelBuilder clientAcceptedVersions(VersionTest test) {
        this.clientAcceptedVersions = test;
        return this;
    }

    /**
     * A predicate run on the server, with the {@link #networkProtocolVersion(int)} from
     * the server, the channel on the remote side.
     * <br />
     * Defaults to only accepting current version.
     *
     * @param test A predicate for testing
     * @see VersionTest#accepts(VersionTest.Status, int)
     */
    public ChannelBuilder serverAcceptedVersions(VersionTest test) {
        this.serverAcceptedVersions = test;
        return this;
    }

    /**
     * Allows a client to connect to a server that is missing this channel.
     * And allows a server to accept a client missing this channel.
     *
     * This includes vanilla connections.
     */
    public ChannelBuilder optional() {
        return optionalServer().optionalClient();
    }

    /**
     * Allows a client to connect to a server that is missing this channel.
     */
    public ChannelBuilder optionalServer() {
        return clientAcceptedVersions(VersionTest.ACCEPT_VANILLA.or(VersionTest.ACCEPT_MISSING.or(this.getClientAcceptedVersions())));
    }

    /**
     * Tells the server to accept clients that are missing this channel.
     */
    public ChannelBuilder optionalClient() {
        return serverAcceptedVersions(VersionTest.ACCEPT_VANILLA.or(VersionTest.ACCEPT_MISSING.or(this.getServerAcceptedVersions())));
    }

    /**
     * Registers a AttributeKey to be filled when a new connection is created.
     * This is meant as a simple way to attach data on a per-connection bases.
     *
     * @param key The key to assign
     * @param factory A factory that creates a new instance of the context data
     */
    public <T> ChannelBuilder attribute(AttributeKey<T> key, Supplier<T> factory) {
        return this.attribute(key, con -> factory.get());
    }

    /**
     * Registers a AttributeKey to be filled when a new connection is created.
     * This is meant as a simple way to attach data on a per-connection bases.
     *
     * @param key The key to assign
     * @param factory A factory that creates a new instance of the context data
     */
    public <T> ChannelBuilder attribute(AttributeKey<T> key, Function<Connection, T> factory) {
        this.attributes.put(key, factory);
        return this;
    }


    /**
     * Registers a function that will be called when a new connection is established.
     * This is meant to allow you to do basic configuration and add context objects.
     * This is equivalent to the {@link net.minecraftforge.event.network.ConnectionStartEvent ConnectionStartEvent}
     */
    public ChannelBuilder connectionHandler(Consumer<Connection> handler) {
        this.connectionHandler = handler;
        return this;
    }

    /**
     * Create the impl instance
     * @return the {@link NetworkInstance}
     */
    private NetworkInstance createNetworkInstance() {
        var instance = new NetworkInstance(name, networkProtocolVersion,
            getClientAcceptedVersions(), getServerAcceptedVersions(),
            attributes, connectionHandler
        );

        NetworkRegistry.register(instance);

        if (registerSelf)
            instance.addChild(name);

        return instance;

    }

    private VersionTest getClientAcceptedVersions() {
        return this.clientAcceptedVersions == null ? VersionTest.exact(this.networkProtocolVersion) : this.clientAcceptedVersions;
    }

    private VersionTest getServerAcceptedVersions() {
        return this.serverAcceptedVersions == null ? VersionTest.exact(this.networkProtocolVersion) : this.serverAcceptedVersions;
    }

    /**
     * Build a new {@link SimpleChannel} with this builder's configuration.
     *
     * @return A new {@link SimpleChannel}
     */
    public SimpleChannel simpleChannel() {
        return channel(SimpleChannel::new);
    }

    /**
     * Build a new {@link EventNetworkChannel} with this builder's configuration.
     * @return A new {@link EventNetworkChannel}
     */
    public EventNetworkChannel eventNetworkChannel() {
        return channel(EventNetworkChannel::new);
    }

    /**
     * Build a new {@link PayloadChannel} with this builder's configuration.
     * @return A new {@link PayloadConnection PayloadConnection&lt;CustomPacketPayload&gt}
     */
    public PayloadConnection<CustomPacketPayload> payloadChannel() {
        this.registerSelf = false;
        return channel(PayloadChannel::builder);
    }

    /**
     * Registers this channel with the {@link NetworkManager} and calls the supplied Function.
     * This is meant to allow modders to build their own Channel implementations.
     */
    public <C> C channel(Function<NetworkInstance, C> factory) {
        return factory.apply(createNetworkInstance());
    }
}