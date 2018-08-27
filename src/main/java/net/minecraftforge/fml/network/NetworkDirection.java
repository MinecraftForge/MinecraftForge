/*
 * Minecraft Forge
 * Copyright (c) 2018.
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

import it.unimi.dsi.fastutil.objects.Reference2ReferenceArrayMap;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.client.CPacketCustomPayloadLogin;
import net.minecraft.network.login.server.SPacketCustomPayloadLogin;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.network.play.server.SPacketCustomPayload;
import net.minecraftforge.fml.LogicalSide;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum NetworkDirection
{
    PLAY_TO_SERVER(NetworkEvent.ClientCustomPayloadEvent::new, LogicalSide.CLIENT, CPacketCustomPayload.class),
    PLAY_TO_CLIENT(NetworkEvent.ServerCustomPayloadEvent::new, LogicalSide.SERVER, SPacketCustomPayload.class),
    LOGIN_TO_SERVER(NetworkEvent.ClientCustomPayloadEvent::new, LogicalSide.CLIENT, CPacketCustomPayloadLogin.class),
    LOGIN_TO_CLIENT(NetworkEvent.ServerCustomPayloadEvent::new, LogicalSide.SERVER, SPacketCustomPayloadLogin.class);

    private final BiFunction<PacketBuffer, Supplier<NetworkEvent.Context>, NetworkEvent> eventSupplier;
    private final LogicalSide logicalSide;
    private final Class<? extends Packet> packetClass;

    private static final Reference2ReferenceArrayMap<Class<? extends Packet>, NetworkDirection> packetLookup;

    static {
        packetLookup = Stream.of(values()).
                collect(Collectors.toMap(NetworkDirection::getPacketClass, Function.identity(), (m1,m2)->m1, Reference2ReferenceArrayMap::new));
    }

    NetworkDirection(BiFunction<PacketBuffer, Supplier<NetworkEvent.Context>, NetworkEvent> eventSupplier, LogicalSide logicalSide, Class<? extends Packet> clazz)
    {
        this.eventSupplier = eventSupplier;
        this.logicalSide = logicalSide;
        this.packetClass = clazz;
    }

    private Class<? extends Packet> getPacketClass() {
        return packetClass;
    }
    public static <T extends ICustomPacket<?>> NetworkDirection directionFor(Class<T> customPacket)
    {
        return packetLookup.get(customPacket);
    }

    public NetworkEvent getEvent(final PacketBuffer buffer, final Supplier<NetworkEvent.Context> manager) {
        return this.eventSupplier.apply(buffer, manager);
    }

    public LogicalSide getLogicalSide()
    {
        return logicalSide;
    }
}
