package net.minecraftforge.client.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.util.ResourceLocation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Supplier;

public abstract class ModelProvider<T extends ModelBuilder<?>> implements IDataProvider {

    public static final String BLOCK_FOLDER = "block";
    public static final String ITEM_FOLDER = "item";

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
    protected final DataGenerator generator;
    protected final String modid;
    protected final String folder;
    protected final Supplier<T> factory;
    protected final Map<ResourceLocation, T> builders = new HashMap<>();

    protected abstract void registerBuilders();

    public ModelProvider(DataGenerator generator, String modid, String folder, Supplier<T> factory) {
        this.generator = generator;
        this.modid = modid;
        this.folder = folder;
        this.factory = factory;
    }

    protected T getBuilder(String path) {
        return builders.computeIfAbsent(new ResourceLocation(modid, path), $ -> factory.get());
    }

    @Override
    public void act(DirectoryCache cache) throws IOException {
        builders.clear();
        registerBuilders();
        for (Entry<ResourceLocation, T> e : builders.entrySet()) {
            Path path = getPath(e.getKey());
            JsonObject json = e.getValue().serialize();
            if (path == null) {
                return;
            }

            try {
                String s = GSON.toJson(json);
                String s1 = HASH_FUNCTION.hashUnencodedChars(s).toString();
                if (!Objects.equals(cache.getPreviousHash(path), s1) || !Files.exists(path)) {
                    Files.createDirectories(path.getParent());

                    try (BufferedWriter bufferedwriter = Files.newBufferedWriter(path)) {
                        bufferedwriter.write(s);
                    }
                }

                cache.func_208316_a(path, s1);
            } catch (IOException ioexception) {
                LOGGER.error("Couldn't save models to {}", path, ioexception);
            }
        }
    }

    protected Path getPath(ResourceLocation loc) {
        return this.generator.getOutputFolder().resolve("assets/" + loc.getNamespace() + "/models/" + folder + "/" + loc.getPath() + ".json");
    }
}
