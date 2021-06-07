package net.minecraftforge.event.entity.living;

import net.minecraft.world.entity.LivingEntity;

public class LivingBreatheEvent extends LivingEvent
{
    private boolean canBreathe;
    public LivingBreatheEvent(LivingEntity entity, boolean canBreathe)
    {
        super(entity);
        this.canBreathe = canBreathe;
    }

    public boolean isCanBreathe()
    {
        return canBreathe;
    }

    public void setCanBreathe(boolean canBreathe)
    {
        this.canBreathe = canBreathe;
    }
}
