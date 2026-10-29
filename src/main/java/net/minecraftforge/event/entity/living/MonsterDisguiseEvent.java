/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.living;

import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.extensions.IForgeItemStack;
import net.minecraftforge.eventbus.api.bus.CancellableEventBus;
import net.minecraftforge.eventbus.api.event.RecordEvent;
import net.minecraftforge.eventbus.api.event.characteristic.Cancellable;

/**
 * This event is fired on the forge bus before an Monster detects that a player is looking at them.
 * It will not be fired if the detection is already prevented by {@link IForgeItemStack#isMonsterDisguise(Player, Monster)}
 * <p>
 * This event is {@linkplain Cancellable cancellable}.
 * If this event is cancelled, the Monster will not target the player.
 *
 * @param getPlayer The player that is being checked.
 */
public record MonsterDisguiseEvent(Monster getEntity, Player getPlayer) implements Cancellable, LivingEvent, RecordEvent {
    public static final CancellableEventBus<MonsterDisguiseEvent> BUS = CancellableEventBus.create(MonsterDisguiseEvent.class);
}
