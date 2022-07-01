/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network;

import net.minecraft.client.multiplayer.resolver.ResolvedServerAddress;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.multiplayer.resolver.ServerNameResolver;
import net.minecraft.util.HttpUtil;

import javax.annotation.Nullable;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Optional;

public class DualStackUtils
{
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
        if (inetAddress instanceof Inet6Address)
        {
            System.setProperty("java.net.preferIPv4Stack", "false");
            System.setProperty("java.net.preferIPv6Addresses", "true");
            return true;
        }
        else
        {
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

    public static String getMulticastGroup() {
        if (checkIPv6(getLocalAddress())) return "FF75:230::60";
        else return "224.0.2.60";
    }
}
