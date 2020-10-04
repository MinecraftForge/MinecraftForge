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

import com.mojang.serialization.Dynamic;
import net.minecraft.client.gui.screen.BiomeGeneratorTypeScreens;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.Dimension;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public abstract class GeneratorType {

    protected static final Logger LOGGER = LogManager.getLogger();

    private final String name;
    private final RegistryKey<DimensionType> dimensionType;

    public GeneratorType(String name, RegistryKey<DimensionType> dimensionType) {
        this.name = name;
        this.dimensionType = dimensionType;
    }

    /**
     * Get the unique name of this GeneratorType
     *
     * @return the name of this GeneratorType
     */
    public String getName() {
        return name;
    }

    /**
     * The DimensionType that this generator applies to
     *
     * @return the RegistryKey of the DimensionType
     */
    public RegistryKey<DimensionType> getDimensionTypeKey() {
        return dimensionType;
    }

    /**
     * Returns whether this GeneratorType should be displayed as on option
     * in the client's world creation menus. If the return value is false,
     * the option for this GeneratorType will only be displayed while the
     * user is holding down L-Shift.
     *
     * @return true if this GeneratorType should be displayed as an option on the client
     */
    @OnlyIn(Dist.CLIENT)
    public boolean isVisible() {
        return true;
    }

    /**
     * Returns a factory for creating new instances of this GeneratorType's
     * settings/configuration screen.
     *
     * @return the edit screen factory or null
     */
    @Nullable
    @OnlyIn(Dist.CLIENT)
    public BiomeGeneratorTypeScreens.IFactory getEditScreen() {
        return null;
    }

    /**
     * Create a new DimensionGeneratorSettings instance using this GeneratorType
     *
     * @param seed       the world seed
     * @param structures flag controlling whether structures generate
     * @param bonusChest flag controlling whether the bonus chest will generate at spawn
     * @param registries the DynamicRegistries to construct the settings with
     * @return a new DimensionGeneratorSettings instance
     */
    public DimensionGeneratorSettings createDimensionGeneratorSettings(long seed, boolean structures, boolean bonusChest, DynamicRegistries registries) {
        return createDimensionGeneratorSettings(seed, structures, bonusChest, registries, null);
    }

    /**
     * Create a new DimensionGeneratorSettings instance using this GeneratorType
     *
     * @param seed             the world seed
     * @param structures       flag controlling whether structures generate
     * @param bonusChest       flag controlling whether the bonus chest will generate at spawn
     * @param registries       the DynamicRegistries to construct the settings with
     * @param generatorOptions the generator options (may be null)
     * @return a new DimensionGeneratorSettings instance
     */
    public DimensionGeneratorSettings createDimensionGeneratorSettings(long seed, boolean structures, boolean bonusChest, DynamicRegistries registries, @Nullable Dynamic<?> generatorOptions) {
        Registry<Biome> biomes = registries.func_243612_b(Registry.field_239720_u_);
        Registry<DimensionType> dimensionTypes = registries.func_243612_b(Registry.field_239698_ad_);
        Registry<DimensionSettings> dimensionSettings = registries.func_243612_b(Registry.field_243549_ar);
        return createDimensionGeneratorSettings(seed, structures, bonusChest, biomes, dimensionTypes, dimensionSettings, generatorOptions);
    }

    /**
     * Create a new DimensionGeneratorSettings instance using this GeneratorType
     *
     * @param seed              the world seed
     * @param structures        flag controlling whether structures generate
     * @param bonusChest        flag controlling whether the bonus chest will generate at spawn
     * @param biomes            the Biome instance registry
     * @param dimensionTypes    the DimensionType instance registry
     * @param dimensionSettings the DimesnionSettings instance registry
     * @param generatorOptions  the generator options (may be null)
     * @return a new DimensionGeneratorSettings instance
     */
    public DimensionGeneratorSettings createDimensionGeneratorSettings(long seed, boolean structures, boolean bonusChest, Registry<Biome> biomes, Registry<DimensionType> dimensionTypes, Registry<DimensionSettings> dimensionSettings, @Nullable Dynamic<?> generatorOptions) {
        SimpleRegistry<Dimension> dimensions = DimensionType.func_242718_a(dimensionTypes, biomes, dimensionSettings, seed);
        Supplier<DimensionType> dimensionType = () -> dimensionTypes.func_243576_d(getDimensionTypeKey());
        ChunkGenerator chunkGenerator = createChunkGenerator(seed, biomes, dimensionSettings, generatorOptions);
        return new DimensionGeneratorSettings(seed, structures, bonusChest, DimensionGeneratorSettings.func_241520_a_(dimensions, dimensionType, chunkGenerator));
    }

    @Override
    public String toString() {
        return "GeneratorType{" + name + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GeneratorType that = (GeneratorType) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * Create a new instance of this GeneratorType's ChunkGenerator
     *
     * @param seed              the world seed
     * @param biomes            the Biome registry
     * @param dimensionSettings the DimensionSettings registry
     * @param generatorOptions  the serialized generator options (may be null)
     * @return a new ChunkGenerator instance for the provided parameters
     */
    public abstract ChunkGenerator createChunkGenerator(long seed, Registry<Biome> biomes, Registry<DimensionSettings> dimensionSettings, @Nullable Dynamic<?> generatorOptions);
}
