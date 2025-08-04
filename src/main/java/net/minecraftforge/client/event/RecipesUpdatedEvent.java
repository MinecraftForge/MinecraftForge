/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import net.minecraft.client.ClientRecipeBook;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.RecordEvent;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.ApiStatus;

/**
 * Fired when the {@link ClientRecipeBook} has updated information about recipes from the server to the client.
 *
 * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.</p>
 *
 * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 *
 * @param getRecipeBook the recipe manager
 */
public record RecipesUpdatedEvent(ClientRecipeBook getRecipeBook) implements RecordEvent {
    public static final EventBus<RecipesUpdatedEvent> BUS = EventBus.create(RecipesUpdatedEvent.class);

    @ApiStatus.Internal
    public RecipesUpdatedEvent {}
}
