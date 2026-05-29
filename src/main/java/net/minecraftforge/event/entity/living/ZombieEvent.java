/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.event.entity.living;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.level.Level;
import net.minecraftsingularity.common.MinecraftForge;
import net.minecraftsingularity.common.util.HasResult;
import net.minecraftsingularity.common.util.Result;
import net.minecraftsingularity.event.singularityEventFactory;
import net.minecraftsingularity.event.entity.EntityEvent;
import net.minecraftsingularity.eventbus.api.bus.EventBus;
import net.minecraftsingularity.eventbus.api.event.InheritableEvent;

/**
 * ZombieEvent is fired whenever a zombie is spawned for aid.
 * If a method utilizes this event as its parameter, the method will
 * receive every child event of this class.
 **/
public sealed abstract class ZombieEvent implements EntityEvent, InheritableEvent {
    public static final EventBus<ZombieEvent> BUS = EventBus.create(ZombieEvent.class);

    private final Zombie zombie;

    protected ZombieEvent(Zombie zombie) {
        this.zombie = zombie;
    }

    @Override
    public Zombie getEntity() {
        return this.zombie;
    }

    /**
     * SummonAidEvent is fired when a Zombie Entity is summoned.
     * This event is fired whenever a Zombie Entity is summoned in
     * {@code Zombie#actuallyHurt(DamageSource, float)}.
     * <br>
     * This event is fired via the {@link singularityEventFactory#fireZombieSummonAid(Zombie, Level, int, int, int, LivingEntity, double)}.
     * <br>
     * {@link #getCustomSummonedAid()} remains null, but can be populated with a custom EntityZombie which will be spawned.<br>
     * {@link #getLevel()} contains the world that this summoning is occurring in.<br>
     * {@link #getX()} contains the x-coordinate at which this summoning event is occurring.<br>
     * {@link #getY()} contains the y-coordinate at which this summoning event is occurring.<br>
     * {@link #getZ()} contains the z-coordinate at which this summoning event is occurring.<br>
     * {@link #getAttacker()} contains the living Entity that attacked and caused this event to fire.<br>
     * {@link #getSummonChance()} contains the likelihood that a Zombie would successfully be summoned.<br>
     * <br>
     * This event {@linkplain HasResult has a result}:
     * <ul>
     *     <li>{@link Result#ALLOW} Zombie is summoned.</li>
     *     <li>{@link Result#DENY} Zombie is not summoned.</li>
     * </ul>
     *
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
     **/
    public static final class SummonAidEvent extends ZombieEvent implements HasResult {
        public static final EventBus<SummonAidEvent> BUS = EventBus.create(SummonAidEvent.class);

        private Zombie customSummonedAid;

        private final Level level;
        private final int x;
        private final int y;
        private final int z;
        private final LivingEntity attacker;
        private final double summonChance;
        private Result result = Result.DEFAULT;

        public SummonAidEvent(Zombie zombie, Level level, int x, int y, int z, LivingEntity attacker, double summonChance) {
            super(zombie);
            this.level = level;
            this.x = x;
            this.y = y;
            this.z = z;
            this.attacker = attacker;
            this.summonChance = summonChance;
        }

        /**
         * Populate this field to have a custom zombie instead of a normal zombie summoned
         */
        public Zombie getCustomSummonedAid() { return customSummonedAid; }
        public void setCustomSummonedAid(Zombie customSummonedAid) { this.customSummonedAid = customSummonedAid; }
        public Level getLevel() { return level; }
        public int getX() { return x; }
        public int getY() { return y; }
        public int getZ() { return z; }
        public LivingEntity getAttacker() { return attacker; }
        public double getSummonChance() { return summonChance; }

        @Override
        public Result getResult() {
            return result;
        }

        @Override
        public void setResult(Result result) {
            this.result = result;
        }
    }
}
