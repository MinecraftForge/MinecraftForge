package net.minecraftforge.craftcond.time;

import java.util.Calendar;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.world.World;
import net.minecraftforge.craftcond.BaseCondition;

public class ConditionTime extends BaseCondition
{
	private enum DateType
	{
		YEAR(Calendar.YEAR),
		MONTH(Calendar.MONTH),
		DAY(Calendar.DAY_OF_MONTH),
		WEEKDAY(Calendar.DAY_OF_WEEK),
		HOUR(Calendar.HOUR_OF_DAY),
		MINUTE(Calendar.MINUTE);
		
		private final int _calendarValue;
		
		private DateType(int calendarValue)
		{
			_calendarValue = calendarValue;
		}
		
		private int getCalendarValue()
		{
			return _calendarValue;
		}
	};
	
	private final DateType _dateType;
	
	private ConditionTime(DateType dateType, Object[] args)
	{
		super(args);
		_dateType = dateType;
	}
	
	public static ConditionTime isYear(int year)
	{
		return new ConditionTime(DateType.YEAR, new Object[] { year });
	}
	
	public static ConditionTime betweenYear(int minYear, int maxYear)
	{
		return new ConditionTime(DateType.YEAR, new Object[] { minYear, maxYear });
	}
	
	public static ConditionTime isMonth(int month)
	{
		return new ConditionTime(DateType.MONTH, new Object[] { month });
	}
	
	public static ConditionTime betweenMonth(int minMonth, int maxMonth)
	{
		return new ConditionTime(DateType.MONTH, new Object[] { minMonth, maxMonth });
	}
	
	public static ConditionTime isDay(int day)
	{
		return new ConditionTime(DateType.DAY, new Object[] { day });
	}
	
	public static ConditionTime betweenDay(int minDay, int maxDay)
	{
		return new ConditionTime(DateType.DAY, new Object[] { minDay, maxDay });
	}
	
	public static ConditionTime isWeekday(int weekday)
	{
		return new ConditionTime(DateType.WEEKDAY, new Object[] { weekday });
	}
	
	public static ConditionTime betweenWeekday(int minWeekday, int maxWeekday)
	{
		return new ConditionTime(DateType.WEEKDAY, new Object[] { minWeekday, maxWeekday });
	}
	
	public static ConditionTime isHour(int hour)
	{
		return new ConditionTime(DateType.HOUR, new Object[] { hour });
	}
	
	public static ConditionTime betweenHour(int minHour, int maxHour)
	{
		return new ConditionTime(DateType.HOUR, new Object[] { minHour, maxHour });
	}
	
	public static ConditionTime isMinute(int minute)
	{
		return new ConditionTime(DateType.MINUTE, new Object[] { minute });
	}
	
	public static ConditionTime betweenMinute(int minMinute, int maxMinute)
	{
		return new ConditionTime(DateType.MINUTE, new Object[] { minMinute, maxMinute });
	}

	@Override
	public boolean isVerified(InventoryCrafting inventory, Entity crafter, World world, int x, int y, int z)
	{
		Calendar calendar = Calendar.getInstance();
		
		if (getArgs().length == 1)
			return calendar.get(_dateType.getCalendarValue()) == getIntArg(0);
		
		if (getArgs().length == 2)
		{
			if (getIntArg(0) < getIntArg(1))
				return getIntArg(0) <= calendar.get(_dateType.getCalendarValue()) && calendar.get(_dateType.getCalendarValue()) <= getIntArg(1);
			return calendar.get(_dateType.getCalendarValue()) >= getIntArg(0) || calendar.get(_dateType.getCalendarValue()) <= getIntArg(1);
		}
		return false;
	}
}
