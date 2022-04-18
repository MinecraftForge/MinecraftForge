/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.living;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityEvent;

/**
 * ZombieEvent is fired whenever a zombie is spawned for aid.
 * If a method utilizes this event as its parameter, the method will
 * receive every child event of this class.
 * 
 * All children of this event are fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
public class ZombieEvent extends EntityEvent {

    public ZombieEvent(Zombie entity)
    {
        super(entity);
    }

    public Zombie getSummoner()
    {
        return (Zombie) getEntity();
    }
    
    /**
     * SummonAidEvent is fired when a Zombie Entity is summoned.
     * This event is fired whenever a Zombie Entity is summoned in 
     * {@code Zombie#actuallyHurt(DamageSource, float)}.
     * 
     * This event is fired via the {@link ForgeEventFactory#fireZombieSummonAid(Zombie, Level, int, int, int, LivingEntity, double)}.
     * 
     * {@link #customSummonedAid} remains null, but can be populated with a custom EntityZombie which will be spawned.
     * {@link #world} contains the world that this summoning is occurring in.
     * {@link #x} contains the x-coordinate at which this summoning event is occurring. 
     * {@link #y} contains the y-coordinate at which this summoning event is occurring. 
     * {@link #z} contains the z-coordinate at which this summoning event is occurring. 
     * {@link #attacker} contains the living Entity that attacked and caused this event to fire.
     * {@link #summonChance} contains the likelihood that a Zombie would successfully be summoned.
     * 
     * This event is not {@link net.minecraftforge.eventbus.api.Cancelable}.
     * 
     * This event has a result. {@link HasResult}
     * {@link Result#ALLOW} Zombie is summoned.
     * {@link Result#DENY} Zombie is not summoned.
     * 
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
     **/
    @HasResult
    public static class SummonAidEvent extends ZombieEvent {
        private Zombie customSummonedAid;
        
        private final Level world;
        private final int x;
        private final int y;
        private final int z;
        private final LivingEntity attacker;
        private final double summonChance;
        
        public SummonAidEvent(Zombie entity, Level world, int x, int y, int z, LivingEntity attacker, double summonChance)
        {
            super(entity);
            this.world = world;
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
        public Level getWorld() { return world; }
        public int getX() { return x; }
        public int getY() { return y; }
        public int getZ() { return z; }
        public LivingEntity getAttacker() { return attacker; }
        public double getSummonChance() { return summonChance; }
    }
}
