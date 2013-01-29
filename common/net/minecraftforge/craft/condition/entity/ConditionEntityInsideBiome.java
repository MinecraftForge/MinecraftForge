package net.minecraftforge.craft.condition.entity;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.world.World;
import net.minecraftforge.craft.condition.BaseCondition;
import net.minecraftforge.craft.condition.ConditionException;

public class ConditionEntityInsideBiome extends BaseCondition
{
    private final String _biomeName;
    
	private ConditionEntityInsideBiome(String biomeName)
	{
		_biomeName = biomeName;
	}
	
	public static ConditionEntityInsideBiome isInside(String biomeName)
	{
	    if (biomeName == null)
            throw new ConditionException("Biome Name can't be NULL");
		return new ConditionEntityInsideBiome(biomeName);
	}

	@Override
	public boolean isVerified(InventoryCrafting inventory, Entity crafter, World world, int x, int y, int z)
	{
		return crafter.worldObj.getBiomeGenForCoords((int)crafter.posX, (int)crafter.posZ).biomeName.equalsIgnoreCase(_biomeName);
	}
}
