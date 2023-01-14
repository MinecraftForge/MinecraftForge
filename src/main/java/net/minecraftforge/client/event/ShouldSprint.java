/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * This event is fired on client side when player decides to change running state.
 * Canceling this event will prevent player's running state from changing,
 * which can also prevent player from transitioning from running to walking.
 **/
@Cancelable
public class ShouldSprint extends PlayerEvent {
    public final boolean willSprint;

    public ShouldSprint(LocalPlayer player, boolean willSprint) {
        super(player);
        this.willSprint = willSprint;
    }
}
