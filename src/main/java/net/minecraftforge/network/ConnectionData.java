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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

public class ConnectionData
{
    private ImmutableList<String> modList;
    private ImmutableMap<ResourceLocation, String> channels;

    /* package private */ ConnectionData(List<String> modList, Map<ResourceLocation, String> channels)
    {
        this.modList = ImmutableList.copyOf(modList);
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
        return modList;
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
     * @param mismatchedChannelData The ids, versions and respective mod names of the mismatched or missing channels, stored like this: [channelId -> [modName, channelVersion]]
     * @param presentChannelData The ids and versions of the present channels
     * @param missingRegistryModData The ids and names of mods that have mismatched registry data
     */
    public record ModMismatchData(Map<ResourceLocation, Pair<String, String>> mismatchedChannelData, Map<ResourceLocation, String> presentChannelData, Map<String, String> missingRegistryModData, boolean mismatchedDataFromServer)
    {
        public static ModMismatchData channel(Map<ResourceLocation, String> mismatchedChannels, Map<ResourceLocation, String> presentChannels, boolean mismatchedDataFromServer)
        {
            Map<ResourceLocation, Pair<String, String>> mismatchedChannelData = mismatchedChannels.entrySet().stream().map(e -> getModDataFromChannel(e.getKey(), e.getValue())).filter(Objects::nonNull).collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
            Map<ResourceLocation, String> presentChannelData = presentChannels.entrySet().stream().filter(e -> mismatchedChannels.containsKey(e.getKey())).collect(Collectors.toMap(Entry::getKey, Entry::getValue));

            return new ModMismatchData(mismatchedChannelData, presentChannelData, new HashMap<>(), mismatchedDataFromServer);
        }

        public static ModMismatchData registry(Multimap<ResourceLocation, ResourceLocation> missingRegistries) {
            Map<String, String> missingRegistryModData = missingRegistries.values().stream().map(ResourceLocation::getNamespace).distinct().map(ModMismatchData::getModDataFromId).collect(Collectors.toMap(Pair::getLeft, Pair::getRight));

            return new ModMismatchData(new HashMap<>(), new HashMap<>(), missingRegistryModData, true);
        }

        public static Pair<ResourceLocation, Pair<String, String>> getModDataFromChannel(ResourceLocation id, String channelVersion)
        {
            Optional<? extends ModContainer> mod = ModList.get().getModContainerById(id.getNamespace());

            return mod.map(modContainer -> Pair.of(id, Pair.of(modContainer.getModInfo().getDisplayName(), channelVersion))).orElse(Pair.of(id, Pair.of(id.getNamespace(), channelVersion)));
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
