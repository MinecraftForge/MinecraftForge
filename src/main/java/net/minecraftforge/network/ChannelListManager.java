/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network;

import com.mojang.logging.LogUtils;
import io.netty.buffer.Unpooled;
import net.minecraft.ResourceLocationException;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.network.ChannelRegistrationChangeEvent;
import net.minecraftforge.event.network.CustomPayloadEvent;

import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

@ApiStatus.Internal // TODO: Decide what I want to have public, Right now it should just be add/remove channels.
public class ChannelListManager {
    private static final Logger LOGGER = LogUtils.getLogger();
    static final EventNetworkChannel REGISTER = ChannelBuilder
        .named("minecraft:register")
        .optional()
        .eventNetworkChannel()
        .addListener(ChannelListManager::registerListener);
    static final EventNetworkChannel UNREGISTER = ChannelBuilder
        .named("minecraft:unregister")
        .optional()
        .eventNetworkChannel()
        .addListener(ChannelListManager::unregisterListener);

    private static void registerListener(CustomPayloadEvent evt) {
        var ctx = evt.getSource();
        updateFrom(ctx, evt.getPayload(), ChannelRegistrationChangeEvent.Type.REGISTER);
        ctx.setPacketHandled(true);
        // Send the client's channels to the server whenever asked. If we're in the login state then our login wrapper will unwrap for us.
        if (ctx.isClientSide())
            addChannels(ctx.getConnection());
    }

    private static void unregisterListener(CustomPayloadEvent evt) {
        var ctx = evt.getSource();
        updateFrom(ctx, evt.getPayload(), ChannelRegistrationChangeEvent.Type.UNREGISTER);
        ctx.setPacketHandled(true);
    }

    public static void addChannels(Connection connection) {
        addChannels(connection, NetworkRegistry.buildChannelVersions().keySet().stream()
            .filter(rl -> !"minecraft".equals(rl.getNamespace()))
            .toList()
        );
    }

    public static void addChannels(Connection connection, ResourceLocation... channels) {
        addChannels(connection, Arrays.asList(channels));
    }

    public static void addChannels(Connection connection, Collection<ResourceLocation> channels) {
        var list = NetworkContext.get(connection);
        var toSend = new HashSet<ResourceLocation>();
        for (var channel : channels) {
            if (list.sentChannels.add(channel))
                toSend.add(channel);
        }
        sendChannels(REGISTER, connection, toSend);
    }

    public static void removeChannels(Connection connection, ResourceLocation... channels) {
        removeChannels(connection, Arrays.asList(channels));
    }

    public static void removeChannels(Connection connection, Collection<ResourceLocation> channels) {
        var list = NetworkContext.get(connection);
        var toSend = new HashSet<ResourceLocation>();
        for (var channel : channels) {
            if (list.sentChannels.remove(channel))
                toSend.add(channel);
        }
        sendChannels(UNREGISTER, connection, toSend);
    }

    private static void sendChannels(EventNetworkChannel channel, Connection connection, Collection<ResourceLocation> channels) {
        if (channels.isEmpty())
            return;

        var buf = new FriendlyByteBuf(Unpooled.buffer());
        for (var c : channels) {
            buf.writeBytes(c.toString().getBytes(StandardCharsets.UTF_8));
            buf.writeByte(0);
        }

        channel.send(buf, connection);
    }

    private static void updateFrom(CustomPayloadEvent.Context source, FriendlyByteBuf buffer, final ChannelRegistrationChangeEvent.Type changeType) {
        byte[] data = new byte[Math.max(buffer.readableBytes(), 0)];
        buffer.readBytes(data);
        var channels = new HashSet<String>();

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

        var changed = new HashSet<ResourceLocation>();
        for (var channel : channels) {
            // It also says nothing about the format of channels so ignore bad channels.
            if (channel.isEmpty())
                continue;

            try {
                changed.add(new ResourceLocation(channel));
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
                    target.registrationChange(changeType == ChannelRegistrationChangeEvent.Type.REGISTER);
            }
        }
    }
}
