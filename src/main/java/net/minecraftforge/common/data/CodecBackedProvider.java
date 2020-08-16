package net.minecraftforge.common.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.resources.IResource;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.RegistryKeyCodec;
import net.minecraft.util.registry.WorldSettingsImport;
import net.minecraft.world.DimensionType;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.common.IExtensibleEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jline.utils.InputStreamReader;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

public abstract class CodecBackedProvider<P> implements IDataProvider {
    protected final static Logger LOGGER = LogManager.getLogger();
    protected final Codec<P> codec;
    protected final ExistingFileHelper fileHelper;
    protected final Gson gson;

    protected CodecBackedProvider(Codec<P> codec, ExistingFileHelper helper) {
        this(codec, helper, new GsonBuilder().setPrettyPrinting().registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer()).create());
    }

    protected CodecBackedProvider(Codec<P> codec, ExistingFileHelper fileHelper, Gson gson) {
        this.codec = codec;
        this.fileHelper = fileHelper;
        this.gson = gson;
    }

    /**
     * Helper to retrieve a previously created WorldGen Object.
     *
     * @param prefixKey Usually a {@code RegistryKey<Registry<A>>}
     */
    protected <A> A getFromFile(Codec<A> codec, ResourceLocation toFind, RegistryKey<?> prefixKey) {
        return getFromFile(codec, toFind, prefixKey.func_240901_a_().getPath());
    }

    /**
     * Helper to retrieve a previously created WorldGen Object.
     *
     * If the codec is an instance of {@link RegistryKeyCodec},
     * The {@link WorldSettingsImport} ops will first try to lookup the resource location in the
     * its registry.
     * This is necessary because otherwise instead of some Objects referencing the namespace of another object,
     * they will simply copy the whole content of the object directly.
     * So, whenever possible, a RegistryKeyCodec should be passed instead of a plain Codec.
     * (Pass in {@link DimensionType#field_236002_f_} instead of {@link DimensionType#field_235997_a_})
     *
     * Also, since {@link IExtensibleEnum} can cause problems when encoding enums through {@link IStringSerializable},
     * a partial result when decoding is possible.
     *
     * @param codec     Codec of the type of object to retrieve.
     * @param toFind    Name of the object to find.
     * @param prefix    The path to go from "data/" to "/file.json"
     * @param <A>       Type of the object
     * @return          The object.
     */
    protected <A> A getFromFile(Codec<A> codec, ResourceLocation toFind, String prefix) {
        if(codec instanceof RegistryKeyCodec) {
            JsonElement fromRegistry = gson.toJsonTree(toFind);
            Optional<A> opt = codec.parse(fileHelper.getRegistryParser(), fromRegistry).result();
            if(opt.isPresent()) {
                return opt.get();
            }
            else
                LOGGER.info("Could not decode " + toFind + " from registry. Decoding manually.");
        }

        try {
            IResource resource = fileHelper.getResource(toFind, ResourcePackType.SERVER_DATA, ".json", prefix);
            JsonObject existing = gson.fromJson(new InputStreamReader(resource.getInputStream()), JsonObject.class);
            return codec.parse(fileHelper.getRegistryParser(), existing).getOrThrow(false, s -> LOGGER.error("Could not decode " + toFind + " from\n " + existing + " \n\t-> " + s));
        } catch (IOException io) {
            throw new RuntimeException(io);
        }
    }

    protected void save(P instance, DirectoryCache cache, Path path) throws IOException {
        IDataProvider.save(
                gson,
                cache,
                codec.encodeStart(fileHelper.getRegistrySerializer(), instance).getOrThrow(false,s -> LOGGER.error("Could not encode object for " + this.getName() + " because : " + s)),
                path
        );
    }
}
