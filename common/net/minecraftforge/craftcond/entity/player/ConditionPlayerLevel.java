package net.minecraftforge.craftcond.entity.player;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.world.World;
import net.minecraftforge.craftcond.BaseCondition;

public class ConditionPlayerLevel extends BaseCondition
{
	private enum ComparisionMode
	{
		EQUALS,
		LESS,
		MORE,
		BETWEEN
	}
	
	private final ComparisionMode _comparisionMode;
	
	private ConditionPlayerLevel(ComparisionMode comparisionMode, Object[] args)
	{
		super(args);
		_comparisionMode = comparisionMode;
	}
	
	public static ConditionPlayerLevel equals(int level)
	{
		return new ConditionPlayerLevel(ComparisionMode.EQUALS, new Object[] { level });
	}
	
	public static ConditionPlayerLevel lessThan(int level)
	{
		return new ConditionPlayerLevel(ComparisionMode.LESS, new Object[] { level });
	}
	
	public static ConditionPlayerLevel moreThan(int level)
	{
		return new ConditionPlayerLevel(ComparisionMode.MORE, new Object[] { level });
	}
	
	public static ConditionPlayerLevel between(int minLevel, int maxLevel)
	{
		return new ConditionPlayerLevel(ComparisionMode.BETWEEN, new Object[] { minLevel, maxLevel });
	}

	@Override
	public boolean isVerified(InventoryCrafting inventory, Entity crafter, World world, int x, int y, int z)
	{
		if (!(crafter instanceof EntityPlayer))
			return false;
		
		EntityPlayer player = (EntityPlayer)crafter;
		
		switch (_comparisionMode)
		{
			case EQUALS:
				return player.experienceLevel == getIntArg(0);
			case LESS:
				return player.experienceLevel < getIntArg(0);
			case MORE:
				return player.experienceLevel > getIntArg(0);
			case BETWEEN:
				return getIntArg(0) <= player.experienceLevel && player.experienceLevel <= getIntArg(1);
		}
		return false;
	}
}
