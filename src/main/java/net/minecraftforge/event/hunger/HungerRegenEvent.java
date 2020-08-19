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
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * Base class for all HealthRegenEvent events.
 *
 * All children of this event are fired on the {@link MinecraftForge#EVENT_BUS}.
 */
public abstract class HungerRegenEvent extends Event
{
    private final PlayerEntity player;

    public HungerRegenEvent(PlayerEntity player)
    {
        this.player = player;
    }

    public PlayerEntity getPlayer()
    {
        return player;
    }

    /**
     * Fired twice every second for each player while in Peaceful difficulty,
     * in order to control how much hunger to passively regenerate.
     *
     * This event is never fired if the game rule "naturalRegeneration" is false.
     *
     * {@link #deltaHunger} contains the delta to be applied to hunger.
     *
     * This event is {@link Cancelable}.
     * If this event is canceled, it will skip adding hunger to the player.
     *
     * This event does not have a {@link Result}. {@link HasResult}
     */
    @Cancelable
    public static class PeacefulRegen extends HungerRegenEvent
    {
        private int deltaHunger;

        public PeacefulRegen(PlayerEntity player)
        {
            super(player);
            this.deltaHunger = 1;
        }

        public int getDeltaHunger()
        {
            return deltaHunger;
        }

        public void setDeltaHunger(int deltaHunger)
        {
            this.deltaHunger = deltaHunger;
        }
    }
}
