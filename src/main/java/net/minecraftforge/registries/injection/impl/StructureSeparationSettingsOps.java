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

import com.google.common.base.Preconditions;
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
import net.minecraftforge.registries.injection.JsonOp;
import net.minecraftforge.registries.injection.MergeStrategy;
import org.apache.logging.log4j.Level;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Operations that target entries of the {@link net.minecraft.world.gen.DimensionSettings} registry.
 *
 * Implementation is dependant on the following Codec schemas:
 * - {@link net.minecraft.world.gen.DimensionSettings#field_236097_a_}
 * - {@link net.minecraft.world.gen.settings.DimensionStructuresSettings#field_236190_a_}
 * - {@link net.minecraft.world.gen.settings.StructureSeparationSettings#field_236664_a_}
 *
 * Resulting Json:
 * {
 *   "structures": {
 *     "stronghold": {},
 *     "structures": {
 *       "minecraft:structure_name": {
 *         "salt": #int,
 *         "spacing": #int,
 *         "separation": #int
 *       }
 *     }
 *   }
 * }
 *
 * The merge and inject operations target the innermost "structures" json-object which corresponds to
 * the field found at {@link net.minecraft.world.gen.settings.DimensionStructuresSettings#field_236193_d_}.
 * This is a map that holds the StructureSeparationSettings keyed by the Structure they apply to.
 * The merge and inject operations in this class add the absent entries to that json-object according to the
 * user-specified {@link MergeStrategy}.
 */
public final class StructureSeparationSettingsOps
{
    private static final Level LOG_LEVEL = Level.DEBUG;
    private static final String STRUCTURE_SETTINGS_KEY = "structures";
    private static final String SEPARATION_SETTINGS_KEY = "structures";

    private StructureSeparationSettingsOps()
    {

    }

    /**
     * Merges the structure separation setting maps of two different configurations for the same DimensionSettings entry.
     */
    public static class Merge implements JsonOp.Merge<DimensionSettings>
    {
        private final MergeStrategy mergeStrategy;

        public Merge(MergeStrategy mergeStrategy)
        {
            this.mergeStrategy = mergeStrategy;
        }

        @Override
        public String getName() {
            return "MergeStructureSeparationSettings";
        }

        @Override
        public void merge(RegistryKey<DimensionSettings> entryKey, JsonElement dest, JsonElement src) throws Exception
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
                ForgeResourceAccess.LOGGER.log(LOG_LEVEL, " - Injected separation settings for structure: {} in: {}", registryName, entryKey);
            }
        }
    }

    /**
     * Injects code-registered structure separation settings for a given DimensionSettings entry.
     */
    public static class Inject implements JsonOp.Inject<DimensionSettings>
    {
        private final MergeStrategy mergeStrategy;

        public Inject(MergeStrategy mergeStrategy)
        {
            this.mergeStrategy = mergeStrategy;
        }

        @Override
        public String getName() {
            return "InjectStructureSeparationSettings";
        }

        @Override
        public void inject(RegistryKey<DimensionSettings> entryKey, JsonElement entryData) throws Exception
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
                    ForgeResourceAccess.LOGGER.log(LOG_LEVEL, " - Injected separation settings for structure: {} in: {}", registryName, entryKey);
                });
            }
        }
    }

    private static JsonObject getStructureSeparations(JsonElement entryData) throws Exception
    {
        assertJsonObject(entryData, "EntryData");

        // Json representation of DimensionStructureSettings
        JsonElement structureSettingsJson = entryData.getAsJsonObject().get(STRUCTURE_SETTINGS_KEY);
        assertJsonObject(structureSettingsJson, "StructureSettings");

        // Json representation of the Map<Structure,StructureSeparationSettings> field in DimensionStructureSettings
        JsonElement separationSettingsJson = structureSettingsJson.getAsJsonObject().get(SEPARATION_SETTINGS_KEY);
        assertJsonObject(separationSettingsJson, "StructureSeparationSettings");

        return separationSettingsJson.getAsJsonObject();
    }

    private static Set<String> getNamespaceExclusions(JsonObject data, MergeStrategy mergeStrategy)
    {
        switch (mergeStrategy)
        {
            case ALL_NAMESPACES:
                // Don't exclude any namespaces.
                return Collections.emptySet();
            case MOD_NAMESPACES:
                // Exclude the vanilla namespace only.
                return Collections.singleton("minecraft");
            case ABSENT_NAMESPACES:
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

    private static void assertJsonObject(JsonElement element, String name) throws NullPointerException, IllegalStateException
    {
        Preconditions.checkNotNull(element, "%s is null!", name);
        Preconditions.checkState(element.isJsonObject(), "%s is not a json object!", name);
    }
}
