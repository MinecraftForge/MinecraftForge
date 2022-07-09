/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.textures;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
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
 * @see net.minecraftforge.client.event.RegisterTextureAtlasSpriteLoadersEvent
 */
public interface ITextureAtlasSpriteLoader
{
    /**
     * Load a {@link TextureAtlasSprite} for the given resource.
     */
    @NotNull
    TextureAtlasSprite load(TextureAtlas atlas, ResourceManager resourceManager, TextureAtlasSprite.Info textureInfo,
                            Resource resource, int atlasWidth, int atlasHeight, int spriteX, int spriteY,
                            int mipmapLevel, NativeImage image);
}
