package net.minecraftforge.craftcond;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.world.World;

public class ConditionWorld extends Condition
{
	private static final int DAWN = 0;
	private static final int MIDDAY = 6000;
	private static final int DUSK = 12000;
	private static final int MIDNIGHT = 18000;
	
	private enum WorldConditions
	{
		BETWEEN_TIME
	}
	
	private final WorldConditions _conditionType;
	private final Object[] _args;
	
	private ConditionWorld(WorldConditions conditionType, Object[] args)
	{
		_conditionType = conditionType;
		_args = args;
	}
	
	public static ConditionWorld isBetween(int time1, int time2)
	{
		return new ConditionWorld(WorldConditions.BETWEEN_TIME, new Object[] { time1, time2 });
	}
	
	public static ConditionWorld isMoring()
	{
		return new ConditionWorld(WorldConditions.BETWEEN_TIME, new Object[] { DAWN, MIDDAY });
	}
	
	public static ConditionWorld isAfternoon()
	{
		return new ConditionWorld(WorldConditions.BETWEEN_TIME, new Object[] { MIDDAY, DUSK });
	}
	
	public static ConditionWorld isEvening()
	{
		return new ConditionWorld(WorldConditions.BETWEEN_TIME, new Object[] { DUSK, MIDNIGHT });
	}
	
	public static ConditionWorld isNight()
	{
		return new ConditionWorld(WorldConditions.BETWEEN_TIME, new Object[] { MIDNIGHT, DAWN });
	}
	
	@Override
	public boolean isVerified(InventoryCrafting inventory, Entity crafter, World world, int x, int y, int z)
	{
		switch (_conditionType)
		{
		case BETWEEN_TIME:
			if ((Integer)_args[0] < (Integer)_args[1])
				return world.getWorldTime() >= (Integer)_args[0] && (Integer)_args[1] <= world.getWorldTime();
			else
				return world.getWorldTime() >= (Integer)_args[0] || (Integer)_args[1] <= world.getWorldTime();
		}
		return false;
	}
}
