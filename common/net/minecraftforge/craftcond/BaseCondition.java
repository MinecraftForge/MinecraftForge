package net.minecraftforge.craftcond;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.world.World;

public abstract class BaseCondition
{
	private final Object[] _args;
	
	protected BaseCondition(Object[] args)
	{
		_args = args;
	}
	
	protected Object[] getArgs()
	{
		return _args;
	}
	
	protected int getIntArg(int index)
	{
		return (Integer)_args[index];
	}
	
	protected String getStringArg(int index)
	{
		return (String)_args[index];
	}
	
	protected BaseCondition getConditionArg(int index)
	{
		return (BaseCondition)_args[index];
	}
	
	public abstract boolean isVerified(InventoryCrafting inventory, Entity crafter, World world, int x, int y, int z);
}
