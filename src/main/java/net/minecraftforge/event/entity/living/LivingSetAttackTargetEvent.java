package net.minecraftforge.event.entity.living;

import net.minecraft.entity.EntityLivingBase;

/**
 * LivingSetAttackTargetEvent is fired when an Entity sets a target to attack.<br>
 * This event is fired whenever an Entity sets a target to attack in
 * EntityLiving#setAttackTarget(EntityLivingBase) and
 * EntityLivingBase#setRevengeTarget(EntityLivingBase).<br>
 * <br>
 * This event is fired via the {@link ForgeHooks#onLivingSetAttackTarget(EntityLivingBase, EntityLivingBase)}.<br>
 * <br>
 * {@link #target} contains the newly targeted Entity.<br>
 * <br>
 * This event is not {@link Cancelable}.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
public class LivingSetAttackTargetEvent extends LivingEvent
{

    public final EntityLivingBase target;
    public LivingSetAttackTargetEvent(EntityLivingBase entity, EntityLivingBase target)
    {
        super(entity);
        this.target = target;
    }

}
