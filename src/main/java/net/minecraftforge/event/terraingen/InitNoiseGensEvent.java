package net.minecraftforge.event.terraingen;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.gen.NoiseGenerator;
import net.minecraftforge.event.world.*;

public class InitNoiseGensEvent extends WorldEvent
{
    public final Random rand;
    public final NoiseGenerator[] originalNoiseGens;
    public NoiseGenerator[] newNoiseGens;
    
    public InitNoiseGensEvent(World world, Random rand, NoiseGenerator[] original)
    {
        super(world);
        this.rand = rand;
        originalNoiseGens = original;
        newNoiseGens = original.clone();
    }
}