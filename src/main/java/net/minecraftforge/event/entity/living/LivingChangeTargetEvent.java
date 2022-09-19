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
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * This event allows you to change the target an entity has. <br>
 * This event is fired before {@link LivingSetAttackTargetEvent}. <br>
 * <br>
 * This event is fired via the {@link ForgeHooks#onLivingChangeTarget(LivingEntity, LivingEntity, ILivingTargetType)}<br>
 * <br>
 * {@link #getOriginalTarget()} returns the target that should originally be set.
 * The return value cannot be affected by calling {@link #setNewTarget(LivingEntity)}.<br>
 * {@link #getNewTarget()} returns the new target that this entity will have.
 * The return value can be affected by calling {@link #setNewTarget(LivingEntity)}.<br>
 * {@link #getTargetType()} returns the target type that caused the change of targets.<br>
 * <br>
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
    
    
    /**
     * A living target type indicates what kind of system caused a change of
     * targets. For a list of default target types, take a look at
     * {@link LivingTargetType}.
     */
    public static interface ILivingTargetType
    {
        
    }
    
    /**
     * This enum contains two default living target types.
     */
    public static enum LivingTargetType implements ILivingTargetType
    {
        /**
         * This target type indicates that the target has been set by calling {@link Mob#setTarget(LivingEntity)}.
         */
        MOB_TARGET,
        /**
         * This target type indicates that the target has been set by the {@link StartAttacking} behavior.
         */
        BEHAVIOR_TARGET;
    }
}
