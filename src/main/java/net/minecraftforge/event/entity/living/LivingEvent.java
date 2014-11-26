package net.minecraftforge.event.entity.living;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.event.entity.EntityEvent;

/**
 * LivingEvent is fired whenever an event involving Living entities occurs.<br>
 * If a method utilizes this {@link Event} as its parameter, the method will 
 * receive every child event of this class.<br>
 * <br>
 * All children of this event are fired on the {@link MinecraftForge#EVENT_BUS}.<br>
 **/
public class LivingEvent extends EntityEvent
{
    public final EntityLivingBase entityLiving;
    public LivingEvent(EntityLivingBase entity)
    {
        super(entity);
        entityLiving = entity;
    }
    
    /**
     * LivingUpdateEvent is fired when an Entity is updated. <br>
     * This event is fired whenever an Entity is updated in 
     * EntityLivingBase#onUpdate(). <br>
     * <br>
     * This event is fired via the {@link ForgeHooks#onLivingUpdate(EntityLivingBase)}.<br>
     * <br>
     * This event is {@link Cancelable}.<br>
     * If this event is canceled, the Entity does not update.<br>
     * <br>
     * This event does not have a result. {@link HasResult}<br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
     **/
    @Cancelable
    public static class LivingUpdateEvent extends LivingEvent
    {
        public LivingUpdateEvent(EntityLivingBase e){ super(e); }
    }
    
    /**
     * LivingJumpEvent is fired when an Entity jumps.<br>
     * This event is fired whenever an Entity jumps in 
     * EntityLivingBase#jump(), EntityMagmaCube#jump(),
     * and EntityHorse#jump().<br>
     * <br>
     * This event is fired via the {@link ForgeHooks#onLivingJump(EntityLivingBase)}.<br>
     * <br>
     * This event is not {@link Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult}<br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
     **/
    public static class LivingJumpEvent extends LivingEvent
    {
        public LivingJumpEvent(EntityLivingBase e){ super(e); }
    }
}
