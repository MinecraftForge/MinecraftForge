/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.living;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * LivingDrownEvent is fired whenever a living entity can't breathe and its air supply is less than or equal to zero.
 * <p>
 * This event is fired via {@link ForgeHooks#onLivingBreathe(LivingEntity, int, int)}.
 * <p>
 * This event is {@link Cancelable}. Effects of cancellation are noted in {@link #setCanceled(boolean)}.
 * <p>
 * This event does not {@linkplain HasResult have a result}.
 * This event is fired on {@link MinecraftForge#EVENT_BUS}
 **/
@Cancelable
public class LivingDrownEvent extends LivingEvent {
    private boolean isDrowning;
    private float damageAmount;
    private int bubbleCount;

    /**
     * Constructs a new LivingDrownEvent.
     *
     * @param entity       The entity that is drowning.
     * @param isDrowning   If the entity is "actively" drowning, and would take damage.
     * @param damageAmount The amount of {@linkplain DamageSources#drown() drowning damage} the entity would take.
     * @param bubbleCount  The number of {@linkplain ParticleTypes#BUBBLE} particles that will be spawned when actively drowning.
     */
    @ApiStatus.Internal
    public LivingDrownEvent(LivingEntity entity, boolean isDrowning, float damageAmount, int bubbleCount) {
        super(entity);
        this.isDrowning = isDrowning;
        this.damageAmount = damageAmount;
        this.bubbleCount = bubbleCount;
    }


    @ApiStatus.Internal
    @Deprecated(forRemoval = true, since = "1.20.1")
    public LivingDrownEvent(LivingEntity entity, boolean isDrowning) {
    	this(entity, isDrowning, 2.0F, 8);
    }

    /**
     * This method returns true if the entity is "actively" drowning.<br>
     * For most entities, this happens when their air supply reaches -20.<br>
     * When this is true, the entity will take damage, spawn particles, and reset their air supply to 0.
     *
     * @return If the entity is actively drowning.
     */
    public boolean isDrowning() {
        return isDrowning;
    }

    /**
     * Sets if the entity is actively drowning.
     *
     * @param isDrowning The new value.
     * @see #isDrowning()
     */
    public void setDrowning(boolean isDrowning) {
        this.isDrowning = isDrowning;
    }

    /**
     * Gets the amount of {@linkplain DamageSources#drown() drowning damage} the entity would take.<br>
     * Drowning damage is only inflicted if the entity is {@linkplain #isDrowning() actively drowning}.<br>
     * For vanilla entities, the default amount of damage is 2 (1 heart).
     * <p>
     * If the damage amount is less than or equal to zero, {@link Entity#hurt} will not be called.
     *
     * @return The amount of damage that will be dealt to the entity when actively drowning.
     */
    public float getDamageAmount() {
        return damageAmount;
    }

    /**
     * Sets the amount of drowning damage that may be inflicted.
     *
     * @param damageAmount The new value.
     * @see #getDamageAmount()
     */
    public void setDamageAmount(float damageAmount) {
        this.damageAmount = damageAmount;
    }

    /**
     * Gets the number of {@linkplain ParticleTypes#BUBBLE} particles that would be spawned.<br>
     * Bubbles are only spawned if the entity is {@linkplain #isDrowning() actively drowning}.<br>
     * For vanilla entities, the default value is 8 particles.
     *
     * @return The number of bubble particles that will spawn when actively drowning.
     */
    public int getBubbleCount() {
        return bubbleCount;
    }

    /**
     * Sets the amount of bubbles that may be spawned.
     *
     * @param bubbleCount The new value.
     * @see #getBubbleCount()
     */
    public void setBubbleCount(int bubbleCount) {
        this.bubbleCount = bubbleCount;
    }

    /**
     * Cancels the drowning event.<br>
     * Cancellation is mostly equivalent to {@link #setDrowning(boolean)} with a value of false.<br>
     * However, this also incurs the usual side effects of cancellation.
     */
    @Override
    public void setCanceled(boolean cancel) {
        super.setCanceled(cancel);
    }
}