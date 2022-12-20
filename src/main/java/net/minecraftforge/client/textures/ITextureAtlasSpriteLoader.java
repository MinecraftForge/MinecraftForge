/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.textures;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraftforge.client.event.RegisterTextureAtlasSpriteLoadersEvent;
import org.jetbrains.annotations.NotNull;

/**
 * A loader for custom {@linkplain TextureAtlasSprite texture atlas sprites}.
 * <p>
 * The loader can be specified in the corresponding .mcmeta file for a texture as follows:
 * <pre>
 * {
 *   "forge": {
 *     "loader": "examplemod:example_tas_loader"
 *   }
 * }
 * </pre>
 *
 * @see RegisterTextureAtlasSpriteLoadersEvent
 */
public interface ITextureAtlasSpriteLoader
{
    SpriteContents loadContents(ResourceLocation name, Resource resource, FrameSize frameSize, NativeImage image,
            AnimationMetadataSection animationMeta, ForgeTextureMetadata forgeMeta);

    /**
     * Creates a {@link TextureAtlasSprite} from the given {@link SpriteContents}.
     */
    @NotNull
    TextureAtlasSprite makeSprite(ResourceLocation atlasName, SpriteContents contents, int atlasWidth, int atlasHeight, int spriteX, int spriteY, int mipmapLevel);
}
