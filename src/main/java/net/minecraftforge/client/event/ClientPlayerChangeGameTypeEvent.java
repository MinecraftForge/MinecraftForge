/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.client.event;

import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.world.level.GameType;
import net.minecraftsingularity.eventbus.api.bus.EventBus;
import net.minecraftsingularity.eventbus.api.event.RecordEvent;
import net.minecraftsingularity.fml.LogicalSide;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;

/**
 * Fired when the client player is notified of a change of {@link GameType} from the server.
 *
 * <p>This event is fired only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 *
 * @param getInfo the client player information
 * @param getCurrentGameType the current game type of the player
 * @param getNewGameType the new game type of the player
 */
@NullMarked
public record ClientPlayerChangeGameTypeEvent(PlayerInfo getInfo, GameType getCurrentGameType, GameType getNewGameType) implements RecordEvent {
    public static final EventBus<ClientPlayerChangeGameTypeEvent> BUS = EventBus.create(ClientPlayerChangeGameTypeEvent.class);

    @ApiStatus.Internal
    public ClientPlayerChangeGameTypeEvent {}
}
