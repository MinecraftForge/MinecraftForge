/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.loot;

import java.io.IOException;
import java.util.*;

import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;

public class LootModifierManager extends SimpleJsonResourceReloadListener {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final String FOLDER = "loot_modifiers";

    private final HolderLookup.Provider registries;
    private Map<ResourceLocation, IGlobalLootModifier> modifiers = ImmutableMap.of();

    /** @deprecated Use {@link #LootModifierManager(HolderLookup.Provider)} */
    @Deprecated(forRemoval = true, since = "1.21.1")
    public LootModifierManager(RegistryAccess registries) {
        this((HolderLookup.Provider) registries);
    }

    public LootModifierManager(HolderLookup.Provider registries) {
        super(GSON, FOLDER);
        this.registries = registries;
    }

    @Override
    protected Map<ResourceLocation, JsonElement> prepare(ResourceManager resources, ProfilerFiller profilerFiller) {
        var path = ResourceLocation.fromNamespaceAndPath("forge", FOLDER + "/global_loot_modifiers.json");

        List<ResourceLocation> toLoad = new ArrayList<>();
        //read in all data files from forge:loot_modifiers/global_loot_modifiers in order to do layering
        for (var resource : resources.getResourceStack(path)) {
            try (var reader = resource.openAsReader()) {
                var json = GsonHelper.fromJson(GSON, reader, JsonObject.class);

                if (GsonHelper.getAsBoolean(json, "replace", false))
                    toLoad.clear();

                for(var entry : GsonHelper.getAsJsonArray(json, "entries")) {
                    ResourceLocation loc = ResourceLocation.parse(entry.getAsString());
                    toLoad.remove(loc); //remove and re-add if needed, to update the ordering.
                    toLoad.add(loc);
                }
            } catch (RuntimeException | IOException ioexception) {
                LOGGER.error("Couldn't read global loot modifier list {} in data pack {}", path, resource.sourcePackId(), ioexception);
            }
        }

        //use layered config to fetch modifier data files (modifiers missing from config are disabled)
        var ret = super.prepare(resources, profilerFiller);
        ret.keySet().removeIf(k -> !toLoad.contains(k));
        return ret;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> resources, ResourceManager resourceManagerIn, ProfilerFiller profilerIn) {
        Builder<ResourceLocation, IGlobalLootModifier> builder = ImmutableMap.builder();
        var ops = registries.createSerializationContext(JsonOps.INSTANCE);
        resources.forEach((location, json) -> {
            IGlobalLootModifier.DIRECT_CODEC.parse(ops, json)
                // log error if parse fails
                .ifError(error -> LOGGER.warn("Could not decode GlobalLootModifier with json id {} - error: {}", location, error.message()))
                // add loot modifier if parse succeeds
                .ifSuccess(modifier -> builder.put(location, modifier));
        });
        this.modifiers = builder.build();
    }

    /**
     * An immutable collection of the registered loot modifiers in layered order.
     */
    public Collection<IGlobalLootModifier> getAllLootMods() {
        return modifiers.values();
    }
}