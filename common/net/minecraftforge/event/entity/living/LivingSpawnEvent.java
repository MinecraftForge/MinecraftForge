package net.minecraftforge.event.entity.living;

import net.minecraft.src.EntityLiving;
import net.minecraft.src.World;

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
}
