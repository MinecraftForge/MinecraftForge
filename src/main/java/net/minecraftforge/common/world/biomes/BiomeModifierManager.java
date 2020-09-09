package net.minecraftforge.common.world.biomes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldSettingsImport;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.world.biomes.conditions.base.IBiomeCondition;
import net.minecraftforge.common.world.biomes.modifiers.base.BiomeModifier;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = "forge")
public class BiomeModifierManager extends JsonReloadListener
{
    private static BiomeModifierManager INSTANCE = null;

    public static final Codec<Pair<Boolean, List<ResourceLocation>>> BIOME_MODIFIER_LIST =
            Codec.mapPair(
                    Codec.BOOL.optionalFieldOf("replace", false),
                    ResourceLocation.field_240908_a_.listOf().fieldOf("entries")
            ).codec();

    private static final Logger LOGGER = LogManager.getLogger("BiomeModifierManager");
    private static final Gson GSON = new GsonBuilder().create();
    private static final String FOLDER = "biome_modifiers";

    private List<BiomeModifier> deserializedModifiers = null;
    private Function<DynamicOps<JsonElement>, List<BiomeModifier>> modifiers = ops -> { throw new IllegalStateException(); };
    private List<IBiomeCondition> conditions = new ArrayList<>();

    private static boolean worldGenFinished = false;

    private BiomeModifierManager()
    {
        super(GSON, FOLDER);
    }

    private static boolean shouldReload()
    {
        return !worldGenFinished;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> resources, IResourceManager resourceManager, IProfiler profiler)
    {
        List<ResourceLocation> modifierLocs = new LinkedList<>();
        ResourceLocation modifierList = new ResourceLocation("forge:biome_modifiers/global_biome_modifiers.json");
        try
        {
            List<IResource> allModifiers = resourceManager.getAllResources(modifierList);
            Collections.reverse(allModifiers); //reverse, for the correct priority. last => most priority
            for(IResource modifier : allModifiers)
            {
                try (
                        InputStream inputstream = modifier.getInputStream();
                        Reader reader = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8));
                )
                {
                    JsonObject json = JSONUtils.fromJson(GSON, reader, JsonObject.class);
                    Pair<Boolean, List<ResourceLocation>> globalModifiers = BIOME_MODIFIER_LIST.parse(JsonOps.INSTANCE, json)
                            .getOrThrow(false, s -> {}); //the Exception will contain the message no need to log it twice.

                    if(globalModifiers.getFirst())
                        modifierLocs.clear();
                    List<ResourceLocation> toAdd = globalModifiers.getSecond().stream().filter(rl -> !modifierLocs.contains(rl)).collect(Collectors.toCollection(LinkedList::new));
                    Collections.reverse(toAdd); //reverse, for the correct priority. last => most priority
                    modifierLocs.addAll(toAdd);
                }
                catch (RuntimeException | IOException e)
                {
                    LOGGER.error("Couldn't read biome modifier list {} in data pack {}", modifierList, modifier.getPackName(), e);
                }
                finally
                {
                    IOUtils.closeQuietly(modifier);
                }
            }
        }
        catch (IOException e)
        {
            LOGGER.error("Couldn't read global loot modifier list from {}", modifierList, e);
        }

        this.modifiers = ops ->
        {
            List<BiomeModifier> modifiers = new ArrayList<>();
            modifierLocs.forEach(loc ->
                    BiomeModifier.GENERAL_CODEC.parse(ops, resources.get(loc)).get()
                            .ifLeft(modifiers::add)
                            .ifRight(e -> LOGGER.error("Could not parse {} : {}", loc, e)));
            return modifiers;
        };

        //Deserialize the conditions separately, so they can be used to check how many biomes
        // will get modified.
        this.conditions = new ArrayList<>();
        modifierLocs.forEach(loc ->
                IBiomeCondition.FIELD_CODEC.codec().parse(JsonOps.INSTANCE, resources.get(loc)).get()
                        .ifLeft(this.conditions::add)
                        .ifRight(e -> LOGGER.error("Could not parse {} : {}", loc, e)));
        LOGGER.info("Should load {} biome modifiers", modifierLocs.size());
    }

    /**
     * Have to deserialize with the WorldSettingsImport, so that resource locations get read properly.
     */
    private void properlyDeserializeModifiers(DynamicOps<JsonElement> ops)
    {
        if(this.deserializedModifiers == null)
        {
            deserializedModifiers = this.modifiers.apply(ops);
            LOGGER.info("Parsed {} biome modifiers", deserializedModifiers.size());
        }
    }

    private List<BiomeModifier> getDeserializedModifiers()
    {
        if(deserializedModifiers == null)
            throw new IllegalStateException();
        return deserializedModifiers;
    }

    private Biome getModifiedBiome(Biome base)
    {
        List<BiomeModifications> modifications = this.getDeserializedModifiers().stream()
                .map(bm -> bm.getModifications(base))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return BiomeModifications.performModifications(modifications, base);
    }

    private List<ResourceLocation> getBiomesToModify()
    {
        List<ResourceLocation> biomes = new ArrayList<>();
        Predicate<Biome> anyMatch = b -> BiomeModifierManager.INSTANCE.conditions.stream().anyMatch(cond -> cond.test(b));

        for(Map.Entry<RegistryKey<Biome>, Biome> entry : ForgeRegistries.BIOMES.getEntries())
        {
            if(anyMatch.test(entry.getValue()))
            {
                RegistryKey<Biome> key = entry.getKey();
                biomes.add(new ResourceLocation(key.func_240901_a_().getNamespace(), ForgeRegistries.Keys.BIOMES.func_240901_a_().getPath() + "/" + key.func_240901_a_().getPath() + ".json"));
            }
        }
        return biomes;
    }

    @SubscribeEvent
    public static void onResourceReload(AddReloadListenerEvent event)
    {
        if(BiomeModifierManager.shouldReload())
        {
            BiomeModifierManager.INSTANCE = new BiomeModifierManager();
            event.addListener(BiomeModifierManager.INSTANCE);
        }
    }

    @SubscribeEvent
    public static void serverStart(FMLServerStartedEvent event)
    {
        //server starts when the world setting has been decided, so no modifications are possible.
        BiomeModifierManager.worldGenFinished = true;
    }

    @SubscribeEvent
    public static void serverStop(FMLServerStoppedEvent event)
    {
        //for singleplayer, make sure it will be reloaded if another world is created.
        BiomeModifierManager.worldGenFinished = false;
    }

    public static class ModificationAccess implements WorldSettingsImport.IResourceAccess
    {
        private final WorldSettingsImport.IResourceAccess dataObjectLoader;

        public ModificationAccess(IResourceManager manager)
        {
            this.dataObjectLoader = WorldSettingsImport.IResourceAccess.func_244345_a(manager);
        }

        @Override
        public Collection<ResourceLocation> func_241880_a(RegistryKey<? extends Registry<?>> key)
        {
            Collection<ResourceLocation> baseLocs = dataObjectLoader.func_241880_a(key);
            if(!key.equals(Registry.field_239720_u_))
                return baseLocs;

            baseLocs.addAll(BiomeModifierManager.INSTANCE.getBiomesToModify());
            return baseLocs;
        }

        @Override
        public <E> DataResult<Pair<E, OptionalInt>> func_241879_a(DynamicOps<JsonElement> ops, RegistryKey<? extends Registry<E>> regKey, RegistryKey<E> key, Decoder<E> decoder)
        {
            DataResult<Pair<E, OptionalInt>> dataObj = dataObjectLoader.func_241879_a(ops, regKey, key, decoder);
            //only handle biomes.
            if(!regKey.equals(Registry.field_239720_u_))
                return dataObj;

            BiomeModifierManager.INSTANCE.properlyDeserializeModifiers(ops); //ops here will be a WorldSettingsImport
            //First check that the data object did not error, then it's a data defined biome
            // This is to ensure that data overrides of registered biomes are modified relative to the override
            // instead of the vanilla object.
            if(!dataObj.error().isPresent())
            {
                dataObj.map(p -> p.mapFirst(e -> (Biome) e).mapFirst(BiomeModifierManager.INSTANCE::getModifiedBiome));
                return dataObj;
            }

            //Find the biome in the registry.
            ForgeRegistry<Biome> reg = (ForgeRegistry<Biome>) ForgeRegistries.BIOMES;
            ResourceLocation loc = key.func_240901_a_();

            //If it's not there, the previous error was a real one, so return it.
            if(!reg.containsKey(loc))
                return dataObj;

            Biome modified = BiomeModifierManager.INSTANCE.getModifiedBiome(reg.getValue(loc));
            return DataResult.success(Pair.of((E)modified, OptionalInt.of(reg.getID(loc))), Lifecycle.experimental());
        }
    }
}
