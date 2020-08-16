package net.minecraftforge.common.data.worldgen;

import cpw.mods.modlauncher.api.LamdbaExceptionUtils;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Features;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.common.data.CodecBackedProvider;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * No builder for this class as ConfiguredFeatures are simple to create, see {@link Features}.
 */
public abstract class ConfiguredFeatureProvider extends CodecBackedProvider<ConfiguredFeature<?,?>> {
    private final DataGenerator generator;
    private final String modid;
    protected final Map<ResourceLocation, ConfiguredFeature<?, ?>> map = new HashMap<>();

    public ConfiguredFeatureProvider(DataGenerator generator, ExistingFileHelper fileHelper, String modid) {
        super(ConfiguredFeature.field_242763_a, fileHelper); //TODO This codec is dispatched for the vanilla FEATURE registry, and won't affect any mod added features.
        this.generator = generator;
        this.modid = modid;
    }

    protected abstract void start();

    @Override
    public void act(DirectoryCache cache) {
        start();

        Path path = generator.getOutputFolder();

        map.forEach(LamdbaExceptionUtils.rethrowBiConsumer((name, inst) ->
                this.save(inst, cache, path.resolve("data/" + name.getNamespace() + "/worldgen/configured_feature/" + name.getPath() + ".json"))
        ));

        this.fileHelper.reloadResources();
    }

    public void put(ResourceLocation location, ConfiguredFeature<?, ?> feature) {
        map.put(location, feature);
    }

    @Override
    public String getName() {
        return "Configured Features: " + modid;
    }
}
