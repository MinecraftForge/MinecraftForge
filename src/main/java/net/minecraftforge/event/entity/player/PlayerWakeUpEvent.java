/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.player;

import net.minecraft.world.entity.player.Player;

/**
 * This event is fired when the player is waking up.<br/>
 * This is merely for purposes of listening for this to happen.<br/>
 * There is nothing that can be manipulated with this event.
 */
public class PlayerWakeUpEvent extends PlayerEvent
{
    private final boolean wakeImmediately;

    private final boolean updateWorld;

    public PlayerWakeUpEvent(Player player, boolean wakeImmediately, boolean updateWorld)
    {
        super(player);
        this.wakeImmediately = wakeImmediately;
        this.updateWorld = updateWorld;
    }

    /**
     * Used for the 'wake up animation'.
     * This is false if the player is considered 'sleepy' and the overlay should slowly fade away.
     */
    public boolean wakeImmediately() { return wakeImmediately; }

    /**
     * Indicates if the server should be notified of sleeping changes.
     * This will only be false if the server is considered 'up to date' already, because, for example, it initiated the call.
     */
    public boolean updateWorld() { return updateWorld; }
}
