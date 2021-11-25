/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.event.entity.living;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * LivingKnockBackEvent is fired when a living entity is about to be knocked back. <br>
 * This event is fired whenever an Entity is knocked back in
 * {@link LivingEntity#hurt(DamageSource, float)},
 * {@code LivingEntity#blockUsingShield(LivingEntity)},
 * {@link Mob#doHurtTarget(Entity)} and
 * {@link Player#attack(Entity)} <br>
 * <br>
 * This event is fired via {@link ForgeHooks#onLivingKnockBack(LivingEntity, float, double, double)} .<br>
 * <br>
 * {@link #strength} contains the strength of the knock back. <br>
 * {@link #ratioX} contains the x ratio of the knock back. <br>
 * {@link #ratioZ} contains the z ratio of the knock back. <br>
 * <br>
 * This event is {@link Cancelable}.<br>
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
