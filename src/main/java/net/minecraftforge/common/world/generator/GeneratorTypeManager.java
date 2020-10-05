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

package net.minecraftforge.common.world.generator;

import com.google.gson.JsonParseException;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;
import net.minecraftforge.common.world.generator.type.DebugGeneratorType;
import net.minecraftforge.common.world.generator.type.FlatGeneratorType;
import net.minecraftforge.common.world.generator.type.OverworldGeneratorType;
import net.minecraftforge.common.world.generator.type.SingleBiomeGeneratorType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class GeneratorTypeManager
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final GeneratorTypeManager INSTANCE = new GeneratorTypeManager();

    private final List<GeneratorType> generatorTypes = new ArrayList<>();
    private final Map<String, GeneratorType> nameToValue = new HashMap<>();

    private GeneratorType defaultGeneratorType;

    private GeneratorTypeManager()
    {
        // Registered in the same order as BiomeGeneratorTypeScreens
        this.defaultGeneratorType = new OverworldGeneratorType("default", DimensionSettings.field_242734_c, false, false);
        register(this.defaultGeneratorType);
        register(new FlatGeneratorType("flat"));
        register(new OverworldGeneratorType("large_biomes", DimensionSettings.field_242734_c, false, true));
        register(new OverworldGeneratorType("amplified", DimensionSettings.field_242735_d, false, false));
        register(new SingleBiomeGeneratorType("single_biome_surface", DimensionSettings.field_242734_c, Biomes.PLAINS));
        register(new SingleBiomeGeneratorType("single_biome_caves", DimensionSettings.field_242738_g, Biomes.PLAINS));
        register(new SingleBiomeGeneratorType("single_biome_floating_islands", DimensionSettings.field_242739_h, Biomes.PLAINS));
        register(new DebugGeneratorType("debug_all_block_states"));
    }

    /**
     * Get the GeneratorType registered with the given name
     *
     * @param name the name to find a GeneratorType for
     * @return the GeneratorType associated with the given name or null
     */
    @Nullable
    public synchronized GeneratorType getGeneratorType(String name)
    {
        return nameToValue.get(name);
    }

    /**
     * Get the default GeneratorType
     * The default GeneratorType is used on the client when creating a new world. The 'world type' option
     * will be set to this GeneratorType when first opening the world creation screen.
     *
     * @return default GeneratorType
     */
    public synchronized GeneratorType getDefaultGeneratorType()
    {
        return defaultGeneratorType;
    }

    /**
     * Iterate all registered GeneratorTypes in order
     *
     * @param consumer the consumer of each registered GeneratorType
     */
    public synchronized void forEach(Consumer<GeneratorType> consumer)
    {
        generatorTypes.forEach(consumer);
    }

    /**
     * Register a GeneratorType instance with the manager
     *
     * @param generatorType the GeneratorType to register
     * @return true if the instance was added to the manager
     */
    public synchronized boolean register(GeneratorType generatorType)
    {
        if (!nameToValue.containsKey(generatorType.getName()))
        {
            generatorTypes.add(generatorType);
            nameToValue.put(generatorType.getName(), generatorType);
            return true;
        }
        else
        {
            LOGGER.error("Attempted to register generator type '{}' twice", generatorType.getName());
            return false;
        }
    }

    /**
     * Set the default GeneratorType
     *
     * @param generatorType the GeneratorType to use as default
     * @return true if the default GeneratorType was set to the provided instance
     */
    public synchronized boolean setDefaultGeneratorType(@Nonnull GeneratorType generatorType)
    {
        if (nameToValue.containsKey(generatorType.getName()))
        {
            if (generatorType != defaultGeneratorType)
            {
                LOGGER.info("Default generator '{}' has been overridden by '{}'", defaultGeneratorType.getName(), generatorType.getName());
                this.defaultGeneratorType = generatorType;
            }
            return true;
        }
        else
        {
            LOGGER.error("Attempted to set the default generator type to unregistered instance '{}'", generatorType.getName());
            return false;
        }
    }

    /**
     * Attempts to create a new {@link DimensionGeneratorSettings} instance from the provided
     * GeneratorType name and creation parameters.
     *
     * @param name       the name of the GeneratorType to use
     * @param seed       the world seed
     * @param structures flag controlling whether structures generate
     * @param bonusChest flag controlling whether the bonus chest will generate at spawn
     * @param registries the DynamicRegistries to construct the settings with
     * @param options    the generator options serialized as a json string (may be empty)
     * @return a new DimensionGeneratorSettings instance or null if the named GeneratorType
     * is not present or parsing of the options failed
     */
    @Nullable
    public synchronized DimensionGeneratorSettings createGeneratorSettings(String name, long seed, boolean structures, boolean bonusChest, DynamicRegistries registries, String options)
    {
        GeneratorType generatorType = getGeneratorType(name);
        if (generatorType == null)
        {
            LOGGER.error("Unknown generator type '{}'", name);
            return null;
        }
        else
        {
            try
            {
                Dynamic<?> dynamic = options.isEmpty() ? null : new Dynamic<>(JsonOps.INSTANCE, JSONUtils.fromJson(options));
                return generatorType.createDimensionGeneratorSettings(seed, structures, bonusChest, registries, dynamic);
            } catch (JsonParseException e)
            {
                LOGGER.error("Failed to parse generator options", e);
                return null;
            }
        }
    }

    public static GeneratorTypeManager get()
    {
        return INSTANCE;
    }
}
