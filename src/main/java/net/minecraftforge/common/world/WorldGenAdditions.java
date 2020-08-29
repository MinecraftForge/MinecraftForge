package net.minecraftforge.common.world;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.resources.IResource;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.SimpleReloadableResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.codec.DatapackCodec;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.util.registry.WorldSettingsImport;
import net.minecraft.world.Dimension;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

public class WorldGenAdditions
{
    private static final Logger LOGGER = LogManager.getLogger();
    public static final Codec<List<String>> TARGETS = Codec.STRING.listOf().fieldOf("targets").codec();

    public static Codec<DimensionGeneratorSettings> delegateWorldDecoding(Codec<DimensionGeneratorSettings> base)
    {
        return new Codec<DimensionGeneratorSettings>()
        {
            @Override
            public <T> DataResult<Pair<DimensionGeneratorSettings, T>> decode(DynamicOps<T> ops, T input)
            {
                DataResult<Pair<DimensionGeneratorSettings, T>> baseResult = base.decode(ops, input);
                if(ops instanceof WorldSettingsImport && input instanceof JsonElement)
                {
                    if(baseResult.error().isPresent())
                        return baseResult;
                    return baseResult.map(p -> p.mapFirst(base -> handleWorldGeneration(base, (WorldSettingsImport<JsonElement>) ops)));
                }
                return baseResult;
            }

            @Override
            public <T> DataResult<T> encode(DimensionGeneratorSettings input, DynamicOps<T> ops, T prefix)
            {
                return base.encode(input, ops, prefix);
            }
        };
    }

    public static DimensionGeneratorSettings handleWorldGeneration(DimensionGeneratorSettings base, WorldSettingsImport<JsonElement> parser)
    {
        return DimensionGeneratorSettings.basedOn(base, parser)
                .parse(parser, new JsonObject())
                .setPartial(base)
                .resultOrPartial(LOGGER::error)
                .orElseThrow(RuntimeException::new);
    }

    public static <E> MapCodec<SimpleRegistry<E>> handleRegistry(SimpleRegistry<E> base, WorldSettingsImport<JsonElement> parser)
    {
        SimpleRegistry<E> ret = new SimpleRegistry<>(base.func_243578_f(), Lifecycle.experimental());
        for (Map.Entry<RegistryKey<E>, E> entry : base.func_239659_c_())
        {
            E parsed = handleAdditions(entry.getValue(), entry.getKey(), parser);
            ret.register(base.getId(entry.getValue()), entry.getKey(), parsed, base.func_241876_d(entry.getValue()));
        }
        return MapCodec.unit(ret);
    }

    public static <E> E handleAdditions(E value, RegistryKey<E> key, WorldSettingsImport<JsonElement> parser)
    {
        if(value instanceof Biome)
            return (E) Biome.basedOn((Biome) value, (RegistryKey<Biome>) key, parser)
                    .parse(parser, new JsonObject()) //Dummy object, no actual parsing is done.
                    .setPartial((Biome)value) //Fall back on the base value.
                    .resultOrPartial(LOGGER::warn) //Inform of errors if they occured.
                    .orElseThrow(RuntimeException::new); //Impossible

        if(value instanceof Dimension)
            return (E) Dimension.basedOn((Dimension) value, (RegistryKey<Dimension>) key, parser)
                    .parse(parser, new JsonObject()) //Dummy object, no actual parsing is done.
                    .setPartial((Dimension) value) //Fall back on the base value.
                    .resultOrPartial(LOGGER::warn) //Inform of errors if they occured.
                    .orElseThrow(RuntimeException::new); //Impossible

        return value;
    }

    public static MapCodec<List<Pair<Biome.Attributes, Supplier<Biome>>>> COMPLEX_BIOMES = RecordCodecBuilder.<Pair<Biome.Attributes, Supplier<Biome>>>create((inst) ->
            inst.group(
                    Biome.Attributes.field_235104_a_.fieldOf("parameters").forGetter(Pair::getFirst),
                    Biome.field_235051_b_.fieldOf("biome").forGetter(Pair::getSecond)
            ).apply(inst, Pair::of)
    ).listOf().fieldOf("noise_biomes");

    public static MapCodec<List<Pair<Biome.Attributes, Supplier<Biome>>>> complexBiomeAdditions(List<Pair<Biome.Attributes, Supplier<Biome>>> base, RegistryKey<Dimension> key, WorldSettingsImport<JsonElement> parser)
    {
        resetCachingState();
        return parseAddition(base, COMPLEX_BIOMES,
                List::addAll, ArrayList::new, ImmutableList::copyOf, parser,
                "additions/dimensions/noise_biomes", key.func_240901_a_().toString()
        );
    }

    public static final MapCodec<Map<EntityType<?>, MobSpawnInfo.SpawnCosts>> SPAWN_COSTS_CODEC =
            Codec.simpleMap(Registry.ENTITY_TYPE, MobSpawnInfo.SpawnCosts.field_242579_a, Registry.ENTITY_TYPE).codec()
                    .optionalFieldOf("spawn_costs", ImmutableMap.of());

    public static MapCodec<Map<EntityType<?>, MobSpawnInfo.SpawnCosts>> spawnCostsAdditions(Map<EntityType<?>, MobSpawnInfo.SpawnCosts> base, RegistryKey<Biome> key, Biome.Category category, WorldSettingsImport<JsonElement> parser)
    {
        updateCacheFor("spawns");
        weightMap.clear();
        return parseAddition(base, SPAWN_COSTS_CODEC,
                WorldGenAdditions::mergeCosts, HashMap::new, ImmutableMap::copyOf,
                parser,
                "additions/biomes/mob_spawns",
                key.func_240901_a_().toString(), category.getName()
        );
    }

    private static final Object2IntOpenHashMap<EntityType<?>> weightMap = new Object2IntOpenHashMap<>();

    /**
     * Merging is done by averaging out the spawns costs if multiple are present for the same key (EntityType)
     * Since the number of the same costs is unknown, the weightmap holds the last known "weight" or defaults to 2.
     * The "incoming" costs will have a weight of  :    (weight - 1) / weight
     * and the cost to merge will have a weight of :    1 / weight,
     * for a total weight of 1.
     */
    public static void mergeCosts(Map<EntityType<?>, MobSpawnInfo.SpawnCosts> ret, Map<EntityType<?>, MobSpawnInfo.SpawnCosts> merging)
    {
        BiFunction<EntityType<?>, MobSpawnInfo.SpawnCosts, MobSpawnInfo.SpawnCosts> mergeSingle = (key, ogVal) ->
        {
            double f = weightMap.getOrDefault(key, 2);
            double ogFactor = (f-1)/f;
            double toMergeFactor = 1/f;

            MobSpawnInfo.SpawnCosts toMerge = merging.get(key);
            double energyBudget = (ogVal.func_242582_a() * ogFactor) + (toMerge.func_242585_b() * toMergeFactor);
            double charge = (ogVal.func_242585_b() * ogFactor) + (toMerge.func_242585_b() * toMergeFactor) ;

            weightMap.put(key,(int)f+1);
            return new MobSpawnInfo.SpawnCosts(energyBudget, charge);
        };

        merging.forEach((entityType, spawnCosts) ->
        {
            if(ret.computeIfPresent(entityType, mergeSingle) == null)
                ret.put(entityType, spawnCosts);
        });
    }

    public static final MapCodec<Map<EntityClassification, List<MobSpawnInfo.Spawners>>> SPAWNERS_CODEC =
            Codec.simpleMap(
                    EntityClassification.field_233667_g_,
                    MobSpawnInfo.Spawners.field_242587_b.listOf().promotePartial(Util.func_240982_a_("Spawn data: ", LOGGER::error)),
                    IStringSerializable.func_233025_a_(EntityClassification.values())
            ).codec().optionalFieldOf("spawners", ImmutableMap.of());


    public static MapCodec<Map<EntityClassification, List<MobSpawnInfo.Spawners>>> spawnerAdditions(Map<EntityClassification, List<MobSpawnInfo.Spawners>> base, RegistryKey<Biome> key, Biome.Category category, WorldSettingsImport<JsonElement> parser)
    {
        updateCacheFor("spawns");
        return parseAddition(base, SPAWNERS_CODEC,
                (ret, toAdd) ->
                {
                    for(EntityClassification cls : toAdd.keySet())
                        ret.computeIfAbsent(cls, k -> new ArrayList<>()).addAll(toAdd.get(cls));
                },
                imm -> new HashMap<>(imm.entrySet().stream().map(e -> Pair.of(e.getKey(), new ArrayList<>(e.getValue()))).collect(Pair.toMap())), //toMutable
                mute -> ImmutableMap.copyOf(mute.entrySet().stream().map(e -> Pair.of(e.getKey(), ImmutableList.copyOf(e.getValue()))).collect(Pair.toMap())), //toImmutable
                parser,
                "additions/biomes/mob_spawns",
                key.func_240901_a_().toString(), category.getName()
        );
    }

    public static final MapCodec<List<Supplier<StructureFeature<?, ?>>>> STRUCTURES_CODEC =
            StructureFeature.field_242770_c.promotePartial(Util.func_240982_a_("Structure start: ", LOGGER::error))
                    .optionalFieldOf("starts", ImmutableList.of());


    public static MapCodec<List<Supplier<StructureFeature<?, ?>>>> structureAdditions(List<Supplier<StructureFeature<?, ?>>> base, RegistryKey<Biome> key, Biome.Category category, WorldSettingsImport<JsonElement> parser)
    {
        updateCacheFor("features");
        return parseAddition(base , STRUCTURES_CODEC,
                List::addAll, ArrayList::new, ImmutableList::copyOf, parser,
                "additions/biomes/features",
                key.func_240901_a_().toString(), category.getName()
        );
    }

    /**
     * Feature lists are based on {@link GenerationStage.Decoration}, the object itself is a list of lists,
     * the position of the list is very important as it corresponds to the ordinal of the Decoration associated with the features.
     */
    public static final MapCodec<List<List<Supplier<ConfiguredFeature<?, ?>>>>> FEATURES_CODEC =
            ConfiguredFeature.field_242764_c.promotePartial(Util.func_240982_a_("Feature: ", LOGGER::error)).listOf()
                    .optionalFieldOf("features", ImmutableList.of());

    public static MapCodec<List<List<Supplier<ConfiguredFeature<?, ?>>>>> featureAdditions(List<List<Supplier<ConfiguredFeature<?, ?>>>> base , RegistryKey<Biome> key, Biome.Category category, WorldSettingsImport<JsonElement> parser)
    {
        updateCacheFor("features");
        return parseAddition(base, FEATURES_CODEC,
                (ret, toAdd) -> //merger.
                {
                    int size = ret.size();
                    int sizeNeeded = toAdd.size();
                    if(sizeNeeded > size) //Fill any missing lists
                        for(int i = size; i < sizeNeeded; i++)
                            ret.add(new ArrayList<>());

                    for(int i = 0; i < toAdd.size(); i++)
                        ret.get(i).addAll(toAdd.get(i));
                },
                imm -> new ArrayList<>(imm).stream().map(ArrayList::new).collect(Collectors.toList()), //toMutable
                mute -> ImmutableList.copyOf(mute.stream().map(ImmutableList::copyOf).collect(Collectors.toList())), //toImmutable
                parser,
                "additions/biomes/features",
                category.getName(), key.func_240901_a_().toString()
        );
    }

    /**
     * @param base          The base object
     * @param decoder       How to decode additions of this object
     * @param merger        How to merge the original object and the results.
     *                      The first argument is the original object (mutable), the second is the addition (immutable).
     * @param toMutable     How to make the object to change mutable (and any object it contains)
     * @param toImmutable   How to make the resulting object immutable again (and any object it contains)
     * @param parser        How to decode the additions
     * @param folder        The folder where these additions are stored
     * @param validTargets  One of these string need to match in the "target" array for the addition to apply.
     *                      If none are included, there will be no filter and all will match
     * @param <E>           The type of this object.
     * @return              A MapCodec of this object.
     */
    public static <E> MapCodec<E> parseAddition(E base, MapCodec<E> decoder, BiConsumer<E, E> merger,
                                                UnaryOperator<E> toMutable, UnaryOperator<E> toImmutable,
                                                WorldSettingsImport<JsonElement> parser,
                                                String folder, String... validTargets)
    {
        E mutable = toMutable.apply(base);
        List<DataResult<E>> results = new ArrayList<>();
        List<String> targets = Lists.newArrayList(validTargets);

        if(cachingState == Caching.USE)
        {
            getCurrentCache().forEach(j ->
            {
                if(targets.isEmpty() || TARGETS.parse(parser, j).result().orElse(Collections.EMPTY_LIST).stream().anyMatch(targets::contains))
                    results.add(decoder.decoder().parse(parser, j));
            });
        }
        else
        {
            try (
                    ResourcePackList packs = new ResourcePackList();
                    SimpleReloadableResourceManager manager = new SimpleReloadableResourceManager(ResourcePackType.SERVER_DATA)
            )
            {
                //The DatapackCodec will get filled with the mod data packs, the "vanilla" datapack is useless here.
                // The return value does not matter, only using to fill the ResourcePackList
                MinecraftServer.func_240772_a_(packs, new DatapackCodec(ImmutableList.of(), ImmutableList.of()), false);
                packs.func_232623_f_().forEach(manager::addResourcePack);

                for (ResourceLocation location : manager.getAllResourceLocations(folder, s -> s.endsWith(".json")))
                {
                    try (
                            IResource resource = manager.getResource(location);
                            InputStream inputstream = resource.getInputStream();
                            Reader reader = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8));
                    )
                    {
                        JsonParser jsonParser = new JsonParser();
                        JsonElement json = jsonParser.parse(reader);
                        if(cachingState == Caching.STORE)
                            getCurrentCache().add(json);

                        if(targets.isEmpty() || TARGETS.parse(parser, json).result().orElse(Collections.EMPTY_LIST).stream().anyMatch(targets::contains))
                            results.add(decoder.decoder().parse(parser, json));
                    }
                    catch (RuntimeException | IOException e)
                    {
                        results.add(DataResult.error("Could not parse " + location + " : " + e));
                    }
                }
            }
        }

        if(results.isEmpty())
            return MapCodec.unit(base);

        results.forEach(dt -> dt.get().ifLeft(e -> merger.accept(mutable, e)).ifRight(pr -> LOGGER.warn(pr.message())));

        return MapCodec.unit(toImmutable.apply(mutable));
    }

    private static final Map<String, List<JsonElement>> caches = new HashMap<>();
    private static String current = "";
    private static Caching cachingState = Caching.NONE;

    private static void resetCachingState()
    {
        cachingState = Caching.NONE;
        current = "";
    }

    private static void updateCacheFor(String name)
    {
        if(caches.get(name) == null)
        {
            caches.put(name, new ArrayList<>());
            cachingState = Caching.STORE;
        }
        else
        {
            cachingState = Caching.USE;
        }
        current = name;
    }

    private static List<JsonElement> getCurrentCache()
    {
        return caches.get(current);
    }

    enum Caching
    {
        NONE,
        STORE,
        USE;
    }
}