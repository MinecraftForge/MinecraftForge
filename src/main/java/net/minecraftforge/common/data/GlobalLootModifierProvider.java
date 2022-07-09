/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import cpw.mods.modlauncher.api.LamdbaExceptionUtils;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Provider for forge's GlobalLootModifier system. See {@link LootModifier}
 *
 * This provider only requires implementing {@link #start()} and calling {@link #add} from it.
 */
public abstract class GlobalLootModifierProvider implements DataProvider
{
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final DataGenerator gen;
    private final String modid;
    private final Map<String, JsonElement> toSerialize = new HashMap<>();
    private boolean replace = false;

    public GlobalLootModifierProvider(DataGenerator gen, String modid)
    {
        this.gen = gen;
        this.modid = modid;
    }

    /**
     * Sets the "replace" key in global_loot_modifiers to true.
     */
    protected void replacing()
    {
        this.replace = true;
    }

    /**
     * Call {@link #add} here, which will pass in the necessary information to write the jsons.
     */
    protected abstract void start();

    @Override
    public void run(CachedOutput cache) throws IOException
    {
        start();

        Path forgePath = gen.getOutputFolder().resolve("data/forge/loot_modifiers/global_loot_modifiers.json");
        String modPath = "data/" + modid + "/loot_modifiers/";
        List<ResourceLocation> entries = new ArrayList<>();

        toSerialize.forEach(LamdbaExceptionUtils.rethrowBiConsumer((name, json) ->
        {
            entries.add(new ResourceLocation(modid, name));
            Path modifierPath = gen.getOutputFolder().resolve(modPath + name + ".json");
            DataProvider.saveStable(cache, json, modifierPath);
        }));

        JsonObject forgeJson = new JsonObject();
        forgeJson.addProperty("replace", this.replace);
        forgeJson.add("entries", GSON.toJsonTree(entries.stream().map(ResourceLocation::toString).collect(Collectors.toList())));

        DataProvider.saveStable(cache, forgeJson, forgePath);
    }

    /**
     * Passes in the data needed to create the file without any extra objects.
     *
     * @param modifier      The name of the modifier, which will be the file name.
     * @param instance      The instance to serialize
     */
    public <T extends IGlobalLootModifier> void add(String modifier, T instance)
    {
        JsonElement json = IGlobalLootModifier.DIRECT_CODEC.encodeStart(JsonOps.INSTANCE, instance).getOrThrow(false, s -> {});
        this.toSerialize.put(modifier, json);
    }

    @Override
    public String getName()
    {
        return "Global Loot Modifiers : " + modid;
    }
}
