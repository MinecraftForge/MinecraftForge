package net.minecraftforge.common;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

/**
 * Stores a map of items to integers representing the fuel value of the items
 * when used in a brewing stand. If a fuel is not assigned a value, it will use
 * the fuel value of blaze power.
 * 
 * This class should only be interacted with during the FMLCommonSetupEvent, to
 * ensure that values are the same on the client and server and all items have 
 * been registered.
 */
public class BrewingFuelRegistry {

    /**
     * The fuel value of the only vanilla fuel, blaze powder.
     */
    public static final int VANILLA_FUEL_TIME = 20;
    
    private static final Object2IntMap<Item> FUEL_TIMES = new Object2IntOpenHashMap<>();
    
    /**
     * Statically initialize the map to contain blaze powder.
     */
    static {
    	add(Items.BLAZE_POWDER);
    }
    
    /**
     * Adds the given item as a fuel for brewing stands, and assigns it the given fuel
     * value. 
     * 
     * This method should only be called during the FMLCommonSetupEvent. Calling this
     * method at any other time may cause brewing stands to stop working properly.
     * 
     * @param fuel
     *            The Item to set a fuel value for.
     * @param fuelTime
     *            The number of Items that this fuel lasts for.
     * @return The previous fuel value (if any) of the given Item.
     */
    public static int add(Item fuel, int fuelTime) {
        return FUEL_TIMES.put(fuel, fuelTime);
    }

    /**
     * Adds the given item as a fuel for brewing stands, and assigns the default
     * fuel value of blaze powder.
     * 
     * This method should only be called during the FMLCommonSetupEvent. Calling this
     * method at any other time may cause brewing stands to stop working properly.
     * 
     * @param fuel
     *            The Item to set a fuel value for.
     * @return The previous fuel value (if any) of the given Item.
     */
    public static int add(Item fuel) {
        return add(fuel, VANILLA_FUEL_TIME);
    }
    
    /**
     * Removes the given item as a fuel for brewing stands.
     * Use with care; this should not be done if at all possible.
     * Returns the previous fuel value of the item, if that is needed.
     * 
     * This method should only be called during the FMLCommonSetupEvent. Calling this
     * method at any other time may cause brewing stands to stop working properly.
     * 
     * @param fuel
     *            The Item to remove as a fuel option for brewing stands.
     * @return The original fuel value of the item.
     */
    public static int remove(Item fuel) {
        return FUEL_TIMES.removeInt(fuel);
    }

    /**
     * Retrieves the brewing stand fuel value of the given item. If the item is not
     * in the map, this function returns zero.
     * 
     * This method should only be called during the FMLCommonSetupEvent. Calling this
     * method at any other time may cause brewing stands to stop working properly.
     * 
     * @param fuel
     *            The Item to retrieve the fuel value of.
     * @return The fuel value of the given Item.
     */
    public static int get(Item fuel) {
        return FUEL_TIMES.getOrDefault(fuel, 0);
    }
}
