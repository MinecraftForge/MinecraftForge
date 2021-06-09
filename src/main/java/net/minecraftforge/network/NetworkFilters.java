/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

import java.util.Map;
import java.util.function.Function;

import net.minecraft.network.NetworkManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableMap;

public class NetworkFilters
{

    private static final Logger LOGGER = LogManager.getLogger();

    private static final Map<String, Function<NetworkManager, VanillaPacketFilter>> instances = ImmutableMap.of(
            "forge:vanilla_filter", manager -> new VanillaConnectionNetworkFilter(),
            "forge:forge_fixes", ForgeConnectionNetworkFilter::new
    );

    public static void injectIfNecessary(NetworkManager manager)
    {
        instances.forEach((key, filterFactory) -> {
            VanillaPacketFilter filter = filterFactory.apply(manager);
            if (filter.isNecessary(manager))
            {
                manager.channel().pipeline().addBefore("packet_handler", key, filter);
                LOGGER.debug("Injected {} into {}", filter, manager);
            }
        });
    }

    private NetworkFilters()
    {
    }

}
