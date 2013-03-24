package net.minecraftforge.event.entity.living;

import net.minecraft.entity.EntityLiving;
import net.minecraftforge.event.Cancelable;

@Cancelable
public class LivingMoveEvent extends LivingEvent
{
    public float forward;
    public float strafe;
    public LivingMoveEvent(EntityLiving entity, float forward, float strafe)
    {
        super(entity);
        this.forward = forward;
        this.strafe = strafe;
    }
}
