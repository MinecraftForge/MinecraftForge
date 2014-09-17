package net.minecraftforge.event.world;

import net.minecraft.world.World;
import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;

/**
 * Explosion event.  Happens when the world creates an explosion.  Set to canceled to stop the explosion from happening.
 * @author Mithion
 *
 */
@Cancelable
public class ExplosionEvent extends Event
{
    public final World world;
    public final double x;
    public final double y;
    public final double z;
    public double radius;
    public boolean isSmoking;
    public boolean isFlaming;
    
    public ExplosionEvent(World world, double x, double y, double z, double radius, boolean isSmoking, boolean isFlaming)
    {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.radius = radius;
        this.isSmoking = isSmoking;
        this.isFlaming = isFlaming;
    }
}
