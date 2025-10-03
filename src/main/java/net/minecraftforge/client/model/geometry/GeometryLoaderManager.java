/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.geometry;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ModelEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Manager for {@linkplain IGeometryLoader geometry loaders}.
 * <p>
 * Provides a lookup.
 */
public final class GeometryLoaderManager {
    private static Map<ResourceLocation, IGeometryLoader> LOADERS;
    private static String LOADER_LIST;

    /**
     * Finds the {@link IGeometryLoader} for a given name, or null if not found.
     */
    @Nullable
    public static IGeometryLoader get(ResourceLocation name) {
        return LOADERS.get(name);
    }

    /**
     * Retrieves a comma-separated list of all active loaders, for use in error messages.
     */
    public static String getLoaderList() {
        return LOADER_LIST;
    }

    @ApiStatus.Internal
    public static void init() {
        var loaders = new HashMap<ResourceLocation, IGeometryLoader>();
        ModelEvent.RegisterGeometryLoaders.BUS.post(new ModelEvent.RegisterGeometryLoaders(loaders));
        LOADERS = Map.copyOf(loaders);
        LOADER_LIST = loaders.keySet().stream().map(ResourceLocation::toString).collect(Collectors.joining(", "));
    }

    private GeometryLoaderManager() {}
}
