/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.living;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.RamTarget;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.Nullable;

/**
 * LivingKnockBackEvent is fired when a living entity is about to be knocked back. <br>
 * This event is fired whenever an Entity is knocked back in
 * {@link LivingEntity#hurt(DamageSource, float)},
 * {@link LivingEntity#blockedByShield(LivingEntity)},
 * {@link Mob#doHurtTarget(Entity)},
 * {@link Player#attack(Entity)} and
 * {@link RamTarget#tick(ServerLevel, Goat, long)}. <br>
 * <br>
 * This event is fired via {@link ForgeHooks#onLivingKnockBack(LivingEntity, Context, float, double, double)} .<br>
 * <br>
 * {@link #context} contains the cause of the knock back. <br>
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
    protected final Context context;

    @Deprecated(forRemoval = true)
    public LivingKnockBackEvent(LivingEntity target, float strength, double ratioX, double ratioZ)
    {
        this(target, Context.EMPTY, strength, ratioX, ratioZ);
    }

    public LivingKnockBackEvent(LivingEntity target, Context context, float strength, double ratioX, double ratioZ)
    {
        super(target);
        this.strength = this.originalStrength = strength;
        this.ratioX = this.originalRatioX = ratioX;
        this.ratioZ = this.originalRatioZ = ratioZ;
        this.context = context;
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

    public Context getContext() {return this.context;}

    public static final class Context {
        public static final Context EMPTY = new Builder().cause(KnockbackCause.UNKNOWN).createContext();
        private final KnockbackCause cause;
        @Nullable private final DamageSource damageSource;
        @Nullable private final Float damageReceived;
        @Nullable private final Entity attackSource;
        private final ItemStack itemUsedForAttack;

        private Context(Builder contextBuilder)
        {
            this.cause = contextBuilder.cause;
            this.damageSource = contextBuilder.damageSource;
            this.damageReceived = contextBuilder.damageReceived;
            this.attackSource = contextBuilder.attackSource;
            this.itemUsedForAttack = contextBuilder.itemUsedForAttack;
        }

        public static Context createShieldBlockedAttackFrom(Entity attackSource)
        {
            return new Builder().cause(KnockbackCause.SHIELD_BLOCKED_ATTACK_FROM_ENTITY).attackSource(attackSource).createContext();
        }

        public static Context createHurtBy(DamageSource damageSource, float damageReceived)
        {
            return new Builder().cause(KnockbackCause.HURT_BY_DAMAGE_SOURCE).damageSource(damageSource).damageReceived(damageReceived).attackSource(damageSource.getDirectEntity()).createContext();
        }

        public static Context createHurtBy(DamageSource damageSource, float damageReceived, Entity attackSource)
        {
            return new Builder().cause(KnockbackCause.HURT_BY_DAMAGE_SOURCE).damageSource(damageSource).damageReceived(damageReceived).attackSource(attackSource).createContext();
        }

        public static Context createAttackedBy(Entity attackSource, ItemStack itemUsedForAttack, float damageReceived)
        {
            return new Builder().cause(KnockbackCause.ATTACKED_BY_ENTITY).damageReceived(damageReceived).attackSource(attackSource).itemUsedForAttack(itemUsedForAttack).createContext();
        }

        public static Context createSweepAttackedBy(Entity attackSource, ItemStack itemUsedForAttack, float damageReceived)
        {
            return new Builder().cause(KnockbackCause.SWEEP_ATTACKED_BY_ENTITY).damageReceived(damageReceived).attackSource(attackSource).itemUsedForAttack(itemUsedForAttack).createContext();
        }

        public static Context createRammedBy(Entity attackSource)
        {
            return new Builder().cause(KnockbackCause.RAMMED_BY_ENTITY).attackSource(attackSource).createContext();
        }

        public KnockbackCause getCause()
        {
            return cause;
        }

        /**
         * @return an optional {@link DamageSource} that is responsible for the knockback
         */
        @Nullable
        public DamageSource getDamageSource()
        {
            return damageSource;
        }

        /**
         * @return an optional {@link Entity} that is the responsible for the knockback
         */
        @Nullable
        public Entity getAttacker()
        {
            return attackSource;
        }

        /**
         * @return damage that was received during to the knockback (e.g. {@link LivingEntity#hurt(DamageSource, float)}) or <br>
         * {@code null} if no damage was received.
         */
        @Nullable
        public Float getDamageReceived()
        {
            return damageReceived;
        }

        /**
         * @return the {@link ItemStack} instance that was used for the knockback or
         * {@code ItemStack.EMPTY} if no ItemStack was used.
         */
        public ItemStack getItemUsedForAttack()
        {
            return itemUsedForAttack;
        }

        public enum KnockbackCause
        {
            /**
             * knockback cause is not known or the deprecated ForgeHook was called
             */
            UNKNOWN,
            /**
             * knockback is caused by blocking an attack from an entity
             * @see LivingEntity#blockedByShield(LivingEntity)
             */
            SHIELD_BLOCKED_ATTACK_FROM_ENTITY,
            /**
             * knockback is caused internally by being damaged
             * @see LivingEntity#hurt(DamageSource, float)
             */
            HURT_BY_DAMAGE_SOURCE,
            /**
             * knockback is caused externally by an entity attack
             * @see Mob#doHurtTarget(Entity)
             * @see Player#attack(Entity)
             */
            ATTACKED_BY_ENTITY,
            /**
             * knockback is caused externally by an entity sweep attack
             * @see Player#attack(Entity)
             */
            SWEEP_ATTACKED_BY_ENTITY,
            /**
             * knockback is caused externally by an entity ramming
             * @see RamTarget#tick(ServerLevel, Goat, long)
             */
            RAMMED_BY_ENTITY
        }

        public static class Builder
        {
            private KnockbackCause cause = KnockbackCause.UNKNOWN;
            private @Nullable Float damageReceived = null;
            private @Nullable Entity attackSource = null;
            private @Nullable DamageSource damageSource = null;
            private ItemStack itemUsedForAttack = ItemStack.EMPTY;

            public Builder cause(KnockbackCause cause)
            {
                this.cause = cause;
                return this;
            }

            public Builder damageReceived(float damageReceived)
            {
                this.damageReceived = damageReceived;
                return this;
            }

            public Builder damageSource(DamageSource damageSource)
            {
                this.damageSource = damageSource;
                return this;
            }

            public Builder attackSource(Entity attackSource)
            {
                this.attackSource = attackSource;
                return this;
            }

            public Builder itemUsedForAttack(ItemStack itemUsedForAttack)
            {
                this.itemUsedForAttack = itemUsedForAttack;
                return this;
            }

            public Context createContext()
            {
                return new Context(this);
            }
        }
    }
}
