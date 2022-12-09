package net.minecraftforge.common.data;

import com.mojang.serialization.*;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSources;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

import java.util.*;
import java.util.function.BiConsumer;

public abstract class SpriteSourceProvider extends JsonCodecProvider<List<SpriteSource>>
{
    protected static final ResourceLocation BLOCKS_ATLAS = new ResourceLocation("blocks");
    protected static final ResourceLocation BANNER_PATTERNS_ATLAS = new ResourceLocation("banner_patterns");
    protected static final ResourceLocation BEDS_ATLAS = new ResourceLocation("beds");
    protected static final ResourceLocation CHESTS_ATLAS = new ResourceLocation("chests");
    protected static final ResourceLocation SHIELD_PATTERNS_ATLAS = new ResourceLocation("shield_patterns");
    protected static final ResourceLocation SHULKER_BOXES_ATLAS = new ResourceLocation("shulker_boxes");
    protected static final ResourceLocation SIGNS_ATLAS = new ResourceLocation("signs");
    protected static final ResourceLocation MOB_EFFECTS_ATLAS = new ResourceLocation("mob_effects");
    protected static final ResourceLocation PAINTINGS_ATLAS = new ResourceLocation("paintings");
    protected static final ResourceLocation PARTICLES_ATLAS = new ResourceLocation("particles");

    private final Map<ResourceLocation, SourceList> atlases = new HashMap<>();

    public SpriteSourceProvider(DataGenerator generator, ExistingFileHelper fileHelper, String modid)
    {
        super(generator, fileHelper, modid, JsonOps.INSTANCE, PackType.CLIENT_RESOURCES, "atlases", SpriteSources.FILE_CODEC, Map.of());
    }

    @Override
    protected final void gather(BiConsumer<ResourceLocation, List<SpriteSource>> consumer)
    {
        addSources();
        atlases.forEach((atlas, srcList) -> consumer.accept(atlas, srcList.sources));
    }

    protected abstract void addSources();



    protected final SourceList atlas(ResourceLocation atlas)
    {
        return atlases.computeIfAbsent(atlas, $ -> new SourceList());
    }

    protected static final class SourceList
    {
        private final List<SpriteSource> sources = new ArrayList<>();

        public SourceList addSource(SpriteSource source)
        {
            sources.add(source);
            return this;
        }
    }
}
