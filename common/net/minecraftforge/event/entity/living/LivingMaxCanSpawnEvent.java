package net.minecraftforge.event.entity.living;

import net.minecraft.entity.EntityLiving;
import net.minecraftforge.event.Event.HasResult;

@HasResult
public class LivingMaxCanSpawnEvent extends LivingEvent
{
    /**
     * This event is fired when the spawning system tries to determine the
     * maximum amount of the entity can spawn in a chunk.
     *  
     * If you set the result to 'ALLOW', it means that you want to return
     * the value of maxSpawnInChunk as the maximum amount allowed in the
     * chunk.
     */
    public int maxSpawnInChunk;
    
    public LivingMaxCanSpawnEvent(EntityLiving entity)
    {
        super(entity);
    }
}
