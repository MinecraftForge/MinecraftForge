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

    public static final class Context
    {
        public static final Context EMPTY = new Builder().setCause(KnockbackCause.UNKNOWN).createContext();
        private final KnockbackCause cause;
        @Nullable private final DamageSource damageSource;
        @Nullable private final Float damage;
        @Nullable private final Entity attacker;
        private final ItemStack itemUsedByAttacker;

        private Context(Builder contextBuilder)
        {
            this.cause = contextBuilder.cause;
            this.damageSource = contextBuilder.damageSource;
            this.damage = contextBuilder.damage;
            this.attacker = contextBuilder.attacker;
            this.itemUsedByAttacker = contextBuilder.itemUsedByAttacker;
        }

        public static Context createShieldBlockedAttackFrom(Entity attackSource)
        {
            return new Builder().setCause(KnockbackCause.SHIELD_BLOCKED_ATTACK_FROM_ENTITY).setAttacker(attackSource).createContext();
        }

        public static Context createHurtBy(DamageSource damageSource, float damage)
        {
            return new Builder().setCause(KnockbackCause.HURT_BY_DAMAGE_SOURCE).setDamageSource(damageSource).setDamage(damage).setAttacker(damageSource.getDirectEntity()).createContext();
        }

        public static Context createHurtBy(DamageSource damageSource, float damage, Entity attacker)
        {
            return new Builder().setCause(KnockbackCause.HURT_BY_DAMAGE_SOURCE).setDamageSource(damageSource).setDamage(damage).setAttacker(attacker).createContext();
        }

        public static Context createAttackedBy(Entity attacker, ItemStack itemUsedForAttack, float damage)
        {
            return new Builder().setCause(KnockbackCause.ATTACKED_BY_ENTITY).setDamage(damage).setAttacker(attacker).setItemUsedByAttacker(itemUsedForAttack).createContext();
        }

        public static Context createSweepAttackedBy(Entity attacker, ItemStack itemUsedByAttacker, float damage)
        {
            return new Builder().setCause(KnockbackCause.SWEEP_ATTACKED_BY_ENTITY).setDamage(damage).setAttacker(attacker).setItemUsedByAttacker(itemUsedByAttacker).createContext();
        }

        public static Context createRammedBy(Entity attacker)
        {
            return new Builder().setCause(KnockbackCause.RAMMED_BY_ENTITY).setAttacker(attacker).createContext();
        }

        public KnockbackCause getCause()
        {
            return cause;
        }

        /**
         * @return {@link DamageSource} that is associated with the knockback or {@code null}
         */
        @Nullable
        public DamageSource getDamageSource()
        {
            return damageSource;
        }

        /**
         * @return {@link Entity} that is associated with the knockback or {@code null}
         */
        @Nullable
        public Entity getAttacker()
        {
            return attacker;
        }

        /**
         * @return damage that is associated with the knockback (e.g. {@link LivingEntity#hurt(DamageSource, float)}) or <br>
         * {@code null} if no damage was received.
         */
        @Nullable
        public Float getDamage()
        {
            return damage;
        }

        /**
         * @return the {@link ItemStack} instance that is associated with the knockback or
         * {@code ItemStack.EMPTY} if no ItemStack was used.
         */
        public ItemStack getItemUsedByAttacker()
        {
            return itemUsedByAttacker;
        }

        public enum KnockbackCause
        {
            /**
             * knockback cause is not known or the deprecated ForgeHook was called
             */
            UNKNOWN,
            /**
             * knockback is caused externally by blocking an attack from an entity
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
            private @Nullable Float damage = null;
            private @Nullable Entity attacker = null;
            private @Nullable DamageSource damageSource = null;
            private ItemStack itemUsedByAttacker = ItemStack.EMPTY;

            public Builder setCause(KnockbackCause cause)
            {
                this.cause = cause;
                return this;
            }

            public Builder setDamage(float damage)
            {
                this.damage = damage;
                return this;
            }

            public Builder setDamageSource(DamageSource damageSource)
            {
                this.damageSource = damageSource;
                return this;
            }

            public Builder setAttacker(Entity entity)
            {
                this.attacker = entity;
                return this;
            }

            public Builder setItemUsedByAttacker(ItemStack stack)
            {
                this.itemUsedByAttacker = stack;
                return this;
            }

            public Context createContext()
            {
                return new Context(this);
            }
        }
    }
}
