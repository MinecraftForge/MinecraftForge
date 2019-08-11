/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

package net.minecraftforge.client.extensions;

import com.google.common.collect.ImmutableList;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

public interface IForgeTextureAtlasSprite
{
    default TextureAtlasSprite getSprite()
    {
        return (TextureAtlasSprite)this;
    }

    /**
     * Load the specified resource as this sprite's data.
     * This method does not need to generate mipmaps - only first level has to be specified.
     * @param manager Main resource manager
     * @param location File resource location
     * @param mipmapLevels Size of image array to allocate
     * @param textureGetter Accessor for dependencies. May block if dependency is not yet loaded.
     */
    default void load(IResourceManager manager, ResourceLocation location, int mipmapLevels, Function<ResourceLocation, CompletableFuture<TextureAtlasSprite>> textureGetter) throws IOException
    {
        try (IResource resource = manager.getResource(location))
        {
            getSprite().loadSpriteFrames(resource, mipmapLevels);
        }
    }

    /**
     * @return all textures that should be loaded before this texture.
     */
    default Collection<ResourceLocation> getDependencies()
    {
        return ImmutableList.of();
    }
}
