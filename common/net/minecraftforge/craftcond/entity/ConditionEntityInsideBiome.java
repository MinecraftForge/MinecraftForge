package net.minecraftforge.craftcond.entity;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.world.World;
import net.minecraftforge.craftcond.BaseCondition;

public class ConditionEntityInsideBiome extends BaseCondition
{
	private ConditionEntityInsideBiome(Object[] args)
	{
		super(args);
	}
	
	public static ConditionEntityInsideBiome isInside(String biomeName)
	{
		return new ConditionEntityInsideBiome(new Object[] { biomeName });
	}

	@Override
	public boolean isVerified(InventoryCrafting inventory, Entity crafter, World world, int x, int y, int z)
	{
		return crafter.worldObj.getBiomeGenForCoords((int)crafter.posX, (int)crafter.posZ).biomeName.equalsIgnoreCase(getStringArg(0));
	}
}
