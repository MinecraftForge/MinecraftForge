/**
 * This software is provided under the terms of the Minecraft Forge Public
 * License v1.0.
 */

package net.minecraftforge.common;

import java.util.ArrayList;

public class Property
{
	public enum Type
	{
		STRING,
		INTEGER,
		BOOLEAN,
		DOUBLE;

		public char getIDChar()
		{
			return name().charAt(0);
		}
	}

	public String			name;
	public String			value;
	public String[]			valueList;	// used for List configs
	public String			comment;
	public final boolean	isList;
	public final Type		type;

	@Deprecated
	public Property()
	{
		type = null;
		isList = false;
	}

	public Property(String name, String value, Type type)
	{
		this.name = name;
		this.value = value;
		valueList = new String[] { value };
		this.type = type;
		isList = false;
	}

	public Property(String name, String[] value, Type type)
	{
		this.name = name;
		valueList = value;

		StringBuilder builder = new StringBuilder();
		for (String string : value)
			builder.append("|").append(string);

		this.value = builder.toString();

		this.type = type;

		isList = true;
	}

	/**
	 * Returns the value in this property as a integer,
	 * if the value is not a valid integer, it will return -1.
	 * 
	 * @return The value
	 */
	public int getInt()
	{
		return getInt(-1);
	}

	/**
	 * Returns the value in this property as an integer,
	 * if the value is not a valid integer, it will return the
	 * provided default.
	 * 
	 * @param _default The default to provide if the current value is not a valid integer
	 * @return The value
	 */
	public int getInt(int _default)
	{
		try
		{
			return Integer.parseInt(value);
		}
		catch (NumberFormatException e)
		{
			return _default;
		}
	}

	/**
	 * Checks if the current value stored in this property can be converted to an integer.
	 * @return True if the type of the Property is an Integer
	 */
	public boolean isIntValue()
	{
		try
		{
			Integer.parseInt(value);
			return true;
		}
		catch (NumberFormatException e)
		{
			return false;
		}
	}

	/**
	 * Returns the value in this property as a boolean,
	 * if the value is not a valid boolean, it will return the
	 * provided default.
	 * 
	 * @param _default The default to provide
	 * @return The value as a boolean, or the default
	 */
	public boolean getBoolean(boolean _default)
	{
		if (isBooleanValue())
			return Boolean.parseBoolean(value);
		else
			return _default;
	}

	/**
	 * Checks if the current value held by this property is a valid boolean value.
	 * @return True if it is a boolean value
	 */
	public boolean isBooleanValue()
	{
		return "true".equals(value.toLowerCase()) || "false".equals(value.toLowerCase());
	}

	/**
	 * Checks if the current value held by this property is a valid double value.
	 * @return True if the value can be converted to an double
	 */
	public boolean isDoubleValue()
	{
		try
		{
			Double.parseDouble(value);
			return true;
		}
		catch (NumberFormatException e)
		{
			return false;
		}
	}

	/**
	 * Returns the value in this property as a double,
	 * if the value is not a valid double, it will return the
	 * provided default.
	 * 
	 * @param _default The default to provide if the current value is not a valid double
	 * @return The value
	 */
	public double getDouble(double _default)
	{
		try
		{
			return Double.parseDouble(value);
		}
		catch (NumberFormatException e)
		{
			return _default;
		}
	}

	/**
	 * Returns the integer value of all values that can
	 * be parsed in the list.
	 * 
	 * @return Array of length 0 if none of the values could be parsed.
	 */
	public int[] getIntList()
	{
		ArrayList<Integer> nums = new ArrayList<Integer>();
		for (String value : valueList)
			try
			{
				nums.add(Integer.parseInt(value));
			}
			catch (NumberFormatException e)
			{
				continue;
			}

		int[] primitives = new int[nums.size()];

		for (int i = 0; i < nums.size(); i++)
			primitives[i] = nums.get(i);

		return primitives;
	}

	/**
	 * Checks if at least one of the current values stored in this property can be converted to an integer.
	 * @return True if the type of the Property is an Integer List
	 */
	public boolean isIntList()
	{
		for (String value : valueList)
			try
			{
				Integer.parseInt(value);
				return true;
			}
			catch (NumberFormatException e)
			{
				return false;
			}
		return false;
	}

	/**
	 * Returns the boolean value of all values that can
	 * be parsed in the list.
	 * 
	 * @return Array of length 0 if none of the values could be parsed.
	 */
	public boolean[] getBooleanList()
	{
		ArrayList<Boolean> nums = new ArrayList<Boolean>();
		for (String value : valueList)
			try
			{
				nums.add(Boolean.parseBoolean(value));
			}
			catch (NumberFormatException e)
			{
				continue;
			}

		boolean[] primitives = new boolean[nums.size()];

		for (int i = 0; i < nums.size(); i++)
			primitives[i] = nums.get(i);

		return primitives;
	}

	/**
	 * Checks if at least one of the current values stored in this property can be converted to a boolean.
	 * @return True if it is a boolean value
	 */
	public boolean isBooleanList()
	{
		for (String value : valueList)
		{
			if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value))
				;
			return true;
		}

		return false;
	}

	/**
	 * Returns the double value of all values that can
	 * be parsed in the list.
	 * 
	 * @return Array of length 0 if none of the values could be parsed.
	 */
	public double[] getDoubleList()
	{
		ArrayList<Double> nums = new ArrayList<Double>();
		for (String value : valueList)
			try
			{
				nums.add(Double.parseDouble(value));
			}
			catch (NumberFormatException e)
			{
				continue;
			}

		double[] primitives = new double[nums.size()];

		for (int i = 0; i < nums.size(); i++)
			primitives[i] = nums.get(i);

		return primitives;
	}

	/**
	 * Checks if at least one of the current values stored in this property can be converted to a double.
	 * @return True if the type of the Property is a double List
	 */
	public boolean isDoubleList()
	{
		for (String value : valueList)
			try
			{
				Double.parseDouble(value);
				return true;
			}
			catch (NumberFormatException e)
			{
				return false;
			}
		return false;
	}

	public Type getType()
	{
		return type;
	}

	/**
	 * @deprecated use the public var name instead
	 */
	@Deprecated
	public String getName()
	{
		return name;
	}

	/**
	 * @deprecated use the public var name instead
	 */
	@Deprecated
	public void setName(String name)
	{
		this.name = name;
	}
}
