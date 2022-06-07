/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;

/**
 * Fired when the {@link RecipeManager} has received and synced the recipes from the server to the client.
 *
 * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}. </p>
 *
 * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
 */
public class RecipesUpdatedEvent extends Event
{

    private final RecipeManager mgr;

    /**
     * @hidden
     * @see ForgeHooksClient#onRecipesUpdated(RecipeManager)
     */
    public RecipesUpdatedEvent(RecipeManager mgr)
    {
        this.mgr = mgr;
    }

    /**
     * {@return the recipe manager}
     */
    public RecipeManager getRecipeManager()
    {
        return mgr;
    }
}
