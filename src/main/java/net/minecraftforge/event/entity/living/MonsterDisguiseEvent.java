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
 *
 * @deprecated This event is scheduled for removal in 26.4 due to a lack of adoption by mods, in order to ease porting.
 *             Please reach out to the Forge team if you are relying on this and would like to see it remain in Forge.
 *             We may keep it or add it back in a future version if there is enough demand.
 */
@Deprecated(forRemoval = true, since = "26.4")
public record MonsterDisguiseEvent(Monster getEntity, Player getPlayer) implements Cancellable, LivingEvent, RecordEvent {
    public static final CancellableEventBus<MonsterDisguiseEvent> BUS = CancellableEventBus.create(MonsterDisguiseEvent.class);
}
