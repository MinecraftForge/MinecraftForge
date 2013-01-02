package net.minecraftforge.event.entity.living;

import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;
import net.minecraftforge.event.Cancelable;

@Deprecated //Remove next MC Version
@Cancelable
public class LivingSpecialSpawnEvent extends LivingEvent
{
    public final World world;
    public final float x;
    public final float y;
    public final float z;
    private boolean handeled = false;
    
    public LivingSpecialSpawnEvent(EntityLiving entity, World world, float x, float y, float z)
    {
        super(entity);
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
