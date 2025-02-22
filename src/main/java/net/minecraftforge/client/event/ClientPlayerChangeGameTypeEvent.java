/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.world.level.GameType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.RecordEvent;
import net.minecraftforge.fml.LogicalSide;

/**
 * Fired when the client player is notified of a change of {@link GameType} from the server.
 *
 * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.</p>
 *
 * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 *
 * @param info the client player information
 * @param currentGameType the current game type of the player
 * @param newGameType the new game type of the player
 */
public record ClientPlayerChangeGameTypeEvent(PlayerInfo info, GameType currentGameType, GameType newGameType)
        implements RecordEvent {
    public static final EventBus<ClientPlayerChangeGameTypeEvent> BUS = EventBus.create(ClientPlayerChangeGameTypeEvent.class);
}
