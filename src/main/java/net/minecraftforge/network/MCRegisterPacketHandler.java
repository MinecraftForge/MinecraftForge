/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network;

import com.mojang.logging.LogUtils;
import io.netty.buffer.Unpooled;
import io.netty.util.Attribute;
import net.minecraft.ResourceLocationException;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

public class MCRegisterPacketHandler
{
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final MCRegisterPacketHandler INSTANCE = new MCRegisterPacketHandler();

    public static class ChannelList {
        private Set<ResourceLocation> locations = new HashSet<>();
        private Set<ResourceLocation> remoteLocations = Set.of();

        public void updateFrom(final Supplier<NetworkEvent.Context> source, FriendlyByteBuf buffer, final NetworkEvent.RegistrationChangeType changeType) {
            byte[] data = new byte[Math.max(buffer.readableBytes(), 0)];
            buffer.readBytes(data);
            Set<ResourceLocation> oldLocations = this.locations;
            this.locations = bytesToResLocation(data);
            this.remoteLocations = Set.copyOf(this.locations);
            // ensure all locations receive updates, old and new.
            oldLocations.addAll(this.locations);
            oldLocations.stream()
                    .map(NetworkRegistry::findTarget)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEach(t->t.dispatchEvent(new NetworkEvent.ChannelRegistrationChangeEvent(source, changeType)));
        }

        byte[] toByteArray() {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            for (ResourceLocation rl : locations) {
                try {
                    bos.write(rl.toString().getBytes(StandardCharsets.UTF_8));
                    bos.write(0);
                } catch (IOException e) {
                    // fake IOE
                }
            }
            return bos.toByteArray();
        }

        private Set<ResourceLocation> bytesToResLocation(byte[] all) {
            HashSet<ResourceLocation> rl = new HashSet<>();
            int last = 0;
            for (int cur = 0; cur < all.length; cur++) {
                if (all[cur] == '\0') {
                    String s = new String(all, last, cur - last, StandardCharsets.UTF_8);
                    try {
                        rl.add(new ResourceLocation(s));
                    } catch (ResourceLocationException ex) {
                        LOGGER.warn("Invalid channel name received: {}. Ignoring", s);
                    }
                    last = cur + 1;
                }
            }
            return rl;
        }

        /**
         * {@return the unmodifiable set of channel locations sent by the remote side}
         * This is useful for interacting with other modloaders via the network to inspect registered network channel IDs.
         */
        public Set<ResourceLocation> getRemoteLocations() {
            return this.remoteLocations;
        }
    }

    public void addChannels(Set<ResourceLocation> locations, Connection manager) {
        getFrom(manager).locations.addAll(locations);
    }

    void registerListener(NetworkEvent evt) {
        final ChannelList channelList = getFrom(evt);
        channelList.updateFrom(evt.getSource(), evt.getPayload(), NetworkEvent.RegistrationChangeType.REGISTER);
        evt.getSource().get().setPacketHandled(true);
    }

    void unregisterListener(NetworkEvent evt) {
        final ChannelList channelList = getFrom(evt);
        channelList.updateFrom(evt.getSource(), evt.getPayload(), NetworkEvent.RegistrationChangeType.UNREGISTER);
        evt.getSource().get().setPacketHandled(true);
    }

    private static ChannelList getFrom(Connection manager) {
        return fromAttr(manager.channel().attr(NetworkConstants.FML_MC_REGISTRY));
    }

    private static ChannelList getFrom(NetworkEvent event) {
        return fromAttr(event.getSource().get().attr(NetworkConstants.FML_MC_REGISTRY));
    }

    private static ChannelList fromAttr(Attribute<ChannelList> attr) {
        attr.setIfAbsent(new ChannelList());
        return attr.get();
    }

    public void sendRegistry(Connection manager, final NetworkDirection dir) {
        FriendlyByteBuf pb = new FriendlyByteBuf(Unpooled.buffer());
        pb.writeBytes(getFrom(manager).toByteArray());
        final ICustomPacket<Packet<?>> iPacketICustomPacket = dir.buildPacket(Pair.of(pb, 0), NetworkConstants.MC_REGISTER_RESOURCE);
        manager.send(iPacketICustomPacket.getThis());
    }
}
