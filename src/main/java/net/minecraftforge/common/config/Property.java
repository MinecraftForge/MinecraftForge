/**
 * This software is provided under the terms of the Minecraft Forge Public
 * License v1.0.
 */

package net.minecraftforge.common.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

import net.minecraftforge.fml.client.config.GuiConfigEntries.IConfigEntry;
import net.minecraftforge.fml.client.config.GuiEditArrayEntries.IArrayEntry;

public class Property
{
    public enum Type
    {
        STRING,
        INTEGER,
        BOOLEAN,
        DOUBLE,
        COLOR,
        MOD_ID;

        public static Type tryParse(char id)
        {
            for (int x = 0; x < values().length; x++)
            {
                if (values()[x].getID() == id)
                {
                    return values()[x];
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
    private String defaultValue;
    public String comment;
    private String[] values;
    private String[] defaultValues;
    private String[] validValues;
    private String langKey;
    private String minValue;
    private String maxValue;

    private Class<? extends IConfigEntry> configEntryClass = null;
    private Class<? extends IArrayEntry> arrayEntryClass = null;

    private boolean requiresWorldRestart = false;
    private boolean showInGui = true;
    private boolean requiresMcRestart = false;
    private Pattern validationPattern;
    private final boolean wasRead;
    private final boolean isList;
    private boolean isListLengthFixed = false;
    private int maxListLength = -1;
    private final Type type;
    private boolean changed = false;

    public Property(String name, String value, Type type)
    {
        this(name, value, type, false, new String[0], name);
    }

    public Property(String name, String value, Type type, boolean read)
    {
        this(name, value, type, read, new String[0], name);
    }

    public Property(String name, String value, Type type, String[] validValues)
    {
        this(name, value, type, false, validValues, name);
    }

    public Property(String name, String value, Type type, String langKey)
    {
        this(name, value, type, false, new String[0], langKey);
    }

    public Property(String name, String value, Type type, boolean read, String langKey)
    {
        this(name, value, type, read, new String[0], langKey);
    }

    public Property(String name, String value, Type type, String[] validValues, String langKey)
    {
        this(name, value, type, false, validValues, langKey);
    }

    Property(String name, String value, Type type, boolean read, String[] validValues, String langKey)
    {
        setName(name);
        this.value = value;
        this.values = new String[0];
        this.type  = type;
        wasRead    = read;
        isList     = false;
        this.defaultValue = value;
        this.defaultValues = new String[0];
        this.validValues = validValues;
        this.isListLengthFixed = false;
        this.maxListLength = -1;
        this.minValue = String.valueOf(Integer.MIN_VALUE);
        this.maxValue = String.valueOf(Integer.MAX_VALUE);
        this.langKey = langKey;
        this.comment = "";
    }

    public Property(String name, String[] values, Type type)
    {
        this(name, values, type, false);
    }

    Property(String name, String[] values, Type type, boolean read)
    {
        this(name, values, type, read, new String[0], name);
    }

    public Property(String name, String[] values, Type type, String langKey)
    {
        this(name, values, type, false, langKey);
    }

    Property(String name, String[] values, Type type, boolean read, String langKey)
    {
        this(name, values, type, read, new String[0], langKey);
    }

    Property(String name, String[] values, Type type, boolean read, String[] validValues, String langKey)
    {
        setName(name);
        this.type   = type;
        this.values = Arrays.copyOf(values, values.length);
        wasRead     = read;
        isList      = true;
        this.value = "";
        this.defaultValue = "";
        for (String s : values)
            this.defaultValue += ", [" + s + "]";
        this.defaultValue = this.defaultValue.replaceFirst(", ", "");
        this.defaultValues = Arrays.copyOf(values, values.length);
        this.validValues = validValues;
        this.isListLengthFixed = false;
        this.maxListLength = -1;
        this.minValue = String.valueOf(Integer.MIN_VALUE);
        this.maxValue = String.valueOf(Integer.MAX_VALUE);
        this.langKey = langKey;
        this.comment = "";
    }

    /**
     * Returns whether or not this Property is defaulted.
     *
     * @return true if the current value(s) is(are) deeply equal to the default value(s)
     */
    public boolean isDefault()
    {
        if (this.isBooleanList())
        {
            if (values.length == defaultValues.length)
            {
                for (int i = 0; i < values.length; i++)
                    if (Boolean.parseBoolean(values[i]) != Boolean.parseBoolean(defaultValues[i]))
                        return false;

                return true;
            }
            else
                return false;
        }

        if (this.isIntList())
        {
            if (values.length == defaultValues.length)
            {
                for (int i = 0; i < values.length; i++)
                    if (Integer.parseInt(values[i]) != Integer.parseInt(defaultValues[i]))
                        return false;

                return true;
            }
            else
                return false;
        }

        if (this.isDoubleList())
        {
            if (values.length == defaultValues.length)
            {
                for (int i = 0; i < values.length; i++)
                    if (Double.parseDouble(values[i]) != Double.parseDouble(defaultValues[i]))
                        return false;

                return true;
            }
            else
                return false;
        }

        if (this.isList())
        {
            if (values.length == defaultValues.length)
            {
                for (int i = 0; i < values.length; i++)
                    if (!values[i].equals(defaultValues[i]))
                        return false;

                return true;
            }
            else
                return false;
        }

        if (this.type == Type.BOOLEAN && this.isBooleanValue())
            return Boolean.parseBoolean(value) == Boolean.parseBoolean(defaultValue);

        if (this.type == Type.INTEGER && this.isIntValue())
            return Integer.parseInt(value) == Integer.parseInt(defaultValue);

        if (this.type == Type.DOUBLE && this.isDoubleValue())
            return Double.parseDouble(value) == Double.parseDouble(defaultValue);

        return value.equals(defaultValue);
    }

    /**
     * Sets the current value(s) of this Property to the default value(s).
     */
    public Property setToDefault()
    {
        this.value = this.defaultValue;
        this.values = Arrays.copyOf(this.defaultValues, this.defaultValues.length);
        return this;
    }

    /**
     * Gets the raw String default value of this Property. Check for isList() == false first.
     *
     * @return the default value String
     */
    public String getDefault()
    {
        return defaultValue;
    }

    /**
     * Gets the raw String[] default values of this Property. Check for isList() == true first.
     *
     * @return the default values String[]
     */
    public String[] getDefaults()
    {
        return Arrays.copyOf(this.defaultValues, this.defaultValues.length);
    }

    /**
     * Sets the flag for whether or not this Property can be edited while a world is running. Care should be taken to ensure
     * that only properties that are truly dynamic can be changed from the in-game options menu. When set to false the Property will be
     * editable from both the main menu Mods list config screen and the in-game Mod Options config screen. When set to true the Property
     * will only be editable from the main menu Mods list config screen.
     */
    public Property setRequiresWorldRestart(boolean requiresWorldRestart)
    {
        this.requiresWorldRestart = requiresWorldRestart;
        return this;
    }

    /**
     * Returns whether or not this Property is able to be edited while a world is running using the in-game Mod Options screen
     * as well as the Mods list screen, or only from the Mods list screen. Setting this flag to true will disable editing of
     * this property while a world is running.
     */
    public boolean requiresWorldRestart()
    {
        return this.requiresWorldRestart;
    }

    /**
     * Sets whether or not this Property should be allowed to show on config GUIs.
     * Defaults to true.
     */
    public Property setShowInGui(boolean showInGui)
    {
        this.showInGui = showInGui;
        return this;
    }

    /**
     * Gets whether or not this Property should be allowed to show on config GUIs.
     * Defaults to true unless set to false.
     */
    public boolean showInGui()
    {
        return showInGui;
    }

    /**
     * Sets whether or not this Property requires Minecraft to be restarted when changed.
     * Defaults to false. Setting this flag to true will also disable editing of
     * this property while a world is running.
     */
    public Property setRequiresMcRestart(boolean requiresMcRestart)
    {
        this.requiresMcRestart = this.requiresWorldRestart = requiresMcRestart;
        return this;
    }

    /**
     * Gets whether or not this Property requires Minecraft to be restarted when changed.
     * Defaults to false unless set to true.
     */
    public boolean requiresMcRestart()
    {
        return this.requiresMcRestart;
    }

    /**
     * Sets the maximum length of this list/array Property. Only important if isList() == true. If the current values array or default
     * values array is longer than the new maximum it will be resized. If calling both this method and setIsListLengthFixed(true), this
     * method should be called afterwards (but is not required).
     */
    public Property setMaxListLength(int max)
    {
        this.maxListLength = max;
        if (this.maxListLength != -1)
        {
            if (values != null && values.length != maxListLength)
                if (this.isListLengthFixed || values.length > maxListLength)
                    values = Arrays.copyOf(values, maxListLength);

            if (defaultValues != null && defaultValues.length != maxListLength)
                if (this.isListLengthFixed || defaultValues.length > maxListLength)
                    defaultValues = Arrays.copyOf(defaultValues, maxListLength);
        }
        return this;
    }

    /**
     * Gets the maximum length of this list/array Property. Only important if isList() == true.
     */
    public int getMaxListLength()
    {
        return this.maxListLength;
    }

    /**
     * Sets the flag for whether this list/array Property has a fixed length. Only important if isList() == true. If calling both this
     * method and setMaxListLength(), this method should be called first (but is not required).
     */
    public Property setIsListLengthFixed(boolean isListLengthFixed)
    {
        this.isListLengthFixed = isListLengthFixed;
        return this;
    }

    /**
     * Returns whether or not this list/array has a fixed length. Only important if isList() == true.
     */
    public boolean isListLengthFixed()
    {
        return this.isListLengthFixed;
    }

    /**
     * Sets a custom IConfigEntry class that should be used in place of the standard entry class for this Property type. This class
     * MUST provide a constructor with the following parameter types: {@code GuiConfig} (the owning GuiConfig screen will be provided),
     * {@code GuiConfigEntries} (the owning GuiConfigEntries will be provided), {@code IConfigElement} (the IConfigElement for this Property
     * will be provided).
     */
    public Property setConfigEntryClass(Class<? extends IConfigEntry> clazz)
    {
        this.configEntryClass = clazz;
        return this;
    }

    /**
     * Gets the custom IConfigEntry class that should be used in place of the standard entry class for this Property type, or null if
     * none has been set.
     *
     * @return a class that implements IConfigEntry
     */
    public Class<? extends IConfigEntry> getConfigEntryClass()
    {
        return this.configEntryClass;
    }

    /**
     * Sets a custom IGuiEditListEntry class that should be used in place of the standard entry class for this Property type. This class
     * MUST provide a constructor with the following parameter types: {@code GuiEditList} (the owning GuiEditList screen will be provided),
     * {@code GuiPropertyList} (the parent GuiPropertyList will be provided), {@code IConfigProperty} (the IConfigProperty for this Property
     * will be provided).
     *
     * @param clazz a class that implements IConfigEntry
     */
    public Property setArrayEntryClass(Class<? extends IArrayEntry> clazz)
    {
        this.arrayEntryClass = clazz;
        return this;
    }

    /**
     * Gets the custom IArrayEntry class that should be used in place of the standard entry class for this Property type, or null if
     * none has been set.
     *
     * @return a class that implements IArrayEntry
     */
    public Class<? extends IArrayEntry> getArrayEntryClass()
    {
        return this.arrayEntryClass;
    }

    /**
     * Sets a regex Pattern object used to validate user input for formatted String or String[] properties.
     *
     * @param validationPattern
     */
    public Property setValidationPattern(Pattern validationPattern)
    {
        this.validationPattern = validationPattern;
        return this;
    }

    /**
     * Gets the Pattern object used to validate user input for this Property.
     *
     * @return the user input validation Pattern object, or null if none is set
     */
    public Pattern getValidationPattern()
    {
        return this.validationPattern;
    }

    /**
     * Sets the localization language key for this Property so that the config GUI screens are nice and pretty <3. The string languageKey +
     * ".tooltip" is used for tooltips when a user hovers the mouse over a GUI property label.
     *
     * @param langKey a string language key such as myawesomemod.config.myPropName
     */
    public Property setLanguageKey(String langKey)
    {
        this.langKey = langKey;
        return this;
    }

    /**
     * Gets the language key string for this Property.
     *
     * @return the language key
     */
    public String getLanguageKey()
    {
        return this.langKey;
    }

    /**
     * Sets the default string value of this Property.
     *
     * @param defaultValue a String value
     */
    public Property setDefaultValue(String defaultValue)
    {
        this.defaultValue = defaultValue;
        return this;
    }

    /**
     * Sets the default String[] values of this Property.
     *
     * @param defaultValues an array of String values
     */
    public Property setDefaultValues(String[] defaultValues)
    {
        this.defaultValue = "";
        for (String s : defaultValues)
            this.defaultValue += ", [" + s + "]";
        this.defaultValue = this.defaultValue.replaceFirst(", ", "");
        this.defaultValues = Arrays.copyOf(defaultValues, defaultValues.length);
        return this;
    }

    /**
     * Sets the default int value of this Property.
     *
     * @param defaultValue an int value
     */
    public Property setDefaultValue(int defaultValue)
    {
        setDefaultValue(Integer.toString(defaultValue));
        return this;
    }

    /**
     * Sets the default int[] values of this Property.
     *
     * @param defaultValues an array of int values
     */
    public Property setDefaultValues(int[] defaultValues)
    {
        String[] temp = new String[defaultValues.length];
        for (int i = 0; i < defaultValues.length; i++)
            temp[i] = Integer.toString(defaultValues[i]);

        setDefaultValues(temp);
        return this;
    }

    /**
     * Sets the default double value of this Property.
     *
     * @param defaultValue a double value
     */
    public Property setDefaultValue(double defaultValue)
    {
        setDefaultValue(Double.toString(defaultValue));
        return this;
    }

    /**
     * Sets the default double[] values of this Property
     *
     * @param defaultValues an array of double values
     */
    public Property setDefaultValues(double[] defaultValues)
    {
        String[] temp = new String[defaultValues.length];
        for (int i = 0; i < defaultValues.length; i++)
            temp[i] = Double.toString(defaultValues[i]);

        setDefaultValues(temp);
        return this;
    }

    /**
     * Sets the default boolean value of this Property.
     *
     * @param defaultValue a boolean value
     */
    public Property setDefaultValue(boolean defaultValue)
    {
        setDefaultValue(Boolean.toString(defaultValue));
        return this;
    }

    /**
     * Sets the default boolean[] values of this Property.
     *
     * @param defaultValues an array of boolean values
     */
    public Property setDefaultValues(boolean[] defaultValues)
    {
        String[] temp = new String[defaultValues.length];
        for (int i = 0; i < defaultValues.length; i++)
            temp[i] = Boolean.toString(defaultValues[i]);

        setDefaultValues(temp);
        return this;
    }

    /**
     * Sets the minimum int value of this Property.
     *
     * @param minValue an int value
     */
    public Property setMinValue(int minValue)
    {
        this.minValue = Integer.toString(minValue);
        return this;
    }

    /**
     * Sets the maximum int value of this Property.
     *
     * @param maxValue an int value
     */
    public Property setMaxValue(int maxValue)
    {
        this.maxValue = Integer.toString(maxValue);
        return this;
    }

    /**
     * Sets the minimum double value of this Property.
     *
     * @param minValue a double value
     */
    public Property setMinValue(double minValue)
    {
        this.minValue = Double.toString(minValue);
        return this;
    }

    /**
     * Sets the maximum double value of this Property.
     *
     * @param maxValue a double value
     */
    public Property setMaxValue(double maxValue)
    {
        this.maxValue = Double.toString(maxValue);
        return this;
    }

    /**
     * Gets the minimum value.
     *
     * @return the minimum value bound
     */
    public String getMinValue()
    {
        return minValue;
    }

    /**
     * Gets the maximum value.
     *
     * @return the maximum value bound
     */
    public String getMaxValue()
    {
        return maxValue;
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
     * Sets the array of valid values that this String Property can be set to. When an array of valid values is defined for a Property the
     * GUI control for that property will be a value cycle button.
     *
     * @param validValues a String array of valid values
     */
    public Property setValidValues(String[] validValues)
    {
        this.validValues = validValues;
        return this;
    }

    /**
     * Gets the array of valid values that this String Property can be set to, or null if not defined.
     *
     * @return a String array of valid values
     */
    public String[] getValidValues()
    {
        return this.validValues;
    }

    /**
     * Returns the value in this property as an integer,
     * if the value is not a valid integer, it will return the initially provided default.
     *
     * @return The value
     */
    public int getInt()
    {
        try
        {
            return Integer.parseInt(value);
        }
        catch (NumberFormatException e)
        {
            return Integer.parseInt(defaultValue);
        }
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
     * Returns the value in this property as a boolean, if the value is not a valid boolean, it will return the provided default.
     *
     * @return The value as a boolean, or the default
     */
    public boolean getBoolean()
    {
        if (isBooleanValue())
        {
            return Boolean.parseBoolean(value);
        }
        else
        {
            return Boolean.parseBoolean(defaultValue);
        }
    }

    /**
     * Checks if the current value held by this property is a valid boolean value.
     *
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
     * Returns the value in this property as a double, if the value is not a valid double, it will return the provided default.
     *
     * @param _default The default to provide if the current value is not a valid double
     * @return The value
     */
    public double getDouble()
    {
        try
        {
            return Double.parseDouble(value);
        }
        catch (NumberFormatException e)
        {
            return Double.parseDouble(defaultValue);
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
        if (isList && type == Type.INTEGER)
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
        return isList && type == Type.INTEGER;
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
        if (isList && type == Type.BOOLEAN)
            for (String value : values)
            {
                if (!"true".equalsIgnoreCase(value) && !"false".equalsIgnoreCase(value))
                {
                    return false;
                }
            }

        return isList && type == Type.BOOLEAN;
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
        if (isList && type == Type.DOUBLE)
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

        return isList && type == Type.DOUBLE;
    }

    /**
     * Gets the name/key for this Property.
     *
     * @return the Property name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name/key for this Property.
     *
     * @param name a name
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Determines if this config value was just created, or if it was read from the config file.
     * This is useful for mods who auto-assign their blocks to determine if the ID returned is
     * a configured one, or a automatically generated one.
     *
     * @return True if this property was loaded from the config file with a value
     */
    public boolean wasRead()
    {
        return wasRead;
    }

    /**
     * Gets the Property.Type enum value for this Property.
     *
     * @return the Property's type
     */
    public Type getType()
    {
        return type;
    }

    /**
     * Returns whether or not this Property is a list/array.
     *
     * @return true if this Property is a list/array, false otherwise
     */
    public boolean isList()
    {
        return isList;
    }

    /**
     * Gets the changed status of this Property.
     *
     * @return true if this Property has changed, false otherwise
     */
    public boolean hasChanged(){ return changed; }
    void resetChangedState(){ changed = false; }

    /**
     * Sets the value of this Property to the provided String value.
     */
    public Property setValue(String value)
    {
        this.value = value;
        changed = true;
        return this;
    }

    public void set(String value)
    {
        this.setValue(value);
    }

    /**
     * Sets the values of this Property to the provided String[] values.
     */
    public Property setValues(String[] values)
    {
        this.values = Arrays.copyOf(values, values.length);
        changed = true;
        return this;
    }

    public void set(String[] values)
    {
        this.setValues(values);
    }

    /**
     * Sets the value of this Property to the provided int value.
     */
    public Property setValue(int value)
    {
        setValue(Integer.toString(value));
        return this;
    }

    /**
     * Sets the value of this Property to the provided boolean value.
     */
    public Property setValue(boolean value)
    {
        setValue(Boolean.toString(value));
        return this;
    }

    /**
     * Sets the value of this Property to the provided double value.
     */
    public Property setValue(double value)
    {
        setValue(Double.toString(value));
        return this;
    }

    /**
     * Sets the values of this Property to the provided boolean[] values.
     */
    public Property setValues(boolean[] values)
    {
        this.values = new String[values.length];
        for (int i = 0; i < values.length; i++)
            this.values[i] = String.valueOf(values[i]);
        changed = true;
        return this;
    }

    public void set(boolean[] values)
    {
        this.setValues(values);
    }

    /**
     * Sets the values of this Property to the provided int[] values.
     */
    public Property setValues(int[] values)
    {
        this.values = new String[values.length];
        for (int i = 0; i < values.length; i++)
            this.values[i] = String.valueOf(values[i]);
        changed = true;
        return this;
    }

    public void set(int[] values)
    {
        this.setValues(values);
    }

    /**
     * Sets the values of this Property to the provided double[] values.
     */
    public Property setValues(double[] values)
    {
        this.values = new String[values.length];
        for (int i = 0; i < values.length; i++)
            this.values[i] = String.valueOf(values[i]);
        changed = true;
        return this;
    }

    public void set(double[] values)
    {
        this.setValues(values);
    }
    public void set(int     value){ set(Integer.toString(value)); }
    public void set(boolean value){ set(Boolean.toString(value)); }
    public void set(double  value){ set(Double.toString(value));  }
}
