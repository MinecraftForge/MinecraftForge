/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.living;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.StartAttacking;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent.ILivingTargetType;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent.LivingTargetType;

/**
 * This event is deprecated. Use {@link LivingChangeTargetEvent} instead.<br>
 * <br>
 * LivingSetAttackTargetEvent is fired when an Entity sets a target to attack.<br>
 * This event is fired whenever an Entity sets a target to attack in
 * {@link Mob#setTarget(LivingEntity)} or {@link StartAttacking#setAttackTarget(Mob, LivingEntity)}<br>
 * <br>
 * This event is fired via the {@link ForgeHooks#onLivingSetAttackTarget(LivingEntity, LivingEntity)}.<br>
 * <br>
 * {@link #getTarget} returns the newly targeted Entity.<br>
 * {@link #getTargetType()} returns the target type that caused the change of targets.<br>
 * <br>
 * This event is not {@link net.minecraftforge.eventbus.api.Cancelable}.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
// TODO: Remove in 1.20
@Deprecated(since = "1.19.2", forRemoval = true)
public class LivingSetAttackTargetEvent extends LivingEvent
{
    private final ILivingTargetType targetType;
    private final LivingEntity originalTarget;
    
    public LivingSetAttackTargetEvent(LivingEntity entity, LivingEntity target)
    {
        super(entity);
        this.targetType = LivingTargetType.MOB_TARGET;        
        this.originalTarget = target;
    }
    
    public LivingSetAttackTargetEvent(LivingEntity entity, LivingEntity target, ILivingTargetType targetType)
    {
        super(entity);
        this.targetType = targetType;
        this.originalTarget = target;
    }

    /**
     * {@return the target this living entity had after changing its target to a new
     * target, but before posting this event.}
     */
    public LivingEntity getTarget()
    {
        return originalTarget;
    }
    
    /**
     * {@return the target type of this event.}
     */
    public ILivingTargetType getTargetType()
    {
        return targetType;
    }
}
