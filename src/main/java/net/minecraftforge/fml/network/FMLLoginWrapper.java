/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.network;

import io.netty.buffer.Unpooled;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.event.EventNetworkChannel;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Wrapper for custom login packets. Transforms unnamed login channel messages into channels dispatched the same
 * as regular custom packets.
 */
public class FMLLoginWrapper {
    private static final Logger LOGGER = LogManager.getLogger();
    static final ResourceLocation WRAPPER = new ResourceLocation("fml:loginwrapper");
    private EventNetworkChannel wrapperChannel;

    FMLLoginWrapper() {
        wrapperChannel = NetworkRegistry.ChannelBuilder.named(FMLLoginWrapper.WRAPPER).
                clientAcceptedVersions(a->true).
                serverAcceptedVersions(a->true).
                networkProtocolVersion(()-> FMLNetworkConstants.NETVERSION)
                .eventNetworkChannel();
        wrapperChannel.addListener(this::wrapperReceived);
    }

    private <T extends NetworkEvent> void wrapperReceived(final T packet) {
        // we don't care about channel registration change events on this channel
        if (packet instanceof NetworkEvent.ChannelRegistrationChangeEvent) return;
        final NetworkEvent.Context wrappedContext = packet.getSource().get();
        final PacketBuffer payload = packet.getPayload();
        ResourceLocation targetNetworkReceiver = FMLNetworkConstants.FML_HANDSHAKE_RESOURCE;
        PacketBuffer data = null;
        if (payload != null) {
            targetNetworkReceiver = payload.readResourceLocation();
            final int payloadLength = payload.readVarInt();
            data = new PacketBuffer(payload.readBytes(payloadLength));
        }
        final int loginSequence = packet.getLoginIndex();
        LOGGER.debug(FMLHandshakeHandler.FMLHSMARKER, "Recieved login wrapper packet event for channel {} with index {}", targetNetworkReceiver, loginSequence);
        final NetworkEvent.Context context = new NetworkEvent.Context(wrappedContext.getNetworkManager(), wrappedContext.getDirection(), new PacketDispatcher((rl, buf) -> {
            LOGGER.debug(FMLHandshakeHandler.FMLHSMARKER, "Dispatching wrapped packet reply for channel {} with index {}", rl, loginSequence);
            wrappedContext.getPacketDispatcher().sendPacket(WRAPPER, this.wrapPacket(rl, buf));
        }));
        final NetworkEvent.LoginPayloadEvent loginPayloadEvent = new NetworkEvent.LoginPayloadEvent(data, () -> context, loginSequence);
        NetworkRegistry.findTarget(targetNetworkReceiver).ifPresent(ni -> {
            ni.dispatchLoginPacket(loginPayloadEvent);
            wrappedContext.setPacketHandled(context.getPacketHandled());
        });
    }

    private PacketBuffer wrapPacket(final ResourceLocation rl, final PacketBuffer buf) {
        PacketBuffer pb = new PacketBuffer(Unpooled.buffer(buf.capacity()));
        pb.writeResourceLocation(rl);
        pb.writeVarInt(buf.readableBytes());
        pb.writeBytes(buf);
        return pb;
    }

    void sendServerToClientLoginPacket(final ResourceLocation resourceLocation, final PacketBuffer buffer, final int index, final NetworkManager manager) {
        PacketBuffer pb = wrapPacket(resourceLocation, buffer);
        manager.sendPacket(NetworkDirection.LOGIN_TO_CLIENT.buildPacket(Pair.of(pb, index), WRAPPER).getThis());
    }
}
