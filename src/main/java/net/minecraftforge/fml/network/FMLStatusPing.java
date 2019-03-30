/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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
import com.google.common.collect.Maps;
import com.google.gson.*;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.RegistryManager;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;

public class FMLStatusPing {

    private Map<ResourceLocation, Pair<String, Boolean>> channelVersions;
    private int numberOfMods;
    private int fmlNetworkVer;

    public FMLStatusPing(){
        this.channelVersions = NetworkRegistry.buildChannelVersionsForListPing();
        this.numberOfMods = ModList.get().size();
        this.fmlNetworkVer = FMLNetworkConstants.FMLNETVERSION;
    }

    private FMLStatusPing(Map<ResourceLocation, Pair<String, Boolean>> deserialized, int nom, int fmlNetVer){
        this.channelVersions = ImmutableMap.copyOf(deserialized);
        this.numberOfMods = nom;
        this.fmlNetworkVer = fmlNetVer;
    }

    public static class Serializer {

        public static FMLStatusPing deserialize(JsonObject forgeData, JsonDeserializationContext ctx) {
            try {
                JsonArray mods = JsonUtils.getJsonArray(forgeData, "mods");
                Map<ResourceLocation, Pair<String, Boolean>> versions = Maps.newHashMap();
                for(JsonElement el : mods){
                    JsonObject jo = el.getAsJsonObject();
                    ResourceLocation name = new ResourceLocation(JsonUtils.getString(jo, "namespace"), JsonUtils.getString(jo, "path"));
                    String version = JsonUtils.getString(jo, "version");
                    Boolean canBeAbsent = JsonUtils.getBoolean(jo, "mayBeAbsent");
                    versions.put(name, Pair.of(version, canBeAbsent));
                }
                return new FMLStatusPing(versions, JsonUtils.getInt(forgeData, "numberOfMods"), JsonUtils.getInt(forgeData, "fmlNetworkVersion"));
            }catch (Exception c){
                return null;
            }
        }

        public static JsonObject serialize(FMLStatusPing forgeData, JsonSerializationContext ctx){
            JsonObject obj = new JsonObject();
            JsonArray mods = new JsonArray();
            forgeData.channelVersions.entrySet().stream().map(p -> {
                JsonObject mi = new JsonObject();
                mi.addProperty("namespace", p.getKey().getNamespace());
                mi.addProperty("path", p.getKey().getPath());
                mi.addProperty("version", p.getValue().getKey());
                mi.addProperty("mayBeAbsent", p.getValue().getValue());
                return mi;
            }).forEach(mods::add);
            obj.add("mods", mods);
            obj.addProperty("numberOfMods", forgeData.numberOfMods);
            obj.addProperty("fmlNetworkVersion", forgeData.fmlNetworkVer);
            return obj;
        }
    }

    public Map<ResourceLocation, Pair<String, Boolean>> getPresentMods(){
        return this.channelVersions;
    }

    public int getNumberOfMods(){
        return numberOfMods;
    }

    public int getFMLNetworkVersion(){
        return fmlNetworkVer;
    }

}
