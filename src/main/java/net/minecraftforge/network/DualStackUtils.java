/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network;

import net.minecraft.client.multiplayer.resolver.ResolvedServerAddress;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.multiplayer.resolver.ServerNameResolver;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.HttpUtil;
import net.minecraftforge.common.ForgeConfig;
import net.minecraftforge.server.ServerLifecycleHooks;
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

    private static boolean doneInitialChecks = false;

    /**
     * Called by {@link net.minecraftforge.common.MinecraftForge} to load this class so that the initial network
     * property constants are set before any of the other methods in this class are called.
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

        if (hostAddr.isPresent())
        {
            @Nullable
            final var address = hostAddr.get().getAddress();

            if (!doneInitialChecks)
                doInitialChecks(address);

            return checkIPv6(address);
        }
        else
        {
            return false;
        }
    }

    /**
     * Checks if an address is an IPv6 one or an IPv4 one, lets Netty know accordingly and returns the result.
     *
     * @param inetAddress The address you want to check
     * @return true if IPv6, false if IPv4
     */
    public static boolean checkIPv6(final InetAddress inetAddress)
    {
        if (!doneInitialChecks)
            doInitialChecks(inetAddress);

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
            final String addr = inetAddress == null ? "null" : inetAddress.getHostAddress();
            LOGGER.debug("Unable to determine IP version of address: \"" + addr + "\"");
            if (INITIAL_PREFER_IPv4_STACK.equalsIgnoreCase("false") && INITIAL_PREFER_IPv6_ADDRESSES.equalsIgnoreCase("true"))
            {
                LOGGER.debug("Assuming IPv6 as Java was explicitly told to prefer it.");
                System.setProperty("java.net.preferIPv4Stack", "false");
                System.setProperty("java.net.preferIPv6Addresses", "true");
                return true;
            }

            LOGGER.debug("Assuming IPv4.");
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

    /**
     * Perform initial checks for potential network configuration issues and log detailed warnings with possible
     * solutions if any are found.
     * @param inetAddress The resolved address
     */
    public static void doInitialChecks(final InetAddress inetAddress) {
        final var serverInstance = ServerLifecycleHooks.getCurrentServer();
        if (serverInstance == null || !serverInstance.isDedicatedServer())
            return;

        if (!ForgeConfig.COMMON.warnOnKnownNetworkingIssues.get())
        {
            LOGGER.debug("Skipping initial network configuration checks as they are disabled in the forge-common.toml.");
            doneInitialChecks = true;
            return;
        }

        final var dedicatedServer = (DedicatedServer) serverInstance;

        // warn the server owner if they have preventProxyConnections enabled and are in IPv6 or dual-stack mode
        final boolean preventProxyConnections = dedicatedServer.getProperties().preventProxyConnections;
        if (preventProxyConnections && INITIAL_PREFER_IPv4_STACK.equalsIgnoreCase("false"))
        {
            LOGGER.warn("Forge has detected a potential network configuration issue which may prevent some players from connecting to your server.");
            LOGGER.warn("prevent-proxy-connections is set to true in the server.properties and Forge is running in IPv6-only or dual-stack (both IPv4 and IPv6) mode.");
            LOGGER.warn("Some ISPs rely on tunnel brokers or special network configurations to provide IPv6 connectivity to their customers, " +
                    "which may prevent players from connecting to your server with this setting enabled.");
            LOGGER.warn("");
            LOGGER.warn("Possible solutions (you may need to do a combination of these):");
            LOGGER.warn("  - Disable the prevent-proxy-connections setting in server.properties.");
            LOGGER.warn("  - Turn off this warning in the forge-common.toml config file.");
            LOGGER.warn("");
            LOGGER.warn("If you need help, contact your server host or ask in the Forge forums or Discord server.");
        }

        // get the server-ip from server.properties
        String ip = dedicatedServer.getProperties().serverIp.trim();
        final boolean ipIsDomainName = ip.matches("\\.[a-zA-Z]{2,}$"); // true if IP ends with a TLD (e.g. example.com)

        if (ipIsDomainName)
        {
            // TODO: resolve the address to an IP using DNS-over-HTTPS, then use that IP for the checks below
            doneInitialChecks = true;
            return;
        }

        if (inetAddress instanceof Inet6Address)
        {
            // if the server-ip isn't an IPv6 address but Java is set to prefer IPv6 addresses, warn the server owner
            if (!ip.contains(":") && (INITIAL_PREFER_IPv4_STACK.equalsIgnoreCase("false")
                                      || INITIAL_PREFER_IPv6_ADDRESSES.equalsIgnoreCase("true")))
            {
                LOGGER.warn("Forge has detected a potential network configuration issue which may prevent some players from connecting to your server.");
                LOGGER.warn("Java has been explicitly told to prefer IPv6 addresses, but the server-ip in " +
                        "server.properties is explicitly set to \"" + ip + "\" which is an IPv4 address.");
                LOGGER.warn("");
                LOGGER.warn("Possible solutions (you may need to do a combination of these):");
                LOGGER.warn("  - Don't explicitly set the server-ip in server.properties - leave it blank instead (i.e.: \"server-ip=\").");
                LOGGER.warn("  - Set the server-ip in server.properties to server-ip=0000:0000:0000:0000:0000:0000:0000:0000");
                LOGGER.warn("  - Verify that your network adapter has IPv6 properly enabled and configured correctly.");
                if (INITIAL_PREFER_IPv4_STACK.equalsIgnoreCase("false"))
                    LOGGER.warn("  - Remove the explicit -Djava.net.preferIPv4Stack=false from your JVM arguments.");
                if (INITIAL_PREFER_IPv6_ADDRESSES.equalsIgnoreCase("true"))
                    LOGGER.warn("  - Remove the explicit -Djava.net.preferIPv6Addresses=true from your JVM arguments or change it from \"true\" to \"system\".");
                LOGGER.warn("");
                LOGGER.warn("If you need help, contact your server host or ask in the Forge forums or Discord server.");
            }
        }
        else
        {
            if (ip.startsWith("[") && ip.endsWith("]"))
                ip = ip.substring(1, ip.length() - 1); // Remove square brackets from the IPv6 address if present

            // if the server-ip isn't an IPv4 address but Java is set to prefer IPv4 addresses, warn the server owner
            if (ip.contains(":") && (INITIAL_PREFER_IPv4_STACK.equalsIgnoreCase("true")
                                     || INITIAL_PREFER_IPv6_ADDRESSES.equalsIgnoreCase("false")))
            {
                LOGGER.warn("Forge has detected a potential network configuration issue which may prevent some players from connecting to your server.");
                LOGGER.warn("Java has been explicitly told to prefer IPv4 addresses, but the server-ip in " +
                        "server.properties is explicitly set to \"" + ip + "\" which is an IPv6 address.");
                LOGGER.warn("");
                LOGGER.warn("Possible solutions (you may need to do a combination of these):");
                LOGGER.warn("  - Don't explicitly set the server-ip in server.properties - leave it blank instead (i.e.: \"server-ip=\").");
                LOGGER.warn("  - Verify that your network adapter has IPv6 properly enabled and configured correctly.");
                if (INITIAL_PREFER_IPv4_STACK.equalsIgnoreCase("true"))
                    LOGGER.warn("  - Remove the explicit -Djava.net.preferIPv4Stack=true from your JVM arguments.");
                if (INITIAL_PREFER_IPv6_ADDRESSES.equalsIgnoreCase("false"))
                    LOGGER.warn("  - Remove the explicit -Djava.net.preferIPv6Addresses=false from your JVM arguments or change it from \"false\" to \"system\".");
                LOGGER.warn("  - Turn off this warning in the forge-common.toml config file.");
                LOGGER.warn("");
                LOGGER.warn("Please note that disabling IPv6 may increase your server's ping time and latency as players will be forced to use IPv4, " +
                        "which usually requires additional network hops through multiple layers of CG-NAT to reach your server.");
                LOGGER.warn("Disabling IPv6 should be considered a last resort and can cause further connection issues for some players if done improperly.");
                LOGGER.warn("");
                LOGGER.warn("If you need help, contact your server host or ask in the Forge forums or Discord server.");
            }
        }

        doneInitialChecks = true;
    }
}
