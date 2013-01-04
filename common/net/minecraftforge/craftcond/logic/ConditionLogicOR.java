package net.minecraftforge.craftcond.logic;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.world.World;
import net.minecraftforge.craftcond.BaseCondition;

public class ConditionLogicOR extends BaseCondition
{
	private ConditionLogicOR(Object[] args)
	{
		super(args);
	}
	
	public static ConditionLogicOR compare(BaseCondition... conditions)
	{
		return new ConditionLogicOR(conditions);
	}

	@Override
	public boolean isVerified(InventoryCrafting inventory, Entity crafter, World world, int x, int y, int z)
	{
		for (Object cond : getArgs())
			if (((BaseCondition)cond).isVerified(inventory, crafter, world, x, y, z))
				return true;
		return false;
	}
}
