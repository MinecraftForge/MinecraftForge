/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.extensions;

import java.util.Collection;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.resources.ResourceLocation;

public interface IForgeTextureAtlasSprite
{

    /**
     * @deprecated See {@link net.minecraftforge.client.textures.ITextureAtlasSpriteLoader}
     */
    @Deprecated
    default boolean hasCustomLoader(ResourceManager manager, ResourceLocation location)
    {
        return false;
    }

    /**
     * @deprecated See {@link net.minecraftforge.client.textures.ITextureAtlasSpriteLoader}
     */
    @Deprecated
    default boolean load(ResourceManager manager, ResourceLocation location, Function<ResourceLocation, TextureAtlasSprite> textureGetter)
    {
        return true;
    }

    @Deprecated
    default Collection<ResourceLocation> getDependencies() 
    {
        return ImmutableList.of();
    }
}
