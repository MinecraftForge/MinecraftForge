/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.resource;

import java.util.function.Predicate;

import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;

public interface ISelectiveResourceReloadListener extends IResourceManagerReloadListener
{
    @Override
    default void onResourceManagerReload(IResourceManager resourceManager)
    {
        // For compatibility, call the selective version from the non-selective function
        onResourceManagerReload(resourceManager, SelectiveReloadStateHandler.INSTANCE.get());
    }

    /**
     * A version of onResourceManager that selectively chooses {@link net.minecraftforge.resource.IResourceType}s
     * to reload.
     * When using this, the given predicate should be called to ensure the relevant resources should
     * be reloaded at this time.
     *
     * @param resourceManager the resource manager being reloaded
     * @param resourcePredicate predicate to test whether any given resource type should be reloaded
     */
    void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate);
}
