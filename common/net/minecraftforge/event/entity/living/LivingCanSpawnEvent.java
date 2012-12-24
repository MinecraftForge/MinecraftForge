package net.minecraftforge.event.entity.living;

import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;
import net.minecraftforge.event.Event.HasResult;

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
public class LivingCanSpawnEvent extends LivingSpawnEvent
{

    public LivingCanSpawnEvent(EntityLiving entity, World world, float x, float y, float z)
    {
        super(entity, world, x, y, z);
    }
}