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

package net.minecraftforge.client.extensions;

import java.util.Collection;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

public interface IForgeTextureAtlasSprite
{

    /**
     * @deprecated See {@link net.minecraftforge.client.textures.ITextureAtlasSpriteLoader}
     */
    @Deprecated
    default boolean hasCustomLoader(IResourceManager manager, ResourceLocation location)
    {
        return false;
    }

    /**
     * @deprecated See {@link net.minecraftforge.client.textures.ITextureAtlasSpriteLoader}
     */
    @Deprecated
    default boolean load(IResourceManager manager, ResourceLocation location, Function<ResourceLocation, TextureAtlasSprite> textureGetter)
    {
        return true;
    }

    @Deprecated
    default Collection<ResourceLocation> getDependencies() 
    {
        return ImmutableList.of();
    }
}
