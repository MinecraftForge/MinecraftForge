package net.minecraftforge.event.entity.living;

import net.minecraft.src.EntityLiving;
import net.minecraft.src.World;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event.HasResult;

/**
 * Fires before special spawn events. Cancel the event to cancel special spawn.
 * 
 * On slime spawns, result is significant:
 *    DEFAULT: use vanilla spawn rules
 *    ALLOW:   allow the spawn
 *    DENY:    deny the spawn
 *
 */
@Cancelable
@HasResult
public class LivingSpecialSpawnEvent extends LivingEvent
{
    public final World world;
    public final float x;
    public final float y;
    public final float z;
    
    public LivingSpecialSpawnEvent(EntityLiving entity, World world, float x, float y, float z)
    {
        super(entity);
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
