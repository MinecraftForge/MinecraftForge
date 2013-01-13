package net.minecraftforge.craft.condition.entity;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.world.World;
import net.minecraftforge.craft.condition.BaseCondition;

public class ConditionEntityInsideArea extends BaseCondition
{
	private enum AreaType
	{
		AREA_X, AREA_Y, AREA_Z, AREA_XZ, AREA_XYZ
	}
	
	private final AreaType _areaType;
	private final int[] _coordinates;
	
	private ConditionEntityInsideArea(AreaType areaType, int... coordinates)
	{
		_areaType = areaType;
		_coordinates = coordinates;
	}
	
	public static ConditionEntityInsideArea isBetweenX(int minX, int maxX)
	{
		return new ConditionEntityInsideArea(AreaType.AREA_X, minX, maxX);
	}
	
	public static ConditionEntityInsideArea isBetweenY(int minY, int maxY)
	{
		return new ConditionEntityInsideArea(AreaType.AREA_Y, minY, maxY);
	}
	
	public static ConditionEntityInsideArea isBetweenZ(int minZ, int maxZ)
	{
		return new ConditionEntityInsideArea(AreaType.AREA_Z, minZ, maxZ);
	}
	
	public static ConditionEntityInsideArea isBetweenXZ(int minX, int minZ, int maxX, int maxZ)
	{
		return new ConditionEntityInsideArea(AreaType.AREA_XZ, minX, minZ, maxX, maxZ);
	}
	
	public static ConditionEntityInsideArea isBetweenXYZ(int minX, int minY, int minZ, int maxX, int maxY, int maxZ)
	{
		return new ConditionEntityInsideArea(AreaType.AREA_XYZ, minX, minY, minZ, maxX, maxY, maxZ);
	}

	@Override
	public boolean isVerified(InventoryCrafting inventory, Entity crafter, World world, int x, int y, int z)
	{
		switch (_areaType)
		{
			case AREA_X:
				return _coordinates[0] <= crafter.posX && crafter.posX <= _coordinates[1];
			case AREA_Y:
				return _coordinates[0] <= crafter.posY && crafter.posY <= _coordinates[1];
			case AREA_Z:
				return _coordinates[0] <= crafter.posZ && crafter.posZ <= _coordinates[1];
			case AREA_XZ:
				return _coordinates[0] <= crafter.posX && crafter.posX <= _coordinates[2]
				&& _coordinates[1] <= crafter.posZ && crafter.posZ <= _coordinates[3];
			case AREA_XYZ:
				return _coordinates[0] <= crafter.posX && crafter.posX <= _coordinates[3]
				&& _coordinates[1] <= crafter.posY && crafter.posY <= _coordinates[4]
				&& _coordinates[2] <= crafter.posZ && crafter.posZ <= _coordinates[5];
		}
		return false;
	}
}
