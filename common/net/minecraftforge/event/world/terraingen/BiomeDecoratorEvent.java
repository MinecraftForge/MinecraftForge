package net.minecraftforge.event.world.terraingen;

import net.minecraft.src.BiomeDecorator;
import net.minecraft.src.World;
import net.minecraftforge.event.world.WorldEvent;

public abstract class BiomeDecoratorEvent extends WorldEvent
{
    public final BiomeDecorator biomeDecorator;

    public BiomeDecoratorEvent(BiomeDecorator biomeDecorator, World world)
    {
        super(world);
        this.biomeDecorator = biomeDecorator;
    }

    public static class PreOreGenerationEvent extends BiomeDecoratorEvent
    {

        public PreOreGenerationEvent(BiomeDecorator biomeDecorator, World world)
        {
            super(biomeDecorator, world);
        }
        
    }

    public static class PostOreGenerationEvent extends BiomeDecoratorEvent
    {

        public PostOreGenerationEvent(BiomeDecorator biomeDecorator, World world)
        {
            super(biomeDecorator, world);
        }
        
    }
    
}
