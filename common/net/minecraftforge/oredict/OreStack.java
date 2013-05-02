package net.minecraftforge.oredict;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import static net.minecraftforge.oredict.OreDictionary.WILDCARD_VALUE;
import static net.minecraftforge.oredict.OreDictionary.getOreID;
import static net.minecraftforge.oredict.OreDictionary.getOreName;
import static net.minecraftforge.oredict.OreDictionary.getOres;

/**
 * The OreStack for use in mod recipes that support ItemStacks with size larger than 1.
 * 
 * @author ExE Boss
 */
public final class OreStack
{
	/** The oredictionary name */
	public String ore;
	public ArrayList<ItemStack> ores = new ArrayList<ItemStack>();
	
	public OreStack(String ore, int stackSize)
	{
		this.ore = ore;
		this.ores = getOres(ore);
		this.setStackSize(stackSize);
	}
	
	public OreStack(ItemStack stack)
	{
		this.ore = getOreName(OreDictionary.getOreID(stack));
		if(this.ore != "Unknown")
			this.ores = getOres(this.ore);
		else this.ores.add(stack);
	}
	
	public OreStack(int id, int stackSize, int damage)
	{
		this(new ItemStack(id, stackSize, damage));
	}
	
	public OreStack(Item item, int stackSize, int damage)
	{
		this(item.itemID, stackSize, damage);
	}
	
	public OreStack(Item item, int stackSize)
	{
		this(item, stackSize, WILDCARD_VALUE);
	}
	
	public OreStack(Item item)
	{
		this(item, 1);
	}
	
	public OreStack(Block block, int stackSize, int damage)
	{
		this(block.blockID, stackSize, damage);
	}
	
	public OreStack(Block block, int stackSize)
	{
		this(item, stackSize, WILDCARD_VALUE);
	}
	
	public OreStack(Block block)
	{
		this(item, 1);
	}
	
	public void setStackSize(int stackSize)
	{
		for(ItemStack ore : ores)
			ore.stackSize = stackSize;
	}
	
	public int getStackSize()
	{
		return ores.get(0).stackSize;
	}
}