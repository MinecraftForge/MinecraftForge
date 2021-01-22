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

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.Util;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;
import net.minecraftforge.common.ForgeConfig;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ForgeWorldType extends ForgeRegistryEntry<ForgeWorldType>
{
    private static final Logger LOGGER = LogManager.getLogger();

    public static ForgeWorldType getDefaultWorldType()
    {
        String defaultWorldType = ForgeConfig.COMMON.defaultWorldType.get();

        if (StringUtils.isNullOrEmpty(defaultWorldType) || "default".equals(defaultWorldType))
            return null; // use vanilla

        ForgeWorldType def = ForgeRegistries.WORLD_TYPES.getValue(new ResourceLocation(defaultWorldType));
        if (def == null)
        {
            LOGGER.error("The defaultWorldType '{}' specified in the forge config has not been registered. The vanilla default generator will be used.", defaultWorldType);
        }

        return def;
    }

    private final IChunkGeneratorFactory factory;

    public ForgeWorldType(IChunkGeneratorFactory factory)
    {
        this.factory = factory;
    }

    public ForgeWorldType(IBasicChunkGeneratorFactory factory)
    {
        this.factory = factory;
    }

    public String getTranslationKey()
    {
        return Util.makeTranslationKey("generator", getRegistryName());
    }

    public ITextComponent getDisplayName()
    {
        return new TranslationTextComponent(getTranslationKey());
    }

    /**
     * Called from both the dedicated server and the world creation screen in the client.
     * to construct the DimensionGEneratorSettings:
     * @return The constructed chunk generator.
     */
    public ChunkGenerator createChunkGenerator(Registry<Biome> biomeRegistry, Registry<DimensionSettings> dimensionSettingsRegistry, long seed, String generatorSettings)
    {
        return this.factory.createChunkGenerator(biomeRegistry, dimensionSettingsRegistry, seed, generatorSettings);
    }

    public DimensionGeneratorSettings createSettings(DynamicRegistries dynamicRegistries, long seed, boolean generateStructures, boolean generateLoot, String generatorSettings)
    {
        return this.factory.createSettings(dynamicRegistries, seed, generateStructures, generateLoot, generatorSettings);
    }

    public interface IChunkGeneratorFactory
    {
        ChunkGenerator createChunkGenerator(Registry<Biome> biomeRegistry, Registry<DimensionSettings> dimensionSettingsRegistry, long seed, String generatorSettings);

        default DimensionGeneratorSettings createSettings(DynamicRegistries dynamicRegistries, long seed, boolean generateStructures, boolean bonusChest, String generatorSettings) {
            Registry<Biome> biomeRegistry = dynamicRegistries.getRegistry(Registry.BIOME_KEY);
            Registry<DimensionType> dimensionTypeRegistry = dynamicRegistries.getRegistry(Registry.DIMENSION_TYPE_KEY);
            Registry<DimensionSettings> dimensionSettingsRegistry = dynamicRegistries.getRegistry(Registry.NOISE_SETTINGS_KEY);
            return new DimensionGeneratorSettings(seed, generateStructures, bonusChest,
                    DimensionGeneratorSettings.func_242749_a(dimensionTypeRegistry,
                            DimensionType.getDefaultSimpleRegistry(dimensionTypeRegistry, biomeRegistry, dimensionSettingsRegistry, seed),
                            createChunkGenerator(biomeRegistry, dimensionSettingsRegistry, seed, generatorSettings)));
        }
    }

    public interface IBasicChunkGeneratorFactory extends IChunkGeneratorFactory
    {
        ChunkGenerator createChunkGenerator(Registry<Biome> biomeRegistry, Registry<DimensionSettings> dimensionSettingsRegistry, long seed);

        default ChunkGenerator createChunkGenerator(Registry<Biome> biomeRegistry, Registry<DimensionSettings> dimensionSettingsRegistry, long seed, String generatorSettings)
        {
            return createChunkGenerator(biomeRegistry, dimensionSettingsRegistry, seed);
        }
    }
}
