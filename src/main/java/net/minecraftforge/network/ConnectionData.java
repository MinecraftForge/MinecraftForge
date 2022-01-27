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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

public class ConnectionData
{
    private ImmutableMap<String, Pair<String, String>> modData;
    private ImmutableMap<ResourceLocation, String> channels;

    /* package private */ ConnectionData(Map<String, Pair<String, String>> modData, Map<ResourceLocation, String> channels)
    {
        this.modData = ImmutableMap.copyOf(modData);
        this.channels = ImmutableMap.copyOf(channels);
    }

    /**
     * Returns the list of mods present in the remote.
     * <b>WARNING: This list is not authoritative.
     *    A mod missing from the list does not mean the mod isn't there,
     *    and similarly a mod present in the list does not mean it is there.
     *    People using hacked clients WILL hack the mod lists to make them look correct.
     *    Do not use this as an anti-cheat feature!
     *  </b>
     * @return An immutable list of MODIDs
     */
    public ImmutableList<String> getModList()
    {
        return modData.keySet().asList();
    }

    /**
     * Returns the list of mods and respective mod names and versions present in the remote.
     * <b>WARNING: This list is not authoritative.
     *    A mod missing from the list does not mean the mod isn't there,
     *    and similarly a mod present in the list does not mean it is there.
     *    People using hacked clients WILL hack the mod lists to make them look correct.
     *    Do not use this as an anti-cheat feature!
     *  </b>
     * @return An immutable map of MODIDs and their respective mod versions
     */
    public ImmutableMap<String, Pair<String, String>> getModData()
    {
        return modData;
    }

    /**
     * Returns the list of impl channels present in the remote.
     * <b>WARNING: This list is not authoritative.
     *    A channel missing from the list does not mean the remote won't accept packets with that channel ID,
     *    and similarly a channel present in the list does not mean the remote won't ignore it.
     *    People using hacked clients WILL hack the channel list to make it look correct.
     *    Do not use this as an anti-cheat feature!
     *  </b>
     * @return An immutable map of channel IDs and their respective protocol versions.
     */
    public ImmutableMap<ResourceLocation, String> getChannels()
    {
        return channels;
    }

    /**
     * A class for holding the mod mismatch data of a failed handshake.
     * Contains a list of mismatched channels, the channels present on the side the handshake failed on, the mods with mismatching registries (if available) and the information of whether the mismatching data's origin is the server.
     * @param mismatchedChannelData The ids and respective mod versions of the mismatched or missing channels
     * @param presentChannelData The ids and respective mod names and versions of the present channels corresponding to the mismatched channels, stored like this: [channelId -> [modName, modVersion]]
     * @param missingRegistryModData The ids and names of mods that have mismatched registry data
     */
    public record ModMismatchData(Map<ResourceLocation, String> mismatchedChannelData, Map<ResourceLocation, Pair<String, String>> presentChannelData, Map<String, String> missingRegistryModData, boolean mismatchedDataFromServer)
    {
        public static ModMismatchData channel(Map<ResourceLocation, String> mismatchedChannels, ConnectionData connectionData, boolean mismatchedDataFromServer)
        {
            Map<ResourceLocation, String> mismatchedChannelData = toRemoteChannelData(mismatchedChannels, connectionData, mismatchedDataFromServer);
            Map<ResourceLocation, Pair<String, String>> presentChannelData = getLocalChannelData(mismatchedChannels, connectionData, mismatchedDataFromServer);

            return new ModMismatchData(mismatchedChannelData, presentChannelData, new HashMap<>(), mismatchedDataFromServer);
        }

        public static ModMismatchData registry(Multimap<ResourceLocation, ResourceLocation> missingRegistries)
        {
            Map<String, String> missingRegistryModData = missingRegistries.values().stream().map(ResourceLocation::getNamespace).distinct().map(ModMismatchData::getModDataFromId).collect(Collectors.toMap(Pair::getLeft, Pair::getRight));

            return new ModMismatchData(new HashMap<>(), new HashMap<>(), missingRegistryModData, true);
        }

        public static Map<ResourceLocation, String> toRemoteChannelData(Map<ResourceLocation, String> mismatchedChannels, ConnectionData connectionData, boolean mismatchedDataFromServer)
        {
            Map<String, String> remoteModVersions;

            if (mismatchedDataFromServer) { //enhance with data from the server
                remoteModVersions = connectionData != null ? connectionData.getModData().entrySet().stream().collect(Collectors.toMap(Entry::getKey, e -> e.getValue().getRight())) : new HashMap<>();
            }
            else { //enhance with local data
                remoteModVersions = ModList.get().getMods().stream().map(info -> Pair.of(info.getModId(), info.getVersion().toString())).collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
            }
            return mismatchedChannels.keySet().stream().map(channel -> Pair.of(channel, mismatchedChannels.get(channel).equals(NetworkRegistry.ABSENT) ? NetworkRegistry.ABSENT : remoteModVersions.getOrDefault(channel.getNamespace(), NetworkRegistry.ABSENT))).collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
        }

        public static Map<ResourceLocation, Pair<String, String>> getLocalChannelData(Map<ResourceLocation, String> mismatchedChannelsFilter, ConnectionData connectionData, boolean mismatchedDataFromServer)
        {
            Map<ResourceLocation, String> localChannelData;

            if (mismatchedDataFromServer) //mismatch happened on the client, use client data
                localChannelData = NetworkRegistry.buildChannelVersions();
            else { //mismatch happened on the server, use server data
                localChannelData = connectionData != null ? connectionData.getChannels() : new HashMap<>();
            }
            return localChannelData.keySet().stream().filter(mismatchedChannelsFilter::containsKey).map(channel -> getLocalModDataFromChannel(channel, connectionData, mismatchedDataFromServer)).collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
        }

        public static Pair<ResourceLocation, Pair<String, String>> getLocalModDataFromChannel(ResourceLocation channel, ConnectionData connectionData, boolean mismatchedDataFromServer)
        {
            if (mismatchedDataFromServer) {
                return ModList.get().getModContainerById(channel.getNamespace()).map(modContainer -> Pair.of(channel, Pair.of(modContainer.getModInfo().getDisplayName(), modContainer.getModInfo().getVersion().toString()))).orElse(Pair.of(channel, Pair.of(channel.getNamespace(), "")));
            } else {
                Map<String, Pair<String, String>> modData = connectionData != null ? connectionData.getModData() : new HashMap<>();
                return Pair.of(channel, modData.getOrDefault(channel.getNamespace(), Pair.of(channel.getNamespace(), "")));
            }
        }

        public static Pair<String, String> getModDataFromId(String id)
        {
            Optional<? extends ModContainer> mod = ModList.get().getModContainerById(id);

            return mod.map(modContainer -> Pair.of(id, modContainer.getModInfo().getDisplayName())).orElse(Pair.of(id, id));
        }

        public boolean containsMismatches()
        {
            return (mismatchedChannelData != null && !mismatchedChannelData.isEmpty()) || (missingRegistryModData != null && !missingRegistryModData.isEmpty());
        }
    }
}
