package net.minecraftforge.network;

import java.util.Map;

import net.minecraft.network.NetworkManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableMap;

public class NetworkFilters
{

    private static final Logger LOGGER = LogManager.getLogger();

    private static final Map<String, TargetedNetworkFilter> instances = ImmutableMap.of(
            "forge:vanilla_filter", new VanillaConnectionNetworkFilter(),
            "forge:forge_fixes", new ForgeConnectionNetworkFilter()
    );

    public static void injectIfNecessary(NetworkManager manager)
    {
        instances.forEach((key, filter) -> {
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
