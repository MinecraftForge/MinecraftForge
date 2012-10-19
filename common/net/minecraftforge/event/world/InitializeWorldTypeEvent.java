package net.minecraftforge.event.world;

import net.minecraft.src.WorldType;
import net.minecraftforge.event.Event;

public abstract class InitializeWorldTypeEvent extends Event
{

    public final long seed;
    public final WorldType worldType;

    public InitializeWorldTypeEvent(long seed, WorldType worldType)
    {
        this.seed = seed;
        this.worldType = worldType;
    }

}