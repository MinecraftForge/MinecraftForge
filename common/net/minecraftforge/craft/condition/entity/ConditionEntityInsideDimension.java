package net.minecraftforge.craft.condition.entity;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.world.World;
import net.minecraftforge.craft.condition.BaseCondition;

public class ConditionEntityInsideDimension extends BaseCondition
{
    private final int _dimensionId;
    
	private ConditionEntityInsideDimension(int dimensionId)
	{
	    _dimensionId = dimensionId;
	}
	
	public static ConditionEntityInsideDimension isInside(int dimensionId)
	{
		return new ConditionEntityInsideDimension(dimensionId);
	}

	@Override
	public boolean isVerified(InventoryCrafting inventory, Entity crafter, World world, int x, int y, int z)
	{
		return crafter.worldObj.provider.dimensionId == _dimensionId;
	}
}
