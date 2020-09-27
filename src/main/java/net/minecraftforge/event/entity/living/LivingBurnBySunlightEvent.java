package net.minecraftforge.event.entity.living;

import net.minecraft.entity.LivingEntity;
import net.minecraftforge.eventbus.api.Event;

/**
 * LivingBurnBySunlightEvent is triggered when an entity is trying
 * to burn (caused by sunlight) during {@link LivingEntity#livingTick()}
 * through {@link net.minecraft.entity.Entity#setFire(int)}
 *
 * This event has a result. {@link HasResult}
 * {@link Result#ALLOW} Burn the entity for custom duration
 * {@link Result#DENY} Prevent the entity from burning
 * {@link Result#DEFAULT} Burn the entity for original duration
 */
@Event.HasResult
public class LivingBurnBySunlightEvent extends LivingEvent
{
    private int duration;

    public LivingBurnBySunlightEvent(LivingEntity entity, int duration)
    {
        super(entity);
        this.duration = duration;
    }

    public int getDuration()
    {
        return duration;
    }

    public void setDuration(int duration)
    {
        this.duration = duration;
    }
}
