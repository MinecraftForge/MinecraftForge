/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.client.model.geometry;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.client.resources.model.geometry.UnbakedGeometry;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftsingularity.client.event.ModelEvent.RegisterGeometryLoaders;
import net.minecraftsingularity.client.event.RegisterClientReloadListenersEvent;

/**
 * A loader for custom {@linkplain UnbakedGeometry model geometries}.
 * <p>
 * If you do any caching, you should implement {@link ResourceManagerReloadListener} and register it with
 * {@link RegisterClientReloadListenersEvent}.
 *
 * @see RegisterGeometryLoaders
 * @see RegisterClientReloadListenersEvent
 */
public interface IGeometryLoader {
    UnbakedGeometry read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException;
}
