package net.minecraftforge.registries.injection;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldSettingsImport;
import net.minecraftforge.registries.injection.impl.SeparationSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ForgeResourceAccess implements WorldSettingsImport.IResourceAccess
{
    private static final JsonParser JSON_PARSER = new JsonParser();
    public static final Logger LOGGER = LogManager.getLogger("ForgeResourceAccess");

    private final IResourceManager resourceManager;
    private final WorldSettingsImport.IResourceAccess vanillaDelegate;
    private final Map<RegistryKey<? extends Registry<?>>, RegistryEntryModifier<?>> modifiers;

    public ForgeResourceAccess(IResourceManager resourceManager,
                               WorldSettingsImport.IResourceAccess vanillaDelegate,
                               Map<RegistryKey<? extends Registry<?>>, RegistryEntryModifier<?>> modifiers)
    {
        this.resourceManager = resourceManager;
        this.vanillaDelegate = vanillaDelegate;
        this.modifiers = modifiers;
    }

    @Override
    public Collection<ResourceLocation> getRegistryObjects(RegistryKey<? extends Registry<?>> registryKey)
    {
        return vanillaDelegate.getRegistryObjects(registryKey);
    }

    @Override
    public <E> DataResult<Pair<E, OptionalInt>> decode(DynamicOps<JsonElement> ops, RegistryKey<? extends Registry<E>> registryKey, RegistryKey<E> entryKey, Decoder<E> decoder)
    {
        final RegistryEntryModifier<?> modifier = modifiers.get(registryKey);

        // Delegate to vanilla if we have no modifiers for this registry entry type.
        if (modifier == null || modifier.isEmpty()) return vanillaDelegate.decode(ops, registryKey, entryKey, decoder);

        final ResourceLocation resourceFile = toFileLocation(registryKey, entryKey.getLocation());

        try
        {
            JsonObject entryDataResult = load(entryKey, resourceFile, modifier);

            if (modifier.hasInjections())
            {
                LOGGER.debug("Injecting registry entry data into {}", entryKey);
                modifier.injectRaw(entryKey, entryDataResult);
            }

            entryDataResult.addProperty("forge:registry_name", entryKey.getLocation().toString());

            return decoder.parse(ops, entryDataResult).map(entry -> Pair.of(entry, OptionalInt.empty()));
        } catch (IOException e) {
            // Same as anonymous implementation in WorldSettingsImport.IResourceAccess.
            return DataResult.error("Failed to parse " + resourceFile + " file: " + e.getMessage());
        }
    }

    @Override
    public String toString()
    {
        return "ForgeResourceAccess[" + resourceManager + "]";
    }

    private JsonObject load(RegistryKey<?> entryKey, ResourceLocation file, RegistryEntryModifier<?> modifier) throws IOException
    {
        JsonElement entryDataResult = null;

        if (modifier.hasMergers())
        {
            // Note: The resource manager provides resources in order of priority, low to high.
            final List<IResource> resources = resourceManager.getAllResources(file);

            if (resources.size() > 1)
            {
                LOGGER.debug("Merging datapack registry entries for {}", entryKey);
            }

            // The merge operation only inserts missing data so iterate in reverse order to make sure
            // that the highest priority resource is inserted first.
            for (int i = resources.size() - 1; i >= 0; i--)
            {
                try (IResource resource = resources.get(i); Reader reader = newReader(resource))
                {
                    JsonElement entryDataCandidate = JSON_PARSER.parse(reader);
                    if (entryDataResult == null)
                    {
                        // Note: We don't need to merge the first candidate with itself!
                        entryDataResult = entryDataCandidate;
                        continue;
                    }
                    // Merge copies data from the candidate into the result.
                    modifier.merge(entryDataResult, entryDataCandidate);
                }
            }
        }
        else
        {
            try (IResource resource = resourceManager.getResource(file); Reader reader = newReader(resource))
            {
                entryDataResult = JSON_PARSER.parse(reader);
            }
        }

        // Assert that what we've parsed/merged is valid.
        if (entryDataResult == null) throw newParseError(file, new NullPointerException("entryDataResult is null!"));
        if (!entryDataResult.isJsonObject()) throw newParseError(file, new IllegalStateException("entryDataResult is not a JsonObject: " + entryDataResult.getClass()));

        return entryDataResult.getAsJsonObject();
    }

    private static Reader newReader(IResource resource)
    {
        return new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
    }

    private static ResourceLocation toFileLocation(RegistryKey<?> registryKey, ResourceLocation entryName)
    {
        String filepath = registryKey.getLocation().getPath() + "/" + entryName.getPath() + ".json";
        return new ResourceLocation(entryName.getNamespace(), filepath);
    }

    private static IOException newParseError(ResourceLocation file, Throwable cause)
    {
        return new IOException("Failed to parse json file: " + file, cause);
    }

    public static ForgeResourceAccess wrap(IResourceManager resourceManager, WorldSettingsImport.IResourceAccess vanilla)
    {
        ImmutableMap.Builder<RegistryKey<? extends Registry<?>>, RegistryEntryModifier<?>> builder = ImmutableMap.builder();

        builder.put(Registry.NOISE_SETTINGS_KEY, RegistryEntryModifier.builder(Registry.NOISE_SETTINGS_KEY)
                .add(new SeparationSettings.InjectorImpl(name -> true))
                .add(new SeparationSettings.MergerImpl())
                .build());

        return new ForgeResourceAccess(resourceManager, vanilla, builder.build());
    }
}
