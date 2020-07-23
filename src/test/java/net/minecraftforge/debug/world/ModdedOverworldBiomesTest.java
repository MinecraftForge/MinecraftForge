package net.minecraftforge.debug.world;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.Category;
import net.minecraft.world.biome.Biome.RainType;
import net.minecraft.world.biome.BiomeAmbience;
import net.minecraft.world.biome.MoodSoundAmbience;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.BiomeManager.BiomeEntry;
import net.minecraftforge.common.BiomeManager.BiomeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(value = ModdedOverworldBiomesTest.MOD_ID)
public class ModdedOverworldBiomesTest
{
    public static final String MOD_ID = "modded_overworld_biomes_test";

    private static DeferredRegister<Biome> BIOMES = DeferredRegister.create(ForgeRegistries.BIOMES, MOD_ID);
    private static RegistryObject<Biome> redBiome = BIOMES.register("red_biome", () -> new Biome(new Biome.Builder()
            .surfaceBuilder(SurfaceBuilder.DEFAULT, SurfaceBuilder.GRASS_DIRT_GRAVEL_CONFIG).precipitation(RainType.RAIN)
            .depth(1f).scale(1f).temperature(1f).downfall(1f).category(Category.PLAINS)
            .func_235097_a_(new BiomeAmbience.Builder().func_235246_b_(4159204).func_235248_c_(329011)
                    .func_235239_a_(12638463).func_235243_a_(MoodSoundAmbience.field_235027_b_).func_235238_a_())
            .parent((String) null))
    {
        @Override
        public int getGrassColor(double posX, double posZ)
        {
            return 0xff0000;
        }
    });

    public ModdedOverworldBiomesTest()
    {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        BIOMES.register(modBus);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        BiomeManager.addBiome(BiomeType.WARM, new BiomeEntry(redBiome.get(), 200));
    }

}
