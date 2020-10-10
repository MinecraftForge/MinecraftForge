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

package net.minecraftforge.event.world;

import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.Dimension;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraftforge.common.world.level.LevelType;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.lifecycle.IModBusEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.OptionalInt;

public abstract class LevelTypeEvent extends Event implements IModBusEvent
{
    private static final Logger LOGGER = LogManager.getLogger();

    protected LevelType levelType;

    protected LevelTypeEvent(LevelType levelType)
    {
        this.levelType = levelType;
    }

    public LevelType getLevelType()
    {
        return levelType;
    }

    /**
     * Used to set the default {@link net.minecraftforge.common.world.level.LevelType} for creating new worlds with.
     */
    public static class DefaultLevel extends LevelTypeEvent
    {
        public DefaultLevel(LevelType levelType)
        {
            super(levelType);
        }

        /**
         * Set the default {@link net.minecraftforge.common.world.level.LevelType}
         *
         * @param levelType The value to use as default.
         * @return True if the default level type was updated.
         */
        public boolean setLevelType(@Nonnull LevelType levelType)
        {
            if (this.levelType != levelType)
            {
                LOGGER.info("Default LevelType '{}' has been overridden by '{}'", this.levelType.getRegistryName(), levelType.getRegistryName());
                this.levelType = levelType;
                return true;
            }
            return false;
        }
    }

    /**
     * Called when creating a level from an {@link net.minecraftforge.common.world.level.LevelType}. Mods can use this
     * event to add extra dimensions to the level.
     */
    public static class CreateLevel extends LevelTypeEvent
    {
        private final long seed;
        private final Registry<DimensionType> types;
        private final Registry<Biome> biomes;
        private final Registry<DimensionSettings> settings;
        private final Dynamic<?> generatorOptions;

        private final boolean replacements;
        private final SimpleRegistry<Dimension> dimensions;

        public CreateLevel(LevelType levelType, SimpleRegistry<Dimension> dimensions, boolean replacements, long seed, Registry<DimensionType> types, Registry<Biome> biomes, Registry<DimensionSettings> settings, @Nullable Dynamic<?> generatorOptions)
        {
            super(levelType);
            this.dimensions = dimensions;
            this.seed = seed;
            this.types = types;
            this.biomes = biomes;
            this.settings = settings;
            this.replacements = replacements;
            this.generatorOptions = generatorOptions;
        }

        public boolean allowsReplacements()
        {
            return replacements;
        }

        public long getSeed()
        {
            return seed;
        }

        public Registry<DimensionType> getDimensionTypes()
        {
            return types;
        }

        public Registry<Biome> getBiomes()
        {
            return biomes;
        }

        public Registry<DimensionSettings> getDimensionSettings()
        {
            return settings;
        }

        public Dynamic<?> getGeneratorOptions()
        {
            return generatorOptions;
        }

        /**
         * Add a {@link net.minecraft.world.Dimension} to the levels dimension registry if an entry has not
         * already been registered for the provided {@link net.minecraft.util.RegistryKey}.
         *
         * @param key       The registry key of the dimension to add
         * @param dimension The dimension to add
         * @return True if the dimension was added to the levels dimension registry.
         */
        public boolean addDimension(RegistryKey<Dimension> key, Dimension dimension)
        {
            return addDimension(key, dimension, false);
        }

        /**
         * Add a {@link net.minecraft.world.Dimension} to the levels dimension registry, optionally replacing an
         * existing entry for the provided {@link net.minecraft.util.RegistryKey} if the event supports it.
         *
         * @param key       The registry key of the dimension to add
         * @param dimension The dimension to add
         * @param replace   A flag to control whether an existing value for the key will be replaced.
         * @return True if the dimension was added to the levels dimension registry.
         */
        public boolean addDimension(RegistryKey<Dimension> key, Dimension dimension, boolean replace)
        {
            boolean present = dimensions.func_230516_a_(key) != null;
            if ((replace && allowsReplacements()) || !present)
            {
                dimensions.func_241874_a(OptionalInt.empty(), key, dimension, Lifecycle.stable());
                if (present)
                {
                    LOGGER.info("Dimension for '{}' has been replaced by '{}'", key.func_240901_a_(), dimension);
                }
                return true;
            }
            return false;
        }
    }
}
