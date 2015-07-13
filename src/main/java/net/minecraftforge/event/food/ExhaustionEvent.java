package net.minecraftforge.event.food;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Base class for all ExhaustionEvent events.<br>
 * <br>
 * All children of this event are fired on the {@link MinecraftForge#EVENT_BUS}.
 */
public abstract class ExhaustionEvent extends Event
{
    public final EntityPlayer player;

    public ExhaustionEvent(EntityPlayer player)
    {
        this.player = player;
    }

    /**
     * Fired each FoodStats update to determine whether or not exhaustion is allowed for the {@link #player}.
     *
     * This event is fired in {@link FoodStats#onUpdate}.<br>
     * <br>
     * This event is not {@link Cancelable}.<br>
     * <br>
     * This event uses the {@link Result}. {@link HasResult}<br>
     * {@link Result#DEFAULT} will use the vanilla conditionals.<br>
     * {@link Result#ALLOW} will allow exhaustion without condition.<br>
     * {@link Result#DENY} will deny exhaustion without condition.<br>
     */
    @HasResult
    public static class AllowExhaustion extends ExhaustionEvent
    {
        public AllowExhaustion(EntityPlayer player)
        {
            super(player);
        }
    }

    /**
     * Fired every time max exhaustion level is retrieved to allow control over its value.
     *
     * This event is fired in {@link FoodStats#onUpdate}.<br>
     * <br>
     * {@link #maxExhaustionLevel} contains the exhaustion level that will trigger a hunger/saturation decrement.<br>
     * <br>
     * This event is not {@link Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult}<br>
     */
    public static class GetMaxExhaustion extends ExhaustionEvent
    {
        public float maxExhaustionLevel = 4f;

        public GetMaxExhaustion(EntityPlayer player)
        {
            super(player);
        }
    }

    /**
     * Fired once exchaustionLevel exceeds maxExhaustionLevel (see {@link GetMaxExhaustion}),
     * in order to control how exhaustion affects hunger/saturation.
     *
     * This event is fired in {@link FoodStats#onUpdate}.<br>
     * <br>
     * {@link #currentExhaustionLevel} contains the exhaustion level of the {@link #player}.<br>
     * {@link #deltaExhaustion} contains the delta to be applied to exhaustion level (default: {@link GetMaxExhaustion#maxExhaustionLevel}).<br>
     * {@link #deltaHunger} contains the delta to be applied to hunger.<br>
     * {@link #deltaSaturation} contains the delta to be applied to saturation.<br>
     * <br>
     * Note: {@link #deltaHunger} and {@link #deltaSaturation} will vary depending on their vanilla conditionals.
     * For example, deltaHunger will be 0 when this event is fired in Peaceful difficulty.<br>
     * <br>
     * This event is {@link Cancelable}.<br>
     * If this event is canceled, it will skip applying the delta values to hunger and saturation.<br>
     * <br>
     * This event does not have a result. {@link HasResult}<br>
     */
    @Cancelable
    public static class Exhausted extends ExhaustionEvent
    {
        public final float currentExhaustionLevel;
        public float deltaExhaustion;
        public int deltaHunger = -1;
        public float deltaSaturation = -1f;

        public Exhausted(EntityPlayer player, float exhaustionToRemove, float currentExhaustionLevel)
        {
            super(player);
            this.deltaExhaustion = -exhaustionToRemove;
            this.currentExhaustionLevel = currentExhaustionLevel;

            boolean shouldDecreaseSaturationLevel = player.getFoodStats().getSaturationLevel() > 0f;

            if (!shouldDecreaseSaturationLevel)
                deltaSaturation = 0f;

            EnumDifficulty difficulty = player.worldObj.getDifficulty();
            boolean shouldDecreaseFoodLevel = !shouldDecreaseSaturationLevel && difficulty != EnumDifficulty.PEACEFUL;

            if (!shouldDecreaseFoodLevel)
                deltaHunger = 0;
        }
    }
}