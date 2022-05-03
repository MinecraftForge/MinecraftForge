/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.living;

import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraft.entity.LivingEntity;

/**
 * Event for when an Enderman/Shulker teleports or an ender pearl is used.  Can be used to either modify the target position, or cancel the teleport outright.
 * @author Mithion
 * @deprecated Use {@link EntityTeleportEvent.EnderEntity} or {@link EntityTeleportEvent.EnderPearl}.
 */
@Deprecated //TODO Forge: Remove in 1.17
@Cancelable
public class EnderTeleportEvent extends LivingEvent
{

    private double targetX;
    private double targetY;
    private double targetZ;
    private float attackDamage;

    public EnderTeleportEvent(LivingEntity entity, double targetX, double targetY, double targetZ, float attackDamage)
    {
        super(entity);
        this.setTargetX(targetX);
        this.setTargetY(targetY);
        this.setTargetZ(targetZ);
        this.setAttackDamage(attackDamage);
    }

    public double getTargetX() { return targetX; }
    public void setTargetX(double targetX) { this.targetX = targetX; }
    public double getTargetY() { return targetY; }
    public void setTargetY(double targetY) { this.targetY = targetY; }
    public double getTargetZ() { return targetZ; }
    public void setTargetZ(double targetZ) { this.targetZ = targetZ; }
    public float getAttackDamage() { return attackDamage; }
    public void setAttackDamage(float attackDamage) { this.attackDamage = attackDamage; }
}
