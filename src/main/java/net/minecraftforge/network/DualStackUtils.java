/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network;

import net.minecraft.client.multiplayer.resolver.ResolvedServerAddress;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.multiplayer.resolver.ServerNameResolver;
import net.minecraft.util.HttpUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Optional;

public class DualStackUtils
{
    private static final String INITIAL_PREFER_IPv4_STACK = System.getProperty("java.net.preferIPv4Stack");
    private static final String INITIAL_PREFER_IPv6_ADDRESSES = System.getProperty("java.net.preferIPv6Addresses");

    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Called by {@link net.minecraftforge.common.MinecraftForge} to load this class so that the initial network
     * property constants are set before any of the other methods in this class are called. This is so we can
     * distinguish what Java's read once on JVM start vs what we've set for Netty.
     */
    @ApiStatus.Internal
    public static void initialise() {}

    /**
     * Resolve the address and see if Java and the OS return an IPv6 or IPv4 one, then let Netty know
     * accordingly (it doesn't understand the {@code java.net.preferIPv6Addresses=system} property).
     *
     * @param hostAddress The address you want to check
     * @return true if IPv6, false if IPv4
     */
    public static boolean checkIPv6(final String hostAddress)
    {
        final Optional<InetSocketAddress> hostAddr =
                ServerNameResolver.DEFAULT
                        .resolveAddress(ServerAddress.parseString(hostAddress))
                        .map(ResolvedServerAddress::asInetSocketAddress);

        if (hostAddr.isPresent()) return checkIPv6(hostAddr.get().getAddress());
        else return false;
    }

    /**
     * Checks if an address is an IPv6 one or an IPv4 one, lets Netty know accordingly and returns the result.
     *
     * @param inetAddress The address you want to check
     * @return true if IPv6, false if IPv4
     */
    public static boolean checkIPv6(final InetAddress inetAddress)
    {
        if (inetAddress instanceof Inet6Address addr)
        {
            LOGGER.debug("Detected IPv6 address: \"" + addr.getHostAddress() + "\"");
            System.setProperty("java.net.preferIPv4Stack", "false");
            System.setProperty("java.net.preferIPv6Addresses", "true");
            return true;
        }
        else if (inetAddress instanceof Inet4Address addr)
        {
            LOGGER.debug("Detected IPv4 address: \"" + addr.getHostAddress() + "\"");
            System.setProperty("java.net.preferIPv4Stack", "true");
            System.setProperty("java.net.preferIPv6Addresses", "false");
            return false;
        }
        else
        {
            final String addr = inetAddress == null ? "null" : "\"" + inetAddress.getHostAddress() + "\"";
            LOGGER.debug("Unable to determine IP version of address: " + addr);
            if (INITIAL_PREFER_IPv4_STACK.equalsIgnoreCase("false") && INITIAL_PREFER_IPv6_ADDRESSES.equalsIgnoreCase("true"))
            {
                LOGGER.debug("Assuming IPv6 as Java was explicitly told to prefer it...");
                System.setProperty("java.net.preferIPv4Stack", "false");
                System.setProperty("java.net.preferIPv6Addresses", "true");
                return true;
            }

            LOGGER.debug("Assuming IPv4...");
            System.setProperty("java.net.preferIPv4Stack", "true");
            System.setProperty("java.net.preferIPv6Addresses", "false");
            return false;
        }
    }

    /**
     * Get the device's local IP address, taking into account scenarios where the client's network adapter
     * supports IPv6 and has it enabled but the router's LAN does not.
     *
     * @return the client's local IP address or {@code null} if unable to determine it
     */
    @Nullable
    public static InetAddress getLocalAddress()
    {
        final InetAddress localAddr = new InetSocketAddress(HttpUtil.getAvailablePort()).getAddress();
        if (localAddr.isAnyLocalAddress()) return localAddr;

        try
        {
            return InetAddress.getByName("localhost");
        }
        catch (final UnknownHostException e)
        {
            return null;
        }
    }

    /**
     * Used for the "Open to LAN" feature.
     * @return The multicast group to use for LAN discovery - IPv6 if available, IPv4 otherwise.
     */
    public static String getMulticastGroup() {
        if (checkIPv6(getLocalAddress())) return "FF75:230::60";
        else return "224.0.2.60";
    }
}
