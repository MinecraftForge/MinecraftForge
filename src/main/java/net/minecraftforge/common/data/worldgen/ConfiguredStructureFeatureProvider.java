package net.minecraftforge.common.data.worldgen;

import cpw.mods.modlauncher.api.LamdbaExceptionUtils;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.common.data.CodecBackedProvider;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * No builder for this class as StructureFeatures are simple to create, see {@link Structure}.
 */
public abstract class ConfiguredStructureFeatureProvider extends CodecBackedProvider<StructureFeature<?,?>> {
    private final DataGenerator generator;
    private final String modid;
    protected final Map<ResourceLocation, StructureFeature<?, ?>> map = new HashMap<>();

    public ConfiguredStructureFeatureProvider(DataGenerator generator, ExistingFileHelper fileHelper, String modid) {
        super(StructureFeature.field_236267_a_, fileHelper); //TODO This codec is dispatched for the vanilla STRUCTURE_FEATURE registry, and won't affect any mod added structures.
        this.generator = generator;
        this.modid = modid;
    }

    protected abstract void start();

    @Override
    public void act(DirectoryCache cache) {
        start();

        Path path = generator.getOutputFolder();

        map.forEach(LamdbaExceptionUtils.rethrowBiConsumer((name, inst) ->
                this.save(inst, cache, path.resolve("data/" + name.getNamespace() + "/worldgen/configured_structure_feature/" + name.getPath() + ".json"))
        ));

        this.fileHelper.reloadResources();
    }

    public void put(ResourceLocation location, StructureFeature<?, ?> structure) {
        map.put(location, structure);
    }

    @Override
    public String getName() {
        return "Configured Structure Features: " + modid;
    }
}
