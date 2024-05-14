/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network;

import net.minecraft.network.Connection;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.eventbus.api.BusBuilder;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.IEventListener;
import net.minecraftforge.network.Channel.VersionTest;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import org.jetbrains.annotations.ApiStatus;

import io.netty.util.AttributeKey;

/**
 * This is essentially the shared common class for {@link SimpleChannel} and {@link EventNetworkChannel}.
 * I've now introduced {@link Channel} as that common modder facing base class. I am basically using this
 * as the internal API and {@link Channel} as the public.
 */
@ApiStatus.Internal
public final class NetworkInstance {
    // We use an event bus here so that we don't have to have a handle(event) public function on Channel.
    // Should this be changed so that modders can fire other channel's handlers?
    private final IEventBus networkEventBus;
    private final ResourceLocation channelName;
    private final int networkProtocolVersion;
    final VersionTest clientAcceptedVersions;
    final VersionTest serverAcceptedVersions;
    final Map<AttributeKey<?>, Function<Connection, ?>> attributes;
    final Consumer<Connection> channelHandler;
    final ServerStatusPing.ChannelData pingData;
    private final Set<ResourceLocation> ids = new HashSet<>();

    NetworkInstance(ResourceLocation channelName, int networkProtocolVersion,
        VersionTest clientAcceptedVersions, VersionTest serverAcceptedVersions,
        Map<AttributeKey<?>, Function<Connection, ?>> attributes, Consumer<Connection> channelHandler
    ) {
        this.channelName = channelName;
        this.networkProtocolVersion = networkProtocolVersion;
        this.clientAcceptedVersions = clientAcceptedVersions;
        this.serverAcceptedVersions = serverAcceptedVersions;
        this.attributes = attributes;
        this.channelHandler = channelHandler;
        this.networkEventBus = BusBuilder.builder().setExceptionHandler(this::handleError).useModLauncher().build();
        this.pingData = new ServerStatusPing.ChannelData(channelName, networkProtocolVersion, this.clientAcceptedVersions.accepts(VersionTest.Status.MISSING, -1));
    }

    private void handleError(IEventBus iEventBus, Event event, IEventListener[] iEventListeners, int i, Throwable throwable) {
    }

    public <T extends CustomPayloadEvent> void addListener(Consumer<T> eventListener) {
        this.networkEventBus.addListener(eventListener);
    }

    public void registerObject(final Object object) {
        this.networkEventBus.register(object);
    }

    public void unregisterObject(final Object object) {
        this.networkEventBus.unregister(object);
    }

    public boolean dispatch(CustomPayloadEvent event) {
        this.networkEventBus.post(event);
        return event.getSource().getPacketHandled();
    }

    /**
     * Registers another name that will have its CustomPayloadEvents redirected to this channel.
     * Like the main name, this must be unique across all channels.
     */
    public NetworkInstance addChild(ResourceLocation name) {
        NetworkRegistry.register(this, name);
        this.ids.add(name);
        return this;
    }

    ResourceLocation getChannelName() {
        return channelName;
    }

    int getNetworkProtocolVersion() {
        return networkProtocolVersion;
    }

    void registrationChange(ResourceLocation name, boolean registered) {
        // TODO: Expose to listeners?
    }

    boolean isRemotePresent(Connection con) {
        var channels = NetworkContext.get(con).getRemoteChannels();
        return channels.containsAll(ids);
    }
}
