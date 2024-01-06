/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.client.model.geometry.IModelGeometry;

public interface IModelLoader<T extends IModelGeometry<T>> extends ResourceManagerReloadListener
{
    T read(JsonDeserializationContext deserializationContext, JsonObject modelContents);
}
