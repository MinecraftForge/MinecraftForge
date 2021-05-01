package net.minecraftforge.debug.misc;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.event.DynamicRegistriesLoadedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

@Mod("dynamicregistrytest")
public class DynamicRegistriesTest
{
    public static final RegistryKey<ConfiguredFeature<?,?>> TEST_FEATURE = RegistryKey.create(Registry.CONFIGURED_FEATURE_REGISTRY,
            new ResourceLocation("dynamicregistrytest:gold_pile"));

    public DynamicRegistriesTest()
    {
        MinecraftForge.EVENT_BUS.addListener(this::dynamicRegistriesLoaded);
    }

    void dynamicRegistriesLoaded(DynamicRegistriesLoadedEvent e)
    {
        DynamicRegistries.Impl registries = e.getRegistries();

        registries.registry(Registry.BIOME_REGISTRY).ifPresent(biomes ->
                registries.registry(Registry.CONFIGURED_FEATURE_REGISTRY).ifPresent(features ->
                {
                    ConfiguredFeature<?,?> testFeature = features.get(TEST_FEATURE);
                    biomes.forEach(biome ->
                    {
                        BiomeGenerationSettingsBuilder settingsBuilder = new BiomeGenerationSettingsBuilder(biome.getGenerationSettings());
                        settingsBuilder.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, testFeature);
                        try
                        {
                            if(biome.getRegistryName().compareNamespaced(new ResourceLocation("desert")) == 0)
                                ObfuscationReflectionHelper.findField(Biome.class, "field_242424_k").set(biome, settingsBuilder.build());
                        } catch (IllegalAccessException exc)
                        {
                            exc.printStackTrace();
                        }
                    });
                })
        );
    }
}
