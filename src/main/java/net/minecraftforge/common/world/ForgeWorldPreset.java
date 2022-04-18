/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.world;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringUtil;
import net.minecraft.Util;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraftforge.common.ForgeConfig;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ForgeWorldPreset extends ForgeRegistryEntry<ForgeWorldPreset>
{
    private static final Logger LOGGER = LogManager.getLogger();

    public static ForgeWorldPreset getDefaultWorldPreset()
    {
        String defaultWorldType = ForgeConfig.COMMON.defaultWorldType.get();

        if (StringUtil.isNullOrEmpty(defaultWorldType) || "default".equals(defaultWorldType))
            return null; // use vanilla

        ForgeWorldPreset def = ForgeRegistries.WORLD_TYPES.get().getValue(new ResourceLocation(defaultWorldType));
        if (def == null)
        {
            LOGGER.error("The defaultWorldType '{}' specified in the forge config has not been registered. The vanilla default generator will be used.", defaultWorldType);
        }

        return def;
    }

    private final IChunkGeneratorFactory factory;

    public ForgeWorldPreset(IChunkGeneratorFactory factory)
    {
        this.factory = factory;
    }

    public ForgeWorldPreset(IBasicChunkGeneratorFactory factory)
    {
        this.factory = factory;
    }

    public String getTranslationKey()
    {
        return Util.makeDescriptionId("generator", getRegistryName());
    }

    public Component getDisplayName()
    {
        return new TranslatableComponent(getTranslationKey());
    }

    /**
     * Called from both the dedicated server and the world creation screen in the client.
     * to construct the DimensionGEneratorSettings:
     * @return The constructed chunk generator.
     */
    public ChunkGenerator createChunkGenerator(RegistryAccess registryAccess, long seed, String generatorSettings)
    {
        return this.factory.createChunkGenerator(registryAccess, seed, generatorSettings);
    }

    public WorldGenSettings createSettings(RegistryAccess registryAccess, long seed, boolean generateStructures, boolean generateLoot, String generatorSettings)
    {
        return this.factory.createSettings(registryAccess, seed, generateStructures, generateLoot, generatorSettings);
    }

    public interface IChunkGeneratorFactory
    {
        ChunkGenerator createChunkGenerator(RegistryAccess dynamicRegistries, long seed, String generatorSettings);

        default WorldGenSettings createSettings(RegistryAccess dynamicRegistries, long seed, boolean generateStructures, boolean bonusChest, String generatorSettings) {
            Registry<DimensionType> dimensionTypeRegistry = dynamicRegistries.registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY);
            return new WorldGenSettings(seed, generateStructures, bonusChest,
                    WorldGenSettings.withOverworld(dimensionTypeRegistry,
                            DimensionType.defaultDimensions(dynamicRegistries, seed),
                            createChunkGenerator(dynamicRegistries, seed, generatorSettings)));
        }
    }

    public interface IBasicChunkGeneratorFactory extends IChunkGeneratorFactory
    {
        ChunkGenerator createChunkGenerator(RegistryAccess registryAccess, long seed);

        default ChunkGenerator createChunkGenerator(RegistryAccess registryAccess, long seed, String generatorSettings)
        {
            return createChunkGenerator(registryAccess, seed);
        }
    }
}
