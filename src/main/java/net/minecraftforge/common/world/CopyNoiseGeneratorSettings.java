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

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.block.BlockState;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKeyCodec;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.NoiseSettings;
import net.minecraft.world.gen.settings.ScalingSettings;
import net.minecraft.world.gen.settings.SlideSettings;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraft.world.gen.settings.StructureSpreadSettings;
import net.minecraftforge.common.ForgeMod;

// TODO this'll need fixes in 1.17 if noise format changes
public class CopyNoiseGeneratorSettings extends DimensionSettings
{
    /**
     * Codec to use for reading the noise setting during datapack import.
     * This outputs the parent noise setting as a fully exploded jsonobject, so it's not suitable for datagen; use DATAGEN_CODEC for that.
     */
    public static final Codec<CopyNoiseGeneratorSettings> CODEC = RegistryKeyCodec.create(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, DimensionSettings.DIRECT_CODEC)
        .xmap(CopyNoiseGeneratorSettings::copyFromParent, CopyNoiseGeneratorSettings::getParentGetter)
        .fieldOf("parent")
        .codec();
    
    /**
     * Codec to use for datagenerating parented noise settings
     */
    public static final Codec<RegistryKey<DimensionSettings>> DATAGEN_CODEC = Codec.mapPair(
            ResourceLocation.CODEC.fieldOf("type"),
            ResourceLocation.CODEC.fieldOf("parent").xmap(RegistryKey.elementKey(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY), RegistryKey::location))
        .xmap(Pair::getSecond, parent -> Pair.of(ForgeMod.COPY_NOISE_GENERATOR_TYPE.getId(), parent))
        .codec();
    
    private final Supplier<DimensionSettings> parentGetter;
    
    private CopyNoiseGeneratorSettings(DimensionStructuresSettings structureSettings, NoiseSettings noiseSettings, BlockState defaultBlock, BlockState defaultFluid, int bedrockRoofLevel,
        int bedrockFloorLevel, int seaLevel, boolean disableMobGeneration, Supplier<DimensionSettings> parentGetter)
    {
        super(structureSettings, noiseSettings, defaultBlock, defaultFluid, bedrockRoofLevel, bedrockFloorLevel, seaLevel, disableMobGeneration);
        this.parentGetter = parentGetter;
    }

    private Supplier<DimensionSettings> getParentGetter()
    {
        return this.parentGetter;
    }
    
    @Override
    public ForgeNoiseGeneratorType<? extends DimensionSettings> getType()
    {
        return ForgeMod.COPY_NOISE_GENERATOR_TYPE.get();
    }

    public static CopyNoiseGeneratorSettings copyFromParent(Supplier<DimensionSettings> parentGetter)
    {
        DimensionSettings parent = parentGetter.get();
        
        // deep copy the structure settings
        DimensionStructuresSettings parentStructures = parent.structureSettings();
        Optional<StructureSpreadSettings> stronghold = Optional.ofNullable(parentStructures.stronghold()).map(parentStronghold -> new StructureSpreadSettings(parentStronghold.distance(), parentStronghold.spread(), parentStronghold.count()));
        ImmutableMap.Builder<Structure<?>, StructureSeparationSettings> structureBuilder = ImmutableMap.builder();
        parentStructures.structureConfig().putAll(parentStructures.structureConfig());
        Map<Structure<?>, StructureSeparationSettings> separation = structureBuilder.build();
        DimensionStructuresSettings structureSettings = new DimensionStructuresSettings(stronghold, separation);
        
        // deep copy the noise settings
        NoiseSettings parentNoise = parent.noiseSettings();
        ScalingSettings parentSampling = parentNoise.noiseSamplingSettings();
        ScalingSettings samplingSettings = new ScalingSettings(parentSampling.xzScale(), parentSampling.yScale(), parentSampling.xzFactor(), parentSampling.yFactor());
        SlideSettings parentTopSlide = parentNoise.topSlideSettings();
        SlideSettings topSlide = new SlideSettings(parentTopSlide.target(), parentTopSlide.size(), parentTopSlide.offset());
        SlideSettings parentBottomSlide = parentNoise.bottomSlideSettings();
        SlideSettings bottomSlide = new SlideSettings(parentBottomSlide.target(), parentBottomSlide.size(), parentBottomSlide.offset());
        NoiseSettings noiseSettings = new NoiseSettings(parentNoise.height(),
                samplingSettings, topSlide, bottomSlide,
                parentNoise.noiseSizeHorizontal(), parentNoise.noiseSizeVertical(),
                parentNoise.densityFactor(), parentNoise.densityOffset(),
                parentNoise.useSimplexSurfaceNoise(), parentNoise.randomDensityOffset(),
                parentNoise.islandNoiseOverride(), parentNoise.isAmplified());
        
        return new CopyNoiseGeneratorSettings(structureSettings, noiseSettings, parent.getDefaultBlock(), parent.getDefaultFluid(), parent.getBedrockRoofPosition(), parent.getBedrockFloorPosition(), parent.seaLevel(), parent.disableMobGeneration(), parentGetter); 
    }

}
