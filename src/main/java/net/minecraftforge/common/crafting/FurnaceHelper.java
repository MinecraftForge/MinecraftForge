package net.minecraftforge.common.crafting;

import java.util.Collections;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

public class FurnaceHelper 
{
    private static Map<Item, Integer> burnTimes;
    
    private static Map<Item, Integer> getBurnTimes()
    {
        return burnTimes != null ? burnTimes : (burnTimes = Collections.unmodifiableMap(TileEntityFurnace.getBurnTimes()));
    }
    
    public static int getItemBurnTime(ItemStack stack) 
    {
          if (stack.isEmpty()) 
          {
             return 0;
          }
          else
          {
             Item item = stack.getItem();
             int base = stack.getBurnTime();
             return net.minecraftforge.event.ForgeEventFactory.getItemBurnTime(stack, base == -1 ? getBurnTimes().getOrDefault(item, 0) : base);
          }
    }
    
    public static void invalidate()
    {
        burnTimes = null;
    }
}
