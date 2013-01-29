package net.minecraftforge.craft.condition.time;

import java.util.Calendar;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.world.World;
import net.minecraftforge.craft.condition.BaseCondition;
import net.minecraftforge.craft.condition.ConditionException;

public class ConditionTime extends BaseCondition
{
	private final int _dateType;
	private final int[] _time;
	
	private ConditionTime(int dateType, int... time)
	{
		_dateType = dateType;
		_time = time;
	}
	
	public static ConditionTime isYear(int year)
	{
		return new ConditionTime(Calendar.YEAR, year);
	}
	
	public static ConditionTime betweenYear(int minYear, int maxYear)
	{
	    if (minYear == maxYear)
	        return ConditionTime.isYear(minYear);
	    
		return new ConditionTime(Calendar.YEAR, minYear, maxYear);
	}
	
	public static ConditionTime isMonth(int month)
	{
	    if (month < 1 || month > 12)
	        throw new ConditionException("Month Must be Between January (1) and December (12)");
		return new ConditionTime(Calendar.MONTH, month-1);
	}
	
	public static ConditionTime betweenMonth(int minMonth, int maxMonth)
	{
	    if (minMonth == maxMonth)
            return ConditionTime.isMonth(minMonth);
	    
	    if (minMonth < 1 || minMonth > 12 || maxMonth < 1 || maxMonth > 12)
            throw new ConditionException("Month Must be Between January (1) and December (12)");
	    
		return new ConditionTime(Calendar.MONTH, minMonth-1, maxMonth-1);
	}
	
	public static ConditionTime isDay(int day)
	{
	    if (day < 1 || day > 31)
            throw new ConditionException("Day Must be Between 1 and 31");
	    
		return new ConditionTime(Calendar.DAY_OF_MONTH, day);
	}
	
	public static ConditionTime betweenDay(int minDay, int maxDay)
	{
	    if (minDay == maxDay)
            return ConditionTime.isDay(minDay);
	    
	    if (minDay < 1 || minDay > 31 || maxDay < 1 || maxDay > 31)
            throw new ConditionException("Day Must be Between 1 and 31");
        
		return new ConditionTime(Calendar.DAY_OF_MONTH, minDay, maxDay);
	}
	
	public static ConditionTime isWeekday(int weekday)
	{
	    if (weekday < 1 || weekday > 7)
	        throw new ConditionException("Weekday Must be Between Sunday (1) and Saturday (7)"); 
	    
		return new ConditionTime(Calendar.DAY_OF_WEEK, weekday);
	}
	
	public static ConditionTime betweenWeekday(int minWeekday, int maxWeekday)
	{
	    if (minWeekday == maxWeekday)
            return ConditionTime.isWeekday(minWeekday);
	    
	    if (minWeekday < 1 || minWeekday > 7 || maxWeekday < 1 || maxWeekday > 7)
            throw new ConditionException("Weekday Must be Between Sunday (1) and Saturday (7)");
	    
		return new ConditionTime(Calendar.DAY_OF_WEEK, minWeekday, maxWeekday);
	}
	
	public static ConditionTime isHour(int hour)
	{
	    if (hour < 0 || hour > 23)
	        throw new ConditionException("Hour Must be Between 0 and 23");
	    
		return new ConditionTime(Calendar.HOUR_OF_DAY, hour);
	}
	
	public static ConditionTime betweenHour(int minHour, int maxHour)
	{
	    if (minHour == maxHour)
            return ConditionTime.isHour(minHour);
	    
	    if (minHour < 0 || minHour > 23 || maxHour < 0 || maxHour > 23)
            throw new ConditionException("Hour Must be Between 0 and 23");
	    
		return new ConditionTime(Calendar.HOUR_OF_DAY, minHour, maxHour);
	}
	
	public static ConditionTime isMinute(int minute)
	{
	    if (minute < 0 || minute > 60)
	        throw new ConditionException("Minute Must be Between 0 and 59");
	    
		return new ConditionTime(Calendar.MINUTE, minute);
	}
	
	public static ConditionTime betweenMinute(int minMinute, int maxMinute)
	{
	    if (minMinute == maxMinute)
            return ConditionTime.isMinute(minMinute);
	    
	    if (minMinute < 0 || minMinute > 60 || maxMinute < 0 || maxMinute > 60)
            throw new ConditionException("Minute Must be Between 0 and 59");
	    
		return new ConditionTime(Calendar.MINUTE, minMinute, maxMinute);
	}

	@Override
	public boolean isVerified(InventoryCrafting inventory, Entity crafter, World world, int x, int y, int z)
	{
		Calendar calendar = Calendar.getInstance();
		
		switch (_time.length)
		{
    		case 0:
    		    return calendar.get(_dateType) == _time[0];
    		case 1:
    		    if (_time[0] < _time[1])
                    return _time[0] <= calendar.get(_dateType) && calendar.get(_dateType) <= _time[1];
                return calendar.get(_dateType) >= _time[0] || calendar.get(_dateType) <= _time[1];
		}
		return false;
	}
}
