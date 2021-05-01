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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
import net.minecraftforge.registries.injection.impl.StructureSeparationSettingsOps;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;

/**
 * This is an implementation of WorldSettingsImport.IResourceAccess that can optionally apply merge
 * and inject operations on the intermediary json representations of each registry entry being loaded.
 */
public class ForgeResourceAccess implements WorldSettingsImport.IResourceAccess
{
    public static final Logger LOGGER = LogManager.getLogger("ForgeResourceLoader");
    private static final JsonParser JSON_PARSER = new JsonParser();

    private final boolean merge;
    private final boolean inject;
    private final IResourceManager resourceManager;
    private final WorldSettingsImport.IResourceAccess vanillaDelegate;
    private final Map<RegistryKey<? extends Registry<?>>, RegistryEntryModifier<?>> modifiers;

    public ForgeResourceAccess(IResourceManager resourceManager,
                               WorldSettingsImport.IResourceAccess vanillaDelegate,
                               Map<RegistryKey<? extends Registry<?>>, RegistryEntryModifier<?>> modifiers,
                               boolean merge, boolean inject)
    {
        this.resourceManager = resourceManager;
        this.vanillaDelegate = vanillaDelegate;
        this.modifiers = modifiers;
        this.merge = merge;
        this.inject = inject;
    }

    @Override
    public Collection<ResourceLocation> listResources(RegistryKey<? extends Registry<?>> registryKey)
    {
        return resourceManager.listResources(registryKey.location().getPath(), file -> file.equals(".json"));
    }

    @Override
    public <E> DataResult<Pair<E, OptionalInt>> parseElement(DynamicOps<JsonElement> ops, RegistryKey<? extends Registry<E>> registryKey, RegistryKey<E> entryKey, Decoder<E> decoder)
    {
        final RegistryEntryModifier<?> modifier = modifiers.get(registryKey);

        // Delegate to vanilla if there are no modifications to apply to the data.
        if (modifier == null) return vanillaDelegate.parseElement(ops, registryKey, entryKey, decoder);

        final ResourceLocation resourceFile = getFileLocation(registryKey, entryKey.location());
        try
        {
            JsonObject entryDataResult = load(entryKey, resourceFile, modifier);

            if (inject && modifier.hasInjections())
            {
                LOGGER.info("Injecting registry entry data into {}", entryKey);
                modifier.injectRaw(entryKey, entryDataResult);
            }

            entryDataResult.addProperty("forge:registry_name", entryKey.location().toString());

            return decoder.parse(ops, entryDataResult).map(entry -> Pair.of(entry, OptionalInt.empty()));
        }
        catch (Exception e)
        {
            // Same message as anonymous implementation in WorldSettingsImport.IResourceAccess.
            return DataResult.error("Failed to parse " + resourceFile + " file: " + e.getMessage());
        }
    }

    @Override
    public String toString()
    {
        return "ForgeResourceAccess{" +
                "merge=" + merge +
                ", inject=" + inject +
                ", resourceManager=" + resourceManager +
                ", vanillaDelegate=" + vanillaDelegate +
                '}';
    }

    /**
     * Attempts to load the json data for a given registry entry applying the provided RegistryEntryModifier
     * (if present) to the resulting data.
     *
     * @param entryKey The RegistryKey of the entry being loaded.
     * @param file     The resource file path to locate the entry's data at.
     * @param modifier The RegistryEntryModifier for this registry entry type.
     * @return A JsonObject containing the registry entry in serialized form.
     * @throws Exception When an error occurs loading or merging of the json content, or if the resulting json data is in the incorrect format.
     */
    private JsonObject load(RegistryKey<?> entryKey, ResourceLocation file, RegistryEntryModifier<?> modifier) throws Exception
    {
        final JsonElement entryDataResult;

        if (merge && modifier.hasMergers())
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

        if (entryDataResult == null) throw new NullPointerException("entryDataResult is null!");
        if (!entryDataResult.isJsonObject()) throw new IllegalStateException("entryDataResult is not a json object! " + entryDataResult.getClass());

        return entryDataResult.getAsJsonObject();
    }

    /**
     * Attempts to load the json data for a given registry entry from all datapacks contained in the
     * ResourceManager and uses the merge operation provided by the RegistryEntryModifier to combine
     * the data into a single json object.
     *
     * @param entryKey The RegistryKey of the entry being loaded.
     * @param file     The resource file path to locate the entry's data at.
     * @param modifier The RegistryEntryModifier for this registry entry type.
     * @return A JsonElement containing the merged data from all datapacks that contain a file for this resource entry.
     * @throws Exception When an error occurs loading data from the resource manager or from errors thrown during merging the data.
     */
    private JsonElement loadAndMerge(RegistryKey<?> entryKey, ResourceLocation file, RegistryEntryModifier<?> modifier) throws Exception {
        // Note: The resource manager provides resources in order of priority, low to high.
        final List<IResource> resources = resourceManager.getResources(file);

        if (resources.size() > 1)
        {
            LOGGER.info("Merging datapack registry entries for {}", entryKey);
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
                    // We don't need to merge the first candidate with itself!
                    entryDataResult = entryDataCandidate;
                    continue;
                }
                // Merge copies data from the candidate into the result.
                modifier.mergeRaw(entryKey, entryDataResult, entryDataCandidate);
            }
            catch (Exception e)
            {
                // IResource contains an open InputStream so we manually close them all if an error
                // occurs mid-iteration and then rethrow the error to exit.
                resources.forEach(IOUtils::closeQuietly);
                throw e;
            }
        }

        return entryDataResult;
    }

    private static Reader newReader(IResource resource)
    {
        return new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
    }

    private static ResourceLocation getFileLocation(RegistryKey<?> registryKey, ResourceLocation entryName)
    {
        String filepath = registryKey.location().getPath() + "/" + entryName.getPath() + ".json";
        return new ResourceLocation(entryName.getNamespace(), filepath);
    }

    public static WorldSettingsImport.IResourceAccess create(IResourceManager resourceManager, WorldSettingsImport.IResourceAccess vanillaLoader)
    {
        boolean merge = ForgeConfig.COMMON.mergeDataPackWorldGenData.get();
        boolean inject = ForgeConfig.COMMON.injectMissingWorldGenData.get();
        MergeStrategy strategy = ForgeConfig.COMMON.worldGenDataMergeStrategy.get();

        if (!merge && !inject) return vanillaLoader;

        ImmutableMap.Builder<RegistryKey<? extends Registry<?>>, RegistryEntryModifier<?>> builder = ImmutableMap.builder();
        builder.put(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, RegistryEntryModifier.builder(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY)
                        .add(new StructureSeparationSettingsOps.Inject(strategy))
                        .add(new StructureSeparationSettingsOps.Merge(strategy))
                        .build());

        LOGGER.info("Creating datapack-loader with options: merge={}, inject={}, strategy={}", merge, inject, strategy);
        return new ForgeResourceAccess(resourceManager, vanillaLoader, builder.build(), merge, inject);
    }
}
