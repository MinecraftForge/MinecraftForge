package net.minecraftforge.event.entity.living;

import net.minecraftforge.fml.common.eventhandler.Event.HasResult;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityEvent;

/**
 * ZombieEvent is fired whenever a zombie is spawned for aid.
 * If a method utilizes this {@link Event} as its parameter, the method will 
 * receive every child event of this class.
 * 
 * All children of this event are fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
public class ZombieEvent extends EntityEvent {

    public ZombieEvent(EntityZombie entity)
    {
        super(entity);
    }

    public EntityZombie getSummoner()
    {
        return (EntityZombie) entity;
    }
    
    /**
     * SummonAidEvent is fired when a Zombie Entity is summoned.
     * This event is fired whenever a Zombie Entity is summoned in 
     * EntityZombie#attackEntityFrom(DamageSource, float).
     * 
     * This event is fired via the {@link ForgeHooks#fireZombieSummonAid(EntityZombie, World, int, int, int, EntityLivingBase, double)}.
     * 
     * {@link #customSummonedAid} remains null, but can be populated with a custom EntityZombie which will be spawned.
     * {@link #world} contains the world that this summoning is occurring in.
     * {@link #x} contains the x-coordinate at which this summoning event is occurring. 
     * {@link #y} contains the y-coordinate at which this summoning event is occurring. 
     * {@link #z} contains the z-coordinate at which this summoning event is occurring. 
     * {@link #attacker} contains the living Entity that attacked and caused this event to fire.
     * {@link #summonChance} contains the likelihood that a Zombie would successfully be summoned.
     * 
     * This event is not {@link Cancelable}.
     * 
     * This event has a result. {@link HasResult}
     * {@link Result#ALLOW} Zombie is summoned.
     * {@link Result#DENY} Zombie is not summoned.
     * 
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
     **/
    @HasResult
    public static class SummonAidEvent extends ZombieEvent {
        /**
         * Populate this field to have a custom zombie instead of a normal zombie summoned
         */
        public EntityZombie customSummonedAid;
        
        public final World world;
        public final int x;
        public final int y;
        public final int z;
        public final EntityLivingBase attacker;
        public final double summonChance;
        
        public SummonAidEvent(EntityZombie entity, World world, int x, int y, int z, EntityLivingBase attacker, double summonChance)
        {
            super(entity);
            this.world = world;
            this.x = x;
            this.y = y;
            this.z = z;
            this.attacker = attacker;
            this.summonChance = summonChance;
        }
        
    }
}
