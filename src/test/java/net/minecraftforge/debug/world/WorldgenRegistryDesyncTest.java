package net.minecraftforge.debug.world;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeMaker;
import net.minecraft.world.gen.feature.DungeonsFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod("worldgen_registry_desync_test")
@Mod.EventBusSubscriber
public class WorldgenRegistryDesyncTest {

    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, "worldgen_registry_desync_test");
    public static final RegistryObject<Feature<NoFeatureConfig>> dungeon = FEATURES.register("dungeon", () -> new DungeonsFeature(NoFeatureConfig.CODEC));
    public static final DeferredRegister<Biome> BIOMES = DeferredRegister.create(ForgeRegistries.BIOMES, "worldgen_registry_desync_test");
    public static final RegistryObject<Biome> biome = BIOMES.register("biome", BiomeMaker::theVoidBiome);

    public WorldgenRegistryDesyncTest()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        FEATURES.register(modEventBus);
        BIOMES.register(modEventBus);
    }
}
