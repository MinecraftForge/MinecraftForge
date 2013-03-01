package net.minecraftforge.liquids;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event;

import com.google.common.collect.ImmutableMap;
import java.util.Arrays;
import java.util.List;

/**
 * When creating liquids you should register them with this class.
 *
 * @author CovertJaguar <railcraft.wikispaces.com>
 */
public abstract class LiquidDictionary
{

    private static Map<String, LiquidStack> liquids = new HashMap<String, LiquidStack>();
    private static Map<List<Integer>, String> tags = new HashMap<List<Integer>, String>();

    /**
     * When creating liquids you should call this function.
     *
     * Upon passing it a name and liquid item it will return either
     * a preexisting implementation of that liquid or the liquid passed in.
     *
     *
     * @param name the name of the liquid
     * @param liquid the liquid to use if one doesn't exist
     * @return the matching liquid stack
     */
    public static LiquidStack getOrCreateLiquid(String name, LiquidStack liquid)
    {
        LiquidStack existing = liquids.get(name);
        if(existing != null) {
            return existing.copy();
        }
        liquids.put(name, liquid.copy());
        tags.put(Arrays.asList(liquid.itemID, liquid.itemMeta), name);
        MinecraftForge.EVENT_BUS.post(new LiquidRegisterEvent(name, liquid));
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
     * @return a liquidstack for the requested liquid
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
    
    /**
     * Returns the name matching the liquid,
     * if such a liquid exists.
     *
     * Can return null.
     *
     * @param liquid the LiquidStack
     * @return String a unique tag for the liquid
     */
    public static String getLiquidName(LiquidStack liquid)
    {
        return tags.get(Arrays.asList(liquid.itemID, liquid.itemMeta));
    }

    /**
     * Get an immutable list of the liquids defined
     *
     * @return the defined liquids
     */
    public static Map<String, LiquidStack> getLiquids()
    {
        return ImmutableMap.copyOf(liquids);
    }
    /**
     * Fired when a new liquid is created
     *
     */
    public static class LiquidRegisterEvent extends Event
    {
        public final String Name;
        public final LiquidStack Liquid;

        public LiquidRegisterEvent(String name, LiquidStack liquid)
        {
            this.Name = name;
            this.Liquid = liquid.copy();
        }
    }
    
    static
    {
        getOrCreateLiquid("Water", new LiquidStack(Block.waterStill, LiquidContainerRegistry.BUCKET_VOLUME));
        getOrCreateLiquid("Lava", new LiquidStack(Block.lavaStill, LiquidContainerRegistry.BUCKET_VOLUME));
    }
}
