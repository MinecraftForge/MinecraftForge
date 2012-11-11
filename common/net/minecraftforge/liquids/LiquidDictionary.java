package net.minecraftforge.liquids;

import java.util.HashMap;
import java.util.Map;

/**
 * When creating liquids you should register them with this class.
 *
 * @author CovertJaguar <railcraft.wikispaces.com>
 */
public abstract class LiquidDictionary
{

    private static Map<String, LiquidStack> liquids = new HashMap<String, LiquidStack>();

    /**
     * When creating liquids you should call this function.
     *
     * Upon passing it a name and liquid item it will return either
     * a preexisting implementation of that liquid or the liquid passed in.
     *
     *
     * @param name the name of the liquid
     * @param liquid the liquid to use if one doesn't exist
     * @return
     */
    public static LiquidStack getOrCreateLiquid(String name, LiquidStack liquid)
    {
        LiquidStack existing = liquids.get(name);
        if(existing != null) {
            return existing.copy();
        }
        liquids.put(name, liquid.copy());
        return liquid;
    }
    
    /**
     * Returns the liquid matching the name,
     * if such a liquid exists.
     * 
     * Can return null.
     * 
     * @param name the name of the liquid
     * @param amount the amout of liquid
     * @return
     */
    public static LiquidStack getLiquid(String name, int amount)
    {
        LiquidStack liquid = liquids.get(name);
        if(liquid == null) 
            return null;
            
        liquid = liquid.copy();
        liquid.amount = amount;
        return liquid;
    }
}
