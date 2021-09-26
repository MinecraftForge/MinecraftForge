/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

package net.minecraftforge.common.world;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

public class WorldGenValidator {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Will check all biomes for any unregistered or broken worldgen elements.
     * Please always register your configured features and such.
     * Failure to do so may cause unregistered worldgen elements to nuke registered ones from the same biome.
     */
    public static void validateWorldGen(RegistryAccess.RegistryHolder registryHolder) {
        // Do not run in production in order to not slowdown users with the checks
        if (FMLEnvironment.production) return;

        // Small cache so we can skip already checked out worldgen elements to speedup validating.
        Set<Object> checkedElements = new HashSet<>();

        // Grab all registries we will need.
        Registry<Biome> biomeRegistry = registryHolder.registryOrThrow(Registry.BIOME_REGISTRY);
        Registry<ConfiguredFeature<?, ?>> configuredFeatureRegistry = registryHolder.registryOrThrow(Registry.CONFIGURED_FEATURE_REGISTRY);
        Registry<ConfiguredWorldCarver<?>> configuredCarverRegistry = registryHolder.registryOrThrow(Registry.CONFIGURED_CARVER_REGISTRY);
        Registry<ConfiguredStructureFeature<?, ?>> configuredStructureRegistry = registryHolder.registryOrThrow(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY);

        // Checks every biome for unregistered or broken worldgen elements.
        for (Biome biome : biomeRegistry)
        {
            BiomeGenerationSettings biomeGenerationSettings = biome.getGenerationSettings();
            ResourceLocation biomeName = biome.getRegistryName();

            biomeGenerationSettings.features().forEach(generationStageFeatureList ->
                    generationStageFeatureList.forEach(configuredFeatureSupplier -> {
                        ConfiguredFeature<?, ?> feature = configuredFeatureSupplier.get();
                        if (!checkedElements.contains(feature)) {
                            validate(feature, configuredFeatureRegistry, "ConfiguredFeature", ConfiguredFeature.DIRECT_CODEC, biomeName);
                            checkedElements.add(feature);
                        }
                    }));

            for (GenerationStep.Carving carvingStage : GenerationStep.Carving.values())
            {
                biomeGenerationSettings.getCarvers(carvingStage).forEach(configuredWorldCarverSupplier -> {
                    ConfiguredWorldCarver<?> carver = configuredWorldCarverSupplier.get();
                    if (!checkedElements.contains(carver)) {
                        validate(carver, configuredCarverRegistry, "ConfiguredWorldCarver", ConfiguredWorldCarver.DIRECT_CODEC, biomeName);
                        checkedElements.add(carver);
                    }
                });
            }

            biomeGenerationSettings.structures().forEach(structureFeatureSupplier -> {
                ConfiguredStructureFeature<?, ?> structureFeature = structureFeatureSupplier.get();
                if (checkedElements.add(structureFeature))
                {
                    validate(structureFeature, configuredStructureRegistry, "ConfiguredStructureFeature", ConfiguredStructureFeature.DIRECT_CODEC, biomeName);
                }
            });
        }
    }

    /**
     * Handles checking if the worldgen element is broken (unable to be turned into JSON) or unregistered.
     * If an issue is found, this will pause the IDE so the modder can come here and see the error and the worldgenElement for which element is the issue.
     * The console output will have the Forge message as last thing when paused too.
     */
    private static <T> void validate(T worldgenElement, Registry<T> registry, String worldgenElementType, Codec<T> codec, ResourceLocation biomeName) {

        // Checks to make sure the element can be turned into JSON safely or else Minecraft will explode with vague errors.
        JsonElement worldgenElementJSON = codec.encode(worldgenElement, JsonOps.INSTANCE, JsonOps.INSTANCE.empty())
                .getOrThrow(false, errorMsg ->
                        LOGGER.error(String.format("""
                            
                             %s was unable to be turned into json in %s biome.
                             Please breakpoint this line and look at worldgenElement above to see which worldgen element is broken.
                             Check to make sure your codecs are correct and that the values you give the worldgen element does not exceed any limits in the object's codecs.
                             Error: %s
                            """,
                            worldgenElementType,
                            biomeName,
                            errorMsg)));

        // Checks to make sure the element is registered.
        // If not, print out that it is unregistered.
        if (registry.getResourceKey(worldgenElement).isEmpty())
        {
            throw new UnsupportedOperationException(
                    String.format("""
                            
                             Unregistered %s found in %s biome.
                             Please register your %s using Minecraft's Registry class within FMLCommonSetupEvent's enqueueWork.
                             Failure to do so may cause unregistered worldgen elements to nuke registered ones from the same biome which is difficult for players to debug.
                             See this json of the unregistered worldgen element to know which one is unregister:
                             %s
                            """,
                            worldgenElementType,
                            biomeName,
                            worldgenElementType,
                            worldgenElementJSON != null ? gson.toJson(worldgenElementJSON) : "Unable to be parsed into JSON"));
        }
    }
}
