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

package net.minecraftforge.debug.world;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Dynamic;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.Dimension;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.biome.provider.CheckerboardBiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.world.level.LevelType;
import net.minecraftforge.common.world.level.impl.NoiseLevelType;
import net.minecraftforge.common.world.level.impl.OverworldLevelType;
import net.minecraftforge.common.world.level.impl.SingleBiomeLevelType;
import net.minecraftforge.event.world.LevelTypeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Mod(LevelTypeTest.MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class LevelTypeTest
{
    public static final String MODID = "level_type_test";
    private static final Logger LOGGER = LogManager.getLogger(MODID);
    private static final DeferredRegister<LevelType> LEVEL_REGISTER = DeferredRegister.create(ForgeRegistries.LEVEL_TYPES, MODID);

    private static final RegistryObject<LevelType> SINGLE_AMPLIFIED = LEVEL_REGISTER.register("single_amplified",  () -> new SingleBiomeLevelType(DimensionSettings.field_242735_d, Biomes.BADLANDS));
    private static final RegistryObject<LevelType> SPOOKY = LEVEL_REGISTER.register("spooky", SpookyLevelType::new);
    private static final RegistryObject<LevelType> CHECKERBOARD = LEVEL_REGISTER.register("checkerboard", CheckboardType::new);

    public LevelTypeTest()
    {
        LEVEL_REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
        MinecraftForge.EVENT_BUS.addListener(LevelTypeTest::setDefaultLevelType);
        MinecraftForge.EVENT_BUS.addListener(LevelTypeTest::onCreateLevel);
    }

    @SubscribeEvent
    public static void complete(FMLLoadCompleteEvent event)
    {
        ForgeRegistries.LEVEL_TYPES.forEach(t -> LOGGER.info("Level: {}", t.getRegistryName()));
    }

    public static void setDefaultLevelType(LevelTypeEvent.DefaultLevel event)
    {
        LOGGER.info("Setting default LevelType to: {}", SINGLE_AMPLIFIED.getId());
        event.setLevelType(SINGLE_AMPLIFIED.get());
    }

    public static void onCreateLevel(LevelTypeEvent.CreateLevel event)
    {
        if (event.getLevelType() == SINGLE_AMPLIFIED.get())
        {
            LOGGER.info("Replacing nether dimension in level: {}", event.getLevelType().getRegistryName());
            Supplier<DimensionType> nether = () -> event.getDimensionTypes().func_230516_a_(DimensionType.field_236000_d_);
            ChunkGenerator generator = SPOOKY.get().createOverworldChunkGenerator(event.getSeed(), event.getBiomes(), event.getDimensionSettings(), event.getGeneratorOptions());
            Dimension dimension = new Dimension(nether, generator);
            // replace the nether dimension with our 'spooky' one
            event.addDimension(Dimension.field_236054_c_, dimension, true);
        }
    }

    // same as default generation but uses the nether DimensionType
    private static class SpookyLevelType extends OverworldLevelType
    {
        public SpookyLevelType()
        {
            super(DimensionSettings.field_242734_c, false, false);
        }

        // need to override #createDimensionRegistry so that an alternate DimensionType can be set
        @Override
        public SimpleRegistry<Dimension> getDimensions(long seed, Registry<Biome> biomes, Registry<DimensionType> dimensionTypes, Registry<DimensionSettings> dimensionSettings, @Nullable Dynamic<?> generatorOptions)
        {
            SimpleRegistry<Dimension> dimensions = super.getDimensions(seed, biomes, dimensionTypes, dimensionSettings, generatorOptions);
            Supplier<DimensionType> nether = () -> dimensionTypes.func_230516_a_(DimensionType.field_236000_d_);
            ChunkGenerator chunkGenerator = createOverworldChunkGenerator(seed, biomes, dimensionSettings, generatorOptions);
            return setOverworldGenerator(dimensions, nether, chunkGenerator);
        }
    }

    // default generation using the checkerboard BiomeProvider
    private static class CheckboardType extends NoiseLevelType
    {
        public CheckboardType()
        {
            super(DimensionSettings.field_242734_c);
        }

        // demo use of the debug flag (must hold shift for the option to appear when cycling level types)
        @Override
        public boolean isDebug()
        {
            return true;
        }

        @Override
        public BiomeProvider createBiomeProvider(long seed, Registry<Biome> biomes)
        {
            List<Supplier<Biome>> biomeList = biomes.stream()
                    .filter(this::isOverworldBiome)
                    .map(Suppliers::ofInstance)
                    .collect(Collectors.toList());
            return new CheckerboardBiomeProvider(biomeList, 1);
        }

        private boolean isOverworldBiome(Biome biome)
        {
            switch (biome.getCategory())
            {
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
