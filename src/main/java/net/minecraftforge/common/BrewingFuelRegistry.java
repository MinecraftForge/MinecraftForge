package net.minecraftforge.common;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

/**
 * Stores a map of items to integers representing the fuel value of the items
 * when used in a brewing stand. If a fuel is not assigned a value, it will use
 * the fuel value of blaze power. Once added, items cannot be removed from the
 * fuel values table.
 */
public class BrewingFuelRegistry {

    /**
     * The value of the only vanilla fuel, blaze powder.
     */
    public static final int VANILLA_FUEL_TIME = 20;
    private static Object2IntMap<Item> fuelTimes = new Object2IntOpenHashMap<>();
    
    /**
     * Statically initialize the map to contain blaze powder.
     */
    static {
    	add(Items.BLAZE_POWDER);
    }
    
    /**
     * Adds the given item as a fuel for brewing stands, and assigns it the given fuel
     * value.
     */
    public static int add(Item fuel, int fuelTime) {
        return fuelTimes.put(fuel, fuelTime);
    }

    /**
     * Adds the given item as a fuel for brewing stands, and assigns the default
     * fuel value of blaze powder.
     */
    public static int add(Item fuel) {
        return add(fuel, VANILLA_FUEL_TIME);
    }
    
    /**
     * Removes the given item as a fuel for brewing stands.
     * Use with care.
     * Returns the previous fuel value of the item.
     */
    public static int remove(Item fuel) {
        return fuelTimes.removeInt(fuel);
    }

    /**
     * Retrieves the brewing stand fuel value of the given item. If the item is not
     * in the map, this function returns the fuel value of blaze powder.
     */
    public static int get(Item fuel) {
        return fuelTimes.getOrDefault(fuel, 0);
    }
}
