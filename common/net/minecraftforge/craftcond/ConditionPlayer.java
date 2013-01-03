package net.minecraftforge.craftcond;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.world.World;

public class ConditionPlayer extends Condition
{
	private enum PlayerCond
	{
		HAVE_ITEM,
		INSIDE_DIMENSION,
		BETWEEN_COORDINATES
	}
	
	public enum CoordinateType
	{
		COORD_X,
		COORD_Y,
		COORD_Z
	}
	
	private final PlayerCond _playerCond;
	private final Object[] _args;
	
	private ConditionPlayer(PlayerCond playerCond, Object[] args)
	{
		_playerCond = playerCond;
		_args = args;
	}
	
	public static ConditionPlayer haveInventoryItem(int itemId)
	{
		return new ConditionPlayer(PlayerCond.HAVE_ITEM, new Object[] { itemId });
	}
	
	public static ConditionPlayer isInsideDimension(int dimensionId)
	{
		return new ConditionPlayer(PlayerCond.INSIDE_DIMENSION, new Object[] { dimensionId });
	}
	
	public static ConditionPlayer isBetweenCoordinates(CoordinateType typeCoord, int minCoord, int maxCoord)
	{
		return new ConditionPlayer(PlayerCond.BETWEEN_COORDINATES, new Object[] { typeCoord, minCoord, maxCoord });
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
		}
		return false;
	}

}
