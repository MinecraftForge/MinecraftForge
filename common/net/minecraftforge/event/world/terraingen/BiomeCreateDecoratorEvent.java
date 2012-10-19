package net.minecraftforge.event.world.terraingen;

import net.minecraft.src.BiomeDecorator;
import net.minecraftforge.event.Event;

public class BiomeCreateDecoratorEvent extends Event
{
    public BiomeDecorator biomeDecorator;

    public BiomeCreateDecoratorEvent(BiomeDecorator biomeDecorator)
    {
        this.biomeDecorator = biomeDecorator;
    }

}
