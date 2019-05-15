package net.minecraftforge.common.crafting;

import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

public class FurnaceHelper 
{
	private static Map<Item, Integer> burnTimes, vanillaBurnTimes;
	
	public static Map<Item, Integer> getBurnTimes()
	{
		return burnTimes != null ? burnTimes : (burnTimes = Maps.newLinkedHashMap());
	}
	
	public static Map<Item, Integer> getVanillaBurnTimes()
	{
		return vanillaBurnTimes != null ? vanillaBurnTimes : (vanillaBurnTimes = TileEntityFurnace.getBurnTimes());
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
	         //I changed the original logic here. Previously every time ForgeEventFactory#getItemBurnTime was called, now the result is cahced aswell.
	         return getBurnTimes().computeIfAbsent(item, i -> net.minecraftforge.event.ForgeEventFactory.getItemBurnTime(stack, base == -1 ? getVanillaBurnTimes().getOrDefault(i, 0) : base));
	      }
	   }
}
