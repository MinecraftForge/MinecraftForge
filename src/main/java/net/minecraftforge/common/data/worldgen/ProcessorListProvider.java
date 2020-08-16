package net.minecraftforge.common.data.worldgen;

import cpw.mods.modlauncher.api.LamdbaExceptionUtils;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.template.IStructureProcessorType;
import net.minecraft.world.gen.feature.template.StructureProcessorList;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.common.data.CodecBackedProvider;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Very simply provider, doesn't need to be implemented.
 * Use {@link #put} as a Builder pattern.
 */
public class ProcessorListProvider extends CodecBackedProvider<StructureProcessorList> {
    private final DataGenerator generator;
    private final String modid;
    protected final Map<ResourceLocation, StructureProcessorList> map = new HashMap<>();

    public ProcessorListProvider(DataGenerator generator, ExistingFileHelper fileHelper, String modid) {
        super(IStructureProcessorType.field_242921_l, fileHelper);
        this.generator = generator;
        this.modid = modid;
    }

    @Override
    public void act(DirectoryCache cache) {
        Path path = generator.getOutputFolder();

        map.forEach(LamdbaExceptionUtils.rethrowBiConsumer((name, inst) ->
                this.save(inst, cache, path.resolve("data/" + name.getNamespace() + "/worldgen/processor_list/" + name.getPath() + ".json"))
        ));

        this.fileHelper.reloadResources();
    }

    public ProcessorListProvider put(ResourceLocation location, StructureProcessorList inst) {
        map.put(location, inst);
        return this;
    }

    @Override
    public String getName() {
        return "Processor Lists: " + modid;
    }
}
