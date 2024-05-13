/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import io.netty.buffer.Unpooled;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.ProtocolInfo;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.configuration.ClientboundFinishConfigurationPacket;
import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket;
import net.minecraft.network.protocol.login.ServerboundCustomQueryAnswerPacket;

/*
 * This is a utility class that just dumps packets to the logger.
 * If people care I can make this a public API. Makes life a lot easier
 * when debugging network things. But it doesn't have a proper API cuz I haven't felt like designing one.
 */
public class PacketLogger {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker MARKER = MarkerManager.getMarker("PACKETLOG");

    private final Connection connection;
    public boolean enabled = false;

    public PacketLogger(Connection connection) {
        this.connection = connection;
    }

    public void send(Packet<?> packet) {
        common(connection.getReceiving(), connection.getSending(), connection.getOutputboundProtocolInfo(), packet);
    }

    public void recv(Packet<?> packet) {
        common(connection.getReceiving(), connection.getReceiving(), connection.getInboundProtocolInfo(), packet);
    }

    private void common(PacketFlow side, PacketFlow flow, ProtocolInfo<?> protocol, Packet<?> packet) {
        if (!enabled) return;
        if (packet instanceof ClientboundFinishConfigurationPacket)
            enabled = false;

        String channel = null;
        String hex = null;
        switch (packet) {
            case ClientboundCustomPayloadPacket custom -> {
                channel = custom.payload().type().id().toString();
                hex = hex(protocol, packet);
            }
            case ServerboundCustomPayloadPacket custom -> {
                channel = custom.payload().type().id().toString();
                hex = hex(protocol, packet);
            }
            case ClientboundCustomQueryPacket custom -> {
                channel = custom.payload().id().toString() + " id " + custom.transactionId();
                hex = hex(protocol, packet);
            }
            case ServerboundCustomQueryAnswerPacket custom -> {
                channel = packet.getClass().getName() + " id " + custom.transactionId();
                hex = hex(protocol, packet);
            }
            default -> {}
        }

        if (channel != null && hex != null) {
            LOGGER.info(MARKER, "{} {} {} {}\n{}", side(side), dir(flow), packet.getClass().getName(), channel, hex);
        } else if (channel != null) {
            LOGGER.info(MARKER, "{} {} {} {}", side(side), dir(flow), packet.getClass().getName(), channel);
        } else {
            LOGGER.info(MARKER, "{} {} {}", side(side), dir(flow), packet.getClass().getName());
        }
    }

    private static String side(PacketFlow side) {
        return side == PacketFlow.CLIENTBOUND ? "CLIENT" : "SERVER";
    }
    private static String dir(PacketFlow flow) {
        return flow == PacketFlow.CLIENTBOUND ? "S->C" : "C->S";
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static <T extends PacketListener> String hex(ProtocolInfo protocol, Packet packet) {
        var buf = Unpooled.buffer();
        protocol.codec().encode(buf, packet);
        return HexDumper.dump(buf);
    }
}
