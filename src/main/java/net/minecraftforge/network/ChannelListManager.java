/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network;

import com.mojang.logging.LogUtils;
import net.minecraft.ResourceLocationException;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.network.ChannelRegistrationChangeEvent;
import net.minecraftforge.event.network.CustomPayloadEvent;

import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

@ApiStatus.Internal // TODO: Decide what I want to have public, Right now it should just be add/remove channels.
public class ChannelListManager {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final ResourceLocation NAME = ResourceLocation.fromNamespaceAndPath("forge", "channel_registration");

    static final Channel<CustomPacketPayload> CHANNEL = ChannelBuilder
        .named(NAME)
        .optional()
        .payloadChannel()
            .any()
                .bidirectional()
                    .add(Register.TYPE, Register.CODEC, ChannelListManager::register)
                    .add(Unregister.TYPE, Unregister.CODEC, ChannelListManager::unregister)
        .build();

    private record Register(List<String> channels) implements CustomPacketPayload {
        private static final Type<Register> TYPE = CustomPacketPayload.createType("register");
        private static final StreamCodec<FriendlyByteBuf, Register> CODEC = StreamCodec.of(
            (buf, v) -> encode(buf, v.channels),
            buf -> new Register(decode(buf))
        );

        @Override
        public Type<Register> type() {
            return TYPE;
        }
    }

    private record Unregister(List<String> channels) implements CustomPacketPayload {
        private static final Type<Unregister> TYPE = CustomPacketPayload.createType("unregister");
        private static final StreamCodec<FriendlyByteBuf, Unregister> CODEC = StreamCodec.of(
            (buf, v) -> encode(buf, v.channels),
            buf -> new Unregister(decode(buf))
        );

        @Override
        public Type<Unregister> type() {
            return TYPE;
        }
    }

    private static void register(Register payload, CustomPayloadEvent.Context ctx) {
        updateFrom(ctx, payload.channels(), ChannelRegistrationChangeEvent.Type.REGISTER);
        ctx.setPacketHandled(true);
        // Send the client's channels to the server whenever asked. If we're in the login state then our login wrapper will unwrap for us.
        if (ctx.isClientSide())
            addChannels(ctx.getConnection());
    }

    private static void unregister(Unregister payload, CustomPayloadEvent.Context ctx) {
        updateFrom(ctx, payload.channels(), ChannelRegistrationChangeEvent.Type.UNREGISTER);
        ctx.setPacketHandled(true);
    }

    public static void addChannels(Connection connection) {
        addChannels(connection, NetworkRegistry.buildRegisterList());
    }

    public static void addChannels(Connection connection, ResourceLocation... channels) {
        addChannels(connection, Arrays.asList(channels));
    }

    public static void addChannels(Connection connection, Collection<ResourceLocation> channels) {
        var list = NetworkContext.get(connection);
        var toSend = new HashSet<String>();
        for (var channel : channels) {
            if (list.sentChannels.add(channel))
                toSend.add(channel.toString());
        }

        if (!toSend.isEmpty())
            CHANNEL.send(new Register(toSend.stream().sorted().toList()), connection);
    }

    public static void removeChannels(Connection connection, ResourceLocation... channels) {
        removeChannels(connection, Arrays.asList(channels));
    }

    public static void removeChannels(Connection connection, Collection<ResourceLocation> channels) {
        var list = NetworkContext.get(connection);
        var toSend = new HashSet<String>();
        for (var channel : channels) {
            if (list.sentChannels.remove(channel))
                toSend.add(channel.toString());
        }

        if (!toSend.isEmpty())
            CHANNEL.send(new Unregister(toSend.stream().toList()), connection);
    }

    private static void encode(FriendlyByteBuf buf, List<String> channels) {
        for (var c : channels) {
            buf.writeBytes(c.toString().getBytes(StandardCharsets.UTF_8));
            buf.writeByte(0);
        }
    }

    private static List<String> decode(FriendlyByteBuf buffer) {
        byte[] data = new byte[Math.max(buffer.readableBytes(), 0)];
        buffer.readBytes(data);
        var channels = new ArrayList<String>();

        int last = 0;
        for (int cur = 0; cur < data.length; cur++) {
            if (data[cur] == '\0') {
                channels.add(new String(data, last, cur - last, StandardCharsets.UTF_8));
                last = cur + 1;
            }
        }

        // Add the end of the data because the spec doesn't actually say null terminated, just null separated
        if (last < data.length)
            channels.add(new String(data, last, data.length - last, StandardCharsets.UTF_8));

        return Collections.unmodifiableList(channels);
    }

    private static void updateFrom(CustomPayloadEvent.Context source, List<String> channels, final ChannelRegistrationChangeEvent.Type changeType) {
        var changed = new HashSet<ResourceLocation>();
        for (var channel : channels) {
            // It also says nothing about the format of channels so ignore bad channels.
            if (channel.isEmpty())
                continue;

            try {
                changed.add(ResourceLocation.parse(channel));
            } catch (ResourceLocationException ex) {
                // Vanilla packet deserializers now force this to be a resource location, so we should never get this, but just in case.
                LOGGER.warn("Invalid channel name received: {}. Ignoring", channel);
            }
        }

        ForgeEventFactory.onChannelRegistrationChange(source.getConnection(), changeType, changed);

        var list = NetworkContext.get(source.getConnection());
        for (var channel : changed) {
            boolean fire = changeType == ChannelRegistrationChangeEvent.Type.UNREGISTER
                ? list.remoteChannels.remove(channel)
                : list.remoteChannels.add(channel);

            if (fire) {
                var target = NetworkRegistry.findTarget(channel);
                if (target != null)
                    target.registrationChange(channel, changeType == ChannelRegistrationChangeEvent.Type.REGISTER);
            }
        }
    }
}
