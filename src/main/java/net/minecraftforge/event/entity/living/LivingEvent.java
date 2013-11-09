package net.minecraftforge.event.entity.living;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.entity.EntityEvent;

public class LivingEvent extends EntityEvent
{
    public final EntityLivingBase entityLiving;
    public LivingEvent(EntityLivingBase entity)
    {
        super(entity);
        entityLiving = entity;
    }
    
    @Cancelable
    public static class LivingUpdateEvent extends LivingEvent
    {
        public LivingUpdateEvent(EntityLivingBase e){ super(e); }
    }

    public static class LivingJumpEvent extends LivingEvent
    {
        public LivingJumpEvent(EntityLivingBase e){ super(e); }
    }
}
