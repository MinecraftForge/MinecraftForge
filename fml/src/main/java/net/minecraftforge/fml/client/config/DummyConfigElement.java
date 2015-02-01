/*
 * Forge Mod Loader
 * Copyright (c) 2012-2014 cpw.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * Contributors (this class):
 *     bspkrs - implementation
 */

package net.minecraftforge.fml.client.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.config.GuiConfigEntries.IConfigEntry;
import net.minecraftforge.fml.client.config.GuiEditArrayEntries.IArrayEntry;


/**
 * This class's main purpose is to provide the necessary objects for a sample Config GUI for FML, although
 * there may be practical uses for the objects defined here such as using the DummyCategoryElement object as a 
 * wrapper for a custom IGuiConfigListEntry object that opens a special screen.
 * 
 * @author bspkrs
 */
public class DummyConfigElement implements IConfigElement
{
    protected boolean isProperty = true;
    protected boolean isList = false;
    protected ConfigGuiType type;
    protected String name;
    protected String langKey;
    protected Object value;
    protected Object defaultValue;
    protected Object[] values;
    protected Object[] defaultValues;
    protected String[] validValues;
    protected Pattern validStringPattern;
    protected Object minValue;
    protected Object maxValue;
    protected boolean requiresWorldRestart = false;
    protected boolean requiresMcRestart = false;
    protected boolean isListFixedLength = false;
    protected int maxListLength = -1;
    protected List<IConfigElement> childElements;
    protected Class<? extends IConfigEntry> configEntryClass;
    protected Class<? extends IArrayEntry> arrayEntryClass;
    
    /**
     * This class provides a Dummy Category IConfigElement. It can be used to define a custom list of GUI entries that will 
     * appear on the child screen or to specify a custom IGuiConfigListEntryfor a special category.
     */
    public static class DummyCategoryElement extends DummyConfigElement
    {
        public DummyCategoryElement(String name, String langKey, List<IConfigElement> childElements)
        {
            this(name, langKey, childElements, (Class<? extends IConfigEntry>) null);
        }
        
        public DummyCategoryElement(String name, String langKey, Class<? extends IConfigEntry> customListEntryClass)
        {
            this(name, langKey, new ArrayList<IConfigElement>(), customListEntryClass);
        }
        
        public DummyCategoryElement(String name, String langKey, List<IConfigElement> childElements, Class<? extends IConfigEntry> customListEntryClass)
        {
            super(name, (Object) null, ConfigGuiType.CONFIG_CATEGORY, langKey);
            this.childElements = childElements;
            this.configEntryClass = customListEntryClass;
            isProperty = false;
        }
    }
    
    /**
     * This class provides a dummy array-type IConfigElement. 
     */
    public static class DummyListElement extends DummyConfigElement
    {
        public DummyListElement(String name, Object[] defaultValues, ConfigGuiType type, String langKey, boolean isListFixedLength, int maxListLength, Pattern validStringPattern, Object minValue, Object maxValue)
        {
            super(name, (Object) null, type, langKey, minValue, maxValue);
            this.defaultValues = defaultValues;
            this.values = defaultValues;
            this.isListFixedLength = isListFixedLength;
            this.maxListLength = maxListLength;
            this.validStringPattern = validStringPattern;
            isList = true;
        }

        public DummyListElement(String name, Object[] defaultValues, ConfigGuiType type, String langKey)
        {
            this(name, defaultValues, type, langKey, false, -1, (Pattern) null, (Object) null, (Object) null);
        }

        public DummyListElement(String name, Object[] defaultValues, ConfigGuiType type, String langKey, boolean isListFixedLength)
        {
            this(name, defaultValues, type, langKey, isListFixedLength, -1, (Pattern) null, (Object) null, (Object) null);
        }
        
        public DummyListElement(String name, Object[] defaultValues, ConfigGuiType type, String langKey, int maxListLength)
        {
            this(name, defaultValues, type, langKey, false, maxListLength, (Pattern) null, (Object) null, (Object) null);
        }

        public DummyListElement(String name, Object[] defaultValues, ConfigGuiType type, String langKey, Object minValue, Object maxValue)
        {
            this(name, defaultValues, type, langKey, false, -1, (Pattern) null, minValue, maxValue);
        }

        public DummyListElement(String name, Object[] defaultValues, ConfigGuiType type, String langKey, boolean isListFixedLength, Object minValue, Object maxValue)
        {
            this(name, defaultValues, type, langKey, isListFixedLength, -1, (Pattern) null, minValue, maxValue);
        }
        
        public DummyListElement(String name, Object[] defaultValues, ConfigGuiType type, String langKey, int maxListLength, Object minValue, Object maxValue)
        {
            this(name, defaultValues, type, langKey, false, maxListLength, (Pattern) null, minValue, maxValue);
        }

        public DummyListElement(String name, Object[] defaultValues, ConfigGuiType type, String langKey, boolean isListFixedLength, int maxListLength, Object minValue, Object maxValue)
        {
            this(name, defaultValues, type, langKey, isListFixedLength, maxListLength, (Pattern) null, minValue, maxValue);
        }

        public DummyListElement(String name, Object[] defaultValues, ConfigGuiType type, String langKey, Pattern validStringPattern)
        {
            this(name, defaultValues, type, langKey, false, -1, validStringPattern, (Object) null, (Object) null);
        }

        public DummyListElement(String name, Object[] defaultValues, ConfigGuiType type, String langKey, boolean isListFixedLength, Pattern validStringPattern)
        {
            this(name, defaultValues, type, langKey, isListFixedLength, -1, validStringPattern, (Object) null, (Object) null);
        }
        
        public DummyListElement(String name, Object[] defaultValues, ConfigGuiType type, String langKey, int maxListLength, Pattern validStringPattern)
        {
            this(name, defaultValues, type, langKey, false, maxListLength, validStringPattern, (Object) null, (Object) null);
        }
        
        public DummyListElement setCustomEditListEntryClass(Class<? extends IArrayEntry> clazz)
        {
            this.arrayEntryClass = clazz;
            return this;
        }

        @Override
        public Object getDefault()
        {
            return Arrays.toString(this.defaultValues);
        }
    }
    
    public DummyConfigElement(String name, Object defaultValue, ConfigGuiType type, String langKey, String[] validValues, Pattern validStringPattern, Object minValue, Object maxValue)
    {
        this.name = name;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.type = type;
        this.langKey = langKey;
        this.validValues = validValues;
        this.validStringPattern = validStringPattern;
        if (minValue == null)
        {
            if (type == ConfigGuiType.INTEGER)
                this.minValue = (Integer) Integer.MIN_VALUE;
            else if (type == ConfigGuiType.DOUBLE)
                this.minValue = (Double) (-Double.MAX_VALUE);
        }
        else
            this.minValue = minValue;
        if (maxValue == null)
        {
            if (type == ConfigGuiType.INTEGER)
                this.maxValue = (Integer) Integer.MAX_VALUE;
            else if (type == ConfigGuiType.DOUBLE)
                this.maxValue = (Double) Double.MAX_VALUE;
        }
        else
            this.maxValue = maxValue;
    }
    
    public DummyConfigElement(String name, Object defaultValue, ConfigGuiType type, String langKey, Pattern validStringPattern)
    {
        this(name, defaultValue, type, langKey, (String[]) null, validStringPattern, (Object) null, (Object) null);
    }
    
    public DummyConfigElement(String name, Object defaultValue, ConfigGuiType type, String langKey, String[] validValues)
    {
        this(name, defaultValue, type, langKey, validValues, (Pattern) null, (Object) null, (Object) null);
    }
    
    public DummyConfigElement(String name, Object defaultValue, ConfigGuiType type, String langKey)
    {
        this(name, defaultValue, type, langKey, (String[]) null, (Pattern) null, (Object) null, (Object) null);
    }
    
    public DummyConfigElement(String name, Object defaultValue, ConfigGuiType type, String langKey, Object minValue, Object maxValue)
    {
        this(name, defaultValue, type, langKey, (String[]) null, (Pattern) null, minValue, maxValue);
    }
    
    public DummyConfigElement setCustomListEntryClass(Class<? extends IConfigEntry> clazz)
    {
        this.configEntryClass = clazz;
        return this;
    }
    
    @Override
    public boolean isProperty()
    {
        return isProperty;
    }
    
    public IConfigElement setConfigEntryClass(Class<? extends IConfigEntry> clazz)
    {
        this.configEntryClass = clazz;
        return this;
    }

    @Override
    public Class<? extends IConfigEntry> getConfigEntryClass()
    {
        return configEntryClass;
    }
    
    public IConfigElement setArrayEntryClass(Class<? extends IArrayEntry> clazz)
    {
        this.arrayEntryClass = clazz;
        return this;
    }

    @Override
    public Class<? extends IArrayEntry> getArrayEntryClass()
    {
        return arrayEntryClass;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String getQualifiedName()
    {
        return name;
    }

    @Override
    public String getLanguageKey()
    {
        return langKey;
    }

    @Override
    public String getComment()
    {
        return I18n.format(langKey + ".tooltip");
    }

    @Override
    public List<IConfigElement> getChildElements()
    {
        return childElements;
    }

    @Override
    public ConfigGuiType getType()
    {
        return type;
    }

    @Override
    public boolean isList()
    {
        return isList;
    }

    @Override
    public boolean isListLengthFixed()
    {
        return this.isListFixedLength;
    }

    @Override
    public int getMaxListLength()
    {
        return this.maxListLength;
    }

    @Override
    public boolean isDefault()
    {
        if (isProperty)
        {
            if (!isList)
            {
                if (value != null)
                    return value.equals(defaultValue);
                else
                    return defaultValue == null;
            }
            else
            {
                return Arrays.deepEquals(values, defaultValues);
            }
        }
            
        return true;
    }

    @Override
    public Object getDefault()
    {
        return defaultValue;
    }

    @Override
    public Object[] getDefaults()
    {
        return defaultValues;
    }

    @Override
    public void setToDefault()
    {
        if (isList)
            this.values = Arrays.copyOf(this.defaultValues, this.defaultValues.length);
        else
            this.value = defaultValue;
    }
    
    public IConfigElement setRequiresWorldRestart(boolean requiresWorldRestart)
    {
        this.requiresWorldRestart = requiresWorldRestart;
        return this;
    }

    @Override
    public boolean requiresWorldRestart()
    {
        return requiresWorldRestart;
    }

    @Override
    public boolean showInGui()
    {
        return true;
    }
    
    public IConfigElement setRequiresMcRestart(boolean requiresMcRestart)
    {
        this.requiresMcRestart = this.requiresWorldRestart = requiresMcRestart;
        return this;
    }

    @Override
    public boolean requiresMcRestart()
    {
        return requiresMcRestart;
    }

    @Override
    public String[] getValidValues()
    {
        return validValues;
    }

    @Override
    public Pattern getValidationPattern()
    {
        return validStringPattern;
    }

    @Override
    public Object get()
    {
        return value;
    }

    @Override
    public Object[] getList()
    {
        return values;
    }

    @Override
    public void set(Object value)
    {
        defaultValue = value;
    }

    @Override
    public void set(Object[] aVal)
    {
        defaultValues = aVal;
    }

    @Override
    public Object getMinValue()
    {
        return minValue;
    }

    @Override
    public Object getMaxValue()
    {
        return maxValue;
    }
}