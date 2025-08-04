/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.world.level.GameType;
import net.minecraftforge.common.util.HasResult;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.RecordEvent;
import net.minecraftforge.eventbus.api.event.characteristic.Cancellable;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;

/**
 * Fired when the client player is notified of a change of {@link GameType} from the server.
 *
 * <p>This event is not {@linkplain Cancellable cancellable}, and does not {@linkplain HasResult have a result}.</p>
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
