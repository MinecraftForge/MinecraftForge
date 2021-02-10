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

package net.minecraftforge.registries.injection;

import com.google.common.collect.ImmutableMap;
import com.google.gson.*;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldSettingsImport;
import net.minecraftforge.common.ForgeConfig;
import net.minecraftforge.registries.injection.impl.SeparationSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * This is an implementation of WorldSettingsImport.IResourceAccess that can optionally apply merge
 * and inject operations on the intermediary json representations of each registry entry being loaded.
 */
public class ForgeResourceAccess implements WorldSettingsImport.IResourceAccess
{
    public static final Logger LOGGER = LogManager.getLogger("ForgeResourceAccess");
    private static final JsonParser JSON_PARSER = new JsonParser();

    private final boolean merge;
    private final boolean inject;
    private final IResourceManager resourceManager;
    private final Map<RegistryKey<? extends Registry<?>>, RegistryEntryModifier<?>> modifiers;

    public ForgeResourceAccess(IResourceManager resourceManager,
                               Map<RegistryKey<? extends Registry<?>>, RegistryEntryModifier<?>> modifiers,
                               boolean merge, boolean inject)
    {
        this.resourceManager = resourceManager;
        this.modifiers = modifiers;
        this.merge = merge;
        this.inject = inject;
    }

    @Override
    public Collection<ResourceLocation> getRegistryObjects(RegistryKey<? extends Registry<?>> registryKey)
    {
        return resourceManager.getAllResourceLocations(registryKey.getLocation().getPath(), file -> file.equals(".json"));
    }

    @Override
    public <E> DataResult<Pair<E, OptionalInt>> decode(DynamicOps<JsonElement> ops, RegistryKey<? extends Registry<E>> registryKey, RegistryKey<E> entryKey, Decoder<E> decoder)
    {
        final RegistryEntryModifier<?> modifier = modifiers.get(registryKey);
        final ResourceLocation resourceFile = getFileLocation(registryKey, entryKey.getLocation());

        try
        {
            JsonObject entryDataResult = load(entryKey, resourceFile, modifier);

            if (inject && modifier != null && modifier.hasInjections())
            {
                LOGGER.debug("Injecting registry entry data into {}", entryKey);
                modifier.injectRaw(entryKey, entryDataResult);
            }

            entryDataResult.addProperty("forge:registry_name", entryKey.getLocation().toString());

            return decoder.parse(ops, entryDataResult).map(entry -> Pair.of(entry, OptionalInt.empty()));
        } catch (JsonIOException | JsonSyntaxException | IOException e) {
            // Same message as anonymous implementation in WorldSettingsImport.IResourceAccess.
            return DataResult.error("Failed to parse " + resourceFile + " file: " + e.getMessage());
        }
    }

    @Override
    public String toString()
    {
        return "ForgeResourceAccess[" + resourceManager + "]";
    }

    /**
     * Attempts to load the json data for a given registry entry, applying the provided RegistryEntryModifier,
     * if present, to the resulting data.
     *
     * @param entryKey The RegistryKey of the entry being loaded.
     * @param file     The resource file path to locate the entry's data at.
     * @param modifier The RegistryEntryModifier for this registry entry type.
     * @return A JsonObject containing the registry entry in serialize form.
     * @throws IOException When an error occurs loading or merging of the json content, or if the resulting json data is in the incorrect format.
     */
    private JsonObject load(RegistryKey<?> entryKey, ResourceLocation file, @Nullable RegistryEntryModifier<?> modifier) throws IOException
    {
        final JsonElement entryDataResult;

        if (merge && modifier != null && modifier.hasMergers())
        {
            entryDataResult = loadAndMerge(entryKey, file, modifier);
        }
        else
        {
            try (IResource resource = resourceManager.getResource(file); Reader reader = newReader(resource))
            {
                entryDataResult = JSON_PARSER.parse(reader);
            }
        }

        if (entryDataResult == null) throw new IOException("entryDataResult is null!");
        if (!entryDataResult.isJsonObject()) throw new IOException("entryDataResult is not a json object! " + entryDataResult.getClass());

        return entryDataResult.getAsJsonObject();
    }

    /**
     * Attempts to load the json data for a given registry entry from all datapacks contained in the
     * ResourceManager, and uses the merge operation provided by the RegistryEntryModifier to combine
     * the data into a single json object.
     *
     * @param entryKey The RegistryKey of the entry being loaded.
     * @param file     The resource file path to locate the entry's data at.
     * @param modifier The RegistryEntryModifier for this registry entry type.
     * @return A JsonElement containing the merged data from all datapacks that contain a file for this resource entry.
     * @throws IOException When an error occurs loading data from the resource manager or from errors thrown during merging the data.
     */
    private JsonElement loadAndMerge(RegistryKey<?> entryKey, ResourceLocation file, RegistryEntryModifier<?> modifier) throws IOException {
        // Note: The resource manager provides resources in order of priority, low to high.
        final List<IResource> resources = resourceManager.getAllResources(file);

        if (resources.size() > 1)
        {
            LOGGER.debug("Merging datapack registry entries for {}", entryKey);
        }

        JsonElement entryDataResult = null;

        // The merge operation only inserts missing data so iterate in reverse order to make sure
        // that the highest priority resource is inserted first.
        for (int i = resources.size() - 1; i >= 0; i--)
        {
            try (IResource resource = resources.get(i); Reader reader = newReader(resource))
            {
                JsonElement entryDataCandidate = JSON_PARSER.parse(reader);
                if (entryDataResult == null)
                {
                    // Note: We don't need to merge the first candidate with itself!
                    entryDataResult = entryDataCandidate;
                    continue;
                }
                // Merge copies data from the candidate into the result.
                modifier.mergeRaw(entryKey, entryDataResult, entryDataCandidate);
            }
        }

        return entryDataResult;
    }

    private static Reader newReader(IResource resource)
    {
        return new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
    }

    // Returns the full filepath of a given resource in ResourceLocation form.
    private static ResourceLocation getFileLocation(RegistryKey<?> registryKey, ResourceLocation entryName)
    {
        String filepath = registryKey.getLocation().getPath() + "/" + entryName.getPath() + ".json";
        return new ResourceLocation(entryName.getNamespace(), filepath);
    }

    // Helper method for creating the forge IResourceAccess.
    public static WorldSettingsImport.IResourceAccess create(IResourceManager resourceManager)
    {
        boolean merge = ForgeConfig.COMMON.mergeWorldGenData.get();
        boolean inject = ForgeConfig.COMMON.injectModdedWorldGenData.get();

        ImmutableMap.Builder<RegistryKey<? extends Registry<?>>, RegistryEntryModifier<?>> builder = ImmutableMap.builder();
        builder.put(Registry.NOISE_SETTINGS_KEY, RegistryEntryModifier.builder(Registry.NOISE_SETTINGS_KEY)
                        .add(new SeparationSettings.InjectorImpl())
                        .add(new SeparationSettings.MergerImpl())
                        .build());

        return new ForgeResourceAccess(resourceManager, builder.build(), merge, inject);
    }
}
