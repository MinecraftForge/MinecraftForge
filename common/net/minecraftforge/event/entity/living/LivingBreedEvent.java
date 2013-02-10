package net.minecraftforge.event.entity.living;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraftforge.event.Cancelable;

@Cancelable
public class LivingBreedEvent extends LivingEvent {

    public final EntityAnimal animal;
    public final EntityAnimal mate;
    public EntityAgeable child;
    public int xp;
    
    public LivingBreedEvent(EntityAnimal animal, EntityAnimal mate, EntityAgeable child, int xp)
    {
        super(animal);
        this.animal = animal;
        this.mate = mate;
        this.child = child;
        this.xp = xp;
    }

}
