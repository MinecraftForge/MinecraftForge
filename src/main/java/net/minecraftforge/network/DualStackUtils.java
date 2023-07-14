/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network;

import com.mojang.logging.LogUtils;
import com.google.common.net.InetAddresses;
import net.minecraft.client.multiplayer.resolver.ResolvedServerAddress;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.multiplayer.resolver.ServerNameResolver;
import net.minecraft.util.HttpUtil;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.net.Inet4Address;
import net.minecraftforge.common.ForgeConfig;

import javax.annotation.Nullable;
import java.net.SocketAddress;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Optional;

public class DualStackUtils
{
    private static final String INITIAL_PREFER_IPv4_STACK = System.getProperty("java.net.preferIPv4Stack") == null ? "false" : System.getProperty("java.net.preferIPv4Stack");
    private static final String INITIAL_PREFER_IPv6_ADDRESSES = System.getProperty("java.net.preferIPv6Addresses") == null ? "false" : System.getProperty("java.net.preferIPv6Addresses");

    private static final Logger LOGGER = LogUtils.getLogger();

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
        // only log debug messages if we're not in the server pinger thread, as otherwise it's unclear which IP
        // corresponds to which server as soon as you have more than one server in the multiplayer server list
        final String currentThreadName = Thread.currentThread().getName();
        final boolean shouldLogDebug = !currentThreadName.contains("Server Pinger #");

        if (inetAddress instanceof Inet6Address addr)
        {
            if (shouldLogDebug)
                LOGGER.debug("Detected IPv6 address: \"" + addr.getHostAddress() + "\"");

            System.setProperty("java.net.preferIPv4Stack", "false");
            System.setProperty("java.net.preferIPv6Addresses", "true");
            return true;
        }
        else if (inetAddress instanceof Inet4Address addr)
        {
            if (shouldLogDebug)
                LOGGER.debug("Detected IPv4 address: \"" + addr.getHostAddress() + "\"");

            System.setProperty("java.net.preferIPv4Stack", "true");
            System.setProperty("java.net.preferIPv6Addresses", "false");
            return false;
        }
        else
        {
            if (shouldLogDebug) {
                final String addr = inetAddress == null ? "null" : "\"" + inetAddress.getHostAddress() + "\"";
                LOGGER.debug("Unable to determine IP version of address: " + addr);
            }

            if (INITIAL_PREFER_IPv4_STACK.equalsIgnoreCase("false") && INITIAL_PREFER_IPv6_ADDRESSES.equalsIgnoreCase("true"))
            {
                if (shouldLogDebug)
                    LOGGER.debug("Assuming IPv6 as Java was explicitly told to prefer it...");

                System.setProperty("java.net.preferIPv4Stack", "false");
                System.setProperty("java.net.preferIPv6Addresses", "true");
                return true;
            }

            if (shouldLogDebug)
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

    /**
     * Logs the initial values of the {@code java.net.preferIPv4Stack} and {@code java.net.preferIPv6Addresses} system
     * properties that Java has read on JVM start. Useful for debugging hostname lookup failures.
     */
    public static void logInitialPreferences() {
        LOGGER.debug("Initial IPv4 stack preference: " + INITIAL_PREFER_IPv4_STACK);
        LOGGER.debug("Initial IPv6 addresses preference: " + INITIAL_PREFER_IPv6_ADDRESSES);
    }

    /**
     * {@link SocketAddress#toString()} but with IPv6 address compression support
     */
    public static String getAddressString(final SocketAddress address) {
        if (address instanceof final InetSocketAddress inetAddress) {
            String formatted;
            if (inetAddress.isUnresolved()) {
                formatted = inetAddress.getHostName() + "/<unresolved>";
            } else {
                formatted = InetAddresses.toAddrString(inetAddress.getAddress());
                if (inetAddress.getAddress() instanceof Inet6Address)
                    formatted = '[' + formatted + ']';

                formatted = '/' + formatted;
            }

            return formatted + ':' + inetAddress.getPort();
        }

        return address.toString();
    }
}
