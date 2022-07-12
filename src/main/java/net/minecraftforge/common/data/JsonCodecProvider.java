/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.data;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;

import java.util.function.BiConsumer;
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
import net.minecraftforge.common.data.ExistingFileHelper.ResourceType;
import org.slf4j.Logger;

/**
 * <p>Dataprovider for using a Codec to generate jsons.
 * Path names for jsons are derived from the given registry folder and each entry's namespaced id, in the format:</p>
 * <pre>
 * {@code <assets/data>/entryid/registryfolder/entrypath.json }
 * </pre>
 *
 * @param <T> the type of thing being generated.
 */
public class JsonCodecProvider<T> implements DataProvider
{
    private static final Logger LOGGER = LogUtils.getLogger();
    protected final DataGenerator dataGenerator;
    protected final ExistingFileHelper existingFileHelper;
    protected final String modid;
    protected final DynamicOps<JsonElement> dynamicOps;
    protected final PackType packType;
    protected final String directory;
    protected final Codec<T> codec;
    protected final Map<ResourceLocation, T> entries;

    /**
     * @param dataGenerator DataGenerator provided by {@link GatherDataEvent}.
     * @param dynamicOps DynamicOps to encode values to jsons with using the provided Codec, e.g. {@link JsonOps.INSTANCE}.
     * @param packType PackType specifying whether to generate entries in assets or data.
     * @param directory String representing the directory to generate jsons in, e.g. "dimension" or "cheesemod/cheese".
     * @param codec Codec to encode values to jsons with using the provided DynamicOps.
     * @param entries Map of named entries to serialize to jsons. Paths for values are derived from the ResourceLocation's entryid:entrypath as specified above.
     */
    public JsonCodecProvider(DataGenerator dataGenerator, ExistingFileHelper existingFileHelper, String modid, DynamicOps<JsonElement> dynamicOps, PackType packType,
          String directory, Codec<T> codec, Map<ResourceLocation, T> entries)
    {
        // Track generated data so other dataproviders can validate if needed.
        final ResourceType resourceType = new ResourceType(packType, ".json", directory);
        for (ResourceLocation id : entries.keySet())
        {
            existingFileHelper.trackGenerated(id, resourceType);
        }
        this.dataGenerator = dataGenerator;
        this.existingFileHelper = existingFileHelper;
        this.modid = modid;
        this.dynamicOps = dynamicOps;
        this.packType = packType;
        this.directory = directory;
        this.codec = codec;
        this.entries = entries;
    }

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
     * @param entries Map of entries to encode and their ResourceLocations. Paths for values are derived from the ResourceLocation's entryid:entrypath.
     */
    public static <T> JsonCodecProvider<T> forDatapackRegistry(DataGenerator dataGenerator, ExistingFileHelper existingFileHelper, String modid,
          RegistryOps<JsonElement> registryOps, ResourceKey<Registry<T>> registryKey, Map<ResourceLocation, T> entries)
    {
        final ResourceLocation registryId = registryKey.location();
        // Minecraft datapack registry folders are in data/json-namespace/registry-name/
        // Non-vanilla registry folders are data/json-namespace/registry-namespace/registry-name/
        final String registryFolder = registryId.getNamespace().equals("minecraft")
                                      ? registryId.getPath()
                                      : registryId.getNamespace() + "/" + registryId.getPath();
        final Codec<T> codec = (Codec<T>) RegistryAccess.REGISTRIES.get(registryKey).codec();
        return new JsonCodecProvider<>(dataGenerator, existingFileHelper, modid, registryOps, PackType.SERVER_DATA, registryFolder, codec, entries);
    }

    @Override
    public void run(final CachedOutput cache) throws IOException
    {
        final Path outputFolder = this.dataGenerator.getOutputFolder();
        final String dataFolder = this.packType.getDirectory();
        gather(LamdbaExceptionUtils.rethrowBiConsumer((id, value) -> {
            final Path path = outputFolder.resolve(String.join("/", dataFolder, id.getNamespace(), this.directory, id.getPath() + ".json"));
            JsonElement encoded = this.codec.encodeStart(this.dynamicOps, value)
                  .getOrThrow(false, msg -> LOGGER.error("Failed to encode {}: {}", path, msg));
            DataProvider.saveStable(cache, encoded, path);
        }));
    }

    protected void gather(BiConsumer<ResourceLocation, T> consumer)
    {
        this.entries.forEach(consumer);
    }

    @Override
    public String getName()
    {
        return String.format("%s generator for %s", this.directory, this.modid);
    }
}