/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.world;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.data.worldgen.features.NetherFeatures;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biome.Precipitation;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountOnEveryLayerPlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.BiomeModifierSerializer;
import net.minecraftforge.common.world.ModifiableBiomeInfo.BiomeInfo.Builder;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * <p>This tests the following features and requirements of biome modifier jsons::</p>
 * <ul>
 * <li>A biome modifier json is created via datagen.</li>
 * <li>The biome modifier modifies all four modifiable fields in biomes, to ensure patches and coremods apply correctly (generation, spawns, climate, and client effects).</li>
 * <li>The biome modifier uses a biome tag to determine which biomes to modify.</li>
 * <li>The biome modifier adds json feature to modified biomes, to ensure json features are usable in biome modifiers.</li>
 * </ul>
 * <p>If the biome modifier is applied correctly, then badlands biomes should generate large basalt columns,
 * spawn magma cubes, have red-colored water, and be snowy.</p>
 */
@Mod(BiomeModifierTest.MODID)
public class BiomeModifierTest
{
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "biome_modifiers_test";
    public static final boolean ENABLED = true;
    public static final String BASALT_PILLARS = "large_basalt_columns";
    public static final ResourceLocation BASALT_PILLARS_RL = new ResourceLocation(MODID, BASALT_PILLARS);
    public static final String TEST = "test";
    public static final ResourceLocation ADD_FEATURES_TO_BIOMES_RL = new ResourceLocation(MODID, TEST);
    public static final String MODIFY_BADLANDS = "modify_badlands";
    
    public BiomeModifierTest()
    {
        if (!ENABLED)
            return;
        
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        // Serializer types can be registered via deferred register.
        final DeferredRegister<BiomeModifierSerializer<?>> serializers = DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, MODID);
        serializers.register(modBus);
        serializers.register(TEST, () -> new BiomeModifierSerializer<>(TestModifier.makeCodec()));
        
        // Biome modifiers don't need to be registered when defined in json, but they do need to be registered if we are to datagenerate the jsons.
        // We'll also datagenerate a placedfeature json (using our own placedfeature avoids feature cycle problems when we add it to biomes).
        final DeferredRegister<PlacedFeature> placedFeatures = DeferredRegister.create(Registry.PLACED_FEATURE_REGISTRY, MODID);
        placedFeatures.register(modBus);
        placedFeatures.register(BASALT_PILLARS,
            () -> new PlacedFeature((Holder<ConfiguredFeature<?,?>>) (Holder<? extends ConfiguredFeature<?,?>>)NetherFeatures.LARGE_BASALT_COLUMNS,
                List.of(CountOnEveryLayerPlacement.of(1), BiomeFilter.biome())));
        
        modBus.addListener(this::onGatherData);
    }
    
    private void onGatherData(GatherDataEvent event)
    {
        if (!event.includeServer())
            return;
        
        // Example of how to datagen datapack registry objects.
        // Datapack registry objects referred to by other datapack registry objects must be registered first.
        DataGenerator generator = event.getGenerator();
        final Path outputFolder = generator.getOutputFolder();
        final RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, RegistryAccess.BUILTIN.get());
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final String directory = PackType.SERVER_DATA.getDirectory();
        Registry<PlacedFeature> placedFeatures = ops.registry(Registry.PLACED_FEATURE_REGISTRY).get();
        
        // prepare to datagenerate our placed feature
        final String featurePathString = String.join("/", directory, MODID, Registry.PLACED_FEATURE_REGISTRY.location().getPath(), BASALT_PILLARS + ".json");
        final Path featurePath = outputFolder.resolve(featurePathString);
        final PlacedFeature feature = placedFeatures.get(BASALT_PILLARS_RL);
        
        // prepare to datagenerate our biome modifier
        final ResourceLocation biomeModifiersRegistryID = ForgeRegistries.Keys.BIOME_MODIFIERS.location();
        final String biomeModifierPathString = String.join("/", directory, MODID, biomeModifiersRegistryID.getNamespace(), biomeModifiersRegistryID.getPath(), MODIFY_BADLANDS + ".json");
        final Path biomeModifierPath = outputFolder.resolve(biomeModifierPathString);
        final BiomeModifier biomeModifier = new TestModifier(
            new HolderSet.Named<>(ops.registry(Registry.BIOME_REGISTRY).get(), BiomeTags.IS_BADLANDS),
            Decoration.TOP_LAYER_MODIFICATION,
            HolderSet.direct(placedFeatures.getOrCreateHolder(ResourceKey.create(Registry.PLACED_FEATURE_REGISTRY, BASALT_PILLARS_RL))),
            new SpawnerData(EntityType.MAGMA_CUBE, 100, 1, 4),
            Precipitation.SNOW,
            0xFF0000
            );
        
        generator.addProvider(new DataProvider()
        {
            @Override
            public void run(final HashCache cache) throws IOException
            {
                PlacedFeature.DIRECT_CODEC.encodeStart(ops, feature)
                    .resultOrPartial(msg -> LOGGER.error("Failed to encode {}: {}", featurePathString, msg)) // Log error on encode failure.
                    .ifPresent(json -> // Output to file on encode success.
                    {
                        try
                        {
                            DataProvider.save(gson, cache, json, featurePath);
                        }
                        catch (IOException e) // The throws can't deal with this exception, because we're inside the ifPresent.
                        {
                            LOGGER.error("Failed to save " + featurePathString, e);
                        }
                    });
                
                BiomeModifier.DIRECT_CODEC.encodeStart(ops, biomeModifier)
                    .resultOrPartial(msg -> LOGGER.error("Failed to encode {}: {}", biomeModifierPathString, msg)) // Log error on encode failure.
                    .ifPresent(json -> // Output to file on encode success.
                    {
                        try
                        {
                            DataProvider.save(gson, cache, json, biomeModifierPath);
                        }
                        catch (IOException e) // The throws can't deal with this exception, because we're inside the ifPresent.
                        {
                            LOGGER.error("Failed to save " + biomeModifierPathString, e);
                        }
                    });
            }

            @Override
            public String getName()
            {
                return MODID + " data provider";
            }
        });
    }
    
    public static class TestModifier extends BiomeModifier
    {
        private static final RegistryObject<BiomeModifierSerializer<?>> SERIALIZER = RegistryObject.create(ADD_FEATURES_TO_BIOMES_RL, ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, MODID);

        private final HolderSet<Biome> biomes;
        private final Decoration generationStage;
        private final HolderSet<PlacedFeature> features;
        private final SpawnerData spawn;
        private final Precipitation precipitation;
        private final int waterColor;
        
        public TestModifier(HolderSet<Biome> biomes, Decoration generationStage, HolderSet<PlacedFeature> features, SpawnerData spawn, Precipitation precipitation, int waterColor)
        {
            this.biomes = biomes;
            this.generationStage = generationStage;
            this.features = features;
            this.spawn = spawn;
            this.precipitation = precipitation;
            this.waterColor = waterColor;
        }
        
        public HolderSet<Biome> biomes()
        {
            return this.biomes;
        }
        
        public Decoration generationStage()
        {
            return this.generationStage;
        }
        
        public HolderSet<PlacedFeature> features()
        {
            return this.features;
        }
        
        public SpawnerData spawn()
        {
            return this.spawn;
        }
        
        public Precipitation precipitation()
        {
            return this.precipitation;
        }
        
        public int waterColor()
        {
            return this.waterColor;
        }
        
        @Override
        public void modify(Holder<Biome> biome, Phase phase, Builder builder)
        {
            if (phase == Phase.ADD && this.biomes.contains(biome))
            {
                BiomeGenerationSettingsBuilder generation = builder.getGenerationSettings();
                this.features.forEach(holder -> generation.addFeature(this.generationStage, holder));
                builder.getMobSpawnSettings().addSpawn(this.spawn.type.getCategory(), this.spawn);
            }
            else if (phase == Phase.MODIFY && this.biomes.contains(biome))
            {
                builder.getClimateSettings().setPrecipitation(this.precipitation);
                builder.getEffects().waterColor(this.waterColor);
                if (this.precipitation == Precipitation.SNOW)
                    builder.getClimateSettings().setTemperature(0F);
            }
        }

        @Override
        public BiomeModifierSerializer<?> type()
        {
            return SERIALIZER.get();
        }
        
        private static Codec<TestModifier> makeCodec()
        {
            return RecordCodecBuilder.create(builder -> builder.group(
                    Biome.LIST_CODEC.fieldOf("biomes").forGetter(TestModifier::biomes),
                    Codec.STRING.comapFlatMap(TestModifier::generationStageFromString, Decoration::toString).fieldOf("generation_stage").forGetter(TestModifier::generationStage),
                    PlacedFeature.LIST_CODEC.fieldOf("features").forGetter(TestModifier::features),
                    SpawnerData.CODEC.fieldOf("spawn").forGetter(TestModifier::spawn),
                    Precipitation.CODEC.fieldOf("precipitation").forGetter(TestModifier::precipitation),
                    Codec.INT.fieldOf("water_color").forGetter(TestModifier::waterColor)
                ).apply(builder, TestModifier::new));
        }
        
        private static DataResult<Decoration> generationStageFromString(String name)
        {
            try
            {
                return DataResult.success(Decoration.valueOf(name));
            }
            catch(Exception e)
            {
                return DataResult.error("Not a decoration stage: " + name);
            }
        }
    }
}
