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
import java.util.*;
import java.util.function.Supplier;

import com.google.common.base.Suppliers;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
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
    public static final Supplier<Gson> GSON_INSTANCE = Suppliers.memoize(() -> Deserializers.createFunctionSerializer().create());

    private Map<ResourceLocation, IGlobalLootModifier> registeredLootModifiers = ImmutableMap.of();
    private static final String folder = "loot_modifiers";
    
    public LootModifierManager() {
        super(GSON_INSTANCE.get(), folder);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> resourceList, ResourceManager resourceManagerIn, ProfilerFiller profilerIn) {
        Builder<ResourceLocation, IGlobalLootModifier> builder = ImmutableMap.builder();
        Set<ResourceLocation> finalLocations = new HashSet<>();
        ResourceLocation resourcelocation = new ResourceLocation("forge","loot_modifiers/global_loot_modifiers.json");
        //read in all data files from forge:loot_modifiers/global_loot_modifiers in order to do layering
        for(Resource iresource : resourceManagerIn.getResourceStack(resourcelocation)) {
            try (   InputStream inputstream = iresource.open();
                    Reader reader = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8));
                    ) {
                JsonObject jsonobject = GsonHelper.fromJson(GSON_INSTANCE.get(), reader, JsonObject.class);
                boolean replace = jsonobject.get("replace").getAsBoolean();
                if(replace)
                    finalLocations.clear();
                JsonArray entryList = jsonobject.get("entries").getAsJsonArray();

                for(JsonElement entry : entryList)
                    finalLocations.add(new ResourceLocation(entry.getAsString()));
            }

            catch (RuntimeException | IOException ioexception) {
                LOGGER.error("Couldn't read global loot modifier list {} in data pack {}", resourcelocation, iresource.sourcePackId(), ioexception);
            }
        }
        //use layered config to fetch modifier data files (modifiers missing from config are disabled)
        for (ResourceLocation location : finalLocations)
        {
            Codec<? extends IGlobalLootModifier> codec = ForgeRegistries.LOOT_MODIFIER_SERIALIZERS.get().getValue(location);
            if (codec == null)
            {
                LOGGER.warn("Unknown or unregistered codec for GlobalLootModifier: {}", location);
                continue;
            }
            JsonElement json = resourceList.get(location);
            IGlobalLootModifier modifier = codec.decode(JsonOps.INSTANCE, json).get()
                    .map(Pair::getFirst, partial ->
                    {
                        LOGGER.warn("Could not decode GlobalLootModifier with serializer: {} - error: {}", location, partial.message());
                        return null;
                    });
            if(modifier != null)
                builder.put(location, modifier);
        }
        this.registeredLootModifiers = builder.build();
    }

    /**
     * An immutable collection of the registered loot modifiers in layered order.
     */
    public Collection<IGlobalLootModifier> getAllLootMods() {
        return registeredLootModifiers.values();
    }

}
