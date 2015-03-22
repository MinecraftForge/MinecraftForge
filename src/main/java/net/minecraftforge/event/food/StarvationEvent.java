package net.minecraftforge.event.food;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Base class for all StarvationEvent events.<br>
 * <br>
 * All children of this event are fired on the {@link MinecraftForge#EVENT_BUS}.
 */
public abstract class StarvationEvent extends Event
{
    public final EntityPlayer player;

    public StarvationEvent(EntityPlayer player)
    {
        this.player = player;
    }

    /**
     * Fired each FoodStats update to determine whether or not starvation is allowed for the {@link #player}.
     *
     * This event is fired in {@link FoodStats#onUpdate}.<br>
     * <br>
     * This event is not {@link Cancelable}.<br>
     * <br>
     * This event uses the {@link Result}. {@link HasResult}<br>
     * {@link Result#DEFAULT} will use the vanilla conditionals.
     * {@link Result#ALLOW} will allow starvation without condition.
     * {@link Result#DENY} will deny starvation without condition.
     */
    @HasResult
    public static class AllowStarvation extends StarvationEvent
    {
        public AllowStarvation(EntityPlayer player)
        {
            super(player);
        }
    }

    /**
     * Fired every time the starve tick period is retrieved to allow control over its value.
     *
     * This event is fired in {@link FoodStats#onUpdate}.<br>
     * <br>
     * {@link #starveTickPeriod} contains the number of ticks between starvation damage being done.<br>
     * <br>
     * This event is not {@link Cancelable}.<br>
     * <br>
     * This event does not have a {@link Result}. {@link HasResult}<br>
     */
    public static class GetStarveTickPeriod extends StarvationEvent
    {
        public int starveTickPeriod = 80;

        public GetStarveTickPeriod(EntityPlayer player)
        {
            super(player);
        }
    }

    /**
     * Fired once the time since last starvation damage reaches starveTickPeriod (see {@link GetStarveTickPeriod}),
     * in order to control how much starvation damage to do.
     *
     * This event is fired in {@link FoodStats#onUpdate}.<br>
     * <br>
     * {@link #starveDamage} contains the amount of damage to deal from starvation.<br>
     * <br>
     * This event is {@link Cancelable}.<br>
     * If this event is canceled, it will skip dealing starvation damage.<br>
     * <br>
     * This event does not have a {@link Result}. {@link HasResult}<br>
     */
    @Cancelable
    public static class Starve extends StarvationEvent
    {
        public float starveDamage = 1f;

        public Starve(EntityPlayer player)
        {
            super(player);

            EnumDifficulty difficulty = player.worldObj.getDifficulty();
            boolean shouldDoDamage = player.getHealth() > 10.0F || difficulty == EnumDifficulty.HARD || player.getHealth() > 1.0F && difficulty == EnumDifficulty.NORMAL;

            if (!shouldDoDamage)
                starveDamage = 0f;
        }
    }
}
