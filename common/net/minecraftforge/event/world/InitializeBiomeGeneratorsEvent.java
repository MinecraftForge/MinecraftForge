package net.minecraftforge.event.world;

import net.minecraft.src.GenLayer;
import net.minecraft.src.WorldType;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event;

@Cancelable
public class InitializeBiomeGeneratorsEvent extends Event
{
    
    public final long seed;
    public final WorldType worldType;
    public GenLayer[] genLayers;

    public InitializeBiomeGeneratorsEvent(long seed, WorldType worldType, GenLayer[] genLayers)
    {
        this.seed = seed;
        this.worldType = worldType;
        this.genLayers = genLayers;
    }

}
