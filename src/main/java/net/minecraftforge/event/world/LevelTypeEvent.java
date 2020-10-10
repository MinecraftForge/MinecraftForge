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

import javax.annotation.Nullable;
import java.util.OptionalInt;

public abstract class LevelTypeEvent extends Event implements IModBusEvent {

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
    public static class DefaultLevelType extends LevelTypeEvent
    {
        public DefaultLevelType(LevelType levelType)
        {
            super(levelType);
        }

        public void setLevelType(LevelType levelType)
        {
            this.levelType = levelType;
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
        private final SimpleRegistry<Dimension> dimensions;

        public CreateLevel(LevelType levelType, SimpleRegistry<Dimension> dimensions, long seed, Registry<DimensionType> types, Registry<Biome> biomes, Registry<DimensionSettings> settings, @Nullable Dynamic<?> generatorOptions)
        {
            super(levelType);
            this.dimensions = dimensions;
            this.seed = seed;
            this.types = types;
            this.biomes = biomes;
            this.settings = settings;
            this.generatorOptions = generatorOptions;
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

        public boolean addDimension(RegistryKey<Dimension> key, Dimension dimension)
        {
            return addDimension(key, dimension, false);
        }

        public boolean addDimension(RegistryKey<Dimension> key, Dimension dimension, boolean replace)
        {
            if (replace || dimensions.func_230516_a_(key) == null)
            {
                // registers or replaces a dimension
                dimensions.func_241874_a(OptionalInt.empty(), key, dimension, Lifecycle.stable());
                return true;
            }
            return false;
        }
    }
}
