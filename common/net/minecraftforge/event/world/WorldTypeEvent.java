package net.minecraftforge.event.world;

import net.minecraft.src.WorldType;
import net.minecraftforge.event.Event;

public class WorldTypeEvent extends Event
{
    public final WorldType worldType;

    public WorldTypeEvent(WorldType worldType)
    {
        this.worldType = worldType;
    }

    public static class BiomeSize extends WorldTypeEvent
    {
        public final byte originalSize;
        public byte newSize;
        
        public BiomeSize(WorldType worldType, byte original)
        {
            super(worldType);
            originalSize = original;
            newSize = original;
        }
    }
}
