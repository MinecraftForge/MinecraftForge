/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.hunger;

import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * Base class for all StarvationEvent events.
 *
 * All children of this event are fired on the {@link MinecraftForge#EVENT_BUS}.
 */
public abstract class StarvationEvent extends Event
{
    private final Player player;

    public StarvationEvent(Player player)
    {
        this.player = player;
    }

    /**
     * @return The player for which this StarvationEvent pertains to.
     */
    public Player getPlayer()
    {
        return player;
    }

    /**
     * Fired each FoodData update to determine whether or not starvation is allowed for the {@link #player}.
     *
     * This event is not {@link Cancelable}.
     *
     * This event uses the {@link Result}. {@link HasResult}
     * {@link Result#DEFAULT} will use the vanilla conditionals.
     * {@link Result#ALLOW} will allow starvation without condition.
     * {@link Result#DENY} will deny starvation without condition.
     */
    @HasResult
    public static class AllowStarvation extends StarvationEvent
    {
        public AllowStarvation(Player player)
        {
            super(player);
        }
    }

    /**
     * Fired every time the starve tick period is retrieved to allow control over its value.
     *
     * {@link #starveTickPeriod} contains the number of ticks between starvation damage being done.
     *
     * This event is not {@link Cancelable}.
     *
     * This event does not have a {@link Result}. {@link HasResult}
     */
    public static class GetStarveTickPeriod extends StarvationEvent
    {
        private int starveTickPeriod;

        public GetStarveTickPeriod(Player player)
        {
            super(player);
            this.starveTickPeriod = 80;
        }

        /**
         * @return The amount of time, in ticks, between starvation damage events.
         */
        public int getStarveTickPeriod()
        {
            return starveTickPeriod;
        }

        /**
         * @param starveTickPeriod The new amount of time, in ticks, between starvation damage events.
         */
        public void setStarveTickPeriod(int starveTickPeriod)
        {
            this.starveTickPeriod = starveTickPeriod;
        }
    }

    /**
     * Fired once the time since last starvation damage reaches starveTickPeriod (see {@link GetStarveTickPeriod}),
     * in order to control how much starvation damage to do.
     *
     * {@link #starveDamage} contains the amount of damage to deal from starvation.
     *
     * This event is {@link Cancelable}.
     * If this event is canceled, it will skip dealing starvation damage.
     *
     * This event does not have a {@link Result}. {@link HasResult}
     */
    @Cancelable
    public static class Starve extends StarvationEvent
    {
        private float starveDamage;

        public Starve(Player player)
        {
            super(player);

            Difficulty difficulty = player.level.getDifficulty();
            boolean shouldDoDamage = player.getHealth() > 10.0F || difficulty == Difficulty.HARD || player.getHealth() > 1.0F && difficulty == Difficulty.NORMAL;
            this.starveDamage = shouldDoDamage ? 1.0F : 0.0F;
        }

        /**
         * @return The amount of damage to be dealt to the player as a result of starvation.
         */
        public float getStarveDamage()
        {
            return starveDamage;
        }

        /**
         * @param starveDamage The new amount of damage to be dealt to the player as a result of starvation.
         */
        public void setStarveDamage(float starveDamage)
        {
            this.starveDamage = starveDamage;
        }
    }
}
