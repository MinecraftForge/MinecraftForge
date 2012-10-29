package net.minecraftforge.event.terraingen;

import net.minecraft.src.*;
import net.minecraftforge.event.*;

public class CreateBiomeDecoratorEvent extends Event
{
    public final BiomeGenBase biome;
    public final BiomeDecorator originalBiomeDecorator;
    public BiomeDecorator newBiomeDecorator;
    
    public CreateBiomeDecoratorEvent(BiomeGenBase biome, BiomeDecorator original)
    {
        this.biome = biome;
        originalBiomeDecorator = original;
        newBiomeDecorator = original;
    }
}
