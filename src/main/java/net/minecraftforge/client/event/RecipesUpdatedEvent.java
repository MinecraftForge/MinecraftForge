/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import net.minecraft.client.ClientRecipeBook;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.ApiStatus;

/**
 * Fired when the {@link ClientRecipeBook} has updated information about recipes from the server to the client.
 *
 * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.</p>
 *
 * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 */
public final class RecipesUpdatedEvent extends Event {
    private final ClientRecipeBook recipeBook;

    @ApiStatus.Internal
    public RecipesUpdatedEvent(ClientRecipeBook recipeBook) {
        this.recipeBook = recipeBook;
    }

    /**
     * {@return the recipe manager}
     */
    public ClientRecipeBook getRecipeBook() {
        return recipeBook;
    }
}
