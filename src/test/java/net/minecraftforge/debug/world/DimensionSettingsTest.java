package net.minecraftforge.debug.world;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Optional;

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
    public static final ResourceLocation TEST_OVERWORLD = new ResourceLocation(MODID, "test_overworld");

    private static final Logger LOGGER = LogManager.getLogger();

    public DimensionSettingsTest()
    {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerSettings);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::addStructure);
    }

    /**
     * Demonstrates how a mod can register custom DimensionSettings which can be referenced from
     * within a datapack dimension config whilst being discoverable for other mods to add to and
     * remove structures etc from.
     */
    private void registerSettings(FMLCommonSetupEvent event)
    {
        event.enqueueWork(() ->
        {
            LOGGER.info("Registering custom DimensionSettings for Dimension: {}", TEST_OVERWORLD);
            DimensionSettings dimensionSettings = createDimensionSettings();
            WorldGenRegistries.register(WorldGenRegistries.NOISE_GENERATOR_SETTINGS, TEST_OVERWORLD, dimensionSettings);
        });
    }

    /**
     * Demonstrates how a mod could handle adding their structure settings to a specific dimension.
     */
    private void addStructure(FMLLoadCompleteEvent event)
    {
        event.enqueueWork(() ->
        {
            StructureSeparationSettings settings = new StructureSeparationSettings(2, 1, 0);

            WorldGenRegistries.NOISE_GENERATOR_SETTINGS.entrySet().forEach(entry ->
            {
                ResourceLocation dimensionName = entry.getKey().location();

                // Example: blacklisting all vanilla dimensions
                if (dimensionName.getNamespace().equals("minecraft")) return;

                LOGGER.info("Adding Structure: {} to Dimension: {}", Structure.RUINED_PORTAL.getFeatureName(), dimensionName);
                entry.getValue().structureSettings().structureConfig().put(Structure.RUINED_PORTAL, settings);
            });
        });
    }

    /**
     * Create a DimensionSettings instance copying all but the structure settings from overworld.
     */
    private static DimensionSettings createDimensionSettings()
    {
        DimensionSettings overworld = WorldGenRegistries.NOISE_GENERATOR_SETTINGS.getOrThrow(DimensionSettings.OVERWORLD);

        // Make a new DimensionStructuresSettings with no structures
        DimensionStructuresSettings structures = new DimensionStructuresSettings(Optional.empty(), new HashMap<>());

        // Build a new DimensionSettings copying all the other options from 'overworld'
        return new DimensionSettings(
                structures,
                overworld.noiseSettings(),
                overworld.getDefaultBlock(),
                overworld.getDefaultFluid(),
                overworld.getBedrockRoofPosition(),
                overworld.getBedrockFloorPosition(),
                overworld.seaLevel(),
                overworld.disableMobGeneration());
    }
}
