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

package net.minecraftforge.fml.network;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModList;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static net.minecraftforge.fml.network.FMLNetworkConstants.NETWORK;

/**
 * {
 *      "fmlNetworkVersion" : FMLNETVERSION,
 *      "channels": [
 *          {
 *              "res": "fml:handshake",
 *              "version": "1.2.3.4",
 *              "required": true
 *          }
 *     ],
 *     "mods": [
 *          {
 *              "modid": "modid",
 *              "modmarker": "<somestring>"
 *          }
 *     ]
 * }
 *
 */
public class FMLStatusPing {
    private static final Logger LOGGER = LogManager.getLogger();

    private transient Map<ResourceLocation, Pair<String, Boolean>> channels;
    private transient Map<String, String> mods;
    private transient int fmlNetworkVer;
    public FMLStatusPing() {
        this.channels = NetworkRegistry.buildChannelVersionsForListPing();
        this.mods = new HashMap<>();
        ModList.get().forEachModContainer((modid, mc) ->
                    mods.put(modid, mc.getCustomExtension(ExtensionPoint.DISPLAYTEST).
                            map(Pair::getLeft).map(Supplier::get).orElse(FMLNetworkConstants.IGNORESERVERONLY)));
        this.fmlNetworkVer = FMLNetworkConstants.FMLNETVERSION;
    }

    private FMLStatusPing(Map<ResourceLocation, Pair<String, Boolean>> deserialized, Map<String,String> modMarkers, int fmlNetVer) {
        this.channels = ImmutableMap.copyOf(deserialized);
        this.mods = modMarkers;
        this.fmlNetworkVer = fmlNetVer;
    }

    public static class Serializer {
        public static FMLStatusPing deserialize(JsonObject forgeData, JsonDeserializationContext ctx) {
            try {
                final Map<ResourceLocation, Pair<String, Boolean>> channels = StreamSupport.stream(JSONUtils.getJsonArray(forgeData, "channels").spliterator(), false).
                        map(JsonElement::getAsJsonObject).
                        collect(Collectors.toMap(jo -> new ResourceLocation(JSONUtils.getString(jo, "res")),
                                jo -> Pair.of(JSONUtils.getString(jo, "version"), JSONUtils.getBoolean(jo, "required")))
                        );

                final Map<String, String> mods = StreamSupport.stream(JSONUtils.getJsonArray(forgeData, "mods").spliterator(), false).
                        map(JsonElement::getAsJsonObject).
                        collect(Collectors.toMap(jo -> JSONUtils.getString(jo, "modId"), jo->JSONUtils.getString(jo, "modmarker")));

                final int remoteFMLVersion = JSONUtils.getInt(forgeData, "fmlNetworkVersion");
                return new FMLStatusPing(channels, mods, remoteFMLVersion);
            } catch (JsonSyntaxException e) {
                LOGGER.debug(NETWORK, "Encountered an error parsing status ping data", e);
                return null;
            }
        }

        public static JsonObject serialize(FMLStatusPing forgeData, JsonSerializationContext ctx) {
            JsonObject obj = new JsonObject();
            JsonArray channels = new JsonArray();
            forgeData.channels.forEach((namespace, version) -> {
                JsonObject mi = new JsonObject();
                mi.addProperty("res", namespace.toString());
                mi.addProperty("version", version.getLeft());
                mi.addProperty("required", version.getRight());
                channels.add(mi);
            });

            obj.add("channels", channels);

            JsonArray modTestValues = new JsonArray();
            forgeData.mods.forEach((modId, value) -> {
                JsonObject mi = new JsonObject();
                mi.addProperty("modId", modId);
                mi.addProperty("modmarker", value);
                modTestValues.add(mi);
            });
            obj.add("mods", modTestValues);
            obj.addProperty("fmlNetworkVersion", forgeData.fmlNetworkVer);
            return obj;
        }
    }

    public Map<ResourceLocation, Pair<String, Boolean>> getRemoteChannels() {
        return this.channels;
    }

    public Map<String,String> getRemoteModData() {
        return mods;
    }

    public int getFMLNetworkVersion() {
        return fmlNetworkVer;
    }

}
