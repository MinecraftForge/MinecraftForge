package net.minecraftforge.event.entity.living;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent.ILivingTargetType;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event.HasResult;

/**
 * This event allows you to change the target an entity has. <br>
 * This event is fired before {@link LivingSetAttackTargetEvent}. <br>
 * <br>
 * This event is fired via the {@link ForgeHooks#}
 * This event is {@link net.minecraftforge.eventbus.api.Cancelable}.<br>
 * <br>
 * If you cancel this event, the target will not be changed and it will stay the same.
 * Cancelling this event will prevent {@link LivingSetAttackTargetEvent} from being posted.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 */
@Cancelable
public class LivingChangeTargetEvent extends LivingEvent
{
    private final ILivingTargetType targetType;
    private final LivingEntity originalTarget;
    private LivingEntity newTarget;
    
    public LivingChangeTargetEvent(LivingEntity entity, LivingEntity originalTarget, ILivingTargetType targetType)
    {
        super(entity);
        this.originalTarget = originalTarget;
        this.newTarget = originalTarget;
        this.targetType = targetType;
    }

    /**
     * {@return the new target of this entity.}
     */
    public LivingEntity getNewTarget()
    {
        return newTarget;
    }

    /**
     * Sets the new target this entity shall have.
     * @param newTarget The new target of this entity.
     */
    public void setNewTarget(LivingEntity newTarget)
    {
        this.newTarget = newTarget;
    }
    
    /**
     * {@return the living target type.}
     */
    public ILivingTargetType getTargetType()
    {
        return targetType;
    }

    /**
     * {@return the original entity MC intended to use as a target before firing this event.}
     */
    public LivingEntity getOriginalTarget()
    {
        return originalTarget;
    }
}
