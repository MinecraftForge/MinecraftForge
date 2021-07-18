package net.minecraftforge.common.data;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.ResourceLocation;

/**
 * Generic data provider that uses DataFixerUpper Codecs to generate jsons from
 * java objects.
 */
public class JsonDataProvider<T> implements IDataProvider
{

    protected static final Logger LOGGER = LogManager.getLogger();

    protected final Gson gson;
    protected final DataGenerator generator;
    protected final ResourcePackType resourceType;
    protected final String folder;
    protected final Codec<T> codec;
    protected final Map<ResourceLocation, T> objects;

    /**
     * Creates a data provider that uses a codec to generate jsons in a specified
     * folder.
     * 
     * @param gson
     *            The gson instance that will be used to write JsonElements to raw
     *            text json files
     * @param generator
     *            The generator instance from the gather data event
     * @param resourceType
     *            Whether to generate data in the assets or data folder
     * @param folder
     *            The root folder of this data type in a given data domain e.g. to
     *            generate data in resources/data/modid/foods/cheeses/, use
     *            DATA for the resource type, and "foods/cheeses" for
     *            the folder name.
     * @param codec
     *            The codec that will be used to convert objects to jsons
     * @param objects
     *            An ID-to-object map that defines the objects to generate jsons
     *            from and where the jsons will be generated.
     */
    public JsonDataProvider(Gson gson, DataGenerator generator, ResourcePackType resourceType, String folder, Codec<T> codec, Map<ResourceLocation, T> objects)
    {
        this.gson = gson;
        this.generator = generator;
        this.resourceType = resourceType;
        this.folder = folder;
        this.codec = codec;
        this.objects = objects;
    }

    /**
     * Takes all the objects declared by #addObject calls and writes them to json.
     * If this provider has been added to the data generator from gatherDataEvent,
     * this will be automatically called. Alternatively, other data providers can
     * invoke this in their own run methods if they choose to do so.
     */
    @Override
    public void run(DirectoryCache cache) throws IOException
    {
        Path resourcesFolder = this.generator.getOutputFolder();
        this.objects.forEach((id, object) -> {
            Path jsonLocation = resourcesFolder.resolve(String.join("/", this.resourceType.getDirectory(), id.getNamespace(), this.folder, id.getPath() + ".json"));
            this.codec.encodeStart(JsonOps.INSTANCE, object)
                .resultOrPartial(s -> LOGGER.error("{} {} provider failed to encode {}", this.folder, this.resourceType.getDirectory(), jsonLocation, s))
                .ifPresent(jsonElement -> {
                    try
                    {
                        IDataProvider.save(this.gson, cache, jsonElement, jsonLocation);
                    }
                    catch (IOException e)
                    {
                        LOGGER.error("{} {} provider failed to save {}", this.folder, this.resourceType.getDirectory(), jsonLocation, e);
                    }
                });

        });
    }

    /**
     * Gets the name of this data provider. Used by the data generator to log its
     * root data providers.
     */
    @Override
    public String getName()
    {
        return String.format("%s %s provider", this.folder, this.resourceType.getDirectory());
    }

}