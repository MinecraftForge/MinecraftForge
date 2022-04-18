/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network.filters;

import java.util.Map;
import java.util.function.Function;

import net.minecraft.network.Connection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableMap;

public class NetworkFilters
{

    private static final Logger LOGGER = LogManager.getLogger();

    private static final Map<String, Function<Connection, VanillaPacketFilter>> instances = ImmutableMap.of(
            "forge:vanilla_filter", manager -> new VanillaConnectionNetworkFilter(),
            "forge:forge_fixes", ForgeConnectionNetworkFilter::new
    );

    public static void injectIfNecessary(Connection manager)
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
