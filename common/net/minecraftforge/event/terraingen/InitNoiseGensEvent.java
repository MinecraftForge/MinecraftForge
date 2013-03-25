package net.minecraftforge.event.terraingen;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import net.minecraftforge.event.world.*;

public class InitNoiseGensEvent extends WorldEvent
{
    public final Random rand;
    public final NoiseGeneratorOctaves[] originalNoiseGens;
    public NoiseGeneratorOctaves[] newNoiseGens;
    
    public InitNoiseGensEvent(World world, Random rand, NoiseGeneratorOctaves[] original)
    {
        super(world);
        this.rand = rand;
        originalNoiseGens = original;
        newNoiseGens = original.clone();
    }
}