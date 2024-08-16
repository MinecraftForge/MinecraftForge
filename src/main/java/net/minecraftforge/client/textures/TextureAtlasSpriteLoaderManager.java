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
import java.util.Map;

/**
 * Manager for {@link ITextureAtlasSpriteLoader} instances.
 * <p>
 * Provides a lookup.
 */
public final class TextureAtlasSpriteLoaderManager {
    private static Map<ResourceLocation, ITextureAtlasSpriteLoader> getLoaders() {
        final class LazyInit {
            private static final Map<ResourceLocation, ITextureAtlasSpriteLoader> INSTANCE;

            static {
                var loaders = new HashMap<ResourceLocation, ITextureAtlasSpriteLoader>();
                var event = new RegisterTextureAtlasSpriteLoadersEvent(loaders);
                ModLoader.get().postEventWrapContainerInModOrder(event);
                INSTANCE = ImmutableMap.copyOf(loaders);
            }

            private LazyInit() {}
        }

        return LazyInit.INSTANCE;
    }

    /**
     * Finds the loader with the given name, or null if none is registered.
     */
    @Nullable
    public static ITextureAtlasSpriteLoader get(ResourceLocation name) {
        return getLoaders().get(name);
    }

    @ApiStatus.Internal
    public static void init() {
        getLoaders(); // load the LazyInit class
    }

    private TextureAtlasSpriteLoaderManager() {}
}
