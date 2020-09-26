package net.minecraftforge.event.entity.living;

import net.minecraft.entity.LivingEntity;

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
