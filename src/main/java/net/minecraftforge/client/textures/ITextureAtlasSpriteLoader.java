/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.textures;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

/**
 * A loader for custom TextureAtlasSprite implementations.<br>
 * The loader can be specified in the corresponding .mcmeta file for a texture as follows:
 * <pre>
 * {
 *   "forge": {
 *     "loader": "examplemod:example_tas_loader"
 *   }
 * }
 * </pre>
 * @see net.minecraftforge.client.MinecraftForgeClient#registerTextureAtlasSpriteLoader(ResourceLocation, ITextureAtlasSpriteLoader)
 */
public interface ITextureAtlasSpriteLoader
{

    /**
     * Load a TextureAtlasSprite for the given resource.
     */
    @Nonnull
    TextureAtlasSprite load(
            TextureAtlas atlas,
            ResourceManager resourceManager, TextureAtlasSprite.Info textureInfo,
            Resource resource,
            int atlasWidth, int atlasHeight,
            int spriteX, int spriteY, int mipmapLevel,
            NativeImage image
    );

}
