/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.client.model.geometry;

import net.minecraft.resources.Identifier;
import net.minecraftsingularity.client.event.ModelEvent;
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
    private static Map<Identifier, IGeometryLoader> LOADERS;
    private static String LOADER_LIST;

    /**
     * Finds the {@link IGeometryLoader} for a given name, or null if not found.
     */
    @Nullable
    public static IGeometryLoader get(Identifier name) {
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
        var loaders = new HashMap<Identifier, IGeometryLoader>();
        ModelEvent.RegisterGeometryLoaders.BUS.post(new ModelEvent.RegisterGeometryLoaders(loaders));
        LOADERS = Map.copyOf(loaders);
        LOADER_LIST = loaders.keySet().stream().map(Identifier::toString).collect(Collectors.joining(", "));
    }

    private GeometryLoaderManager() {}
}
