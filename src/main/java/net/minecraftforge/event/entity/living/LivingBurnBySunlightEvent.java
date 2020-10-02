package net.minecraftforge.event.entity.living;

import net.minecraft.entity.LivingEntity;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * LivingBurnBySunlightEvent is triggered when an entity is trying
 * to burn (caused by sunlight) during {@link LivingEntity#livingTick()}
 * through {@link net.minecraft.entity.Entity#setFire(int)}
 *
 * This event is {@link Cancelable}
 * If cancelled, there will be no further action, whereas
 * {@link net.minecraft.entity.Entity#setFire(int)} will not be called
 */
@Cancelable
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
