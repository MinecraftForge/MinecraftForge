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

package net.minecraftforge.client;

import com.google.common.collect.Maps;
import net.minecraft.client.gui.screen.BiomeGeneratorTypeScreens;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;
import net.minecraftforge.common.world.ForgeWorldType;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;

public class ForgeWorldTypeScreens
{
    private static final Logger LOGGER = LogManager.getLogger();

    private static final Map<ForgeWorldType, BiomeGeneratorTypeScreens> GENERATORS = Maps.newHashMap();
    private static final Map<ForgeWorldType, BiomeGeneratorTypeScreens.IFactory> GENERATOR_SCREEN_FACTORIES = Maps.newHashMap();

    public static synchronized void registerFactory(ForgeWorldType type, BiomeGeneratorTypeScreens.IFactory factory)
    {
        if (GENERATOR_SCREEN_FACTORIES.containsKey(type))
            throw new IllegalStateException("Factory has already been registered for: " + type);

        GENERATOR_SCREEN_FACTORIES.put(type, factory);
    }

    static BiomeGeneratorTypeScreens getDefaultGenerator()
    {
        ForgeWorldType def = ForgeWorldType.getDefaultWorldType();
        if (def == null)
        {
            return BiomeGeneratorTypeScreens.field_239066_a_;
        }

        BiomeGeneratorTypeScreens gen = GENERATORS.get(def);
        if (gen == null)
        {
            LOGGER.error("The default world type '{}' has not been added to the GUI. Was it registered too late?", def.getRegistryName());
            return BiomeGeneratorTypeScreens.field_239066_a_;
        }

        return gen;
    }

    static BiomeGeneratorTypeScreens.IFactory getGeneratorScreenFactory(Optional<BiomeGeneratorTypeScreens> generator, @Nullable BiomeGeneratorTypeScreens.IFactory biomegeneratortypescreens$ifactory)
    {
        return generator.filter(gen -> gen instanceof GeneratorType)
                .map(type -> GENERATOR_SCREEN_FACTORIES.get(((GeneratorType)type).getWorldType()))
                .orElse(biomegeneratortypescreens$ifactory);
    }

    static void registerTypes()
    {
        ForgeRegistries.WORLD_TYPES.forEach(wt -> {
            GeneratorType gen = new GeneratorType(wt);
            GENERATORS.put(wt, gen);
            BiomeGeneratorTypeScreens.registerGenerator(gen);
        });
    }

    private static class GeneratorType extends BiomeGeneratorTypeScreens
    {
        private final ForgeWorldType worldType;

        public GeneratorType(ForgeWorldType wt)
        {
            super(wt.getDisplayName());
            worldType = wt;
        }

        public ForgeWorldType getWorldType()
        {
            return worldType;
        }

        @Nonnull
        @Override
        public DimensionGeneratorSettings func_241220_a_(@Nonnull DynamicRegistries.Impl dynamicRegistries, long seed, boolean generateStructures, boolean bonusChest)
        {
            return worldType.createSettings(dynamicRegistries, seed, generateStructures, bonusChest, "");
        }

        @Nonnull
        @Override
        protected ChunkGenerator func_241869_a(@Nonnull Registry<Biome> p_241869_1_, @Nonnull Registry<DimensionSettings> p_241869_2_, long p_241869_3_)
        {
            return worldType.createChunkGenerator(p_241869_1_, p_241869_2_, p_241869_3_, "");
        }
    }
}
