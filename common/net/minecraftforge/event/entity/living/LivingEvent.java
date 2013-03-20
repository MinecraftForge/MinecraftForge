package net.minecraftforge.event.entity.living;

import net.minecraft.entity.EntityLiving;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.entity.EntityEvent;

public class LivingEvent extends EntityEvent
{
    public final EntityLiving entityLiving;
    public LivingEvent(EntityLiving entity)
    {
        super(entity);
        entityLiving = entity;
    }
    
    @Cancelable
    public static class LivingUpdateEvent extends LivingEvent
    {
        public LivingUpdateEvent(EntityLiving e){ super(e); }
    }

    public static class LivingJumpEvent extends LivingEvent
    {
        public LivingJumpEvent(EntityLiving e){ super(e); }
    }
}
