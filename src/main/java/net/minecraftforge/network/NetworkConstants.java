/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network;

import io.netty.util.AttributeKey;
import net.minecraft.client.multiplayer.resolver.ResolvedServerAddress;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.multiplayer.resolver.ServerNameResolver;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.IExtensionPoint.DisplayTest;
import net.minecraftforge.network.event.EventNetworkChannel;
import net.minecraftforge.network.simple.SimpleChannel;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import javax.annotation.Nullable;
import java.net.Inet6Address;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Optional;

/**
 * Constants related to networking
 */
public class NetworkConstants
{
    public static final String FMLNETMARKER = "FML";
    public static final int FMLNETVERSION = 2;
    public static final String NETVERSION = FMLNETMARKER + FMLNETVERSION;
    public static final String NOVERSION = "NONE";

    public static final boolean USE_IPV6 = shouldUseIPv6();

    static final Marker NETWORK = MarkerManager.getMarker("FMLNETWORK");
    static final AttributeKey<String> FML_NETVERSION = AttributeKey.valueOf("fml:netversion");
    static final AttributeKey<HandshakeHandler> FML_HANDSHAKE_HANDLER = AttributeKey.valueOf("fml:handshake");
    static final AttributeKey<MCRegisterPacketHandler.ChannelList> FML_MC_REGISTRY = AttributeKey.valueOf("minecraft:netregistry");
    static final AttributeKey<ConnectionData> FML_CONNECTION_DATA = AttributeKey.valueOf("fml:conndata");
    static final ResourceLocation FML_HANDSHAKE_RESOURCE = new ResourceLocation("fml:handshake");
    static final ResourceLocation FML_PLAY_RESOURCE = new ResourceLocation("fml:play");
    static final ResourceLocation MC_REGISTER_RESOURCE = new ResourceLocation("minecraft:register");
    static final ResourceLocation MC_UNREGISTER_RESOURCE = new ResourceLocation("minecraft:unregister");
    static final SimpleChannel handshakeChannel = NetworkInitialization.getHandshakeChannel();
    static final SimpleChannel playChannel = NetworkInitialization.getPlayChannel();
    static final List<EventNetworkChannel> mcRegChannels = NetworkInitialization.buildMCRegistrationChannels();
    /**
     * Return this value in your {@link DisplayTest} function to be ignored.
     */
    public static final String IGNORESERVERONLY = "OHNOES\uD83D\uDE31\uD83D\uDE31\uD83D\uDE31\uD83D\uDE31\uD83D\uDE31\uD83D\uDE31\uD83D\uDE31\uD83D\uDE31\uD83D\uDE31\uD83D\uDE31\uD83D\uDE31\uD83D\uDE31\uD83D\uDE31\uD83D\uDE31\uD83D\uDE31\uD83D\uDE31\uD83D\uDE31";

    public static String init() {
        return NetworkConstants.NETVERSION;
    }

    /**
     * Resolve a localhost address and see if Java and the OS return an IPv6 or IPv4 one, then let netty know
     * accordingly (it doesn't understand the java.net.preferIPv6Addresses=system property)
     *
     * TODO: check on a per-hostname-basis instead of just localhost, just in case
     *
     * @return true if IPv6 is used, false if IPv4 is used
     */
    public static boolean shouldUseIPv6() {
        final Optional<InetSocketAddress> localhostAddr =
                ServerNameResolver.DEFAULT.resolveAddress(ServerAddress.parseString("localhost")).map(ResolvedServerAddress::asInetSocketAddress);
        if (localhostAddr.isPresent() && localhostAddr.get().getAddress() instanceof Inet6Address) {
            System.setProperty("java.net.preferIPv4Stack", "false");
            System.setProperty("java.net.preferIPv6Addresses", "true");
            return true;
        } else {
            System.setProperty("java.net.preferIPv4Stack", "true");
            System.setProperty("java.net.preferIPv6Addresses", "false");
            return false;
        }
    }
}
