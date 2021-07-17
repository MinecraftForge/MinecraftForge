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
import net.minecraft.world.Difficulty;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * Base class for all ExhaustionEvent events.
 *
 * All children of this event are fired on the {@link MinecraftForge#EVENT_BUS}.
 */
public abstract class ExhaustionEvent extends Event
{
    private final PlayerEntity player;

    protected ExhaustionEvent(PlayerEntity player)
    {
        this.player = player;
    }

    public PlayerEntity getPlayer()
    {
        return player;
    }

    /**
     * Fired each FoodStats update to determine whether or not exhaustion is allowed for the {@link #player}.
     *
     * This event is not {@link Cancelable}.
     *
     * This event uses the {@link Result}. {@link HasResult}
     * {@link Result#DEFAULT} will use the vanilla conditionals.
     * {@link Result#ALLOW} will allow exhaustion without condition.
     * {@link Result#DENY} will deny exhaustion without condition.
     */
    @HasResult
    public static class AllowExhaustion extends ExhaustionEvent
    {
        public AllowExhaustion(PlayerEntity player)
        {
            super(player);
        }
    }

    /**
     * Enumeration of actions that cause exhaustion in vanilla Minecraft
     * @see ExhaustionAdded
     */
    public enum ExhaustingActions
    {
        HARVEST_BLOCK,
        NORMAL_JUMP,
        SPRINTING_JUMP,
        ATTACK_ENTITY,
        DAMAGE_TAKEN,
        EFFECT_HUNGER,
        MOVEMENT_SWIM,
        MOVEMENT_WALK_UNDERWATER,
        MOVEMENT_WALK_ONWATER,
        MOVEMENT_SPRINT,
        MOVEMENT_CROUCH,
        MOVEMENT_WALK_ONLAND
    }

    /**
     * Fired each time a {@link #player} does something that changes exhaustion in vanilla Minecraft
     * (i.e. jumping, sprinting, etc; see {@link ExhaustingActions} for the full list of possible sources)
     *
     * This event is fired whenever {@link PlayerEntity#causeFoodExhaustion} is called from within Minecraft code.
     *
     * This event is not {@link Cancelable}.
     *
     * This event does not have a result. {@link HasResult}
     */
    public static class ExhaustionAdded extends ExhaustionEvent
    {
        private final ExhaustingActions action;
        private float deltaExhaustion;

        public ExhaustionAdded(PlayerEntity player, ExhaustingActions action, float deltaExhaustion)
        {
            super(player);
            this.action = action;
            this.deltaExhaustion = deltaExhaustion;
        }

        public ExhaustingActions getAction()
        {
            return action;
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
     * Fired every time max exhaustion level is retrieved to allow control over its value.
     *
     * {@link #maxExhaustionLevel} contains the exhaustion level that will trigger a hunger/saturation decrement.
     *
     * This event is not {@link Cancelable}.
     *
     * This event does not have a result. {@link HasResult}
     */
    public static class GetMaxExhaustion extends ExhaustionEvent
    {
        private float maxExhaustionLevel;

        public GetMaxExhaustion(PlayerEntity player)
        {
            super(player);
            this.maxExhaustionLevel = 4.0F;
        }

        public float getMaxExhaustionLevel()
        {
            return maxExhaustionLevel;
        }

        public void setMaxExhaustionLevel(float maxExhaustionLevel)
        {
            this.maxExhaustionLevel = maxExhaustionLevel;
        }
    }

    /**
     * Fired once exhaustionLevel exceeds maxExhaustionLevel (see {@link GetMaxExhaustion}),
     * in order to control how exhaustion affects hunger/saturation.
     *
     * {@link #currentExhaustionLevel} contains the exhaustion level of the {@link #player}.
     * {@link #deltaExhaustion} contains the delta to be applied to exhaustion level (default: {@link GetMaxExhaustion#maxExhaustionLevel}).
     * {@link #deltaHunger} contains the delta to be applied to hunger.
     * {@link #deltaSaturation} contains the delta to be applied to saturation.
     *
     * Note: {@link #deltaHunger} and {@link #deltaSaturation} will vary depending on their vanilla conditionals.
     * For example, deltaHunger will be 0 when this event is fired in Peaceful difficulty.
     *
     * This event is {@link Cancelable}.
     * If this event is canceled, it will skip applying the delta values to hunger and saturation.
     *
     * This event does not have a result. {@link HasResult}
     */
    @Cancelable
    public static class Exhausted extends ExhaustionEvent
    {
        private final float currentExhaustionLevel;
        private float deltaExhaustion;
        private int deltaHunger;
        private float deltaSaturation;

        public Exhausted(PlayerEntity player, float exhaustionToRemove, float currentExhaustionLevel)
        {
            super(player);
            this.currentExhaustionLevel = currentExhaustionLevel;
            this.deltaExhaustion = -exhaustionToRemove;

            boolean shouldDecreaseSaturationLevel = player.getFoodData().getSaturationLevel() > 0.0F;
            this.deltaSaturation = shouldDecreaseSaturationLevel ? -1.0F : 0.0F;

            boolean shouldDecreaseFoodLevel = !shouldDecreaseSaturationLevel && player.level.getDifficulty() != Difficulty.PEACEFUL;
            this.deltaHunger = shouldDecreaseFoodLevel ? -1 : 0;
        }

        public float getCurrentExhaustionLevel()
        {
            return currentExhaustionLevel;
        }

        public float getDeltaExhaustion()
        {
            return deltaExhaustion;
        }

        public void setDeltaExhaustion(float deltaExhaustion)
        {
            this.deltaExhaustion = deltaExhaustion;
        }

        public int getDeltaHunger()
        {
            return deltaHunger;
        }

        public void setDeltaHunger(int deltaHunger)
        {
            this.deltaHunger = deltaHunger;
        }

        public float getDeltaSaturation()
        {
            return deltaSaturation;
        }

        public void setDeltaSaturation(float deltaSaturation)
        {
            this.deltaSaturation = deltaSaturation;
        }
    }

    /**
     * Fired every time the exhaustion level is capped to allow control over the cap.
     *
     * {@link #exhaustionLevelCap} contains the exhaustion level that will be used to cap the exhaustion level.
     *
     * This event is not {@link Cancelable}.
     *
     * This event does not have a result. {@link HasResult}
     */
    public static class GetExhaustionCap extends ExhaustionEvent
    {
        private float exhaustionLevelCap;

        public GetExhaustionCap(PlayerEntity player)
        {
            super(player);
            this.exhaustionLevelCap = 40.0F;
        }

        public float getExhaustionLevelCap()
        {
            return exhaustionLevelCap;
        }

        public void setExhaustionLevelCap(float exhaustionLevelCap)
        {
            this.exhaustionLevelCap = exhaustionLevelCap;
        }
    }
}
