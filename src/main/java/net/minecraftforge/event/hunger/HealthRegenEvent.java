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
public abstract class HealthRegenEvent extends Event
{
    private final PlayerEntity player;

    protected HealthRegenEvent(PlayerEntity player)
    {
        this.player = player;
    }

    public PlayerEntity getPlayer()
    {
        return player;
    }

    /**
     * Fired each FoodStats update to determine whether or not health regen from food is allowed for the {@link #player}.
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
        public AllowRegen(PlayerEntity player)
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

        public GetRegenTickPeriod(PlayerEntity player)
        {
            super(player);
            this.regenTickPeriod = 80;
        }

        public int getRegenTickPeriod()
        {
            return regenTickPeriod;
        }

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

        public Regen(PlayerEntity player)
        {
            super(player);
            this.deltaHealth = 1.0F;
            this.deltaExhaustion = 6.0F;
        }

        public float getDeltaHealth()
        {
            return deltaHealth;
        }

        public void setDeltaHealth(float deltaHealth)
        {
            this.deltaHealth = deltaHealth;
        }

        public float getDeltaExhaustion()
        {
            return deltaExhaustion;
        }

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

        public PeacefulRegen(PlayerEntity player)
        {
            super(player);
            this.deltaHealth = 1.0F;
        }

        public float getDeltaHealth()
        {
            return deltaHealth;
        }

        public void setDeltaHealth(float deltaHealth)
        {
            this.deltaHealth = deltaHealth;
        }
    }

    /**
     * Fired each FoodStats update to determine whether or health regen from full hunger + saturation is allowed for the {@link #player}.
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
        public AllowSaturatedRegen(PlayerEntity player)
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

        public GetSaturatedRegenTickPeriod(PlayerEntity player)
        {
            super(player);
            this.regenTickPeriod = 10;
        }

        public int getRegenTickPeriod()
        {
            return regenTickPeriod;
        }

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

        public SaturatedRegen(PlayerEntity player)
        {
            super(player);
            this.deltaExhaustion = Math.min(player.getFoodData().getSaturationLevel(), 6.0F);
            this.deltaHealth = deltaExhaustion / 6.0F;
        }

        public float getDeltaHealth()
        {
            return deltaHealth;
        }

        public void setDeltaHealth(float deltaHealth)
        {
            this.deltaHealth = deltaHealth;
        }

        public float getDeltaExhaustion()
        {
            return deltaExhaustion;
        }

        public void setDeltaExhaustion(float deltaExhaustion)
        {
            this.deltaExhaustion = deltaExhaustion;
        }
    }
}
