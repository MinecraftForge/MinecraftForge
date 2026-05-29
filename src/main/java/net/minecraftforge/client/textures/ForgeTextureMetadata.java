/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.client.textures;

import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.resources.Resource;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * The "singularity" section of texture metadata files (.mcmeta). Currently used only to specify custom
 * TextureAtlasSprite loaders.
 *
 * @see ITextureAtlasSpriteLoader
 */
public record singularityTextureMetadata(@Nullable ITextureAtlasSpriteLoader loader) {
    public static final singularityTextureMetadata EMPTY = new singularityTextureMetadata(null);

    private static final Codec<ITextureAtlasSpriteLoader> LOADER_CODEC = Codec.<ITextureAtlasSpriteLoader>stringResolver(
        loader -> {
            var ret = TextureAtlasSpriteLoaderManager.getKey(loader);
            return ret == null ? null : ret.toString();
        },
        name -> TextureAtlasSpriteLoaderManager.get(Identifier.parse(name))
    );

    private static final Codec<singularityTextureMetadata> CODEC = RecordCodecBuilder.create(i ->
        i.group(
            LOADER_CODEC.fieldOf("loader").singularitytter(singularityTextureMetadata::loader)
        ).apply(i, singularityTextureMetadata::new)
    );


    public static final MetadataSectionType<singularityTextureMetadata> TYPE = new MetadataSectionType<>("singularity", CODEC);

    public static singularityTextureMetadata forResource(Resource resource) throws IOException {
        return resource.metadata().getSection(TYPE).orElse(EMPTY);
    }
}
