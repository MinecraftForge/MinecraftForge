/**
 * This software is provided under the terms of the Minecraft Forge Public
 * License v1.0.
 */

package net.minecraftforge.common;

import java.util.ArrayList;

import net.minecraftforge.common.Property.Type;

public class Property
{
    public enum Type
    {
        STRING,
        INTEGER,
        BOOLEAN,
        DOUBLE;

        private static Type[] values = {STRING, INTEGER, BOOLEAN, DOUBLE};

        public static Type tryParse(char id)
        {
            for (int x = 0; x < values.length; x++)
            {
                if (values[x].getID() == id)
                {
                    return values[x];
                }
            }

            return STRING;
        }

        public char getID()
        {
            return name().charAt(0);
        }
    }

    private String name;
    public String value;
    public String comment;
    public String[] valueList;

    private final boolean wasRead;
    private final boolean isList;
    private final Type type;

    public Property()
    {
        wasRead = false;
        type    = null;
        isList  = false;
    }

    public Property(String name, String value, Type type)
    {
        this(name, value, type, false);
    }

    Property(String name, String value, Type type, boolean read)
    {
        setName(name);
        this.value = value;
        this.type  = type;
        wasRead    = read;
        isList     = false;
    }

    public Property(String name, String[] values, Type type)
    {
        this(name, values, type, false);
    }

    Property(String name, String[] values, Type type, boolean read)
    {
        setName(name);
        this.type  = type;
        valueList  = values;
        wasRead    = read;
        isList     = true;
    }

    /**
     * Returns the value in this property as an integer,
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
        {
            return Boolean.parseBoolean(value);
        }
        else
        {
            return _default;
        }
    }

    /**
     * Checks if the current value held by this property is a valid boolean value.
     * @return True if it is a boolean value
     */
    public boolean isBooleanValue()
    {
        return ("true".equals(value.toLowerCase()) || "false".equals(value.toLowerCase()));
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
        {
            try
            {
                nums.add(Integer.parseInt(value));
            }
            catch (NumberFormatException e){}
        }

        int[] primitives = new int[nums.size()];

        for (int i = 0; i < nums.size(); i++)
        {
            primitives[i] = nums.get(i);
        }

        return primitives;
    }

    /**
     * Checks if all of the current values stored in this property can be converted to an integer.
     * @return True if the type of the Property is an Integer List
     */
    public boolean isIntList()
    {
        for (String value : valueList)
        {
            try
            {
                Integer.parseInt(value);
            }
            catch (NumberFormatException e)
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the boolean value of all values that can
     * be parsed in the list.
     * 
     * @return Array of length 0 if none of the values could be parsed.
     */
    public boolean[] getBooleanList()
    {
        ArrayList<Boolean> values = new ArrayList<Boolean>();
        for (String value : valueList)
        {
            try
            {
                values.add(Boolean.parseBoolean(value));
            }
            catch (NumberFormatException e){}
        }

        boolean[] primitives = new boolean[values.size()];

        for (int i = 0; i < values.size(); i++)
        {
            primitives[i] = values.get(i);
        }

        return primitives;
    }

    /**
     * Checks if all of current values stored in this property can be converted to a boolean.
     * @return True if it is a boolean value
     */
    public boolean isBooleanList()
    {
        for (String value : valueList)
        {
            if (!"true".equalsIgnoreCase(value) && !"false".equalsIgnoreCase(value))
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns the double value of all values that can
     * be parsed in the list.
     * 
     * @return Array of length 0 if none of the values could be parsed.
     */
    public double[] getDoubleList()
    {
        ArrayList<Double> values = new ArrayList<Double>();
        for (String value : valueList)
        {
            try
            {
                values.add(Double.parseDouble(value));
            }
            catch (NumberFormatException e) {}
        }

        double[] primitives = new double[values.size()];

        for (int i = 0; i < values.size(); i++)
        {
            primitives[i] = values.get(i);
        }

        return primitives;
    }

    /**
     * Checks if all of the current values stored in this property can be converted to a double.
     * @return True if the type of the Property is a double List
     */
    public boolean isDoubleList()
    {
        for (String value : valueList)
        {
            try
            {
                Double.parseDouble(value);
            }
            catch (NumberFormatException e)
            {
                return false;
            }
        }

        return true;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Determines if this config value was just created, or if it was read from the config file.
     * This is useful for mods who auto-assign there blocks to determine if the ID returned is 
     * a configured one, or a automatically generated one.
     * 
     * @return True if this property was loaded from the config file with a value
     */
    public boolean wasRead()
    {
        return wasRead;
    }

    public Type getType()
    {
        return type;
    }

    public boolean isList()
    {
        return isList;
    }
}
