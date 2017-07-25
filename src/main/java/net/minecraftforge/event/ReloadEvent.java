package net.minecraftforge.event;

import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

public class ReloadEvent extends Event
{ 
    public final WorldServer world;

    public ReloadEvent(WorldServer world)
    {
        this.world = world;
    }

    @Cancelable
    public static class Pre extends ReloadEvent
    {
        public Pre(WorldServer world)
        {
            super(world);
        }
    }

    public static class Post extends ReloadEvent
    {
        public Post(WorldServer world)
        {
            super(world);
        }
    }
}
