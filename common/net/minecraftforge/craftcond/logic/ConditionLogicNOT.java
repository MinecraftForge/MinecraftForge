package net.minecraftforge.craftcond.logic;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.world.World;
import net.minecraftforge.craftcond.BaseCondition;

public class ConditionLogicNOT extends BaseCondition
{
	private ConditionLogicNOT(Object[] args)
	{
		super(args);
	}
	
	public static ConditionLogicNOT invert(BaseCondition condition)
	{
		return new ConditionLogicNOT(new Object[] { condition });
	}

	@Override
	public boolean isVerified(InventoryCrafting inventory, Entity crafter, World world, int x, int y, int z)
	{
		return !getConditionArg(0).isVerified(inventory, crafter, world, x, y, z);
	}
}
