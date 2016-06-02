package net.minecraftforge.event.entity.living;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraftforge.event.entity.EntityEvent;

/**
 * LivingEvent is fired whenever an event involving Living entities occurs.<br>
 * If a method utilizes this {@link net.minecraftforge.fml.common.eventhandler.Event} as its parameter, the method will
 * receive every child event of this class.<br>
 * <br>
 * All children of this event are fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.<br>
 **/
public class LivingEvent extends EntityEvent
{
    private final EntityLivingBase entityLiving;
    public LivingEvent(EntityLivingBase entity)
    {
        super(entity);
        entityLiving = entity;
    }

    public EntityLivingBase getEntityLiving()
    {
        return entityLiving;
    }

    /**
     * LivingUpdateEvent is fired when an Entity is updated. <br>
     * This event is fired whenever an Entity is updated in 
     * {@link EntityLivingBase#onUpdate()}. <br>
     * <br>
     * This event is fired via the {@link net.minecraftforge.common.ForgeHooks#onLivingUpdate(EntityLivingBase)}.<br>
     * <br>
     * This event is {@link net.minecraftforge.fml.common.eventhandler.Cancelable}.<br>
     * If this event is canceled, the Entity does not update.<br>
     * <br>
     * This event does not have a result. {@link net.minecraftforge.fml.common.eventhandler.Event.HasResult}<br>
     * <br>
     * This event is fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.
     **/
    @Cancelable
    public static class LivingUpdateEvent extends LivingEvent
    {
        public LivingUpdateEvent(EntityLivingBase e){ super(e); }
    }
    
    /**
     * LivingJumpEvent is fired when an Entity jumps.<br>
     * This event is fired whenever an Entity jumps in 
     * {@link EntityLivingBase#jump()}, {@link EntityMagmaCube#jump()},
     * and {@link EntityHorse#jump()}.<br>
     * <br>
     * This event is fired via the {@link net.minecraftforge.common.ForgeHooks#onLivingJump(EntityLivingBase)}.<br>
     * <br>
     * This event is not {@link net.minecraftforge.fml.common.eventhandler.Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link net.minecraftforge.fml.common.eventhandler.Event.HasResult}<br>
     * <br>
     * This event is fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.
     **/
    public static class LivingJumpEvent extends LivingEvent
    {
        public LivingJumpEvent(EntityLivingBase e){ super(e); }
    }
}
