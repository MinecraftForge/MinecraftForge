package net.minecraftforge.event.entity.living;

import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event.HasResult;

public class LivingSpawnEvent extends LivingEvent
{
    public final World world;
    public final float x;
    public final float y;
    public final float z;
    
    public LivingSpawnEvent(EntityLiving entity, World world, float x, float y, float z)
    {
        super(entity);
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Fires before mob spawn events.
     * 
     * Result is significant:
     *    DEFAULT: use vanilla spawn rules
     *    ALLOW:   allow the spawn
     *    DENY:    deny the spawn
     *
     */
    @HasResult
    public static class CheckSpawn extends LivingSpawnEvent
    {
        public CheckSpawn(EntityLiving entity, World world, float x, float y, float z)
        {
            super(entity, world, x, y, z);
        }
    }

    @Cancelable
    public static class SpecialSpawn extends LivingSpawnEvent
    {
        public SpecialSpawn(EntityLiving entity, World world, float x, float y, float z)
        {
            super(entity, world, x, y, z);
        }
    }
}