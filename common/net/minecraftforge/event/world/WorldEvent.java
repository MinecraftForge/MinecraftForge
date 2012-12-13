package net.minecraftforge.event.world;

import net.minecraft.world.World;
import net.minecraftforge.event.Event;

public class WorldEvent extends Event
{
    public final World world;

    public WorldEvent(World world)
    {
        this.world = world;
    }

    public static class Load extends WorldEvent
    {
        public Load(World world) { super(world); }
    }

    public static class Unload extends WorldEvent
    {
        public Unload(World world) { super(world); }
    }

    public static class Save extends WorldEvent
    {
        public Save(World world) { super(world); }
    }
}
