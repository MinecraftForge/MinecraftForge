package net.minecraftforge.craftcond;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.world.World;

public class ConditionPlayer extends Condition
{
	private enum PlayerConditions
	{
		HAVE_ITEM,
		INSIDE_DIMENSION,
		BETWEEN_COORDINATES,
		INSIDE_BIOME
	}
	
	public enum CoordinateType
	{
		COORD_X,
		COORD_Y,
		COORD_Z
	}
	
	private final PlayerConditions _playerCond;
	private final Object[] _args;
	
	private ConditionPlayer(PlayerConditions playerCond, Object[] args)
	{
		_playerCond = playerCond;
		_args = args;
	}
	
	public static ConditionPlayer haveInventoryItem(int itemId)
	{
		return new ConditionPlayer(PlayerConditions.HAVE_ITEM, new Object[] { itemId });
	}
	
	public static ConditionPlayer isInsideDimension(int dimensionId)
	{
		return new ConditionPlayer(PlayerConditions.INSIDE_DIMENSION, new Object[] { dimensionId });
	}
	
	public static ConditionPlayer isBetweenCoordinates(CoordinateType typeCoord, int minCoord, int maxCoord)
	{
		return new ConditionPlayer(PlayerConditions.BETWEEN_COORDINATES, new Object[] { typeCoord, minCoord, maxCoord });
	}
	
	public static ConditionPlayer isInsideBiome(String biomeName)
	{
		return new ConditionPlayer(PlayerConditions.INSIDE_BIOME, new Object[] { biomeName });
	}
	
	@Override
	public boolean isVerified(InventoryCrafting inventory, Entity crafter, World world, int x, int y, int z)
	{
		if (crafter == null || !(crafter instanceof EntityPlayer))
			return false;
		
		switch (_playerCond)
		{
			case HAVE_ITEM:
				return ((EntityPlayer)crafter).inventory.hasItem((Integer)_args[0]);
			case INSIDE_DIMENSION:
				return crafter.worldObj.provider.dimensionId == (Integer)_args[0];
			case BETWEEN_COORDINATES:
			{
				switch ((CoordinateType)_args[0])
				{
					case COORD_X:
						return crafter.posX >= (Integer)_args[1] && crafter.posX <= (Integer)_args[2];
					case COORD_Y:
						return crafter.posY >= (Integer)_args[1] && crafter.posY <= (Integer)_args[2];
					case COORD_Z:
						return crafter.posZ >= (Integer)_args[1] && crafter.posZ <= (Integer)_args[2];
				}
				return false;
			}
			case INSIDE_BIOME:
				return crafter.worldObj.getBiomeGenForCoords((int)crafter.posX, (int)crafter.posZ).biomeName.equalsIgnoreCase((String)_args[0]);
		}
		return false;
	}

}
