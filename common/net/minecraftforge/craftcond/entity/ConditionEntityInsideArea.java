package net.minecraftforge.craftcond.entity;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.world.World;
import net.minecraftforge.craftcond.BaseCondition;

public class ConditionEntityInsideArea extends BaseCondition
{
	private enum AreaType
	{
		AREA_X, AREA_Y, AREA_Z, AREA_XZ, AREA_XYZ
	}
	
	private final AreaType _areaType;
	
	private ConditionEntityInsideArea(AreaType areaType, Object[] args)
	{
		super(args);
		_areaType = areaType;
	}
	
	public static ConditionEntityInsideArea isBetweenX(int minX, int maxX)
	{
		return new ConditionEntityInsideArea(AreaType.AREA_X, new Object[] { minX, maxX });
	}
	
	public static ConditionEntityInsideArea isBetweenY(int minY, int maxY)
	{
		return new ConditionEntityInsideArea(AreaType.AREA_Y, new Object[] { minY, maxY });
	}
	
	public static ConditionEntityInsideArea isBetweenZ(int minZ, int maxZ)
	{
		return new ConditionEntityInsideArea(AreaType.AREA_Z, new Object[] { minZ, maxZ });
	}
	
	public static ConditionEntityInsideArea isBetweenXZ(int minX, int minZ, int maxX, int maxZ)
	{
		return new ConditionEntityInsideArea(AreaType.AREA_XZ, new Object[] { minX, minZ, maxX, maxZ });
	}
	
	public static ConditionEntityInsideArea isBetweenXYZ(int minX, int minY, int minZ, int maxX, int maxY, int maxZ)
	{
		return new ConditionEntityInsideArea(AreaType.AREA_XYZ, new Object[] { minX, minY, minZ, maxX, maxY, maxZ });
	}

	@Override
	public boolean isVerified(InventoryCrafting inventory, Entity crafter, World world, int x, int y, int z)
	{
		switch (_areaType)
		{
			case AREA_X:
				return getIntArg(0) <= crafter.posX && crafter.posX <= getIntArg(1);
			case AREA_Y:
				return getIntArg(0) <= crafter.posY && crafter.posY <= getIntArg(1);
			case AREA_Z:
				return getIntArg(0) <= crafter.posZ && crafter.posZ <= getIntArg(1);
			case AREA_XZ:
				return getIntArg(0) <= crafter.posX && crafter.posX <= getIntArg(2)
				&& getIntArg(1) <= crafter.posZ && crafter.posZ <= getIntArg(3);
			case AREA_XYZ:
				return getIntArg(0) <= crafter.posX && crafter.posX <= getIntArg(3)
				&& getIntArg(1) <= crafter.posY && crafter.posY <= getIntArg(4)
				&& getIntArg(2) <= crafter.posZ && crafter.posZ <= getIntArg(5);
		}
		return false;
	}
}
