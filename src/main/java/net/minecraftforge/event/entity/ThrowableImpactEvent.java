package net.minecraftforge.event.entity;

import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * This event is fired before an {@link EntityThrowable} calls its {@link EntityThrowable#onImpact} method.
 * This event is fired via {@link net.minecraftforge.common.ForgeHooks#onThrowableImpact}.
 * This event is cancelable. When canceled, {@link EntityThrowable#onImpact} will not be called.
 * Killing or other handling of the entity after event cancellation is up to the modder.
 * This event is fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.
 *
 * @deprecated use {@link ProjectileImpactEvent.Throwable}
 */
@Deprecated // TODO: remove (1.13)
@Cancelable
public class ThrowableImpactEvent extends EntityEvent
{
    private final EntityThrowable throwable;
    private final RayTraceResult ray;

    public ThrowableImpactEvent(EntityThrowable throwable, RayTraceResult ray)
    {
        super(throwable);
        this.throwable = throwable;
        this.ray = ray;
    }

    public EntityThrowable getEntityThrowable()
    {
        return throwable;
    }

    public RayTraceResult getRayTraceResult()
    {
        return ray;
    }

}
