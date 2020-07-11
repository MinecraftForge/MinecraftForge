/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

package net.minecraftforge.common.loot;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.conditions.LootConditionManager;
import net.minecraft.loot.functions.ILootFunction;
import net.minecraft.loot.functions.LootFunctionManager;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class LootModifierManager extends JsonReloadListener {
    public static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON_INSTANCE = (new GsonBuilder()).registerTypeHierarchyAdapter(ILootFunction.class, LootFunctionManager.func_237450_a_()).registerTypeHierarchyAdapter(ILootCondition.class, LootConditionManager.func_237474_a_()).create();

    private Map<ResourceLocation, IGlobalLootModifier> registeredLootModifiers = ImmutableMap.of();
    private static final String folder = "loot_modifiers";
    
    public LootModifierManager() {
        super(GSON_INSTANCE, folder);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> resourceList, IResourceManager resourceManagerIn, IProfiler profilerIn) {
        Builder<ResourceLocation, IGlobalLootModifier> builder = ImmutableMap.builder();
        //old way (for reference)
        /*Map<IGlobalLootModifier, ResourceLocation> toLocation = new HashMap<IGlobalLootModifier, ResourceLocation>();
        resourceList.forEach((location, object) -> {
            try {
                IGlobalLootModifier modifier = deserializeModifier(location, object);
                builder.put(location, modifier);
                toLocation.put(modifier, location);
            } catch (Exception exception) {
                LOGGER.error("Couldn't parse loot modifier {}", location, exception);
            }
        });
        builder.orderEntriesByValue((x,y) -> {
            return toLocation.get(x).compareTo(toLocation.get(y));
        });*/
        //new way
        ArrayList<ResourceLocation> finalLocations = new ArrayList<ResourceLocation>();
        ResourceLocation resourcelocation = new ResourceLocation("forge","loot_modifiers/global_loot_modifiers.json");
        try {
            //read in all data files from forge:loot_modifiers/global_loot_modifiers in order to do layering
            for(IResource iresource : resourceManagerIn.getAllResources(resourcelocation)) {
                try (   InputStream inputstream = iresource.getInputStream();
                        Reader reader = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8));
                        ) {
                    JsonObject jsonobject = JSONUtils.fromJson(GSON_INSTANCE, reader, JsonObject.class);
                    boolean replace = jsonobject.get("replace").getAsBoolean();
                    if(replace) finalLocations.clear();
                    JsonArray entryList = jsonobject.get("entries").getAsJsonArray();
                    for(JsonElement entry : entryList) {
                        String loc = entry.getAsString();
                        ResourceLocation res = new ResourceLocation(loc);
                        if(finalLocations.contains(res)) finalLocations.remove(res);
                        finalLocations.add(res);
                    }
                }

                catch (RuntimeException | IOException ioexception) {
                    LOGGER.error("Couldn't read global loot modifier list {} in data pack {}", resourcelocation, iresource.getPackName(), ioexception);
                } finally {
                    IOUtils.closeQuietly((Closeable)iresource);
                }
            }
        } catch (IOException ioexception1) {
            LOGGER.error("Couldn't read global loot modifier list from {}", resourcelocation, ioexception1);
        }
        //use layered config to fetch modifier data files (modifiers missing from config are disabled)
        finalLocations.forEach(location -> {
            try {
                IGlobalLootModifier modifier = deserializeModifier(location, resourceList.get(location));
                if(modifier != null)
                    builder.put(location, modifier);
            } catch (Exception exception) {
                LOGGER.error("Couldn't parse loot modifier {}", location, exception);
            }
        });
        ImmutableMap<ResourceLocation, IGlobalLootModifier> immutablemap = builder.build();
        this.registeredLootModifiers = immutablemap;
    }

    private IGlobalLootModifier deserializeModifier(ResourceLocation location, JsonElement element) {
        if (!element.isJsonObject()) return null;
        JsonObject object = element.getAsJsonObject();
        ILootCondition[] lootConditions = GSON_INSTANCE.fromJson(object.get("conditions"), ILootCondition[].class);

        // For backward compatibility with the initial implementation, fall back to using the location as the type.
        // TODO: Remove fallback in 1.16
        ResourceLocation serializer = location;
        if (object.has("type"))
        {
            serializer = new ResourceLocation(JSONUtils.getString(object, "type"));
        }

        return ForgeRegistries.LOOT_MODIFIER_SERIALIZERS.getValue(serializer).read(location, object, lootConditions);
    }

    public static GlobalLootModifierSerializer<?> getSerializerForName(ResourceLocation resourcelocation) {
        return ForgeRegistries.LOOT_MODIFIER_SERIALIZERS.getValue(resourcelocation);
    }

    /**
     * An immutable collection of the registered loot modifiers in layered order.
     * @return
     */
    public Collection<IGlobalLootModifier> getAllLootMods() {
        return registeredLootModifiers.values();
    }

}
