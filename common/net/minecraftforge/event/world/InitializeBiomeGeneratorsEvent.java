package net.minecraftforge.event.world;

import net.minecraft.src.GenLayer;
import net.minecraft.src.WorldType;
import net.minecraftforge.event.Cancelable;

@Cancelable
public class InitializeBiomeGeneratorsEvent extends InitializeWorldTypeEvent
{
    
    public GenLayer[] genLayers;

    public InitializeBiomeGeneratorsEvent(long seed, WorldType worldType, GenLayer[] genLayers)
    {
        super(seed, worldType);
        this.genLayers = genLayers;
    }

}
