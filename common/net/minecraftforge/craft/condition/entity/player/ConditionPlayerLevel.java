package net.minecraftforge.craft.condition.entity.player;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.world.World;
import net.minecraftforge.craft.condition.BaseCondition;

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
	private final int[] _level;
	
	private ConditionPlayerLevel(ComparisionMode comparisionMode, int... level)
	{
		_comparisionMode = comparisionMode;
		_level = level;
	}
	
	public static ConditionPlayerLevel equals(int level)
	{
		return new ConditionPlayerLevel(ComparisionMode.EQUALS, level);
	}
	
	public static ConditionPlayerLevel lessThan(int level)
	{
		return new ConditionPlayerLevel(ComparisionMode.LESS, level);
	}
	
	public static ConditionPlayerLevel moreThan(int level)
	{
		return new ConditionPlayerLevel(ComparisionMode.MORE, level);
	}
	
	public static ConditionPlayerLevel between(int minLevel, int maxLevel)
	{
	    if (minLevel == maxLevel)
	        return ConditionPlayerLevel.equals(minLevel);
	    
	    if (minLevel > maxLevel)
	        return new ConditionPlayerLevel(ComparisionMode.BETWEEN, maxLevel, minLevel);
	    
		return new ConditionPlayerLevel(ComparisionMode.BETWEEN, minLevel, maxLevel);
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
				return player.experienceLevel == _level[0];
			case LESS:
				return player.experienceLevel < _level[0];
			case MORE:
				return player.experienceLevel > _level[0];
			case BETWEEN:
				return _level[0] <= player.experienceLevel && player.experienceLevel <= _level[1];
		}
		return false;
	}
}
