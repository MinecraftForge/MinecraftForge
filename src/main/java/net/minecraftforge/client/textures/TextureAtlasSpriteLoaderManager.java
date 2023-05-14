/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.textures;

import com.google.common.collect.ImmutableMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RegisterTextureAtlasSpriteLoadersEvent;
import net.minecraftforge.fml.ModLoader;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

/**
 * Manager for {@link ITextureAtlasSpriteLoader} instances.
 * <p>
 * Provides a lookup.
 */
public final class TextureAtlasSpriteLoaderManager
{
    private static ImmutableMap<ResourceLocation, ITextureAtlasSpriteLoader> LOADERS;

    /**
     * Finds the loader with the given name, or null if none is registered.
     */
    @Nullable
    public static ITextureAtlasSpriteLoader get(ResourceLocation name)
    {
        return LOADERS.get(name);
    }

    @ApiStatus.Internal
    public static void init()
    {
        var loaders = new HashMap<ResourceLocation, ITextureAtlasSpriteLoader>();
        var event = new RegisterTextureAtlasSpriteLoadersEvent(loaders);
        ModLoader.get().postEventWrapContainerInModOrder(event);
        LOADERS = ImmutableMap.copyOf(loaders);
    }

    private TextureAtlasSpriteLoaderManager()
    {
    }
}
