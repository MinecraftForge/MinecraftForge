/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.world;

import net.minecraftforge.fml.common.Mod;

/**
 * To see the effects of this test mod you must create a new world then teleport to the custom
 * dimension (use: /execute in dimension_settings_test:test_overworld run tp @s ...).
 *
 * You should observe that the only structure that generates in the dimension is the ruined portal
 * and it should do so very frequently.
 *
 * Note: This mod adds a data asset located at:
 * /data/dimension_settings_test/dimension/test_overworld.json
 */
@Mod(DimensionSettingsTest.MODID)
public class DimensionSettingsTest {
    public static final String MODID = "dimension_settings_test";
/*
    public static final ResourceLocation TEST_OVERWORLD = new ResourceLocation(MODID, "test_overworld");

    private static final Logger LOGGER = LogManager.getLogger();

    public DimensionSettingsTest()
    {
        // TODO-PATCHING: Fix these dimensional stuffs
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerSettings);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::addStructure);
    }

    /**
     * Demonstrates how a mod can register custom DimensionSettings which can be referenced from
     * within a datapack dimension config whilst being discoverable for other mods to add to and
     * remove structures etc from.
     * /
    private void registerSettings(FMLCommonSetupEvent event)
    {
        event.enqueueWork(() ->
        {
            LOGGER.info("Registering custom DimensionSettings for Dimension: {}", TEST_OVERWORLD);
            NoiseGeneratorSettings noiseGenSettings = createNoiseGenerationSettings();
            BuiltinRegistries.register(BuiltinRegistries.NOISE_GENERATOR_SETTINGS, TEST_OVERWORLD, noiseGenSettings);
        });
    }

    /**
     * Demonstrates how a mod could handle adding their structure settings to a specific dimension.
     * /
    private void addStructure(FMLLoadCompleteEvent event)
    {
        event.enqueueWork(() ->
        {
            StructureFeatureConfiguration config = new StructureFeatureConfiguration(2, 1, 0);

            BuiltinRegistries.NOISE_GENERATOR_SETTINGS.entrySet().forEach(entry ->
            {
                ResourceLocation dimensionName = entry.getKey().location();

                // Example: blacklisting all vanilla dimensions
                if (dimensionName.getNamespace().equals("minecraft")) return;

                LOGGER.info("Adding Structure: {} to Dimension: {}", StructureFeature.RUINED_PORTAL.getFeatureName(), dimensionName);
                entry.getValue().structureSettings().structureConfig().put(StructureFeature.RUINED_PORTAL, config);
            });
        });
    }

    /**
     * Create a NoiseGeneratorSettings instance copying all but the structure settings from overworld.
     * /
    private static NoiseGeneratorSettings createNoiseGenerationSettings()
    {
        NoiseGeneratorSettings overworld = BuiltinRegistries.NOISE_GENERATOR_SETTINGS.getOrThrow(NoiseGeneratorSettings.OVERWORLD);

        // Make a new StructureSettings with no structures
        StructureSettings structures = new StructureSettings(Optional.empty(), new HashMap<>());

        // Build a new NoiseGeneratorSettings copying all the other options from 'overworld'
        return new NoiseGeneratorSettings(
                structures,
                overworld.noiseSettings(),
                overworld.defaultBlock(),
                overworld.defaultFluid(),
                overworld.surfaceRule(),
                overworld.seaLevel(),
                overworld.disableMobGeneration(),
                overworld.isAquifersEnabled(),
                overworld.isNoiseCavesEnabled(),
                overworld.isOreVeinsEnabled(),
                overworld.isNoodleCavesEnabled(),
                overworld.useLegacyRandomSource()
                );
    }
*/
}

/*
TODO: Make this a data gen:
data/dimension_settings_test/dimension/test_overworld.json
{
  "#comment": [
    "This is an example Dimension showing how it can be configured to use a custom",
    "DimensionSettings that possess similar generation settings to overworld but",
    "has independently configurable structure settings.",
    "",
    "See the 'settings' field below"
  ],
  "type": "minecraft:overworld",
  "generator": {
    "type": "minecraft:noise",
    "seed": 12345,
    "settings": "dimension_settings_test:test_overworld",
    "biome_source": {
      "preset": "minecraft:overworld",
      "type": "minecraft:multi_noise"
    }
  }
}
*/
