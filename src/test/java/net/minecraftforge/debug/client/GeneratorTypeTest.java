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

package net.minecraftforge.debug.client;

import com.google.common.base.Suppliers;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.biome.provider.CheckerboardBiomeProvider;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraftforge.common.world.generator.GeneratorType;
import net.minecraftforge.common.world.generator.GeneratorTypeManager;
import net.minecraftforge.common.world.generator.type.FlatGeneratorType;
import net.minecraftforge.common.world.generator.type.NoiseGeneratorType;
import net.minecraftforge.common.world.generator.type.SingleBiomeGeneratorType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Mod(GeneratorTypeTest.MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class GeneratorTypeTest {

    public static final String MODID = "generator_type_test";
    private static final Logger LOGGER = LogManager.getLogger(MODID);

    @SubscribeEvent
    public static void setup(FMLCommonSetupEvent event) {
        LOGGER.info("Registering GeneratorTypes");
        GeneratorType test1 = new SingleBiomeGeneratorType("single_amplified", DimensionType.field_235999_c_, DimensionSettings.field_242735_d, Biomes.BADLANDS);
        GeneratorType test2 = new FlatGeneratorType("flat_nether", DimensionType.field_236000_d_);
        GeneratorType test3 = new CustomCheckboardType("checkerboard", DimensionType.field_235999_c_);

        GeneratorTypeManager.get().register(test1);
        GeneratorTypeManager.get().register(test2);
        GeneratorTypeManager.get().register(test3);

        GeneratorTypeManager.get().setDefaultGeneratorType(test1);
    }

    private static class CustomCheckboardType extends NoiseGeneratorType {

        public CustomCheckboardType(String name, RegistryKey<DimensionType> dimensionType) {
            super(name, dimensionType, DimensionSettings.field_242734_c);
        }

        @Override
        public boolean isVisible() {
            return false;
        }

        @Override
        public BiomeProvider createBiomeProvider(long seed, Registry<Biome> biomes) {
            List<Supplier<Biome>> biomeList = biomes.stream()
                    .filter(this::isOverworldBiome)
                    .map(Suppliers::ofInstance)
                    .collect(Collectors.toList());
            return new CheckerboardBiomeProvider(biomeList, 1);
        }

        private boolean isOverworldBiome(Biome biome) {
            switch (biome.getCategory()) {
                case NONE:
                case NETHER:
                case THEEND:
                    return false;
                default:
                    return true;
            }
        }
    }
}
