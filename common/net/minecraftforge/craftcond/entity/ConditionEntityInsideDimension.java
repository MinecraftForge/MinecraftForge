package net.minecraftforge.craftcond.entity;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.world.World;
import net.minecraftforge.craftcond.BaseCondition;

public class ConditionEntityInsideDimension extends BaseCondition
{
	private ConditionEntityInsideDimension(Object[] args)
	{
		super(args);
	}
	
	public static ConditionEntityInsideDimension isInside(int dimensionId)
	{
		return new ConditionEntityInsideDimension(new Object[] { dimensionId });
	}

	@Override
	public boolean isVerified(InventoryCrafting inventory, Entity crafter, World world, int x, int y, int z)
	{
		return crafter.worldObj.provider.dimensionId == getIntArg(0);
	}
}
