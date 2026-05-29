/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.event.entity.living;

import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraftsingularity.common.extensions.IForgeItem;
import net.minecraftsingularity.eventbus.api.bus.CancellableEventBus;
import net.minecraftsingularity.eventbus.api.event.RecordEvent;
import net.minecraftsingularity.eventbus.api.event.characteristic.Cancellable;

/**
 * This event is fired on the singularity bus before an Monster detects that a player is looking at them.
 * It will not be fired if the detection is already prevented by {@link IForgeItem#isEnderMask}
 * <p>
 * This event is {@linkplain Cancellable cancellable}.
 * If this event is cancelled, the Monster will not target the player.
 *
 * @param getPlayer The player that is being checked.
 */
public record MonsterDisguiseEvent(Monster getEntity, Player getPlayer) implements Cancellable, LivingEvent, RecordEvent {
    public static final CancellableEventBus<MonsterDisguiseEvent> BUS = CancellableEventBus.create(MonsterDisguiseEvent.class);
}
