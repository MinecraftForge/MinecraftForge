/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.living;

import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

/**
 * LivingDamageEvent is fired just before damage is applied to entity.<br>
 * At this point armor, potion and absorption modifiers have already been applied to damage - this is FINAL value.<br>
 * Also note that appropriate resources (like armor durability and absorption extra hearths) have already been consumed.<br>
 * This event is fired whenever an Entity is damaged in
 * {@code LivingEntity#actuallyHurt(DamageSource, float)} and
 * {@code Player#actuallyHurt(DamageSource, float)}.<br>
 * <br>
 * This event is fired via the {@link ForgeHooks#onLivingDamage(LivingEntity, DamageSource, float)}.<br>
 * <br>
 * {@link #source} contains the DamageSource that caused this Entity to be hurt. <br>
 * {@link #amount} contains the final amount of damage that will be dealt to entity. <br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, the Entity is not hurt. Used resources WILL NOT be restored.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * @see LivingHurtEvent
 **/
@Cancelable
public class LivingDamageEvent extends LivingEvent
{
    private final DamageSource source;
    private float amount;
    public LivingDamageEvent(LivingEntity entity, DamageSource source, float amount)
    {
        super(entity);
        this.source = source;
        this.amount = amount;
    }

    public DamageSource getSource() { return source; }

    public float getAmount() { return amount; }

    public void setAmount(float amount) { this.amount = amount; }
}
