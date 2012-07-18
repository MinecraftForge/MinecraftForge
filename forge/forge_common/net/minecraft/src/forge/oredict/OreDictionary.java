package net.minecraft.src.forge.oredict;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.forge.IOreHandler;

public class OreDictionary
{
    private static class OreType
    {
        public ArrayList<ItemStack> staticIDOres = new ArrayList<ItemStack>();
        public ArrayList<ItemStack> allOres = new ArrayList<ItemStack>();
        
    }
    
    private static int maxID = 0;
    private static HashMap<String, Integer> oreIDs = new HashMap<String, Integer>();
    private static HashMap<Integer, OreType> oreStacks = new HashMap<Integer, OreType>();
    private static ArrayList<IOreHandler> oreHandlers = new ArrayList<IOreHandler>();
    
    private static OreType getOreType(Integer id)
    {
        OreType val = oreStacks.get(id);
        if (val == null)
        {
            val = new OreType();
            oreStacks.put(id, val);
        }
        return val;
    }

    /**
     * Gets the integer ID for the specified ore name. 
     * If the name does not have a ID it assigns it a new one.
     * 
     * @param name The unique name for this ore 'oreIron', 'ingotIron', etc..
     * @return A number representing the ID for this ore type
     */
    public static int getOreID(String name)
    {
        Integer val = oreIDs.get(name);
        if (val == null)
        {
            val = maxID++;
            oreIDs.put(name, val);
            oreStacks.put(val, new OreType());
        }
        return val;
    }
    
    /**
     * Reverse of getOreID, will not create new entries.
     * 
     * @param id The ID to translate to a string
     * @return The String name, or "Unknown" if not found.
     */
    public static String getOreName(int id)
    {
        for (Map.Entry<String, Integer> entry : oreIDs.entrySet())
        {
            if (id == entry.getValue())
            {
                return entry.getKey();
            }
        }
        return "Unknown";
    }
    
    /**
     * Retrieves the ArrayList of items that are registered to this ore type.
     * Creates the list as empty if it did not exist.
     *  
     * @param id The ore ID, see getOreID
     * @param includeDynamicID Whether to return items with changeable IDs
     * @return An arrayList containing ItemStacks registered for this ore
     */
    public static ArrayList<ItemStack> getOres(String name, boolean includeDynamicID)
    {
        return getOres(getOreID(name), includeDynamicID);
    }
    
    @Deprecated
    public static ArrayList<ItemStack> getOres(String name) {return getOres(name, false);}
    
    /**
     * Retrieves the ArrayList of items that are registered to this ore type.
     * Creates the list as empty if it did not exist.
     *  
     * @param id The ore ID, see getOreID
     * @param includeDynamic Whether to return items with changeable IDs
     * @return An arrayList containing ItemStacks registered for this ore
     */
    public static ArrayList<ItemStack> getOres(Integer id, boolean includeDynamic)
    {
        OreType val = getOreType(id);
        return includeDynamic ? val.allOres : val.staticIDOres;
    }
    
    @Deprecated
    public static ArrayList<ItemStack> getOres(Integer id) {return getOres(id, false);}
    
    /** 
     * Register a new ore handler.  
     * This will automatically call the handler with all current ores during 
     * registration, and every time a new ore is added later.
     * 
     * @param handler The Ore Handler
     */
    @Deprecated
    public static void registerOreHandler(IOreHandler handler)
    {
        oreHandlers.add(handler);
        
        HashMap<String, Integer> tmp = (HashMap<String, Integer>)oreIDs.clone();
        
        for(Map.Entry<String, Integer> entry : tmp.entrySet())
        {
            for(ItemStack stack : getOres(entry.getValue(), false))
            {
                handler.registerOre(entry.getKey(), stack);
            }
        }
    }
    
    //Convenience functions that make for cleaner code mod side. They all drill down to registerOre(String, int, ItemStack)
    public static void registerOre(String name, Item      ore){ registerOre(name, new ItemStack(ore));  }
    public static void registerOre(String name, Block     ore){ registerOre(name, new ItemStack(ore));  }
    public static void registerOre(String name, ItemStack ore){ registerOre(name, getOreID(name), ore); }
    public static void registerOre(int    id,   Item      ore){ registerOre(id,   new ItemStack(ore));  }
    public static void registerOre(int    id,   Block     ore){ registerOre(id,   new ItemStack(ore));  }
    public static void registerOre(int    id,   ItemStack ore){ registerOre(getOreName(id), id, ore);   }
    
    /**
     * Registers a ore item into the dictionary.
     * Raises the registerOre function in all registered handlers.
     * 
     * @param name The name of the ore
     * @param id The ID of the ore
     * @param ore The ore's ItemStack
     */
    private static void registerOre(String name, int id, ItemStack ore)
    {
        ore = ore.copy();
        
        OreType type = getOreType(id);
        if(!dynamicRegistration)
        {
            type.staticIDOres.add(ore);
            
            for(IOreHandler handler : oreHandlers)
            {
                handler.registerOre(name, ore);
            }
        }
        type.allOres.add(ore);
    }
    
    /**
     * Forge internal function - do not call this from a mod.
     * 
     * Sets whether registered ores should be considered to have changeable IDs.
     */
    public static void setDynamicRegistration(boolean value)
    {
        dynamicRegistration = value;
    }
    
    private static boolean dynamicRegistration = false;
    
    /**
     * Forge internal function - do not call this from a mod.
     * 
     * Unregisters all ores with changeable IDs.
     */
    public static void clearDynamicOres()
    {
        for(Map.Entry<Integer, OreType> entry : oreStacks.entrySet())
        {
            OreType type = entry.getValue();
            type.allOres = (ArrayList<ItemStack>)type.staticIDOres.clone();
        }
    }
}
