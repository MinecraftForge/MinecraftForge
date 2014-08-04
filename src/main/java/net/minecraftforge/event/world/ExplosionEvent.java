package net.minecraftforge.event.world;

import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;

public class ExplosionEvent extends Event
{
    public final int x;
    public final int y;
    public final int z;
    public final Explosion explosion;
    public final World world;
    
    public ExplosionEvent(int x, int y, int z, Explosion explosion, World world)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.explosion = explosion;
        this.world = world;
    }
    
    @Cancelable
    public static class Pre extends ExplosionEvent
    {
        public Pre(int x, int y, int z, Explosion explosion, World world)
        {
            super(x, y, z, explosion, world);
        }
    }

    public static class Post extends ExplosionEvent
    {
        public Post(int x, int y, int z, Explosion explosion, World world)
        {
            super(x, y, z, explosion, world);
        }
    }
}
