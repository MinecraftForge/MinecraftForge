/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.resource.VanillaResourceType;

import java.util.function.Predicate;

public interface IModelLoader<T extends IModelGeometry<T>> extends ISelectiveResourceReloadListener
{
    @Override
    default IResourceType getResourceType()
    {
        return VanillaResourceType.MODELS;
    }

    @Override
    void onResourceManagerReload(IResourceManager resourceManager);

    @Override
    default void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate)
    {
        if (resourcePredicate.test(getResourceType()))
        {
            onResourceManagerReload(resourceManager);
        }
    }

    T read(JsonDeserializationContext deserializationContext, JsonObject modelContents);
}
