/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.client.textures;

import javax.annotation.Nonnull;

import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

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
            AtlasTexture atlas,
            IResourceManager resourceManager, TextureAtlasSprite.Info textureInfo,
            IResource resource,
            int atlasWidth, int atlasHeight,
            int spriteX, int spriteY, int mipmapLevel,
            NativeImage image
    );

}
