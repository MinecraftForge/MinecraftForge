/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.fml.network;

import io.netty.util.AttributeKey;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.event.EventNetworkChannel;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class NetworkRegistry
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker NETREGISTRY = MarkerManager.getMarker("NETREGISTRY");

    private static Map<ResourceLocation, NetworkInstance> instances = new HashMap<>();
    public static List<String> getNonVanillaNetworkMods()
    {
        return Collections.emptyList();
    }


    public static SimpleChannel newSimpleChannel(final ResourceLocation name, Supplier<String> networkProtocolVersion, Predicate<String> clientAcceptedVersions, Predicate<String> serverAcceptedVersions) {
        return new SimpleChannel(createInstance(name, networkProtocolVersion, clientAcceptedVersions, serverAcceptedVersions));
    }

    public static EventNetworkChannel newEventChannel(final ResourceLocation name, Supplier<String> networkProtocolVersion, Predicate<String> clientAcceptedVersions, Predicate<String> serverAcceptedVersions) {
        return new EventNetworkChannel(createInstance(name, networkProtocolVersion, clientAcceptedVersions, serverAcceptedVersions));
    }
    private static NetworkInstance createInstance(ResourceLocation name, Supplier<String> networkProtocolVersion, Predicate<String> clientAcceptedVersions, Predicate<String> serverAcceptedVersions)
    {
        final NetworkInstance networkInstance = new NetworkInstance(name, networkProtocolVersion, clientAcceptedVersions, serverAcceptedVersions);
        if (instances.containsKey(name)) {
            LOGGER.error(NETREGISTRY, "NetworkDirection channel {} already registered.", name);
            throw new IllegalArgumentException("NetworkDirection Channel {"+ name +"} already registered");
        }
        instances.put(name, networkInstance);
        return networkInstance;
    }

    static Optional<NetworkInstance> findTarget(ResourceLocation resourceLocation)
    {
        return Optional.ofNullable(instances.get(resourceLocation));
    }

    public static class ChannelBuilder {
        private ResourceLocation channelName;
        private Supplier<String> networkProtocolVersion;
        private Predicate<String> clientAcceptedVersions;
        private Predicate<String> serverAcceptedVersions;

        public static ChannelBuilder named(ResourceLocation channelName)
        {
            ChannelBuilder builder = new ChannelBuilder();
            builder.channelName = channelName;
            return builder;
        }

        public ChannelBuilder networkProtocolVersion(Supplier<String> networkProtocolVersion)
        {
            this.networkProtocolVersion = networkProtocolVersion;
            return this;
        }

        public ChannelBuilder clientAcceptedVersions(Predicate<String> clientAcceptedVersions)
        {
            this.clientAcceptedVersions = clientAcceptedVersions;
            return this;
        }

        public ChannelBuilder serverAcceptedVersions(Predicate<String> serverAcceptedVersions)
        {
            this.serverAcceptedVersions = serverAcceptedVersions;
            return this;
        }

        private NetworkInstance createNetworkInstance() {
            return createInstance(channelName, networkProtocolVersion, clientAcceptedVersions, serverAcceptedVersions);
        }

        public SimpleChannel simpleChannel() {
            return new SimpleChannel(createNetworkInstance());
        }

        public EventNetworkChannel eventNetworkChannel() {
            return new EventNetworkChannel(createNetworkInstance());
        }
    }
}
