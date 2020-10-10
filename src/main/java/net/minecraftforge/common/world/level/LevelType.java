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
import net.minecraft.client.gui.screen.BiomeGeneratorTypeScreens;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.Dimension;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.world.level.impl.DebugLevelType;
import net.minecraftforge.common.world.level.impl.FlatLevelType;
import net.minecraftforge.common.world.level.impl.OverworldLevelType;
import net.minecraftforge.common.world.level.impl.SingleBiomeLevelType;
import net.minecraftforge.event.world.LevelTypeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * Represents the type of world-generation that will be used when creating a new world. Implementations of this class
 * provide the {@link net.minecraft.world.gen.ChunkGenerator} for the overworld and can optionally define additional
 * dimensions that generate such as the_nether & end.
 */
public abstract class LevelType extends ForgeRegistryEntry<LevelType>
{
    public static final LevelType DEFAULT = new OverworldLevelType("default", DimensionSettings.field_242734_c, false, false).setRegistryName("default");
    public static final LevelType FLAT = new FlatLevelType("flat").setRegistryName("flat");
    public static final LevelType LARGE_BIOMES = new OverworldLevelType("large_biomes", DimensionSettings.field_242734_c, false, true).setRegistryName("large_biomes");
    public static final LevelType AMPLIFIED = new OverworldLevelType("amplified", DimensionSettings.field_242735_d, false, false).setRegistryName("amplified");
    public static final LevelType SINGLE_BIOME_SURFACE = new SingleBiomeLevelType("single_biome_surface",DimensionSettings.field_242734_c, Biomes.PLAINS).setRegistryName("single_biome_surface");
    public static final LevelType SINGLE_BIOME_CAVES = new SingleBiomeLevelType("single_biome_caves", DimensionSettings.field_242738_g, Biomes.PLAINS).setRegistryName("single_biome_caves");
    public static final LevelType SINGLE_BIOME_FLOATING_ISLANDS = new SingleBiomeLevelType("single_biome_floating_islands", DimensionSettings.field_242739_h, Biomes.PLAINS).setRegistryName("single_biome_floating_islands");
    public static final LevelType DEBUG_ALL_BLOCK_STATES = new DebugLevelType("debug_all_block_states").setRegistryName("debug_all_block_states");
    private static final Logger LOGGER = LogManager.getLogger();

    private final String name;

    public LevelType(@Nonnull String name)
    {
        this.name = name;
    }

    /**
     * Create a new instance of this {@link LevelType}'s overworld {@link net.minecraft.world.gen.ChunkGenerator}.
     *
     * @param seed              The world seed.
     * @param biomes            The biome registry.
     * @param dimensionSettings The dimension settings registry.
     * @param generatorOptions  The legacy generator options (may be null).
     * @return A new overworld {@link net.minecraft.world.gen.ChunkGenerator} instance.
     */
    public abstract ChunkGenerator createOverworldChunkGenerator(long seed, Registry<Biome> biomes, Registry<DimensionSettings> dimensionSettings, @Nullable Dynamic<?> generatorOptions);

    /**
     * A debug {@link LevelType} does not show up on the client as a selectable option unless the user is holding
     * down left-shift whilst cycling the level type options.
     *
     * @return True if this level type should be treated as a debug level type.
     */
    public boolean isDebug()
    {
        return false;
    }

    /**
     * Returns a factory for creating new instances of this {@link LevelType}s settings/configuration screen.
     *
     * @return The edit screen factory or null.
     */
    @Nullable
    @OnlyIn(Dist.CLIENT)
    public BiomeGeneratorTypeScreens.IFactory getEditScreen()
    {
        return null;
    }

    /**
     * Create a new {@link net.minecraft.world.gen.settings.DimensionGeneratorSettings} instance for this {@link LevelType}.
     *
     * @param seed             The world seed.
     * @param structures       A flag determining whether structures should generate.
     * @param bonusChest       A flag determining whether a bonus chest should generate.
     * @param registries       The dynamic registries instance.
     * @param generatorOptions The legacy generator options (may be null).
     * @return A new {@link net.minecraft.world.gen.settings.DimensionGeneratorSettings} instance.
     */
    public DimensionGeneratorSettings createLevel(long seed, boolean structures, boolean bonusChest, DynamicRegistries registries, @Nullable Dynamic<?> generatorOptions)
    {
        Registry<Biome> biomes = registries.func_243612_b(Registry.field_239720_u_);
        Registry<DimensionType> dimensionTypes = registries.func_243612_b(Registry.field_239698_ad_);
        Registry<DimensionSettings> dimensionSettings = registries.func_243612_b(Registry.field_243549_ar);
        SimpleRegistry<Dimension> dimensions = getDimensions(seed, biomes, dimensionTypes, dimensionSettings, generatorOptions);
        // allow other mods to add dimensions to this level
        MinecraftForge.EVENT_BUS.post(new LevelTypeEvent.CreateLevel(this, dimensions, seed, dimensionTypes, biomes, dimensionSettings, generatorOptions));
        return new DimensionGeneratorSettings(seed, structures, bonusChest, dimensions);
    }

    /**
     * Creates the registry of {@link net.minecraft.world.Dimension} that this {@link LevelType} provides.
     * The default implementation of this method populates the registry with this {@link LevelType}'s overworld Dimension,
     * then with the default nether and end Dimensions respectively. This behaviour can be overridden but the minimum
     * requirement is that the returned registry contains a Dimension for the overworld.
     *
     * @param seed              The world seed.
     * @param biomes            The biome registry.
     * @param dimensionTypes    The dimension type registry.
     * @param dimensionSettings The dimension settings registry.
     * @param generatorOptions  The legacy generator options (may be null).
     * @return A new {@link net.minecraft.util.registry.SimpleRegistry} instance containing the dimensions provided by
     * this level type.
     */
    public SimpleRegistry<Dimension> getDimensions(long seed, Registry<Biome> biomes, Registry<DimensionType> dimensionTypes, Registry<DimensionSettings> dimensionSettings, @Nullable Dynamic<?> generatorOptions)
    {
        // Builds the default Dimension registry containing the nether and end Dimensions
        SimpleRegistry<Dimension> registry = DimensionType.func_242718_a(dimensionTypes, biomes, dimensionSettings, seed);
        // Creates the overworld chunk generator
        ChunkGenerator chunkGenerator = createOverworldChunkGenerator(seed, biomes, dimensionSettings, generatorOptions);
        Supplier<DimensionType> overworldType = () -> dimensionTypes.func_243576_d(DimensionType.field_235999_c_);
        // Adds the overworld Dimension to the head of the registry
        return setOverworldGenerator(registry, overworldType, chunkGenerator);
    }

    @Override
    public String toString()
    {
        return "LevelType{" + name + "}";
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LevelType that = (LevelType) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode()
    {
        return name.hashCode();
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
    public static DimensionGeneratorSettings createGeneratorSettings(String name, long seed, boolean structures, boolean bonusChest, DynamicRegistries registries, String options)
    {
        ResourceLocation location = new ResourceLocation(name);
        LevelType levelType = ForgeRegistries.LEVEL_TYPES.getValue(location);
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
                return levelType.createLevel(seed, structures, bonusChest, registries, dynamic);
            } catch (JsonParseException e)
            {
                LOGGER.error("Failed to parse generator options", e);
                return null;
            }
        }
    }

    /**
     * Helper method for updating the overworld {@link net.minecraft.world.gen.ChunkGenerator} in an existing
     * {@link net.minecraft.world.gen.settings.DimensionGeneratorSettings} instance.
     *
     * @param settings   The settings to update.
     * @param registries The dynamic registries.
     * @param generator  The new overworld chunk generator.
     * @return A new {@link net.minecraft.world.gen.settings.DimensionGeneratorSettings} instance using the provided
     * chunk generator for the overworld.
     */
    protected static DimensionGeneratorSettings setOverworldGenerator(DimensionGeneratorSettings settings, DynamicRegistries registries, ChunkGenerator generator)
    {
        return setOverworldGenerator(settings, registries, DimensionType.field_235999_c_, generator);
    }

    /**
     * Helper method for updating the overworld {@link net.minecraft.world.gen.ChunkGenerator} in an existing
     * {@link net.minecraft.world.gen.settings.DimensionGeneratorSettings} instance.
     *
     * @param settings      The settings to update.
     * @param registries    The dynamic registries.
     * @param dimensionType The dimension type to assign the new dimension.
     * @param generator     The chunk generator for the new dimension.
     * @return A new {@link net.minecraft.world.gen.settings.DimensionGeneratorSettings} instance using the provided
     * chunk generator for the overworld.
     */
    protected static DimensionGeneratorSettings setOverworldGenerator(DimensionGeneratorSettings settings, DynamicRegistries registries, RegistryKey<DimensionType> dimensionType, ChunkGenerator generator)
    {
        long seed = settings.func_236221_b_();
        boolean hasStructures = settings.func_236222_c_();
        boolean hasBonusChest = settings.func_236223_d_();
        Registry<DimensionType> dimensionTypes = registries.func_243612_b(Registry.field_239698_ad_);
        Supplier<DimensionType> dimensionTypeSupplier = () -> dimensionTypes.func_230516_a_(dimensionType);
        SimpleRegistry<Dimension> dimensions = setOverworldGenerator(settings.func_236224_e_(), dimensionTypeSupplier, generator);
        return new DimensionGeneratorSettings(seed, hasStructures, hasBonusChest, dimensions);
    }

    /**
     * Helper method for updating the overworld Dimension in a Dimension registry.
     *
     * @param dimensions The dimension registry to update.
     * @param type       The dimension type to assign the new dimension.
     * @param generator  The chunk generator for the new dimension.
     * @return A new {@link net.minecraft.util.registry.SimpleRegistry} containing the contents of the provided
     * dimension registry with a new overworld dimension using the provided chunk generator.
     */
    protected static SimpleRegistry<Dimension> setOverworldGenerator(SimpleRegistry<Dimension> dimensions, Supplier<DimensionType> type, ChunkGenerator generator)
    {
        return DimensionGeneratorSettings.func_241520_a_(dimensions, type, generator);
    }
}
