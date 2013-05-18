package net.minecraftforge.oredict;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class OreStack {
  
	public int oreID, stackSize;
	
	public OreStack(int id) { this(id, 1);	}
	
	public OreStack(String name) { this(OreDictionary.getOreID(name), 1); }
	
	public OreStack(String name, int size) { this(OreDictionary.getOreID(name), size);	}
	
	public OreStack(Item item) { this(OreDictionary.getOreID(new ItemStack(item)), 1);	}
	
	public OreStack(Item item, int size) { this(OreDictionary.getOreID(new ItemStack(item)), size);	}
	
	public OreStack(Block block) { this(OreDictionary.getOreID(new ItemStack(block)), 1); }
	
	public OreStack(Block block, int size) { this(OreDictionary.getOreID(new ItemStack(block)), size); }
	
	public OreStack(ItemStack stack) { this(OreDictionary.getOreID(stack), stack.stackSize); }
	
	public OreStack(int id, int size) {
		oreID = id;
		stackSize = size;
	}
	
	public OreStack copy()
	{
		return new OreStack(oreID, stackSize);
	}
	
	public String toString()
    	{
        	return stackSize + "x" + OreDictionary.getOreName(oreID);
    	}
	
	public boolean isItemEqual(ItemStack stack)
	{
		return OreDictionary.getOreID(stack) == oreID;
	}
}
