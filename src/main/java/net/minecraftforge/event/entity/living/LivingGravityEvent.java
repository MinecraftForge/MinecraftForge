/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.living;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * LivingGravityEvent is fired when a LivingEntity is ticked in {@link LivingEntity#travel(Vec3)}}. <br>
 * <br>
 * This event is fired via the {@link ForgeHooks#onLivingGravity(LivingEntity, Vec3, double)}.<br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, the Entity does not update.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
@Cancelable
public class LivingGravityEvent extends LivingEvent
{

    private final Vec3 vec3;
    private double gravity;

    public LivingGravityEvent(LivingEntity entity, Vec3 vec3, double gravity)
    {
        super(entity);
        this.vec3 = vec3;
        this.gravity = gravity;
    }

    public Vec3 getVec3() { return vec3; }

    public double getGravity() { return gravity; }

    public void setGravity(double gravity) { this.gravity = gravity; }
}
