package net.minecraftforge.event.entity.item;

import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.event.entity.EntityEvent;

/**
 * This event is fired before {@link EntityThrowable#onImpact()} is called, inside {@link EntityThrowable#onUpdate()}.
 * 
 * This event is {@link Cancelable}. When canceled, {@link EntityThrowable#onImpact()} is not called.
 * 
 * This event has no result. {@link HasResult}.
 * 
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 *
 */
public class ThrowableImpactEvent extends EntityEvent 
{
    /**
     * The EntityThrowable that is impacting
     */
    public final EntityThrowable throwable;
    
    /**
     * The MovingObjectPosition that is generated from this impact
     */
    public final MovingObjectPosition movingObjectPosition;
    
    /**
     * Creates a new event for an impacting EntityThrowable
     * 
     * @param throwable The EntityThrowable being impacted
     * @param mop The MovingObjectPosition used in impact calculations
     */
    public ThrowableImpactEvent(EntityThrowable throwable, MovingObjectPosition mop)
    {
        super(throwable);
        this.throwable = throwable;
        this.movingObjectPosition = mop;
    }
}
