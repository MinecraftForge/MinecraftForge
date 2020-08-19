/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

package net.minecraftforge.event.hunger;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.eventbus.api.Event;

/**
 * Base class for all HungerEvent events.
 *
 * All children of this event are fired on the {@link MinecraftForge#EVENT_BUS}.
 */
public abstract class HungerEvent extends Event
{
    private final PlayerEntity player;

    public HungerEvent(PlayerEntity player)
    {
        this.player = player;
    }

    public PlayerEntity getPlayer()
    {
        return player;
    }

    /**
     * Fired every time max hunger level is retrieved to allow control over its value.
     *
     * Note: This also affects max saturation, as saturation is bounded by the player's
     * current hunger level (that is, a max of 40 hunger would also mean a max of 40
     * saturation).
     *
     * {@link #maxHunger} contains the max hunger of the player.
     * {@link #player} contains the player.
     *
     * This event is not {@link Cancelable}.
     *
     * This event does not have a result. {@link HasResult}
     */
    public static class GetMaxHunger extends HungerEvent
    {
        private int maxHunger;

        public GetMaxHunger(PlayerEntity player)
        {
            super(player);
            this.maxHunger = 20;
        }

        public int getMaxHunger()
        {
            return maxHunger;
        }

        public void setMaxHunger(int maxHunger)
        {
            this.maxHunger = maxHunger;
        }
    }
}
