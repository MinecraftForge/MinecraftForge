/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
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
     * Returns a map of mods and respective mod names and versions present in the remote.
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
     * @param mismatchedModData The ids and respective mod versions of the mismatched or missing mods/channels
     * @param presentModData The ids and respective mod names and versions corresponding to the mismatched mods/channels, stored like this: [id -> [modName, modVersion]]
     * @param mismatchedDataFromServer Whether the mismatched data originates from the server. Note that if the mismatched data is from the server, the actual mismatch check happened on the client and vice versa
     */
    public record ModMismatchData(Map<ResourceLocation, String> mismatchedModData, Map<ResourceLocation, Pair<String, String>> presentModData, boolean mismatchedDataFromServer)
    {
        /**
         * Creates a ModMismatchData instance from given channel mismatch data, which is processed side-aware depending on the value of mismatchedDataFromServer
         */
        public static ModMismatchData channel(Map<ResourceLocation, String> mismatchedChannels, ConnectionData connectionData, boolean mismatchedDataFromServer)
        {
            Map<ResourceLocation, String> mismatchedChannelData = enhanceWithModData(mismatchedChannels, connectionData, mismatchedDataFromServer);
            Map<ResourceLocation, Pair<String, String>> presentChannelData = getChannelDataFromSide(mismatchedChannels.keySet(), connectionData, mismatchedDataFromServer);

            return new ModMismatchData(mismatchedChannelData, presentChannelData, mismatchedDataFromServer);
        }

        /**
         * Creates a ModMismatchData instance from given mismatched registry entries
         */
        public static ModMismatchData registry(Multimap<ResourceLocation, ResourceLocation> mismatchedRegistryData, ConnectionData connectionData)
        {
            List<ResourceLocation> mismatchedRegistryMods = mismatchedRegistryData.values().stream().map(ResourceLocation::getNamespace).distinct().map(id -> new ResourceLocation(id, "")).toList();
            Map<ResourceLocation, String> mismatchedRegistryModData = mismatchedRegistryMods.stream().map(id -> ModList.get().getModContainerById(id.getNamespace()).map(modContainer -> Pair.of(id, modContainer.getModInfo().getVersion().toString())).orElse(Pair.of(id, NetworkRegistry.ABSENT))).collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
            Map<ResourceLocation, Pair<String, String>> presentModData = getServerSideModData(mismatchedRegistryModData.keySet(), connectionData).entrySet().stream().collect(Collectors.toMap(e -> new ResourceLocation(e.getKey().getNamespace(), ""), Entry::getValue));

            return new ModMismatchData(mismatchedRegistryModData, presentModData, false); //false because mods marked with NetworkRegistry.ABSENT should be displayed as missing from the client, not the server
        }

        /**
         * Returns true if this ModMismatchData instance contains channel or registry mismatches
         */
        public boolean containsMismatches()
        {
            return mismatchedModData != null && !mismatchedModData.isEmpty();
        }

        private static Map<ResourceLocation, String> enhanceWithModData(Map<ResourceLocation, String> mismatchedChannels, ConnectionData connectionData, boolean mismatchedDataFromServer)
        {
            Map<String, String> remoteModVersions;

            if (mismatchedDataFromServer) //enhance with data from the server
            {
                remoteModVersions = connectionData != null ? connectionData.getModData().entrySet().stream().collect(Collectors.toMap(Entry::getKey, e -> e.getValue().getRight())) : Map.of();
            }
            else //enhance with data from the client
            {
                remoteModVersions = ModList.get().getMods().stream().map(info -> Pair.of(info.getModId(), info.getVersion().toString())).collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
            }
            return mismatchedChannels.keySet().stream().map(channel -> Pair.of(channel, mismatchedChannels.get(channel).equals(NetworkRegistry.ABSENT) ? NetworkRegistry.ABSENT : remoteModVersions.getOrDefault(channel.getNamespace(), NetworkRegistry.ABSENT))).collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
        }

        private static Map<ResourceLocation, Pair<String, String>> getChannelDataFromSide(Set<ResourceLocation> mismatchedChannelsFilter, ConnectionData connectionData, boolean fromClientSide)
        {
            Map<ResourceLocation, String> channelData;

            if (fromClientSide) //mismatch happened on the client, use client data
            {
                channelData = NetworkRegistry.buildChannelVersions();
            }
            else //mismatch happened on the server, use server data
            {
                channelData = connectionData != null ? connectionData.getChannels() : Map.of();
            }
            return channelData.keySet().stream().filter(mismatchedChannelsFilter::contains).map(id -> getModDataFromChannel(id, connectionData, fromClientSide)).collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
        }

        private static Pair<ResourceLocation, Pair<String, String>> getModDataFromChannel(ResourceLocation channel, ConnectionData connectionData, boolean fromClientSide)
        {
            if (fromClientSide)
            {
                return ModList.get().getModContainerById(channel.getNamespace()).map(modContainer -> Pair.of(channel, Pair.of(modContainer.getModInfo().getDisplayName(), modContainer.getModInfo().getVersion().toString()))).orElse(Pair.of(channel, Pair.of(channel.getNamespace(), "")));
            }
            else
            {
                Map<String, Pair<String, String>> modData = connectionData != null ? connectionData.getModData() : Map.of();
                return Pair.of(channel, modData.getOrDefault(channel.getNamespace(), Pair.of(channel.getNamespace(), "")));
            }
        }

        private static Map<ResourceLocation, Pair<String, String>> getServerSideModData(Set<ResourceLocation> mismatchedModsFilter, ConnectionData connectionData)
        {
            Map<String, Pair<String, String>> localChannelData = connectionData != null ? connectionData.getModData() : Map.of();
            Set<String> modIdFilter = mismatchedModsFilter.stream().map(ResourceLocation::getNamespace).collect(Collectors.toSet());
            return localChannelData.entrySet().stream().filter(e -> modIdFilter.contains(e.getKey())).collect(Collectors.toMap(e -> new ResourceLocation(e.getKey(), ""), Entry::getValue));
        }
    }
}
