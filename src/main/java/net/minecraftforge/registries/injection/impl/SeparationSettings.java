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
import java.util.Map;
import java.util.function.Predicate;

public final class SeparationSettings {

    private static final String STRUCTURE_SETTINGS_KEY = "structures";
    private static final String SEPARATION_SETTINGS_KEY = "structures";

    private SeparationSettings()
    {

    }

    public static class MergerImpl implements Merger
    {
        @Override
        public void merge(JsonElement dest, JsonElement src) throws IOException
        {
            JsonObject destSeparationSettings = getStructureSeparations(dest);
            JsonObject srcSeparationSettings = getStructureSeparations(src);
            for (Map.Entry<String, JsonElement> entry : srcSeparationSettings.entrySet())
            {
                if (!destSeparationSettings.has(entry.getKey()))
                {
                    ForgeResourceAccess.LOGGER.debug(" - Inserted separation settings for {}", entry.getKey());
                    destSeparationSettings.add(entry.getKey(), entry.getValue());
                }
            }
        }
    }

    public static class InjectorImpl implements Injector<DimensionSettings>
    {
        private final Predicate<ResourceLocation> predicate;

        public InjectorImpl(Predicate<ResourceLocation> predicate)
        {
            this.predicate = predicate;
        }

        @Override
        public void inject(RegistryKey<DimensionSettings> entryKey, JsonElement entryData) throws IOException
        {
            DimensionSettings dimensionSettings = WorldGenRegistries.NOISE_SETTINGS.getValueForKey(entryKey);
            if (dimensionSettings == null) return;

            // This map contains the vanilla and mod-provided default separation settings.
            Map<Structure<?>, StructureSeparationSettings> separationSettings = dimensionSettings.getStructures().func_236195_a_();

            // This is the json representation of dimensionSettings.getStructures().func_236195_a_()
            JsonObject separationSettingsData = getStructureSeparations(entryData);

            for (Map.Entry<Structure<?>, StructureSeparationSettings> entry : separationSettings.entrySet())
            {
                ResourceLocation registryName = entry.getKey().getRegistryName();

                if (registryName == null) continue;
                if (!predicate.test(registryName)) continue;

                String name = registryName.toString();
                // Do not overwrite existing data.
                if (separationSettingsData.has(name)) continue;

                // Encode to json and add to the entry's data
                StructureSeparationSettings.field_236664_a_.encodeStart(JsonOps.INSTANCE, entry.getValue()).result().ifPresent(data -> {
                    ForgeResourceAccess.LOGGER.debug(" - Inserted separation settings for {}", name);
                    separationSettingsData.add(name, data);
                });
            }
        }
    }

    private static JsonObject getStructureSeparations(JsonElement entryData) throws IOException
    {
        assertJsonObject(entryData, "entryData");

        JsonElement structureSettingsJson = entryData.getAsJsonObject().get(STRUCTURE_SETTINGS_KEY);
        assertJsonObject(structureSettingsJson, "structureSettings");

        JsonElement separationSettingsJson = structureSettingsJson.getAsJsonObject().get(SEPARATION_SETTINGS_KEY);
        assertJsonObject(separationSettingsJson, "structureSeparationSettings");

        return separationSettingsJson.getAsJsonObject();
    }

    private static void assertJsonObject(JsonElement element, String name) throws IOException
    {
        if (element == null) throw new IOException("Json assertion error!", new NullPointerException(name + " is null!"));
        if (!element.isJsonObject()) throw new IOException("Json assertion error!", new IllegalArgumentException(name + " is not a json object!"));
    }
}
