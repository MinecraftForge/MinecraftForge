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

package net.minecraftforge.common.world.level;

import com.google.gson.JsonParseException;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;
import net.minecraftforge.common.world.level.impl.DebugLevelType;
import net.minecraftforge.common.world.level.impl.FlatLevelType;
import net.minecraftforge.common.world.level.impl.OverworldLevelType;
import net.minecraftforge.common.world.level.impl.SingleBiomeLevelType;
import net.minecraftforge.fml.loading.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class LevelTypeManager
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final LevelTypeManager INSTANCE = new LevelTypeManager();

    private final List<LevelType> levelTypes = new ArrayList<>();
    private final Map<String, LevelType> nameToValue = new HashMap<>();

    private LevelType defaultLevelType;

    private LevelTypeManager()
    {
        // Registered in the same order as BiomeGeneratorTypeScreens
        this.defaultLevelType = new OverworldLevelType("default", DimensionSettings.field_242734_c, false, false);
        register(this.defaultLevelType);
        register(new FlatLevelType("flat"));
        register(new OverworldLevelType("large_biomes", DimensionSettings.field_242734_c, false, true));
        register(new OverworldLevelType("amplified", DimensionSettings.field_242735_d, false, false));
        register(new SingleBiomeLevelType("single_biome_surface", DimensionSettings.field_242734_c, Biomes.PLAINS));
        register(new SingleBiomeLevelType("single_biome_caves", DimensionSettings.field_242738_g, Biomes.PLAINS));
        register(new SingleBiomeLevelType("single_biome_floating_islands", DimensionSettings.field_242739_h, Biomes.PLAINS));
        register(new DebugLevelType("debug_all_block_states"));
    }

    /**
     * Get the {@link LevelType} registered with the given name.
     *
     * @param name The level type name to look up.
     * @return The {@link LevelType} associated with the given name or null if absent.
     */
    @Nullable
    public synchronized LevelType getLevelType(String name)
    {
        LevelType levelType = nameToValue.get(name);
        if (levelType == null)
        {
            levelType = nameToValue.get(StringUtils.toLowerCase(name));
        }
        return levelType;
    }

    /**
     * Get the default {@link LevelType}.
     * The default {@link LevelType} is used on the client when creating a new world. The 'world type' option
     * will be set to this {@link LevelType} when first opening the world creation screen.
     *
     * @return The default {@link LevelType}.
     */
    public synchronized LevelType getDefaultLevelType()
    {
        return defaultLevelType;
    }

    /**
     * Iterate all registered {@link LevelType}s in order.
     *
     * @param action The action to be performed for each element.
     */
    public synchronized void forEach(Consumer<LevelType> action)
    {
        levelTypes.forEach(action);
    }

    /**
     * Register a {@link LevelType} instance with the manager.
     *
     * @param levelType The level type to register.
     * @return True if the instance was added to the manager or false if a level type with the same name has already
     * been registered.
     */
    public synchronized boolean register(LevelType levelType)
    {
        if (!nameToValue.containsKey(levelType.getName()))
        {
            levelTypes.add(levelType);
            nameToValue.put(levelType.getName(), levelType);
            return true;
        }
        else
        {
            LOGGER.error("Attempted to register LevelType '{}' twice", levelType.getName());
            return false;
        }
    }

    /**
     * Set the default {@link LevelType}.
     *
     * @param levelType The level type to use as default.
     * @return True if the default level type was set to the provided instance. May return false if the provided
     * level type has not been registered with the manager.
     */
    public synchronized boolean setDefaultLevelType(@Nonnull LevelType levelType)
    {
        if (nameToValue.containsKey(levelType.getName()))
        {
            if (levelType != defaultLevelType)
            {
                LOGGER.info("Default LevelType '{}' has been overridden by '{}'", defaultLevelType.getName(), levelType.getName());
                this.defaultLevelType = levelType;
            }
            return true;
        }
        else
        {
            LOGGER.error("Attempted to set the default LevelType to unregistered instance '{}'", levelType.getName());
            return false;
        }
    }

    /**
     * Looks up the provided {@link LevelType} name and attempts to use it to create a new
     * {@link net.minecraft.world.gen.settings.DimensionGeneratorSettings} instance using the provided parameters.
     *
     * @param name       The name of the level type to use.
     * @param seed       The world seed.
     * @param structures A flag controlling whether structures generate.
     * @param bonusChest A flag controlling whether the bonus chest will generate at spawn.
     * @param registries The dynamic registries to construct the settings with.
     * @param options    The generator options serialized as a json string (may be empty).
     * @return A new {@link net.minecraft.world.gen.settings.DimensionGeneratorSettings} instance or null if the named
     * level type is not present or parsing of the options failed.
     */
    @Nullable
    public synchronized DimensionGeneratorSettings createGeneratorSettings(String name, long seed, boolean structures, boolean bonusChest, DynamicRegistries registries, String options)
    {
        LevelType levelType = getLevelType(name);
        if (levelType == null)
        {
            LOGGER.error("Unknown LevelType '{}'", name);
            return null;
        }
        else
        {
            try
            {
                Dynamic<?> dynamic = options.isEmpty() ? null : new Dynamic<>(JsonOps.INSTANCE, JSONUtils.fromJson(options));
                return levelType.createDimensionGeneratorSettings(seed, structures, bonusChest, registries, dynamic);
            } catch (JsonParseException e)
            {
                LOGGER.error("Failed to parse generator options", e);
                return null;
            }
        }
    }

    public static LevelTypeManager get()
    {
        return INSTANCE;
    }
}
