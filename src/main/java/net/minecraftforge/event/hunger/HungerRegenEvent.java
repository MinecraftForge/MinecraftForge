/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.hunger;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * Base class for all HealthRegenEvent events.
 *
 * All children of this event are fired on the {@link MinecraftForge#EVENT_BUS}.
 */
public abstract class HungerRegenEvent extends Event
{
    private final Player player;

    public HungerRegenEvent(Player player)
    {
        this.player = player;
    }

    /**
     * @return The player for which this HungerRegenEvent pertains to.
     */
    public Player getPlayer()
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

        public PeacefulRegen(Player player)
        {
            super(player);
            this.deltaHunger = 1;
        }

        /**
         * @return The amount of hunger/nutrition to be added.
         */
        public int getDeltaHunger()
        {
            return deltaHunger;
        }

        /**
         * @param deltaHunger The new amount of hunger/nutrition to be added.
         */
        public void setDeltaHunger(int deltaHunger)
        {
            this.deltaHunger = deltaHunger;
        }
    }
}
