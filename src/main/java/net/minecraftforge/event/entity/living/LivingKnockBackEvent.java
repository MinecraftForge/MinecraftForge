/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.living;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * LivingKnockBackEvent is fired when a living entity is about to be knocked back. <br>
 * This event is fired whenever an Entity is knocked back in
 * {@link EntityLivingBase#attackEntityFrom(DamageSource, float)},
 * {@link EntityLivingBase#blockWithShield(EntityLivingBase)},
 * {@link EntityMob#attackEntityAsMob(Entity)} and
 * {@link EntityPlayer#attackTargetEntityWithCurrentItem(Entity)} <br>
 * <br>
 * This event is fired via {@link ForgeHooks#onLivingKnockBack(EntityLivingBase, Entity, float, double, double)}.<br>
 * <br>
 * {@link #attacker} contains the Entity that caused the knock back. <br>
 * {@link #strength} contains the strength of the knock back. <br>
 * {@link #ratioX} contains the x ratio of the knock back. <br>
 * {@link #ratioZ} contains the z ratio of the knock back. <br>
 * <br>
 * This event is {@link net.minecraftforge.eventbus.api.Cancelable}.<br>
 * If this event is canceled, the entity is not knocked back.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 *<br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
@Cancelable
public class LivingKnockBackEvent extends LivingEvent
{
    protected float strength;
    protected double ratioX, ratioZ;
    protected final float originalStrength;
    protected final double originalRatioX, originalRatioZ;

    public LivingKnockBackEvent(LivingEntity target, float strength, double ratioX, double ratioZ)
    {
        super(target);
        this.strength = this.originalStrength = strength;
        this.ratioX = this.originalRatioX = ratioX;
        this.ratioZ = this.originalRatioZ = ratioZ;
    }

    public float getStrength() {return this.strength;}

    public double getRatioX() {return this.ratioX;}

    public double getRatioZ() {return this.ratioZ;}

    public float getOriginalStrength() {return this.originalStrength;}

    public double getOriginalRatioX() {return this.originalRatioX;}

    public double getOriginalRatioZ() {return this.originalRatioZ;}

    public void setStrength(float strength) {this.strength = strength;}

    public void setRatioX(double ratioX) {this.ratioX = ratioX;}

    public void setRatioZ(double ratioZ) {this.ratioZ = ratioZ;}
}
