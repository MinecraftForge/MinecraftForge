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

package net.minecraftforge.common.data.worldgen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cpw.mods.modlauncher.api.LamdbaExceptionUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.NoiseChunkGenerator;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.settings.*;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Noise Settings is the name on the minecraft wiki.
 */
public abstract class NoiseSettingsProvider extends RegistryBackedProvider<DimensionSettings>
{
    private final DataGenerator generator;
    private final String modid;
    protected final Map<ResourceLocation, Builder> builders = new HashMap<>();
    protected static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public NoiseSettingsProvider(DataGenerator generator, RegistryOpsHelper regOps, String modid)
    {
        super(DimensionSettings.field_236097_a_, regOps, Registry.field_243549_ar);
        this.generator = generator;
        this.modid = modid;
    }

    protected abstract void start();

    @Override
    public void act(DirectoryCache cache)
    {
        start();

        Path path = generator.getOutputFolder();

        builders.forEach(LamdbaExceptionUtils.rethrowBiConsumer((name, builder) ->
                this.saveAndRegister(builder.build(), name, cache, path.resolve("data/" + name.getNamespace() + "/worldgen/noise_settings/" + name.getPath() + ".json"))
        ));
    }

    public void put(ResourceLocation location, Builder builder)
    {
        builders.put(location, builder);
    }

    @Override
    public String getName()
    {
        return "Noise Settings: " + modid;
    }

    /**
     * Info gotten from <a href=https://minecraft.gamepedia.com/Custom_world_generation#Noise_settings>the wiki.</a>
     * Default values are the Overworld's.
     */
    public static class Builder
    {
        private DimensionStructuresSettings structuresSettings = new DimensionStructuresSettings(true);
        //Simply called "noise" in the minecraft wiki.
        private NoiseSettings generatorNoise = new NoiseSettings(256, new ScalingSettings(0.9999999814507745D, 0.9999999814507745D, 80.0D, 160.0D), new SlideSettings(-10, 3, 0), new SlideSettings(-30, 0, 0), 1, 2, 1.0D, -0.46875D, true, true, false, false);
        private BlockState defaultBlock = Blocks.STONE.getDefaultState();
        private BlockState defaultFluid = Blocks.WATER.getDefaultState();
        private int bedrockRoofPosition = -10; //[-20, 276]
        private int bedrockFloorPosition = 0; //[-20, 276]
        private int seaLevel = 63; //[0, 255]
        private boolean disableModGeneration = false;

        protected DimensionSettings build()
        {
            return new DimensionSettings(structuresSettings, generatorNoise, defaultBlock, defaultFluid, bedrockRoofPosition, bedrockFloorPosition, seaLevel, disableModGeneration);
        }

        /**
         * Set the default filler block.
         */
        public Builder setDefaultBlock(BlockState state)
        {
            this.defaultBlock = state;
            return this;
        }

        /**
         * Set the default filler Fluid (can be a BlockState that is not a fluid, End uses Air)
         */
        public Builder setDefaultFluid (BlockState state)
        {
            this.defaultFluid = state;
            return this;
        }

        /**
         * Distance from the height of the world defined by {@link NoiseSettings}
         * If it is out of bounds none is made. (see {@link NoiseChunkGenerator#makeBedrock})
         */
        public Builder setBedrockRoofPosition (int pos)
        {
            if(pos < -20 || pos > 276)
                throw new IllegalArgumentException("Bedrock roof position is out of bounds!");
            this.bedrockRoofPosition = pos;
            return this;
        }

        /**
         * Distance from 0 of the floor. If it is out of bounds none is made. (see {@link NoiseChunkGenerator#makeBedrock})
         */
        public Builder setBedrockFloorPosition (int pos)
        {
            if(pos < -20 || pos > 276)
                throw new IllegalArgumentException("Bedrock floor position is out of bounds!");
            this.bedrockFloorPosition = pos;
            return this;
        }

        public Builder setSeaLevel(int level)
        {
            if(level < 0 || level > 255)
                throw new IllegalArgumentException("Sea Level is out of bounds!");
            this.seaLevel = level;
            return this;
        }

        public Builder disableMobSpawning(boolean noSpawns)
        {
            this.disableModGeneration = noSpawns;
            return this;
        }

        public StructureSettingsBuilder setupStructures()
        {
            return new StructureSettingsBuilder();
        }

        public NoiseBuilder setupNoise()
        {
            return new NoiseBuilder();
        }

        private Builder setStructureSettings(DimensionStructuresSettings settings)
        {
            this.structuresSettings = settings;
            return this;
        }

        private Builder setNoise(NoiseSettings noise)
        {
            this.generatorNoise = noise;
            return this;
        }

        /**
         * Default values are the Overworld's.
         * See <a href=https://minecraft.gamepedia.com/Custom_world_generation#Noise_settings>the wiki</a> for more info.
         */
        public class NoiseBuilder
        {
            private int height = 256;
            private ScalingSettings sampling = new ScalingSettings(0.9999999814507745D, 0.9999999814507745D, 80.0D, 160.0D);
            private SlideSettings topSlide = new SlideSettings(-10, 3, 0);
            private SlideSettings bottomSlide = new SlideSettings(-30, 0, 0);
            private int sizeHorizontal = 1; //[1,4]
            private int sizeVertical = 2; //[1,4]
            private double densityFactor = 1;
            private double densityOffset = -0.46875;
            private boolean simplexSurfaceNoise = true;
            private boolean randomDensityOffset = true;
            private boolean islandNoiseOverride = false;
            private boolean amplified = false;

            public Builder finish()
            {
                return Builder.this.setNoise(new NoiseSettings(height, sampling, topSlide, bottomSlide, sizeHorizontal, sizeVertical, densityFactor, densityOffset, simplexSurfaceNoise, randomDensityOffset, islandNoiseOverride, amplified));
            }

            public NoiseBuilder setHeight(int height)
            {
                if(height < 0 || height > 256)
                    throw new IllegalStateException("Height is out of bounds");
                this.height = height;
                return this;
            }

            public NoiseBuilder setSampling(double xzScale, double yScale, double xzFactor, double yFactor)
            {
                this.sampling = new ScalingSettings(xzScale, yScale, xzFactor, yFactor);
                return this;
            }

            public NoiseBuilder setTopSlide(int target, int size, int offset)
            {
                if(size < 0 || size > 256)
                    throw new IllegalStateException("Slide size is out of bounds");
                this.topSlide = new SlideSettings(target, size, offset);
                return this;
            }

            public NoiseBuilder setBottomSlide(int target, int size, int offset)
            {
                if(size < 0 || size > 256)
                    throw new IllegalStateException("Slide size is out of bounds");
                this.bottomSlide = new SlideSettings(target, size, offset);
                return this;
            }

            public NoiseBuilder setSizeHorizontal(int size)
            {
                if(size < 1 || size > 4)
                    throw new IllegalStateException("Size is out of bounds");
                this.sizeHorizontal = size;
                return this;
            }

            public NoiseBuilder setSizeVeritcal(int size)
            {
                if(size < 1 || size > 4)
                    throw new IllegalStateException("Size is out of bounds");
                this.sizeVertical = size;
                return this;
            }

            public NoiseBuilder setDensityFactor(double densityFactor)
            {
                this.densityFactor = densityFactor;
                return this;
            }

            public NoiseBuilder setDensityOffset(double densityOffset)
            {
                this.densityOffset = densityOffset;
                return this;
            }

            public NoiseBuilder setSimplexSurfaceNoise(boolean value)
            {
                this.simplexSurfaceNoise = value;
                return this;
            }

            public NoiseBuilder setRandomDensityOffset(boolean value)
            {
                this.randomDensityOffset = value;
                return this;
            }

            public NoiseBuilder setIslandNoiseOverride(boolean value)
            {
                this.islandNoiseOverride = value;
                return this;
            }

            public NoiseBuilder setAmplified(boolean value)
            {
                this.amplified = value;
                return this;
            }
        }

        public class StructureSettingsBuilder
        {
            private final Map<Structure<?>, StructureSeparationSettings> separationSettings = new HashMap<>();
            private Optional<StructureSpreadSettings> strongholdSettings = Optional.empty();

            /**
             * Sets the default settings with or without stronghold spawns.
             */
            public Builder defaultSettings(boolean withStronghold)
            {
                return Builder.this.setStructureSettings(new DimensionStructuresSettings(withStronghold));
            }

            /**
             * All Structures must have an associated separation value, or else weird stuff happens.
             * Before building, fill the map with any missing structures.
             */
            public Builder finish()
            {
                DimensionStructuresSettings.field_236191_b_.forEach((structure, separation) -> separationSettings.putIfAbsent(structure, separation));
                return Builder.this.setStructureSettings(new DimensionStructuresSettings(strongholdSettings, separationSettings));
            }

            /**
             * @param structure     A VANILLA structure TODO adapt for modded (requires ForgeRegistry codecs)
             * @param spacing       Average distance between 2 structure placement attempts.
             * @param separation    Minimum distance between 2 structure of this type. Has to be less than spacing
             * @param salt          Number that assists in randomization.
             * @return This builder.
             */
            public StructureSettingsBuilder setStructureSeparation(Structure<?> structure, int spacing, int separation, int salt)
            {
                if(separation < spacing)
                    throw new IllegalArgumentException("Spacing has to be smaller than separation");
                separationSettings.put(structure, new StructureSeparationSettings(spacing, separation, salt));
                return this;
            }

            /**
             * @param distance How far apart strongholds are.
             * @param count    How many Stronghold exists
             * @param spread   Unknown effect.
             * @return this builder.
             */
            public StructureSettingsBuilder setStrongholdSettings(int distance, int count, int spread)
            {
                strongholdSettings = Optional.of(new StructureSpreadSettings(distance, count, spread));
                return this;
            }
        }
    }
}
