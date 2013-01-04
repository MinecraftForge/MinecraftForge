package net.minecraftforge.craftcond.world;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.world.World;
import net.minecraftforge.craftcond.BaseCondition;

public class ConditionWorldTime extends BaseCondition
{
	private static final int DAWN = 0;
	private static final int MIDDAY = 6000;
	private static final int DUSK = 12000;
	private static final int MIDNIGHT = 18000;
	
	private ConditionWorldTime(Object[] args)
	{
		super(args);
	}

	public static ConditionWorldTime isBetween(int time1, int time2)
	{
		return new ConditionWorldTime(new Object[] { time1, time2 });
	}

	public static ConditionWorldTime isMoring()
	{
		return new ConditionWorldTime(new Object[] { DAWN, MIDDAY });
	}

	public static ConditionWorldTime isAfternoon()
	{
		return new ConditionWorldTime(new Object[] { MIDDAY, DUSK });
	}

	public static ConditionWorldTime isEvening()
	{
		return new ConditionWorldTime(new Object[] { DUSK, MIDNIGHT });
	}

	public static ConditionWorldTime isNight()
	{
		return new ConditionWorldTime(new Object[] { MIDNIGHT, DAWN });
	}

	@Override
	public boolean isVerified(InventoryCrafting inventory, Entity crafter, World world, int x, int y, int z)
	{
		if (getIntArg(0) < getIntArg(1))
			return getIntArg(0) <= world.getWorldTime() && world.getWorldTime() <= getIntArg(1);
		else if (getIntArg(0) == getIntArg(1))
			return getIntArg(0) == world.getWorldTime();
		else
			return world.getWorldTime() >= getIntArg(0) || world.getWorldTime() <= getIntArg(1);
	}
}
