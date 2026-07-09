/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.living;

import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.LivingEntity;

/**
 * LivingHurtEvent is fired when an Entity is set to be hurt. <br>
 * This event is fired whenever an Entity is hurt in
 * {@link EntityLivingBase#damageEntity(DamageSource, float)} and
 * {@link EntityPlayer#damageEntity(DamageSource, float)}.<br>
 * <br>
 * This event is fired via the {@link ForgeHooks#onLivingHurt(EntityLivingBase, DamageSource, float)}.<br>
 * <br>
 * {@link #source} contains the DamageSource that caused this Entity to be hurt. <br>
 * {@link #amount} contains the amount of damage dealt to the Entity that was hurt. <br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, the Entity is not hurt.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 * @see LivingDamageEvent
 **/
@net.minecraftforge.eventbus.api.Cancelable
public class LivingHurtEvent extends LivingEvent
{
    private final DamageSource source;
    private float amount;
    public LivingHurtEvent(LivingEntity entity, DamageSource source, float amount)
    {
        super(entity);
        this.source = source;
        this.amount = amount;
    }

    public DamageSource getSource() { return source; }

    public float getAmount() { return amount; }

    public void setAmount(float amount) { this.amount = amount; }
}
