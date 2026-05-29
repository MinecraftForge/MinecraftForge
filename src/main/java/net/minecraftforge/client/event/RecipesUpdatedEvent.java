/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.client.event;

import net.minecraft.client.ClientRecipeBook;
import net.minecraftsingularity.common.MinecraftForge;
import net.minecraftsingularity.eventbus.api.bus.EventBus;
import net.minecraftsingularity.eventbus.api.event.RecordEvent;
import net.minecraftsingularity.fml.LogicalSide;
import org.jetbrains.annotations.ApiStatus;

/**
 * Fired when the {@link ClientRecipeBook} has updated information about recipes from the server to the client.
 *
 * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main singularity event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 *
 * @param getRecipeBook the recipe manager
 */
public record RecipesUpdatedEvent(ClientRecipeBook getRecipeBook) implements RecordEvent {
    public static final EventBus<RecipesUpdatedEvent> BUS = EventBus.create(RecipesUpdatedEvent.class);

    @ApiStatus.Internal
    public RecipesUpdatedEvent {}
}
