package net.minecraftforge.craftcond;

import java.util.Calendar;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.world.World;

public class ConditionDate extends Condition
{
	public enum DateType
	{
		DATE_YEAR,
		DATE_MONTH,
		DATE_DAY,
		DATE_WEEKDAY,
		DATE_HOUR,
		DATE_MINUTE
	};

	private final DateType _dateType;
	private final int _dateValue;
	private final int _dateValueMax;

	private ConditionDate(DateType dateType, int dateValue, int dateValueMax)
	{
		_dateType = dateType;
		_dateValue = dateValue;
		_dateValueMax = dateValueMax;
	}

	public static ConditionDate onTime(DateType dateType, int dateValue)
	{
		return new ConditionDate(dateType, dateValue, -1);
	}

	public static ConditionDate betweenTime(DateType dateType, int dateValue, int dateValueMax)
	{
		if (dateValue != dateValueMax)
			return new ConditionDate(dateType, dateValue, dateValueMax);
		return new ConditionDate(dateType, dateValue, -1);
	}

	@Override
	public boolean isVerified(InventoryCrafting inventory, Entity crafter, World world, int x, int y, int z)
	{
		Calendar calendar = Calendar.getInstance();

		if (_dateValueMax == -1)
		{
			switch (_dateType)
			{
				case DATE_YEAR:
					return calendar.get(Calendar.YEAR) == _dateValue;
				case DATE_MONTH:
					return calendar.get(Calendar.MONTH) == _dateValue;
				case DATE_DAY:
					return calendar.get(Calendar.DAY_OF_MONTH) == _dateValue;
				case DATE_WEEKDAY:
					return calendar.get(Calendar.DAY_OF_WEEK) == _dateValue;
				case DATE_HOUR:
					return calendar.get(Calendar.HOUR_OF_DAY) == _dateValue;
				case DATE_MINUTE:
					return calendar.get(Calendar.MINUTE) == _dateValue;
			}
		}
		else
		{
			switch (_dateType)
			{
				case DATE_YEAR:
					if (_dateValue < _dateValueMax)
						return calendar.get(Calendar.YEAR) >= _dateValue && _dateValueMax <= calendar.get(Calendar.YEAR);
					else
						return calendar.get(Calendar.YEAR) >= _dateValue || _dateValueMax <= calendar.get(Calendar.YEAR);
				case DATE_MONTH:
					if (_dateValue < _dateValueMax)
						return calendar.get(Calendar.MONTH) >= _dateValue && _dateValueMax <= calendar.get(Calendar.MONTH);
					else
						return calendar.get(Calendar.MONTH) >= _dateValue || _dateValueMax <= calendar.get(Calendar.MONTH);
				case DATE_DAY:
					if (_dateValue < _dateValueMax)
						return calendar.get(Calendar.DAY_OF_MONTH) >= _dateValue && _dateValueMax <= calendar.get(Calendar.DAY_OF_MONTH);
					else
						return calendar.get(Calendar.DAY_OF_MONTH) >= _dateValue || _dateValueMax <= calendar.get(Calendar.DAY_OF_MONTH);
				case DATE_WEEKDAY:
					if (_dateValue < _dateValueMax)
						return calendar.get(Calendar.DAY_OF_WEEK) >= _dateValue && _dateValueMax <= calendar.get(Calendar.DAY_OF_WEEK);
					else
						return calendar.get(Calendar.DAY_OF_WEEK) >= _dateValue || _dateValueMax <= calendar.get(Calendar.DAY_OF_WEEK);
				case DATE_HOUR:
					if (_dateValue < _dateValueMax)
						return calendar.get(Calendar.HOUR_OF_DAY) >= _dateValue && _dateValueMax <= calendar.get(Calendar.HOUR_OF_DAY);
					else
						return calendar.get(Calendar.HOUR_OF_DAY) >= _dateValue || _dateValueMax <= calendar.get(Calendar.HOUR_OF_DAY);
				case DATE_MINUTE:
					if (_dateValue < _dateValueMax)
						return calendar.get(Calendar.MINUTE) >= _dateValue && _dateValueMax <= calendar.get(Calendar.MINUTE);
					else
						return calendar.get(Calendar.MINUTE) >= _dateValue || _dateValueMax <= calendar.get(Calendar.MINUTE);
			}
		}

		return false;
	}
}
