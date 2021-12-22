package net.minecraftforge.debug.world;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;

import net.minecraft.commands.Commands;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistryAccess.RegistryHolder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.RegistryWriteOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate.Parameter;
import net.minecraft.world.level.biome.Climate.ParameterList;
import net.minecraft.world.level.biome.Climate.ParameterPoint;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource.Preset;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.synth.NormalNoise.NoiseParameters;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.world.MultiNoiseBiomeSourceLoadingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

@Mod(MultiNoiseBiomeSourceLoadingEventTest.MODID)
public class MultiNoiseBiomeSourceLoadingEventTest
{
    public static final String MODID = "multi_noise_biome_source_loading_event_test";
    public static final Logger LOGGER = LogManager.getLogger();
    
    public static final ResourceLocation PRESET_TEST = new ResourceLocation(MODID, "preset_test");
    public static final ResourceLocation NAMED_TEST = new ResourceLocation(MODID, "named_test");
    public static final ResourceLocation NAMELESS_TEST = new ResourceLocation(MODID, "nameless_test");
    
    public MultiNoiseBiomeSourceLoadingEventTest()
    {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;

        modBus.addListener(this::onGatherData);
        forgeBus.addListener(this::onLoadBiomeSource);
        forgeBus.addListener(this::onRegisterCommands);
    }
    
    private void onGatherData(GatherDataEvent event)
    {
        // need dynamic registries to do worldgen datagen
        RegistryHolder registries = RegistryAccess.builtin();
        RegistryWriteOps<JsonElement> writeOps = RegistryWriteOps.create(JsonOps.INSTANCE, registries);
        
        // the objects we reference must have been registered to the registryaccess
        // creating a registry access deep-copies most of the BuiltinRegistries objects,
        // so we must get vanilla objects from the registryaccess instead of getting static references
        Registry<DimensionType> dimensionTypes = registries.registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY);
        Registry<NoiseGeneratorSettings> noiseSettings = registries.registryOrThrow(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY);
        Registry<NoiseParameters> noiseParameters = registries.registryOrThrow(Registry.NOISE_REGISTRY);
        Registry<Biome> biomes = registries.registryOrThrow(Registry.BIOME_REGISTRY);
        
        DimensionType overworldDimensionType = dimensionTypes.get(DimensionType.OVERWORLD_LOCATION);
        NoiseGeneratorSettings overworldNoiseSettings = noiseSettings.get(NoiseGeneratorSettings.OVERWORLD);
        Biome desert = biomes.get(Biomes.DESERT);
        
        // make two three dimensions for testing
        // one with a preset biome source, one with a fully custom biome source and a dimension name, one with a fully custom source without name
        
        // preset biome source dimension
        BiomeSource presetSource = MultiNoiseBiomeSource.Preset.OVERWORLD.biomeSource(biomes);
        ChunkGenerator presetGenerator = new NoiseBasedChunkGenerator(noiseParameters, presetSource, 0, () -> overworldNoiseSettings);
        LevelStem presetDimension = new LevelStem(() -> overworldDimensionType, presetGenerator);
        
        // fully custom biome source dimension with forge:name field
        List<Pair<ParameterPoint, Supplier<Biome>>> parameterPoints = new ArrayList<>();
        parameterPoints.add(Pair.of(new ParameterPoint(
            Parameter.span(0.2F, 0.55F), // temperature
            Parameter.span(0.3F, 1.0F), // humidity
            Parameter.span(0.3F, 1.0F), // continentalness
            Parameter.span(-0.375F, -0.2225F), // erosion
            Parameter.point(1.0F), // depth
            Parameter.span(0.9333F, 1.0F), // weirdness
            0L // offset
            ), () -> desert));
        ParameterList<Supplier<Biome>> biomeParams = new ParameterList<>(parameterPoints);
        BiomeSource namedSource =  new MultiNoiseBiomeSource(biomeParams, Optional.empty(), Optional.of(NAMED_TEST));
        ChunkGenerator namedGenerator = new NoiseBasedChunkGenerator(noiseParameters, namedSource, 0, () -> overworldNoiseSettings);
        LevelStem namedDimension = new LevelStem(() -> overworldDimensionType, namedGenerator);
        
        // fully custom biome source dimension without forge:name field
        BiomeSource namelessSource =  new MultiNoiseBiomeSource(biomeParams, Optional.empty(), Optional.empty());
        ChunkGenerator namelessGenerator = new NoiseBasedChunkGenerator(noiseParameters, namelessSource, 0, () -> overworldNoiseSettings);
        LevelStem namelessDimension = new LevelStem(() -> overworldDimensionType, namelessGenerator);
        
        // now generate those dimensions
        DataGenerator generator = event.getGenerator();
        Path resourcesFolder = generator.getOutputFolder();
        Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();
        
        DataProvider provider = new DataProvider()
        {
            @Override
            public void run(HashCache cache) throws IOException
            {
                BiConsumer<ResourceLocation, LevelStem> dimensionWriter = (id, dimension)->
                {
                    JsonElement json = LevelStem.CODEC.encodeStart(writeOps, dimension).get().orThrow();
                    Path path = resourcesFolder.resolve(String.join("/", PackType.SERVER_DATA.getDirectory(), id.getNamespace(), "dimension", id.getPath()) + ".json");
                    try
                    {
                        DataProvider.save(gson, cache, json, path);
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                };
                dimensionWriter.accept(PRESET_TEST, presetDimension);
                dimensionWriter.accept(NAMED_TEST, namedDimension);
                dimensionWriter.accept(NAMELESS_TEST, namelessDimension);
            }

            @Override
            public String getName()
            {
                return MODID + " worldgen data provider";
            }
        };
        generator.addProvider(provider);
    }
    
    private void onLoadBiomeSource(MultiNoiseBiomeSourceLoadingEvent event)
    {
        // we want to test the following things:
        // * the dimensions that should have extra biomes added have extra biomes added
        // * the dimensions that did not have extra biomes added do not have extra biomes added
        // * extra biomes are not preserved in the biome source across multiple world creates/joins
        
        // expected behaviour is that overworld and overworld preset json dimension have an extra end biome,
        // nether has an extra jungle biome,
        // and the named test dimension has an extra nether biome
        if (Objects.equals(event.getName(), Preset.OVERWORLD.name))
        {
            Biome end = event.getRegistries().registryOrThrow(Registry.BIOME_REGISTRY).get(Biomes.SMALL_END_ISLANDS);
            var parameters = event.getParameters();
            LOGGER.info("Adding end islands biome to overworld biome source with {} biomes", parameters.size());
            parameters.add(Pair.of(new ParameterPoint(
                Parameter.span(-0.5F, 0.5F), // temperature
                Parameter.span(0F, 0.5F), // humidity
                Parameter.span(0.5F, 1F), // continentalness
                Parameter.span(0.5F, 1F), // erosion
                Parameter.span(0.5F, 1F), // depth
                Parameter.span(0.5F, 1F), // weirdness
                0L // offset
                ), () -> end));
        }
        else if (Objects.equals(event.getName(), Preset.NETHER.name))
        {
            Biome end = event.getRegistries().registryOrThrow(Registry.BIOME_REGISTRY).get(Biomes.JUNGLE);
            var parameters = event.getParameters();
            LOGGER.info("Adding jungle biome to nether biome source with {} biomes", parameters.size());
            parameters.add(Pair.of(new ParameterPoint(
                Parameter.point(0.5F), // temperature
                Parameter.point(0.5F), // humidity
                Parameter.point(0.5F), // continentalness
                Parameter.point(0.5F), // erosion
                Parameter.point(0.5F), // depth
                Parameter.point(0.5F), // weirdness
                0L // offset
                ), () -> end));
        }
        else if (Objects.equals(event.getName(), NAMED_TEST))
        {
            Biome end = event.getRegistries().registryOrThrow(Registry.BIOME_REGISTRY).get(Biomes.BASALT_DELTAS);
            var parameters = event.getParameters();
            LOGGER.info("Adding basalt deltas biome to test biome source with {} biomes", parameters.size());
            parameters.add(Pair.of(new ParameterPoint(
                Parameter.point(0.5F), // temperature
                Parameter.point(0.5F), // humidity
                Parameter.point(0.5F), // continentalness
                Parameter.point(0.5F), // erosion
                Parameter.point(0.5F), // depth
                Parameter.point(0.5F), // weirdness
                0L // offset
                ), () -> end));
        }
    }
    
    private void onRegisterCommands(RegisterCommandsEvent event)
    {
        event.getDispatcher().register(
            Commands.literal(MODID)
                .then(Commands.literal("biomes")
                    .executes(context ->{
                        var commandSource = context.getSource();
                        ServerLevel level = commandSource.getLevel();
                        BiomeSource biomeSource = level.getChunkSource().getGenerator().getBiomeSource();
                        if (biomeSource instanceof MultiNoiseBiomeSource multiSource)
                        {
                            var values = multiSource.parameters.values();
                            LOGGER.info(values);
                            commandSource.sendSuccess(new TextComponent("Your biome source has " + values.size() + " parameters"), false);
                        }
                        else
                        {
                            commandSource.sendFailure(new TextComponent("Not a multi-noise biome source"));
                        }
                        return 1;
                    })));
    }
}
