/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.packets.LoginWrapper;

public abstract class Channel<MSG> {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker MARKER = MarkerManager.getMarker("CHANNEL");
    protected final NetworkInstance instance;

    protected Channel(NetworkInstance instance) {
        this.instance = instance;
    }

    public ResourceLocation getName() {
        return instance.getChannelName();
    }

    /**
     * Retrieves the channel name to use for the specified packet
     * This typically is the main channel's name, but in some cases
     * you want to specify a custom one. If your channel has {@link NetworkInstance#addChild(ResourceLocation) children}
     */
    ResourceLocation getName(MSG packet) {
        return getName();
    }

    public int getProtocolVersion() {
        return instance.getNetworkProtocolVersion();
    }

    /**
     * Returns true if the channel is present in the given connection.
     */
    public boolean isRemotePresent(Connection connection) {
        return instance.isRemotePresent(connection);
    }

    public abstract void encode(FriendlyByteBuf out, MSG message);

    protected Packet<?> toVanillaPacket(Connection connection, MSG message) {
        var protocol = connection.getProtocol();
        var handler = switch (protocol) {
            case LOGIN         -> NetworkProtocol.LOGIN;
            case CONFIGURATION -> NetworkProtocol.CONFIGURATION;
            case PLAY          -> NetworkProtocol.PLAY;
            default -> throw new IllegalStateException("Unsupported protocol " + protocol.name() + " in Forge Networking Channel");
        };

        // Login Protocol C->S packets do not contain the plugin channel name. As they are meant to be replies.
        // So fuck it lets just wrap in our own packet that DOES include the channel name
        if (protocol == ConnectionProtocol.LOGIN && this != NetworkInitialization.LOGIN && connection.getSending() == PacketFlow.SERVERBOUND)
            return NetworkInitialization.LOGIN.toVanillaPacket(connection, new LoginWrapper(this, message));

        return handler.buildPacket(connection.getSending(), this, message);
    }

    public void send(MSG msg, Connection connection) {
        connection.send(toVanillaPacket(connection, msg));
    }

    /**
     * Send a message to the {@link PacketDistributor.PacketTarget} from a {@link PacketDistributor} instance.
     *
     * <pre>
     *     channel.send(message, PacketDistributor.PLAYER.with(()->player))
     * </pre>
     *
     * @param target The curried target from a PacketDistributor
     * @param message The message to send
     * @param <MSG> The type of the message
     */
    public void send(MSG msg, PacketDistributor.PacketTarget target) {
        target.send(target.direction().buildPacket(this, msg));
    }

    public void reply(MSG msg, CustomPayloadEvent.Context context) {
        send(msg, context.getConnection());
    }

    protected void validate(Object id, Connection con, NetworkProtocol<?> protocol, PacketFlow direction, boolean sending) {
        var actualD = sending ? con.getSending() : con.getReceiving();
        var actualP = con.getProtocol();
        var expectedP = protocol == null ? actualP : protocol.toVanilla();
        var expectedD = direction == null ? actualD : direction;

        if (expectedP != actualP || expectedD != actualD) {
            var error = "Illegal packet " + (sending ? "sent" : "received") + ", terminating connection. " + id + " expected " +
                expectedD + " " + expectedP + " but was " +
                actualD + " " + actualP;

            LOGGER.error(MARKER, error);
            con.disconnect(Component.literal(error));
            throw new IllegalStateException(error);
        }
    }

    @FunctionalInterface
    public static interface VersionTest {
        public static final VersionTest ACCEPT_MISSING = (status, version) -> status == Status.MISSING;
        public static final VersionTest ACCEPT_VANILLA = (status, version) -> status == Status.VANILLA;
        public static VersionTest exact(int version) {
            return (status, remoteVersion) -> status == Status.PRESENT && version == remoteVersion;
        }

        public enum Status {
            /**
             * This value is used when the connection has not specified that it is modded,
             * and has not sent a channel list. So we must assume that it is a unmodified vanilla environment.
             */
            VANILLA,
            /**
             * This is used when we are on a modded connection that has sent a channel version
             * list, but that list does not contain this channel.
             */
            MISSING,
            /**
             * This is used when we are on a modded connection, the channel version list has been received
             * and this channel is in the list. This is the only time that the version parameter of accept
             * is valid.
             */
            PRESENT;
        }

        /**
         * Tests is the specified version is compatible with this channel.
         * This determines if a we can communicate with a remote end.
         *
         * The status parameter specifies wither or not the remote knows about
         * out channel or not. If the value is {@link Status#MISSING MISSING}
         * or {@link Status#VANILLA VANILLA} then the version parameter is meaningless.
         *
         * If the status is {@link Status#PRESSENT PRESSENT} then the version
         * parameter is the version presented by the remote connection.
         *
         * @return true if we should allow the connection
         */
        boolean accepts(Status status, int version);


        /**
         * Returns a instance that represents the logical negation of this
         * instance.
         */
        default VersionTest negate() {
            return (status, version) -> !accepts(status, version);
        }

        /**
         * Returns a composed instance that represents a short-circuiting logical
         * AND of this instance and another.  When evaluating the composed
         * instance, if this instance is {@code false}, then the {@code other}
         * instance is not evaluated.
         */
        default VersionTest and(VersionTest other) {
            Objects.requireNonNull(other);
            return (status, version) -> accepts(status, version) && other.accepts(status, version);
        }

        /**
         * Returns a composed instance that represents a short-circuiting logical
         * OR of this instance and another.  When evaluating the composed
         * instance, if this instance is {@code true}, then the {@code other}
         * instance is not evaluated.
         */
        default VersionTest or(VersionTest other) {
            Objects.requireNonNull(other);
            return (status, version) -> accepts(status, version) || other.accepts(status, version);
        }
    }
}
