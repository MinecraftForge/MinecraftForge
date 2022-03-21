/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network;

import net.minecraft.network.Connection;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.BusBuilder;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.IEventListener;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class NetworkInstance
{
    public ResourceLocation getChannelName()
    {
        return channelName;
    }

    private final ResourceLocation channelName;
    private final String networkProtocolVersion;
    private final Predicate<String> clientAcceptedVersions;
    private final Predicate<String> serverAcceptedVersions;
    private final IEventBus networkEventBus;

    NetworkInstance(ResourceLocation channelName, Supplier<String> networkProtocolVersion, Predicate<String> clientAcceptedVersions, Predicate<String> serverAcceptedVersions)
    {
        this.channelName = channelName;
        this.networkProtocolVersion = networkProtocolVersion.get();
        this.clientAcceptedVersions = clientAcceptedVersions;
        this.serverAcceptedVersions = serverAcceptedVersions;
        this.networkEventBus = BusBuilder.builder().setExceptionHandler(this::handleError).build();
    }

    private void handleError(IEventBus iEventBus, Event event, IEventListener[] iEventListeners, int i, Throwable throwable)
    {

    }

    public <T extends NetworkEvent> void addListener(Consumer<T> eventListener)
    {
        this.networkEventBus.addListener(eventListener);
    }

    public void addGatherListener(Consumer<NetworkEvent.GatherLoginPayloadsEvent> eventListener)
    {
        this.networkEventBus.addListener(eventListener);
    }

    public void registerObject(final Object object) {
        this.networkEventBus.register(object);
    }

    public void unregisterObject(final Object object) {
        this.networkEventBus.unregister(object);
    }

    boolean dispatch(final NetworkDirection side, final ICustomPacket<?> packet, final Connection manager)
    {
        final NetworkEvent.Context context = new NetworkEvent.Context(manager, side, packet.getIndex());
        this.networkEventBus.post(side.getEvent(packet, () -> context));
        return context.getPacketHandled();
    }

    String getNetworkProtocolVersion() {
        return networkProtocolVersion;
    }

    boolean tryServerVersionOnClient(final String serverVersion) {
        return this.clientAcceptedVersions.test(serverVersion);
    }

    boolean tryClientVersionOnServer(final String clientVersion) {
        return this.serverAcceptedVersions.test(clientVersion);
    }

    void dispatchGatherLogin(final List<NetworkRegistry.LoginPayload> loginPayloadList, boolean isLocal) {
        this.networkEventBus.post(new NetworkEvent.GatherLoginPayloadsEvent(loginPayloadList, isLocal));
    }

    void dispatchLoginPacket(final NetworkEvent.LoginPayloadEvent loginPayloadEvent) {
        this.networkEventBus.post(loginPayloadEvent);
    }

    void dispatchEvent(final NetworkEvent networkEvent) {
        this.networkEventBus.post(networkEvent);
    }

    public boolean isRemotePresent(Connection manager) {
        ConnectionData connectionData = NetworkHooks.getConnectionData(manager);
        return connectionData != null && connectionData.getChannels().containsKey(channelName);
    }
}
