package net.minecraftforge.event.entity.living;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;

/**
 * LivingSetAttackTargetEvent is fired when an Entity sets a target to attack.<br>
 * This event is fired whenever an Entity sets a target to attack in
 * {@link EntityLiving#setAttackTarget(EntityLivingBase)} and
 * {@link EntityLivingBase#setRevengeTarget(EntityLivingBase)}.<br>
 * <br>
 * This event is fired via the {@link net.minecraftforge.common.ForgeHooks#onLivingSetAttackTarget(EntityLivingBase, EntityLivingBase)}.<br>
 * <br>
 * {@link #target} contains the newly targeted Entity.<br>
 * <br>
 * This event is not {@link net.minecraftforge.fml.common.eventhandler.Cancelable}.<br>
 * <br>
 * This event does not have a result. {@link net.minecraftforge.fml.common.eventhandler.Event.HasResult}<br>
 * <br>
 * This event is fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.
 **/
public class LivingSetAttackTargetEvent extends LivingEvent
{

    private final EntityLivingBase target;
    public LivingSetAttackTargetEvent(EntityLivingBase entity, EntityLivingBase target)
    {
        super(entity);
        this.target = target;
    }

    public EntityLivingBase getTarget()
    {
        return target;
    }
}
