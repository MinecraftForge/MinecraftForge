package net.minecraftforge.debug.world;

import net.minecraft.core.QuartPos;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * To see the distribution of the added climate parameters you must go to the custom dimension (use: /execute in climate_parameter_extensions_test:extended run tp ...).
 * <p>You should observe a mostly mountainous terrain with forest biomes at lower y values, snowy slope peaks at high y values, oceans, and uncommon frozen oceans.</p>
 *
 * <p>Note: This mod adds a data asset located at: /data/climate_parameter_extensions_test/dimension/extended.json</p>
 */
@Mod(ClimateParameterExtensionsTest.MODID)
public class ClimateParameterExtensionsTest
{
    public static final String MODID = "climate_parameter_extensions_test";
    private static final ResourceKey<NormalNoise.NoiseParameters> MODDEDNESS = ResourceKey.create(Registry.NOISE_REGISTRY, new ResourceLocation(MODID, "moddedness"));
    private static final ResourceKey<NormalNoise.NoiseParameters> MODDEDNESS_LARGE = ResourceKey.create(Registry.NOISE_REGISTRY, new ResourceLocation(MODID, "moddedness_large"));

    public ClimateParameterExtensionsTest()
    {
        //We add two new climate parameters here, one for a proportion of the level's height, and one for a new noise named 'moddedness'
        BiomeManager.addClimateParameter(new ResourceLocation(MODID, "height"), Climate.Parameter.point(0.0F), (noiseSettings, registry, positionalRandomFactory) ->
        {
            int height = noiseSettings.height();
            float m = 1.0F / height;
            float b = 1.0F - (height + noiseSettings.minY()) * m;
            return (fromBlockX, fromBlockY, fromBlockZ, shiftedX, shiftedY, shiftedZ) -> Mth.clamp(m * QuartPos.toBlock(fromBlockY) + b, 0.0F, 1.0F);
        });
        BiomeManager.addClimateParameter(new ResourceLocation(MODID, "moddedness"), Climate.Parameter.point(0.0F), (noiseSettings, registry, positionalRandomFactory) ->
        {
            NormalNoise noise = Noises.instantiate(registry, positionalRandomFactory, noiseSettings.largeBiomes() ? MODDEDNESS_LARGE : MODDEDNESS);
            return (fromBlockX, fromBlockY, fromBlockZ, shiftedX, shiftedY, shiftedZ) -> (float) noise.getValue(shiftedX, 0.0F, shiftedZ);
        });
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerNoises);
    }

    private void registerNoises(FMLCommonSetupEvent event)
    {
        event.enqueueWork(() ->
        {
            BuiltinRegistries.register(BuiltinRegistries.NOISE, MODDEDNESS, new NormalNoise.NoiseParameters(-10, 1.5, 0.0, 1.0, 0.0, 0.0, 0.5));
            BuiltinRegistries.register(BuiltinRegistries.NOISE, MODDEDNESS_LARGE, new NormalNoise.NoiseParameters(-12, 1.5, 0.0, 1.0, 0.0, 0.0, 0.5));
        });
    }
}
