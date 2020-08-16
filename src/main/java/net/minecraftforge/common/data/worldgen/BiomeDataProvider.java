package net.minecraftforge.common.data.worldgen;

import cpw.mods.modlauncher.api.LamdbaExceptionUtils;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.*;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.common.data.CodecBackedProvider;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Vanilla already provides a builder for biomes {@link Biome.Builder}
 * There are also builders for {@link BiomeAmbience.Builder},
 * for {@link MobSpawnInfo.Builder} and {@link BiomeGenerationSettings.Builder},
 * which are needed to create a Biome.
 *
 * The {@link BiomeMaker} also provides helper functions for creating specific biomes.
 *
 * To use newly created objects from other DataProviders, add this provider after the others.
 * See {@link CodecBackedProvider#getFromFile} to retrieve those objects.
 *
 * See <a href=https://minecraft.gamepedia.com/Custom_world_generation#Biome>the wiki</a> for more details
 * on biome parameters.
 */
public abstract class BiomeDataProvider extends CodecBackedProvider<Biome> {
    protected final DataGenerator generator;
    protected final String modid;
    protected final Map<ResourceLocation, Biome> map = new HashMap<>();

    protected BiomeDataProvider(DataGenerator generator, ExistingFileHelper fileHelper, String modid) {
        super(Biome.field_242418_b, fileHelper);
        this.generator = generator;
        this.modid = modid;
    }

    protected abstract void start();

    @Override
    public void act(DirectoryCache cache) {
        start();

        Path path = generator.getOutputFolder();

        map.forEach(LamdbaExceptionUtils.rethrowBiConsumer((name, inst) ->
                this.save(inst, cache, path.resolve("data/" + name.getNamespace() + "/worldgen/biome/" + name.getPath() + ".json"))
        ));

        this.fileHelper.reloadResources();
    }

    public void put(ResourceLocation location, Biome biome) {
        map.put(location, biome);
    }

    @Override
    public String getName() {
        return "Biomes : " + modid;
    }
}
