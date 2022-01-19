/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.hunger;

import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.hunger.ExhaustingAction;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * Base class for all ExhaustionEvent events.
 *
 * All children of this event are fired on the {@link MinecraftForge#EVENT_BUS}.
 */
public abstract class ExhaustionEvent extends Event
{
    private final Player player;

    protected ExhaustionEvent(Player player)
    {
        this.player = player;
    }

    /**
     * @return The player for which this ExhaustionEvent pertains to.
     */
    public Player getPlayer()
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
        public AllowExhaustion(Player player)
        {
            super(player);
        }
    }

    /**
     * Class of actions that cause exhaustion in vanilla Minecraft
     * @see ExhaustionAdded
     * @see ExhaustingAction
     */
    public static class ExhaustingActions
    {
        public static final ExhaustingAction HARVEST_BLOCK = ExhaustingAction.get("harvest_block");
        public static final ExhaustingAction NORMAL_JUMP = ExhaustingAction.get("normal_jump");
        public static final ExhaustingAction SPRINTING_JUMP = ExhaustingAction.get("sprinting_jump");
        public static final ExhaustingAction ATTACK_ENTITY = ExhaustingAction.get("attack_entity");
        public static final ExhaustingAction DAMAGE_TAKEN = ExhaustingAction.get("damage_taken");
        public static final ExhaustingAction EFFECT_HUNGER = ExhaustingAction.get("effect_hunger");
        public static final ExhaustingAction MOVEMENT_SWIM = ExhaustingAction.get("movement_swim");
        public static final ExhaustingAction MOVEMENT_WALK_UNDERWATER = ExhaustingAction.get("movement_walk_underwater");
        public static final ExhaustingAction MOVEMENT_WALK_ONWATER = ExhaustingAction.get("movement_walk_onwater");
        public static final ExhaustingAction MOVEMENT_SPRINT = ExhaustingAction.get("movement_sprint");
        public static final ExhaustingAction MOVEMENT_CROUCH = ExhaustingAction.get("movement_crouch");
        public static final ExhaustingAction MOVEMENT_WALK_ONLAND = ExhaustingAction.get("movement_walk_onland");
    }

    /**
     * Fired each time a {@link #player} does something that changes exhaustion in vanilla Minecraft.
     * This includes actions such as jumping, sprinting, etc.
     * The full list of vanilla sources is in {@link ExhaustingActions}.
     * Modders can create their own sources by calling {@link ExhaustingAction#get(String)}
     *
     * This event is fired whenever {@link Player#causeFoodExhaustion} is called from within Minecraft code.
     *
     * This event is not {@link Cancelable}.
     *
     * This event does not have a result. {@link HasResult}
     */
    public static class ExhaustionAdded extends ExhaustionEvent
    {
        private final ExhaustingAction action;
        private float deltaExhaustion;

        public ExhaustionAdded(Player player, ExhaustingAction action, float deltaExhaustion)
        {
            super(player);
            this.action = action;
            this.deltaExhaustion = deltaExhaustion;
        }

        /**
         * @return The action the player performed in order to warrant potentially adding exhaustion.
         */
        public ExhaustingAction getAction()
        {
            return action;
        }

        /**
         * @return The amount of exhaustion to be added to the player.
         */
        public float getDeltaExhaustion()
        {
            return deltaExhaustion;
        }

        /**
         * @param deltaExhaustion The new amount of exhaustion to be added to the player.
         */
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

        public GetMaxExhaustion(Player player)
        {
            super(player);
            this.maxExhaustionLevel = 4.0F;
        }

        /**
         * @return The maximum amount of exhaustion this player must reach before triggering a hunger/saturation
         *         decrement.
         */
        public float getMaxExhaustionLevel()
        {
            return maxExhaustionLevel;
        }

        /**
         * @param maxExhaustionLevel The new maximum amount of exhaustion this player must reach before triggering a
         *                           hunger/saturation decrement.
         */
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
     * {@link #deltaExhaustion} contains the delta to be applied to exhaustion level (default: -{@link GetMaxExhaustion#maxExhaustionLevel}).
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

        public Exhausted(Player player, float exhaustionToRemove, float currentExhaustionLevel)
        {
            super(player);
            this.currentExhaustionLevel = currentExhaustionLevel;
            this.deltaExhaustion = -exhaustionToRemove;

            boolean shouldDecreaseSaturationLevel = player.getFoodData().getSaturationLevel() > 0.0F;
            this.deltaSaturation = shouldDecreaseSaturationLevel ? -1.0F : 0.0F;

            boolean shouldDecreaseFoodLevel = !shouldDecreaseSaturationLevel && player.level.getDifficulty() != Difficulty.PEACEFUL;
            this.deltaHunger = shouldDecreaseFoodLevel ? -1 : 0;
        }

        /**
         * @return The player's current exhaustion level.
         */
        public float getCurrentExhaustionLevel()
        {
            return currentExhaustionLevel;
        }

        /**
         * @return The amount of exhaustion to be added (or subtracted generally) to the player.
         */
        public float getDeltaExhaustion()
        {
            return deltaExhaustion;
        }

        /**
         * @param deltaExhaustion The new amount of exhaustion to be added (or subtracted) to the player.
         */
        public void setDeltaExhaustion(float deltaExhaustion)
        {
            this.deltaExhaustion = deltaExhaustion;
        }

        /**
         * @return The amount of hunger/nutrition to be added (or subtracted generally) to the player.
         */
        public int getDeltaHunger()
        {
            return deltaHunger;
        }

        /**
         * @param deltaHunger The new amount of hunger/nutrition to be added (or subtracted) to the player.
         */
        public void setDeltaHunger(int deltaHunger)
        {
            this.deltaHunger = deltaHunger;
        }

        /**
         * @return The amount of saturation to be added (or subtracted generally) to the player.
         */
        public float getDeltaSaturation()
        {
            return deltaSaturation;
        }

        /**
         * @param deltaSaturation The new amount of saturation to be added (or subtracted) to the player.
         */
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

        public GetExhaustionCap(Player player)
        {
            super(player);
            this.exhaustionLevelCap = 40.0F;
        }

        /**
         * @return The maximum amount of exhaustion that can be added to the player at one time (in one action).
         */
        public float getExhaustionLevelCap()
        {
            return exhaustionLevelCap;
        }

        /**
         * @param exhaustionLevelCap The new maximum amount of exhaustion that can be added to the player at on time.
         */
        public void setExhaustionLevelCap(float exhaustionLevelCap)
        {
            this.exhaustionLevelCap = exhaustionLevelCap;
        }
    }
}
