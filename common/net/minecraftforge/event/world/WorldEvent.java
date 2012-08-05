package net.minecraftforge.event.world;

import net.minecraft.src.World;
import net.minecraftforge.event.Event;

public class WorldEvent extends Event
{
    private final World world;
    
    public WorldEvent(World world)
    {
        this.world = world;
    }
    
    public World getWorld()
    {
        return world;
    }
}
