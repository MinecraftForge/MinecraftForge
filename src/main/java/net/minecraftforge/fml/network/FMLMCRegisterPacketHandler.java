/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.fml.network;

import io.netty.buffer.Unpooled;
import io.netty.util.Attribute;
import net.minecraft.network.IPacket;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

import org.apache.commons.lang3.tuple.Pair;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

public class FMLMCRegisterPacketHandler {
    public static final FMLMCRegisterPacketHandler INSTANCE = new FMLMCRegisterPacketHandler();

    public static class ChannelList {
        private Set<ResourceLocation> locations = new HashSet<>();

        public void updateFrom(final Supplier<NetworkEvent.Context> source, PacketBuffer buffer, final NetworkEvent.RegistrationChangeType changeType) {
            byte[] data = new byte[Math.max(buffer.readableBytes(), 0)];
            buffer.readBytes(data);
            Set<ResourceLocation> oldLocations = this.locations;
            this.locations = bytesToResLocation(data);
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
                    rl.add(new ResourceLocation(s));
                    last = cur + 1;
                }
            }
            return rl;
        }
    }

    public void addChannels(Set<ResourceLocation> locations, NetworkManager manager) {
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

    private static ChannelList getFrom(NetworkManager manager) {
        return fromAttr(manager.channel().attr(FMLNetworkConstants.FML_MC_REGISTRY));
    }

    private static ChannelList getFrom(NetworkEvent event) {
        return fromAttr(event.getSource().get().attr(FMLNetworkConstants.FML_MC_REGISTRY));
    }

    private static ChannelList fromAttr(Attribute<ChannelList> attr) {
        attr.setIfAbsent(new ChannelList());
        return attr.get();
    }

    public void sendRegistry(NetworkManager manager, final NetworkDirection dir) {
        PacketBuffer pb = new PacketBuffer(Unpooled.buffer());
        pb.writeBytes(getFrom(manager).toByteArray());
        final ICustomPacket<IPacket<?>> iPacketICustomPacket = dir.buildPacket(Pair.of(pb, 0), FMLNetworkConstants.MC_REGISTER_RESOURCE);
        manager.sendPacket(iPacketICustomPacket.getThis());
    }
}
