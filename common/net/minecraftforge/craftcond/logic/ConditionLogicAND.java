package net.minecraftforge.craftcond.logic;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.world.World;
import net.minecraftforge.craftcond.BaseCondition;

public class ConditionLogicAND extends BaseCondition
{
	private ConditionLogicAND(Object[] args)
	{
		super(args);
	}
	
	public static ConditionLogicAND compare(BaseCondition... conditions)
	{
		return new ConditionLogicAND(conditions);
	}

	@Override
	public boolean isVerified(InventoryCrafting inventory, Entity crafter, World world, int x, int y, int z)
	{
		if (getArgs().length < 2)
			return getConditionArg(0).isVerified(inventory, crafter, world, x, y, z);
		for (int i = 1; i < getArgs().length; i++)
			if (!(getConditionArg(0).isVerified(inventory, crafter, world, x, y, z) && getConditionArg(i).isVerified(inventory, crafter, world, x, y, z)))
				return false;
		return true;
	}
}
