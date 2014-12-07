package net.minecraftforge.event.world;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

/**
 * Explosion events are fired before, during, and after an explosion using net.minecraft.world.Explosion
 * 
 * This event and its children are fired on the {@link MinecraftForge#EVENT_BUS}.
 */
public class ExplosionEvent extends Event
{
    public ExplosionEvent(Explosion explosion, double x, double y, double z, World world, Entity exploder, EntityLivingBase placer)
    {
        this.explosion = explosion;
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
        this.exploder = exploder;
        this.placer = placer;
    }
    
    public final Explosion explosion;
    public final double x;
    public final double y;
    public final double z;
    public final World world;
    public final Entity exploder;
    public final EntityLivingBase placer;
    
    /**
     * Fires immediately prior to an explosion occurring.
     */
    @Cancelable
    public static class Pre extends ExplosionEvent
    {
        public Pre(Explosion explosion, double x, double y, double z, World world, Entity exploder, EntityLivingBase placer, float explosionSize, boolean isFlaming, boolean isSmoking)
        {
            super(explosion, x, y, z, world, exploder, placer);
            
            this.explosionSize = explosionSize;
            this.isFlaming = isFlaming;
            this.isSmoking = isSmoking;
        }
        
        public float explosionSize; // mutable
        
        public boolean isFlaming; // mutable
        public boolean isSmoking; // mutable
    }
    
    /**
     * Fires during an explosion. Specifically, during doExplosionA(), after affected blocks, affected entities, etc.
     * have been calculated, but before any blocks have been broken, entities damaged, etc.
     */
    @Cancelable
    public static class During extends ExplosionEvent
    {
        public During(Explosion explosion, double x, double y, double z, World world, Entity exploder, EntityLivingBase placer, float explosionSize, boolean isFlaming, boolean isSmoking,
                      Collection<Entity> affectedEntities, Collection<ChunkPosition> affectedBlockPositions) // Stupidly long constructor, huzzah!
        {
            super(explosion, x, y, z, world, exploder, placer);
            
            this.affectedEntities = affectedEntities;
            this.affectedBlockPositions = affectedBlockPositions;
            this.explosionSize = explosionSize;
            this.isFlaming = isFlaming;
            this.isSmoking = isSmoking;
        }
        
        public final Collection<Entity> affectedEntities; // mutable
        public final Collection<ChunkPosition> affectedBlockPositions; // mutable
        
        public final float explosionSize; // immutable
        
        public boolean isFlaming; // mutable
        public boolean isSmoking; // mutable
    }
    
    /**
     * Fires immediately after an explosion occurring.
     */
    public static class Post extends ExplosionEvent
    {
        public Post(Explosion explosion, double x, double y, double z, World world, Entity exploder, EntityLivingBase placer, float explosionSize, boolean isFlaming, boolean isSmoking,
                    Collection<Entity> affectedEntities, Collection<ChunkPosition> affectedBlockPositions)
        {
            super(explosion, x, y, z, world, exploder, placer);
            
            this.affectedEntities = Collections.unmodifiableCollection(affectedEntities);
            this.affectedBlockPositions = Collections.unmodifiableCollection(affectedBlockPositions);
            this.explosionSize = explosionSize;
            this.isFlaming = isFlaming;
            this.isSmoking = isSmoking;
        }
        
        public final Collection<Entity> affectedEntities; // immutable
        public final Collection<ChunkPosition> affectedBlockPositions; // immutable
        
        public final float explosionSize; // immutable
        
        public final boolean isFlaming; // immutable
        public final boolean isSmoking; // immutable
    }
}