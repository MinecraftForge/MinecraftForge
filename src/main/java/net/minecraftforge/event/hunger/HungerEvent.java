/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.hunger;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.Event;

/**
 * Base class for all HungerEvent events.
 *
 * All children of this event are fired on the {@link MinecraftForge#EVENT_BUS}.
 */
public abstract class HungerEvent extends Event
{
    private final Player player;

    public HungerEvent(Player player)
    {
        this.player = player;
    }

    /**
     * @return The player for which this HungerEvent pertains to.
     */
    public Player getPlayer()
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

        public GetMaxHunger(Player player)
        {
            super(player);
            this.maxHunger = 20;
        }

        /**
         * @return The maximum amount of hunger/nutrition (and by proxy, saturation) this player can have.
         */
        public int getMaxHunger()
        {
            return maxHunger;
        }

        /**
         * @param maxHunger The new maximum amount of hunger/nutrition (and by proxy, saturation) this player can have.
         */
        public void setMaxHunger(int maxHunger)
        {
            this.maxHunger = maxHunger;
        }
    }
}
