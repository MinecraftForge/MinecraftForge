/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network;

import io.netty.buffer.Unpooled;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.event.EventNetworkChannel;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;

/**
 * Wrapper for custom login packets. Transforms unnamed login channel messages into channels dispatched the same
 * as regular custom packets.
 */
public class LoginWrapper
{
    private static final Logger LOGGER = LogManager.getLogger();
    @ApiStatus.Internal
    public static final ResourceLocation WRAPPER = new ResourceLocation("fml:loginwrapper");
    private EventNetworkChannel wrapperChannel;

    LoginWrapper() {
        wrapperChannel = NetworkRegistry.ChannelBuilder.named(LoginWrapper.WRAPPER).
                clientAcceptedVersions(a->true).
                serverAcceptedVersions(a->true).
                networkProtocolVersion(()-> NetworkConstants.NETVERSION)
                .eventNetworkChannel();
        wrapperChannel.addListener(this::wrapperReceived);
    }

    private <T extends NetworkEvent> void wrapperReceived(final T packet) {
        // we don't care about channel registration change events on this channel
        if (packet instanceof NetworkEvent.ChannelRegistrationChangeEvent) return;
        final NetworkEvent.Context wrappedContext = packet.getSource().get();
        final FriendlyByteBuf payload = packet.getPayload();
        ResourceLocation targetNetworkReceiver = NetworkConstants.FML_HANDSHAKE_RESOURCE;
        FriendlyByteBuf data = null;
        if (payload != null) {
            targetNetworkReceiver = payload.readResourceLocation();
            final int payloadLength = payload.readVarInt();
            data = new FriendlyByteBuf(payload.readBytes(payloadLength));
        }
        final int loginSequence = packet.getLoginIndex();
        LOGGER.debug(HandshakeHandler.FMLHSMARKER, "Recieved login wrapper packet event for channel {} with index {}", targetNetworkReceiver, loginSequence);
        final NetworkEvent.Context context = new NetworkEvent.Context(wrappedContext.getNetworkManager(), wrappedContext.getDirection(), (rl, buf) -> {
            LOGGER.debug(HandshakeHandler.FMLHSMARKER, "Dispatching wrapped packet reply for channel {} with index {}", rl, loginSequence);
            wrappedContext.getPacketDispatcher().sendPacket(WRAPPER, this.wrapPacket(rl, buf));
        });
        final NetworkEvent.LoginPayloadEvent loginPayloadEvent = new NetworkEvent.LoginPayloadEvent(data, () -> context, loginSequence);
        NetworkRegistry.findTarget(targetNetworkReceiver).ifPresent(ni -> {
            ni.dispatchLoginPacket(loginPayloadEvent);
            wrappedContext.setPacketHandled(context.getPacketHandled());
        });
    }

    private FriendlyByteBuf wrapPacket(final ResourceLocation rl, final FriendlyByteBuf buf) {
        FriendlyByteBuf pb = new FriendlyByteBuf(Unpooled.buffer(buf.capacity()));
        pb.writeResourceLocation(rl);
        pb.writeVarInt(buf.readableBytes());
        pb.writeBytes(buf);
        return pb;
    }

    void sendServerToClientLoginPacket(final ResourceLocation resourceLocation, final FriendlyByteBuf buffer, final int index, final Connection manager) {
        FriendlyByteBuf pb = wrapPacket(resourceLocation, buffer);
        manager.send(NetworkDirection.LOGIN_TO_CLIENT.buildPacket(Pair.of(pb, index), WRAPPER).getThis());
    }
}
