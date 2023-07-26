/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.living;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * LivingBreatheEvent is fired whenever a living entity ticks.<br>
 * <br>
 * This event is fired via {@link ForgeHooks#onLivingBreathe(LivingEntity, int, int)}.<br>
 * <br>
 * This event is not {@link Cancelable}.<br>
 * <br>
 * This event does not have a result. {@link HasResult}
 * <br>
 * This event is fired on {@link MinecraftForge#EVENT_BUS}
 */
public class LivingBreatheEvent extends LivingEvent {
    private boolean canBreathe;
    private boolean canRefillAir;
    private int consumeAirAmount;
    private int refillAirAmount;

    @ApiStatus.Internal
    @Deprecated(forRemoval = true, since = "1.20.1")
    public LivingBreatheEvent(LivingEntity entity, boolean canBreathe, int consumeAirAmount, int refillAirAmount) {
    	this(entity, canBreathe, consumeAirAmount, refillAirAmount, canBreathe);
    }

    @ApiStatus.Internal
    public LivingBreatheEvent(LivingEntity entity, boolean canBreathe, int consumeAirAmount, int refillAirAmount, boolean canRefillAir) {
        super(entity);
        this.canBreathe = canBreathe;
        this.canRefillAir = canRefillAir;
        this.consumeAirAmount = Math.max(consumeAirAmount, 0);
        this.refillAirAmount = Math.max(refillAirAmount, 0);
    }

    /**
     * If the entity can breathe and {@link #canRefillAir()} returns true, their air value will be increased by {@link #getRefillAirAmount()}.<br>
     * If the entity can breathe and {@link #canRefillAir()} returns false, their air value will stay the same.<br>
     * If the entity cannot breathe, their air value will be reduced by {@link #getConsumeAirAmount()}.
     * @return True if the entity can breathe
     */
    public boolean canBreathe() {
        return canBreathe;
    }

    /**
     * Sets if the entity can breathe or not.
     * @param canBreathe The new value.
     */
    public void setCanBreathe(boolean canBreathe) {
        this.canBreathe = canBreathe;
    }

    /**
     * If the entity can breathe, {@link #canRefillAir()} will be checked to see if their air value should be refilled.
     * @return True if the entity can refill its air value
     */
    public boolean canRefillAir() {
        return canRefillAir;
    }

    /**
     * Sets if the entity can refill its air value or not.
     * @param canRefillAir The new value.
     */
    public void setCanRefillAir(boolean canRefillAir) {
        this.canRefillAir = canRefillAir;
    }

    /**
     * @return The amount the entity's {@linkplain LivingEntity#getAirSupply() air supply} will be reduced by if the entity {@linkplain #canBreathe() cannot breathe}.
     */
    public int getConsumeAirAmount() {
        return consumeAirAmount;
    }

    /**
     * Sets the new consumed air amount.
     * @param consumeAirAmount The new value.
     * @see #getConsumeAirAmount()
     */
    public void setConsumeAirAmount(int consumeAirAmount) {
        this.consumeAirAmount = Math.max(consumeAirAmount, 0);
    }

    /**
     * @return The amount the entity's {@linkplain LivingEntity#getAirSupply() air supply} will be increased by if the entity {@linkplain #canBreathe() can breathe}.
     */
    public int getRefillAirAmount() {
        return refillAirAmount;
    }

    /**
     * Sets the new refilled air amount.
     *
     * @param refillAirAmount The new value.
     * @see #getRefillAirAmount()
     */
    public void setRefillAirAmount(int refillAirAmount) {
        this.refillAirAmount = Math.max(refillAirAmount, 0);
    }
}