package net.minecraftforge.debug.world;

import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.SurfaceBuilders;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.*;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.Optional;

/**
 * Must be enabled.
 *
 * When generated the "biome_variant_test:main" biome should generate in the overworld.
 * Within the "biome area" there should be hills ("biome_variant_test:hills") and potentially rivers ("biome_variant_test:river") flowing to/from other biomes.
 * At the edge of the "biome area" there should be an edge biome ("biome_variant_test:edge").
 * If the "biome area" is next to an ocean there should be a shore ("biome_variant_test:shore").
 */
@Mod(BiomeVariantTest.MODID)
public class BiomeVariantTest {

    static final String MODID = "biome_variant_test";
    private static final boolean ENABLED = false;

    private static final ResourceKey<Biome> MAIN_KEY = ResourceKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(MODID, "main"));
    private static final ResourceKey<Biome> HILLS_KEY = ResourceKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(MODID, "hills"));
    private static final ResourceKey<Biome> EDGE_KEY = ResourceKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(MODID, "edge"));
    private static final ResourceKey<Biome> RIVER_KEY = ResourceKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(MODID, "river"));
    private static final ResourceKey<Biome> SHORE_KEY = ResourceKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(MODID, "shore"));

    public BiomeVariantTest()
    {
        if(ENABLED)
        {
            FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Biome.class, this::onRegisterBiome);
            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        }
    }

    private void onRegisterBiome(RegistryEvent.Register<Biome> event)
    {
        //Core biome
        BiomeGenerationSettings.Builder main_generation = (new BiomeGenerationSettings.Builder()).surfaceBuilder(SurfaceBuilders.END);
        BiomeDefaultFeatures.addDefaultCarvers(main_generation);
        BiomeDefaultFeatures.addDefaultLakes(main_generation);

        Biome main = (new Biome.BiomeBuilder()).precipitation(Biome.Precipitation.RAIN).biomeCategory(Biome.BiomeCategory.PLAINS).depth(0.1F).scale(0.1F).temperature(0.4F).downfall(0.4F).specialEffects((new BiomeSpecialEffects.Builder()).waterColor(4159204).waterFogColor(329011).fogColor(12638463).skyColor(0).ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).build()).mobSpawnSettings(new MobSpawnSettings.Builder().build()).generationSettings(main_generation.build()).build();
        event.getRegistry().register(main.setRegistryName(MODID, "main"));

        //Hills variant
        BiomeGenerationSettings.Builder hills_generation = (new BiomeGenerationSettings.Builder()).surfaceBuilder(SurfaceBuilders.END);
        BiomeDefaultFeatures.addDefaultCarvers(hills_generation);
        BiomeDefaultFeatures.addDefaultLakes(hills_generation);

        Biome hills = (new Biome.BiomeBuilder()).precipitation(Biome.Precipitation.RAIN).biomeCategory(Biome.BiomeCategory.PLAINS).depth(0.4F).scale(0.3F).temperature(0.8F).downfall(0.4F).specialEffects((new BiomeSpecialEffects.Builder()).waterColor(4159204).waterFogColor(329011).fogColor(12638463).skyColor(0).ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).build()).mobSpawnSettings(new MobSpawnSettings.Builder().build()).generationSettings(hills_generation.build()).build();
        event.getRegistry().register(hills.setRegistryName(MODID, "hills"));

        //Edge variant
        BiomeGenerationSettings.Builder edge_generation = (new BiomeGenerationSettings.Builder()).surfaceBuilder(SurfaceBuilders.END);
        BiomeDefaultFeatures.addDefaultCarvers(edge_generation);
        BiomeDefaultFeatures.addDefaultLakes(edge_generation);

        Biome edge = (new Biome.BiomeBuilder()).precipitation(Biome.Precipitation.RAIN).biomeCategory(Biome.BiomeCategory.PLAINS).depth(0F).scale(0F).temperature(0.8F).downfall(0F).specialEffects((new BiomeSpecialEffects.Builder()).waterColor(4159204).waterFogColor(329011).fogColor(12638463).skyColor(0).ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).build()).mobSpawnSettings(new MobSpawnSettings.Builder().build()).generationSettings(edge_generation.build()).build();
        event.getRegistry().register(edge.setRegistryName(MODID, "edge"));

        //River variant
        BiomeGenerationSettings.Builder river_generation = (new BiomeGenerationSettings.Builder()).surfaceBuilder(SurfaceBuilders.END);
        BiomeDefaultFeatures.addDefaultCarvers(river_generation);
        BiomeDefaultFeatures.addDefaultLakes(river_generation);
        BiomeDefaultFeatures.addDefaultSprings(river_generation);

        Biome river = (new Biome.BiomeBuilder()).precipitation(Biome.Precipitation.RAIN).biomeCategory(Biome.BiomeCategory.PLAINS).depth(-0.5F).scale(0F).temperature(0.8F).downfall(0F).specialEffects((new BiomeSpecialEffects.Builder()).waterColor(4159204).waterFogColor(329011).fogColor(12638463).skyColor(0).ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).build()).mobSpawnSettings(new MobSpawnSettings.Builder().build()).generationSettings(river_generation.build()).build();
        event.getRegistry().register(river.setRegistryName(MODID, "river"));


        //Shore variant
        BiomeGenerationSettings.Builder shore_generation = (new BiomeGenerationSettings.Builder()).surfaceBuilder(SurfaceBuilders.SOUL_SAND_VALLEY);
        BiomeDefaultFeatures.addDefaultCarvers(shore_generation);
        BiomeDefaultFeatures.addDefaultLakes(shore_generation);

        Biome shore = (new Biome.BiomeBuilder()).precipitation(Biome.Precipitation.RAIN).biomeCategory(Biome.BiomeCategory.PLAINS).depth(0f).scale(0F).temperature(0.5F).downfall(0F).specialEffects((new BiomeSpecialEffects.Builder()).waterColor(4159204).waterFogColor(329011).fogColor(12638463).skyColor(0).ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).build()).mobSpawnSettings(new MobSpawnSettings.Builder().build()).generationSettings(shore_generation.build()).build();
        event.getRegistry().register(shore.setRegistryName(MODID, "shore"));

    }

    private void commonSetup(FMLCommonSetupEvent event)
    {
        event.enqueueWork(this::addBiomesToGenerator);
    }

    private void addBiomesToGenerator()
    {
        //Add only the main biome entry
        BiomeManager.addBiome(BiomeManager.BiomeType.WARM, new BiomeManager.BiomeEntry(MAIN_KEY, 10));
        //Tell the BiomeManager all biomes that might spawn
        BiomeManager.addAdditionalOverworldBiomes(MAIN_KEY);
        BiomeManager.addAdditionalOverworldBiomes(HILLS_KEY);
        BiomeManager.addAdditionalOverworldBiomes(EDGE_KEY);
        BiomeManager.addAdditionalOverworldBiomes(RIVER_KEY);
        BiomeManager.addAdditionalOverworldBiomes(SHORE_KEY);

        //Register our biome layer modifier
        BiomeManager.addBiomeLayerModifier(new BiomeManager.IBiomeLayerModifier() {

            @Override
            public Optional<ResourceKey<Biome>> getHillsBiome(ResourceKey<Biome> biome)
            {
                return biome.equals(MAIN_KEY) ? Optional.of(HILLS_KEY) : Optional.empty();
            }

            @Override
            public Optional<ResourceKey<Biome>> getEdgeBiome(ResourceKey<Biome> biome, ResourceKey<Biome> north, ResourceKey<Biome> west, ResourceKey<Biome> south, ResourceKey<Biome> east)
            {
                if(biome.equals(MAIN_KEY))
                {
                    if(!north.equals(MAIN_KEY) || !west.equals(MAIN_KEY) || !south.equals(MAIN_KEY) || !east.equals(MAIN_KEY))
                    {
                        //If any of the adjacent biomes is not the core biome, we are at the biome edge
                        return Optional.of(EDGE_KEY);
                    }
                }
                return Optional.empty();
            }

            @Override
            public Optional<ResourceKey<Biome>> getRiverBiome(ResourceKey<Biome> biome)
            {
                return biome.equals(MAIN_KEY) ? Optional.of(RIVER_KEY) : Optional.empty();
            }

            @Override
            public Optional<ResourceKey<Biome>> getShoreBiome(ResourceKey<Biome> biome, ResourceKey<Biome> north, ResourceKey<Biome> west, ResourceKey<Biome> south, ResourceKey<Biome> east)
            {
                if(biome == MAIN_KEY && (BiomeDictionary.hasType(north, BiomeDictionary.Type.OCEAN) || BiomeDictionary.hasType(west, BiomeDictionary.Type.OCEAN) || BiomeDictionary.hasType(south, BiomeDictionary.Type.OCEAN) || BiomeDictionary.hasType(east, BiomeDictionary.Type.OCEAN)))
                {
                    //If any adjacent biome is of ocean type, we want a shore
                    return Optional.of(SHORE_KEY);
                }
                return Optional.empty();
            }
        });
    }
}
