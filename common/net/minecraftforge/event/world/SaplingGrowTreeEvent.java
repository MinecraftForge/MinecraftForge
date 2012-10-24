package net.minecraftforge.event.world;

import java.util.Random;

import net.minecraft.src.World;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event;

@Cancelable
public class SaplingGrowTreeEvent extends WorldEvent
{
    public final int x;
    public final int y;
    public final int z;
    public final Random random;
    
    public SaplingGrowTreeEvent(World world, int x, int y, int z, Random random)
    {
        super(world);
        this.x = x;
        this.y = y;
        this.z = z;
        this.random = random;
    }

}
