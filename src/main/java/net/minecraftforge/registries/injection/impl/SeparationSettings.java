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

package net.minecraftforge.registries.injection.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraftforge.registries.injection.ForgeResourceAccess;
import net.minecraftforge.registries.injection.Injector;
import net.minecraftforge.registries.injection.MergeStrategy;
import net.minecraftforge.registries.injection.Merger;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class SeparationSettings {

    private static final String STRUCTURE_SETTINGS_KEY = "structures";
    private static final String SEPARATION_SETTINGS_KEY = "structures";

    private SeparationSettings()
    {

    }

    /**
     * Merges the structure separation setting maps of two different configurations for the same DimensionSettings entry.
     */
    public static class MergerImpl implements Merger<DimensionSettings>
    {
        private final MergeStrategy mergeStrategy;

        public MergerImpl(MergeStrategy mergeStrategy)
        {
            this.mergeStrategy = mergeStrategy;
        }

        @Override
        public void merge(RegistryKey<DimensionSettings> entryKey, JsonElement dest, JsonElement src) throws IOException
        {
            JsonObject destSeparationSettings = getStructureSeparations(dest);
            Set<String> excludedNamespaces = getNamespaceExclusions(destSeparationSettings, mergeStrategy);

            JsonObject srcSeparationSettings = getStructureSeparations(src);
            for (Map.Entry<String, JsonElement> entry : srcSeparationSettings.entrySet())
            {
                ResourceLocation registryName = ResourceLocation.tryCreate(entry.getKey());
                if (registryName == null) continue;

                if (excludedNamespaces.contains(registryName.getNamespace())) continue;

                // Don't overwrite existing settings.
                if (destSeparationSettings.has(entry.getKey())) continue;

                destSeparationSettings.add(entry.getKey(), entry.getValue());
                ForgeResourceAccess.LOGGER.info(" - Injected separation settings for {}", registryName);
            }
        }
    }

    /**
     * Injects code-registered structure separation settings for a given DimensionSettings entry.
     */
    public static class InjectorImpl implements Injector<DimensionSettings>
    {
        private final MergeStrategy mergeStrategy;

        public InjectorImpl(MergeStrategy mergeStrategy)
        {
            this.mergeStrategy = mergeStrategy;
        }

        @Override
        public void inject(RegistryKey<DimensionSettings> entryKey, JsonElement entryData) throws IOException
        {
            DimensionSettings dimensionSettings = WorldGenRegistries.NOISE_SETTINGS.getValueForKey(entryKey);
            if (dimensionSettings == null) return;

            // Contains the vanilla and mod-provided default separation settings.
            Map<Structure<?>, StructureSeparationSettings> separationSettings = dimensionSettings.getStructures().func_236195_a_();

            // The json representation of dimensionSettings.getStructures().func_236195_a_()
            JsonObject separationSettingsData = getStructureSeparations(entryData);
            Set<String> excludedNamespaces = getNamespaceExclusions(separationSettingsData, mergeStrategy);

            for (Map.Entry<Structure<?>, StructureSeparationSettings> entry : separationSettings.entrySet())
            {
                ResourceLocation registryName = entry.getKey().getRegistryName();
                if (registryName == null) continue;

                if (excludedNamespaces.contains(registryName.getNamespace())) continue;

                // Don't overwrite existing settings.
                if (separationSettingsData.has(registryName.toString())) continue;

                // Encode the settings to json and add to the entry's data.
                StructureSeparationSettings.field_236664_a_.encodeStart(JsonOps.INSTANCE, entry.getValue()).result().ifPresent(data -> {
                    separationSettingsData.add(registryName.toString(), data);
                    ForgeResourceAccess.LOGGER.info(" - Injected separation settings for {}", registryName);
                });
            }
        }
    }

    private static JsonObject getStructureSeparations(JsonElement entryData) throws IOException
    {
        assertJsonObject(entryData, "entryData");

        // Json representation of DimensionStructureSettings
        JsonElement structureSettingsJson = entryData.getAsJsonObject().get(STRUCTURE_SETTINGS_KEY);
        assertJsonObject(structureSettingsJson, "structureSettings");

        // Json representation of the Map<Structure,StructureSeparationSettings> field in DimensionStructureSettings
        JsonElement separationSettingsJson = structureSettingsJson.getAsJsonObject().get(SEPARATION_SETTINGS_KEY);
        assertJsonObject(separationSettingsJson, "structureSeparationSettings");

        return separationSettingsJson.getAsJsonObject();
    }

    private static Set<String> getNamespaceExclusions(JsonObject data, MergeStrategy mergeStrategy)
    {
        switch (mergeStrategy)
        {
            case ADD_ALL_CONTENT:
                // Don't exclude any namespaces.
                return Collections.emptySet();
            case ADD_MOD_CONTENT:
                // Exclude the vanilla namespace only.
                return Collections.singleton("minecraft");
            case ADD_MISSING_MOD_CONTENT:
                // Exclude namespaces already contained in the data - we can assume that any omissions
                // of content under those namespaces was intentional.
                Set<String> namespaces = new HashSet<>();
                for (Map.Entry<String, ?> entry : data.entrySet())
                {
                    ResourceLocation name = ResourceLocation.tryCreate(entry.getKey());
                    if (name != null) namespaces.add(name.getNamespace());
                }
                return namespaces;
        }
        return Collections.emptySet();
    }

    private static void assertJsonObject(JsonElement element, String name) throws IOException
    {
        if (element == null) throw new IOException("Json assertion error!", new NullPointerException(name + " is null!"));
        if (!element.isJsonObject()) throw new IOException("Json assertion error!", new IllegalArgumentException(name + " is not a json object!"));
    }
}
