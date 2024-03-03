/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.Event;

/**
 * Fired on {@link Dist#CLIENT} when {@link RecipeManager} has all of its recipes synced from the server to the client (just after a client has connected),
 */
public class RecipesUpdatedEvent extends Event
{
    
    private final RecipeManager mgr;
    
    public RecipesUpdatedEvent(RecipeManager mgr)
    {
        this.mgr = mgr;
    }

    /**
     * @return The newly-updated recipe manager that now contains all the recipes that were just received.
     */
    public RecipeManager getRecipeManager()
    {
        return mgr;
    }
}
