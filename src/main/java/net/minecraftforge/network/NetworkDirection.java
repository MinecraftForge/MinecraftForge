/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceArrayMap;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.DiscardedPayload;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket;
import net.minecraft.network.protocol.login.ServerboundCustomQueryAnswerPacket;
import net.minecraft.network.protocol.login.custom.DiscardedQueryAnswerPayload;
import net.minecraft.network.protocol.login.custom.DiscardedQueryPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.LogicalSide;

@SuppressWarnings("rawtypes")
public enum NetworkDirection {
    PLAY_TO_SERVER(LogicalSide.CLIENT, ServerboundCustomPayloadPacket.class, 1, NetworkDirection::playServerbound),
    PLAY_TO_CLIENT(LogicalSide.SERVER, ClientboundCustomPayloadPacket.class, 0, NetworkDirection::playClientbound),
    LOGIN_TO_SERVER(LogicalSide.CLIENT, ServerboundCustomQueryAnswerPacket.class, 3, NetworkDirection::loginServerbound),
    LOGIN_TO_CLIENT(LogicalSide.SERVER, ClientboundCustomQueryPacket.class, 2, NetworkDirection::loginClientbound);

    private final LogicalSide logicalSide;
    private final Class<? extends Packet> packetClass;
    private final int otherWay;
    private final Factory factory;

    private static final Reference2ReferenceArrayMap<Class<? extends Packet>, NetworkDirection> packetLookup = new Reference2ReferenceArrayMap<>();
    private static final NetworkDirection[] values = values();

    static {
        for (var value : values)
            packetLookup.put(value.getPacketClass(), value);
    }

    private NetworkDirection(LogicalSide logicalSide, Class<? extends Packet> clazz, int i, Factory factory) {
        this.logicalSide = logicalSide;
        this.packetClass = clazz;
        this.otherWay = i;
        this.factory = factory;
    }

    private Class<? extends Packet> getPacketClass() {
        return packetClass;
    }

    public static <T extends ICustomPacket<?>> NetworkDirection directionFor(Class<T> customPacket) {
        return packetLookup.get(customPacket);
    }

    public NetworkDirection reply() {
        return values[this.otherWay];
    }

    public LogicalSide getOriginationSide() {
        return logicalSide;
    }

    public LogicalSide getReceptionSide() {
        return reply().logicalSide;
    }

    @SuppressWarnings("unchecked")
    public <T extends Packet<?>> ICustomPacket<T> buildPacket(FriendlyByteBuf data, ResourceLocation channelName) {
        return this.factory.create(data, 0, channelName);
    }

    private interface Factory<T extends Packet<?>> {
        ICustomPacket<T> create(FriendlyByteBuf data, Integer index, ResourceLocation channelName);
    }

    private static ServerboundCustomPayloadPacket playServerbound(FriendlyByteBuf data, Integer index, ResourceLocation channelName) {
        return new ServerboundCustomPayloadPacket(new DiscardedPayload(channelName, data));
    }

    private static ClientboundCustomPayloadPacket playClientbound(FriendlyByteBuf data, Integer index, ResourceLocation channelName) {
        return new ClientboundCustomPayloadPacket(new DiscardedPayload(channelName, data));
    }

    private static ServerboundCustomQueryAnswerPacket loginServerbound(FriendlyByteBuf data, Integer index, ResourceLocation channelName) {
        return new ServerboundCustomQueryAnswerPacket(index, new DiscardedQueryAnswerPayload(data));
    }

    private static ClientboundCustomQueryPacket loginClientbound(FriendlyByteBuf data, Integer index, ResourceLocation channelName) {
        return new ClientboundCustomQueryPacket(index, new DiscardedQueryPayload(channelName, data));
    }
}
