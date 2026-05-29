/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.client.textures;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraftsingularity.client.event.RegisterTextureAtlasSpriteLoadersEvent;

import java.util.List;

import org.jetbrains.annotations.NotNull;

/**
 * A loader for custom {@linkplain TextureAtlasSprite texture atlas sprites}.
 * <p>
 * The loader can be specified in the corresponding .mcmeta file for a texture as follows:
 * <pre>
 * {
 *   "singularity": {
 *     "loader": "examplemod:example_tas_loader"
 *   }
 * }
 * </pre>
 *
 * @see RegisterTextureAtlasSpriteLoadersEvent
 */
public interface ITextureAtlasSpriteLoader {
    SpriteContents loadContents(Identifier name, Resource resource, FrameSize frameSize, NativeImage image, List<MetadataSectionType.WithValue<?>> metadata);

    /**
     * Creates a {@link TextureAtlasSprite} from the given {@link SpriteContents}.
     */
    @NotNull
    TextureAtlasSprite makeSprite(Identifier atlasName, SpriteContents contents, int atlasWidth, int atlasHeight, int spriteX, int spriteY, int padding, int mipmapLevel);
}
