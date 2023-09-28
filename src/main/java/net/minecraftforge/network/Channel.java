/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network;

import java.util.Objects;

import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.packets.LoginWrapper;

public abstract class Channel<MSG> {
    protected final NetworkInstance instance;

    protected Channel(NetworkInstance instance) {
        this.instance = instance;
    }

    public ResourceLocation getName() {
        return instance.getChannelName();
    }

    public int getProtocolVersion() {
        return instance.getNetworkProtocolVersion();
    }

    /**
     * Returns true if the channel is present in the given connection.
     */
    public boolean isRemotePresent(Connection connection) {
        return NetworkContext.get(connection).getRemoteChannels().contains(getName());
    }

    public abstract FriendlyByteBuf toBuffer(MSG message);

    // Package private so we can call from ourselves.
    Packet<?> toVanillaPacket(Connection connection, MSG message) {
        var protocol = connection.getProtocol();
        var serverbound = connection.getSending() == PacketFlow.SERVERBOUND;
        var data = toBuffer(message);

        if (protocol == ConnectionProtocol.LOGIN) {
            // Login Protocol C->S packets do not contain the plugin channel name. As they are meant to be replies.
            // So fuck it lets just wrap in our own packet that DOES include the channel name
            if (serverbound) {
                if (this != NetworkInitialization.LOGIN)
                    return NetworkInitialization.LOGIN.toVanillaPacket(connection, new LoginWrapper(getName(), data));
                else
                    return NetworkDirection.LOGIN_TO_SERVER.buildPacket(data, getName()).getThis();
            }
            return NetworkDirection.LOGIN_TO_CLIENT.buildPacket(data, getName()).getThis();
        } else if (protocol == ConnectionProtocol.PLAY || protocol == ConnectionProtocol.CONFIGURATION) {
            var dir = serverbound ? NetworkDirection.PLAY_TO_SERVER : NetworkDirection.PLAY_TO_CLIENT;
            return dir.buildPacket(data, getName()).getThis();
        } else
            throw new IllegalStateException("Unsupported protocol " + protocol.name() + " in Forge Networking Channel");
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
        target.send(target.direction().buildPacket(toBuffer(msg), getName()).getThis());
    }

    public void reply(MSG msg, CustomPayloadEvent.Context context) {
        send(msg, context.getConnection());
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
