package net.minecraftforge.common.data;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;

import cpw.mods.modlauncher.api.LamdbaExceptionUtils;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

/**
 * <p>Dataprovider for using a Codec to generate jsons.
 * Path names for jsons are derived from the given registry folder and each entry's namespaced id, in the format:</p>
 * <pre>
 * {@code <assets/data>/entryid/registryfolder/entrypath.json }
 * </pre>
 * 
 * @param <T> the type of thing being generated.
 * @param dataGenerator DataGenerator provided by {@link GatherDataEvent}.
 * @param dynamicOps DynamicOps to encode values to jsons with using the provided Codec, e.g. {@link JsonOps.INSTANCE}.
 * @param packType PackType specifying whether to generate entries in assets or data.
 * @param registryFolder String representing the registry folder, e.g. "dimension" or "cheesemod/cheese".
 * @param codec Codec to encode values to jsons with using the provided DynamicOps.
 * @param entries Map of named entries to serialize to jsons. Paths for values are derived from the ResourceLocation's entryid:entrypath as specified above.
 */
public record JsonCodecProvider<T>(DataGenerator dataGenerator, String modid, DynamicOps<JsonElement> dynamicOps, PackType packType, String registryFolder, Codec<T> codec, Map<ResourceLocation, T> entries) implements DataProvider
{
    private static final Logger LOGGER = LogManager.getLogger();
    
    /**
     * {@return DatapackRegistryProvider that encodes using the registered loading codec for the provided registry key}
     * Ensures the correct directory and codec are used.
     * Only vanilla datapack registries enumerated in {@link RegistryAccess} and custom forge datapack registries can
     * be generated this way.
     * 
     * @param <T> Registry element type, e.g. Biome
     * @param dataGenerator DataGenerator provided by {@link GatherDataEvent}.
     * @param modid namespace of the mod adding this DataProvider, for logging purposes.
     * @param registryOps RegistryOps to encode values to json with.
     * @param registryKey ResourceKey identifying the registry and its directory.
     * @param entries Map of entries to encode and their ResourceKeys.
     */
    public static <T> JsonCodecProvider<T> forDatapackRegistry(DataGenerator dataGenerator, String modid, RegistryOps<JsonElement> registryOps, ResourceKey<Registry<T>> registryKey, Map<ResourceLocation, T> entries)
    {
        final ResourceLocation registryId = registryKey.location();
        // Minecraft datapack registry folders are in data/json-namespace/registry-name/
        // Non-vanilla registry folders are data/json-namespace/registry-namespace/registry-name/
        final String registryFolder = registryId.getNamespace().equals("minecraft")
            ? registryId.getPath()
            : registryId.getNamespace() + "/" + registryId.getPath();
        final Codec<T> codec = (Codec<T>)RegistryAccess.REGISTRIES.get(registryKey).codec();
        return new JsonCodecProvider<>(dataGenerator, modid, registryOps, PackType.SERVER_DATA, registryFolder, codec, entries);
    }

    @Override
    public void run(final CachedOutput cache) throws IOException
    {
        final Path outputFolder = this.dataGenerator().getOutputFolder();
        final String dataFolder = this.packType().getDirectory();
        for (final var entry : this.entries().entrySet())
        {
            final ResourceLocation id = entry.getKey();
            final T value = entry.getValue();
            final Path path = outputFolder.resolve(String.join("/", dataFolder, id.getNamespace(), this.registryFolder(), id.getPath()+".json"));
            this.codec().encodeStart(this.dynamicOps(), value)
                .resultOrPartial(msg -> LOGGER.error("Failed to encode {}: {}", path, msg)) // log error on encode failure
                .ifPresent(LamdbaExceptionUtils.rethrowConsumer(json -> DataProvider.saveStable(cache, json, path)));// output to file on encode success
        }
    }

    @Override
    public String getName()
    {
        return String.format("%s generator for %s", this.registryFolder(), this.modid());
    }
}