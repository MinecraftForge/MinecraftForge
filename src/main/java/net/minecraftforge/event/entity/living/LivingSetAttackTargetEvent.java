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

/**
 * LivingSetAttackTargetEvent is fired when an Entity sets a target to attack.<br>
 * This event is fired whenever an Entity sets a target to attack in
 * {@link Mob#setTarget(LivingEntity)}.<br>
 * <br>
 * This event is fired via the {@link ForgeHooks#onLivingSetAttackTarget(LivingEntity, LivingEntity)}.<br>
 * <br>
 * {@link #target} contains the newly targeted Entity.<br>
 * <br>
 * This event is not {@link net.minecraftforge.eventbus.api.Cancelable}.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
public class LivingSetAttackTargetEvent extends LivingEvent
{

    private final LivingEntity target;
    private final boolean causedByBehavior;
    public LivingSetAttackTargetEvent(LivingEntity entity, LivingEntity target)
    {
        super(entity);
        this.target = target;
        this.causedByBehavior = false;
    }
    
    public LivingSetAttackTargetEvent(LivingEntity entity, LivingEntity target, boolean causedByBehavior)
    {
        super(entity);
        this.target = target;
        this.causedByBehavior = causedByBehavior;
    }

    public LivingEntity getTarget()
    {
        return target;
    }
    
    /**
     * {@return {@code true} when the event was caused by {@link StartAttacking}, otherwise returns {@code false}}
     */
    public boolean isCausedByBehavior()
    {
        return causedByBehavior;
    }
}
