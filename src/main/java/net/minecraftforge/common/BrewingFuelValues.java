package net.minecraftforge.common;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.world.item.Item;

/**
 * Stores a map of items to integers representing the fuel value of the items
 * when used in a brewing stand. If a fuel is not assigned a value, it will use
 * the fuel value of blaze power. Once added, items cannot be removed from the
 * fuel values table.
 */
public class BrewingFuelValues {

    /**
     * The value of the only vanilla fuel, blaze powder.
     */
    public static final int VANILLA_FUEL_TIME = 20;
    private Object2IntMap<Item> fuelTimes;

    public BrewingFuelValues() 
    {
        this.fuelTimes = new Object2IntOpenHashMap<>();
    }

    /**
     * Adds the given item as a fuel for blaze rods, and assigns it the given fuel
     * value.
     */
    public void add(Item fuel, int fuelTime) 
    {
        this.fuelTimes.put(fuel, fuelTime);
    }

    /**
     * Adds the given item as a fuel for brewing stands, and assigns the default
     * fuel value of blaze powder.
     */
    public void add(Item fuel) 
    {
        this.add(fuel, VANILLA_FUEL_TIME);
    }

    /**
     * Retrieves the brewing stand fuel value of the given item. If the item is not
     * in the map, this function returns the fuel value of blaze powder.
     */
    public int get(Item fuel) 
    {
        return this.fuelTimes.getOrDefault(fuel, VANILLA_FUEL_TIME);
    }

    /**
     * Returns true if and only if this object's map has the given item as a fuel.
     * Note that this map will not hold blaze powder by default.
     */
    public boolean hasFuel(Item fuel) 
    {
        return this.fuelTimes.containsKey(fuel);
    }
}
