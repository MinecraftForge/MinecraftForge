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
import net.minecraftforge.registries.injection.Merger;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

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
        @Override
        public void merge(RegistryKey<DimensionSettings> entryKey, JsonElement dest, JsonElement src) throws IOException
        {
            JsonObject destSeparationSettings = getStructureSeparations(dest);
            Set<String> destNamespaces = getNamespaces(destSeparationSettings);

            JsonObject srcSeparationSettings = getStructureSeparations(src);
            for (Map.Entry<String, JsonElement> entry : srcSeparationSettings.entrySet())
            {
                ResourceLocation name = ResourceLocation.tryCreate(entry.getKey());

                // Check if dest has already been configured with content from the given namespace - assume omissions are intentional.
                if (name == null || destNamespaces.contains(name.getNamespace())) continue;

                // If it's a new namespace it's new content.
                ForgeResourceAccess.LOGGER.debug(" - Injected separation settings for {}", name);
                destSeparationSettings.add(name.toString(), entry.getValue());
            }
        }
    }

    /**
     * Injects code-registered structure separation settings for a given DimensionSettings entry.
     */
    public static class InjectorImpl implements Injector<DimensionSettings>
    {
        private final Predicate<ResourceLocation> predicate;

        public InjectorImpl()
        {
            // Default behaviour is to only inject modded content.
            this(name -> !name.getNamespace().equals("minecraft"));
        }

        public InjectorImpl(Predicate<ResourceLocation> predicate)
        {
            this.predicate = predicate;
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
            Set<String> namespaces = getNamespaces(separationSettingsData);

            for (Map.Entry<Structure<?>, StructureSeparationSettings> entry : separationSettings.entrySet())
            {
                ResourceLocation registryName = entry.getKey().getRegistryName();
                // Filter out invalid entries.
                if (registryName == null || !predicate.test(registryName)) continue;

                // Check if the data has already been configured with content from the given namespace - assume omissions are intentional.
                if (namespaces.contains(registryName.getNamespace())) continue;

                // Encode the settings to json and add to the entry's data.
                StructureSeparationSettings.field_236664_a_.encodeStart(JsonOps.INSTANCE, entry.getValue()).result().ifPresent(data -> {
                    ForgeResourceAccess.LOGGER.debug(" - Injected separation settings for {}", registryName);
                    separationSettingsData.add(registryName.toString(), data);
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

    private static Set<String> getNamespaces(JsonObject data)
    {
        Set<String> namespaces = new HashSet<>();
        for (Map.Entry<String, ?> entry : data.entrySet())
        {
            ResourceLocation name = ResourceLocation.tryCreate(entry.getKey());
            if (name != null) namespaces.add(name.getNamespace());
        }
        return namespaces;
    }

    private static void assertJsonObject(JsonElement element, String name) throws IOException
    {
        if (element == null) throw new IOException("Json assertion error!", new NullPointerException(name + " is null!"));
        if (!element.isJsonObject()) throw new IOException("Json assertion error!", new IllegalArgumentException(name + " is not a json object!"));
    }
}
