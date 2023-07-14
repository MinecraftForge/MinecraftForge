/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.living;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * LivingBreatheEvent is fired whenever a living entity ticks.<br>
 * <br>
 * This event is fired via the {@link ForgeHooks#onLivingBreathe(LivingEntity, int, int)}.<br>
 * <br>
 * {@link #canBreathe()} contains if this entity can breathe.<br>
 * {@link #getConsumeAirAmount()} contains the amount of air that will be consumed if this entity can't breathe.<br>
 * {@link #getRefillAirAmount()} contains the amount of air that will be refilled if this entity can breathe.<br>
 * <br>
 * This event is not {@link Cancelable}.<br>
 * <br>
 * This event does not have a result. {@link HasResult}
 **/
public class LivingBreatheEvent extends LivingEvent
{
    private boolean canBreathe;
    private int consumeAirAmount;
    private int refillAirAmount;

    public LivingBreatheEvent(LivingEntity entity, boolean canBreathe, int consumeAirAmount, int refillAirAmount)
    {
        super(entity);
        this.canBreathe = canBreathe;
        this.consumeAirAmount = Math.max(consumeAirAmount, 0);
        this.refillAirAmount = Math.max(refillAirAmount, 0);
    }

    public boolean canBreathe()
    {
        return canBreathe;
    }

    public void setCanBreathe(boolean canBreathe)
    {
        this.canBreathe = canBreathe;
    }

    public int getConsumeAirAmount()
    {
        return consumeAirAmount;
    }

    public void setConsumeAirAmount(int consumeAirAmount)
    {
        this.consumeAirAmount = Math.max(consumeAirAmount, 0);
    }

    public int getRefillAirAmount()
    {
        return refillAirAmount;
    }

    public void setRefillAirAmount(int refillAirAmount)
    {
        this.refillAirAmount = Math.max(refillAirAmount, 0);
    }
}