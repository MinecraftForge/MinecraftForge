/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.data;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import cpw.mods.modlauncher.api.LamdbaExceptionUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Provider for forge's GlobalLootModifier system. See {@link LootModifier}
 *
 * This provider only requires implementing {@link #start()} and calling {@link #add} from it.
 */
public abstract class GlobalLootModifierProvider implements DataProvider {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final PackOutput output;
    private final String modid;
    private final CompletableFuture<HolderLookup.Provider> registries;
    private final Map<String, IGlobalLootModifier> toSerialize = new HashMap<>();
    private boolean replace = false;

    public GlobalLootModifierProvider(PackOutput output, String modid, CompletableFuture<HolderLookup.Provider> registries) {
        this.output = output;
        this.modid = modid;
        this.registries = registries;
    }

    /**
     * Sets the "replace" key in global_loot_modifiers to true.
     */
    protected void replacing() {
        this.replace = true;
    }

    /**
     * Call {@link #add} here, which will pass in the necessary information to write the jsons.
     */
    protected abstract void start(HolderLookup.Provider registries);

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        return this.registries.thenCompose(p -> this.run(cache, p));
    }

    private CompletableFuture<?> run(CachedOutput cache, HolderLookup.Provider registries) {
        start(registries);

        Path forgePath = this.output.getOutputFolder(PackOutput.Target.DATA_PACK).resolve("forge/loot_modifiers/global_loot_modifiers.json");
        Path modifierFolderPath = this.output.getOutputFolder(PackOutput.Target.DATA_PACK).resolve(this.modid).resolve("loot_modifiers");
        List<ResourceLocation> entries = new ArrayList<>();

        ImmutableList.Builder<CompletableFuture<?>> futuresBuilder = new ImmutableList.Builder<>();


        var ops = registries.createSerializationContext(JsonOps.INSTANCE);
        var codec = IGlobalLootModifier.DIRECT_CODEC;

        toSerialize.forEach(LamdbaExceptionUtils.rethrowBiConsumer((name, instance) -> {
            var json = codec.encodeStart(ops, instance).getOrThrow();
            entries.add(ResourceLocation.fromNamespaceAndPath(modid, name));
            Path modifierPath = modifierFolderPath.resolve(name + ".json");
            futuresBuilder.add(DataProvider.saveStable(cache, json, modifierPath));
        }));

        JsonObject forgeJson = new JsonObject();
        forgeJson.addProperty("replace", this.replace);
        forgeJson.add("entries", GSON.toJsonTree(entries.stream().map(ResourceLocation::toString).collect(Collectors.toList())));

        futuresBuilder.add(DataProvider.saveStable(cache, forgeJson, forgePath));

        return CompletableFuture.allOf(futuresBuilder.build().toArray(CompletableFuture[]::new));
    }

    /**
     * Passes in the data needed to create the file without any extra objects.
     *
     * @param modifier      The name of the modifier, which will be the file name.
     * @param instance      The instance to serialize
     */
    public <T extends IGlobalLootModifier> void add(String modifier, T instance) {
        this.toSerialize.put(modifier, instance);
    }

    @Override
    public String getName() {
        return "Global Loot Modifiers : " + modid;
    }
}
