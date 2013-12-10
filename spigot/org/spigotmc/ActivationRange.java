package org.spigotmc;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.effect.EntityWeatherEffect;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.util.MathHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.SpigotTimings;
import net.minecraft.world.ChunkCoordIntPair; // MCPC+
import net.minecraftforge.common.FakePlayer;

public class ActivationRange
{

    static AxisAlignedBB maxBB = AxisAlignedBB.getBoundingBox( 0, 0, 0, 0, 0, 0 );
    static AxisAlignedBB miscBB = AxisAlignedBB.getBoundingBox( 0, 0, 0, 0, 0, 0 );
    static AxisAlignedBB animalBB = AxisAlignedBB.getBoundingBox( 0, 0, 0, 0, 0, 0 );
    static AxisAlignedBB monsterBB = AxisAlignedBB.getBoundingBox( 0, 0, 0, 0, 0, 0 );

    /**
     * Initializes an entities type on construction to specify what group this
     * entity is in for activation ranges.
     *
     * @param entity
     * @return group id
     */
    public static byte initializeEntityActivationType(Entity entity)
    {
        Chunk chunk = null;
        if ( entity instanceof EntityMob || entity instanceof EntitySlime )
        {
            return 1; // Monster
        } else if ( entity instanceof EntityCreature || entity instanceof EntityAmbientCreature )
        {
            return 2; // Animal
        } else
        {
            return 3; // Misc
        }
    }

    /**
     * These entities are excluded from Activation range checks.
     *
     * @param entity
     * @param world
     * @return boolean If it should always tick.
     */
    public static boolean initializeEntityActivationState(Entity entity, SpigotWorldConfig config)
    {
        if ( ( entity.activationType == 3 && config.miscActivationRange == 0 )
                || ( entity.activationType == 2 && config.animalActivationRange == 0 )
                || ( entity.activationType == 1 && config.monsterActivationRange == 0 )
                || entity instanceof EntityPlayer
                || entity instanceof EntityThrowable
                || entity instanceof EntityDragon
                || entity instanceof EntityDragonPart
                || entity instanceof EntityWither
                || entity instanceof EntityFireball
                || entity instanceof EntityWeatherEffect
                || entity instanceof EntityTNTPrimed
                || entity instanceof EntityEnderCrystal
                || entity instanceof EntityFireworkRocket )
        {
            return true;
        }

        return false;
    }

    /**
     * Utility method to grow an AABB without creating a new AABB or touching
     * the pool, so we can re-use ones we have.
     *
     * @param target
     * @param source
     * @param x
     * @param y
     * @param z
     */
    public static void growBB(AxisAlignedBB target, AxisAlignedBB source, int x, int y, int z)
    {
        target.minX = source.minX - x;
        target.minY = source.minY - y;
        target.minZ = source.minZ - z;
        target.maxX = source.maxX + x;
        target.maxY = source.maxY + y;
        target.maxZ = source.maxZ + z;
    }

    /**
     * Find what entities are in range of the players in the world and set
     * active if in range.
     *
     * @param world
     */
    public static void activateEntities(World world)
    {
        SpigotTimings.entityActivationCheckTimer.startTiming();
        final int miscActivationRange = world.spigotConfig.miscActivationRange;
        final int animalActivationRange = world.spigotConfig.animalActivationRange;
        final int monsterActivationRange = world.spigotConfig.monsterActivationRange;

        int maxRange = Math.max( monsterActivationRange, animalActivationRange );
        maxRange = Math.max( maxRange, miscActivationRange );
        maxRange = Math.min( ( world.spigotConfig.viewDistance << 4 ) - 8, maxRange );

        for ( Entity player : new ArrayList<Entity>( world.playerEntities ) )
        {

            player.activatedTick = MinecraftServer.currentTick;
            growBB( maxBB, player.boundingBox, maxRange, 256, maxRange );
            growBB( miscBB, player.boundingBox, miscActivationRange, 256, miscActivationRange );
            growBB( animalBB, player.boundingBox, animalActivationRange, 256, animalActivationRange );
            growBB( monsterBB, player.boundingBox, monsterActivationRange, 256, monsterActivationRange );

            int i = MathHelper.floor_double( maxBB.minX / 16.0D );
            int j = MathHelper.floor_double( maxBB.maxX / 16.0D );
            int k = MathHelper.floor_double( maxBB.minZ / 16.0D );
            int l = MathHelper.floor_double( maxBB.maxZ / 16.0D );

            for ( int i1 = i; i1 <= j; ++i1 )
            {
                for ( int j1 = k; j1 <= l; ++j1 )
                {
                    if ( world.getWorld().isChunkLoaded( i1, j1 ) )
                    {
                        activateChunkEntities( world.getChunkFromChunkCoords( i1, j1 ) );
                    }
                }
            }
        }
        SpigotTimings.entityActivationCheckTimer.stopTiming();
    }

    /**
     * Checks for the activation state of all entities in this chunk.
     *
     * @param chunk
     */
    private static void activateChunkEntities(Chunk chunk)
    {
        for ( List<Entity> slice : chunk.entityLists )
        {
            for ( Entity entity : slice )
            {
                if ( MinecraftServer.currentTick > entity.activatedTick )
                {
                    if ( entity.defaultActivationState )
                    {
                        entity.activatedTick = MinecraftServer.currentTick;
                        continue;
                    }
                    switch ( entity.activationType )
                    {
                        case 1:
                            if ( monsterBB.intersectsWith( entity.boundingBox ) )
                            {
                                entity.activatedTick = MinecraftServer.currentTick;
                            }
                            break;
                        case 2:
                            if ( animalBB.intersectsWith( entity.boundingBox ) )
                            {
                                entity.activatedTick = MinecraftServer.currentTick;
                            }
                            break;
                        case 3:
                        default:
                            if ( miscBB.intersectsWith( entity.boundingBox ) )
                            {
                                entity.activatedTick = MinecraftServer.currentTick;
                            }
                    }
                }
            }
        }
    }

    /**
     * If an entity is not in range, do some more checks to see if we should
     * give it a shot.
     *
     * @param entity
     * @return
     */
    public static boolean checkEntityImmunities(Entity entity)
    {
        // quick checks.
        if ( entity.inWater /* isInWater */ || entity.fire > 0 )
        {
            return true;
        }
        if ( !( entity instanceof EntityArrow ) )
        {
            if ( !entity.onGround || entity.riddenByEntity != null
                    || entity.ridingEntity != null )
            {
                return true;
            }
        } else if ( !( (EntityArrow) entity ).inGround )
        {
            return true;
        }
        // special cases.
        if ( entity instanceof EntityLiving )
        {
            EntityLiving living = (EntityLiving) entity;
            if ( living.attackTime > 0 || living.hurtTime > 0 || living.activePotionsMap.size() > 0 )
            {
                return true;
            }
            if ( entity instanceof EntityCreature && ( (EntityCreature) entity ).entityToAttack != null )
            {
                return true;
            }
            if ( entity instanceof EntityVillager && ( (EntityVillager) entity ).isMating() /* Getter for first boolean */ )
            {
                return true;
            }
            if ( entity instanceof EntityAnimal )
            {
                EntityAnimal animal = (EntityAnimal) entity;
                if ( animal.isChild() || animal.isInLove() /*love*/ )
                {
                    return true;
                }
                if ( entity instanceof EntitySheep && ( (EntitySheep) entity ).getSheared() )
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if the entity is active for this tick.
     *
     * @param entity
     * @return
     */
    public static boolean checkIfActive(Entity entity)
    {
        SpigotTimings.checkIfActiveTimer.startTiming();
        if (entity instanceof EntityPlayer && !(entity instanceof FakePlayer)) return true;
        
        // MCPC+ start - check if entity is in forced chunk and if so, set to active
        int i = MathHelper.floor_double(entity.posX);
        int j = MathHelper.floor_double(entity.posZ);
        boolean isForced = entity.worldObj.getPersistentChunks().containsKey(new ChunkCoordIntPair(i >> 4, j >> 4));
        boolean isActive = entity.activatedTick >= MinecraftServer.currentTick || entity.defaultActivationState || isForced;
        // MCPC+ end

        // Should this entity tick?
        if ( !isActive )
        {
            if ( ( MinecraftServer.currentTick - entity.activatedTick - 1 ) % 20 == 0 )
            {
                // Check immunities every 20 ticks.
                if ( checkEntityImmunities( entity ) )
                {
                    // Triggered some sort of immunity, give 20 full ticks before we check again.
                    entity.activatedTick = MinecraftServer.currentTick + 20;
                }
                isActive = true;
            }
            // Add a little performance juice to active entities. Skip 1/4 if not immune.
        } else if ( !entity.defaultActivationState && entity.ticksExisted % 4 == 0 && !checkEntityImmunities( entity ) )
        {
            isActive = false;
        }
        // MCPC+ start - disabled, this breaks moving chunkloaders such as AnchorCarts when entering new chunks that are not yet loaded
        // Make sure not on edge of unloaded chunk

        int x = MathHelper.floor_double( entity.posX );
        int z = MathHelper.floor_double( entity.posZ );
        if (isActive && !entity.worldObj.doChunksNearChunkExist(x, 0, z, 16)) {
            isActive = false;
        }

        // MCPC+ end
        SpigotTimings.checkIfActiveTimer.stopTiming();
        return isActive;
    }
}
