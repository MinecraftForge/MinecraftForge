package net.minecraftforge.craft.condition.world;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.world.World;
import net.minecraftforge.craft.condition.BaseCondition;
import net.minecraftforge.craft.condition.ConditionException;

public class ConditionWorldTime extends BaseCondition
{
    private static final int MIN_TIME = 0;
    private static final int MAX_TIME = 24000;
    
	private static final int DAWN = 0;
	private static final int MIDDAY = 6000;
	private static final int DUSK = 12000;
	private static final int MIDNIGHT = 18000;
	
	private final int _minTime;
	private final int _maxTime;
	
	private ConditionWorldTime(int minTime, int maxTime)
	{
	    _minTime = minTime;
	    _maxTime = maxTime;
	}

	public static ConditionWorldTime isBetween(int minTime, int maxTime)
	{
	    if (minTime == maxTime)
	        throw new ConditionException("Min Time and Max Time Must be Different");
	    
	    if (minTime < MIN_TIME || minTime > MAX_TIME || maxTime < MIN_TIME || maxTime > MAX_TIME)
	        throw new ConditionException("Time Must be Between 0 and 24000");
	    
		return new ConditionWorldTime(minTime, maxTime);
	}

	public static ConditionWorldTime isMoring()
	{
		return ConditionWorldTime.isBetween(DAWN, MIDDAY);
	}

	public static ConditionWorldTime isAfternoon()
	{
		return ConditionWorldTime.isBetween(MIDDAY, DUSK);
	}

	public static ConditionWorldTime isEvening()
	{
		return ConditionWorldTime.isBetween(DUSK, MIDNIGHT);
	}

	public static ConditionWorldTime isNight()
	{
		return ConditionWorldTime.isBetween(MIDNIGHT, DAWN);
	}

	@Override
	public boolean isVerified(InventoryCrafting inventory, Entity crafter, World world, int x, int y, int z)
	{
		if (_minTime < _maxTime)
			return _minTime <= world.getWorldTime() && world.getWorldTime() <= _maxTime;
		
		return world.getWorldTime() >= _minTime || world.getWorldTime() <= _maxTime;
	}
}
