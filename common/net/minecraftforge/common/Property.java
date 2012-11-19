/**
 * This software is provided under the terms of the Minecraft Forge Public
 * License v1.0.
 */

package net.minecraftforge.common;

public class Property
{
    public enum Type
    {
        STRING,
        INTEGER,
        BOOLEAN,
        LIST,
        DOUBLE;
        
        public char getIDChar()
        {
            return this.name().charAt(0);
        }
    }

    public String name;
    public String value;
    public String[] valueList; // used for List configs
    public String comment;
    private Type type;
    
    public Property(){}
    
    public Property(String name, String value, Type type)
    {
        this.name = name;
        this.value = value;
        this.valueList = new String[] {value};
        this.type = type;
    }
    
    public Property(String name, String[] value)
    {
        this.name = name;
        this.valueList = value;
        
        StringBuilder builder = new StringBuilder();
        for (String string : value)
            builder.append("|").append(string);
        
        this.value = builder.toString();
        
        this.type = Type.LIST;
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
     * @return True if the type of the Property is a Double
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
