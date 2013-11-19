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
    private String value;
    public String comment;
    private String[] values;

    private final boolean wasRead;
    private final boolean isList;
    private final Type type;
    private boolean changed = false;

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
        this.type   = type;
        this.values = values;
        wasRead     = read;
        isList      = true;
    }

    /**
     * Returns the value in this property as it's raw string.
     * 
     * @return current value
     */
    public String getString()
    {
        return value;
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

    public String[] getStringList()
    {
        return values;
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
        
        for (String value : values)
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
        for (String value : values)
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
        ArrayList<Boolean> tmp = new ArrayList<Boolean>();
        for (String value : values)
        {
            try
            {
                tmp.add(Boolean.parseBoolean(value));
            }
            catch (NumberFormatException e){}
        }

        boolean[] primitives = new boolean[tmp.size()];

        for (int i = 0; i < tmp.size(); i++)
        {
            primitives[i] = tmp.get(i);
        }

        return primitives;
    }

    /**
     * Checks if all of current values stored in this property can be converted to a boolean.
     * @return True if it is a boolean value
     */
    public boolean isBooleanList()
    {
        for (String value : values)
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
        ArrayList<Double> tmp = new ArrayList<Double>();
        for (String value : values)
        {
            try
            {
                tmp.add(Double.parseDouble(value));
            }
            catch (NumberFormatException e) {}
        }

        double[] primitives = new double[tmp.size()];

        for (int i = 0; i < tmp.size(); i++)
        {
            primitives[i] = tmp.get(i);
        }

        return primitives;
    }

    /**
     * Checks if all of the current values stored in this property can be converted to a double.
     * @return True if the type of the Property is a double List
     */
    public boolean isDoubleList()
    {
        for (String value : values)
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

    public boolean hasChanged(){ return changed; }
    void resetChangedState(){ changed = false; }

    public void set(String value)
    {
        this.value = value;
        changed = true;
    }

    public void set(String[] values)
    {
        this.values = values;
        changed = true;
    }

    public void set(int     value){ set(Integer.toString(value)); }
    public void set(boolean value){ set(Boolean.toString(value)); }
    public void set(double  value){ set(Double.toString(value));  }
}
