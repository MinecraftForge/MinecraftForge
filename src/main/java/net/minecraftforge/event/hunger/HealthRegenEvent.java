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
public abstract class HealthRegenEvent extends Event
{
    private final Player player;

    protected HealthRegenEvent(Player player)
    {
        this.player = player;
    }

    /**
     * @return The player for which this HealthRegenEvent pertains to.
     */
    public Player getPlayer()
    {
        return player;
    }

    /**
     * Fired each FoodData update to determine whether or not health regen from food is allowed for the {@link #player}.
     * However, this event will not be fired if saturated regen occurs, as saturated health regen will take precedence
     * over normal health regen (see {@link AllowSaturatedRegen}).
     *
     * This event is not {@link Cancelable}.
     *
     * This event uses the {@link Result}. {@link HasResult}
     * {@link Result#DEFAULT} will use the vanilla conditionals.
     * {@link Result#ALLOW} will allow regen without condition.
     * {@link Result#DENY} will deny regen without condition.
     */
    @HasResult
    public static class AllowRegen extends HealthRegenEvent
    {
        public AllowRegen(Player player)
        {
            super(player);
        }
    }

    /**
     * Fired every time the regen tick period is retrieved to allow control over its value.
     *
     * {@link #regenTickPeriod} contains the number of ticks between each regen.
     *
     * This event is not {@link Cancelable}.
     *
     * This event does not have a {@link Result}. {@link HasResult}
     */
    public static class GetRegenTickPeriod extends HealthRegenEvent
    {
        private int regenTickPeriod;

        public GetRegenTickPeriod(Player player)
        {
            super(player);
            this.regenTickPeriod = 80;
        }

        /**
         * @return The amount of time, in ticks, between unsaturated health regen events.
         */
        public int getRegenTickPeriod()
        {
            return regenTickPeriod;
        }

        /**
         * @param regenTickPeriod The new amount of time, in ticks, between unsaturated health regen events.
         */
        public void setRegenTickPeriod(int regenTickPeriod)
        {
            this.regenTickPeriod = regenTickPeriod;
        }
    }

    /**
     * Fired once the ticks since last regen reaches regenTickPeriod (see {@link GetRegenTickPeriod}),
     * in order to control how regen affects health/exhaustion.
     *
     * {@link #deltaHealth} contains the delta to be applied to health.
     * {@link #deltaExhaustion} contains the delta to be applied to exhaustion level.
     *
     * This event is {@link Cancelable}.
     * If this event is canceled, it will skip applying the delta values to health and exhaustion.
     *
     * This event does not have a {@link Result}. {@link HasResult}
     */
    @Cancelable
    public static class Regen extends HealthRegenEvent
    {
        private float deltaHealth;
        private float deltaExhaustion;

        public Regen(Player player)
        {
            super(player);
            this.deltaHealth = 1.0F;
            this.deltaExhaustion = 6.0F;
        }

        /**
         * @return The amount of health to be restored.
         */
        public float getDeltaHealth()
        {
            return deltaHealth;
        }

        /**
         * @param deltaHealth The new amount of health to be restored.
         */
        public void setDeltaHealth(float deltaHealth)
        {
            this.deltaHealth = deltaHealth;
        }

        /**
         * @return The amount of exhaustion to be added as part of this regen operation.
         */
        public float getDeltaExhaustion()
        {
            return deltaExhaustion;
        }

        /**
         * @param deltaExhaustion The new amount of exhaustion to be added as part of this regen operation.
         */
        public void setDeltaExhaustion(float deltaExhaustion)
        {
            this.deltaExhaustion = deltaExhaustion;
        }
    }

    /**
     * Fired every second for each player while in Peaceful difficulty,
     * in order to control how much health to passively regenerate.
     *
     * This event is never fired if the game rule "naturalRegeneration" is false.
     *
     * {@link #deltaHealth} contains the delta to be applied to health.
     *
     * This event is {@link Cancelable}.
     * If this event is canceled, it will skip healing the player.
     *
     * This event does not have a {@link Result}. {@link HasResult}
     */
    @Cancelable
    public static class PeacefulRegen extends HealthRegenEvent
    {
        private float deltaHealth;

        public PeacefulRegen(Player player)
        {
            super(player);
            this.deltaHealth = 1.0F;
        }

        /**
         * @return The amount of health to be restored.
         */
        public float getDeltaHealth()
        {
            return deltaHealth;
        }

        /**
         * @param deltaHealth The new amount of health to be restored.
         */
        public void setDeltaHealth(float deltaHealth)
        {
            this.deltaHealth = deltaHealth;
        }
    }

    /**
     * Fired each FoodData update to determine whether or health regen from full hunger + saturation is allowed for the {@link #player}.
     *
     * Saturated health regen will take precedence over normal health regen.
     *
     * This event is not {@link Cancelable}.
     *
     * This event uses the {@link Result}. {@link HasResult}
     * {@link Result#DEFAULT} will use the vanilla conditionals.
     * {@link Result#ALLOW} will allow regen without condition.
     * {@link Result#DENY} will deny regen without condition.
     */
    @HasResult
    public static class AllowSaturatedRegen extends HealthRegenEvent
    {
        public AllowSaturatedRegen(Player player)
        {
            super(player);
        }
    }

    /**
     * Fired every time the saturated regen tick period is retrieved to allow control over its value.
     *
     * {@link #regenTickPeriod} contains the number of ticks between each saturated regen.
     *
     * This event is not {@link Cancelable}.
     *
     * This event does not have a {@link Result}. {@link HasResult}
     */
    public static class GetSaturatedRegenTickPeriod extends HealthRegenEvent
    {
        private int regenTickPeriod;

        public GetSaturatedRegenTickPeriod(Player player)
        {
            super(player);
            this.regenTickPeriod = 10;
        }

        /**
         * @return The amount of time, in ticks, between saturated health regen events.
         */
        public int getRegenTickPeriod()
        {
            return regenTickPeriod;
        }

        /**
         * @param regenTickPeriod The new amount of time, in ticks, between saturated health regen events.
         */
        public void setRegenTickPeriod(int regenTickPeriod)
        {
            this.regenTickPeriod = regenTickPeriod;
        }
    }

    /**
     * Fired once the ticks since last regen reaches regenTickPeriod (see {@link GetSaturatedRegenTickPeriod}),
     * in order to control how regen affects health/exhaustion.
     *
     * By default, the amount of health restored depends on the player's current saturation level.
     *
     * {@link #deltaHealth} contains the delta to be applied to health.
     * {@link #deltaExhaustion} contains the delta to be applied to exhaustion level.
     *
     * This event is {@link Cancelable}.
     * If this event is canceled, it will skip applying the delta values to health and exhaustion.
     *
     * This event does not have a {@link Result}. {@link HasResult}
     */
    @Cancelable
    public static class SaturatedRegen extends HealthRegenEvent
    {
        private float deltaHealth;
        private float deltaExhaustion;

        public SaturatedRegen(Player player)
        {
            super(player);
            this.deltaExhaustion = Math.min(player.getFoodData().getSaturationLevel(), 6.0F);
            this.deltaHealth = deltaExhaustion / 6.0F;
        }

        /**
         * @return The amount of health to be restored.
         */
        public float getDeltaHealth()
        {
            return deltaHealth;
        }

        /**
         * @param deltaHealth The new amount of health to be restored.
         */
        public void setDeltaHealth(float deltaHealth)
        {
            this.deltaHealth = deltaHealth;
        }

        /**
         * @return The amount of exhaustion to be added as part of this regen operation.
         */
        public float getDeltaExhaustion()
        {
            return deltaExhaustion;
        }

        /**
         * @param deltaExhaustion The new amount of exhaustion to be added as part of this regen operation.
         */
        public void setDeltaExhaustion(float deltaExhaustion)
        {
            this.deltaExhaustion = deltaExhaustion;
        }
    }
}
