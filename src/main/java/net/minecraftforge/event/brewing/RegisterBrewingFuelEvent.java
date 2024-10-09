package net.minecraftforge.event.brewing;

import net.minecraft.world.item.Item;
import net.minecraftforge.common.BrewingFuelValues;

public class RegisterBrewingFuelEvent extends net.minecraftforge.eventbus.api.Event 
{
    
    private BrewingFuelValues brewingFuelValues;
    
    public RegisterBrewingFuelEvent()
    {
        super();
        this.brewingFuelValues = new BrewingFuelValues();
    }
    
    public void register(Item fuel, int fuelValue) 
    {
        this.brewingFuelValues.add(fuel, fuelValue);
    }
    
    /**
     * Is this unsafe? Should we allow outside access to the values?
     * This could result in unexpected changes, which might cause problems.
     */
    public BrewingFuelValues getBrewingFuelValues() {
        return this.brewingFuelValues;
    }
    
}
