package net.minecraftforge.debug.client;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.biome.provider.CheckerboardBiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraftforge.common.world.generator.GeneratorType;
import net.minecraftforge.common.world.generator.GeneratorTypeManager;
import net.minecraftforge.common.world.generator.type.NoiseGeneratorType;
import net.minecraftforge.common.world.generator.type.OverworldGeneratorType;
import net.minecraftforge.common.world.generator.type.SingleBiomeGeneratorType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
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
        GeneratorType test1 = new TestGeneratorType1();
        GeneratorType test2 = new TestGeneratorType2();

        GeneratorTypeManager.get().register(test1);
        GeneratorTypeManager.get().register(test2);
        GeneratorTypeManager.get().setDefaultGeneratorType(test1);
    }

    private static class TestGeneratorType1 extends SingleBiomeGeneratorType {

        public TestGeneratorType1() {
            super("single_amplified", DimensionType.field_235999_c_, DimensionSettings.field_242735_d, Biomes.BADLANDS);
        }
    }

    private static class TestGeneratorType2 extends NoiseGeneratorType {

        public TestGeneratorType2() {
            super("checkerboard", DimensionType.field_235999_c_, DimensionSettings.field_242734_c);
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
