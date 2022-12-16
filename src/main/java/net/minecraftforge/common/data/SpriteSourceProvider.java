/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.data;

import com.mojang.serialization.JsonOps;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSources;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * <p>Data provider for atlas configuration files.<br>
 * An atlas configuration is bound to a specific texture atlas such as the {@code minecraft:blocks} atlas and
 * allows adding additional textures to the atlas by adding {@link SpriteSource}s to the configuration.</p>
 * <p>See {@link SpriteSources} for the available sources and the constants in this class for the
 * atlases used in vanilla Minecraft</p>
 */
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

    public SpriteSourceProvider(PackOutput output, ExistingFileHelper fileHelper, String modid)
    {
        super(output, fileHelper, modid, JsonOps.INSTANCE, PackType.CLIENT_RESOURCES, "atlases", SpriteSources.FILE_CODEC, Map.of());
    }

    @Override
    protected final void gather(BiConsumer<ResourceLocation, List<SpriteSource>> consumer)
    {
        addSources();
        atlases.forEach((atlas, srcList) -> consumer.accept(atlas, srcList.sources));
    }

    protected abstract void addSources();

    /**
     * Get or create a {@link SourceList} for the given atlas
     * @param atlas The texture atlas the sources should be added to, see constants at the top for the format
     *              and the vanilla atlases
     * @return an existing {@code SourceList} for the given atlas or a new one if not present yet
     */
    protected final SourceList atlas(ResourceLocation atlas)
    {
        return atlases.computeIfAbsent(atlas, $ -> new SourceList());
    }

    protected static final class SourceList
    {
        private final List<SpriteSource> sources = new ArrayList<>();

        /**
         * Add the given {@link SpriteSource} to this atlas configuration
         * @param source The {@code SpriteSource} to be added
         */
        public SourceList addSource(SpriteSource source)
        {
            sources.add(source);
            return this;
        }
    }
}
