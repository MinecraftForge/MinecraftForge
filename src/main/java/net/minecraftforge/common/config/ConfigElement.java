/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

/**
 * This software is provided under the terms of the Minecraft Forge Public
 * License v1.0.
 */

package net.minecraftforge.common.config;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import net.minecraftforge.fml.client.config.ConfigGuiType;
import net.minecraftforge.fml.client.config.GuiConfigEntries.IConfigEntry;
import net.minecraftforge.fml.client.config.GuiEditArrayEntries.IArrayEntry;
import net.minecraftforge.fml.client.config.IConfigElement;

/**
 * This class bridges the gap between the FML config GUI classes and the Forge Configuration classes.
 */
public class ConfigElement implements IConfigElement
{
    private Property prop;
    private Property.Type type;
    private boolean isProperty;
    private ConfigCategory category;
    private boolean categoriesFirst = true;

    public ConfigElement(ConfigCategory category)
    {
        this.category = category;
        isProperty = false;
    }

    public ConfigElement(Property prop)
    {
        this.prop = prop;
        this.type = prop.getType();
        this.isProperty = true;
    }

    public ConfigElement listCategoriesFirst(boolean categoriesFirst)
    {
        this.categoriesFirst = categoriesFirst;
        return this;
    }

    @Override
    public List<IConfigElement> getChildElements()
    {
        if (!isProperty)
        {
            List<IConfigElement> elements = new ArrayList<IConfigElement>();
            Iterator<ConfigCategory> ccI = category.getChildren().iterator();
            Iterator<Property> pI = category.getOrderedValues().iterator();
            @SuppressWarnings("unused")
            int index = 0;

            if (categoriesFirst)
                while (ccI.hasNext())
                {
                    ConfigElement temp = new ConfigElement(ccI.next());
                    if (temp.showInGui()) // don't bother adding elements that shouldn't show
                        elements.add(temp);
                }

            while (pI.hasNext())
            {
                ConfigElement temp = new ConfigElement(pI.next());
                if (temp.showInGui())
                    elements.add(temp);
            }

            if (!categoriesFirst)
                while (ccI.hasNext())
                {
                    ConfigElement temp = new ConfigElement(ccI.next());
                    if (temp.showInGui())
                        elements.add(temp);
                }

            return elements;
        }
        return null;
    }

    @Override
    public String getName()
    {
        return isProperty ? prop.getName() : category.getName();
    }

    @Override
    public boolean isProperty()
    {
        return isProperty;
    }

    @Override
    public Class<? extends IConfigEntry> getConfigEntryClass()
    {
        return isProperty ? prop.getConfigEntryClass() : category.getConfigEntryClass();
    }

    @Override
    public Class<? extends IArrayEntry> getArrayEntryClass()
    {
        return isProperty ? prop.getArrayEntryClass() : null;
    }

    @Override
    public String getQualifiedName()
    {
        return isProperty ? prop.getName() : category.getQualifiedName();
    }

    @Override
    public ConfigGuiType getType()
    {
        return isProperty ? getType(this.prop) : ConfigGuiType.CONFIG_CATEGORY;
    }

    public static ConfigGuiType getType(Property prop)
    {
        return prop.getType() == Property.Type.BOOLEAN ? ConfigGuiType.BOOLEAN : prop.getType() == Property.Type.DOUBLE ? ConfigGuiType.DOUBLE :
            prop.getType() == Property.Type.INTEGER ? ConfigGuiType.INTEGER : prop.getType() == Property.Type.COLOR ? ConfigGuiType.COLOR :
            prop.getType() == Property.Type.MOD_ID ? ConfigGuiType.MOD_ID : ConfigGuiType.STRING;
    }

    @Override
    public boolean isList()
    {
        return isProperty && prop.isList();
    }

    @Override
    public boolean isListLengthFixed()
    {
        return isProperty && prop.isListLengthFixed();
    }

    @Override
    public int getMaxListLength()
    {
        return isProperty ? prop.getMaxListLength() : -1;
    }

    @Override
    public String getComment()
    {
        return isProperty ? prop.getComment() : category.getComment();
    }

    @Override
    public boolean isDefault()
    {
        return !isProperty || prop.isDefault();
    }

    @Override
    public void setToDefault()
    {
        if (isProperty)
            prop.setToDefault();
    }

    @Override
    public boolean requiresWorldRestart()
    {
        return isProperty ? prop.requiresWorldRestart() : category.requiresWorldRestart();
    }

    @Override
    public boolean showInGui()
    {
        return isProperty ? prop.showInGui() : category.showInGui();
    }

    @Override
    public boolean requiresMcRestart()
    {
        return isProperty ? prop.requiresMcRestart() : category.requiresMcRestart();
    }

    @Override
    public String[] getValidValues()
    {
        return isProperty ? prop.getValidValues() : null;
    }

    @Override
    public String getLanguageKey()
    {
        return isProperty ? prop.getLanguageKey() : category.getLanguagekey();
    }

    @Override
    public Object getDefault()
    {
        return isProperty ? prop.getDefault() : null;
    }

    @Override
    public Object[] getDefaults()
    {
        if (isProperty)
        {
            String[] aVal = prop.getDefaults();
            if (type == Property.Type.BOOLEAN)
            {
                Boolean[] ba = new Boolean[aVal.length];
                for(int i = 0; i < aVal.length; i++)
                    ba[i] = Boolean.valueOf(aVal[i]);
                return ba;
            }
            else if (type == Property.Type.DOUBLE)
            {
                Double[] da = new Double[aVal.length];
                for(int i = 0; i < aVal.length; i++)
                    da[i] = Double.valueOf(aVal[i].toString());
                return da;
            }
            else if (type == Property.Type.INTEGER)
            {
                Integer[] ia = new Integer[aVal.length];
                for(int i = 0; i < aVal.length; i++)
                    ia[i] = Integer.valueOf(aVal[i].toString());
                return ia;
            }
            else
                return aVal;
        }
        return null;
    }

    @Override
    public Pattern getValidationPattern()
    {
        return isProperty ? prop.getValidationPattern() : null;
    }

    @Override
    public Object get()
    {
        return isProperty ? prop.getString() : null;
    }

    @Override
    public Object[] getList()
    {
        if (isProperty)
        {
            String[] aVal = prop.getStringList();
            if (type == Property.Type.BOOLEAN)
            {
                Boolean[] ba = new Boolean[aVal.length];
                for(int i = 0; i < aVal.length; i++)
                    ba[i] = Boolean.valueOf(aVal[i]);
                return ba;
            }
            else if (type == Property.Type.DOUBLE)
            {
                Double[] da = new Double[aVal.length];
                for(int i = 0; i < aVal.length; i++)
                    da[i] = Double.valueOf(aVal[i].toString());
                return da;
            }
            else if (type == Property.Type.INTEGER)
            {
                Integer[] ia = new Integer[aVal.length];
                for(int i = 0; i < aVal.length; i++)
                    ia[i] = Integer.valueOf(aVal[i].toString());
                return ia;
            }
            else
                return aVal;
        }
        return null;
    }

    @Override
    public void set(Object value)
    {
        if (isProperty)
        {
            if (type == Property.Type.BOOLEAN)
                prop.set(Boolean.parseBoolean(value.toString()));
            else if (type == Property.Type.DOUBLE)
                prop.set(Double.parseDouble(value.toString()));
            else if (type == Property.Type.INTEGER)
                prop.set(Integer.parseInt(value.toString()));
            else
                prop.set(value.toString());
        }
    }

    @Override
    public void set(Object[] aVal)
    {
        if (isProperty)
        {
            if (type == Property.Type.BOOLEAN)
            {
                boolean[] ba = new boolean[aVal.length];
                for(int i = 0; i < aVal.length; i++)
                    ba[i] = Boolean.valueOf(aVal[i].toString());
                prop.set(ba);
            }
            else if (type == Property.Type.DOUBLE)
            {
                double[] da = new double[aVal.length];
                for(int i = 0; i < aVal.length; i++)
                    da[i] = Double.valueOf(aVal[i].toString());
                prop.set(da);
            }
            else if (type == Property.Type.INTEGER)
            {
                int[] ia = new int[aVal.length];
                for(int i = 0; i < aVal.length; i++)
                    ia[i] = Integer.valueOf(aVal[i].toString());
                prop.set(ia);
            }
            else
            {
                String[] is = new String[aVal.length];
                for(int i = 0; i < aVal.length; i++)
                    is[i] = aVal[i].toString();
                prop.set(is);
            }
        }
    }

    @Override
    public Object getMinValue()
    {
        return isProperty ? prop.getMinValue() : null;
    }

    @Override
    public Object getMaxValue()
    {
        return isProperty ? prop.getMaxValue() : null;
    }
    
    public static ConfigElement from(String modid)
    {
        return from(modid, null);
    }
    
    /**
     * Provides a ConfigElement which conta
     * @param modid
     * @param name
     * @return
     */
    public static ConfigElement from(String modid, String name)
    {
        return from(modid, name, "general");//'general' is default in @Config annotation
    }
    
    /**
     * Provides a ConfigElement derived from the annotation-based config system
     * @param modid The modid stated in the {@code @Config} annotation.
     * @param name The name stated in the {@code @Config} annotation.
     * @param categoryName The category name stated in the {@code @Config} annotation.
     * @return A ConfigElement based on the described category.
     */
    public static ConfigElement from(String modid, String name, String categoryName)
    {
        Configuration config = ConfigManager.getConfiguration(modid, name);
        if(config == null)
        {
            String error = String.format("The configuration '%s' of mod '%s' is not handled by the ConfigManager!", name, modid);
            throw new RuntimeException(error);
        }
        
        ConfigCategory category = config.getCategory(categoryName);
        return new ConfigElement(category);
    }
}
