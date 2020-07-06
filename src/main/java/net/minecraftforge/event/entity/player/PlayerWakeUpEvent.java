/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.event.entity.player;

import net.minecraft.entity.player.PlayerEntity;

/**
 * This event is fired when the player is waking up.<br/>
 * This is merely for purposes of listening for this to happen.<br/>
 * There is nothing that can be manipulated with this event.
 */
public class PlayerWakeUpEvent extends PlayerEvent
{
    private final boolean wakeImmediately;

    private final boolean updateWorld;

    public PlayerWakeUpEvent(PlayerEntity player, boolean wakeImmediately, boolean updateWorld)
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
