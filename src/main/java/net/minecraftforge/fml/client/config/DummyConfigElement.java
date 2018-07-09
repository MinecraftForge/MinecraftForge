/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.fml.client.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.config.GuiConfigEntries.IConfigEntry;
import net.minecraftforge.fml.client.config.GuiEditArrayEntries.IArrayEntry;

import javax.annotation.Nullable;


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
    @Nullable
    protected Class<? extends IConfigEntry> configEntryClass;
    protected Class<? extends IArrayEntry> arrayEntryClass;
    
    /**
     * This class provides a Dummy Category IConfigElement. It can be used to define a custom list of GUI entries that will 
     * appear on the child screen or to specify a custom IGuiConfigListEntry for a special category.
     */
    public static class DummyCategoryElement extends DummyConfigElement
    {
        public DummyCategoryElement(String name, String langKey, List<IConfigElement> childElements)
        {
            this(name, langKey, childElements, null);
        }
        
        public DummyCategoryElement(String name, String langKey, Class<? extends IConfigEntry> customListEntryClass)
        {
            this(name, langKey, new ArrayList<IConfigElement>(), customListEntryClass);
        }
        
        public DummyCategoryElement(String name, String langKey, List<IConfigElement> childElements, @Nullable Class<? extends IConfigEntry> customListEntryClass)
        {
            super(name, null, ConfigGuiType.CONFIG_CATEGORY, langKey);
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
        public DummyListElement(String name, Object[] defaultValues, ConfigGuiType type, String langKey, boolean isListFixedLength, int maxListLength, @Nullable Pattern validStringPattern, @Nullable Object minValue, @Nullable Object maxValue)
        {
            super(name, null, type, langKey, minValue, maxValue);
            this.defaultValues = defaultValues;
            this.values = defaultValues;
            this.isListFixedLength = isListFixedLength;
            this.maxListLength = maxListLength;
            this.validStringPattern = validStringPattern;
            isList = true;
        }

        public DummyListElement(String name, Object[] defaultValues, ConfigGuiType type, String langKey)
        {
            this(name, defaultValues, type, langKey, false, -1, null, null, null);
        }

        public DummyListElement(String name, Object[] defaultValues, ConfigGuiType type, String langKey, boolean isListFixedLength)
        {
            this(name, defaultValues, type, langKey, isListFixedLength, -1, null, null, null);
        }
        
        public DummyListElement(String name, Object[] defaultValues, ConfigGuiType type, String langKey, int maxListLength)
        {
            this(name, defaultValues, type, langKey, false, maxListLength, null, null, null);
        }

        public DummyListElement(String name, Object[] defaultValues, ConfigGuiType type, String langKey, Object minValue, Object maxValue)
        {
            this(name, defaultValues, type, langKey, false, -1, null, minValue, maxValue);
        }

        public DummyListElement(String name, Object[] defaultValues, ConfigGuiType type, String langKey, boolean isListFixedLength, Object minValue, Object maxValue)
        {
            this(name, defaultValues, type, langKey, isListFixedLength, -1, null, minValue, maxValue);
        }
        
        public DummyListElement(String name, Object[] defaultValues, ConfigGuiType type, String langKey, int maxListLength, Object minValue, Object maxValue)
        {
            this(name, defaultValues, type, langKey, false, maxListLength, null, minValue, maxValue);
        }

        public DummyListElement(String name, Object[] defaultValues, ConfigGuiType type, String langKey, boolean isListFixedLength, int maxListLength, Object minValue, Object maxValue)
        {
            this(name, defaultValues, type, langKey, isListFixedLength, maxListLength, null, minValue, maxValue);
        }

        public DummyListElement(String name, Object[] defaultValues, ConfigGuiType type, String langKey, Pattern validStringPattern)
        {
            this(name, defaultValues, type, langKey, false, -1, validStringPattern, null, null);
        }

        public DummyListElement(String name, Object[] defaultValues, ConfigGuiType type, String langKey, boolean isListFixedLength, Pattern validStringPattern)
        {
            this(name, defaultValues, type, langKey, isListFixedLength, -1, validStringPattern, null, null);
        }
        
        public DummyListElement(String name, Object[] defaultValues, ConfigGuiType type, String langKey, int maxListLength, Pattern validStringPattern)
        {
            this(name, defaultValues, type, langKey, false, maxListLength, validStringPattern, null, null);
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
                this.minValue = Integer.MIN_VALUE;
            else if (type == ConfigGuiType.DOUBLE)
                this.minValue = -Double.MAX_VALUE;
        }
        else
            this.minValue = minValue;
        if (maxValue == null)
        {
            if (type == ConfigGuiType.INTEGER)
                this.maxValue = Integer.MAX_VALUE;
            else if (type == ConfigGuiType.DOUBLE)
                this.maxValue = Double.MAX_VALUE;
        }
        else
            this.maxValue = maxValue;
    }
    
    public DummyConfigElement(String name, Object defaultValue, ConfigGuiType type, String langKey, Pattern validStringPattern)
    {
        this(name, defaultValue, type, langKey, null, validStringPattern, null, null);
    }
    
    public DummyConfigElement(String name, Object defaultValue, ConfigGuiType type, String langKey, String[] validValues)
    {
        this(name, defaultValue, type, langKey, validValues, null, null, null);
    }
    
    public DummyConfigElement(String name, Object defaultValue, ConfigGuiType type, String langKey)
    {
        this(name, defaultValue, type, langKey, null, null, null, null);
    }
    
    public DummyConfigElement(String name, Object defaultValue, ConfigGuiType type, String langKey, Object minValue, Object maxValue)
    {
        this(name, defaultValue, type, langKey, null, null, minValue, maxValue);
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
