package net.minecraftforge.event.world;

import java.util.Random;

import net.minecraft.src.NoiseGeneratorOctaves;
import net.minecraft.src.World;
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

    public static class InitNoiseGens extends WorldEvent
    {
        public final Random rand;
        public final NoiseGeneratorOctaves[] originalNoiseGens;
        public NoiseGeneratorOctaves[] newNoiseGens;
        
        public InitNoiseGens(World world, Random rand, NoiseGeneratorOctaves[] original)
        {
            super(world);
            this.rand = rand;
            originalNoiseGens = original;
            newNoiseGens = original.clone();
        }
    }
}
