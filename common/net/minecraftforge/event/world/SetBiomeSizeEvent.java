package net.minecraftforge.event.world;

import net.minecraft.src.GenLayer;
import net.minecraft.src.WorldType;

public class SetBiomeSizeEvent extends InitializeWorldTypeEvent
{

    public byte biomeSize;
    
    public SetBiomeSizeEvent(long seed, WorldType worldType, byte biomeSize)
    {
        super(seed, worldType);
        this.biomeSize = biomeSize;
    }

}
