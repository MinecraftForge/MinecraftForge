/*
 * Minecraft Forge
 * Copyright (c) 2016-2022.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
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

public class DualStackUtils {
    /**
     * Resolve the address and see if Java and the OS return an IPv6 or IPv4 one, then let netty know
     * accordingly (it doesn't understand the java.net.preferIPv6Addresses=system property)
     *
     * @author Paint_Ninja
     * @param hostAddress The address you want to check
     * @return true if IPv6, false if IPv4
     */
    public static boolean isIPv6(final String hostAddress) {
        final Optional<InetSocketAddress> hostAddr =
                ServerNameResolver.DEFAULT
                        .resolveAddress(ServerAddress.parseString(hostAddress))
                        .map(ResolvedServerAddress::asInetSocketAddress);

        if (hostAddr.isPresent()) return isIPv6(hostAddr.get().getAddress());
        else return false;
    }

    /**
     * Checks if an address is an IPv6 one or an IPv4 one, then lets netty know accordingly and returns the result
     *
     * @author Paint_Ninja
     * @param inetAddress The address you want to check
     * @return true if IPv6, false if IPv4
     */
    public static boolean isIPv6(final InetAddress inetAddress) {
        if (inetAddress instanceof Inet6Address) {
            System.setProperty("java.net.preferIPv4Stack", "false");
            System.setProperty("java.net.preferIPv6Addresses", "true");
            return true;
        } else {
            System.setProperty("java.net.preferIPv4Stack", "true");
            System.setProperty("java.net.preferIPv6Addresses", "false");
            return false;
        }
    }
    
    /**
     * Get the device's local IP address, taking into account scenarios where the client's network adapter
     * supports IPv6 and has it enabled but the router's LAN does not.
     *
     * @author Paint_Ninja
     * @return the client's local IP address or null if unable
     */
    @Nullable
    public static InetAddress getLocalAddress() {
        final InetAddress localAddr = new InetSocketAddress(HttpUtil.getAvailablePort()).getAddress();
        if (localAddr.isAnyLocalAddress()) return localAddr;

        try {
            return InetAddress.getByName("localhost");
        } catch (final UnknownHostException e) {
            return null;
        }
    }
}
