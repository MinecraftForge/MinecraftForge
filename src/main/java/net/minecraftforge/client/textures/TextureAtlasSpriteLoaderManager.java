/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.textures;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RegisterTextureAtlasSpriteLoadersEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * Manager for {@link ITextureAtlasSpriteLoader} instances.
 * <p>
 * Provides a lookup.
 * TODO: Convert this into a proper registry
 */
public final class TextureAtlasSpriteLoaderManager {
    private static BiMap<ResourceLocation, ITextureAtlasSpriteLoader> LOADERS;

    /**
     * Finds the loader with the given name, or null if none is registered.
     */
    @Nullable
    public static ITextureAtlasSpriteLoader get(ResourceLocation name) {
        return LOADERS.get(name);
    }

    @Nullable
    public static ResourceLocation getKey(ITextureAtlasSpriteLoader value) {
        return LOADERS.inverse().get(value);
    }

    @ApiStatus.Internal
    public static void init() {
        var loaders = HashBiMap.<ResourceLocation, ITextureAtlasSpriteLoader>create();
        RegisterTextureAtlasSpriteLoadersEvent.BUS.post(new RegisterTextureAtlasSpriteLoadersEvent(loaders));
        LOADERS = ImmutableBiMap.copyOf(loaders);
    }

    private TextureAtlasSpriteLoaderManager() {}
}
