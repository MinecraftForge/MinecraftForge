/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
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

import net.minecraft.world.level.storage.loot.Deserializers;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class LootModifierManager extends SimpleJsonResourceReloadListener {
    public static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON_INSTANCE = Deserializers.createFunctionSerializer().create();

    private Map<ResourceLocation, IGlobalLootModifier> registeredLootModifiers = ImmutableMap.of();
    private static final String folder = "loot_modifiers";
    
    public LootModifierManager() {
        super(GSON_INSTANCE, folder);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> resourceList, ResourceManager resourceManagerIn, ProfilerFiller profilerIn) {
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
            for(Resource iresource : resourceManagerIn.getResources(resourcelocation)) {
                try (   InputStream inputstream = iresource.getInputStream();
                        Reader reader = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8));
                        ) {
                    JsonObject jsonobject = GsonHelper.fromJson(GSON_INSTANCE, reader, JsonObject.class);
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
                    LOGGER.error("Couldn't read global loot modifier list {} in data pack {}", resourcelocation, iresource.getSourceName(), ioexception);
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
        LootItemCondition[] lootConditions = GSON_INSTANCE.fromJson(object.get("conditions"), LootItemCondition[].class);

        ResourceLocation serializer = new ResourceLocation(GsonHelper.getAsString(object, "type"));

        return ForgeRegistries.LOOT_MODIFIER_SERIALIZERS.get().getValue(serializer).read(location, object, lootConditions);
    }

    public static GlobalLootModifierSerializer<?> getSerializerForName(ResourceLocation resourcelocation) {
        return ForgeRegistries.LOOT_MODIFIER_SERIALIZERS.get().getValue(resourcelocation);
    }

    /**
     * An immutable collection of the registered loot modifiers in layered order.
     */
    public Collection<IGlobalLootModifier> getAllLootMods() {
        return registeredLootModifiers.values();
    }

}
