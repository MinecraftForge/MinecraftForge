package net.minecraftforge.event.terraingen;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.WorldType;

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

    public static class InitBiomeGens extends WorldTypeEvent
    {
        public final long seed;
        public final GenLayer[] originalBiomeGens;
        public GenLayer[] newBiomeGens;
        
        public InitBiomeGens(WorldType worldType, long seed, GenLayer[] original)
        {
            super(worldType);
            this.seed = seed;
            originalBiomeGens = original;
            newBiomeGens = original.clone();
        }
    }
}
