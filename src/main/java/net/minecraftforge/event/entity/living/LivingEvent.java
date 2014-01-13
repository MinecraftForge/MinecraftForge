package net.minecraftforge.event.entity.living;

import cpw.mods.fml.common.eventhandler.Cancelable;
import net.minecraft.entity.EntityLivingBase;
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
    
    /**
     * An event that fires whenever an EntityLivingBase or
     * anything deriving from it start/stops sprinting
     * 
     * Canceling this event will stop the entity
     * from changing its sprinting state.
     */
    public static class LivingSprintEvent extends LivingEvent
    {
        public final boolean isSprinting;
        public LivingSprintEvent(EntityLivingBase entity, boolean isSprinting)
        {
            super(entity);
            this.isSprinting = isSprinting;
        }
        
    }
}
