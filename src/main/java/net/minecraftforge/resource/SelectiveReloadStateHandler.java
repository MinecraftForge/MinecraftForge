/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.resource;

import javax.annotation.Nullable;
import java.util.function.Predicate;

import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraftforge.common.ForgeConfig;

/**
 * Handles reload parameters for selective loaders.
 */
public enum SelectiveReloadStateHandler
{
    INSTANCE;

    @Nullable
    private Predicate<IResourceType> currentPredicate = null;

    /***
     * Pushes a resource type predicate for the current reload.
     * Should only be called when initiating a resource reload.
     * If a reload is already in progress when this is called, an exception will be thrown.
     *
     * @param resourcePredicate the resource requirement predicate for the current reload
     */
    public void beginReload(Predicate<IResourceType> resourcePredicate)
    {
        if (this.currentPredicate != null)
        {
            throw new IllegalStateException("Recursive resource reloading detected");
        }

        this.currentPredicate = resourcePredicate;
    }

    /**
     * Gets the current reload resource predicate for the initiated reload.
     *
     * @return the active reload resource predicate, or an accepting one if none in progress
     */
    public Predicate<IResourceType> get()
    {
        if (this.currentPredicate == null || !ForgeConfig.CLIENT.selectiveResourceReloadEnabled.get())
        {
            return ReloadRequirements.all();
        }

        return this.currentPredicate;
    }

    /**
     * Finishes the current reload and deletes the previously added reload predicate.
     */
    public void endReload()
    {
        this.currentPredicate = null;
    }

    //Helpder function for testing if vanilla listeners should reload.
    @SuppressWarnings("deprecation")
    public boolean test(IResourceManagerReloadListener listener)
    {
        IResourceType type = listener.getResourceType();
        return type == null || get() == null || get().test(type);
    }
}
